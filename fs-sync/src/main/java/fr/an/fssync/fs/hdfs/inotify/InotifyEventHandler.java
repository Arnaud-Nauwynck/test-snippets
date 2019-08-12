package fr.an.fssync.fs.hdfs.inotify;

import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.Event.AppendEvent;
import org.apache.hadoop.hdfs.inotify.Event.CloseEvent;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent;
import org.apache.hadoop.hdfs.inotify.Event.MetadataUpdateEvent;
import org.apache.hadoop.hdfs.inotify.Event.RenameEvent;
import org.apache.hadoop.hdfs.inotify.Event.TruncateEvent;
import org.apache.hadoop.hdfs.inotify.Event.UnlinkEvent;

import lombok.val;

public abstract class InotifyEventHandler {

    public void handleInotifyEvents(Event[] events) {
	for(Event event : events) {
	    handleInotifyEvent(event);
	}
    }
    
    public void handleInotifyEvent(Event event) {
	val eventType = event.getEventType();
	switch(eventType) {
	case CREATE:
	    handleCreateEvent((CreateEvent) event); 
	    break;
	case CLOSE: 
	    handleCloseEvent((CloseEvent) event); 
	    break;
	case APPEND:
	    handleAppendEvent((AppendEvent) event); 
	    break;
	case RENAME:
	    handleRenameEvent((RenameEvent) event); 
	    break;
	case METADATA:
	    handleMetadataUpdateEvent((MetadataUpdateEvent) event); 
	    break;
	case UNLINK:
	    handleUnlinkEvent((UnlinkEvent) event); 
	    break;
	case TRUNCATE:
	    handleTruncateEvent((TruncateEvent) event); 
	    break;
	}
    }
    
    public abstract void handleCreateEvent(CreateEvent event);
    
    public abstract void handleCloseEvent(CloseEvent event);
    
    public abstract void handleAppendEvent(AppendEvent event);
    
    public abstract void handleRenameEvent(RenameEvent event);
    
    public abstract void handleMetadataUpdateEvent(MetadataUpdateEvent event);
    
    public abstract void handleUnlinkEvent(UnlinkEvent event);
    
    public abstract void handleTruncateEvent(TruncateEvent event);

}
