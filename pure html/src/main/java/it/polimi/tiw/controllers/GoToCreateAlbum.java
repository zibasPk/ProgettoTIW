package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.tinylog.Logger;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;

@WebServlet("/GoToCreateAlbum")
public class GoToCreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

		ServletContext servletContext = getServletContext();
		Logger.debug(servletContext.getContextPath());

		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Album> ownedAlbums = null;
		List<Image> images = null;
		User user = null;
		try {
			user = (User) request.getSession().getAttribute("user");
		} catch (Exception e) {
			e.printStackTrace();
		}

		AlbumDAO albumService = new AlbumDAO(connection);
		ImageDAO imageService = new ImageDAO(connection);
		try {
			ownedAlbums = albumService.findOwnedAlbums(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in retrieving albums from the database");
			return;
		}
		
		try {
			images = imageService.findFreeImages(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in retrieving images from the database");
			return;
		}

		String path = "/WEB-INF/create-album.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("albums", ownedAlbums);
		ctx.setVariable("images", images);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
