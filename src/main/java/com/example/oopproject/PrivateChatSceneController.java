package com.example.oopproject;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PrivateChatSceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    String imageAddress;
    public static int status=0;
    public static int counter =0;
    public static boolean replyMode = false;
    public static int repliedMessageId;
    public static String repliedMessageType;
    @FXML
    Button attachButton;
    @FXML
    Circle circle;
    @FXML
    TextArea typingMessage;
    @FXML
    TextField imageUrl;
    @FXML
    private VBox vbox;
    @FXML
    Label name;
    @FXML
    Button sendButton;
    public void switchToPrivateChatScene() throws IOException {
        Parent root= FXMLLoader.load(getClass().getResource("PrivateChatScene.fxml"));
        stage = (Stage) name.getScene().getWindow();
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
    public void sendMessage(ActionEvent actionEvent) throws IOException,SQLException {
        if(MainController.isInEditMode){
            MainController.isInEditMode=false;
            int id = MainController.editedMessageId;
            //update message text in database
            JDBC jdbc = new JDBC();
            jdbc.setInfo("UPDATE messages SET Text='"+typingMessage.getText()
                    +"' WHERE MessageID="+id+";");
            //
            switchToPrivateChatScene();
        }
        else {
            if (!replyMode) {
                if (status == 0) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("MessageTemplate.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    if(MainController.themeMode.equals("dark")){
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplate - Copy.css").toExternalForm());
                    }
                    else{
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplate.css").toExternalForm());
                    }
                    MessageTemplateController messageTemplateController = fxmlLoader.getController();
                    messageTemplateController.setTheMessageLabelText(typingMessage.getText()
                            , findSpecificUsernameFullname(MainController.loggedInUsername), setTheTime());

                    saveMessageInDatabase(typingMessage.getText());

                    messageTemplateController.setID(getTheLastMessageID());

                    typingMessage.setText("");
                    HBox hBox=new HBox();
                    hBox.getChildren().add(anchorPane);
                    hBox.setAlignment(Pos.BASELINE_RIGHT);
                    vbox.getChildren().add(hBox);
                    VBox.setMargin(hBox, new Insets(5));

                    counter++;
                    ///SQL :
                    //پیام جدید که از کاربر با نام کاربری MainController.loggedInUsername به کاربر با نام کاربری SearchUsernameForPrivateChatController.searchedUsernameForChat در دیتابیس ذخیره شود
                    // دقت شود که دو نوع پیام داریم که در این if  پیام مدنظر متنی است . بنابراین typingMessage.getText() متن پیام میباشد .
                } else {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("MessageTemplateWithImg.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    if(MainController.themeMode.equals("dark")){

                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplateWithImg - Copy.css").toExternalForm());
                    }
                    else{
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplateWithImg.css").toExternalForm());
                    }
                    MessageTemplateWithImgController messageTemplateWithImgController = fxmlLoader.getController();
                    messageTemplateWithImgController.setTheMessageLabelText(typingMessage.getText(),
                            findSpecificUsernameFullname(MainController.loggedInUsername), setTheTime()
                            , imageAddress);

                    saveMessageInDatabase(typingMessage.getText(),imageAddress);

                    messageTemplateWithImgController.setID(getTheLastMessageID());


                    typingMessage.setText("");
                    HBox hBox=new HBox();
                    hBox.getChildren().add(anchorPane);
                    hBox.setAlignment(Pos.BASELINE_RIGHT);
                    vbox.getChildren().add(hBox);
                    VBox.setMargin(hBox, new Insets(5));
                    counter++;
                    ///SQL :
                    //پیام جدید که از کاربر با نام کاربری MainController.loggedInUsername به کاربر با نام کاربری SearchUsernameForPrivateChatController.searchedUsernameForChat در دیتابیس ذخیره شود
                    //دقت شود که دو نوع پیام داریم که در این else  پیام مدنظر شامل عکس و کپشن عکس است . بناربراین typingMessage.getText() کپشن پیام است
                    // و imageUrl.getText() آدرس عکس پیام است
                    status = 0;

                }
            } else {
                replyMode = false;
                if (repliedMessageType.equals("text")) {
                    if (status == 0) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedMessageTemplate.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplate - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplate.css").toExternalForm());
                        }
                        RepliedMessageTemplateController repliedMessageTemplateController = fxmlLoader.getController();
                        repliedMessageTemplateController.setData(getMessageTextById(repliedMessageId),
                                findSpecificUsernameFullname(MainController.loggedInUsername) + " Replied To " +
                                        "(" + findSpecificUsernameFullname(getSenderUsernameByMessageId(repliedMessageId))
                                        + ")", setTheTime()
                                , typingMessage.getText());

                        saveReplyMessageInDatabase(typingMessage.getText(), repliedMessageId);

                        repliedMessageTemplateController.setID(getTheLastMessageID());

                        typingMessage.setText("");
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        hBox.setAlignment(Pos.BASELINE_RIGHT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));
                        switchToPrivateChatScene();
                        counter++;
                    } else {
                        status = 0;
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedMessageTemplateWithImg.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplateWithImg - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplateWithImg.css").toExternalForm());
                        }
                        RepliedMessageTemplateWithImgController repliedMessageTemplateWithImgController = fxmlLoader.getController();
                        repliedMessageTemplateWithImgController.setData(getMessageTextById(repliedMessageId),
                                findSpecificUsernameFullname(MainController.loggedInUsername) + " Replied To " +
                                        "(" + findSpecificUsernameFullname(getSenderUsernameByMessageId(repliedMessageId))
                                        + ")", setTheTime()
                                , typingMessage.getText(), imageAddress);

                        saveReplyMessageInDatabase(typingMessage.getText(), imageAddress, repliedMessageId);

                        repliedMessageTemplateWithImgController.setID(getTheLastMessageID());

                        typingMessage.setText("");
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        hBox.setAlignment(Pos.BASELINE_RIGHT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));
                        switchToPrivateChatScene();
                        counter++;

                    }
                } else {
                    if (status == 0) {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedImgMessageTemplate.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplate - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplate.css").toExternalForm());
                        }
                        RepliedImgMessageTemplateController repliedImgMessageTemplateController = fxmlLoader.getController();
                        repliedImgMessageTemplateController.setData(getMessageTextById(repliedMessageId),
                                findSpecificUsernameFullname(MainController.loggedInUsername) + " Replied To " +
                                        "(" + findSpecificUsernameFullname(getSenderUsernameByMessageId(repliedMessageId))
                                        + ")", setTheTime()
                                , typingMessage.getText(), getMessageImageUrlById(repliedMessageId));

                        saveReplyMessageInDatabase(typingMessage.getText(), repliedMessageId);

                        repliedImgMessageTemplateController.setID(getTheLastMessageID());

                        typingMessage.setText("");
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        hBox.setAlignment(Pos.BASELINE_RIGHT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));
                        switchToPrivateChatScene();
                        counter++;
                    } else {
                        status = 0;
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedImgMessageTemplateWithImg.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplateWithImg - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplateWithImg.css").toExternalForm());
                        }
                        RepliedImgMessageTemplateWithImgController repliedImgMessageTemplateWithImgController = fxmlLoader.getController();
                        repliedImgMessageTemplateWithImgController.setData(getMessageTextById(repliedMessageId),
                                findSpecificUsernameFullname(MainController.loggedInUsername) + " Replied To " +
                                        "(" + findSpecificUsernameFullname(getSenderUsernameByMessageId(repliedMessageId))
                                        + ")", setTheTime()
                                , typingMessage.getText(), getMessageImageUrlById(repliedMessageId)
                                , imageAddress);

                        saveReplyMessageInDatabase(typingMessage.getText(), imageAddress, repliedMessageId);

                        repliedImgMessageTemplateWithImgController.setID(getTheLastMessageID());

                        typingMessage.setText("");
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        hBox.setAlignment(Pos.BASELINE_RIGHT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));
                        switchToPrivateChatScene();
                        counter++;

                    }
                }
            }
        }
    }
    public int RepliedMessageID(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ReplyTo FROM messages WHERE" +
                " MessageID="+ID+";");
        while(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }
    public void saveMessageInDatabase(String Text,String ImageSource) throws SQLException {
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO Messages (Time,Text,ImageSource,SenderName,ReceiverName) VALUES ('"
                +setTheTime()+"','"+Text+"','"+ImageSource+"','"+MainController.loggedInUsername
                +"','"+SearchUsernameForPrivateChatController.searchedUsernameForChat+"');");

    }
    public void saveMessageInDatabase(String Text) throws SQLException{
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO Messages (Time,Text,SenderName,ReceiverName) VALUES ('"
                +setTheTime()+"','"+Text+"','"+MainController.loggedInUsername
                +"','"+SearchUsernameForPrivateChatController.searchedUsernameForChat+"');");
    }
    public void saveReplyMessageInDatabase(String Text,String ImageSource,int repliedID) throws SQLException {
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO Messages (Time,Text,ImageSource,SenderName,ReceiverName,ReplyTo) VALUES ('"
                +setTheTime()+"','"+Text+"','"+ImageSource+"','"+MainController.loggedInUsername
                +"','"+SearchUsernameForPrivateChatController.searchedUsernameForChat+"',"+repliedID+");");

    }
    public void saveReplyMessageInDatabase(String Text,int repliedID) throws SQLException{
        JDBC jdbc = new JDBC();
        jdbc.setInfo("INSERT INTO Messages (Time,Text,SenderName,ReceiverName,ReplyTo) VALUES ('"
                +setTheTime()+"','"+Text+"','"+MainController.loggedInUsername
                +"','"+SearchUsernameForPrivateChatController.searchedUsernameForChat+"',"+repliedID+");");
    }
    public int getTheLastMessageID() throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT MAX(MessageID) FROM messages");
        while(resultSet.next())
        return resultSet.getInt(1);
        return 0;
    }
    public String getMessageTextById(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text FROM messages WHERE MessageID="+ID+";");
        while(resultSet.next())
            return resultSet.getString(1);
        return "";
    }
    public String getMessageImageUrlById(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ImageSource FROM messages WHERE" +
                " MessageID="+ID+";");
        while(resultSet.next())
            return resultSet.getString(1);
        return "";
    }
    public String getSenderUsernameByMessageId(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT SenderName FROM messages WHERE" +
                " MessageID="+ID+";");
        while(resultSet.next())
            return resultSet.getString(1);
        return "";
    }
    public String setTheTime(){
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter currentTimeForEdit = DateTimeFormatter.ofPattern("    HH:mm");
        String formattedTime = currentTime.format(currentTimeForEdit).toString();
        return formattedTime;
    }
    public String findSpecificUsernameFullname( String userName) throws SQLException{
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
    public void editModeOn() throws SQLException{
        int id = MainController.editedMessageId;
        JDBC jdbc = new JDBC();
        String text = "2";
        ResultSet resultSet = jdbc.getInfo("SELECT Text FROM messages WHERE MessageID="+id+";");
        while(resultSet.next())
        text = resultSet.getString(1);
        typingMessage.setText(text);
        //sendButton.setText("Edit");

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /////////////////
        FileChooser fil_chooser = new FileChooser();
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e)
                    {

                        // get the file selected
                        File file = fil_chooser.showOpenDialog(stage);

                        if (file != null) {

                                String str=file.toURI().toString();

                                imageAddress=str;
                                status=1;

                        }
                    }
                };
        attachButton.setOnAction(event);
        /////////////////
        try {
            if(!profilePicUrl(SearchUsernameForPrivateChatController.searchedUsernameForChat).equals("")){
                File file = new File(profilePicUrl(SearchUsernameForPrivateChatController.searchedUsernameForChat));
                Image image = new Image((profilePicUrl(SearchUsernameForPrivateChatController.searchedUsernameForChat)));
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
       if(MainController.isInEditMode2){
           MainController.isInEditMode2=false;
           try {
               editModeOn();
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
        try {
            name.setText(findSpecificUsernameFullname(
                    SearchUsernameForPrivateChatController.searchedUsernameForChat));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            loadPreviousMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       // imageUrl.setVisible(false);
        status=0;

        //////////////////////////
        if(MainController.isInEditMode) {
            sendButton.getStylesheets().clear();
            if(MainController.themeMode.equals("dark"))
                sendButton.getStylesheets().add(getClass().getResource("editButton-Dark.css").toExternalForm());
            else
                sendButton.getStylesheets().add(getClass().getResource("editButton.css").toExternalForm());
        }
        else if(PrivateChatSceneController.replyMode) {
            sendButton.getStylesheets().clear();
            if(MainController.themeMode.equals("dark"))
                sendButton.getStylesheets().add(getClass().getResource("replyButton-Dark.css").toExternalForm());
            else
                sendButton.getStylesheets().add(getClass().getResource("replyButton.css").toExternalForm());
        }
        else if(!MainController.isInEditMode && !PrivateChatSceneController.replyMode){
            sendButton.getStylesheets().clear();
            if(MainController.themeMode.equals("dark"))
                sendButton.getStylesheets().add(getClass().getResource("sendButton-Dark.css").toExternalForm());
            else
                sendButton.getStylesheets().add(getClass().getResource("sendButton.css").toExternalForm());
        }
    }
    public boolean isReplyToMessage(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ReplyTo FROM messages WHERE MessageID="+ID);
        while (resultSet.next()){
            if(resultSet.getInt(1) == -1)
                return false;
        }
        return true;
    }
    public boolean hasImage(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ImageSource FROM messages WHERE MessageID="+ID);
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
    public boolean isForwarded(int ID) throws SQLException{
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT ForwardedFrom FROM" +
                " messages WHERE MessageID="+ID+";");
        while (resultSet.next()){
            if(resultSet.getString(1) == null)
                return false;
        }
        return true;
    }
    public void searchMessage() throws IOException {
        Stage stage = new Stage();
        Parent root= FXMLLoader.load(getClass().getResource("SearchMessage.fxml"));
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
    public void loadPreviousMessages() throws IOException,SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT Text,ImageSource,Time,SenderName,MessageID,ForwardedFrom" +
                " FROM Messages WHERE " +
                "(SenderName='"+SearchUsernameForPrivateChatController.searchedUsernameForChat+"' AND" +
                " ReceiverName='"+MainController.loggedInUsername+"')" +
                "OR (SenderName='"+MainController.loggedInUsername+
                "' AND ReceiverName='"+SearchUsernameForPrivateChatController.searchedUsernameForChat
                +"') ORDER BY MessageID ASC");
        while(resultSet.next()) {
            if(!isReplyToMessage(resultSet.getInt(5))) {
                String forwardedFrom = "";
                if(isForwarded(resultSet.getInt(5))){
                    forwardedFrom = " Forwarded From "
                            +findSpecificUsernameFullname(resultSet.getString(6));
                }
                if (resultSet.getString(2) == null) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("MessageTemplate.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    if(MainController.themeMode.equals("dark")){
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplate - Copy.css").toExternalForm());
                    }
                    else{
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplate.css").toExternalForm());
                    }
                    MessageTemplateController messageTemplateController = fxmlLoader.getController();
                    messageTemplateController.setTheMessageLabelText(resultSet.getString(1),
                            findSpecificUsernameFullname(resultSet.getString(4))+forwardedFrom
                            , resultSet.getString(3));

                    messageTemplateController.setID(resultSet.getInt(5));
                    if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        if(resultSet.getString(4).equals(MainController.loggedInUsername))
                        hBox.setAlignment(Pos.BASELINE_RIGHT);
                        else
                            hBox.setAlignment(Pos.BASELINE_LEFT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));

                        counter++;
                    }
                    ///SQL :
                    //پیام جدید که از کاربر با نام کاربری MainController.loggedInUsername به کاربر با نام کاربری SearchUsernameForPrivateChatController.searchedUsernameForChat در دیتابیس ذخیره شود
                    // دقت شود که دو نوع پیام داریم که در این if  پیام مدنظر متنی است . بنابراین typingMessage.getText() متن پیام میباشد .
                } else {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("MessageTemplateWithImg.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    if(MainController.themeMode.equals("dark")){
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplateWithImg - Copy.css").toExternalForm());
                    }
                    else{
                        anchorPane.getStylesheets().clear();
                        anchorPane.getStylesheets().add(getClass().getResource("MessageTemplateWithImg.css").toExternalForm());
                    }
                    MessageTemplateWithImgController messageTemplateWithImgController = fxmlLoader.getController();
                    messageTemplateWithImgController.setTheMessageLabelText(resultSet.getString(1),
                            findSpecificUsernameFullname(resultSet.getString(4))+forwardedFrom
                            , resultSet.getString(3), resultSet.getString(2));

                    messageTemplateWithImgController.setID(resultSet.getInt(5));
                    if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                        HBox hBox=new HBox();
                        hBox.getChildren().add(anchorPane);
                        if(resultSet.getString(4).equals(MainController.loggedInUsername))
                            hBox.setAlignment(Pos.BASELINE_RIGHT);
                        else
                            hBox.setAlignment(Pos.BASELINE_LEFT);
                        vbox.getChildren().add(hBox);
                        VBox.setMargin(hBox, new Insets(5));
                        counter++;
                    }
                    ///SQL :
                    //پیام جدید که از کاربر با نام کاربری MainController.loggedInUsername به کاربر با نام کاربری SearchUsernameForPrivateChatController.searchedUsernameForChat در دیتابیس ذخیره شود
                    //دقت شود که دو نوع پیام داریم که در این else  پیام مدنظر شامل عکس و کپشن عکس است . بناربراین typingMessage.getText() کپشن پیام است
                    // و imageUrl.getText() آدرس عکس پیام است
                    status = 0;
                   // imageUrl.setVisible(false);
                   // imageUrl.setText("");
                }
            }
            else{
                if(hasImage(resultSet.getInt(5))){
                    if(hasImage(RepliedMessageID(resultSet.getInt(5)))){
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedImgMessageTemplateWithImg.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplateWithImg - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplateWithImg.css").toExternalForm());
                        }
                        RepliedImgMessageTemplateWithImgController repliedImgMessageTemplateWithImgController  = fxmlLoader.getController();
                        repliedImgMessageTemplateWithImgController.setRepliedMessageID(RepliedMessageID(resultSet.getInt(5)));
                        repliedImgMessageTemplateWithImgController.setData(getMessageTextById(RepliedMessageID(resultSet.getInt(5))),
                                findSpecificUsernameFullname(resultSet.getString(4))+" Replied To " +
                                        "("+findSpecificUsernameFullname(getSenderUsernameByMessageId(RepliedMessageID(resultSet.getInt(5))))
                                        +")",resultSet.getString(3)
                                ,resultSet.getString(1), getMessageImageUrlById(RepliedMessageID(resultSet.getInt(5)))
                                , resultSet.getString(2))  ;

                        repliedImgMessageTemplateWithImgController.setID(resultSet.getInt(5));

                        typingMessage.setText("");
                        if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                            HBox hBox=new HBox();
                            hBox.getChildren().add(anchorPane);
                            if(resultSet.getString(4).equals(MainController.loggedInUsername))
                                hBox.setAlignment(Pos.BASELINE_RIGHT);
                            else
                                hBox.setAlignment(Pos.BASELINE_LEFT);
                            vbox.getChildren().add(hBox);
                            VBox.setMargin(hBox, new Insets(5));

                            counter++;
                        }

                    }
                    else{
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedMessageTemplateWithImg.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplateWithImg - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplateWithImg.css").toExternalForm());
                        }
                        RepliedMessageTemplateWithImgController repliedMessageTemplateWithImgController  = fxmlLoader.getController();
                        repliedMessageTemplateWithImgController.setRepliedMessageID(RepliedMessageID(resultSet.getInt(5)));
                        repliedMessageTemplateWithImgController.setData(getMessageTextById(RepliedMessageID(resultSet.getInt(5))),
                                findSpecificUsernameFullname(resultSet.getString(4))+" Replied To " +
                                        "("+findSpecificUsernameFullname(getSenderUsernameByMessageId(RepliedMessageID(resultSet.getInt(5))))
                                        +")",resultSet.getString(3)
                                ,resultSet.getString(1),resultSet.getString(2))  ;


                        repliedMessageTemplateWithImgController.setID(resultSet.getInt(5));


                        typingMessage.setText("");
                        if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                            HBox hBox=new HBox();
                            hBox.getChildren().add(anchorPane);
                            if(resultSet.getString(4).equals(MainController.loggedInUsername))
                                hBox.setAlignment(Pos.BASELINE_RIGHT);
                            else
                                hBox.setAlignment(Pos.BASELINE_LEFT);
                            vbox.getChildren().add(hBox);
                            VBox.setMargin(hBox, new Insets(5));

                            counter++;
                        }

                    }
                }
                else{
                    if(hasImage(RepliedMessageID(resultSet.getInt(5)))){
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedImgMessageTemplate.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplate - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedImgMessageTemplate.css").toExternalForm());
                        }
                        RepliedImgMessageTemplateController repliedImgMessageTemplateController= fxmlLoader.getController();
                        repliedImgMessageTemplateController.setRepliedMessageID(RepliedMessageID(resultSet.getInt(5)));
                        repliedImgMessageTemplateController.setData(getMessageTextById(RepliedMessageID(resultSet.getInt(5))),
                                findSpecificUsernameFullname(resultSet.getString(4))+" Replied To " +
                                        "("+findSpecificUsernameFullname(getSenderUsernameByMessageId(RepliedMessageID(resultSet.getInt(5))))
                                        +")",resultSet.getString(3)
                                ,resultSet.getString(1), getMessageImageUrlById(RepliedMessageID(resultSet.getInt(5))))  ;


                        repliedImgMessageTemplateController.setID(resultSet.getInt(5));

                        typingMessage.setText("");
                        if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                            HBox hBox=new HBox();
                            hBox.getChildren().add(anchorPane);
                            if(resultSet.getString(4).equals(MainController.loggedInUsername))
                                hBox.setAlignment(Pos.BASELINE_RIGHT);
                            else
                                hBox.setAlignment(Pos.BASELINE_LEFT);
                            vbox.getChildren().add(hBox);
                            VBox.setMargin(hBox, new Insets(5));

                            counter++;
                        }
                    }
                    else{
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("RepliedMessageTemplate.fxml"));
                        AnchorPane anchorPane = fxmlLoader.load();
                        if(MainController.themeMode.equals("dark")){
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplate - Copy.css").toExternalForm());
                        }
                        else{
                            anchorPane.getStylesheets().clear();
                            anchorPane.getStylesheets().add(getClass().getResource("RepliedMessageTemplate.css").toExternalForm());
                        }
                        RepliedMessageTemplateController repliedMessageTemplateController = fxmlLoader.getController();
                        repliedMessageTemplateController.setRepliedMessageID(RepliedMessageID(resultSet.getInt(5)));
                        repliedMessageTemplateController.setData(getMessageTextById(RepliedMessageID(resultSet.getInt(5))),
                                findSpecificUsernameFullname(resultSet.getString(4))+" Replied To " +
                                        "("+findSpecificUsernameFullname(getSenderUsernameByMessageId(RepliedMessageID(resultSet.getInt(5))))
                                        +")",resultSet.getString(3)
                                ,resultSet.getString(1))  ;

                        repliedMessageTemplateController.setID(resultSet.getInt(5));


                        typingMessage.setText("");
                        if(!checkIfTheMessageIsDeleted(resultSet.getInt(5))) {
                            HBox hBox=new HBox();
                            hBox.getChildren().add(anchorPane);
                            if(resultSet.getString(4).equals(MainController.loggedInUsername))
                                hBox.setAlignment(Pos.BASELINE_RIGHT);
                            else
                                hBox.setAlignment(Pos.BASELINE_LEFT);
                            vbox.getChildren().add(hBox);
                            VBox.setMargin(hBox, new Insets(5));
                            counter++;
                        }
                    }
                }
            }
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
}
