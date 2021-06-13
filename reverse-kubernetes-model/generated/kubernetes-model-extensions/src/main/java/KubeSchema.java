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
import io.fabric8.kubernetes.api.model.extensions.DaemonSet;
import io.fabric8.kubernetes.api.model.extensions.DaemonSetList;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.api.model.extensions.DeploymentRollback;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressList;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicy;
import io.fabric8.kubernetes.api.model.extensions.NetworkPolicyList;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSet;
import io.fabric8.kubernetes.api.model.extensions.ReplicaSetList;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public APIGroup aPIGroup;
    public APIGroupList aPIGroupList;
    public BaseKubernetesList baseKubernetesList;
    public CreateOptions createOptions;
    public DaemonSet daemonSet;
    public DaemonSetList daemonSetList;
    public DeleteOptions deleteOptions;
    public Deployment deployment;
    public DeploymentList deploymentList;
    public DeploymentRollback deploymentRollback;
    public GetOptions getOptions;
    public Info info;
    public Ingress ingress;
    public IngressList ingressList;
    public ListOptions listOptions;
    public NetworkPolicy networkPolicy;
    public NetworkPolicyList networkPolicyList;
    public ObjectMeta objectMeta;
    public ObjectReference objectReference;
    public Patch patch;
    public PatchOptions patchOptions;
    public Quantity quantity;
    public ReplicaSet replicaSet;
    public ReplicaSetList replicaSetList;
    public RootPaths rootPaths;
    public Status status;
    public String time;
    public TypeMeta typeMeta;
    public UpdateOptions updateOptions;

}
