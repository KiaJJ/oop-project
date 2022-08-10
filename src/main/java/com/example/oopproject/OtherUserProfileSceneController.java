package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OtherUserProfileSceneController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    public int counter;
    @FXML
    Circle cirlce;
    @FXML
    Button blockButton;
    @FXML
    Button unBlockButton;

    @FXML
    private Label followersNum;

    @FXML
    private Label followingsNum;

    @FXML
    private Label fullName;

    @FXML
    private GridPane gridPosts;

    @FXML
    private ImageView userImage;

    @FXML
    private Label userName;

    @FXML
    private Button follow;

    @FXML
    private MenuButton followersMenu;

    @FXML
    private MenuButton followingsMenu;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            if(checkIfTheUserIsBlocked(MainController.searchedUsername)){
                blockButton.setVisible(false);
            }
            else{
                unBlockButton.setVisible(false);
            }
            if(checkIfTheUserIsFollowed()){
                follow.setVisible(false);
            }
            else{
                follow.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userName.setText(MainController.searchedUsername);
        try {
            if(!profilePicUrl(MainController.searchedUsername).equals("")){
                File file = new File(profilePicUrl(MainController.searchedUsername));
                Image image = new Image((profilePicUrl(MainController.searchedUsername)));
                cirlce.setFill(new ImagePattern(image));
                //userImage.setImage(image);
//                Circle circle = new Circle();
//                circle.setStroke(Color.BLACK);
//                userImage.setClip(circle);
//                SnapshotParameters parameters = new SnapshotParameters();
//                parameters.setFill(Color.TRANSPARENT);
//                WritableImage newImage = userImage.snapshot(parameters,null);
//                userImage.setClip(null);
//                userImage.setImage(newImage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            fullName.setText(findSpecificUsernameFullname(MainController.searchedUsername));
            followersNum.setText(Integer.toString(findSpecificUsernameFollowersNum(MainController.searchedUsername)));
            followingsNum.setText(Integer.toString(findSpecificUsernameFollowingsNum(MainController.searchedUsername)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            addPosts();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            completeFollowersList(MainController.searchedUsername);
            completeFollowingsList(MainController.searchedUsername);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void completeFollowersList(String userName) throws SQLException{
        ///SQL:
        // فالوورز های کاربر با نام کاربری userName به لیست followersMenu اد شوند
        // نمونه اد کردن در زیر آمده است
        /*MenuItem menuItem = new MenuItem("نام کاربری فالوور");
        followersMenu.getItems().add(menuItem);*/
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Follower FROM follows WHERE Followed = '" +userName+ "';");
        while(resultSet.next()){
            MenuItem menuItem = new MenuItem(resultSet.getString(1));
            followersMenu.getItems().add(menuItem);
        }
    }
    public void completeFollowingsList(String userName) throws SQLException{
        ///SQL:
        // فالوویینگ های کاربر با نام کاربری userName به لیست followingsMenu اد شوند
        // نمونه اد کردن در زیر آمده است
        /*MenuItem menuItem = new MenuItem("نام کاربری فالویینگ");
        followersMenu.getItems().add(menuItem);*/
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Followed FROM follows WHERE Follower = '" +userName+ "';");
        while(resultSet.next()) {
            MenuItem menuItem = new MenuItem(resultSet.getString(1));
            followingsMenu.getItems().add(menuItem);
        }
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
    public void blockUser(ActionEvent actionEvent) throws SQLException{
        //یوزر با نام کاربری MainController.searchedUsername به بلاک لیست یوزر با نام کاربری MainController.loggedInUsername اضافه شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO blocked VALUES('"+MainController.loggedInUsername+"','"
        +MainController.searchedUsername+"','Blocked');");
        blockButton.setVisible(false);
        unBlockButton.setVisible(true);
    }
    public void unBlockUser(ActionEvent actionEvent) throws SQLException{
        // یوزر با نام کاربری MainController.searchedUsername از بلاک لیست یوزر با نام کاربری MainController.loggedInUsername خارج شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("DELETE FROM blocked WHERE Blocker='"+MainController.loggedInUsername
        +"' AND Blocked='"+MainController.searchedUsername+"';");
        unBlockButton.setVisible(false);
        blockButton.setVisible(true);
    }
    public int findSpecificUsernameFollowingsNum( String userName) throws SQLException{
        int output = 0;
        ///SQL :
        //تعداد followings کاربر با نام کاربری userName به صورت فرمت بیان شده در زیر ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT FollowStatus FROM " +
                "follows WHERE FollowStatus = 'follows' AND " +
                "Follower ='"+userName+"';");
        while (resultSet.next()){
            output++;
        }
        return output;
    }
    public int findSpecificUsernameFollowersNum( String userName) throws SQLException{
        int output = 0;
        ///SQL :
        //تعداد followers کاربر با نام کاربری userName به صورت فرمت بیان شده در زیر ریترن شود
        //firstname+" "+lastname
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT FollowStatus FROM " +
                "follows WHERE FollowStatus = 'follows' AND " +
                "Followed ='"+userName+"';");
        while (resultSet.next()){
            output++;
        }
        return output;
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
    public void addPosts() throws IOException, SQLException {
        ///SQL :
        //در حین تکمیل این بخش حتما به نویسنده این تابع اطلاع دهید : آرمین دهقان
        // با الگوریتمی که در نظر دارید  در یک for کل پست های کاربر با نام کاربریMainController.loggedInUsername را در  gridPosts اد کنید
        // در زیر نمونه ای برای اد کردن آمده است :
        // برای اد کردن ابتدا counter  را برابر 0 قرار دهید و با هر بار اد کردن یکی به مقدار آن بیافزایید
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text,Time,ImageSource,NumberOfLikes" +
                ",NumberOfComments,PostID,SenderName FROM posts WHERE SenderName='"
                +MainController.searchedUsername+"';");
        while(resultSet.next()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("PostTemplate.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            if(MainController.themeMode.equals("dark")){
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("PostTemplate - Copy.css").toExternalForm());
            }
            else{
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("PostTemplate.css").toExternalForm());
            }
            PostTemplateController postTemplateController = fxmlLoader.getController();
            postTemplateController.setData(resultSet.getString(3),resultSet.getString(1)
                    ,resultSet.getInt(6),resultSet.getString(2),resultSet.getString(7)
                    ,resultSet.getInt(4),resultSet.getInt(5),profilePicUrl(resultSet.getString(7)));
            if (MainController.getAccountType(MainController.searchedUsername).equals("Normal")) {
            } else {

                postTemplateController.justForBusinessAccountInOtherAccountsAndProfile();

            }
            /////////////////////////////////////
            ////////DailyViews
            if(MainController.getAccountType(resultSet.getString(7)).equals("Business")) {
                String today = MainController.setTheTime();
                int previousDailyViews = 0;
                if (dailyReportExists(resultSet.getInt(6), today)) {
                    ResultSet report = jdbc.getInfo("SELECT NumberOfDailyViews FROM businessPostDailyReport WHERE" +
                            " PostID=" + resultSet.getInt(6) + " AND Date='" + today + "';");
                    while (report.next()) {
                        previousDailyViews = report.getInt(1);
                    }
                    previousDailyViews++;
                    jdbc.setInfo("UPDATE businessPostDailyReport SET NumberOfDailyViews=" + previousDailyViews + "" +
                            " WHERE PostID=" + resultSet.getInt(6) + " AND Date='" + today + "';");
                } else {
                    jdbc.setInfo("INSERT INTO businessPostDailyReport(PostID,Date,NumberOfDailyViews)" +
                            " VALUES(" + resultSet.getInt(6) + ",'" + today + "',1);");
                }
            }
            /////////////////////////////////////
            gridPosts.add(anchorPane,0,counter++);
            GridPane.setMargin(anchorPane,new Insets(20));
        }

    }
    public boolean checkIfTheUserIsFollowed() throws SQLException{
        ///SQl :
        //بررسی شود که آیا کاربر با نام کاربری MainController.loggedInUsername کابر با نام کاربری MainController.searchedUsername را فالو کرده است یا خیر
        // اگر فالو کرده بود true اگر نه false ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Follower,Followed FROM follows WHERE Follower='"
                +MainController.loggedInUsername+"' AND Followed='"+MainController.searchedUsername+"';");
        while(resultSet.next()){
            return true;
        }
        return false;
    }
    public boolean checkIfTheUserIsBlocked(String userName) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Blocker,Blocked FROM blocked WHERE Blocker='"
                +MainController.loggedInUsername+"' AND Blocked='"+userName+"';");
        while(resultSet.next()){
            return true;
        }
        return false;
    }
    public void follow() throws SQLException, IOException {
        ///SQl :
        // کاربر با نام کاربری MainController.searchedUsername به لیست فالووینگ های کاربر با نام کاربری MainController.loggedInUsername اضافه شود
        //کاربر با نام کاربریMainController.loggedInUsername به لیست فالوورز های کاربر با نام کاربری MainController.searchedUsername اضافه شود
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO follows VALUES ('" + MainController.loggedInUsername + "','"
                + MainController.searchedUsername + "','follows');");
        follow.setVisible(false);
        Parent root= FXMLLoader.load(getClass().getResource("OtherUserProfileScene.fxml"));
        stage = (Stage)gridPosts .getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("OtherUserProfileScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("OtherUserProfileScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToHomeScene(ActionEvent event) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
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
}
