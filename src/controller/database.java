

//
//
//public class database {
//
//    public static Connection getConnection() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            return DriverManager.getConnection(
//                    "jdbc:mysql://localhost:3306/my_database", "root",
//                    ""
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//}
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class database {
//    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker_db";
//    private static final String USER = "root";
//    private static final String PASSWORD = "IamgonnabePROGRAMMER100%";
//
//    public static Connection getConnection() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            return DriverManager.getConnection(URL, USER, PASSWORD);
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    private static final String URL = "jdbc:mysql://localhost:3306/expense_treacker_db"; //
    private static final String USER = "root";
    private static final String PASSWORD = "IamgonnabePROGRAMMER100%";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ make sure this is correct
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace(); // ✅ Print full error
            return null;
        }
    }
}