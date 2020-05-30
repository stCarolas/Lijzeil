package  com.github.stcarolas.javaparser;

import java.io.InputStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.YamlPrinter;
import com.github.stcarolas.lijzeil.Position;
import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.functions.createfunction.CreateFunction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreateFunctionTest {
	YamlPrinter printer = new YamlPrinter(true);
	String path = "/TestClass.java";

	@Test
	public void testNodeSelection(){
		InputStream content = this.getClass().getResourceAsStream(path);
		CompilationUnit unit = StaticJavaParser.parse(content);
		Range range = new Range(new Position(35,5), new Position(37,64));

		CreateFunction parser = new CreateFunction();
		//Try<ExpressionStmt> nodes = new CreateFunction().selectedNodes(unit, range);

		//Option<PackageDeclaration> unitPackage = Option.ofOptional(unit.getPackageDeclaration());
		//For(unitPackage, nodes).yield(parser::createFunction).get();

		//Assertions.assertFalse(nodes.isEmpty());
	}

}
