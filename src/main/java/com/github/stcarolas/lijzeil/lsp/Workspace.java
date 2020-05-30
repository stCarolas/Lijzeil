package  com.github.stcarolas.lijzeil.lsp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;

import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.services.WorkspaceService;

import io.vavr.control.Try;
import io.vavr.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Named
@Log4j2
public class Workspace implements WorkspaceService {

	@Inject
	private CommandCache commands;

	@Inject
	private LSPClient lspClient;

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {
	}

	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
	}

	@Override
	public CompletableFuture<Object> executeCommand(
		ExecuteCommandParams params
	){
		;
		return commands.executeCommand(params.getCommand())
				.flatMap($ -> Future.fromCompletableFuture(lspClient.sendEdit($)).toTry())
				.map($ -> (Object)$)
				.toCompletableFuture();
	}

}
