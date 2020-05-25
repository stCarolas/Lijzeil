package  com.github.stcarolas.javaparser;

import com.github.javaparser.ast.Node;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import static io.vavr.API.*;

@RequiredArgsConstructor
@Data
@Log4j2
public class Range {
  final Position begin;
  final Position end;

  public boolean wrappedBy(Node node){
    log.debug("begin: " + node.getBegin());
    log.debug("end: " + node.getEnd());

    boolean isWithin = node.getBegin()
      .filter(position -> 
          position.line <= begin.getLine() 
          //|| (position.line == begin.getLine() && position.column <= begin.getColumn())
      )
      .isPresent();
    log.debug("isWithin by beginning "+ begin.getLine() + "-" + end.getLine() + " :"+ isWithin);

    isWithin = isWithin && node.getEnd()
      .filter(position -> 
          position.line >= end.getLine()
          //|| (position.line == end.getLine() && position.column >= end.getColumn())
      )
      .isPresent();
    log.debug("isWithin total "+ begin.getLine() + "-" + end.getLine() + " :"+ isWithin);
    return isWithin;
  }
}
