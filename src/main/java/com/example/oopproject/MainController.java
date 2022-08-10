package com.example.oopproject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {
    public static boolean theme = true;
    public static String themeMode = "light";  /// <<light>> or <<dark>>
    public static String loggedInUsername;
    public static String searchedUsername;
    public static int forwardedMessageId;
    public static boolean isInEditMode=false;
    public static boolean isInEditMode2=false;
    public static int editedMessageId;
    public static int deletedMessageId;
    public static String groupName;
    public static int newGroupId;
    public static boolean inGroupAddMember=false;
    public static boolean isInGroupModeChat=false;
    public static boolean isInSearchMode=false;
    public static int searchedMessageId;
    public static String message;
    public static String getSenderUsername(int messageId) throws SQLException {
        String output = "";
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT SenderName FROM messages WHERE MessageID="
        +messageId+";");
        while (resultSet.next())
            return resultSet.getString(1);
        return output;
    }
    public static String getAccountType(String userName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT TypeOfAccount FROM users WHERE UserName='"
                +userName+"';");
        while(resultSet.next()){
            return resultSet.getString(1);
        }
        return "";
    }
    public static  String setTheTime(){
        LocalDate currentTime = LocalDate.now();
        String output = currentTime.toString();
        return output;
    }
}
