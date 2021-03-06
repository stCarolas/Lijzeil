package  com.github.stcarolas.lijzeil.lsp;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import lombok.extern.log4j.Log4j2;

@Named
@Singleton
@Log4j2
public class Server implements LanguageServer {

	@Inject
	private TextDocumentService document;

	@Inject
	private CommandExecutor workspace;

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		log.info("Init server");
		return CompletableFuture.supplyAsync(this::init);
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		log.info("Shutdown server");
		return CompletableFuture.supplyAsync(this::init);
	}

	@Override
	public void exit() {
			log.info("Exit");
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return document;
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return workspace;
	}

	private InitializeResult init(){
		ServerCapabilities capabilities = new ServerCapabilities();
		capabilities.setCodeActionProvider(true);
		return new InitializeResult(capabilities);
	}

}
