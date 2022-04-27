package it.polimi.tiw.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.tinylog.Logger;

public class TestDAO {
	private Connection connection;

	public TestDAO(Connection con) {
		this.connection = con;
	}

	public void findAllUsers() throws SQLException {
		String userNames = "";
		
		try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM progettotiw.user");) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					userNames = userNames + result.getString("name") + "\n";
				}
			}
		}
		Logger.debug(userNames);
	}

}
