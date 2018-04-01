package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.service.EmployeeService;
import com.revature.service.EmployeeServices;

public class LoginControllerAlpha implements LoginController {

	EmployeeService service = EmployeeServices.getInstance();

	private static Logger logger = Logger.getLogger(LoginController.class);

	
	@Override
	public Object login(HttpServletRequest request) {


		System.out.println("in controller.");
		if(request.getMethod().equals("GET")) {
			return "login.html";
		}
		System.out.println("controller, username: "+ (String)request.getParameter("username"));
		Employee logged = new Employee(0, null, null, (String)request.getParameter("username"), (String)request.getParameter("password"), null, null);
		logged = service.authenticate(logged);
		//logger.trace("finished authenticate "+ logged.toString());
		//bad credentials
		if(logged == null)
		{
			logger.trace("bad login");
			return new ClientMessage("Invalid login.");
			//return new ClientMessage("Login failed.");
		}
		request.getSession().setAttribute("loggedUser",logged);
		System.out.println("logged in");
		logger.trace(logged.toString());
		logger.trace(request.getSession().getAttribute(("loggedUser")));
		logger.trace(request.getSession());
		return logged;
	}

	@Override
	public String logout(HttpServletRequest request) {
		
		request.getSession().invalidate();
		
		return "login.html";
	}

}
