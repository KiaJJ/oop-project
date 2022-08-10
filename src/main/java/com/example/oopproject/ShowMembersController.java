package com.example.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShowMembersController implements Initializable {
    @FXML
    ListView <String>members;

    public int getGroupIdByGroupName(String groupName) throws SQLException {
        JDBC jdbc = new JDBC();
        ResultSet resultSet = jdbc.getInfo("SELECT GroupID FROM groupChats " +
                "WHERE GroupName='"+groupName+"';");
        while (resultSet.next())
            return resultSet.getInt(1);
        return 0;
    }
    public void completeMembers() throws SQLException {
        int id = getGroupIdByGroupName(MainController.groupName);
        ArrayList<String> members2 = new ArrayList<>();
        JDBC jdbc = new JDBC();
        ResultSet resultSetPrime = jdbc.getInfo("SELECT Member,MemberStatus FROM isInGroupChat WHERE " +
                "GroupID="+id+";");
        while(resultSetPrime.next()){
                String status = "";
                if(resultSetPrime.getString(2).equals("Banned")) {
                    status = status +" (Banned)";
                }
                members2.add(resultSetPrime.getString(1) + status);

        }
        ObservableList<String> observableList2 = FXCollections.observableList(members2);
        members.setItems(observableList2);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            completeMembers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
