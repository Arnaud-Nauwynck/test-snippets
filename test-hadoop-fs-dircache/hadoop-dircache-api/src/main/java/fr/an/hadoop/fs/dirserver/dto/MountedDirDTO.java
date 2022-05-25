package fr.an.hadoop.fs.dirserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MountedDirDTO {

	public String name;
	
	public String baseMountUrl;
	
	
}
