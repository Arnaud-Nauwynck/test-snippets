package fr.an.tests.reverseyarn.dto;

import java.net.URL;

import lombok.Data;

@Data
public class LocalResource {

	public URL resource;
	public long size;
	public long timestamp;
	public LocalResourceType type;
	public LocalResourceVisibility visibility;
	public String pattern;
	public boolean shouldBeUploadedToSharedCache;

}
