package  com.github.stcarolas.lijzeil.lsp;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

import javax.inject.Named;
import javax.inject.Singleton;

import com.github.stcarolas.lijzeil.WorkspaceChanges;

import org.eclipse.lsp4j.WorkspaceEdit;

@Named
@Singleton
@Log4j2
public class CommandCache {
	Map<String, FunctionAsCommand> commands = Map();

	public void addCommand(String key, FunctionAsCommand command){
		log.info("saving command {}", key);
		commands = commands.put(key, command);
	}

	public Try<WorkspaceEdit> executeCommand(String key){
		return commands.get(key)
			.toTry(() -> new RuntimeException("Missing command " + key))
			.map(FunctionAsCommand::execute)
			.flatMap(
				$ -> $.asWorkspaceEdit()
					.toTry(() -> new RuntimeException("Cant convert workspace changes"))
			)
			.andThen( change -> log.info("change: {}", change.getChanges()) );
	}

}
