package it.polimi.tiw.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tinylog.Logger;

import com.google.gson.Gson;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.controllers.utils.ConnectionHandler;
import it.polimi.tiw.controllers.utils.OrderCache;

@WebServlet("/SaveAlbumOrder")
public class SaveAlbumOrder extends HttpServlet {
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
		Logger.debug("served at " + this.toString());
		BufferedReader reader = req.getReader();
		Gson gson = new Gson();
		Integer[] order = gson.fromJson(reader, Integer[].class);

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		try {
			if (!OrderCache.saveOrder(user, Arrays.asList(order), connection)) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().println("invalid saved order");
			}
		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			resp.getWriter().println("Failure in database while savingorder");
			return;
		}
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
