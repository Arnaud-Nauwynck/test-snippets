package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public abstract class AbstractDescribedNode extends AbstractNode {

    protected Description description;

}
