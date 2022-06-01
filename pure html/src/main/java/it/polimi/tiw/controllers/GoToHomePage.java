package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.List;
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
import org.tinylog.Logger;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;


@WebServlet("/GoToHomePage")
public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public GoToHomePage() {
		super();
	}
	
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {;
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		ServletContext servletContext = getServletContext();
		Logger.debug(servletContext.getContextPath());
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {	
		List<Album> ownedAlbums = null;
		List<Album> notOwnedAlbums = null;
		
		User user = null;
		try {
			user = (User)request.getSession().getAttribute("user");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		AlbumDAO bService = new AlbumDAO(connection);
		try {
			ownedAlbums = bService.findOwnedAlbums(user.getId());
			notOwnedAlbums = bService.findNotOwnedAlbums(user.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY,
					"Error in retrieving albums from the database");
			return;
		}
		
		String path = "/WEB-INF/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("owned_albums", ownedAlbums);
		ctx.setVariable("not_owned_albums", notOwnedAlbums);
		templateEngine.process(path, ctx, response.getWriter());
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
	
}
		
		
		
		
		