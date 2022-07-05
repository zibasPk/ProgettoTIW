package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CommentDAO;

@WebServlet("/CreateComment")
@MultipartConfig

public class CreateComment extends HttpServlet {
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
		 
		Integer imageId = null;
		try {
			imageId = Integer.parseInt(request.getParameter("imageid"));
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		
		String comment = StringEscapeUtils.escapeJava(request.getParameter("newComment"));
		if(comment == null || comment.length() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the comment is empty");
			return;
		}
		
		// checks if the comment length is less than 280
		if (comment.length() > 280) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("the comment is too long");
			return;
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		CommentDAO commentService = new CommentDAO(connection);
		
		try {
			commentService.createComment(imageId, user.getId(), comment);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			response.getWriter().println("Error while inserting comment into DB");
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");	
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
