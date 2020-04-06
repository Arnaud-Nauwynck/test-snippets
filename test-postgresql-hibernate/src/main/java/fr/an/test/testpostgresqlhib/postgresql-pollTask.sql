
SET search_path TO testdb1;

CREATE OR REPLACE FUNCTION pollExecTask()
RETURNS bigint 
AS $$
DECLARE taskid bigint = 0;
BEGIN
    SELECT t.id INTO taskid FROM exec_task t WHERE t.status = 0 
        LIMIT 1 FOR UPDATE of t SKIP LOCKED;

    if (taskid > 0) THEN
        update exec_task 
            set status = 1 
            WHERE id = taskid;
    end if;

    RETURN taskid;
END;
$$  LANGUAGE plpgsql





CREATE OR REPLACE FUNCTION pollExecTask_updatePrev(prevTaskId bigint)
RETURNS bigint 
AS $$
DECLARE taskid bigint = 0;
BEGIN
    if (prevTaskId > 0) THEN
        delete from exec_task WHERE id = prevTaskId;
    end if;
    
    SELECT t.id INTO taskid FROM exec_task t WHERE t.status = 0 
        LIMIT 1 FOR UPDATE of t SKIP LOCKED;

    if (taskid > 0) THEN
        update exec_task 
            set status = 1 
            WHERE id = taskid;
    end if;

    RETURN taskid;
END;
$$  LANGUAGE plpgsql
