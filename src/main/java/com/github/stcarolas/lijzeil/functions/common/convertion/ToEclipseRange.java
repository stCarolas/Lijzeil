package com.github.stcarolas.lijzeil.functions.common.convertion;

import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import io.vavr.Function1;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("ToEclipseRange")
public class ToEclipseRange implements Function1<com.github.javaparser.Range, Range> {

	public Range apply(com.github.javaparser.Range original) {
		Position end = toEclipsePosition.apply(original.end);
		end.setCharacter(end.getCharacter()+1);
		return new Range(
			toEclipsePosition.apply(original.begin),
			end
		);
	}

	@Inject
	@Named("ToEclipsePosition")
	private Function1<com.github.javaparser.Position, Position> toEclipsePosition;
}
