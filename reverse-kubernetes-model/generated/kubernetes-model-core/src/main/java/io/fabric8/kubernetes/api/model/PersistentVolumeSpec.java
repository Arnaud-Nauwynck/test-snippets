
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeSpec implements KubernetesResource
{

    public List<java.lang.String> accessModes = new ArrayList<java.lang.String>();
    public AWSElasticBlockStoreVolumeSource awsElasticBlockStore;
    public AzureDiskVolumeSource azureDisk;
    public AzureFilePersistentVolumeSource azureFile;
    public Map<String, Quantity> capacity;
    public CephFSPersistentVolumeSource cephfs;
    public CinderPersistentVolumeSource cinder;
    public ObjectReference claimRef;
    public CSIPersistentVolumeSource csi;
    public FCVolumeSource fc;
    public FlexPersistentVolumeSource flexVolume;
    public FlockerVolumeSource flocker;
    public GCEPersistentDiskVolumeSource gcePersistentDisk;
    public GlusterfsPersistentVolumeSource glusterfs;
    public HostPathVolumeSource hostPath;
    public ISCSIPersistentVolumeSource iscsi;
    public LocalVolumeSource local;
    public List<java.lang.String> mountOptions = new ArrayList<java.lang.String>();
    public NFSVolumeSource nfs;
    public VolumeNodeAffinity nodeAffinity;
    public java.lang.String persistentVolumeReclaimPolicy;
    public PhotonPersistentDiskVolumeSource photonPersistentDisk;
    public PortworxVolumeSource portworxVolume;
    public QuobyteVolumeSource quobyte;
    public RBDPersistentVolumeSource rbd;
    public ScaleIOPersistentVolumeSource scaleIO;
    public java.lang.String storageClassName;
    public StorageOSPersistentVolumeSource storageos;
    public java.lang.String volumeMode;
    public VsphereVirtualDiskVolumeSource vsphereVolume;

}
