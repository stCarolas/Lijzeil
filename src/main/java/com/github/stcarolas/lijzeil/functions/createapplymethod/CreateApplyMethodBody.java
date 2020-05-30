package  com.github.stcarolas.lijzeil.functions.createapplymethod;

import javax.inject.Named;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import com.github.javaparser.printer.YamlPrinter;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Named("CreateApplyMethodBody")
public class CreateApplyMethodBody 
	implements Function2<VariableDeclarator, ClassOrInterfaceDeclaration, TextEdit> {

	public TextEdit apply(
		VariableDeclarator function,
		ClassOrInterfaceDeclaration classCode
	){
		MethodDeclaration method = classCode
			.addMethod(name(function), Keyword.PUBLIC)
			.setType(returnType(function));

		method.createBody()
			.addStatement(
				returnFunctionApplication(function, availableVariables(classCode))
			);

		log.trace("Constructed method: {}", method);

		Position position = Option.ofOptional(classCode.getEnd())
			.map($ -> new Position($.line - 1, $.column - 1))
			.getOrElse(new Position(0,0));
		return new TextEdit(
			new org.eclipse.lsp4j.Range(position, position),
			method.toString() + "\n"
		);
	}

	private ReturnStmt returnFunctionApplication(
		VariableDeclarator function,
		Map<String, SimpleName> allFields
	){
		return new ReturnStmt(
			functionArgs(function)
				.map(
					type -> allFields.get(type).map(SimpleName::toString)
						.getOrElse("null")
				)
				.intersperse(",")
				.foldLeft(
					new StringBuilder(name(function)).append(".apply("),
					StringBuilder::append
				)
				.append(")")
				.toString()
			);
	}

	private List<String> functionArgs(
		VariableDeclarator function
	){
		return List.ofAll(function.getType().getChildNodes())
			.drop(1)
			.dropRight(1)
			.map(Node::toString);
	}

	private String name(VariableDeclarator function){
		return function.getNameAsString();
	}

	private String returnType(VariableDeclarator function){
		return List.ofAll(function.getType().getChildNodes())
			.lastOption()
			.map(node -> node.toString())
			.getOrElse("void");
	}

	private Map<String, SimpleName> availableVariables(
		ClassOrInterfaceDeclaration classCode
	){
		return List.ofAll(classCode.getFields())
			.reverse()
			.flatMap(field -> field.getVariables())
			.toMap(
				field -> field.getType().toString(),
				field -> field.getName()
			);
	}

}
