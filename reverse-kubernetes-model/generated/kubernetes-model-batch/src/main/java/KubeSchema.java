import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.APIGroup;
import io.fabric8.kubernetes.api.model.APIGroupList;
import io.fabric8.kubernetes.api.model.BaseKubernetesList;
import io.fabric8.kubernetes.api.model.CreateOptions;
import io.fabric8.kubernetes.api.model.DeleteOptions;
import io.fabric8.kubernetes.api.model.GetOptions;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Patch;
import io.fabric8.kubernetes.api.model.PatchOptions;
import io.fabric8.kubernetes.api.model.PodTemplate;
import io.fabric8.kubernetes.api.model.RootPaths;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.TypeMeta;
import io.fabric8.kubernetes.api.model.UpdateOptions;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public APIGroup aPIGroup;
    public APIGroupList aPIGroupList;
    public BaseKubernetesList baseKubernetesList;
    public CreateOptions createOptions;
    public io.fabric8.kubernetes.api.model.batch.v1beta1.CronJob cronJob;
    public io.fabric8.kubernetes.api.model.batch.v1beta1.CronJobList cronJobList;
    public DeleteOptions deleteOptions;
    public GetOptions getOptions;
    public Info info;
    public Job job;
    public JobList jobList;
    public ListOptions listOptions;
    public ObjectMeta objectMeta;
    public Patch patch;
    public PatchOptions patchOptions;
    public PodTemplate podTemplateList;
    public Quantity quantity;
    public RootPaths rootPaths;
    public Status status;
    public String time;
    public TypeMeta typeMeta;
    public UpdateOptions updateOptions;
    public io.fabric8.kubernetes.api.model.batch.v1.CronJob v1CronJob;
    public io.fabric8.kubernetes.api.model.batch.v1.CronJobList v1CronJobList;

}
