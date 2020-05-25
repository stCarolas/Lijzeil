package  com.github.stcarolas.javaparser.commands;

import javax.inject.Named;

import com.github.stcarolas.javaparser.CreateFunction;
import com.github.stcarolas.javaparser.Position;
import com.github.stcarolas.javaparser.lsp.Command;

import org.eclipse.lsp4j.Range;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class ExtractAsFunction implements Command {
  private final String path;
  private final Range range;
  private CreateFunction create = new CreateFunction();

  @Override
	public void execute() {
    Position end = new Position(range.getEnd().getLine(), range.getEnd().getCharacter());
    log.info("ExtractAsFunction");
    com.github.stcarolas.javaparser.Range parsedRange = 
      new com.github.stcarolas.javaparser.Range(end, end);
    create.execute(path, parsedRange);
	}
}
