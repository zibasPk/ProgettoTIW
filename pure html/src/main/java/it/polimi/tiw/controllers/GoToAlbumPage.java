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

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.controllers.utils.ParamValidator;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;

@WebServlet("/album")
public class GoToAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

		ServletContext servletContext = getServletContext();

		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Image> images = null;
		int neededPages = 1;

		String albumIdStr = request.getParameter("id");
		String pageStr = request.getParameter("page");
		String path = getServletContext().getContextPath();
		ParamValidator validator = new ParamValidator(response);

		// checking if params are empty
		if (pageStr == null || pageStr.isEmpty() || albumIdStr == null || albumIdStr.isEmpty()) {
			Logger.debug("\nparameters incomplete redirecting to homepage");
			response.sendRedirect(path + "/GoToHomePage");
			return;
		}
		// creating DAOs
		ImageDAO imgService = new ImageDAO(connection);
		AlbumDAO albumService = new AlbumDAO(connection);

		// checking if params are parsable
		int albumId = 0;
		int currPage = 0;
		try {
			albumId = Integer.parseInt(albumIdStr);
			currPage = Integer.parseInt(pageStr);
		} catch (NumberFormatException e2) {
			Logger.debug("\nparameters invalid redirecting to homepage");
			response.sendRedirect(path += "/GoToHomePage");
			return;
		}

		// checking if params values are valid
		if(!validator.validateAlbum(albumService, path, albumId, currPage)) return;

		// fetching the five images of the current page from database
		try {
			int limit = currPage * 5;
			int offset = currPage * 5 - 5;
			images = imgService.findFiveImagesFromAlbum(albumId, limit, offset);
			neededPages = (int) Math.ceil(((double) imgService.findAlbumSize(albumId)) / 5);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in retrieving images from the database");
			return;
		}

		// using template resolver to generate html page and setting up ctx variables
		path = "/WEB-INF/album.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("images", images);
		ctx.setVariable("pages", neededPages);
		ctx.setVariable("nextPage", currPage + 1);
		ctx.setVariable("prevPage", currPage - 1);
		Logger.debug("neede pages: " + neededPages);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
