package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.util.ConnectionUtil;


//table: reimbursement
//R_ID          NOT NULL NUMBER         
//R_REQUESTED   NOT NULL TIMESTAMP(6)   
//R_RESOLVED             TIMESTAMP(6)   
//R_AMOUNT      NOT NULL NUMBER(8,2)    
//R_DESCRIPTION          VARCHAR2(4000) 
//R_RECEIPT              BLOB           
//EMPLOYEE_ID   NOT NULL NUMBER         
//MANAGER_ID             NUMBER         
//RS_ID         NOT NULL NUMBER         
//RT_ID         NOT NULL NUMBER   
//

public class ReimbursementRepositoryJdbc implements ReimbursementRepository {


	private static Logger logger = Logger.getLogger(ReimbursementRepositoryJdbc.class);

	@Override
	public boolean insert(Reimbursement reimbursement) {
		try(Connection connection = ConnectionUtil.getConnection()) {
			int statementIndex = 0;
			String command = "INSERT INTO REIMBURSEMENT VALUES(NULL,?,?,?,?,NULL,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(command);

			//Set attributes to be inserted
			statement.setTimestamp(++statementIndex, Timestamp.valueOf(reimbursement.getRequested()));
			statement.setTimestamp(++statementIndex, Timestamp.valueOf(reimbursement.getResolved()));
			statement.setDouble(++statementIndex, reimbursement.getAmount());
			statement.setString(++statementIndex, reimbursement.getDescription());
			//statement.setBlob(++statementIndex, (Blob)reimbursement.getReceipt());
			statement.setInt(++statementIndex, reimbursement.getRequester().getId());
			statement.setInt(++statementIndex, reimbursement.getApprover().getId());
			statement.setInt(++statementIndex, reimbursement.getStatus().getId());
			statement.setInt(++statementIndex, reimbursement.getType().getId());

			if(statement.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.warn("Exception creating a new employee", e);
		}
		return false;
	}

	@Override
	public boolean update(Reimbursement reimbursement) {
		try(Connection connection = ConnectionUtil.getConnection()){
			int statementIndex = 0;

			String command="UPDATE REIMBURSEMENT SET MANAGER_ID = ?, RS_ID = ?   WHERE R_ID = ?";

			logger.trace("getting statement object in update account");

			PreparedStatement statement = connection.prepareStatement(command);
			//Set attributes to be inserted
			//statement.setTimestamp(++statementIndex, Timestamp.valueOf(reimbursement.getRequested()));
			//statement.setTimestamp(++statementIndex, Timestamp.valueOf(reimbursement.getResolved()));
			//statement.setDouble(++statementIndex, reimbursement.getAmount());
			//statement.setString(++statementIndex, reimbursement.getDescription());
			//statement.setBlob(++statementIndex, (Blob)reimbursement.getReceipt());
			//statement.setInt(++statementIndex, reimbursement.getRequester().getId());

			//change to get logged user id.
			statement.setInt(++statementIndex, reimbursement.getApprover().getId());

			//change to enum/static of status value
			statement.setInt(++statementIndex, reimbursement.getStatus().getId());
			//statement.setInt(++statementIndex, reimbursement.getType().getId());
			logger.trace("parameters for update of account set");
			//System.out.println(statement.toString());


			if(statement.executeUpdate() > 0){
				logger.trace("account updated succefully");
				return true;
			}
		} catch (SQLException e) {
			logger.error("exception thrown while updating ", e);
		}


		return false;
	}

	@Override
	public Reimbursement select(int reimbursementId) {
		logger.trace("Selecting reimbursement");
		try (Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			String command = "SELECT * FROM REIMBURSEMENT R INNER JOIN REIMBURSEMENT_STATUS RS ON R.RS_ID = RS.RS_ID "
					+ "INNER JOIN REIMBURSEMENT_TYPE RT ON R.RT_ID = RT.RT_ID WHERE R.R_ID = ?";  

			PreparedStatement statement = connection.prepareStatement(command);
			statement.setInt(++parameterIndex, reimbursementId);

			ResultSet result = statement.executeQuery();

			if (result.next()) {
				Reimbursement reimbursement = new Reimbursement(
						result.getInt("R_ID"),
						result.getTimestamp("R_REQUESTED").toLocalDateTime(),
						null,
						result.getDouble("R_AMOUNT"),
						result.getString("R_DESCRIPTION"),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("EMPLOYEE_ID")),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("MANAGER_ID")),
						new ReimbursementStatus(
								result.getInt("RS_ID"),
								result.getString("RS_STATUS")
								),
						new ReimbursementType(
								result.getInt("RT_ID"),
								result.getString("RT_TYPE")
								)
						);
				if (result.getString("R_RESOLVED") != null) {
					reimbursement.setResolved(result.getTimestamp("R_RESOLVED").toLocalDateTime());
				}
				return reimbursement;
			}
		} catch (SQLException e) {
			logger.error("Exception thrown while selecting reimbursement", e);
		}
		return null;
	}

	@Override
	public Set<Reimbursement> selectPending(int employeeId) {
		logger.trace("Selecting pending reimbursement");
		try (Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			String command = "SELECT * FROM REIMBURSEMENT R INNER JOIN REIMBURSEMENT_STATUS RS ON R.RS_ID = RS.RS_ID "
					+ "INNER JOIN REIMBURSEMENT_TYPE RT ON R.RT_ID = RT.RT_ID WHERE R.EMPLOYEE_ID = ? AND R.RS_ID = ?";

			PreparedStatement statement = connection.prepareStatement(command);
			statement.setInt(++parameterIndex, employeeId);
			statement.setInt(++parameterIndex, 1);

			ResultSet result = statement.executeQuery();
			Set<Reimbursement> reimbursements = new HashSet<>();

			while (result.next()) {
				reimbursements.add(new Reimbursement(
						result.getInt("R_ID"),
						result.getTimestamp("R_REQUESTED").toLocalDateTime(),
						null,
						result.getDouble("R_AMOUNT"),
						result.getString("R_DESCRIPTION"),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("EMPLOYEE_ID")),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("MANAGER_ID")),
						new ReimbursementStatus(
								result.getInt("RS_ID"),
								result.getString("RS_STATUS")
								),
						new ReimbursementType(
								result.getInt("RT_ID"),
								result.getString("RT_TYPE")
								)
						));
			}
			return reimbursements;

		} catch (SQLException e) {
			logger.error("Exception thrown while selecting pending reimbursement", e);
		}
		return null;
	}

	@Override
	public Set<Reimbursement> selectFinalized(int employeeId) {
		logger.trace("Selecting finalized reimbursement");
		try (Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			String command = "SELECT * FROM REIMBURSEMENT R INNER JOIN REIMBURSEMENT_STATUS RS ON R.RS_ID = RS.RS_ID "
					+ "INNER JOIN REIMBURSEMENT_TYPE RT ON R.RT_ID = RT.RT_ID WHERE R.EMPLOYEE_ID = ? AND (R.RS_ID = ? OR R.RS_ID = ?)";

			PreparedStatement statement = connection.prepareStatement(command);
			statement.setInt(++parameterIndex, employeeId);
			statement.setInt(++parameterIndex, 2);
			statement.setInt(++parameterIndex, 3);

			ResultSet result = statement.executeQuery();
			Set<Reimbursement> reimbursements = new HashSet<>();

			while (result.next()) {
				reimbursements.add(new Reimbursement(
						result.getInt("R_ID"),
						result.getTimestamp("R_REQUESTED").toLocalDateTime(),
						null,
						result.getDouble("R_AMOUNT"),
						result.getString("R_DESCRIPTION"),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("EMPLOYEE_ID")),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("MANAGER_ID")),
						new ReimbursementStatus(
								result.getInt("RS_ID"),
								result.getString("RS_STATUS")
								),
						new ReimbursementType(
								result.getInt("RT_ID"),
								result.getString("RT_TYPE")
								)
						));
			}
			return reimbursements;
		} catch (SQLException e) {
			logger.error("Exception thrown while selecting finalized reimbursement", e);
		}
		return null;
	}

	@Override
	public Set<Reimbursement> selectAllPending() {
		logger.trace("Selecting all pending reimbursement");
		try (Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			String command = "SELECT * FROM REIMBURSEMENT R INNER JOIN REIMBURSEMENT_STATUS RS ON R.RS_ID = RS.RS_ID "
					+ "INNER JOIN REIMBURSEMENT_TYPE RT ON R.RT_ID = RT.RT_ID WHERE R.RS_ID = ?";

			PreparedStatement statement = connection.prepareStatement(command);
			statement.setInt(++parameterIndex, 1);

			ResultSet result = statement.executeQuery();
			Set<Reimbursement> reimbursements = new HashSet<>();

			while (result.next()) {
				reimbursements.add(new Reimbursement(
						result.getInt("R_ID"),
						result.getTimestamp("R_REQUESTED").toLocalDateTime(),
						null,
						result.getDouble("R_AMOUNT"),
						result.getString("R_DESCRIPTION"),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("EMPLOYEE_ID")),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("MANAGER_ID")),
						new ReimbursementStatus(
								result.getInt("RS_ID"),
								result.getString("RS_STATUS")
								),
						new ReimbursementType(
								result.getInt("RT_ID"),
								result.getString("RT_TYPE")
								)
						));
			}
			return reimbursements;
		} catch (SQLException e) {
			logger.error("Exception thrown while selecting pending reimbursement", e);
		}
		return null;
	}

	@Override
	public Set<Reimbursement> selectAllFinalized() {
		logger.trace("Selecting all finalized reimbursement");
		try (Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			String command = "SELECT * FROM REIMBURSEMENT R INNER JOIN REIMBURSEMENT_STATUS RS ON R.RS_ID = RS.RS_ID "
					+ "INNER JOIN REIMBURSEMENT_TYPE RT ON R.RT_ID = RT.RT_ID WHERE R.RS_ID = ? OR R.RS_ID = ?";

			PreparedStatement statement = connection.prepareStatement(command);
			statement.setInt(++parameterIndex, 2);
			statement.setInt(++parameterIndex, 3);

			ResultSet result = statement.executeQuery();
			Set<Reimbursement> reimbursements = new HashSet<>();

			while (result.next()) {
				reimbursements.add(new Reimbursement(
						result.getInt("R_ID"),
						result.getTimestamp("R_REQUESTED").toLocalDateTime(),
						null,
						result.getDouble("R_AMOUNT"),
						result.getString("R_DESCRIPTION"),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("EMPLOYEE_ID")),
						EmployeeRepositoryJdbc.getInstance().select(result.getInt("MANAGER_ID")),
						new ReimbursementStatus(
								result.getInt("RS_ID"),
								result.getString("RS_STATUS")
								),
						new ReimbursementType(
								result.getInt("RT_ID"),
								result.getString("RT_TYPE")
								)
						));
			}
			return reimbursements;
		} catch (SQLException e) {
			logger.error("Exception thrown while selecting finalized reimbursement", e);
		}
		return null;
	}

	@Override
	public Set<ReimbursementType> selectTypes() {
		logger.trace("Selecting types");
		try (Connection connection = ConnectionUtil.getConnection()) {
			String command = "SELECT * FROM REIMBURSEMENT_TYPE";

			PreparedStatement statement = connection.prepareStatement(command);

			ResultSet result = statement.executeQuery();
			Set<ReimbursementType> types = new HashSet<>();

			while (result.next()) {
				types.add(new ReimbursementType(
						result.getInt("RT_ID"),
						result.getString("RT_TYPE")
						));
			}
			return types;
		} catch (SQLException e) {
			logger.error("Exception thrown while selecting types", e);
		}
		return null;
	}
}
