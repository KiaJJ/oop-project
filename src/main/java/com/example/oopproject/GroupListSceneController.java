package com.example.oopproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GroupListSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    ListView <String> groupList ;
    String groupName;

    public void createNewGroup(ActionEvent event ) throws IOException, SQLException {
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO groupChats(Admin) VALUES('"+MainController.loggedInUsername+"');");
        jdbc.setInfo("INSERT INTO isInGroupChat" +
                " VALUES('"+MainController.loggedInUsername+"','"+getTheLastGroupChatID()+"','Admin');");
        MainController.newGroupId = getTheLastGroupChatID();
        Parent root= FXMLLoader.load(getClass().getResource("CreateNewGroupScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("CreateNewGroupScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("CreateNewGroupScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public int getTheLastGroupChatID() throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT MAX(GroupID) FROM groupChats");
        while(resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            completeGroupNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        groupNameListener();
    }
    public void completeGroupNames() throws SQLException {
        ArrayList<String> groups = new ArrayList<>();
        ///SQL :
        // تمامی گروه هایی که یوزر با نام کاربری MainController.loggedInUsername در آن عضو است را در لیست ویو نمایش دهید
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupName FROM groupChats WHERE" +
                " GroupID IN (SELECT GroupID FROM isInGroupChat WHERE Member='"+
                MainController.loggedInUsername+"');");
        while(resultSet.next()){
            if(resultSet.getString(1)!=null)
            groups.add(resultSet.getString(1));
        }
        ObservableList<String> observableList = FXCollections.observableList(groups);
        groupList.setItems(observableList);
    }
    public void groupNameListener(){
        groupList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                groupName=groupList.getSelectionModel().getSelectedItem();
                MainController.groupName=groupName;
                try {
                    switchToGroupChatScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void switchToGroupChatScene() throws IOException {
        MainController.isInGroupModeChat=true;
        Parent root = FXMLLoader.load(getClass().getResource("GroupChatScene.fxml"));
        stage = (Stage) groupList.getScene().getWindow();
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
}
