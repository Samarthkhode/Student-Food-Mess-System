package com.Mess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	public static Connection getConnection() {
		Connection con = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Mess", "root", "root");
			System.out.println("Database connected successfully!");
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Database connection failed!");
			e.printStackTrace();
		}
		return con;
	}
	
}
