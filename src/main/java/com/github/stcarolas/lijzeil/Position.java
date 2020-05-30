package  com.github.stcarolas.lijzeil;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Position {
  final int line;
  final int column;
}
