
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DownwardAPIVolumeSource implements KubernetesResource
{

    public int defaultMode;
    public List<DownwardAPIVolumeFile> items = new ArrayList<DownwardAPIVolumeFile>();

}
