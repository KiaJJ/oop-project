package com.example.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ShowDailyViewsController implements Initializable {
    @FXML
    ListView date;
    @FXML
    ListView views;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userName = MainController.loggedInUsername;
        ArrayList<String> dateArray = new ArrayList<>();
        ArrayList <Integer> viewsArray = new ArrayList<>();
        ////SQL
        JDBC jdbc = new JDBC();
        ResultSet resultSet;
        try {
            resultSet = jdbc.getInfo("SELECT Date,NumberOfDailyViews" +
                    " FROM businessAccountDailyReport" +
                    " WHERE UserName='"+userName+"';");
            while (resultSet.next()){
                dateArray.add(resultSet.getString(1));
                viewsArray.add(resultSet.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ////
        ObservableList<String> observableList = FXCollections.observableList(dateArray);
        date.setItems(observableList);
        ObservableList<Integer> observableList2 = FXCollections.observableList(viewsArray);
        views.setItems(observableList2);
    }
}
