
cf 
        <class>org.apache.oozie.WorkflowActionBean</class>
        <class>org.apache.oozie.WorkflowJobBean</class>
        <class>org.apache.oozie.CoordinatorJobBean</class>
        <class>org.apache.oozie.CoordinatorActionBean</class>
        <class>org.apache.oozie.SLAEventBean</class>
        <class>org.apache.oozie.BundleJobBean</class>
        <class>org.apache.oozie.BundleActionBean</class>
        <class>org.apache.oozie.sla.SLARegistrationBean</class>
        <class>org.apache.oozie.sla.SLASummaryBean</class>
   
   strange not found classes ??     
        <class>org.apache.oozie.client.rest.JsonWorkflowJob</class>
        <class>org.apache.oozie.client.rest.JsonWorkflowAction</class>
        <class>org.apache.oozie.client.rest.JsonCoordinatorJob</class>
        <class>org.apache.oozie.client.rest.JsonCoordinatorAction</class>
        <class>org.apache.oozie.client.rest.JsonSLAEvent</class>
        <class>org.apache.oozie.client.rest.JsonBundleJob</class>
        <class>org.apache.oozie.util.db.ValidateConnectionBean</class>

oozie.sql => tmp file generated from  
oozie-tools/src/main/java/org/apache/oozie/tools/OozieDBCLI.java
.. from org.apache.openjpa.jdbc.meta.MappingTool.main(args);

indexes.. cf
org.apache.oozie.util.db.CompositeIndex

    I_WF_JOBS_STATUS_CREATED_TIME ("WF_JOBS", "status", "created_time"),

    I_COORD_ACTIONS_JOB_ID_STATUS ("COORD_ACTIONS", "job_id", "status"),

    I_COORD_JOBS_STATUS_CREATED_TIME ("COORD_JOBS", "status", "created_time"),
    I_COORD_JOBS_STATUS_LAST_MODIFIED_TIME ("COORD_JOBS", "status", "last_modified_time"),
    I_COORD_JOBS_PENDING_DONE_MATERIALIZATION_LAST_MODIFIED_TIME("COORD_JOBS", "pending", "done_materialization", "last_modified_time"),
    I_COORD_JOBS_PENDING_LAST_MODIFIED_TIME ("COORD_JOBS", "pending", "last_modified_time"),

    I_BUNLDE_JOBS_STATUS_CREATED_TIME ("BUNDLE_JOBS", "status", "created_time"),
    I_BUNLDE_JOBS_STATUS_LAST_MODIFIED_TIME ("BUNDLE_JOBS", "status", "last_modified_time"),

    I_BUNLDE_ACTIONS_PENDING_LAST_MODIFIED_TIME ("BUNDLE_ACTIONS", "pending", "last_modified_time");
