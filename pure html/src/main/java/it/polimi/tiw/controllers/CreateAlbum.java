package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.AlbumDAO;

@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String albumName = request.getParameter("albumName");
	 
		//String error = null;
		User user = null;
		if (albumName == null || albumName.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		// checks if the album name lenght is longer than 32 chars
		if (albumName.length() > 32) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The name is too long");
			return;
		}
		
		try {
			user = (User)request.getSession().getAttribute("user");
		} catch(Exception e) {
			e.printStackTrace();
		}
		AlbumDAO albumService = new AlbumDAO(connection);
		
		try {
			albumService.createAlbum(user.getId(), albumName);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error while inserting album into DB");
			return;
		}
		response.sendRedirect(getServletContext().getContextPath() +"/GoToCreateAlbum");
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
