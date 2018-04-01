package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import com.revature.model.Employee;

public class HomeControllerAlpha implements HomeController {

	@Override
	public String showEmployeeHome(HttpServletRequest request) {
	Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedUser");
		//TODO: implement method & use response to determine manager/employee
		/* If customer is not logged in */
		if(loggedEmployee == null) {
			return "login.html";
		}
		
		return "home.html";
	}

}
