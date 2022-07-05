package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.AlbumDAO;

@WebServlet("/CreateAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String albumName = StringEscapeUtils.escapeJava(req.getParameter("albumName"));
		User user = null;
		
		if (albumName == null || albumName.isEmpty()) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("Missing parameters");
			return;
		}
		// checks if the album name lenght is longer than 32 chars
		if (albumName.length() > 32) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println("The name is too long");
			return;
		}

		try {
			user = (User) req.getSession().getAttribute("user");
		} catch (Exception e) {
			e.printStackTrace();
		}
		AlbumDAO albumService = new AlbumDAO(connection);
		
		// tries to create an album
		try {
			albumService.createAlbum(user.getId(), albumName);
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error while inserting album into DB");
			return;
		}
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");
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