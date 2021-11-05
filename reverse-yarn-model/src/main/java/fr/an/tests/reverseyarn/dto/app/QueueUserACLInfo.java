package fr.an.tests.reverseyarn.dto.app;

import java.util.List;

import lombok.Data;

@Data
public class QueueUserACLInfo {

	public String queueName;
	public List<QueueACL> acls;

}
