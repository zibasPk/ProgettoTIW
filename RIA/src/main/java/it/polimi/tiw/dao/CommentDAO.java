package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.tiw.beans.Comment;

public class CommentDAO {
	private Connection connection;
	
	public CommentDAO(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * adds a new comment to the database
	 * 
	 */
	public void createComment(int imageID, int userID, String text) throws SQLException {
		String query = "INSERT INTO progettotiw.comment (userID, imageID, text) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			pstatement.setInt(2, imageID);
			pstatement.setString(3, text);
			pstatement.executeUpdate();
		}
	}
	/**
	 * find comments for given image id, if there are non it returns empty list
	 * 
	 */
	public List<Comment> findCommentsForImage(int imageID) throws SQLException {
		String query = "SELECT ID, userID, imageID, text FROM progettotiw.comment " + "WHERE imageID = ?";
		List<Comment> comments = new ArrayList<>();
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, imageID);
			
			try (ResultSet result = pstatement.executeQuery();){
				while(result.next()) {
					Comment comment = new Comment();
					comment.setID(result.getInt("ID"));
					comment.setUserID(result.getInt("userID"));
					comment.setImageID(result.getInt("imageID"));
					comment.setText(result.getString("text"));
					comments.add(comment);
				}
			}
		}
		return comments;
	}

}
