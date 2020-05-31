package  com.github.stcarolas.lijzeil.lsp;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.lijzeil.WorkspaceChanges;
import com.github.stcarolas.lijzeil.functions.Zeil;
import com.github.stcarolas.lijzeil.functions.createfunction.CreateFunction;
import com.github.stcarolas.lijzeil.lsp.stubs.DefaultDocumentService;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

@Named
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
				.toJavaList()
		);
	}

	@Inject
	private java.util.List<Zeil> functions;

	@Inject
	private CommandCache cache;

}
