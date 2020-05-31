package  com.github.stcarolas.lijzeil.lsp.stubs;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.lijzeil.WorkspaceChanges;
import com.github.stcarolas.lijzeil.functions.Zeil;
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

@Log4j2
public abstract class DefaultDocumentService implements TextDocumentService {

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {}

}
