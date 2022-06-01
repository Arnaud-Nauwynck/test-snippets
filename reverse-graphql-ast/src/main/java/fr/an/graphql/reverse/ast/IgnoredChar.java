package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class IgnoredChar {

    public enum IgnoredCharKind {
        SPACE, COMMA, TAB, CR, LF, OTHER
    }

    private String value;
    private IgnoredCharKind kind;
    private SourceLocation sourceLocation;

}
