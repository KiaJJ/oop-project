package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditProfileSceneController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ImageView profilePic;

    @FXML
    private Button profilePicButton;

    @FXML
    private TextField userName;
    public void switchToAccountScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("AccountScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("AccountScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("AccountScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void changeUsername(ActionEvent actionEvent) throws SQLException {
        String newUsername=userName.getText();
        String[] fullName=newUsername.split(" ");
            userName.setText(newUsername);
            JDBC jdbc = new JDBC();
            jdbc.setInfo("UPDATE users SET FirstName='"+fullName[0]+"'," +
                    " LastName ='"+fullName[1]
                    +"' WHERE UserName='"+MainController.loggedInUsername+"';");
    }
    public boolean checkUserExistence(String inputUserName) throws SQLException {
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
    public String profilePicUrl(String userName) throws SQLException{
        String output="";
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ProfilePicSource " +
                "FROM users WHERE UserName='"+userName+"';");
        while(resultSet.next())
            if(resultSet.getString(1) != null)
                return resultSet.getString(1);
        return output;
    }
    public String findSpecificUsernameFullname( String userName) throws SQLException {
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
            userName.setText(findSpecificUsernameFullname(MainController.loggedInUsername));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if(!profilePicUrl(MainController.loggedInUsername).equals("")){
                File file = new File(profilePicUrl(MainController.loggedInUsername));
                Image image = new Image((profilePicUrl(MainController.loggedInUsername)));
                profilePic.setImage(image);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ////////
        FileChooser fil_chooser = new FileChooser();
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e)
                    {

                        // get the file selected
                        File file = fil_chooser.showOpenDialog(stage);

                        if (file != null) {
                            try {
                                String str=file.toURI().toString();

                                setImageForUser(str ,MainController.loggedInUsername );
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                switchToEditProfileScene();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                };

        profilePicButton.setOnAction(event);
    }
    public void setImageForUser(String imageUrl,String userName) throws SQLException{
        JDBC jdbc = new JDBC();
        jdbc.setInfo("UPDATE users SET ProfilePicSource='"+imageUrl+"'" +
                " WHERE UserName='"+userName+"';");
    }
    public void switchToEditProfileScene() throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("EditProfileScene.fxml"));
        stage = (Stage) profilePic.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("EditProfileScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("EditProfileScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
