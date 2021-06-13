
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ExecConfig implements KubernetesResource
{

    public String apiVersion;
    public List<String> args = new ArrayList<String>();
    public String command;
    public List<ExecEnvVar> env = new ArrayList<ExecEnvVar>();
    public String installHint;
    public boolean provideClusterInfo;

}
