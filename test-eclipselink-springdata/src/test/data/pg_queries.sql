
select * from departments;

select * from employees;

select d.*, e.email 
 from departments d, employees e 
 where d.manager_id = e.employee_id;

