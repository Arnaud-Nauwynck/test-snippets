
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EphemeralContainer implements KubernetesResource
{

    public List<String> args = new ArrayList<String>();
    public List<String> command = new ArrayList<String>();
    public List<EnvVar> env = new ArrayList<EnvVar>();
    public List<EnvFromSource> envFrom = new ArrayList<EnvFromSource>();
    public String image;
    public String imagePullPolicy;
    public Lifecycle lifecycle;
    public Probe livenessProbe;
    public String name;
    public List<ContainerPort> ports = new ArrayList<ContainerPort>();
    public Probe readinessProbe;
    public ResourceRequirements resources;
    public SecurityContext securityContext;
    public Probe startupProbe;
    public boolean stdin;
    public boolean stdinOnce;
    public String targetContainerName;
    public String terminationMessagePath;
    public String terminationMessagePolicy;
    public boolean tty;
    public List<VolumeDevice> volumeDevices = new ArrayList<VolumeDevice>();
    public List<VolumeMount> volumeMounts = new ArrayList<VolumeMount>();
    public String workingDir;

}
