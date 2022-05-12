package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
//import org.tinylog.Logger;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/album")
public class GoToAlbumPage extends HttpServlet {
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
		// Logger.debug(servletContext.getContextPath());

		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	private List<Image> sliceImages(List<Image> images, int page) {
		int from = page * 5 - 5;
		int to = images.size() > page * 5 - 1 ? page * 5 : images.size();

		return images.subList(from, to);
	}

	private Integer returnImageIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameterMap().containsKey("img")) {
			String imgTxt = request.getParameter("img");
			int img = 0;
			try {
				img = Integer.parseInt(imgTxt);
			} catch (NumberFormatException e2) {
				response.sendError(505, "Bad post ID input");
			}
			return img;
		}
		return null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Image> images = null;
		List<Comment> comments = null;
		List<String> authors = null;
		int neededPages = 1;

		String albumIDStr = request.getParameter("id");
		String pageStr = request.getParameter("page");

		if (albumIDStr == null || albumIDStr.isEmpty() || pageStr == null || pageStr.isEmpty()) {
			response.sendError(505, "Parameters incomplete");
			return;
		}

		ImageDAO imgService = new ImageDAO(connection);
		CommentDAO commentService = new CommentDAO(connection);
		UserDAO userService = new UserDAO(connection);
		int albumID = 0;
		int currPage = 0;
		String error = null;
		try {
			albumID = Integer.parseInt(albumIDStr);
			currPage = Integer.parseInt(pageStr);
		} catch (NumberFormatException e2) {
			error = "Bad post ID input";
		}
		Integer imageIndex = returnImageIndex(request, response);

		try {
			images = imgService.findImagesFromAlbum(albumID);
			if (imageIndex != null) {
				comments = commentService.findCommentsForImage(imageIndex);
				authors = new ArrayList<>();
				for (Comment comment : comments)
					authors.add(userService.getUserFromID(comment.getID()).getFullName());
			}
			neededPages = (int) Math.ceil(((double) images.size()) / 5);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in retrieving images from the database");
			return;
		}

		if (currPage * 5 > images.size() + 5) {
			response.sendError(400, "invalid parameters");
			return;
		}

		if (error != null) {
			response.sendError(505, error);
		} else {
			String path = "/WEB-INF/album.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			if (imageIndex != null) {
				ctx.setVariable("comments", comments);
				ctx.setVariable("authors", authors);
			}
			ctx.setVariable("images", sliceImages(images, currPage));
			ctx.setVariable("pages", neededPages);
			ctx.setVariable("nextPage", currPage + 1);
			ctx.setVariable("prevPage", currPage - 1);
			templateEngine.process(path, ctx, response.getWriter());
		}

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
