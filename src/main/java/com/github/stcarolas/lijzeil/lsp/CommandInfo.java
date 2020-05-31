package  com.github.stcarolas.lijzeil.lsp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Getter
public class CommandInfo {
	@With private final String description;
	@With private final String key;
}
