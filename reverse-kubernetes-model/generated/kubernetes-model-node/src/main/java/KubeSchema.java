import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.BaseKubernetesList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.TypeMeta;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public BaseKubernetesList baseKubernetesList;
    public Info info;
    public ObjectMeta objectMeta;
    public ObjectReference objectReference;
    public Quantity quantity;
    public Status status;
    public TypeMeta typeMeta;
    public io.fabric8.kubernetes.api.model.node.v1.RuntimeClass v1RuntimeClass;
    public io.fabric8.kubernetes.api.model.node.v1.RuntimeClassList v1RuntimeClassList;
    public io.fabric8.kubernetes.api.model.node.v1alpha1.RuntimeClass v1alpha1RuntimeClass;
    public io.fabric8.kubernetes.api.model.node.v1alpha1.RuntimeClassList v1alpha1RuntimeClassList;
    public io.fabric8.kubernetes.api.model.node.v1beta1.RuntimeClass v1beta1RuntimeClass;
    public io.fabric8.kubernetes.api.model.node.v1beta1.RuntimeClassList v1beta1RuntimeClassList;

}
