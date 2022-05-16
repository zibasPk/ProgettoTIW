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
	public List<Album> findOwnedAlbums(int userID) throws SQLException{
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album WHERE ownerID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();){				
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
	 * @param userID
	 * @return albums not owned by user
	 * @throws SQLException
	 */
	public List<Album> findNotOwnedAlbums(int userID) throws SQLException{
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album WHERE ownerID != ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();){
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
	public boolean validAlbum(int albumID) throws SQLException{
		String query = "SELECT * FROM progettotiw.album WHERE ID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();){
				if(!result.isBeforeFirst())// no results
					return false;
				return true;
			}
		}
	}
		
}
