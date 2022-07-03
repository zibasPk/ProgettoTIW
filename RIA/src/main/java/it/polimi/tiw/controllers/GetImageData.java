package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
//import it.polimi.tiw.beans.User;
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
		try {
			imageId = Integer.parseInt(req.getParameter("imageId"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Incorrect param values");
			return;
		}
		
		Image albumImage = null;

		ImageDAO imageService = new ImageDAO(connection);

		try {
			albumImage = imageService.findImage(imageId);
		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			e.printStackTrace();
			resp.getWriter().println("Error in retrieving image from databse");
			return;
		}
		
		CommentDAO commentService = new CommentDAO(connection);
		UserDAO userService = new UserDAO(connection);
		HashMap<String, String> commentToAuthor = new HashMap<>();
 		
		try {
            albumImage = imageService.findImage(imageId);
            commentService.findCommentsForImage(imageId).forEach(comment -> {
                try {
                    commentToAuthor.put(comment.getText(), userService.getUserFromID(comment.getUserID()).getFullName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			e.printStackTrace();
			resp.getWriter().println("Error in retrieving comments from databse");
			return;
        }
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String albumImageJson = gson.toJson(albumImage);
		String commentAuthorJson = gson.toJson(commentToAuthor);
		String bothJson = "[" + albumImageJson + "," + commentAuthorJson + "]";
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(bothJson);
		
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
