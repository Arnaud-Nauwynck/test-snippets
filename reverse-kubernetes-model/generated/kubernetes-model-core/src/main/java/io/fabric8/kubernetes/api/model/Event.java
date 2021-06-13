
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Event implements HasMetadata, Namespaced
{

    public java.lang.String action;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "v1";
    public int count;
    public MicroTime eventTime;
    public String firstTimestamp;
    public ObjectReference involvedObject;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "Event";
    public String lastTimestamp;
    public java.lang.String message;
    public ObjectMeta metadata;
    public java.lang.String reason;
    public ObjectReference related;
    public java.lang.String reportingComponent;
    public java.lang.String reportingInstance;
    public EventSeries series;
    public EventSource source;
    public java.lang.String type;

}
