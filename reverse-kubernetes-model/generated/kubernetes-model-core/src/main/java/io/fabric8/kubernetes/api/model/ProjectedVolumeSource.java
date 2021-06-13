
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ProjectedVolumeSource implements KubernetesResource
{

    public int defaultMode;
    public List<VolumeProjection> sources = new ArrayList<VolumeProjection>();

}
