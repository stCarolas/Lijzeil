package  com.github.stcarolas.javaparser.lsp;

import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
@Log4j2
public class CommandCache {
  Map<String, Command> commands = Map();

  public void addCommand(String key, Command command){
    log.info("saving command {}", key);
    commands = commands.put(key, command);
  }

  public Try<String> executeCommand(String key){
    log.info("execute {}",key);
    commands.get(key).forEach(Command::execute);
    commands = commands.remove(key);
    return Success("Success");
  }
}
