package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import it.polimi.tiw.dao.*;
import it.polimi.tiw.beans.*;

@WebServlet("/GoToHomePage")
	public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		List<Album> ownedAlbums = null;
		List<Album> notOwnedAlbums = null;
		
		User user = null;
		try {
			user = (User)request.getSession().getAttribute("user");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		AlbumDAO bService = new AlbumDAO(connection);
		try {
			ownedAlbums = bService.findOwnedAlbums(user.getId());
			notOwnedAlbums = bService.findNotOwnedAlbums(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in retrieving albums from the database");
			return;
		}
	}
	
	
	
	
	
	
	
	
	
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				
			}
		}
	}
	
}
		
		
		
		
		