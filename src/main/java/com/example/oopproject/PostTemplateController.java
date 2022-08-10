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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PostTemplateController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    public static int likedPostId;
    public static int commentedOnPostId;
    public static int businessPostId;
    int ID;
    boolean liked = false;
    @FXML
    Circle picture;
    @FXML
    private ImageView img;

    @FXML
    private Label caption;

    @FXML
    private Label postCreator;

    @FXML
    private Label timePostCreated;
    @FXML
    TextArea comment;

    @FXML
    Label Ad;
    @FXML
    Button detail;
    @FXML
    Label comments;
    @FXML
    Label likes;
    @FXML
    Button likeButton;

    public void setData(String imageAddress , String inputCaption , int id ,
                        String time , String postCreatorUserName
                        , int numberOfLikes , int numberOfComments , String pictureUrl) throws SQLException {
        Image image = new Image(imageAddress);
        img.setImage(image);
        ID=id;
        caption.setText(inputCaption);
        postCreator.setText(postCreatorUserName);
        timePostCreated.setText(time);
        likes.setText(Integer.toString(numberOfLikes)+" Likes");
        comments.setText(Integer.toString(numberOfComments)+" Comments");
        if(!profilePicUrl(postCreatorUserName).equals("")) {
            Image image2 = new Image((profilePicUrl(postCreatorUserName)));
            picture.setFill(new ImagePattern(image2));
        }
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
    public void likePost() throws SQLException {
        JDBC jdbc = new JDBC();
            if (!likerUserNameExists(MainController.loggedInUsername)) {
                //add a like in posts table
                int previousLikes = 0;
                ResultSet resultSet = jdbc.getInfo("SELECT NumberOfLikes FROM posts WHERE PostID=" + ID + ";");
                while (resultSet.next()) {
                    previousLikes = resultSet.getInt(1);
                }
                previousLikes++;
                jdbc.setInfo("UPDATE posts SET NumberOfLikes=" + previousLikes + " WHERE PostID=" + ID + ";");
                //add the liker to likes table
                jdbc.setInfo("INSERT INTO likes(LikedOnPostID,LikerUserName) VALUES(" + ID + ",'"
                        + MainController.loggedInUsername + "');");
                likes.setText(Integer.toString(previousLikes) + " Likes");
                //liked = true;

                /////daily likes
                if(MainController.getAccountType(getSenderUsernameByPostId(ID)).equals("Business")) {
                    String today = MainController.setTheTime();
                    int previousDailyLikes = 0;
                    if (dailyReportExists(ID, today)) {
                        resultSet = jdbc.getInfo("SELECT NumberOfDailyLikes FROM businessPostDailyReport WHERE" +
                                " PostID=" + ID + " AND Date='" + today + "';");
                        while (resultSet.next()) {
                            previousDailyLikes = resultSet.getInt(1);
                        }
                        previousDailyLikes++;
                        jdbc.setInfo("UPDATE businessPostDailyReport SET NumberOfDailyLikes=" + previousDailyLikes + "" +
                                " WHERE PostID=" + ID + " AND Date='" + today + "';");
                    } else {
                        jdbc.setInfo("INSERT INTO businessPostDailyReport(PostID,Date,NumberOfDailyLikes)" +
                                " VALUES(" + ID + ",'" + today + "',1);");
                    }
                }
        }
    }
    public String getSenderUsernameByPostId(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT SenderName FROM posts WHERE" +
                " PostID="+ID+";");
        while(resultSet.next())
            return resultSet.getString(1);
        return "";
    }
    public boolean dailyReportExists(int postId,String date) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Date FROM businessPostDailyReport WHERE PostID="
                +postId+";");
        while (resultSet.next()) {
            if (date.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public boolean  likerUserNameExists(String userName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT LikerUserName FROM likes WHERE LikedOnPostID="
        +ID+";");
        while (resultSet.next()) {
            if (userName.equals(resultSet.getString(1)))
                return true;
        }
        return false;
    }
    public void commentOnPost() throws SQLException{
        //add the comment to comments table
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO comments(Time,Text,CommentedOnPostID,CommenterUserName)" +
                " VALUES('"+setTheTime()+"','"+comment.getText()+"',"+ID
        +",'"+MainController.loggedInUsername+"');");
        //add to the number of comments
        int previousCommentsNum = 0;
        ResultSet resultSet = jdbc.getInfo("SELECT NumberOfComments FROM posts WHERE PostID=" + ID + ";");
        while (resultSet.next()) {
            previousCommentsNum = resultSet.getInt(1);
        }
        previousCommentsNum++;
        jdbc.setInfo("UPDATE posts SET NumberOfComments=" + previousCommentsNum + " WHERE PostID=" + ID + ";");
        comment.setText("");
        comments.setText(Integer.toString(previousCommentsNum) + " Comments");
    }
    public void showLikes() throws IOException {
        likedPostId=ID;
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ShowLikes.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowLikes - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowLikes.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void showComments() throws IOException {
        commentedOnPostId=ID;
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ShowComments.fxml"));
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
        Ad.setVisible(false);
        detail.setVisible(false);
        forShowComments();
        forShowLikes();
    }
    public void hideLikeButton(){
        likeButton.setVisible(false);
    }
    public void justForBusinessAccountInOtherAccountsAndProfile(){
        Ad.setVisible(true);
    }
    public void justForBusinessAccountInProfile(){
        detail.setVisible(true);
    }
    public void showDetail(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        businessPostId=ID;
        Parent root= FXMLLoader.load(getClass().getResource("BusinessPostDetail.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("BusinessPostDetail - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("BusinessPostDetail.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
