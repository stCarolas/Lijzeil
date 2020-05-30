package com.github.stcarolas.lijzeil.functions.createapplymethod;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.stcarolas.lijzeil.Range;

import io.vavr.Function3;
import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

@Named("FindFieldDeclarations")
public class FindFieldDeclarations 
  implements Function2<CompilationUnit, Range, List<FieldDeclaration>> {

  public List<FieldDeclaration> apply(CompilationUnit unit, Range range) {
    return findSelectedNode.apply(
        range,
        node -> node.getClass().equals(FieldDeclaration.class),
        unit
      )
      .map(item -> (FieldDeclaration)item);
  }

  @Inject @Named("FindWrappingSelectionNode")
  private Function3<Range, Function<Node, Boolean>, Node, List<Node>>
    findSelectedNode;

}
