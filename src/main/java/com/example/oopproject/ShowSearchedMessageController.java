package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ShowSearchedMessageController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    ImageView messageImage;
    @FXML
    Label messageText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            int id = MainController.searchedMessageId;
            ////
        JDBC jdbc = new JDBC();
        try {
            ResultSet resultSet = jdbc.getInfo("SELECT Text,ImageSource FROM messages WHERE MessageID=" + id + ";");
            while (resultSet.next()) {
                if (hasImage(id)) {
                    Image image = new Image(getClass().getResourceAsStream(resultSet.getString(2)));
                    messageImage.setImage(image);
                }
                messageText.setText(resultSet.getString(1));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void back(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("SearchMessage.fxml"));
        stage = (Stage) messageText.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SearchMessage - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SearchMessage.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public boolean hasImage(int ID) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ImageSource FROM messages " +
                "WHERE MessageID="+ID);
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
}
