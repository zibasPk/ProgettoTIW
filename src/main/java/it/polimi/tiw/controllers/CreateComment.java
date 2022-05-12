package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDAO;

@WebServlet("/CreateComment")
public class CreateComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateComment() {
		super();
	}

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
			;
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String comment = request.getParameter("comment");
		String albumIdStr = request.getParameter("album-id");
		String imageIdStr = request.getParameter("image-id");
		String pageNumberStr = request.getParameter("page"); 
		int albumId = 0;
		int imageId = 0;
		int pageNumber = 0;
		//String error = null;
		User user = null;
		if (comment == null || comment.isEmpty() || 
				imageIdStr == null || imageIdStr.isEmpty() ||
				albumIdStr == null || albumIdStr.isEmpty() ||
				pageNumberStr == null || pageNumberStr.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		try {
			user = (User)request.getSession().getAttribute("user");
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			albumId = Integer.parseInt(albumIdStr);
			imageId = Integer.parseInt(imageIdStr);
			pageNumber = Integer.parseInt(pageNumberStr);
		} catch (NumberFormatException e2) {
			//error = "Bad post ID input";
		}
		CommentDAO commentService = new CommentDAO(connection);
		
		try {
			commentService.createComment(imageId, user.getId(), comment);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String path = getServletContext().getContextPath();
		path += "/album?id=" + albumId + "&page=" + pageNumber + "&img=" + imageId;
		response.sendRedirect(path);
	} 	

}
