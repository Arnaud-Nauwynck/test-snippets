
package io.fabric8.kubernetes.api.model.events.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.EventSource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.MicroTime;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectReference;

@Generated("jsonschema2pojo")
public class Event implements HasMetadata, Namespaced
{

    public java.lang.String action;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "events.k8s.io/v1beta1";
    public int deprecatedCount;
    public String deprecatedFirstTimestamp;
    public String deprecatedLastTimestamp;
    public EventSource deprecatedSource;
    public MicroTime eventTime;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "Event";
    public ObjectMeta metadata;
    public java.lang.String note;
    public java.lang.String reason;
    public ObjectReference regarding;
    public ObjectReference related;
    public java.lang.String reportingController;
    public java.lang.String reportingInstance;
    public EventSeries series;
    public java.lang.String type;

}
