package  com.github.stcarolas.javaparser.functions.common;

import static io.vavr.API.Try;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.function.Function;

import javax.inject.Named;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import io.vavr.control.Try;

@Named("ParseCompilationUnit")
public class ParseCompilationUnit implements Function<String, Try<CompilationUnit>> {

	@Override
	public Try<CompilationUnit> apply(String uri) {
    return Try(() -> new FileInputStream(new File(URI.create(uri))))
        .map(content -> StaticJavaParser.parse(content));
	}

}
