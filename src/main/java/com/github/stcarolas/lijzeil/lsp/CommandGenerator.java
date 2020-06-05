package  com.github.stcarolas.lijzeil.lsp;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.stcarolas.lijzeil.functions.Zeil;
import com.github.stcarolas.lijzeil.lsp.stubs.DefaultDocumentService;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import io.vavr.collection.List;
import lombok.extern.log4j.Log4j2;

@Named
@Singleton
@Log4j2
public class CommandGenerator extends DefaultDocumentService {

	public CompletableFuture<java.util.List<Either<Command, CodeAction>>> codeAction(
		CodeActionParams params
	){
		String path = params.getTextDocument().getUri();
		Range range = params.getRange();
		log.info("analyze {} in range {}:{} - {}:{}", path,
			range.getStart().getLine(),
			range.getStart().getCharacter(),
			range.getEnd().getLine(),
			range.getEnd().getCharacter()
		);

		return CompletableFuture.supplyAsync(
			() -> List.ofAll(functions)
				.map(
					function -> new FunctionAsCommand(URI.create(path), range, function)
				)
				.map(cache::addCommand)
				.map(command -> new Command(command.getDescription(),command.getKey()))
				.map(command -> Either.<Command, CodeAction>forLeft(command))
				.asJava()
		);
	}

	@Inject
	private java.util.List<Zeil> functions;

	@Inject
	private CommandCache cache;

}
