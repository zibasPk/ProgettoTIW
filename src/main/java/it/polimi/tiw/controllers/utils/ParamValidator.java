package it.polimi.tiw.controllers.utils;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;
import org.tinylog.Logger;

import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;

public class ParamValidator {
	HttpServletResponse response;
	private final static int MAX_EMAIL_LENGHT = 320;
	private final static int MAX_PASSWORD_LENGHT = 45;
	private final static int MAX_NAME_LENGHT = 45;
	private final static int MAX_SURNAME_LENGHT = 45;

	public ParamValidator(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * This method checks the parameters to make sure they're valid. Whenever a
	 * parameter is null or too long the login fails and an error message is
	 * displayed.
	 */
	public boolean validateLogin(String email, String password) throws IOException {
		if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Login failed: " + "Missing parameters");
			return false;
		}
		if (email.length() > MAX_EMAIL_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Login failed: " + "Email is too long");
			return false;
		}
		if (password.length() > MAX_PASSWORD_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Login failed: " + "Password is too long");
			return false;
		}
		return true;
	}

	/**
	 * This method checks the parameters to make sure they're valid. Whenever a
	 * parameter doesn't follow certain rules the sign up fails and an error message
	 * is displayed.
	 */
	public boolean validateRegistration(String email, String name, String surname, String password,
			String confirmPassword) throws IOException {
		if (email == null || email.isEmpty() || password == null || password.isEmpty() || name == null || name.isEmpty()
				|| surname == null || surname.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Missing parameters");
			return false;
		}
		if (email.length() > MAX_EMAIL_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Email is too long");
			return false;
		}
		if (!EmailValidator.getInstance().isValid(email)) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "The email format is invalid");
			return false;
		}
		if (name.length() > MAX_NAME_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Name is too long");
			return false;
		}
		if (!name.matches("\\S+")) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Name contains an empty space");
			return false;
		}
		if (surname.length() > MAX_SURNAME_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Surname is too long");
			return false;
		}
		if (!surname.matches("\\S+")) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "SurName contains an empty space");
			return false;
		}
		if (password.length() > MAX_PASSWORD_LENGHT) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Password is too long");
			return false;
		}
		if (!password.matches("\\S+")) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Password contains an empty space");
			return false;
		}
		if (!password.equals(confirmPassword)) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Signup failed: " + "Passwords do not match");
			return false;
		}
		return true;
	}

	/**
	 * This method checks whether the album exists and that the page number fits the
	 * number of images of said album. Whenever one or both these conditions aren't
	 * valid the method redirects to the home page and an error message is
	 * displayed.
	 */
	public boolean validateAlbum(AlbumDAO albumService, String path, int albumId, int currPage) throws IOException {
		try {
			if (!albumService.validAlbum(albumId)) {
				Logger.debug("album id isn't valid redirectiong to homepage");
				response.sendRedirect(path + "/GoToHomePage");
				return false;
			}
			if (currPage * 5 - 5 > albumService.findAlbumImageCount(albumId) || currPage <= 0) {
				Logger.debug("album page isn't valid redirecting to first page");
				response.sendRedirect(path + "/album?id=" + albumId + "&page=1");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in checking id param from the database");
			return false;
		}
		return true;
	}

	/**
	 * This method checks that the image belongs to the album (by checking imgID and
	 * albumId). Whenever this condition isn't valid the method redirects to the
	 * album page and an error message is displayed.
	 */
	public boolean validateShowImage(AlbumDAO albumService, ImageDAO imgService, String path, int imgID, int albumID,
			int currPage) throws IOException {
		try {
			if (albumService.findImageAlbumID(imgID) != albumID) {
				response.sendRedirect(path + "/album?id=" + albumID + "&page=" + currPage);
				return false;
			}
			if (imgService.findImage(imgID) == null) {
				Logger.debug("\nimg id is invalid redirecting to homepage");
				response.sendRedirect(path + "/album?id=" + albumID + "&page=" + currPage);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in checking image & album param from the database");
			return false;
		}
		return true;
	}
}
