package  com.github.stcarolas.javaparser.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import io.micronaut.context.ApplicationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

@RequiredArgsConstructor
@Log4j2
public class Launcher {
  public static void main(String args[]){
		Try(() -> ApplicationContext.builder().start())
			.map(context -> context.getBean(Server.class))
      .map(server -> LSPLauncher.createServerLauncher(server, System.in, System.out))
			.andThen(() -> log.info("Server created") )
      .andThen(launcher -> launcher.startListening())
			.onFailure( error -> log.error("Cant create Server: {}", error.getMessage()) );
  }

}
