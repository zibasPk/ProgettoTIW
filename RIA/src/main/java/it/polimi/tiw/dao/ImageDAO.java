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
	 * finds five images from a certain album using the given index offset
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
	
	public List<Image> findImagesFromAlbum(int albumID) throws SQLException {
		List<Image> images = new ArrayList<>();
		String query = "SELECT * from progettotiw.image join progettotiw.image_to_album on ID = imageID "
				+ "WHERE albumID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
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
	 * 
	 * @return all images of a certain user that were in no album
	 * @throws SQLException
	 */
	public List<Image> findFreeImages(int userId) throws SQLException {
		List<Image> images = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.image\r\n"
				+ "WHERE image.uploaderID = ? and image.ID NOT IN (select imageID from image_to_album)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return null;
				while (result.next()) {
					Image image = new Image();
					image.setId(result.getInt("ID"));
					image.setTitle(result.getString("title"));
					image.setDescription(result.getString("description"));
					image.setPath(result.getString("path"));
					image.setDate(result.getDate("date"));
					images.add(image);
				}
				return images;
			}
		}
	}
	
	/**
	 * Creates a new row in the image_to_album table adding an image to an album
	 */
	public void addImageToAlbum(int imageId, int albumId) throws SQLException {
		String query = "INSERT INTO progettotiw.image_to_album (imageID, albumID) VALUES (?, ?))";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, imageId);
			pstatement.setInt(2, albumId);
			pstatement.executeUpdate();
		}
	}
}


