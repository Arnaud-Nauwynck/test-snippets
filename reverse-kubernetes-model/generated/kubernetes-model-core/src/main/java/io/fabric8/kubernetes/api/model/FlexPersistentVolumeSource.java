
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class FlexPersistentVolumeSource implements KubernetesResource
{

    public java.lang.String driver;
    public java.lang.String fsType;
    public Map<String, String> options;
    public boolean readOnly;
    public SecretReference secretRef;

}
