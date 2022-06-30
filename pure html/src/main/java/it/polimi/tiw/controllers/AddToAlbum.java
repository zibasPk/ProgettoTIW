package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tinylog.Logger;

import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;

@WebServlet("/AddToAlbum")
public class AddToAlbum extends HttpServlet {
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
		String albumIdStr = request.getParameter("albumId");
		String imageIdStr = request.getParameter("imageId");
		int albumId = 0;
		int imageId = 0;

		if (albumIdStr == null || albumIdStr.isEmpty() ||
				imageIdStr == null || imageIdStr.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		try {
			albumId = Integer.parseInt(albumIdStr);
			imageId = Integer.parseInt(imageIdStr);
		} catch (NumberFormatException e2) {
			Logger.debug("parameters not parsable redirecting to homepage");
		}
		
		AlbumDAO albumService = new AlbumDAO(connection);
		ImageDAO imageService = new ImageDAO(connection);
		
		try {
			if (!albumService.validAlbum(albumId)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album doesn't exist");
				return;
			}
			if (imageService.findImage(imageId) == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image doesn't exist");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error while checking param existance from DB");
			return;
		}
		
		try {
			imageService.addImageToAlbum(imageId, albumId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error while inserting album into DB");
			return;
		}
		response.sendRedirect(getServletContext().getContextPath() + "/GoToCreateAlbum");
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
