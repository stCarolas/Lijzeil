package com.github.stcarolas.lijzeil.lsp;

import static io.vavr.API.For;
import static io.vavr.API.Try;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.function.Consumer;

import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import io.micronaut.context.ApplicationContext;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class LijzeilLauncher {

	public static void main(String args[]) {
		Try<ApplicationContext> context = Try(ApplicationContext.builder()::start);
		For(
			context.map($ -> $.getBean(Server.class)),
			context.map($ -> $.getBean(LSPClient.class))
		)
			.yield(server)
			.flatMap(runUntilShutdown)
			.andThen(logShutdown)
			.onFailure(logError);
	}

	private static Function2<Server, LSPClient, Launcher<LanguageClient>> server =
		(server, clientWrapper) -> {
			Launcher<LanguageClient> launcher = null;
			try {
				TeeInputStream inStream = new TeeInputStream(System.in, new FileOutputStream("/tmp/test"));
				TeeOutputStream outStream = new TeeOutputStream(System.out, new FileOutputStream("/tmp/testOut"));
				launcher = LSPLauncher.createServerLauncher(server, inStream, outStream);
				clientWrapper.setClient(launcher.getRemoteProxy());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return launcher;
		};

	private static Function1<Launcher<LanguageClient>, Try<Void>> runUntilShutdown = 
		server -> Future.fromJavaFuture(server.startListening()).toTry();

	private static Runnable logShutdown = () -> log.info("shutdown server");
	private static Consumer<Throwable> logError = error -> log.error("Error while running Server: {}", error.getMessage());
}
