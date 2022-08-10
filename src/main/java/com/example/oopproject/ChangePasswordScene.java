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
import java.sql.SQLException;

public class ChangePasswordScene {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;

    @FXML
    private TextField newPassword;

    @FXML
    private TextField repeatedNewPassword;


    public void submit (ActionEvent actionEvent) throws IOException,SQLException {
        if(newPassword.getText().equals("") || repeatedNewPassword.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Password Repetition And Password Can't Be Empty !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if( !checkPasswordCorrectRepetition()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Password Repetition Doesn't Match The Password !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(!checkString(newPassword.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Unacceptable Password !");
            alert.setContentText("Your Password Should : \nBe At Least 6 Characters \nContain Capital Letters \nContain Numbers   ");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if( checkPasswordCorrectRepetition()){
            changePassword();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Your Password Has Been Successfully Changed");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            if(alert.showAndWait().get()== ButtonType.OK){
                switchToLoginScene(actionEvent);
            }

        }
    }
    private static boolean checkString(String input) {
        char ch;
        boolean capitalFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < input.length();i++) {
            ch = input.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            }
            if(numberFlag && capitalFlag  && input.length()>=6)
                return true;
        }
        return false;
    }
    public void changePassword() throws SQLException {
        String newInputPassword = newPassword.getText();
        /// SQL :
        //پسورد کاربر با نام کابری ForgotPassword.currentUsername به newInputPassword تغییر داده شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("UPDATE users SET Password='" + newInputPassword +"' WHERE UserName='"+
                ForgotPassword.currentUsername+"';");

    }
    public boolean checkPasswordCorrectRepetition(){
        if(newPassword.getText().equals(repeatedNewPassword.getText())){
            return true;
        }
        return false;
    }
    public void switchToLoginScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("LoginScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("LoginScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
