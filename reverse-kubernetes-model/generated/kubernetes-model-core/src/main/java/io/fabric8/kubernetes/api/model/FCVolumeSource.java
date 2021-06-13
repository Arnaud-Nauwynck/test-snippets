
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class FCVolumeSource implements KubernetesResource
{

    public String fsType;
    public int lun;
    public boolean readOnly;
    public List<String> targetWWNs = new ArrayList<String>();
    public List<String> wwids = new ArrayList<String>();

}
