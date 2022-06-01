package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class Comment {
    public String content;
    public SourceLocation sourceLocation;

}
