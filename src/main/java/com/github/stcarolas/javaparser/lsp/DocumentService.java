package  com.github.stcarolas.javaparser.lsp;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.stcarolas.javaparser.commands.ExtractAsFunction;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

@Named
public class DocumentService implements TextDocumentService{

  @Inject
  private CommandCache cache;

  @Inject
  private List<Command> commands;

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {}

  @Override
  public void didChange(DidChangeTextDocumentParams params) {}

  @Override
  public void didClose(DidCloseTextDocumentParams params) {}

  @Override
  public void didSave(DidSaveTextDocumentParams params) {}

  @Override
  public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(
    CodeActionParams params
  ){
    String key = UUID.randomUUID().toString();
    com.github.stcarolas.javaparser.lsp.Command command = 
      new ExtractAsFunction(params.getTextDocument().getUri(),params.getRange());
    cache.addCommand(key, command);
    return CompletableFuture
      .supplyAsync(
        () -> List.of(
          Either.forLeft(
            new Command("Extract statement as Function", key)
          )
        )
      );
  }

}
