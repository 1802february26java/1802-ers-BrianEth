package com.revature.service;

import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.controller.LoginController;
import com.revature.model.Employee;
import com.revature.model.EmployeeToken;
import com.revature.repository.EmployeeRepository;
import com.revature.repository.EmployeeRepositoryJdbc;

public class EmployeeServices implements EmployeeService {
	EmployeeRepository repository = EmployeeRepositoryJdbc.getInstance();
	
	private static Logger logger = Logger.getLogger(EmployeeServices.class);

	
	private static EmployeeServices services = new EmployeeServices();
	
	private EmployeeServices(){
		
	}
	
	public static EmployeeServices getInstance() {
		return services;
	}
	
	
	@Override
	public Employee authenticate(Employee employee) {
		//TODO: add check for false employee, check for null?
		System.out.println("in service layer.");
		Employee logged = repository.select(employee.getUsername());
		if(logged.getUsername() == null)
		{
			return null;
		}
		String pw = repository.getPasswordHash(employee);
		logger.trace(pw);
		if(pw.equals(logged.getPassword())) {
			logger.trace(logged.toString());
			return logged;
		}
		return null;
	}

	@Override
	public Employee getEmployeeInformation(Employee employee) {
		
		//maybe get by username?
		return repository.select(employee.getId());
	}

	@Override
	public Set<Employee> getAllEmployeesInformation() {
		
		return repository.selectAll();
	}

	@Override
	public boolean createEmployee(Employee employee) {
		return repository.insert(employee);
	}

	@Override
	public boolean updateEmployeeInformation(Employee employee) {
		return repository.update(employee);
	}

	@Override
	public boolean updatePassword(Employee employee) {
		// TODO pasword updating
		return false;
	}

	@Override
	public boolean isUsernameTaken(Employee employee) {
		Employee existing = repository.select(employee.getUsername());
		if(existing.getUsername() == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean createPasswordToken(Employee employee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletePasswordToken(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTokenExpired(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub
		return false;
	}

}
