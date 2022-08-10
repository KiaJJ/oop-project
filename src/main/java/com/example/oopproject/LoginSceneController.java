package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginSceneController{
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    Button forgotPassword;

    @FXML
    Button login;

    @FXML
    TextField password;

    @FXML
    Button signUp;

    @FXML
    TextField userName;
    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    AnchorPane backAnchorPane;
    DialogPane dialogPane;
    public void submitLogin(ActionEvent actionEvent) throws IOException,SQLException {
        if(password.getText().equals("") ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Password Repetition Can't Be Empty !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if (userName.getText().equals("")){
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
        else if (!checkPasswordCorrection()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect Password !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(checkPasswordCorrection() && checkUserExistence()) {
            MainController.loggedInUsername=userName.getText();
            switchToHomeScene(actionEvent);
        }

    }
    public boolean checkUserExistence() throws SQLException {
        String inputUserName = userName.getText();
        ///SQL :
        //بررسی شود که آیا کاربر با نام کابری inputUsername وجود دارد یا خیر
        // اگر وجود داشت true اگر نه false ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while(resultSet.next()){
            if(inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return  false;
    }
    public boolean checkPasswordCorrection() throws SQLException {
        String inputPassword = password.getText();
        String inputUserName = userName.getText();
        ///SQL :
        //بررسی شود که آیا نام کاربری inputUsername دارای رمز عبور inputPassword است یا خیر
        //اگر مطابقت داشت true در غیر اینصورت false ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Password FROM users WHERE UserName='"+inputUserName+"';");
        while(resultSet.next()){
            if(inputPassword.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public void switchToHomeScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("HomeScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("HomeScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToSignUpScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ////
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SignUpScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SignUpScene.css").toExternalForm());
        }
        ////
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToForgotPasswordScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("ForgotPassword.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ForgotPassword - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ForgotPassword.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
