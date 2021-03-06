package com.revature.service;

import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementType;
import com.revature.repository.EmployeeRepositoryJdbc;
import com.revature.repository.ReimbursementRepository;
import com.revature.repository.ReimbursementRepositoryJdbc;

public class ReimbursementServices implements ReimbursementService {

	private ReimbursementRepository repository = ReimbursementRepositoryJdbc.getInstance();
	

	private static final Logger logger = Logger.getLogger(ReimbursementServices.class);
	
	private static ReimbursementService service = new ReimbursementServices();
	
	private ReimbursementServices() {
		
	}
	
	
	public static ReimbursementService getInstance() {
		return service;
	}
	
	
	@Override
	public boolean submitRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		logger.trace("service layer: submit request");
		return repository.insert(reimbursement);
	}

	
	//approve/deny request
	@Override
	public boolean finalizeRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return repository.update(reimbursement);
	}

	@Override
	public Reimbursement getSingleRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return repository.select(reimbursement.getId());
	}

	@Override
	public Set<Reimbursement> getUserPendingRequests(Employee employee) {
		return repository.selectPending(employee.getId());
	}

	@Override
	public Set<Reimbursement> getUserFinalizedRequests(Employee employee) {
		// TODO Auto-generated method stub
		return repository.selectFinalized(employee.getId());
	}

	@Override
	public Set<Reimbursement> getAllPendingRequests() {
		// TODO Auto-generated method stub
		return repository.selectAllPending();
	}

	@Override
	public Set<Reimbursement> getAllResolvedRequests() {
		// TODO Auto-generated method stub
		return repository.selectAllFinalized();
	}

	@Override
	public Set<ReimbursementType> getReimbursementTypes() {
		// TODO Auto-generated method stub
		return repository.selectTypes();
	}

}
