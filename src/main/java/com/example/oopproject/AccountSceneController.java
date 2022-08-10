package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AccountSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    public static int counter =0 ;
    @FXML
    Circle circle ;
    @FXML
    Button views;
    @FXML
    private MenuButton followersMenu;

    @FXML
    private Label followersNum;

    @FXML
    private MenuButton followingsMenu;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            forBusinessAccountOnly();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userName.setText(MainController.loggedInUsername);
        try {
            fullName.setText(findSpecificUsernameFullname(MainController.loggedInUsername));
            followersNum.setText(Integer.toString(findSpecificUsernameFollowersNum(MainController.loggedInUsername)));
            followingsNum.setText(Integer.toString(findSpecificUsernameFollowingsNum(MainController.loggedInUsername)));
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        try {
            addPosts();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        try {
            completeFollowersList(MainController.loggedInUsername);
            completeFollowingsList(MainController.loggedInUsername);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        try {
            if(!profilePicUrl(MainController.loggedInUsername).equals("")){
                File file = new File(profilePicUrl(MainController.loggedInUsername));
                Image image = new Image((profilePicUrl(MainController.loggedInUsername)));
                circle.setFill(new ImagePattern(image));
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
    public int findSpecificUsernameFollowersNum( String userName) throws SQLException {
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
    public void addPosts() throws IOException, SQLException {
        ///SQL :
        //در حین تکمیل این بخش حتما به نویسنده این تابع اطلاع دهید : آرمین دهقان
        // با الگوریتمی که در نظر دارید  در یک for کل پست های کاربر با نام کاربریMainController.loggedInUsername را در  gridPosts اد کنید
        // در زیر نمونه ای برای اد کردن آمده است :
        // برای اد کردن ابتدا counter  را برابر 0 قرار دهید و با هر بار اد کردن یکی به مقدار آن بیافزایید
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text,Time,ImageSource,NumberOfLikes" +
                ",NumberOfComments,PostID,SenderName FROM posts WHERE SenderName='"
        +MainController.loggedInUsername+"';");
        while(resultSet.next()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("PostTemplate.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            ////

            if(MainController.themeMode.equals("dark")){
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("PostTemplate - Copy.css").toExternalForm());
            }
            else{
                anchorPane.getStylesheets().clear();
                anchorPane.getStylesheets().add(getClass().getResource("PostTemplate.css").toExternalForm());
            }

            ////
            PostTemplateController postTemplateController = fxmlLoader.getController();
            postTemplateController.setData(resultSet.getString(3),resultSet.getString(1)
                    ,resultSet.getInt(6),resultSet.getString(2),resultSet.getString(7)
                     ,resultSet.getInt(4),resultSet.getInt(5) , profilePicUrl(resultSet.getString(7)));
            if (MainController.getAccountType(MainController.loggedInUsername).equals("Normal")) {
                postTemplateController.hideLikeButton();
            } else {
                postTemplateController.hideLikeButton();
                postTemplateController.justForBusinessAccountInOtherAccountsAndProfile();
                postTemplateController.justForBusinessAccountInProfile();
            }
            gridPosts.add(anchorPane,0,counter++);
        GridPane.setMargin(anchorPane,new Insets(20));
        }
        /*FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("PostTemplate.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        PostTemplateController postTemplateController = fxmlLoader.getController();
        postTemplateController.setData("JustForFun.jpg" و "sth" , id , timePostCreated(String Format) , postCreatorUsername);*/
        //  در بالا بجای JustForFun.jpg آدرس عکس قرار داده شود و بجای sth کپشن عکس قرار داده شود و بجای id آیدی پست را بگذارید

        /*gridPosts.add(anchorPane,0,0);*/
        /*GridPane.setMargin(anchorPane,new Insets(20));*/

    }
    public void forBusinessAccountOnly() throws SQLException {
        if(MainController.getAccountType( MainController.loggedInUsername).equals("Normal")){
            views.setVisible(false);
        }
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
    public void switchToEditProfileScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("EditProfileScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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
    public void showDailyViews(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("ShowDailyViews.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowDailyViews - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("ShowDailyViews.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void changeTheme(ActionEvent actionEvent) throws IOException{
        if(MainController.themeMode.equals("dark"))
            MainController.themeMode = "light";
        else
            MainController.themeMode = "dark";
        Parent root= FXMLLoader.load(getClass().getResource("AccountScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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

}
