package com.Mess;

import java.sql.Connection;

public class Test_Connection {
	public static void main(String[] args) {
		DBConnection db = new DBConnection();
		Connection con = db.getConnection();

		if (con != null) {
			System.out.println("Connection test Successfully!");
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
