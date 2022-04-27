package it.polimi.tiw.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.tinylog.Logger;

import it.polimi.tiw.beans.Image;

public class TestDAO {
	private Connection connection;

	public TestDAO(Connection con) {
		this.connection = con;
	}

	public String findAllUsers() throws SQLException {
		String userNames = "\n";
		
		try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM progettotiw.user");) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					userNames = userNames + result.getString("name") + "\n";
				}
			}
		}
		Logger.debug(userNames);
		return userNames;
	}

	public ArrayList<Image> findAllImages() throws SQLException {
		ArrayList<Image> images = new ArrayList<>();
		try (PreparedStatement pstatement = connection.prepareStatement("SELECT * FROM progettotiw.image");) {
			try (ResultSet result = pstatement.executeQuery();){
				while (result.next()) {
					Image image = new Image();
					image.setId(result.getInt("ID"));
					image.setTitle(result.getString("title"));
					image.setDescription(result.getString("description"));
					image.setPath(result.getString("path"));
					image.setDate(result.getDate("date"));
					images.add(image);
				}
			}
		}
		return images;
	}
	

}
