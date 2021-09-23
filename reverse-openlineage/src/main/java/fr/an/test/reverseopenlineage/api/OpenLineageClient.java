package fr.an.test.reverseopenlineage.api;

public interface OpenLineageClient {

	// POST "/api/{version}/lineage"
	public void postLineageRunEvent(OpenLineageDTO.RunEvent event);
	
}
