package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.repository.EmployeeRepositoryJdbc;
import com.revature.service.ReimbursementService;
import com.revature.service.ReimbursementServices;

public class ReimbursementControllerAlpha implements ReimbursementController {

	private ReimbursementService service = ReimbursementServices.getInstance();

	private static final Logger logger = Logger.getLogger(ReimbursementServices.class);
	
	
	
	@Override
	public Object submitRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		logger.trace("Controller: submitRequest");
		logger.trace(request.getSession());
		logger.trace(request.getSession().getAttribute(("loggedUser")));
		Employee loggedUser = (Employee) request.getSession().getAttribute("loggedUser");
		logger.trace("sessionID: " + request.getHeader("JESSIONID"));
		//logger.trace(request.getCookies().length);
		if(loggedUser == null) {
			logger.trace("not logged in.");
			return new ClientMessage("please login");
		}
		logger.trace("in controller logged: " + loggedUser );
		Reimbursement reimbursement = new Reimbursement(0, 
				null, null, 
				(double) Double.parseDouble(request.getParameter("amount")), 
				request.getParameter("description"), loggedUser, 
				null, (new ReimbursementStatus(1, "PENDING")), 
				(new ReimbursementType(1,request.getParameter("type")))
				);
		logger.trace(reimbursement);
		
		if (service.submitRequest(reimbursement))
		{
			return new ClientMessage("Request Submitted.");
		}
		
		//create reimbursement based on input from angular?
		
		return new ClientMessage("Submition failed.");
	}

	@Override
	public Object singleRequest(HttpServletRequest request) {
		// TODO 
		Reimbursement req = new Reimbursement(
				Integer.parseInt(request.getParameter("id")),
				null, null, 0.0, "", null, null, null, null);
		return service.getSingleRequest(req);
	}

	@Override
	public Object multipleRequests(HttpServletRequest request) {
		// TODO request.param("requestType") to decide which type to get?
		Employee loggedUser = (Employee) request.getSession().getAttribute("loggedUser");
		Employee searchUser;
		if (request.getParameter("requestType").equals("pending")){
			logger.trace("requesting pending reimbursements");
			return service.getUserPendingRequests(loggedUser);
		} else if (request.getParameter("requestType").equals("finalized")){
			logger.trace("requesting finalized reimbursements");
			return service.getUserFinalizedRequests(loggedUser);
		} else if (request.getParameter("requestType").equals("allPending")) {
			logger.trace("requesting ALL pending reimbursements");
			return service.getAllPendingRequests();
		} else if (request.getParameter("requestType").equals("allResolved")) {
			logger.trace("requesting ALL resolved reimbursements");
			return service.getAllResolvedRequests();
		} else if (request.getParameter("requestType").equals("searchPending")) {
			searchUser=new Employee(
					Integer.parseInt(request.getParameter("id")),
					null,null,null,null,null,null);
			logger.trace("searching pending");
			return service.getUserPendingRequests(searchUser);
		} else if (request.getParameter("requestType").equals("searchFinalized")) {
			searchUser=new Employee(
					Integer.parseInt(request.getParameter("id")),
					null,null,null,null,null,null);
			logger.trace("searching finalized");
			return service.getUserFinalizedRequests(searchUser);
		}else {
			//select request by user ?
		return null;
		}
	}

	@Override
	public Object finalizeRequest(HttpServletRequest request) {
		Reimbursement reimbursement = service.getSingleRequest(
						new Reimbursement(
								Integer.parseInt(request.getParameter("id")),
								null, null, 0.0, "", null, null, null, null));
		reimbursement.setApprover((Employee)request.getSession()
				.getAttribute("loggedUser"));
		reimbursement.setStatus(new ReimbursementStatus(
							Integer.parseInt(request.getParameter("statusNum")),
									request.getParameter("status")));
		logger.trace("controller updated reimbursement: " + reimbursement);
		return service.finalizeRequest(reimbursement);
	}

	@Override
	public Object getRequestTypes(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
