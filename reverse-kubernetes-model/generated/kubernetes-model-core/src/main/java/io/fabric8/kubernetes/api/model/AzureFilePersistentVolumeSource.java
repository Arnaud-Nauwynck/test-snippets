
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class AzureFilePersistentVolumeSource implements KubernetesResource
{

    public boolean readOnly;
    public String secretName;
    public String secretNamespace;
    public String shareName;

}
