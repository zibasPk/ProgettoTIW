package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Album;

public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @param userID
	 * @return albums owned by user
	 * @throws SQLException
	 */
	public List<Album> findOwnedAlbums(int userID) throws SQLException {
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album WHERE ownerID = ? order by creation_date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album.setID(result.getInt("ID"));
					album.setTitle(result.getString("title"));
					album.setOwnerID(result.getInt("ownerID"));
					album.setCreationDate(result.getDate("creation_date"));
					albums.add(album);
				}
			}
		}
		return albums;
	}

	public int findAlbumImageCount(int albumID) throws SQLException {
		String query = "SELECT count(*) as count FROM progettotiw.image join progettotiw.image_to_album ON ID = imageID"
				+ " WHERE albumID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					return result.getInt("count");
				}
				return 0;
			}
		}
	}

	/**
	 * @param userID
	 * @return albums not owned by user
	 * @throws SQLException
	 */
	public List<Album> findNotOwnedAlbums(int userID) throws SQLException {
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album WHERE ownerID != ? order by creation_date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album.setID(result.getInt("ID"));
					album.setTitle(result.getString("title"));
					album.setOwnerID(result.getInt("ownerID"));
					album.setCreationDate(result.getDate("creation_date"));
					albums.add(album);
				}
			}
		}
		return albums;
	}

	/**
	 * 
	 * @return true if album with albumID exists in database
	 */
	public boolean validAlbum(int albumID) throws SQLException {
		String query = "SELECT * FROM progettotiw.album WHERE ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return false;
				return true;
			}
		}
	}
	/**
	 * 
	 * @return the album containing the image with the given imageId, if there aren't any results it returns null
	 * @throws SQLException
	 */
	public Integer findImageAlbumID(int imageID) throws SQLException {
		String query = "SELECT albumID FROM progettotiw.image_to_album WHERE imageID = ?";
		Integer albumID = null;
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, imageID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return albumID;
				while (result.next()) {
					albumID = result.getInt("albumID");
				}
			}
			return albumID;
		}
	}
	
	/**
	 * Creates an album in the DB
	 * @param ownerId
	 * @param albumName
	 * @throws SQLException
	 */
	public void createAlbum(int ownerId,String albumName) throws SQLException {
		String query = "INSERT INTO progettotiw.album (ownerID, title, creation_date) VALUES (?, ?, CURRENT_DATE())";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, ownerId);
			pstatement.setString(2, albumName);
			pstatement.executeUpdate();
		}
	}
}
