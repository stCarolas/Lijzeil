package  com.github.stcarolas.lijzeil.functions.madefunction;

import javax.inject.Named;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.YamlPrinter;

import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("RenameMethod")
public class RenameMethod {
	public void apply(MethodDeclaration method){
		log.debug(
			"method to rename: {}",
			new YamlPrinter(true).output(method)
		);
		method.setName("newName");
	}
}
