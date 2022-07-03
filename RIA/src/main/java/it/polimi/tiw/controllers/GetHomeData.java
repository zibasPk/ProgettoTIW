package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.controllers.utils.OrderCache;
import it.polimi.tiw.dao.AlbumDAO;

@WebServlet("/GetHomeData")
public class GetHomeData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");

		OrderCache cache = new OrderCache();
		List<Album> myAlbums = null;
		List<Album> otherAlbums = null;
		try {
			myAlbums = cache.getOrderedAlbums(user, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving albums from databse");
			return;
		}
		AlbumDAO albumService = new AlbumDAO(connection);
		try {
			otherAlbums = albumService.findNotOwnedAlbums(user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving albums from databse");
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String myAlbumsJson = gson.toJson(myAlbums);
		String otherAlbumsJson = gson.toJson(otherAlbums);
		String bothJson = "[" + myAlbumsJson + "," + otherAlbumsJson + "]";
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
