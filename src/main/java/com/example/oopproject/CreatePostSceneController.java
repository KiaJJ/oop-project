package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CreatePostSceneController implements Initializable  {
    private Stage stage;
    private Scene scene;
    private Parent root;
    String imageAddress;
    @FXML
    TextArea postCaption;
    @FXML
    Button attach;


    public void switchToHomeScene(ActionEvent actionEvent) throws IOException,SQLException {
        addPost();
        Parent root= FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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
    public String setTheTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter currentTimeForEdit = DateTimeFormatter.ofPattern("    HH:mm");
        String formattedTime = currentTime.format(currentTimeForEdit).toString();
        return formattedTime;
    }
    public void addPost() throws SQLException {
        ///SQL:
        //پست با کپشن ()postCaption.getText و آدرس عکس postImageUrl.getText به پست های کاربر با نام کاربری MainController.loggedInUsername اضافه شود
        //دقت شود که زمان ایجاد پست نیز ذخیره شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO posts(Text,SenderName,ImageSource,Time) VALUES('"
                +postCaption.getText()+"','"+ MainController.loggedInUsername+"','"
                +imageAddress+"','" +setTheTime()+"');");
    }
    public void switchToHomeScene2(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /////////////////
        FileChooser fil_chooser = new FileChooser();
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e)
                    {

                        // get the file selected
                        File file = fil_chooser.showOpenDialog(stage);

                        if (file != null) {

                            String str=file.toURI().toString();

                            imageAddress=str;


                        }
                    }
                };
        attach.setOnAction(event);
    }
}
