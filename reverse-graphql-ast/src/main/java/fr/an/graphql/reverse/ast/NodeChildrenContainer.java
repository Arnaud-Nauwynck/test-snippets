package fr.an.graphql.reverse.ast;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import lombok.Data;

@Data
public class NodeChildrenContainer {

    private Map<String, List<Node>> children = new LinkedHashMap<>();

}
