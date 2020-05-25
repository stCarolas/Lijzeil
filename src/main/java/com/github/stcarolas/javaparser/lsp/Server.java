package  com.github.stcarolas.javaparser.lsp;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

@Named
public class Server implements LanguageServer {

  @Inject
  private TextDocumentService document;
  @Inject
  private WorkspaceService workspace;

  @Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		return CompletableFuture.supplyAsync(this::init);
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		return CompletableFuture.supplyAsync(this::init);
	}

	@Override
	public void exit() {}

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
