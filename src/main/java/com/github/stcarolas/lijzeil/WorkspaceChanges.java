package  com.github.stcarolas.lijzeil;

import static io.vavr.API.Option;

import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceChanges {
  private Map<String, List<TextEdit>> changes = Map();

  public Option<WorkspaceEdit> asWorkspaceEdit(){
    return Option(changes)
      .map($ -> $.mapValues(item -> item.toJavaList()))
      .map(Map::toJavaMap)
      .map(WorkspaceEdit::new);
  }

  public WorkspaceChanges withChange(String uri, TextEdit change){
    return new WorkspaceChanges(
      changes.put(
        uri, 
        changes.get(uri).getOrElse(List()).append(change)
      )
    );
  }

}
