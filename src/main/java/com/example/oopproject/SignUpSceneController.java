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
import java.util.DuplicateFormatFlagsException;

// takmil shavad
public class SignUpSceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;
    public static int sQNum ;
    public static String accountType;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField password;

    @FXML
    private TextField repeatedPassword;

    @FXML
    private TextField sQAnswer;

    @FXML
    private Button signUp;

    @FXML
    private TextField username;

    public void submitSignUp(ActionEvent actionEvent) throws IOException,SQLException {
        if(password.getText().equals("") || repeatedPassword.getText().equals("")){
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
        else if (username.getText().equals("")){
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
        else if(!checkPasswordCorrectRepetition() && !checkUserNameExistence())
        {
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
        else if(checkPasswordCorrectRepetition() && checkUserNameExistence())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("This Username Has Already Been Used !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(!checkPasswordCorrectRepetition() && checkUserNameExistence())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("This Username Has Already Been Used !\nPassword Repetition Doesn't Match The Password !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(!checkString(password.getText())){
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
        else if(checkPasswordCorrectRepetition() && !checkUserNameExistence()){
            addNewUser();
            switchToLoginScene(actionEvent);
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
    public void setSqNum1(ActionEvent actionEvent){ sQNum=1 ;}
    public void setSqNum2(ActionEvent actionEvent){ sQNum=2 ;}
    public void setSqNum3(ActionEvent actionEvent){ sQNum=3 ;}
    public void setAccountTypeBusiness(ActionEvent actionEvent){ accountType = "Business" ;}
    public void setAccountTypeNormal(ActionEvent actionEvent){ accountType = "Normal" ;}
    public void addNewUser() throws SQLException{
        ////SQL :
        //کاربر جدید به دیتابیس اضافه شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO users VALUES("+"'"+username.getText()+"','"+password.getText()
                +"','"+firstName.getText()+"','"+lastName.getText()+"',"+sQNum+",'"+sQAnswer.getText()+"','"+accountType+"');");

    }
    public boolean  checkUserNameExistence() throws SQLException {
        String inputUserName = username.getText();
        ////SQL :
        //بررسی شود که inputUserName در دیتابیس وجود دارد یا خیر
        //اگر وجود داشت true ریترن شود اگر نه false
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while (resultSet.next()) {
            if (inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public boolean  checkPasswordCorrectRepetition(){
        if(password.getText().equals(repeatedPassword.getText())){
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
