package  com.github.stcarolas.lijzeil.lsp;

import java.net.URI;

import com.github.stcarolas.lijzeil.Position;
import com.github.stcarolas.lijzeil.Range;
import com.github.stcarolas.lijzeil.WorkspaceChanges;

import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class FunctionAsCommand {

	private final URI path;
	private final org.eclipse.lsp4j.Range range;
	private final Function2<URI, Range, WorkspaceChanges> function;

	public WorkspaceChanges execute() {
		Position end = new Position(range.getEnd().getLine(), range.getEnd().getCharacter());
		Range parsedRange = new Range(end, end);
		return function.apply(path, parsedRange);
	}

}
