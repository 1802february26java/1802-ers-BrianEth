package com.revature.service;

import java.util.Set;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementType;
import com.revature.repository.ReimbursementRepository;
import com.revature.repository.ReimbursementRepositoryJdbc;

public class ReimbursementServices implements ReimbursementService {

	ReimbursementRepository repository = ReimbursementRepositoryJdbc.getInstance();
	
	@Override
	public boolean submitRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return repository.insert(reimbursement);
	}

	@Override
	public boolean finalizeRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Reimbursement getSingleRequest(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return repository.select(reimbursement.getId());
	}

	@Override
	public Set<Reimbursement> getUserPendingRequests(Employee employee) {
		// TODO Auto-generated method stub
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
