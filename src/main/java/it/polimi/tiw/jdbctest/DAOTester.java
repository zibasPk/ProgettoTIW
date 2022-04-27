package it.polimi.tiw.jdbctest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tinylog.Logger;

import it.polimi.tiw.DAOs.TestDAO;
import it.polimi.tiw.beans.Image;

public class DAOTester extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TestDAO service;
		Logger.debug("\nqui1");
		
		final String DB_URL = "jdbc:mysql://localhost:3306/progettoTIW?serverTimezone=UTC";
		final String USER = "root";
		final String PASS = "milone1";
		String result = "Connection worked";
		ArrayList<Image> images = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			
		} catch (Exception e) {
			result = "Connection failed";
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		try {
			Logger.debug("\nqui");
			service = new TestDAO(connection);
			result = service.findAllUsers();
			images = service.findAllImages();
		} catch (Exception e) {
			result = "SQL exception";
		}
		PrintWriter out = response.getWriter();
		out.println(result);
		out.println(images.toString());
		out.close();
	}
}