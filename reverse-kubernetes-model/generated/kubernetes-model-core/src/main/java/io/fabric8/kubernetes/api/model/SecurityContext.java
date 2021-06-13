
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SecurityContext implements KubernetesResource
{

    public boolean allowPrivilegeEscalation;
    public Capabilities capabilities;
    public boolean privileged;
    public String procMount;
    public boolean readOnlyRootFilesystem;
    public Long runAsGroup;
    public boolean runAsNonRoot;
    public Long runAsUser;
    public SELinuxOptions seLinuxOptions;
    public SeccompProfile seccompProfile;
    public WindowsSecurityContextOptions windowsOptions;

}
