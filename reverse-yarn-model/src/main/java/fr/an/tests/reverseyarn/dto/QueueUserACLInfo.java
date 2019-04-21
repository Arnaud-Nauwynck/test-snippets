package fr.an.tests.reverseyarn.dto;

import java.util.List;

import lombok.Data;

@Data
public class QueueUserACLInfo {

	public String queueName;
	public List<QueueACL> acls;

}
