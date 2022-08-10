package com.example.oopproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GroupSettingsSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;
    @FXML
    TextField groupName;
    @FXML
    ListView <String> blockMember;
    @FXML
    ListView <String> banMember;

    public void switchToGroupChatScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("GroupChatScene.fxml"));
        stage = (Stage) groupName.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupChatScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupChatScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void changeGroupName(ActionEvent actionEvent) throws SQLException {
        int groupId=getGroupIdByGroupName(MainController.groupName);
        JDBC jdbc = new JDBC();
        jdbc.setInfo("UPDATE groupChats SET groupName='"+groupName.getText()+"' " +
                "WHERE GroupID="+groupId+";");
        MainController.groupName=groupName.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(" Successfully Changed");
        dialogPane = alert.getDialogPane();
        if(MainController.themeMode.equals("light"))
            dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
        else
            dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
        dialogPane.getStyleClass().add("dialogPane");
        alert.show();
    }
    public int getGroupIdByGroupName(String groupName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupID FROM groupChats " +
                "WHERE GroupName='"+groupName+"';");
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    public void completeMembers() throws SQLException {
        int id = getGroupIdByGroupName(MainController.groupName);
        ArrayList<String> members1 = new ArrayList<>();
        ArrayList<String> members2 = new ArrayList<>();
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Member,MemberStatus FROM isInGroupChat WHERE " +
                "GroupID="+id+";");
        while(resultSet.next()){

            if(!resultSet.getString(2).equals("Admin") )
            {members1.add(resultSet.getString(1));}
        }
        ResultSet resultSetPrime = jdbc.getInfo("SELECT Member,MemberStatus FROM isInGroupChat WHERE " +
                "GroupID="+id+";");
        while(resultSetPrime.next()){
            if(!resultSetPrime.getString(2).equals("Admin") ) {
                String status = "";
                if(resultSetPrime.getString(2).equals("Banned")) {
                    status = status +" (Banned)";
                }

                    members2.add(resultSetPrime.getString(1) + status);
            }
        }
        ObservableList<String> observableList = FXCollections.observableList(members2);
        banMember.setItems(observableList);
        ObservableList<String> observableList2 = FXCollections.observableList(members1);
        blockMember.setItems(observableList2);
    }
    ////block == remove
    public void blockMemberListener(){
        blockMember.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String userName;
                userName=blockMember.getSelectionModel().getSelectedItem();
                int id = 0;
                try {
                    id= getGroupIdByGroupName(MainController.groupName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                JDBC jdbc = new JDBC();
                try {
                    jdbc.setInfo("DELETE FROM isInGroupChat WHERE GroupID="+id+" AND Member='"
                    +userName+"';");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    groupSettings();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void banMemberListener(){
        banMember.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String userName;
                userName=banMember.getSelectionModel().getSelectedItem();
                if(userName.contains(" (Banned)")){
                    userName=userName.replace(" (Banned)" , "");
                }
                int id = 0;
                try {
                    id= getGroupIdByGroupName(MainController.groupName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ///
                JDBC jdbc = new JDBC();
                try {
                    ResultSet resultSet = jdbc.getInfo("SELECT MemberStatus FROM isInGroupChat" +
                            " WHERE Member='" + userName + "' AND GroupID=" + id + ";");

                    while (resultSet.next()) {

                        String newStatus = "";
                        if (resultSet.getString(1).equals("Normal")) {
                            newStatus = newStatus + "Banned";
                        }
                        else if(resultSet.getString(1).equals("Banned")) {
                            newStatus = newStatus + "Normal";
                        }
                            jdbc.setInfo("UPDATE isInGroupChat SET MemberStatus='" + newStatus
                                    + "' WHERE GroupID=" + id  +
                                    " AND Member='" + userName + "';");

                    }
                }
                catch (SQLException e) {
                        e.printStackTrace();
                    }
                    ///
                    try {
                        groupSettings();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        });
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupName.setText(MainController.groupName);
        try {
            completeMembers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        blockMemberListener();
        banMemberListener();
    }
    public void groupSettings() throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("GroupSettingsScene.fxml"));
        stage = (Stage) blockMember.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupSettingsScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupSettingsScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
