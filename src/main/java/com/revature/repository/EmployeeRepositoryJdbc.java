package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.EmployeeToken;
import com.revature.util.ConnectionUtil;

public class EmployeeRepositoryJdbc implements EmployeeRepository {

	private static final Logger logger = Logger.getLogger(EmployeeRepositoryJdbc.class);

	private static EmployeeRepositoryJdbc repository = new EmployeeRepositoryJdbc();

	private EmployeeRepositoryJdbc()
	{
	}

	public static EmployeeRepositoryJdbc getInstance()
	{
		return repository;
	}

	@Override
	public boolean insert(Employee employee)
	{
		try (Connection connection = ConnectionUtil.getConnection())
		{
			final String command = "INSERT INTO USER_T VALUES(NULL,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(command);
			int statementIndex = 0;

			statement.setString(++statementIndex, employee.getFirstName().toUpperCase());
			statement.setString(++statementIndex, employee.getLastName().toUpperCase());
			statement.setString(++statementIndex, employee.getUsername().toLowerCase());
			statement.setString(++statementIndex, employee.getPassword());
			statement.setString(++statementIndex, employee.getEmail().toLowerCase());
			statement.setInt(++statementIndex, employee.getEmployeeRole().getId());

			if ( statement.executeUpdate() != 0 )
			{
				logger.trace("inserted");
				return true;
			}
			else
			{
				return false;
			}

		}
		catch (SQLException e)
		{
			logger.error("user/employee insertion failed", e);
		}
		return false;
	}

	@Override
	public boolean update(Employee employee)
	{
		try (Connection connection = ConnectionUtil.getConnection())
		{
			final String command = "UPDATE USER_T SET U_FIRSTNAME = ?, U_LASTNAME = ? , U_PASSWORD = ?, U_EMAIL = ?, UR_ID = ? WHERE U_ID =  ?";
			PreparedStatement statement = connection.prepareStatement(command);
			int statementIndex = 0;

			statement.setString(++statementIndex, employee.getFirstName().toUpperCase());
			statement.setString(++statementIndex, employee.getLastName().toUpperCase());
			statement.setString(++statementIndex, employee.getPassword());
			statement.setString(++statementIndex, employee.getEmail().toLowerCase());
			statement.setInt(++statementIndex, employee.getEmployeeRole().getId());
			statement.setInt(++statementIndex, employee.getId());

			if ( statement.executeUpdate() != 0 )
			{
				logger.trace("Updated employee");
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			logger.error("Employee failed to update", e);
		}
		return false;
	}

	@Override
	public Employee select(int employeeId)
	{
		try (Connection connection = ConnectionUtil.getConnection())
		{
			final String command = "SELECT U.U_ID, U.U_FIRSTNAME, U.U_LASTNAME, U.U_USERNAME, U.U_PASSWORD, U.U_EMAIL, U.UR_ID, UR.UR_TYPE FROM USER_T U INNER JOIN USER_ROLE UR ON U.UR_ID = UR.UR_ID WHERE U.U_ID = ?";
			PreparedStatement statement = connection.prepareStatement(command);

			statement.setInt(1, employeeId);

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next())
			{
				Employee employee = new Employee();

				employee.setId(resultSet.getInt("U_ID"));
				employee.setFirstName(resultSet.getString("U_FIRSTNAME"));
				employee.setLastName(resultSet.getString("U_LASTNAME"));
				employee.setUsername(resultSet.getString("U_USERNAME"));
				employee.setPassword(resultSet.getString("U_PASSWORD"));
				employee.setEmail(resultSet.getString("U_EMAIL"));
				employee.setEmployeeRole(new EmployeeRole(resultSet.getInt("UR_ID"), resultSet.getString("UR_TYPE")));

				return employee;
			}
		}
		catch (SQLException e)
		{
			logger.error("Specified Employee not found (ID)", e);
		}
		return new Employee();
	}

	@Override
	public Employee select(String username)
	{
		try (Connection connection = ConnectionUtil.getConnection())
		{
			final String command = "SELECT U.U_ID, U.U_FIRSTNAME, U.U_LASTNAME, U.U_USERNAME, U.U_PASSWORD, U.U_EMAIL, U.UR_ID, UR.UR_TYPE FROM USER_T U INNER JOIN USER_ROLE UR ON U.UR_ID = UR.UR_ID WHERE U.U_USERNAME = ?";
			PreparedStatement statement = connection.prepareStatement(command);

			statement.setString(1, username);

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next())
			{
				Employee employee = new Employee();

				employee.setId(resultSet.getInt("U_ID"));
				employee.setFirstName(resultSet.getString("U_FIRSTNAME"));
				employee.setLastName(resultSet.getString("U_LASTNAME"));
				employee.setUsername(resultSet.getString("U_USERNAME"));
				employee.setPassword(resultSet.getString("U_PASSWORD"));
				employee.setEmail(resultSet.getString("U_EMAIL"));
				employee.setEmployeeRole(new EmployeeRole(resultSet.getInt("UR_ID"), resultSet.getString("UR_TYPE")));

				return employee;
			}
		}
		catch (SQLException e)
		{
			logger.error("Specified Employee not found (username)", e);
		}
		return new Employee();
	}

	@Override
	public Set<Employee> selectAll()
	{
		Set<Employee> employees = new HashSet<>();

		try (Connection connection = ConnectionUtil.getConnection())
		{
			final String command = "SELECT U.U_ID, U.U_FIRSTNAME, U.U_LASTNAME, U.U_USERNAME, U.U_PASSWORD, U.U_EMAIL, U.UR_ID, UR.UR_TYPE FROM USER_T U INNER JOIN USER_ROLE UR ON U.UR_ID = UR.UR_ID";
			PreparedStatement statement = connection.prepareStatement(command);

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next())
			{
				Employee employee = new Employee();

				employee.setId(resultSet.getInt("U_ID"));
				employee.setFirstName(resultSet.getString("U_FIRSTNAME"));
				employee.setLastName(resultSet.getString("U_LASTNAME"));
				employee.setUsername(resultSet.getString("U_USERNAME"));
				employee.setPassword(resultSet.getString("U_PASSWORD"));
				employee.setEmail(resultSet.getString("U_EMAIL"));
				employee.setEmployeeRole(new EmployeeRole(resultSet.getInt("UR_ID"), resultSet.getString("UR_TYPE")));

				employees.add(employee);
			}
		}
		catch (SQLException e)
		{
			logger.error("Select all employees failed.", e);
		}
		return employees;
	}

	@Override
	public String getPasswordHash(Employee employee)
	{
		try(Connection connection = ConnectionUtil.getConnection()) {

			int statementIndex = 0;

			String command = "SELECT GET_HASH(?) AS HASH FROM DUAL";

			PreparedStatement statement = connection.prepareStatement(command);

			statement.setString(++statementIndex, employee.getPassword());

			ResultSet result = statement.executeQuery();



			if(result.next()) {

				return result.getString("HASH");

			}

		} catch (SQLException e) {

			logger.warn("Exception getting customer hash", e);

		} 

		return new String();	
	}

	@Override
	public boolean insertEmployeeToken(EmployeeToken employeeToken)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteEmployeeToken(EmployeeToken employeeToken)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EmployeeToken selectEmployeeToken(EmployeeToken employeeToken)
	{
		// TODO Auto-generated method stub
		return null;
	}
}

