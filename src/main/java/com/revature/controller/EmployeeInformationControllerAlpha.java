package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.service.EmployeeService;
import com.revature.service.EmployeeServices;

public class EmployeeInformationControllerAlpha implements EmployeeInformationController {

	EmployeeService service = EmployeeServices.getInstance();

	private static Logger logger = Logger.getLogger(EmployeeInformationControllerAlpha.class);

	
	@Override
	public Object registerEmployee(HttpServletRequest request) {
		Employee employee = new Employee( 0,
				request.getParameter("username"),
				request.getParameter("password"),
				request.getParameter("firstname"),
				request.getParameter("lastname"),
				request.getParameter("email"),
				new EmployeeRole(1, "EMPLOYEE"));
		// TODO: allow register new manager?
		return service.createEmployee(employee);
	}

	@Override
	public Object updateEmployee(HttpServletRequest request) {
		logger.trace("in controller");
		int idDebug = ((Employee) request.getSession().getAttribute("loggedUser")).getId();
		logger.trace("idDebug: "+idDebug);
		Employee employee = new Employee( 
				idDebug,
				request.getParameter("firstname"),
				request.getParameter("lastname"),
				request.getParameter("username"),
				"notARealPassword",
				request.getParameter("email"),
				null);
		logger.trace("updating user to: " + employee);
		return service.updateEmployeeInformation(employee);
	}

	@Override
	public Object viewEmployeeInformation(HttpServletRequest request) {
		Employee loggedUser = (Employee) request.getSession().getAttribute("loggedUser");
		return service.getEmployeeInformation(loggedUser);
	}

	@Override
	public Object viewAllEmployees(HttpServletRequest request) {
		return service.getAllEmployeesInformation();
	}

	@Override
	public Object usernameExists(HttpServletRequest request) {
		Employee checkName = new Employee (0,
				request.getParameter("name"),
				"blankPassword",
				"blankFirstName",
				"blankLastName",
				"blankEmail",
				null);
		return service.isUsernameTaken(checkName);
	}

}
