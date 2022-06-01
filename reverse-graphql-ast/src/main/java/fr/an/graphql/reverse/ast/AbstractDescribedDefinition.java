package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public abstract class AbstractDescribedDefinition extends Definition {

    protected Description description;
    
}
