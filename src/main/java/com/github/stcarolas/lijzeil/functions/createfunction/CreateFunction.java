package  com.github.stcarolas.lijzeil.functions.createfunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.YamlPrinter;
import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.WorkspaceChanges;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

@Log4j2
@Named("CreateFunction")
public class CreateFunction implements Function2<URI, Range, Try<WorkspaceChanges>> {

	public Try<WorkspaceChanges> apply(URI path, Range range){
		Try<CompilationUnit> unit = parseCompilationUnit.apply(path);
		Try<PackageDeclaration> unitPackage = unit.flatMap(parsePackage);
		Try<List<Node>> nodes = 
			unit.map(findSelectedNode.apply(range, this::applicableStatement));

		return For(unitPackage, nodes)
			.yield(this::createFunction)
			.flatMap($ -> writeSource.apply(path.toString(), $))
			.map(source -> new WorkspaceChanges());
	}

	public CompilationUnit createFunction(
		PackageDeclaration classPackage,
		List<Node> nodes
	){
		CompilationUnit source = new CompilationUnit();
		ExpressionStmt code = (ExpressionStmt) nodes.head();

		ClassOrInterfaceDeclaration classCode = source
			.setPackageDeclaration(classPackage)
			.addClass(functionName(code))
			.setPublic(true)
			.addAnnotation(named(code))
			.addImplementedType(functionType(code));

		addApplyMethod(classCode, code);

		log.debug("created: {}", source);
		return source;
	}

	public MethodDeclaration addApplyMethod(
		ClassOrInterfaceDeclaration classCode,
		ExpressionStmt originalCode
	){
		MethodDeclaration method = classCode.addMethod("apply");
		List<NameExpr> functionParameters = undeclaredVariables(originalCode);
		java.util.Map<String, Type> declarations = declaredVariables(originalCode);
		for(NameExpr variable: functionParameters){
			String name = variable.getNameAsString();
			Type type = declarations.get(name);
			if (type != null){
				method.addParameter(type, name);
			}
		}
		VariableDeclarationExpr declaration = 
			(VariableDeclarationExpr)originalCode.getChildNodes().get(0);
		Expression init = declaration.getVariable(0).getInitializer().get();
		ReturnStmt returnStmt = new ReturnStmt(init);
		method.setType(returnType(originalCode));
		method.setPublic(true);
		method.createBody().addStatement(returnStmt);
		return method;
	}

	public AnnotationExpr named(ExpressionStmt code){
		return new SingleMemberAnnotationExpr()
			.setMemberValue(new StringLiteralExpr(functionName(code)))
			.setName("Named");
	}

	public ClassOrInterfaceType functionType(ExpressionStmt code){
		Type returnType = returnType(code);
		List<NameExpr> functionParameters = undeclaredVariables(code);
		log.debug("undeclaredVariables: {}", functionParameters);
		java.util.Map<String, Type> declarations = declaredVariables(code);

		ClassOrInterfaceType functionType = new ClassOrInterfaceType("Function");
		NodeList types = new NodeList();
		for(NameExpr variable: functionParameters){
			String name = variable.getNameAsString();
			Type type = declarations.get(name);
			if (type != null){
				types.add(type);
			}
		}
		log.debug("return type: {}", returnType);
		log.debug("types: {}", types);
		types.add(returnType);
		functionType.setTypeArguments(types);
		return functionType;
	}

	public Type returnType(ExpressionStmt code){
		return code.getExpression()
			.asVariableDeclarationExpr()
			.getVariables()
			.getFirst().get()
			.getType();
	}

	public List<NameExpr> undeclaredVariables(ExpressionStmt code){
			List<NameExpr> usedNames = List.ofAll(code.findAll(NameExpr.class));
			return usedNames.filter(name -> checkVariableForDeclaration(name, code) );
	}

	public java.util.Map<String, Type> declaredVariables(ExpressionStmt code){
			Optional<Node> methodCode = Optional.of(code);
			java.util.Map<String, Type> variables = new HashMap<>();

			while(
				methodCode.isPresent()
					&& !methodCode.get().getClass().equals(MethodDeclaration.class)
			){
				methodCode = methodCode.flatMap( $ -> $.getParentNode() );
			}

			MethodDeclaration method = (MethodDeclaration)methodCode.get();
			method.getParameters().forEach( param -> {
				variables.put(param.getName().asString(), param.getType());
			});

			java.util.List<VariableDeclarationExpr> declarations = 
				methodCode.get().findAll(VariableDeclarationExpr.class);
			declarations.forEach( declaration -> {
				declaration.getVariables().forEach( variable -> {
					variables.put(variable.getName().asString(), variable.getType());
				});
			});

			ClassOrInterfaceDeclaration classCode = 
				(ClassOrInterfaceDeclaration)method.getParentNode().get();
			classCode.getFields().forEach( 
				field -> field.getVariables().forEach( variable -> {
					variables.put(variable.getName().asString(), variable.getType());
				})
			);

			return variables;
	}

	public boolean checkVariableForDeclaration(NameExpr variable, ExpressionStmt root){
		boolean declared = false;
		String name = variable.getName().toString();
		Optional<Node> parent = variable.getParentNode();
		while( 
			!declared
			&& parent.isPresent() 
			&& parent.filter($ -> $.equals(root)).isEmpty() 
		){
			Node node = parent.get();
			if (node.getClass().equals(LambdaExpr.class)){
				LambdaExpr expression = (LambdaExpr) node;
				if (expression.getParameterByName(name).isPresent()){
					declared = true;
				};
			}
			parent = node.getParentNode();
		}
		return !declared;
	}

	public String functionName(ExpressionStmt block){
		List<VariableDeclarator> variables = List.ofAll(block.getExpression()
			.asVariableDeclarationExpr()
			.getVariables());
		String name = variables.head().getName().asString();
		name = name.substring(0,1).toUpperCase() + name.substring(1);
		return name;
	}

	public boolean applicableStatement(Node node){
		if (node.getClass().equals(ExpressionStmt.class)){
			return  node.getChildNodes().get(0).getClass().equals(VariableDeclarationExpr.class);
		} else {
			return false;
		}
	}

	@Inject @Named("WriteSource")
	private Function2<String, CompilationUnit, Try<String>>
		writeSource;

	@Inject @Named("ParseCompilationUnit")
	private Function<URI, Try<CompilationUnit>>
		parseCompilationUnit;

	@Inject @Named("ParsePackage")
	private Function<CompilationUnit, Try<PackageDeclaration>>
		parsePackage;

	@Inject @Named("FindWrappingSelectionNode")
	private Function3<Range, Function<Node,Boolean>, Node, List<Node>>
		findSelectedNode;

}
