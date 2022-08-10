package com.example.oopproject;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeSceneController implements Initializable {
    public static int counter =0;
    @FXML
    GridPane gridPosts;
    @FXML
    TextField searchedUsername;
    DialogPane dialogPane;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            addFollowingsPosts();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    public void searchUsername(ActionEvent actionEvent) throws IOException,SQLException {
        if (searchedUsername.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username Can't Be Empty !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if (!checkUserExistence()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username Not Found !");
            dialogPane = alert.getDialogPane();
            if(MainController.themeMode.equals("light"))
                dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
            else
                dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
            dialogPane.getStyleClass().add("dialogPane");
            alert.show();
        }
        else if(checkUserExistence()) {
            if (!searchedUsername.getText().equals(MainController.loggedInUsername)){
                MainController.searchedUsername = searchedUsername.getText();
            ///////////////
            //daily account views
            if (MainController.getAccountType(MainController.searchedUsername).equals("Business")) {
                String today = MainController.setTheTime();
                JDBC jdbc = new JDBC();
                int previousDailyViews = 0;
                if (dailyReportExists(MainController.searchedUsername, today)) {
                    ResultSet report = jdbc.getInfo("SELECT NumberOfDailyViews FROM businessAccountDailyReport" +
                            " WHERE" +
                            " UserName='" + MainController.searchedUsername + "' AND Date='" + today + "';");
                    while (report.next()) {
                        previousDailyViews = report.getInt(1);
                    }
                    previousDailyViews++;
                    jdbc.setInfo("UPDATE businessAccountDailyReport SET NumberOfDailyViews=" + previousDailyViews + "" +
                            " WHERE UserName='" + MainController.searchedUsername + "' AND Date='" + today + "';");
                } else {
                    jdbc.setInfo("INSERT INTO businessAccountDailyReport(UserName,Date,NumberOfDailyViews)" +
                            " VALUES('" + MainController.searchedUsername + "','" + today + "',1);");
                }
            }
            /////////////

            switchToOtherUserProfileScene(actionEvent);
        }
        }

    }
    public void switchToPrivateChatScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("SearchUsernameForPrivateChat.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SearchUsernameForPrivateChat - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("SearchUsernameForPrivateChat.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToLoginScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToAccountScene(ActionEvent actionEvent) throws IOException {
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
    public void switchToCreatePostScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("CreatePostScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("CreatePostScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("CreatePostScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToOtherUserProfileScene(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("OtherUserProfileScene.fxml"));
        stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
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

    public boolean checkUserExistence() throws SQLException {
        String inputUserName = searchedUsername.getText();
        ///SQL :
        //بررسی شود که آیا کاربر با نام کابری inputUsername وجود دارد یا خیر
        // اگر وجود داشت true اگر نه false ریترن شود
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT UserName FROM users");
        while(resultSet.next()){
            if(inputUserName.equals(resultSet.getString(1)))
                return true;
        }
        return  false;

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
    public boolean dailyReportExists(String userName,String date) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Date FROM businessAccountDailyReport" +
                " WHERE UserName='" +userName+"';");
        while (resultSet.next()) {
            if (date.equals(resultSet.getString(1)))
                return true;
        }
        return false;
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
    public void addFollowingsPosts() throws IOException,SQLException {
        ///SQL :
        //در حین تکمیل این بخش حتما به نویسنده این تابع اطلاع دهید : آرمین دهقان
        // با الگوریتمی که در نظر دارید  در یک for تعداد مشخصی از جدید ترین پست های followings ها را در gridPosts اد کنید
        // در زیر نمونه ای برای اد کردن آمده است :
        // برای اد کردن ابتدا counter  را برابر 0 قرار دهید و با هر بار اد کردن یکی به مقدار آن بیافزایید

        /*FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("PostTemplate.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        PostTemplateController postTemplateController = fxmlLoader.getController();
        postTemplateController.setData("JustForFun.jpg" , "Sth" , id , timePostCreated(String Format) , postCreatorUsername);*/
        // در بالا بجای JustForFun.jpg آدرس عکس قرار داده شود و بجای Sth کپشن پست را قرار دهید و بجای id آیدی پست را قراردهید

        /*gridPosts.add(anchorPane,0,counter);*/
        /*GridPane.setMargin(anchorPane,new Insets(20));*/
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text,Time,ImageSource,NumberOfLikes" +
                ",NumberOfComments,PostID,SenderName FROM posts" +
                " WHERE SenderName IN(SELECT Followed FROM follows WHERE Follower='" +
                MainController.loggedInUsername + "') ORDER BY PostID DESC;");
        while (resultSet.next() && counter < 5) {
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
            postTemplateController.setData(resultSet.getString(3), resultSet.getString(1)
                    , resultSet.getInt(6), resultSet.getString(2), resultSet.getString(7)
                    , resultSet.getInt(4), resultSet.getInt(5),profilePicUrl(resultSet.getString(7)));
            if (MainController.getAccountType(resultSet.getString(7)).equals("Normal")) {

            } else {

                postTemplateController.justForBusinessAccountInOtherAccountsAndProfile();

            }
            ////////////////////////////////////////////////////
            ////DailyViews
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
            ////////////////////////////////////////////////////
            gridPosts.add(anchorPane, 0, counter++);
            GridPane.setMargin(anchorPane, new Insets(15));
        }
        counter = 0;
    }
    public void switchToGroupListScene(ActionEvent event) throws IOException {
        Parent root=FXMLLoader.load(getClass().getResource("GroupListScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupListScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupListScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
