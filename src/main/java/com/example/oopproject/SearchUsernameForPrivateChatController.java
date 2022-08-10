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
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchUsernameForPrivateChatController implements Initializable {

    public static String searchedUsernameForChat;
    DialogPane dialogPane;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    TextField searchedUsername;
    @FXML
    ListView followers;

    public void searchUsername(ActionEvent actionEvent) throws IOException,SQLException {
        if (searchedUsername.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username Can't Be Empty !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if (!checkUserExistence()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username Not Found !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(checkUserExistence()){
            if(checkIfTheUserIsBlocked(searchedUsername.getText(),MainController.loggedInUsername)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You Are Blocked By The User!");
                dialogPane = alert.getDialogPane();
                if(MainController.themeMode.equals("light"))
                    dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                else
                    dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                dialogPane.getStyleClass().add("dialogPane");
                alert.show();
            }
            else {
                searchedUsernameForChat=searchedUsername.getText();
                switchToPrivateChatScene(actionEvent);
            }
        }

    }
    public void switchToPrivateChatScene2() throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("PrivateChatScene.fxml"));
        stage = (Stage) followers.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToPrivateChatScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("PrivateChatScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public boolean checkUserExistence() throws SQLException {
        String inputUserName = searchedUsername.getText();
        ///SQL :
        //بررسی شود که آیا کاربر با نام کابری inputUsername وجود دارد یا خیر
        // اگر وجود داشت true اگر نه false ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while(resultSet.next()){
            if(inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return false;
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
        this.followers.setItems(observableList);
    }
    public void followersListListener(){
        followers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                String userName;
                userName= (String) followers.getSelectionModel().getSelectedItem();
                try {
                    if(checkIfTheUserIsBlocked(userName,MainController.loggedInUsername)){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("You Are Blocked By The User!");
                        dialogPane = alert.getDialogPane();
                        if(MainController.themeMode.equals("light"))
                            dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                        else
                            dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                        dialogPane.getStyleClass().add("dialogPane");
                        alert.show();
                    }
                    else {
                        searchedUsernameForChat=userName;
                        switchToPrivateChatScene2();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
        });
    }
    public boolean checkIfTheUserIsBlocked(String blocker,String blocked) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Blocker,Blocked FROM blocked WHERE Blocker='"
                +blocker+"' AND Blocked='"+blocked+"';");
        while(resultSet.next()){
            return true;
        }
        return false;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            completeFollowersList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        followersListListener();
    }
}
