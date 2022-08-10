package com.example.oopproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BusinessPostDetailController implements Initializable {
    @FXML
    private CategoryAxis days;
    @FXML
    private NumberAxis numbers;
    @FXML
    private BarChart<String, Number > chart;

    @FXML
    ListView date;

    @FXML
    ListView likes;

    @FXML
    ListView views;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int postID = PostTemplateController.businessPostId;
        ArrayList <String> dateArray = new ArrayList<>();
        ArrayList <Integer> likesArray = new ArrayList<>();
        ArrayList <Integer> viewsArray = new ArrayList<>();
        XYChart.Series<String , Number> likesAxis = new XYChart.Series<>();
        XYChart.Series<String , Number> viewsAxis = new XYChart.Series<>();
        likesAxis.setName("Likes");
        viewsAxis.setName("Views");
        ////SQL
        JDBC jdbc = new JDBC();
        ResultSet resultSet;
        try {
           resultSet = jdbc.getInfo("SELECT Date,NumberOfDailyLikes,NumberOfDailyViews" +
                   " FROM businessPostDailyReport" +
                    " WHERE PostID="+postID+";");
            while (resultSet.next()){
                dateArray.add(resultSet.getString(1));
                likesArray.add(resultSet.getInt(2));
                viewsArray.add(resultSet.getInt(3));
                likesAxis.getData().add(new XYChart.Data<>(resultSet.getString(1) ,resultSet.getInt(2) ));
                viewsAxis.getData().add(new XYChart.Data<>(resultSet.getString(1) ,resultSet.getInt(3) ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ////
        ObservableList<String> observableList = FXCollections.observableList(dateArray);
        date.setItems(observableList);
        ObservableList<Integer> observableList2 = FXCollections.observableList(likesArray);
        likes.setItems(observableList2);
        ObservableList<Integer> observableList3 = FXCollections.observableList(viewsArray);
        views.setItems(observableList3);
        chart.getData().addAll(likesAxis,viewsAxis);
    }
}
