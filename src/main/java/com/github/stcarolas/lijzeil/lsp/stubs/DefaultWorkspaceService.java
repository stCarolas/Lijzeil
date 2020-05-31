package  com.github.stcarolas.lijzeil.lsp.stubs;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class DefaultWorkspaceService implements WorkspaceService  {

	@Override
	public void didChangeConfiguration(DidChangeConfigurationParams params) {}

	@Override
	public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {}

}
