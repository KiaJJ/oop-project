package com.example.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShowLikesPrimeController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    ListView likersList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int commentId=CommentTemplatePrimeController.likedCommentId;
        ObservableList<String> observableList = null;
        try {
            observableList = FXCollections.observableList(getLikersList(commentId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        likersList.setItems(observableList);
    }
    public ArrayList getLikersList(int commentID) throws SQLException {
        ArrayList<String> userNames= new ArrayList<>();
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo(
                "SELECT LikerUserName FROM likes WHERE LikedOnCommentID="+commentID+";");
        while(resultSet.next()){
            userNames.add(resultSet.getString(1));
        }
        return userNames;
    }
}
