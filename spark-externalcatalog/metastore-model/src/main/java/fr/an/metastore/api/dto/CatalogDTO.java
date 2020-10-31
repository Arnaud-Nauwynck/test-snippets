package fr.an.metastore.api.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CatalogDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String,CatalogDatabaseDTO> databases = new LinkedHashMap<>();

}
