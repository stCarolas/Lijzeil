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
import static com.github.stcarolas.lijzeil.functions.common.Selectors.*;
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
			.flatMap(unit -> findMethods.apply(range, methodSelector, unit))
			.map(method -> (MethodDeclaration) method)
			.map(this::addImplements);
		log.trace("apply method: {}", new YamlPrinter(true).output(methods.head()));
		return new WorkspaceChanges();
	}

	private MethodDeclaration addImplements(MethodDeclaration method) {
		Option<ClassOrInterfaceDeclaration> classDeclaration = findParentNode.apply(
			classSelector,
			method
		)
			.map(node -> (ClassOrInterfaceDeclaration) node);

		classDeclaration.forEach(
			$ -> log.trace("classTree: {}", new YamlPrinter(true).output($))
		);
		classDeclaration.forEach(
			$ -> log.trace("classLine: {}", constructClassDeclarationLine($))
		);

		String implementation = String.format(
			"%s<%s>",
			chooseFunction(method),
			collectTypes(method)
		);
		log.debug("target implementation: {}", implementation);
		return method;
	}

	private String constructClassDeclarationLine(
		ClassOrInterfaceDeclaration classDeclaration
	) {
		return List.ofAll(classDeclaration.getModifiers())
			.map($ -> $.toString())
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
		return types.isEmpty() ? None() : Some(types.mkString("extends ",",",""));
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

	@Override
	public String description() {
		return "Use method as Function#apply";
	}
}
