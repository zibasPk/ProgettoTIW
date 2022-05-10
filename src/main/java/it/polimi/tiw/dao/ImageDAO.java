package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Image;

public class ImageDAO {
	private Connection connection;
	
	public ImageDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * finds images from a certain album
	 */
	public List<Image> findImagesFromAlbum(int albumID) throws SQLException{
		List<Image> images = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.image join progettotiw.image_to_album " + "ON ID = imageID WHERE albumID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
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
