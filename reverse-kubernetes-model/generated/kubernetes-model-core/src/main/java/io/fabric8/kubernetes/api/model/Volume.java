
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Volume implements KubernetesResource
{

    public AWSElasticBlockStoreVolumeSource awsElasticBlockStore;
    public AzureDiskVolumeSource azureDisk;
    public AzureFileVolumeSource azureFile;
    public CephFSVolumeSource cephfs;
    public CinderVolumeSource cinder;
    public ConfigMapVolumeSource configMap;
    public CSIVolumeSource csi;
    public DownwardAPIVolumeSource downwardAPI;
    public EmptyDirVolumeSource emptyDir;
    public EphemeralVolumeSource ephemeral;
    public FCVolumeSource fc;
    public FlexVolumeSource flexVolume;
    public FlockerVolumeSource flocker;
    public GCEPersistentDiskVolumeSource gcePersistentDisk;
    public GitRepoVolumeSource gitRepo;
    public GlusterfsVolumeSource glusterfs;
    public HostPathVolumeSource hostPath;
    public ISCSIVolumeSource iscsi;
    public String name;
    public NFSVolumeSource nfs;
    public PersistentVolumeClaimVolumeSource persistentVolumeClaim;
    public PhotonPersistentDiskVolumeSource photonPersistentDisk;
    public PortworxVolumeSource portworxVolume;
    public ProjectedVolumeSource projected;
    public QuobyteVolumeSource quobyte;
    public RBDVolumeSource rbd;
    public ScaleIOVolumeSource scaleIO;
    public SecretVolumeSource secret;
    public StorageOSVolumeSource storageos;
    public VsphereVirtualDiskVolumeSource vsphereVolume;

}
