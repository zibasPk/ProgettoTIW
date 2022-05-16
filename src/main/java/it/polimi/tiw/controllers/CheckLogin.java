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
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private final int MAX_EMAIL_LENGHT = 320;
	private final int MAX_PASSWORD_LENGHT = 45;
	
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		if (password.length() > MAX_PASSWORD_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Password is too long");
			return;
		}
		if (email.length() > MAX_EMAIL_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is too long");
			return;
		}
		
		UserDAO userService = new UserDAO(connection);
		User user = null;
		try {
			user = userService.checkCredentials(email, password);
		} catch (SQLException e){
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		
		String path = getServletContext().getContextPath();
		if (user == null) {
			path += "/index.html";
		} else {
			request.getSession().setAttribute("user", user);
			path += "/GoToHomePage";
		}
		response.sendRedirect(path);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
