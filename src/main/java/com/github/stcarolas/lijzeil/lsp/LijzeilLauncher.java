package  com.github.stcarolas.lijzeil.lsp;

import org.apache.commons.io.output.TeeOutputStream;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import io.micronaut.context.ApplicationContext;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

import java.io.File;
import java.io.FileOutputStream;

@RequiredArgsConstructor
@Log4j2
public class LijzeilLauncher {
	public static void main(String args[]){

		Try<ApplicationContext> context = Try(() -> ApplicationContext.builder().start());

		Try<Launcher<LanguageClient>> launcher = 
			context
				.map($ -> $.getBean(Server.class))
				.map(server -> LSPLauncher.createServerLauncher(server, System.in, System.out));

		Try<LanguageClient> client = launcher.map($ -> $.getRemoteProxy());
		Try<LSPClient> clientWrapper = context.map($ -> $.getBean(LSPClient.class));

		For(clientWrapper, client)
			.yield(LSPClient::setClient)
			.peek(it -> log.info("Server created"));

		launcher
			.map($ -> $.startListening() )
			.onFailure(error -> log.error("Cant create Server: {}", error.getMessage()));
	}

}
