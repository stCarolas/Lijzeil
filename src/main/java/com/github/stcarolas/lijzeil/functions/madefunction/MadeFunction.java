package com.github.stcarolas.lijzeil.functions.madefunction;

import java.net.URI;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.YamlPrinter;
import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.WorkspaceChanges;
import com.github.stcarolas.lijzeil.functions.Zeil;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import static com.github.stcarolas.lijzeil.functions.common.Selectors.*;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("MadeFunction")
public class MadeFunction implements Zeil {

	public WorkspaceChanges apply(URI path, Range range) {
		List<MethodDeclaration> methods = parseCompilationUnit.apply(path)
			.toList()
			.flatMap(unit -> findMethods.apply(range, methodWithBody, unit))
			.map(method -> (MethodDeclaration) method);
		log.debug("methods: {}", methods.size());
		List<TextEdit> classDeclarationChanges = methods.flatMap(this::addImplements);
		log.debug("class changes: {}", classDeclarationChanges.size());
		List<TextEdit> methodsRenaming = methods.flatMap(this::renameMethod);
		log.debug("method changes: {}", methodsRenaming.size());
		return new WorkspaceChanges(Map(path.toString(), classDeclarationChanges.appendAll(methodsRenaming)));
	}

	private Iterable<TextEdit> renameMethod(MethodDeclaration method) {
		return Option.ofOptional(method.getName().getRange())
			.map(toEclipseRange)
			.map(range -> new TextEdit(range, "apply"));
	}

	private Iterable<TextEdit> addImplements(MethodDeclaration method) {
		Option<ClassOrInterfaceDeclaration> classDeclaration = findParentNode.apply(
			classSelector,
			method
		)
			.map(node -> (ClassOrInterfaceDeclaration) node);

		String implementation = String.format(
			"%s<%s>",
			chooseFunction(method),
			collectTypes(method)
		);
		Option<org.eclipse.lsp4j.Range> range = classDeclaration.map(
			this::getClassDeclarationRange
		);
		classDeclaration.forEach($ -> $.addImplements(implementation));
		Option<String> declaration = classDeclaration.map(
			this::constructClassDeclarationLine
		);
		return For(range, declaration).yield(TextEdit::new);
	}

	private org.eclipse.lsp4j.Range getClassDeclarationRange(
		ClassOrInterfaceDeclaration classDeclaration
	) {
		return new org.eclipse.lsp4j.Range(
			getBeginPosition(classDeclaration),
			getEndPosition(classDeclaration)
		);
	}

	private Position getBeginPosition(ClassOrInterfaceDeclaration classDeclaration) {
		return List.ofAll(classDeclaration.getModifiers())
			.flatMap(
				modifier -> Option.ofOptional(modifier.getBegin())
					.map($ -> new Position($.line, $.column))
			)
			.appendAll(
				Option.ofOptional(classDeclaration.getName().getBegin())
					.map($ -> new Position($.line, $.column))
			)
			.headOption()
			.map($ -> new Position($.getLine() - 1, $.getCharacter() - 1))
			.getOrElse(new Position(0, 0));
	}

	private Position getEndPosition(ClassOrInterfaceDeclaration classDeclaration) {
		return List.ofAll(
			Option.ofOptional(classDeclaration.getName().getEnd())
				.map($ -> new Position($.line, $.column))
		)
			.appendAll(
				List.ofAll(classDeclaration.getImplementedTypes())
					.lastOption()
					.flatMap(
						type -> Option.ofOptional(type.getEnd())
							.map($ -> new Position($.line, $.column))
					)
			)
			.appendAll(
				List.ofAll(classDeclaration.getExtendedTypes())
					.lastOption()
					.flatMap(
						type -> Option.ofOptional(type.getEnd())
							.map($ -> new Position($.line, $.column))
					)
			)
			.lastOption()
			.map($ -> new Position($.getLine() - 1, $.getCharacter()))
			.getOrElse(new Position(0, 0));
	}

	private String constructClassDeclarationLine(
		ClassOrInterfaceDeclaration classDeclaration
	) {
		return List.ofAll(classDeclaration.getModifiers())
			.map(Node::toString)
			.append(classDeclaration.isInterface() ? "interface" : "class")
			.append(classDeclaration.getNameAsString())
			.appendAll(extendsTypes(classDeclaration))
			.append(interfaces(classDeclaration))
			.map(String::trim)
			.intersperse(" ")
			.foldLeft(new StringBuilder(), StringBuilder::append)
			.toString();
	}

	private String interfaces(ClassOrInterfaceDeclaration classDeclaration) {
		return List.ofAll(classDeclaration.getImplementedTypes())
			.map($ -> $.toString())
			.intersperse(",")
			.foldLeft(new StringBuilder("implements "), StringBuilder::append)
			.toString();
	}

	public Option<String> extendsTypes(ClassOrInterfaceDeclaration classCode) {
		List<ClassOrInterfaceType> types = List.ofAll(classCode.getExtendedTypes());
		return types.isEmpty() ? None() : Some(types.mkString("extends ", ",", ""));
	}

	private String collectTypes(MethodDeclaration method) {
		return List.ofAll(method.getParameters())
			.map(param -> param.getType().toString())
			.append(method.getType().toString())
			.intersperse(",")
			.foldLeft(new StringBuffer(), StringBuffer::append)
			.toString();
	}

	private String chooseFunction(MethodDeclaration method) {
		return "Function" + method.getParameters().size();
	}

	@Inject
	@Named("ParseCompilationUnit")
	private Function<URI, Try<CompilationUnit>> parseCompilationUnit;

	@Inject
	@Named("FindParentNode")
	private Function2<Function<Node, Boolean>, Node, Option<Node>> findParentNode;

	@Inject
	@Named("FindWrappingSelectionNode")
	private Function3<Range, Function<Node, Boolean>, Node, List<Node>> findMethods;

	@Inject
	@Named("ToEclipseRange")
	private Function1<com.github.javaparser.Range, org.eclipse.lsp4j.Range> toEclipseRange;

	@Override
	public String description() {
		return "Use method as Function#apply";
	}
}
