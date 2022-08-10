package com.example.oopproject;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RepliedMessageTemplateController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Label repliedMessage;

    @FXML
    private Label senderName;

    @FXML
    private Label time;

    @FXML
    private Label typingMessage;

    @FXML
    AnchorPane repliedPane;
    @FXML
    AnchorPane typingPane;
    @FXML
    HBox detail;

    int ID;
    public int repliedMessageID;
    public void setData(String repliedMessage , String senderName , String time , String typingMessage
            ) throws SQLException {
        if(!checkIfTheMessageIsDeleted(repliedMessageID)) {
            this.repliedMessage.setText(repliedMessage);
            this.senderName.setText(senderName);
            this.time.setText(time);
            this.typingMessage.setText(typingMessage);
        }
        else{
            this.repliedMessage.setText("Deleted Message");
            this.senderName.setText(senderName);
            this.time.setText(time);
            this.typingMessage.setText(typingMessage);
        }
    }
    public boolean checkIfTheMessageIsDeleted(int messageId) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Status FROM messages WHERE MessageID="
                +messageId+";");
        while(resultSet.next()){
            System.out.println(resultSet.getString(1));
            if(resultSet.getString(1).equals("Deleted"))
                return true;
        }
        return false;
    }
    public void setRepliedMessageID(int id){
        this.repliedMessageID=id;
    }
    public void setID(int id){
        this.ID=id;
    }
    public void reply(){
        typingPane.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //reply left click
                        if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 1) {
                            if(!MainController.isInGroupModeChat) {
                                PrivateChatSceneController.replyMode = true;
                                PrivateChatSceneController.repliedMessageType = "text";
                                PrivateChatSceneController.repliedMessageId = ID;
                                try {
                                    switchToPrivateChatScene();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                GroupChatSceneController.replyMode=true;
                                GroupChatSceneController.repliedMessageType = "text";
                                GroupChatSceneController.repliedMessageId = ID;
                                try {
                                    switchToGroupChatScene();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //forward right click
                        else if(mouseEvent.isSecondaryButtonDown() && mouseEvent.getClickCount() == 1){
                            MainController.forwardedMessageId=ID;
                            try {
                                switchOpenForwardScene();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //edit double left click
                        else {
                            try {
                                if(mouseEvent.isBackButtonDown()&& MainController.getSenderUsername(ID).equals(MainController.loggedInUsername)){
                                    try {
                                        if(!isForwarded(ID)) {
                                            MainController.isInEditMode = true;
                                            MainController.isInEditMode2 = true;
                                            MainController.editedMessageId = ID;
                                            if(!MainController.isInGroupModeChat) {
                                                try {
                                                    switchToPrivateChatScene();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                try {
                                                    switchToGroupChatScene();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //delete double right click
                                else if(mouseEvent.isMiddleButtonDown() && MainController.getSenderUsername(ID).equals(MainController.loggedInUsername)){
                                    MainController.deletedMessageId=ID;
                                    JDBC jdbc = new JDBC();
                                    try {
                                        jdbc.setInfo("UPDATE messages SET Status='Deleted' WHERE MessageID="
                                                +ID+";");
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    if(!MainController.isInGroupModeChat){
                                        try {
                                            switchToPrivateChatScene();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else{
                                        try {
                                            switchToGroupChatScene();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reply();
    }
    public void switchToPrivateChatScene() throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("PrivateChatScene.fxml"));
        stage = (Stage) senderName.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("PrivateChatScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchOpenForwardScene() throws IOException {
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("GetDetailForForwarding.fxml"));
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GetDetailForForwarding - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GetDetailForForwarding.css").toExternalForm());
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToGroupChatScene() throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("GroupChatScene.fxml"));
        stage = (Stage) senderName.getScene().getWindow();
        if(MainController.themeMode.equals("dark")){
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupChatScene - Copy.css").toExternalForm());
        }
        else{
            root.getStylesheets().clear();
            root.getStylesheets().add(getClass().getResource("GroupChatScene.css").toExternalForm());
        }
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public boolean isForwarded(int ID) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ForwardedFrom FROM" +
                " messages WHERE MessageID="+ID+";");
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
}
