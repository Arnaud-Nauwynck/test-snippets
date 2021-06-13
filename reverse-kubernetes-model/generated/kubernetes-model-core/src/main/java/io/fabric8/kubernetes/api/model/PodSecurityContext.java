
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodSecurityContext implements KubernetesResource
{

    public Long fsGroup;
    public String fsGroupChangePolicy;
    public Long runAsGroup;
    public boolean runAsNonRoot;
    public Long runAsUser;
    public SELinuxOptions seLinuxOptions;
    public SeccompProfile seccompProfile;
    public List<Long> supplementalGroups = new ArrayList<Long>();
    public List<Sysctl> sysctls = new ArrayList<Sysctl>();
    public WindowsSecurityContextOptions windowsOptions;

}
