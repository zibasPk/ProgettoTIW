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
	public List<Image> findFiveImagesFromAlbum(int albumID, int limit, int offset) throws SQLException {
		List<Image> images = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.image join progettotiw.image_to_album ON ID = imageID"
				+ " WHERE albumID = ?" + " LIMIT ? offset ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			pstatement.setInt(2, limit);
			pstatement.setInt(3, offset);
			try (ResultSet result = pstatement.executeQuery();) {
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
	
	/**
	 * Returns the amount of images in the album with the given ID
	 * @throws SQLException
	 */
	public Integer findAlbumSize(int albumID) throws SQLException {
		String query = "SELECT count(*) FROM progettotiw.image join progettotiw.image_to_album ON ID = imageID"
				+ " WHERE albumID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return null;
				result.next();
				return result.getInt(1);
			}
		}
	}
	
	/**
	 * Finds a specific image from a given ID
	 * @throws SQLException
	 */
	public Image findImage(int imageID) throws SQLException {
		String query = "SELECT * FROM progettotiw.image WHERE ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, imageID);
			try (ResultSet result = pstatement.executeQuery();) {
				Image image = new Image();
				if (!result.isBeforeFirst())// no results
					return null;
				while (result.next()) {
					image.setId(result.getInt("ID"));
					image.setTitle(result.getString("title"));
					image.setDescription(result.getString("description"));
					image.setPath(result.getString("path"));
					image.setDate(result.getDate("date"));
				}
				return image;
			}
		}
	}

}
