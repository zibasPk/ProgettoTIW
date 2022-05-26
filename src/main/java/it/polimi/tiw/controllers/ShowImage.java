package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.tinylog.Logger;

//import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/showImage")
public class ShowImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}

		ServletContext servletContext = getServletContext();

		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Image fullImage = null;
		Map<String, String> commentToAuthor = new LinkedHashMap<>();

		String albumIdStr = request.getParameter("id");
		String pageStr = request.getParameter("page");
		String imgTxt = request.getParameter("img");

		String path = getServletContext().getContextPath();

		// checking if params are empty
		if (pageStr == null || pageStr.isEmpty() || albumIdStr == null || albumIdStr.isEmpty()) {
			Logger.debug("\nparameters incomplete redirecting to homepage");
			response.sendRedirect(path + "/GoToHomePage");
			return;
		}
		
		// checking if album & currPage params are parsable
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

		// creating DAOs
		ImageDAO imgService = new ImageDAO(connection);
		CommentDAO commentService = new CommentDAO(connection);
		UserDAO userService = new UserDAO(connection);
		AlbumDAO albumService = new AlbumDAO(connection);

		// checking if album & currPage params values are valid
		try {
			if (!albumService.validAlbum(albumId)) {
				Logger.debug("album id isn't valid redirectiong to homepage");
				response.sendRedirect(path + "/GoToHomePage");
				return;
			}
			if (currPage * 5 - 5 > albumService.findAlbumImageCount(albumId) || currPage < 0) {
				Logger.debug("album page isn't valid redirecting to first page");
				response.sendRedirect(path + "/album?id=" + albumId + "&page=1");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in checking id param from the database");
			return;
		}
		
		// checking if img param is valid
		if (imgTxt == null || imgTxt.isEmpty()) {
			Logger.debug("\nimg parameter incomplete redirecting to homepage");
			response.sendRedirect(request.getServletContext().getContextPath() + "/GoToHomePage");
		}
		
		//parsing img index
		int imageIndex = 0;
		try {
			imageIndex = Integer.parseInt(imgTxt);
		} catch (NumberFormatException e2) {
			Logger.debug("\nimg parameter invalid redirecting to homepage");
			response.sendRedirect(request.getServletContext().getContextPath() + "/GoToHomePage");
		}

		// fetching full image + comments from database
		try {
			fullImage = imgService.findImage(imageIndex);
			if (fullImage == null) {
				Logger.debug("\nimg id is invalid redirecting to homepage");
				response.sendRedirect(path + "/album?id=" + albumId + "&page=" + currPage);
				return;
			}
			commentService.findCommentsForImage(imageIndex).forEach(comment -> {
				try {
					commentToAuthor.put(comment.getText(), userService.getUserFromID(comment.getUserID()).getFullName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in retrieving comments from the database");
			return;
		}

		// using template resolver to generate html page and setting up ctx variables
		path = "/WEB-INF/album.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("comments", commentToAuthor);
		ctx.setVariable("bigimage", fullImage);
		Logger.debug("bigimage context variable was set" + fullImage.toString());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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
