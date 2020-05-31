package  com.github.stcarolas.lijzeil.functions.common;

import static io.vavr.API.*;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.stcarolas.lijzeil.Range;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Named("FindWrappingSelectionNode")
public class FindWrappingSelectionNode 
	implements Function3<Range, Function<Node,Boolean>, Node, List<Node>> {

	@Inject @Named("FindParentNode")
	private Function2<Function<Node,Boolean>, Node, Option<Node>> findTargetParent;

	public List<Node> apply(
		Range range,
		Function<Node,Boolean> predicate,
		Node unit
	){
		List<Node> nodes = filterChildNodesWrappingRange(range, unit)
			.foldLeft(
				List(), 
				(list, node) -> list.appendAll(findTargetParent.apply(predicate, node)).distinct()
			);
		log.debug("found parent nodes: {}", nodes.size());
		return nodes;
	}

	public List<Node> filterChildNodesWrappingRange(Range range, Node node) {
		List<Node> childNodes = List.of(node);
		List<Node> selectedNodes = childNodes;
		while(!childNodes.isEmpty()){
			selectedNodes = childNodes;
			childNodes = childNodes
				.flatMap( unit -> List.ofAll(unit.getChildNodes()) )
				.filter(range::wrappedBy);
			log.debug("childNode iteration: {}", childNodes);
		}
		log.debug("selectedNodes: {}", selectedNodes);
		return selectedNodes;
	}

}
