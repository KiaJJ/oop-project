package com.example.oopproject;
//import com.mysql.cj.jdbc.StatementImpl;

import java.sql.*;

public class JDBC {


    Connection connection;
    {

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/messengerdatabase",
                    "root", "kj15121381");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet getInfo(String sql) throws SQLException {
        Statement statement= connection.createStatement();
        ResultSet resultSet= statement.executeQuery(sql);
        return resultSet;
    }
    public void setInfo(String sql) throws SQLException {
        Statement statement= connection.createStatement();
        int result = statement.executeUpdate(sql);
        if (result > 0)
            System.out.println("successful process");
        else
            System.out.println(
                    "unsuccessful insertion ");
    }

}
