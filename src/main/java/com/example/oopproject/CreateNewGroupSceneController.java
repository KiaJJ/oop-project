package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CreateNewGroupSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    DialogPane dialogPane;
    String imageUrl;
    @FXML
    TextField groupName;
    @FXML
    private Button attach;

    public void addMember(ActionEvent actionEvent) throws IOException {
        openMemberList();
    }
    public void openMemberList() throws IOException {
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("MemberListScene.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("MemberListScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("MemberListScene.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void submit(ActionEvent actionEvent) throws SQLException, IOException {
        ///SQL
        JDBC jdbc = new JDBC();
        jdbc.setInfo("UPDATE groupChats SET ProfilePictureSource='"+imageUrl
                +"',GroupName='"+groupName.getText()+"' WHERE GroupID="
        +MainController.newGroupId+";");
        ///continue
        MainController.groupName= groupName.getText();
        switchToGroupChatScene();

    }
    public void switchToGroupChatScene() throws IOException {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /////////
        FileChooser fil_chooser = new FileChooser();
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e)
                    {

                        // get the file selected
                        File file = fil_chooser.showOpenDialog(stage);

                        if (file != null) {
                                String str=file.toURI().toString();
                                imageUrl=str;
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setContentText("Successfully Set!");
                            dialogPane = alert.getDialogPane();
                            if(MainController.themeMode.equals("light"))
                                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                            else
                                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                            dialogPane.getStyleClass().add("dialogPane");
                                alert.show();

                        }
                    }
                };

        attach.setOnAction(event);
        /////////
    }
}
