package  com.github.stcarolas.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.printer.YamlPrinter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import static io.vavr.API.*;

import java.io.InputStream;
import java.util.Optional;

public class CreateFunctionTest {
  YamlPrinter printer = new YamlPrinter(true);
  String path = "/TestClass.java";

  @Test
  public void testNodeSelection(){
    InputStream content = this.getClass().getResourceAsStream(path);
    CompilationUnit unit = StaticJavaParser.parse(content);
    Range range = new Range(new Position(35,5), new Position(37,64));

    CreateFunction parser = new CreateFunction();
    Try<ExpressionStmt> nodes = new CreateFunction().selectedNodes(unit, range);

    Option<PackageDeclaration> unitPackage = Option.ofOptional(unit.getPackageDeclaration());
    For(unitPackage, nodes).yield(parser::createFunction).get();

    Assertions.assertFalse(nodes.isEmpty());
  }

}
