package com.Mess;

import java.lang.management.ThreadInfo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Mess_management_App {
	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws SQLException {

		try (Connection con = DBConnection.getConnection()) {
			System.out.println("MySQL connected Successfully!");
		} catch (SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
			return;
		}

		while (true) {
			System.out.println("\n====== Food Mess System =========");
			System.out.println("1. Add Student");
			System.out.println("2. View Student");
			System.out.println("3. Search Student");
			System.out.println("4. Update Student");
			System.out.println("5. Delete Student");
			System.out.println("6. View Food Menu");
			System.out.println("7. Book Meal");
			System.out.println("8. Record Payment");
			System.out.println("9. Student Mess Bill Report");
			System.out.println("0. Exit");
			System.out.print("Enter Choice: ");

			int choice = readInt();

			switch (choice) {
			case 1 -> addStudent();
			case 2 -> viewStudent();
			case 3 -> searchStudent();
			case 4 -> updateStudent();
			case 5 -> deleteStudent();
			case 6 -> viewMenu();
			case 7 -> bookMeal();
			case 8 -> recordPayment();
			case 9 -> billReport();
			case 0 -> {
				System.out.println("Thank You for using Mess App!");
				sc.close();
				return;
			}
			default -> System.out.println("Invaid Choice!");

			}

		}
	}

	private static int readInt() {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Enter a valid whole number: ");
			}
		}
	}

	// Add Student Method...

	private static void addStudent() {

		String name = readtext("Student Name: ");
		String email = readtext("Email (optional): ");
		String phone = readtext("Phone: ");
		String Course = readtext("Course: ");

		if (name.isEmpty() || Course.isEmpty()) {
			System.out.println("Student name and course are required.");
			return;
		}

		String sql = """
				INSERT INTO Students
				(Student_Name, Email, Phone, Course)
				VALUES (?, ?, ?, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.setString(2, email.isEmpty() ? null : email);
			ps.setString(3, phone);
			ps.setString(4, Course);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Student added successfully!");
			}

		} catch (SQLException e) {
			System.out.println("Could not add student: " + e.getMessage());
		}
	}

	// View All Students..

	private static void viewStudent() {

		String sql = "SELECT * FROM Students ORDER BY Student_ID";

		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			System.out.println("\n--- STUDENT LIST ----");

			boolean found = false;

			while (rs.next()) {
				found = true;

				System.out.println("ID: " + rs.getInt("Student_ID") + " | Name: " + rs.getString("Student_Name")
						+ " | Email: " + rs.getString("Email") + " | Phone: " + rs.getString("Phone") + " | Course: "
						+ rs.getString("Course"));
			}

			if (!found) {
				System.out.println("No students found.");
			}

		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	// Search Student By ID...

	private static void searchStudent() {

		System.out.print("Enter Student ID: ");
		int id = readInt();

		String sql = "SELECT * FROM Students WHERE Student_ID = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					System.out.println("Student ID: " + rs.getInt("Student_ID"));
					System.out.println("Name: " + rs.getString("Student_Name"));
					System.out.println("Email: " + rs.getString("Email"));
					System.out.println("Phone: " + rs.getString("Phone"));
					System.out.println("Course: " + rs.getString("Course"));
				} else {
					System.out.println("Student not found.");
				}
			}

		} catch (SQLException e) {
			System.out.println("Search error: " + e.getMessage());
		}
	}

	// Update student..

	private static void updateStudent() {

		System.out.print("Enter Student ID to update: ");
		int id = readInt();

		String name = readtext("New Name: ");
		String email = readtext("New Email: ");
		String phone = readtext("New Phone: ");
		String course = readtext("New Course: ");

		String sql = """
				UPDATE Students
				SET Student_Name = ?,
				    Email = ?,
				    Phone = ?,
				    Course = ?
				WHERE Student_ID = ?
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.setString(2, email.isEmpty() ? null : email);
			ps.setString(3, phone);
			ps.setString(4, course);
			ps.setInt(5, id);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Student updated successfully!");
			} else {
				System.out.println("Student ID not found!");
			}

		} catch (SQLException e) {
			System.out.println("Update failed: " + e.getMessage());
		}
	}

	// Delete Student....

	private static String readtext(String message) {
		System.out.print(message);
		return sc.nextLine().trim();
	}

	private static void deleteStudent() {
		System.out.println("Enter student ID to delete: ");
		int id = readInt();

		String sql = "DELETE FROM Students WHERE student_id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			int rows = ps.executeUpdate();

			System.out.println(rows > 0 ? "Student deleted Successfully.." : "Student ID Not Found..");
		} catch (SQLException e) {
			System.out.println("Cannot delete student. Existing booking or Payments" + "may reference this student. "
					+ e.getMessage());
		}
	}

	// View food menu..

	private static void viewMenu() {
		String sql = "SELECT * FROM Menu ORDER BY Menu_ID";

		try (Connection con = DBConnection.getConnection();
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery(sql)) {

			System.out.println("\n--- FOOD MENU ---");

			while (rs.next()) {
				System.out.printf("ID: %d | Meal: %s | Food: %s | Rs. %s%n", rs.getInt("Menu_ID"),
						rs.getString("Meal_Type"), rs.getString("Food_Name"), rs.getBigDecimal("Price"));
			}

		} catch (SQLException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	// Book Meal...

	private static void bookMeal() {

		String sql = """
				INSERT INTO Booking
				(student_id, Menu_id, Booking_Date, Quantity, Total_Amount)
				VALUES (?, ?, ?, ?, ?)
				""";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			System.out.print("Enter Student ID: ");
			int studentId = readInt();

			System.out.print("Enter Menu ID: ");
			int menuId = readInt();

			// Read date safely
			System.out.print("Enter Booking Date (YYYY-MM-DD): ");
			String bookingDate = sc.nextLine().trim();

			if (bookingDate.isEmpty()) {
				System.out.println("Booking date cannot be empty!");
				return;
			}

			java.sql.Date sqlDate = java.sql.Date.valueOf(bookingDate);

			System.out.print("Enter Quantity: ");
			int quantity = readInt();

			if (quantity <= 0) {
				System.out.println("Quantity must be greater than zero!");
				return;
			}

			System.out.print("Enter Total Amount: ");
			double totalAmount = Double.parseDouble(sc.nextLine().trim());

			ps.setInt(1, studentId);
			ps.setInt(2, menuId);
			ps.setDate(3, sqlDate);
			ps.setInt(4, quantity);
			ps.setDouble(5, totalAmount);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Meal booked successfully!");
			}

		} catch (Exception e) {
			System.out.println("Booking failed!");
			e.printStackTrace();
		}
	}

	// Record Payment...
	private static void recordPayment() {
		System.out.print("Student ID: ");
		int studentId = readInt();

		System.out.print("Payment Amount: ");
		BigDecimal amount;

		try {
			amount = new BigDecimal(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid amount.");
			return;
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Amount must be greater than zero.");
			return;
		}

		System.out.print("Payment Method (CASH/UPI/CARD): ");
		String method = sc.nextLine().trim().toUpperCase();

		String sql = "INSERT INTO Payments " + "(Student_ID, Amount, Payment_Date, Payment_Method) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, studentId);
			ps.setBigDecimal(2, amount);
			ps.setDate(3, Date.valueOf(LocalDate.now()));
			ps.setString(4, method);

			int rows = ps.executeUpdate();

			if (rows > 0) {
				System.out.println("Payment recorded successfully!");
			}

		} catch (SQLException e) {
			System.out.println("Payment failed: " + e.getMessage());
		}
	}

	// Bill Report For One Student..

	private static void billReport() {
		System.out.println("Enter Student Id: ");
		int studentId = readInt();

		String sql = """
				SELECT
				    s.student_id,
				    s.Student_Name,
				    COALESCE(
				        (SELECT SUM(b.Total_Amount)
				         FROM Booking b
				         WHERE b.student_id = s.student_id),
				        0
				    ) AS total_bill,
				    COALESCE(
				        (SELECT SUM(p.amount)
				         FROM Payments p
				         WHERE p.student_id = s.student_id),
				        0
				    ) AS total_paid
				FROM Students s
				""";

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				System.out.println("Student ID: " + rs.getInt("student_id"));
				System.out.println("Name: " + rs.getString("Student_Name"));
				System.out.println("Total Bill: " + rs.getDouble("total_bill"));
				System.out.println("Total Paid: " + rs.getDouble("total_paid"));
				System.out.println("--------------------------");
			}

		} catch (SQLException e) {
			System.out.println("Report Failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
}