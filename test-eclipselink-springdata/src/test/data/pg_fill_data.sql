
insert into departments (department_id, department_name)
  values (nextval('departments_seq'), 'Administration Department');
insert into departments (department_id, department_name)
  values (nextval('departments_seq'), 'IT Department');
insert into departments (department_id, department_name)
  values (nextval('departments_seq'), 'Sales Department');

-- select * from departments


insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'BossFirstname', 'BossLastname', 'bossfirst.last@company.com', NULL, 1);

insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'VicePresFirstName', 'VicePresLastName', 'vicepresfirst.last@company.com', 1, 1);

insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'AdminDptMgrFirstName', 'AdminDptMgrLastName', 'AdminDptMgr@company.com', 2, 1);

insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'ITDptMgrFirstName', 'ITDptMgrLastName', 'ITDptMgr@company.com', 2, 2);

insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'SalesDptMgrFirstName', 'SalesDptMgrLastName', 'SalesDptMgr@company.com', 2, 3);

insert into employees(employee_id, first_name, last_name, email, manager_id, department_id)
 values (nextval('employees_seq'), 'John', 'Smith', 'john.smith@company.com', 4, 2);

-- select * from employees

update departments set manager_id = 3 where department_name = 'Administration Department';
update departments set manager_id = 4 where department_name = 'IT Department';
update departments set manager_id = 5 where department_name = 'Sales Department';



