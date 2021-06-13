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
import io.fabric8.kubernetes.api.model.policy.v1beta1.Eviction;
import io.fabric8.kubernetes.api.model.policy.v1beta1.PodSecurityPolicy;
import io.fabric8.kubernetes.api.model.policy.v1beta1.PodSecurityPolicyList;
import io.fabric8.kubernetes.api.model.policy.v1beta1.RunAsUserStrategyOptions;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public APIGroup aPIGroup;
    public APIGroupList aPIGroupList;
    public BaseKubernetesList baseKubernetesList;
    public CreateOptions createOptions;
    public DeleteOptions deleteOptions;
    public Eviction eviction;
    public GetOptions getOptions;
    public Info info;
    public RunAsUserStrategyOptions kubernetesRunAsUserStrategyOptions;
    public ListOptions listOptions;
    public ObjectMeta objectMeta;
    public ObjectReference objectReference;
    public Patch patch;
    public PatchOptions patchOptions;
    public io.fabric8.kubernetes.api.model.policy.v1beta1.PodDisruptionBudget podDisruptionBudget;
    public io.fabric8.kubernetes.api.model.policy.v1beta1.PodDisruptionBudgetList podDisruptionBudgetList;
    public PodSecurityPolicy podSecurityPolicy;
    public PodSecurityPolicyList podSecurityPolicyList;
    public Quantity quantity;
    public RootPaths rootPaths;
    public Status status;
    public String time;
    public TypeMeta typeMeta;
    public UpdateOptions updateOptions;
    public io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudget v1PodDisruptionBudget;
    public io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudgetList v1PodDisruptionBudgetList;

}
