package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;

@WebServlet("/GetCreateAlbumData")
//allows the servlet to recieve multi part objects such as a FormData
@MultipartConfig
public class GetCreateAlbumData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Album> ownedAlbums = null;
		List<Image> images = null;
		User user = null;
		try {
			user = (User) req.getSession().getAttribute("user");
		} catch (Exception e) {
			e.printStackTrace();
		}

		AlbumDAO albumService = new AlbumDAO(connection);
		ImageDAO imageService = new ImageDAO(connection);
		try {
			ownedAlbums = albumService.findOwnedAlbums(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving albums from databse");
			return;
		}
		
		try {
			images = imageService.findFreeImages(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Error in retrieving images from databse");
			return;
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String ownedAlbumsJson = gson.toJson(ownedAlbums);
		String myfreeImagesJson = gson.toJson(images);
		String bothJson = "[" + ownedAlbumsJson + "," + myfreeImagesJson + "]";
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