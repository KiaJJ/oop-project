package com.example.oopproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ShowCommentsPrime implements Initializable {
    public int counter =0;
    @FXML
    private GridPane gridComments;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadComments();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void loadComments() throws IOException, SQLException {
        int commentId= CommentTemplatePrimeController.commentedOnCommentId;
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text,NumberOfComments,NumberOfLikes," +
                "commenterUserName,Time,CommentID FROM comments WHERE CommentedOnCommentID="+commentId+";");
        while(resultSet.next()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("CommentTemplatePrime.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            if(MainController.themeMode.equals("dark")){
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("CommentTemplate - Copy.css").toExternalForm());
            }
            else{
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("CommentTemplate.css").toExternalForm());
            }
            CommentTemplatePrimeController commentTemplatePrimeController = fxmlLoader.getController();
            commentTemplatePrimeController.setData(resultSet.getString(1),resultSet.getInt(2)
                    ,resultSet.getInt(3),resultSet.getString(4),resultSet.getString(5)
                    ,resultSet.getInt(6));
            gridComments.add(anchorPane, 0, counter++);
            GridPane.setMargin(anchorPane, new Insets(20));
        }
    }
}
