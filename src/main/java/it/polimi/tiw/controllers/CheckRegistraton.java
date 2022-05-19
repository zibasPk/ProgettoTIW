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

import org.apache.commons.validator.routines.EmailValidator;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/CheckRegistration")
public class CheckRegistraton extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
  private final int MAX_EMAIL_LENGHT = 320;
	private final int MAX_NAME_LENGHT = 45;
	private final int MAX_SURNAME_LENGHT = 45;
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
		String name = request.getParameter("name");
		String surname = request.getParameter("email");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		//todo far ripetere la password
		//controlli sui dati inseriti nel form lato html?
		String errorMsg = validateParams(email, name, surname, password);
		if (errorMsg != null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Signup failed: " + errorMsg);
			return;
		}
		
		//makes email lowercase
		email = email.toLowerCase();
		//controllo se esiste già un altro user con la stessa mail
		UserDAO userService = new UserDAO(connection);
		boolean duplicate = false;
		try {
			duplicate = userService.checkDuplicateEmail(email);
		} catch (SQLException e){
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		
		//aggiungo le credenziali nell'database, salvo l'user nella sessione, e faccio una redirect alla home page
		String path = getServletContext().getContextPath();
		User user = null;
		if (duplicate == false) {
			try {
				userService.createCredentials(email, name, surname, password);
				user = userService.checkCredentials(email, password);
			} catch (SQLException e){
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential creation");
				return;
			}
			path = path + "/GoToHomePage"; 
			request.getSession().setAttribute("user", user);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User already registered");
			return;
		}
		
		response.sendRedirect(path);
	}
	
	private String validateParams(String email, String name, String surname, String password) {
		if (email == null || email.isEmpty() || password == null || password.isEmpty() || 
				name == null || name.isEmpty() || surname == null || surname.isEmpty()) {
			return "Missing parameters";
		}
		//todo mettere i limiti in html
		if (email.length() > MAX_EMAIL_LENGHT) {
			return "The email is too long";
		}
		if (!EmailValidator.getInstance().isValid(email)) {
			return "The email format is invalid";
		}
		if (name.length() > MAX_NAME_LENGHT) {
			return "Name is too long";
		}
		if(!name.matches("\\S+")) {
			return "Name contains an empty space";
		}
		if (surname.length() > MAX_SURNAME_LENGHT) {
			return "Surname is too long";
		}
		if(!surname.matches("\\S+")) {
			return "Surname contains an empty spaces";
		}
		if (password.length() > MAX_PASSWORD_LENGHT) {
			return "Password is too long";
		}
		if(!password.matches("\\S+")) {
			return "Password contains an empty spaces";
		}
		return null;
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
