package  com.github.stcarolas.lijzeil.functions.common;

import javax.inject.Named;

import com.github.stcarolas.lijzeil.Range;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;

import io.vavr.Function2;
import io.vavr.control.Option;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("CreateTextEdit")
public class CreateTextEdit implements Function2<Range, String, Option<TextEdit>>{

	public Option<TextEdit> apply(Range changeRange, String change){
		Option<Range> optRange = Option(changeRange);

		Option<Position> begin = optRange
			.flatMap($ -> Option($.getBegin()))
			.map($ -> new Position($.getLine(),$.getColumn()));

		Option<Position> end = optRange
			.flatMap($ -> Option($.getEnd()))
			.map($ -> new Position($.getLine(),$.getColumn()));

		return For(begin, end)
			.yield(org.eclipse.lsp4j.Range::new)
			.map($ -> new TextEdit($, change));
	}

}
