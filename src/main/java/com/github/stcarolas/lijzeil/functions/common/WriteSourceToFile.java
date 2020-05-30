package  com.github.stcarolas.lijzeil.functions.common;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

import javax.inject.Named;

import com.github.javaparser.ast.CompilationUnit;

import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Named("WriteSourceToFile")
@Log4j2
public class WriteSourceToFile implements Function2<File, CompilationUnit, Try<String>>{

	public Try<String> apply(File aTargetFile, CompilationUnit aSource){

		Try<String> source = 
			Option(aSource).map(CompilationUnit::toString)
				.toTry(() -> new RuntimeException("Missing source for writing to file"));

		return Try(() -> new FileWriter(aTargetFile))
			.flatMap(
				$ -> source
					.andThenTry($::write)
					.andThenTry($::flush)
			);

	}

}
