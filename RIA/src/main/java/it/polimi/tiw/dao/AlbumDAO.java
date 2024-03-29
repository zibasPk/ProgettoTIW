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
	 * @param albumID
	 * @return album with the given ID, if there isn't one null
	 * @throws SQLException
	 */
	public Album findAlbum(Integer albumID) throws SQLException {
		Album album = null;
		String query = "SELECT * FROM progettotiw.album WHERE ID = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return album;

				result.next();
				album = new Album();
				album.setID(result.getInt("ID"));
				album.setTitle(result.getString("title"));
				album.setOwnerID(result.getInt("ownerID"));
				album.setCreationDate(result.getDate("creation_date"));
			}
		}
		return album;
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
	 * @return albums not owned by user ordered in descending order by date
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
	 * @return the album containing the image with the given imageId, if there
	 *         aren't any results it returns null
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
	 * deletes old order and inserts the given album order into the DB for a certain user.
	 */
	public void saveAlbumOrder(int userId, List<Integer> albumOrder) throws SQLException {
		connection.setAutoCommit(false);
		String query = "DELETE FROM progettotiw.album_order WHERE userID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		query = "INSERT INTO progettotiw.album_order (userID, albumID, position) VALUES (?, ?, ?)";
		for (Integer albumId : albumOrder) {
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, userId);
				pstatement.setInt(2, albumId);
				pstatement.setInt(3, albumOrder.indexOf(albumId));
				pstatement.executeUpdate();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
		}
		connection.commit();
	}

	/**
	 * Inserts an album in a given position
	 * 
	 * @throws SQLException
	 */
	public void addToAlbumOrder(int userId, int albumId, int position) throws SQLException {
		String query = "INSERT INTO progettotiw.album_order (userID, albumID, position) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, albumId);
			pstatement.setInt(3, position);
			pstatement.executeUpdate();
		}
	}

	/**
	 * @param userId
	 * @return saved order of albums, if there is none returns null
	 * @throws SQLException
	 */
	public List<Integer> getAlbumOrder(int userId) throws SQLException {
		List<Integer> order = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album_order WHERE userID = ? order by position ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst())// no results
					return null;
				while (result.next()) {
					order.add(result.getInt("albumID"));
				}
			}
		}
		return order;
	}

	/**
	 * Deletes the order entry for a certain user
	 * 
	 * @param userId
	 * @throws SQLException
	 */
	public void deleteAlbumOrder(int userId) throws SQLException {
		String query = "DELETE FROM progettotiw.album_order WHERE userID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.executeUpdate();
		}
	}

	/**
	 * Creates an album in the DB
	 * 
	 * @param ownerId
	 * @param albumName
	 * @throws SQLException
	 */
	public void createAlbum(int ownerId, String albumName) throws SQLException {
		String query = "INSERT INTO progettotiw.album (ownerID, title, creation_date) VALUES (?, ?, CURRENT_DATE())";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, ownerId);
			pstatement.setString(2, albumName);
			pstatement.executeUpdate();
		}
	}
}
