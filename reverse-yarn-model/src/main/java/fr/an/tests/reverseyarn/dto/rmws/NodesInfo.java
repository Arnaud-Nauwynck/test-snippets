package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class NodesInfo {

	protected List<NodeInfo> node = new ArrayList<NodeInfo>();

}
