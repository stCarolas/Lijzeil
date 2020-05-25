package  com.github.stcarolas.javaparser.functions.common;

import static io.vavr.API.For;
import static io.vavr.API.Option;
import static io.vavr.API.Try;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.javaparser.ast.CompilationUnit;

import io.vavr.Function2;
import io.vavr.control.Try;

@Named("WriteSource")
public class WriteSource implements Function2<String, CompilationUnit, Try<String>> {

  @Inject @Named("WriteSource")
  Function2<File, CompilationUnit, Try<String>> writeSource;

  public Try<String> apply(String uri, CompilationUnit aSource){
    Try<CompilationUnit> source = Option(aSource)
      .toTry(() -> new RuntimeException("Missing generated source for writing to file"));

    Try<String> name = source.map($ -> $.getType(0).getName().toString());

    Try<Path> targetDirectory = Try(() -> URI.create(uri))
      .map(Paths::get)
      .map(Path::getParent);

    Try<File> targetFile = For(targetDirectory, name)
      .yield( (dir, functionName) ->  dir.resolve(functionName + ".java"))
      .map(Path::toFile);

    return For(targetFile, source)
      .yield(writeSource)
      .flatMap($ -> $);
  }

}
