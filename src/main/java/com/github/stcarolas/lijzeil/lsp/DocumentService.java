package  com.github.stcarolas.lijzeil.lsp;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.lijzeil.WorkspaceChanges;
import com.github.stcarolas.lijzeil.functions.createfunction.CreateFunction;

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
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

@Named
@Log4j2
public class DocumentService implements TextDocumentService{

	@Override
	public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(
		CodeActionParams params
	){
		String key = UUID.randomUUID().toString();
		String path = params.getTextDocument().getUri();
		Range range = params.getRange();
		log.info("analyze {} in range {}:{} - {}:{} as command {}", path,
			range.getStart().getLine(),
			range.getStart().getCharacter(),
			range.getEnd().getLine(),
			range.getEnd().getCharacter(),
			key
		);
		FunctionAsCommand createFunctionCommand = 
			new FunctionAsCommand(URI.create(path), range, createFunction);
		cache.addCommand(key, createFunctionCommand);
		String createApplyMethodKey = UUID.randomUUID().toString();
		FunctionAsCommand createApplyMethodCommand = 
			new FunctionAsCommand(URI.create(path), range, createApplyMethod);
		cache.addCommand(createApplyMethodKey, createApplyMethodCommand);
		return CompletableFuture
			.supplyAsync(
				() -> List.of(
					Either.forLeft(
						new Command("Extract statement as Function", key)
					),
					Either.forLeft(
						new Command("Create method with function apply", createApplyMethodKey)
					)
				)
			);
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {}

	@Inject @Named("CreateFunction")
	private Function2<URI, com.github.stcarolas.lijzeil.Range, WorkspaceChanges>
		createFunction;

	@Inject @Named("CreateApplyMethod")
	private Function2<URI, com.github.stcarolas.lijzeil.Range, WorkspaceChanges>
		createApplyMethod;

	@Inject
	private CommandCache cache;


}
