package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class SourceLocation {

    private int line;
    private int column;
    private String sourceName;

}
