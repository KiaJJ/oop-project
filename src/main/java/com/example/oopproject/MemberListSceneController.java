package com.example.oopproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MemberListSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;
    @FXML
    ListView <String> memberList;


    public void addMemberToTheGroup(String userName,int groupId) throws SQLException{
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO isInGroupChat VALUES('"+userName+"','"+groupId+"','Normal');");
    }
    public void listener(){
        memberList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String userName;
                userName = memberList.getSelectionModel().getSelectedItem();
                try {
                    if (!MainController.inGroupAddMember) {
                        if (!checkInGroupExistence(userName, MainController.newGroupId)) {
                            addMemberToTheGroup(userName, MainController.newGroupId);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Username Successfully Added To The Group !");
                            dialogPane = alert.getDialogPane();
                            if(MainController.themeMode.equals("light"))
                                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                            else
                                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                            dialogPane.getStyleClass().add("dialogPane");
                            alert.show();
                        } else {

                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Username Has Been Already Added To The Group !");
                            dialogPane = alert.getDialogPane();
                            if(MainController.themeMode.equals("light"))
                                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                            else
                                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                            dialogPane.getStyleClass().add("dialogPane");
                            alert.show();
                        }
                    }
                    else{
                        if (!checkInGroupExistence(userName, getGroupIdByGroupName(MainController.groupName))) {
                            addMemberToTheGroup(userName, getGroupIdByGroupName(MainController.groupName));
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Username Successfully Added To The Group !");
                            dialogPane = alert.getDialogPane();
                            if(MainController.themeMode.equals("light"))
                                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                            else
                                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                            dialogPane.getStyleClass().add("dialogPane");
                            alert.show();
                        } else {

                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Username Has Been Already Added To The Group !");
                            dialogPane = alert.getDialogPane();
                            if(MainController.themeMode.equals("light"))
                                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                            else
                                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                            dialogPane.getStyleClass().add("dialogPane");
                            alert.show();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public int getGroupIdByGroupName(String groupName) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupID FROM groupChats " +
                "WHERE GroupName='"+groupName+"';");
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    public boolean checkInGroupExistence(String userName , int groupId) throws SQLException{
            JDBC jdbc = new JDBC();
            ResultSet resultSet = jdbc.getInfo("SELECT Member FROM isInGroupChat WHERE GroupID" +
                    "="+groupId+";");
            while(resultSet.next()){
                if(userName.equals(resultSet.getString(1)))
                    return true;
            }
            return  false;
        }

    public void completeFollowersList() throws SQLException {
        ArrayList<String> followers = new ArrayList<>();
        String userName= MainController.loggedInUsername;
        ///SQL:
        // فالوورز های کاربر با نام کاربری userName به لیست followersMenu اد شوند
        // نمونه اد کردن در زیر آمده است
        /*MenuItem menuItem = new MenuItem("نام کاربری فالوور");
        followersMenu.getItems().add(menuItem);*/
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Follower FROM follows WHERE Followed = '" +userName+ "';");
        while(resultSet.next()){
            followers.add(resultSet.getString(1));
        }
        ObservableList<String> observableList = FXCollections.observableList(followers);
        memberList.setItems(observableList);
    }

    public String findSpecificUsernameFullname( String userName) throws SQLException{
        String output = null;
        ///SQL :
        //نام و نام خانوادگی کاربر با نام کاربرس userName به صورت فرمت بیان شده در زیر ریترن شود
        //firstname+" "+lastname
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT firstName,lastName FROM " +
                "users WHERE UserName='" + userName+"';");
        while(resultSet.next()){
            return resultSet.getString(1)+" "+resultSet.getString(2);
        }
        return output;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            completeFollowersList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listener();


    }
}
