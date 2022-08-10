package com.example.oopproject;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CommentTemplatePrimeController implements Initializable {
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


    public void likeComment() throws SQLException {
        JDBC jdbc = new JDBC();
        if (!likerUserNameExists(MainController.loggedInUsername)) {
            //add a like in posts table
            int previousLikes = 0;
            ResultSet resultSet = jdbc.getInfo("SELECT NumberOfLikes FROM comments WHERE CommentID=" + Id + ";");
            while (resultSet.next()) {
                previousLikes = resultSet.getInt(1);
            }
            previousLikes++;
            jdbc.setInfo("UPDATE comments SET NumberOfLikes=" + previousLikes + " WHERE CommentID=" + Id + ";");
            //add the liker to likes table
            jdbc.setInfo("INSERT INTO likes(LikedOnCommentID,LikerUserName) VALUES(" + Id + ",'"
                    + MainController.loggedInUsername + "');");
            likes.setText(Integer.toString(previousLikes) + " Likes");
            //liked = true;

        }
    }
    public boolean  likerUserNameExists(String userName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT LikerUserName FROM likes WHERE LikedOnCommentID="
                +Id+";");
        while (resultSet.next()) {
            if (userName.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public void commentOnComment() throws SQLException{
        //add the comment to comments table
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO comments(Time,Text,CommentedOnCommentID,CommenterUserName)" +
                " VALUES('"+setTheTime()+"','"+comment.getText()+"',"+Id
                +",'"+MainController.loggedInUsername+"');");
        //add to the number of comments
        int previousCommentsNum = 0;
        ResultSet resultSet = jdbc.getInfo("SELECT NumberOfComments FROM comments WHERE CommentID=" + Id + ";");
        while (resultSet.next()) {
            previousCommentsNum = resultSet.getInt(1);
        }
        previousCommentsNum++;
        jdbc.setInfo("UPDATE comments SET NumberOfComments=" + previousCommentsNum + " WHERE CommentID=" + Id + ";");
        comment.setText("");
        comments.setText(Integer.toString(previousCommentsNum) + " Comments");
    }
    public void showLikes() throws IOException {
        likedCommentId=Id;
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ShowLikesPrime.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowLikesPrime - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowLikesPrime.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void showComments() throws IOException {
        commentedOnCommentId=Id;
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ShowCommentsPrime.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowComments - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowComments.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void forShowComments(){
        comments.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        try {
                            showComments();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    public void forShowLikes(){
        likes.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        try {
                            showLikes();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    public String setTheTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter currentTimeForEdit = DateTimeFormatter.ofPattern("    HH:mm");
        String formattedTime = currentTime.format(currentTimeForEdit).toString();
        return formattedTime;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        forShowComments();
        forShowLikes();
    }

}
