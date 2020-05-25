package  com.github.stcarolas.javaparser.functions.common;

import javax.inject.Named;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import io.vavr.control.Option;
import io.vavr.control.Try;

import static io.vavr.API.*;

import java.util.function.Function;

@Named("ParsePackage")
public class ParsePackage implements Function<CompilationUnit, Try<PackageDeclaration>> {

  public Try<PackageDeclaration> apply(CompilationUnit unit){
    return Option(unit)
      .toTry(() -> new RuntimeException("Missing CompilationUnit for parsing package"))
      .flatMap(
        $ -> Option.ofOptional($.getPackageDeclaration()).toTry()
      );
  }

}
