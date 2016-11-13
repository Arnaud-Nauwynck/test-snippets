package fr.an.tools.git2neo4j.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;

import fr.an.tools.git2neo4j.domain.RevTreeEntity;

public class ObjectIdUtils {

	private ObjectIdUtils() {}

	public static Map<ObjectId,RevTreeEntity> lsToMap(Collection<RevTreeEntity> ls) {
		Map<ObjectId,RevTreeEntity> res = new HashMap<>(ls.size());
		for(RevTreeEntity e : ls) {
			res.put(e.getObjectId(), e);
		}
		return res;
	}
	
	public static List<String> toNameList(Collection<ObjectId> ids) {
		ArrayList<String> res = new ArrayList<>(ids.size());
		for(ObjectId id : ids) {
			res.add(id.name());
		}
		return res;
	}
}
