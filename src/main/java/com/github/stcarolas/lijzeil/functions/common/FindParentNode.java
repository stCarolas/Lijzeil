package  com.github.stcarolas.lijzeil.functions.common;

import java.util.function.Function;

import javax.inject.Named;

import com.github.javaparser.ast.Node;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.extern.log4j.Log4j2;
import static io.vavr.API.*;

@Log4j2
@Named("FindParentNode")
public class FindParentNode 
	implements Function2<Function<Node,Boolean>, Node, Option<Node>> {

	@Override
	public Option<Node> apply(Function<Node, Boolean> predicate, Node childNode) {
		Option<Node> node = Option(childNode);
		while(notTargetNode(predicate, node)){
			node = node.flatMap(this::getParent);
		}
		log.trace("targetNode: {}", node);
		return node;
	}

	public boolean notTargetNode(Function<Node, Boolean> predicate, Option<Node> node){
		log.trace("node: {}", node);
		boolean predicateResult = node.exists($ -> predicate.apply($));
		log.trace("predicate: {}", predicateResult);
		boolean lastNode = node.isEmpty();
		log.trace("isLastNode: {}", lastNode);
		return  lastNode || !predicateResult ;
	}

	public Option<Node> getParent(Node childNode){
		return Option.ofOptional(childNode.getParentNode());
	}

}
