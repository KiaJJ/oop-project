package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CommentTemplateController  {
    public static int likedCommentId;
    public static int commentedOnCommentId;
    int Id;
    @FXML
    private Label commentText;

    @FXML
    private Label comments;

    @FXML
    private Button likeButton;

    @FXML
    private Label likes;

    @FXML
    private Label commenter;

    @FXML
    private Label timeCommented;

    @FXML
    TextArea comment;

    @FXML
    AnchorPane anchorPane;


    public void setData(String commentText , int numberOfComments , int numberOfLikes
            , String commenterName , String time , int Id ){
        this.Id=Id;
        this.commenter.setText(commenterName);
        this.timeCommented.setText(time);
        this.commentText.setText(commentText);
        this.comments.setText(Integer.toString(numberOfComments)+" Comments");
        this.likes.setText(Integer.toString(numberOfLikes)+" Likes");
    }



    public String setTheTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter currentTimeForEdit = DateTimeFormatter.ofPattern("    HH:mm");
        String formattedTime = currentTime.format(currentTimeForEdit).toString();
        return formattedTime;
    }

}
