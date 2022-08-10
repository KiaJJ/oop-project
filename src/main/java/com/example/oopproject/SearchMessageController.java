package com.example.oopproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMessageController implements Initializable {

    @FXML
    ListView <String> id;
    @FXML
    ListView <String> time;
    @FXML
    ListView <String> sender;
    @FXML
    ListView <String> firstCharacters;
    @FXML
    TextArea searchedMessage;
    DialogPane dialogPane;
    String ID;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void search() throws IOException {
        MainController.isInSearchMode=true;
        MainController.message = searchedMessage.getText();
        Parent root= FXMLLoader.load(getClass().getResource("SearchMessage.fxml"));
        stage = (Stage) searchedMessage.getScene().getWindow();
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
    public void listener(){
        id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                int ID;
                ID= Integer.parseInt(id.getSelectionModel().getSelectedItem());
                MainController.searchedMessageId=ID;
                root= null;
                try {
                    root = FXMLLoader.load(getClass().getResource("showSearchedMessage.fxml"));
                    stage = (Stage) searchedMessage.getScene().getWindow();
                    if(MainController.themeMode.equals("dark")){
                        root.getStylesheets().clear();
                        root.getStylesheets().add(getClass().getResource("showSearchedMessages - Copy.css").toExternalForm());
                    }
                    else{
                        root.getStylesheets().clear();
                        root.getStylesheets().add(getClass().getResource("showSearchedMessages.css").toExternalForm());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }
    public int getGroupIdByGroupName(String groupName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupID FROM groupChats " +
                "WHERE GroupName='"+groupName+"';");
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listener();
        if(MainController.isInSearchMode){
        if (!MainController.isInGroupModeChat) {
            MainController.isInSearchMode = false;
            ArrayList<String> messageSender = new ArrayList<>();
            ArrayList<String> messageId = new ArrayList<>();
            ArrayList<String> timeSended = new ArrayList<>();
            ArrayList<String> firstChars = new ArrayList<>();
            ///SQL
            JDBC jdbc = new JDBC();
            try {
                ResultSet resultSet = jdbc.getInfo("SELECT Text,ImageSource,Time,SenderName,MessageID" +
                        " FROM Messages WHERE " +
                        "(SenderName='" + SearchUsernameForPrivateChatController.searchedUsernameForChat + "' AND" +
                        " ReceiverName='" + MainController.loggedInUsername + "')" +
                        "OR (SenderName='" + MainController.loggedInUsername +
                        "' AND ReceiverName='" + SearchUsernameForPrivateChatController.searchedUsernameForChat
                        + "') ORDER BY MessageID ASC");
                while(resultSet.next()){
                    Pattern ptrn = Pattern.compile(MainController.message);
                    Matcher matcher = ptrn.matcher(resultSet.getString(1));
                    if(matcher.find()){
                        messageSender.add(resultSet.getString(4));
                        messageId.add(Integer.toString(resultSet.getInt(5)));
                        timeSended.add(resultSet.getString(3));
                        if(resultSet.getString(1).length()<15){
                            firstChars.add(resultSet.getString(1));
                        }
                        else{
                            String input;
                            input = resultSet.getString(1).substring(0,14) +  " ....";
                            firstChars.add(input);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(messageId.size()==0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Message Not Found!!");
                dialogPane = alert.getDialogPane();
                if(MainController.themeMode.equals("light"))
                    dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                else
                    dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                dialogPane.getStyleClass().add("dialogPane");
                alert.show();
            }
            /////
            ObservableList<String> observableList = FXCollections.observableList(messageId);
            id.setItems(observableList);

            ObservableList<String> observableList2 = FXCollections.observableList(timeSended);
            time.setItems(observableList2);

            ObservableList<String> observableList3 = FXCollections.observableList(firstChars);
            firstCharacters.setItems(observableList3);

            ObservableList<String> observableList4 = FXCollections.observableList(messageSender);
            sender.setItems(observableList4);
        }
        else{
            MainController.isInSearchMode = false;
            ArrayList<String> messageSender = new ArrayList<>();
            ArrayList<String> messageId = new ArrayList<>();
            ArrayList<String> timeSended = new ArrayList<>();
            ArrayList<String> firstChars = new ArrayList<>();
            ///SQL
            JDBC jdbc = new JDBC();
            try {
                ResultSet resultSet = jdbc.getInfo("SELECT Text,ImageSource,Time,SenderName,MessageID" +
                        " FROM Messages WHERE " +
                        "GroupID="+getGroupIdByGroupName(MainController.groupName)+" ORDER BY MessageID ASC");
                while(resultSet.next()){
                    Pattern ptrn = Pattern.compile(MainController.message);
                    Matcher matcher = ptrn.matcher(resultSet.getString(1));
                    if(matcher.find()){
                        messageSender.add(resultSet.getString(4));
                        messageId.add(Integer.toString(resultSet.getInt(5)));
                        timeSended.add(resultSet.getString(3));
                        if(resultSet.getString(1).length()<15){
                            firstChars.add(resultSet.getString(1));
                        }
                        else{
                            String input;
                            input = resultSet.getString(1).substring(0,14)+ " ....";
                            firstChars.add(input);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(messageId.size()==0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Message Not Found!!");
                dialogPane = alert.getDialogPane();
                if(MainController.themeMode.equals("light"))
                    dialogPane.getStylesheets().add(getClass().getResource("Alert.css").toExternalForm());
                else
                    dialogPane.getStylesheets().add(getClass().getResource("Alert - Copy.css").toExternalForm());
                dialogPane.getStyleClass().add("dialogPane");
                alert.show();
            }
            /////
            ObservableList<String> observableList = FXCollections.observableList(messageId);
            id.setItems(observableList);

            ObservableList<String> observableList2 = FXCollections.observableList(timeSended);
            time.setItems(observableList2);

            ObservableList<String> observableList3 = FXCollections.observableList(firstChars);
            firstCharacters.setItems(observableList3);

            ObservableList<String> observableList4 = FXCollections.observableList(messageSender);
            sender.setItems(observableList4);
        }
    }
    }
}
