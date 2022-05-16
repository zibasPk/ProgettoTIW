package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
//import org.tinylog.Logger;
import org.tinylog.Logger;

//import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.AlbumDAO;
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

	private List<Image> sliceImageList(List<Image> images, int page) {
		int from = page * 5 - 5;
		int to = images.size() > page * 5 - 1 ? page * 5 : images.size();

		return images.subList(from, to);
	}

	private Integer returnImageIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameterMap().containsKey("img")) {
			String imgTxt = request.getParameter("img");
			if (imgTxt == null || imgTxt.isEmpty()) {
				Logger.debug("\nimg parameter incomplete redirecting to homepage");
				response.sendRedirect(request.getServletContext().getContextPath() + "/GoToHomePage");
			}
			int img = 0;
			try {
				img = Integer.parseInt(imgTxt);
			} catch (NumberFormatException e2) {
				Logger.debug("\nimg parameter invalid redirecting to homepage");
				response.sendRedirect(request.getServletContext().getContextPath() + "/GoToHomePage");
			}
			return img;
		}
		return null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Image> images = null;
		Map<String, String> commentToAuthor = new HashMap<>();
		int neededPages = 1;

		String albumIdStr = request.getParameter("id");
		String pageStr = request.getParameter("page");
		String path = getServletContext().getContextPath();
			
		//checking if params are empty
		if (pageStr == null || pageStr.isEmpty() || albumIdStr == null || albumIdStr.isEmpty()) {
			Logger.debug("\nparameters incomplete redirecting to homepage");
			Logger.debug("non ha senso");
			
			response.sendRedirect(path + "/GoToHomePage");
			return;
		}

		ImageDAO imgService = new ImageDAO(connection);
		CommentDAO commentService = new CommentDAO(connection);
		UserDAO userService = new UserDAO(connection);
		AlbumDAO albumService = new AlbumDAO(connection);
		int albumId = 0;
		int currPage = 0;
		
		//checking if params are parsable
		try {
			albumId = Integer.parseInt(albumIdStr);
			currPage = Integer.parseInt(pageStr);
		} catch (NumberFormatException e2) {
			Logger.debug("\nparameters invalid redirecting to homepage");
			response.sendRedirect(path += "/GoToHomePage");
			return;
		}
		Integer imageIndex = returnImageIndex(request, response);
		
		
		try {
			images = imgService.findImagesFromAlbum(albumId);
			if (imageIndex != null) {
				if(!imgService.validImage(imageIndex)) {
					Logger.debug("\nimg id is invalid redirecting to homepage");
					response.sendRedirect(path += "/GoToHomePage");
					return;
				}
				commentService.findCommentsForImage(imageIndex).forEach(comment -> {
					try {
						commentToAuthor.put(comment.getText(), userService.getUserFromID(comment.getUserID()).getFullName());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});	
			}
			neededPages = (int) Math.ceil(((double) images.size()) / 5);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in retrieving comments from the database");
			return;
		}
		
		//checking if params values are valid
		try {
			if(!albumService.validAlbum(albumId)) {
				Logger.debug("album id isn't valid redirectiong to homepage");
				response.sendRedirect(path + "/GoToHomePage");
				return;
			}
			Logger.debug("album id is valid");
		} catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in check id param from the database");
			return;
		}
		if (currPage * 5 > images.size() + 5 || currPage < 0) {
			response.sendRedirect(path + "/album?id=" + albumId +"&page=1");
			return;
		}
		
		//using template resolver to generate html page
		path = "/WEB-INF/album.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (imageIndex != null) {
			ctx.setVariable("comments", commentToAuthor);
		}
		ctx.setVariable("images", sliceImageList(images, currPage));
		ctx.setVariable("pages", neededPages);
		ctx.setVariable("nextPage", currPage + 1);
		ctx.setVariable("prevPage", currPage - 1);
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
