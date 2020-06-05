package  com.github.stcarolas.lijzeil.lsp;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.github.stcarolas.lijzeil.lsp.stubs.DefaultWorkspaceService;

import org.eclipse.lsp4j.ExecuteCommandParams;

import io.vavr.concurrent.Future;

@Named
@Singleton
public class CommandExecutor extends DefaultWorkspaceService {

	public CompletableFuture<Object> executeCommand(ExecuteCommandParams params){
		return commands.executeCommand(params.getCommand())
				.flatMap($ -> Future.fromCompletableFuture(lspClient.sendEdit($)).toTry())
				.map($ -> (Object)$)
				.toCompletableFuture();
	}

	@Inject
	private CommandCache commands;

	@Inject
	private LSPClient lspClient;

}
