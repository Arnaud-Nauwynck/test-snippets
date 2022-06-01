package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class NodeParentTree<T /*extends Node*/> {

    private T node;
    private NodeParentTree<T> parent;
    private List<String> path;

}