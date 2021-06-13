
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodSpec implements KubernetesResource
{

    public Long activeDeadlineSeconds;
    public Affinity affinity;
    public boolean automountServiceAccountToken;
    public List<Container> containers = new ArrayList<Container>();
    public PodDNSConfig dnsConfig;
    public java.lang.String dnsPolicy;
    public boolean enableServiceLinks;
    public List<EphemeralContainer> ephemeralContainers = new ArrayList<EphemeralContainer>();
    public List<HostAlias> hostAliases = new ArrayList<HostAlias>();
    public boolean hostIPC;
    public boolean hostNetwork;
    public boolean hostPID;
    public java.lang.String hostname;
    public List<LocalObjectReference> imagePullSecrets = new ArrayList<LocalObjectReference>();
    public List<Container> initContainers = new ArrayList<Container>();
    public java.lang.String nodeName;
    public Map<String, String> nodeSelector;
    public Map<String, Quantity> overhead;
    public java.lang.String preemptionPolicy;
    public int priority;
    public java.lang.String priorityClassName;
    public List<PodReadinessGate> readinessGates = new ArrayList<PodReadinessGate>();
    public java.lang.String restartPolicy;
    public java.lang.String runtimeClassName;
    public java.lang.String schedulerName;
    public PodSecurityContext securityContext;
    public java.lang.String serviceAccount;
    public java.lang.String serviceAccountName;
    public boolean setHostnameAsFQDN;
    public boolean shareProcessNamespace;
    public java.lang.String subdomain;
    public Long terminationGracePeriodSeconds;
    public List<Toleration> tolerations = new ArrayList<Toleration>();
    public List<TopologySpreadConstraint> topologySpreadConstraints = new ArrayList<TopologySpreadConstraint>();
    public List<Volume> volumes = new ArrayList<Volume>();

}
