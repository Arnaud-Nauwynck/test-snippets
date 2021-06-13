
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class PodSecurityPolicySpec implements KubernetesResource
{

    public boolean allowPrivilegeEscalation;
    public List<AllowedCSIDriver> allowedCSIDrivers = new ArrayList<AllowedCSIDriver>();
    public List<String> allowedCapabilities = new ArrayList<String>();
    public List<AllowedFlexVolume> allowedFlexVolumes = new ArrayList<AllowedFlexVolume>();
    public List<AllowedHostPath> allowedHostPaths = new ArrayList<AllowedHostPath>();
    public List<String> allowedProcMountTypes = new ArrayList<String>();
    public List<String> allowedUnsafeSysctls = new ArrayList<String>();
    public List<String> defaultAddCapabilities = new ArrayList<String>();
    public boolean defaultAllowPrivilegeEscalation;
    public List<String> forbiddenSysctls = new ArrayList<String>();
    public FSGroupStrategyOptions fsGroup;
    public boolean hostIPC;
    public boolean hostNetwork;
    public boolean hostPID;
    public List<HostPortRange> hostPorts = new ArrayList<HostPortRange>();
    public boolean privileged;
    public boolean readOnlyRootFilesystem;
    public List<String> requiredDropCapabilities = new ArrayList<String>();
    public RunAsGroupStrategyOptions runAsGroup;
    public RunAsUserStrategyOptions runAsUser;
    public RuntimeClassStrategyOptions runtimeClass;
    public SELinuxStrategyOptions seLinux;
    public SupplementalGroupsStrategyOptions supplementalGroups;
    public List<String> volumes = new ArrayList<String>();

}
