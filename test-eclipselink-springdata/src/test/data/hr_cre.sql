

CREATE TABLE departments( 
 department_id    NUMBER(4),
 department_name  VARCHAR2(30) CONSTRAINT  dept_name_nn  NOT NULL,
 manager_id       NUMBER(6),

 CONSTRAINT dept_id_pk  KEY (department_id),
 CONSTRAINT dept_mgr_fk FOREIGN KEY (manager_id) REFERENCES employees (employee_id),
);

CREATE UNIQUE INDEX dept_id_pk ON departments (department_id);
CREATE SEQUENCE departments_seq INCREMENT BY   10;


CREATE TABLE employees ( 
  employee_id    NUMBER(6),
  first_name     VARCHAR2(20),
  last_name      VARCHAR2(25) CONSTRAINT     emp_last_name_nn  NOT NULL,
  email          VARCHAR2(25) CONSTRAINT     emp_email_nn  NOT NULL,
  manager_id     NUMBER(6),
  department_id  NUMBER(4),

  CONSTRAINT     emp_email_uk UNIQUE (email),
  CONSTRAINT     emp_emp_id_pk PRIMARY KEY (employee_id),
  CONSTRAINT     emp_dept_fk FOREIGN KEY (department_id) REFERENCES departments,
  CONSTRAINT     emp_manager_fk FOREIGN KEY (manager_id) REFERENCES employees
);

CREATE UNIQUE INDEX emp_emp_id_pk ON employees (employee_id) ;
CREATE SEQUENCE employees_seq INCREMENT BY   1;
