package com.github.stcarolas.lijzeil.functions.common.convertion;

import javax.inject.Named;
import org.eclipse.lsp4j.Position;
import io.vavr.Function1;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("ToEclipsePosition")
public class ToEclipsePosition
	implements Function1<com.github.javaparser.Position, Position> {

	public Position apply(com.github.javaparser.Position original) {
		return new Position(original.line-1, original.column-1);
	}
}
