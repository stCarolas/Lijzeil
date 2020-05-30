package  com.github.stcarolas.lijzeil.functions.common;

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
public class ParseCompilationUnit implements Function<URI, Try<CompilationUnit>> {

	@Override
	public Try<CompilationUnit> apply(URI uri) {
	return Try(() -> new FileInputStream(new File(uri)))
		.map(content -> StaticJavaParser.parse(content));
	}

}
