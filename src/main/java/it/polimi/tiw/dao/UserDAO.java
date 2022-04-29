package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * checks if a user is registered<br>
	 * -	if he isn't returns null<br>
	 * -  if he is returns User bean
	 */
	public User checkCredentials(String email, String password) throws SQLException {
		String query = "SELECT ID, email, name, surname FROM progettotiw.user" + "WHERE password = ? and email = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst())// no result check credentials failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("ID"));
					user.setEmail(result.getString("email"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}
	/**
	 * adds credentials to database
	 */
	public void createCredentials(String email, String name, String surname, String password) throws SQLException {
		String query = "INSERT INTO progettotiw.user (email, name, surname, password) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, name);
			pstatement.setString(3, surname);
			pstatement.setString(4, password);
			pstatement.executeUpdate();
		}
	}
	
}