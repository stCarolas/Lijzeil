package  com.github.stcarolas.lijzeil.lsp;

import java.util.concurrent.CompletableFuture;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ApplyWorkspaceEditResponse;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.services.LanguageClient;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Named
@Singleton
@NoArgsConstructor
@AllArgsConstructor
public class LSPClient {
  private LanguageClient client;

  public LSPClient setClient(LanguageClient client){
    this.client = client;
    return this;
  }

  public CompletableFuture<ApplyWorkspaceEditResponse> sendEdit(WorkspaceEdit edit){
    return client.applyEdit(new ApplyWorkspaceEditParams(edit));
  }
}
