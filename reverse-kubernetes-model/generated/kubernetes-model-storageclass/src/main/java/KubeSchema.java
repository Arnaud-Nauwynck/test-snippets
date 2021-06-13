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
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.RootPaths;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.TypeMeta;
import io.fabric8.kubernetes.api.model.UpdateOptions;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.fabric8.kubernetes.api.model.storage.VolumeAttachment;
import io.fabric8.kubernetes.api.model.storage.VolumeAttachmentList;
import io.fabric8.kubernetes.api.model.storage.v1beta1.CSIStorageCapacity;
import io.fabric8.kubernetes.api.model.storage.v1beta1.CSIStorageCapacityList;
import io.fabric8.kubernetes.api.model.version.Info;

@Generated("jsonschema2pojo")
public class KubeSchema {

    public APIGroup aPIGroup;
    public APIGroupList aPIGroupList;
    public BaseKubernetesList baseKubernetesList;
    public io.fabric8.kubernetes.api.model.storage.v1beta1.CSIDriver cSIDriver;
    public io.fabric8.kubernetes.api.model.storage.v1beta1.CSIDriverList cSIDriverList;
    public io.fabric8.kubernetes.api.model.storage.v1beta1.CSINode cSINode;
    public io.fabric8.kubernetes.api.model.storage.v1beta1.CSINodeList cSINodeList;
    public CSIStorageCapacity cSIStorageCapacity;
    public CSIStorageCapacityList cSIStorageCapacityList;
    public CreateOptions createOptions;
    public DeleteOptions deleteOptions;
    public GetOptions getOptions;
    public Info info;
    public ListOptions listOptions;
    public ObjectMeta objectMeta;
    public ObjectReference objectReference;
    public Patch patch;
    public PatchOptions patchOptions;
    public Quantity quantity;
    public RootPaths rootPaths;
    public Status status;
    public StorageClass storageClass;
    public StorageClassList storageClassList;
    public String time;
    public TypeMeta typeMeta;
    public UpdateOptions updateOptions;
    public io.fabric8.kubernetes.api.model.storage.CSIDriver v1CSIDriver;
    public io.fabric8.kubernetes.api.model.storage.CSIDriverList v1CSIDriverList;
    public io.fabric8.kubernetes.api.model.storage.CSINode v1CSINode;
    public io.fabric8.kubernetes.api.model.storage.CSINodeList v1CSINodeList;
    public VolumeAttachment volumeAttachment;
    public VolumeAttachmentList volumeAttachmentList;

}
