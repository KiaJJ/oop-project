package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPassword {
    public static String currentUsername;
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;

    @FXML
    private TextField userNamePrime;

    @FXML
    private TextField answer;

    @FXML
    private Button submit;

    public void submit(ActionEvent actionEvent) throws IOException,SQLException {
        if(userNamePrime.getText().equals("")){
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
        else if(answer.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Security Question Answer Can't Be Empty !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(!checkUserNameExistence()){
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
        else if(!checkAnswerCorrection()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect Security Question Answer !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(checkAnswerCorrection() && checkUserNameExistence()){
            currentUsername= userNamePrime.getText();
            switchToChangePasswordScene(actionEvent);
        }
    }
    public boolean  checkUserNameExistence() throws SQLException {
        String inputUserName = userNamePrime.getText();
        ///SQL
        // بررسی شود که آیا inputUsername در دیتابیس وجود دارد یا خیر
        //اگر وجود داشت true ریترن شود اگر نه false
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while(resultSet.next()){
            if(inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public boolean  checkAnswerCorrection() throws SQLException{
        String inputAnswer = answer.getText();
        ///SQL
        // بررسی شود که آیا inputAnswer با پاسخ وارد شده برای سوال امنیتی شده هنگام ثبت نام همخوانی دارد یا نه
        // اگر همخوانی داشت true اگر نه false  ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT SecurityAnswer FROM users WHERE UserName = '"
                +userNamePrime.getText()+"';");
        while(resultSet.next()){
            if(inputAnswer.equals(resultSet.getString(1)))
                return true;
        }
        return false;

    }

    public void switchToChangePasswordScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("ChangePasswordScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ChangePasswordScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ChangePasswordScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
