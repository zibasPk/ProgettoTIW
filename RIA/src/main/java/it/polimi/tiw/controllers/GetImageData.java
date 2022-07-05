package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.UserDAO;


@WebServlet("/GetImageData")
public class GetImageData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer imageId = null;
		
		// gets image param and tries to parse it
		try {
			imageId = Integer.parseInt(req.getParameter("imageId"));
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Incorrect param values");
			return;
		}
		
		// tries retrieving image from DB
		Image albumImage = null;
		ImageDAO imageService = new ImageDAO(connection);
		try {
			albumImage = imageService.findImage(imageId);
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving image from databse");
			return;
		}
		
		// makes a list of comments and a list of their authors names
		CommentDAO commentService = new CommentDAO(connection);
		UserDAO userService = new UserDAO(connection);
		List<Comment> comments = new ArrayList<>();
		List<String> authors = new ArrayList<>();
		try {
            albumImage = imageService.findImage(imageId);
            commentService.findCommentsForImage(imageId).forEach(comment -> {
                try {
                	comments.add(comment);
                	authors.add(userService.getUserFromID(comment.getUserID()).getFullName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
        	e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving comments from databse");
			return;
        }
		
		// sends back to client the image and both lists in json format
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String albumImageJson = gson.toJson(albumImage);
		String commentsJson = gson.toJson(comments);
		String authorsJson = gson.toJson(authors);
		String allJson = "[" + albumImageJson + "," + commentsJson + "," + authorsJson + "]";
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(allJson);
		
	}
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

