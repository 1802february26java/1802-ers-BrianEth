package com.revature.request;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.controller.EmployeeInformationControllerAlpha;
import com.revature.controller.ErrorControllerAlpha;
import com.revature.controller.LoginControllerAlpha;
import com.revature.controller.ReimbursementControllerAlpha;
import com.revature.service.ReimbursementServices;

/**
 * The RequestHelper class is consulted by the MasterServlet and provides
 * him with a view URL or actual data that needs to be transferred to the
 * client.
 * 
 * It will execute a controller method depending on the requested URI.
 * 
 * Recommended to change this logic to consume a ControllerFactory.
 * 
 * @author Revature LLC
 */
public class RequestHelper {
	private static RequestHelper requestHelper;
	private static final Logger logger = Logger.getLogger(RequestHelper.class);
	
	private RequestHelper() {}

	public static RequestHelper getRequestHelper() {
		if(requestHelper == null) {
			return new RequestHelper();
		}
		else {
			return requestHelper;
		}
	}
	
	/**
	 * Checks the URI within the request object passed by the MasterServlet
	 * and executes the right controller with a switch statement.
	 * 
	 * @param request
	 * 		  The request object which contains the solicited URI.
	 * @return A String containing the URI where the user should be
	 * forwarded, or data (any object) for AJAX requests.
	 */
	public Object process(HttpServletRequest request) {
		//logger.trace("helper before trace");
		switch(request.getRequestURI())
		{
		case "/ERS/login.do":
			return new LoginControllerAlpha().login(request);
		case "/ERS/logout.do":
			return new LoginControllerAlpha().logout(request);
		case "/ERS/submitReimbursement.do":
			logger.trace("HELPER: submit reimbursement.");
			return new ReimbursementControllerAlpha().submitRequest(request);
		//TODO: make functional, see controllers??
		case "/ERS/info.do":
			logger.trace("info");
			return new EmployeeInformationControllerAlpha().viewEmployeeInformation(request);
		case "/ERS/update.do":
			//TODO: test route
			logger.trace("Helper: updating info");
			return new EmployeeInformationControllerAlpha().updateEmployee(request);
		case "/ERS/debugReimbursement.do":
			return new ReimbursementControllerAlpha().singleRequest(request);
		case "/ERS/resolvedReimbursement.do":
			return new ReimbursementControllerAlpha();
		case "/ERS/history.do":
			return new ReimbursementControllerAlpha().multipleRequests(request);
		default:
			logger.trace("not caught in switch statement");
			return new ErrorControllerAlpha().showError(request);
		}
	}
}
