package  com.github.stcarolas.lijzeil.functions.common;

import java.util.function.Function;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class Selectors {

	public static final Function<Node, Boolean> classSelector = 
		node -> node.getClass().equals(ClassOrInterfaceDeclaration.class) 
			&&
			(
				((ClassOrInterfaceDeclaration)node).getMembers().isNonEmpty()
				|| !((ClassOrInterfaceDeclaration)node).getFields().isEmpty()
			);

	public static final Function<Node, Boolean> method = 
		node -> node.getClass().equals(MethodDeclaration.class);

	public static final Function<Node, Boolean> methodWithBody = 
		node -> node.getClass().equals(MethodDeclaration.class)
		&& ((MethodDeclaration)node).getBody().isPresent();

}
