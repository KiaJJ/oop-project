package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetDetailForForwardingController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;
    @FXML
    TextField username;

    @FXML
    Button send;
    public void closeStage(){
        Stage stage;
        stage = (Stage) username.getScene().getWindow();
        stage.close();
    }
    public int getGroupIdByGroupName(String groupName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupID FROM groupChats " +
                "WHERE GroupName='"+groupName+"';");
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    public void forward(ActionEvent event) throws SQLException, IOException {
        int groupId= getGroupIdByGroupName(username.getText());
        int id = MainController.forwardedMessageId;
        if(checkUserExistence() || getGroupIdByGroupName(username.getText())!=0) {
            //sender : MainController.logged in....
            //receiver : username....
            //forwarded from : sender id
            JDBC jdbc = new JDBC();
            ResultSet resultSet = jdbc.getInfo("SELECT Text,Time,ImageSource,ForwardedFrom" +
                    ",SenderName" +
                    " FROM messages WHERE MessageID=" + id + ";");
            while(resultSet.next()) {
                if (checkUserExistence()) {
                    if (isForwarded(id)) {
                        if (hasImage(id)) {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ImageSource,ForwardedFrom" +
                                    ",SenderName,ReceiverName) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime() + "','" + resultSet.getString(3)
                                    + "','" + resultSet.getString(4) + "','" + MainController.loggedInUsername
                                    + "','" + username.getText() + "');");
                        } else {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ForwardedFrom" +
                                    ",SenderName,ReceiverName) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime()
                                    + "','" + resultSet.getString(4) + "','" + MainController.loggedInUsername
                                    + "','" + username.getText() + "');");
                        }
                    } else {
                        if (hasImage(id)) {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ImageSource,ForwardedFrom" +
                                    ",SenderName,ReceiverName) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime() + "','" + resultSet.getString(3)
                                    + "','" + resultSet.getString(5) + "','" + MainController.loggedInUsername
                                    + "','" + username.getText() + "');");
                        } else {
                            jdbc.setInfo("INSERT INTO messages (Text,Time,ForwardedFrom" +
                                    ",SenderName,ReceiverName) VALUES ('" + resultSet.getString(1)
                                    + "','" + setTheTime()
                                    + "','" + resultSet.getString(5) + "','" + MainController.loggedInUsername
                                    + "','" + username.getText() + "');");
                        }
                    }
                }
                else if(getGroupIdByGroupName(username.getText())!=0){
                    if (isForwarded(id)) {
                        if (hasImage(id)) {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ImageSource,ForwardedFrom" +
                                    ",SenderName,GroupID) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime() + "','" + resultSet.getString(3)
                                    + "','" + resultSet.getString(4) + "','" + MainController.loggedInUsername
                                    + "','" + groupId + "');");
                        } else {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ForwardedFrom" +
                                    ",SenderName,GroupID) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime()
                                    + "','" + resultSet.getString(4) + "','" + MainController.loggedInUsername
                                    + "','" + groupId + "');");
                        }
                    } else {
                        if (hasImage(id)) {
                            jdbc.setInfo("INSERT INTO messages(Text,Time,ImageSource,ForwardedFrom" +
                                    ",SenderName,GroupID) VALUES('" + resultSet.getString(1)
                                    + "','" + setTheTime() + "','" + resultSet.getString(3)
                                    + "','" + resultSet.getString(5) + "','" + MainController.loggedInUsername
                                    + "','" + groupId + "');");
                        } else {
                            jdbc.setInfo("INSERT INTO messages (Text,Time,ForwardedFrom" +
                                    ",SenderName,GroupID) VALUES ('" + resultSet.getString(1)
                                    + "','" + setTheTime()
                                    + "','" + resultSet.getString(5) + "','" + MainController.loggedInUsername
                                    + "','" + groupId + "');");
                        }
                    }

                }
            }
            closeStage();

        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username Or Group Not Found !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        ////////////////////////////////////////////////////

    }

    public String setTheTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter currentTimeForEdit = DateTimeFormatter.ofPattern("    HH:mm");
        String formattedTime = currentTime.format(currentTimeForEdit).toString();
        return formattedTime;
    }
    public boolean checkUserExistence() throws SQLException {
        String inputUserName = username.getText();
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while(resultSet.next()){
            if(inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return  false;
    }
    public boolean hasImage(int ID) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ImageSource FROM messages " +
                "WHERE MessageID="+ID);
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
    public boolean isForwarded(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ForwardedFrom FROM" +
                " messages WHERE MessageID="+ID+";");
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
}