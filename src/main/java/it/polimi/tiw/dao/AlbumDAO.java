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
	/*
	 * returns albums owned by user
	 */
	public List<Album> findOwnedAlbums(int userID) throws SQLException{
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album" + "WHERE userID = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();){
				pstatement.setInt(1, userID);
				pstatement.executeUpdate();
				
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
	/*
	 * returns albums not owned by user
	 */
	public List<Album> findNotOwnedAlbums(int userID) throws SQLException{
		List<Album> albums = new ArrayList<>();
		String query = "SELECT * FROM progettotiw.album" + "WHERE userID != ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();){
				pstatement.setInt(1, userID);
				pstatement.executeUpdate();
				
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
		
}
