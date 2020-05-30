package  com.github.stcarolas.lijzeil;

import com.github.javaparser.ast.Node;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

@RequiredArgsConstructor
@Data
@Log4j2
public class Range {
	final Position begin;
	final Position end;

	public boolean wrappedBy(Node node){
		log.debug("begin: {}",node.getBegin());
		log.debug("end: {}",node.getEnd());

		boolean isWithin = node.getBegin()
			.filter(position -> 
					position.line <= begin.getLine() 
			)
			.isPresent();

		isWithin = isWithin && node.getEnd()
			.filter(position -> 
					position.line >= end.getLine()
			)
			.isPresent();
		return isWithin;
	}
}
