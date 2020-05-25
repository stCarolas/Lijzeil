package  com.github.stcarolas.javaparser.lsp;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Named
@Log4j2
public class Workspace implements WorkspaceService {

  @Inject
  private CommandCache commands;

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
    log.info("Executing command");
    return CompletableFuture.supplyAsync(() -> commands.executeCommand(params.getCommand()));
  }

}
