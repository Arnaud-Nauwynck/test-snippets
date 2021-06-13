
package io.fabric8.kubernetes.api.model.batch.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class JobTemplateSpec implements KubernetesResource
{

    public ObjectMeta metadata;
    public JobSpec spec;

}
