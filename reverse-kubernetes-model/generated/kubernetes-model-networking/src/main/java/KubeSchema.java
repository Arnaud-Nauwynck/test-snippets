import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.APIGroup;
import io.fabric8.kubernetes.api.model.APIGroupList;
import io.fabric8.kubernetes.api.model.BaseKubernetesList;
import io.fabric8.kubernetes.api.model.CreateOptions;
import io.fabric8.kubernetes.api.model.DeleteOptions;
import io.fabric8.kubernetes.api.model.GetOptions;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.Patch;
import io.fabric8.kubernetes.api.model.PatchOptions;
import io.fabric8.kubernetes.api.model.RootPaths;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.TypeMeta;
import io.fabric8.kubernetes.api.model.UpdateOptions;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyList;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public APIGroup aPIGroup;
    public APIGroupList aPIGroupList;
    public BaseKubernetesList baseKubernetesList;
    public CreateOptions createOptions;
    public DeleteOptions deleteOptions;
    public GetOptions getOptions;
    public Info info;
    public io.fabric8.kubernetes.api.model.networking.v1beta1.Ingress ingress;
    public io.fabric8.kubernetes.api.model.networking.v1beta1.IngressClass ingressClass;
    public io.fabric8.kubernetes.api.model.networking.v1beta1.IngressClassList ingressClassList;
    public io.fabric8.kubernetes.api.model.networking.v1beta1.IngressList ingressList;
    public ListOptions listOptions;
    public NetworkPolicy networkPolicy;
    public NetworkPolicyList networkPolicyList;
    public ObjectMeta objectMeta;
    public ObjectReference objectReference;
    public Patch patch;
    public PatchOptions patchOptions;
    public Quantity quantity;
    public RootPaths rootPaths;
    public Status status;
    public String time;
    public TypeMeta typeMeta;
    public UpdateOptions updateOptions;
    public io.fabric8.kubernetes.api.model.networking.v1.Ingress v1Ingress;
    public io.fabric8.kubernetes.api.model.networking.v1.IngressClass v1IngressClass;
    public io.fabric8.kubernetes.api.model.networking.v1.IngressClassList v1IngressClassList;
    public io.fabric8.kubernetes.api.model.networking.v1.IngressList v1IngressList;

}
