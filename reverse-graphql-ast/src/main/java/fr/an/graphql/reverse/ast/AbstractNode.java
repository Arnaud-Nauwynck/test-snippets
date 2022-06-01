package fr.an.graphql.reverse.ast;


import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public abstract class AbstractNode {

    private SourceLocation sourceLocation;
    private List<Comment> comments;
    private IgnoredChars ignoredChars;
    private Map<String, String> additionalData;

    public abstract <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param);
    
}
