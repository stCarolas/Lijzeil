package  com.github.stcarolas.lijzeil.functions.createapplymethod;

import java.net.URI;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.github.javaparser.printer.YamlPrinter;
import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.WorkspaceChanges;
import com.github.stcarolas.lijzeil.functions.Zeil;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;
import static com.github.stcarolas.lijzeil.functions.common.Selectors.classSelector;

import io.vavr.API;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

@Log4j2
@Named("CreateApplyMethod")
public class CreateApplyMethod implements Zeil{

	public WorkspaceChanges apply(URI path, Range range) {
		return parseCompilationUnit.apply(path).toList()
			.flatMap(unit -> findFieldDeclarations.apply(unit,range))
			.flatMap(FieldDeclaration::getVariables)
			.flatMap(this::createApplyMethod)
			.foldLeft(
				new WorkspaceChanges(),
				(changes, change) -> changes.withChange(path.toString(), change)
			);
	}

	private Option<TextEdit> createApplyMethod(VariableDeclarator functionField){
		return findParentNode.apply(classSelector, functionField)
			.map(
				classDeclaration -> (ClassOrInterfaceDeclaration)classDeclaration
			)
			.map(
				classDeclaration -> createMethodBody.apply(functionField,classDeclaration)
			);
	}

	@Inject @Named("ParseCompilationUnit")
	private Function<URI, Try<CompilationUnit>>
		parseCompilationUnit;

	@Inject @Named("FindParentNode")
	private Function2<Function<Node,Boolean>, Node, Option<Node>> 
		findParentNode;

	@Inject @Named("CreateApplyMethodBody")
	private Function2<VariableDeclarator, ClassOrInterfaceDeclaration, TextEdit>
		createMethodBody;

	@Inject @Named("FindFieldDeclarations")
	private Function2<CompilationUnit, Range, List<FieldDeclaration>>
		findFieldDeclarations;

	@Override
	public String description() {
		return "Create method with function application";
	}
}
