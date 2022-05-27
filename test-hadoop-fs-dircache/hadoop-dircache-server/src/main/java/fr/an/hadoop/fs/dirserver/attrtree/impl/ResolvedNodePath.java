package fr.an.hadoop.fs.dirserver.attrtree.impl;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResolvedNodePath {

	public final DirNode[] pathDirs;
	public final Node node;
	
}
