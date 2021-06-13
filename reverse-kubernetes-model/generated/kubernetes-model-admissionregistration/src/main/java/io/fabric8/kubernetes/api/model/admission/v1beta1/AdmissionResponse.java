
package io.fabric8.kubernetes.api.model.admission.v1beta1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Status;

@Generated("jsonschema2pojo")
public class AdmissionResponse implements KubernetesResource
{

    public boolean allowed;
    public Map<String, String> auditAnnotations;
    public java.lang.String patch;
    public java.lang.String patchType;
    public Status status;
    public java.lang.String uid;
    public List<java.lang.String> warnings = new ArrayList<java.lang.String>();

}
