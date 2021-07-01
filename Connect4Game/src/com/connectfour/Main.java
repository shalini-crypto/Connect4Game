package com.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        //connected with Controller Class
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();

        //covering all the space of the Pane
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        //get the very first index of grid pane
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);




        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //create menu bar
    public  MenuBar  createMenu(){
        //File menu
        Menu fileMenu = new Menu("File");
        //File menu item
        MenuItem newGame = new MenuItem("New Game");
        //action listener for new game
        newGame.setOnAction(event -> {
            controller.resetGame();
        });

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> {
           controller.resetGame();
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Quit");
        //action listener for exit
        exitGame.setOnAction(event -> {
            exitGame();
        });

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        //Help Menu Item
        MenuItem aboutGame = new MenuItem("About Connect4");
        //action listener for about game
        aboutGame.setOnAction(event -> {
            aboutConnect4();
        });
        
        MenuItem aboutMe = new MenuItem("About Me");
       //action listener aboutMe
        aboutMe.setOnAction(event -> {
            aboutMe();
        });
        helpMenu.getItems().addAll(aboutGame, separatorMenuItem, aboutMe);





        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;

    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Shalini Singh");
        alert.setContentText("I am developer this is my first game using javaFx. I love to play with codes");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and" +
                " then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended" +
                " grid. The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four " +
                "of one's own discs. Connect Four is a solved game." +
                " The first player can always win by playing the right moves.");
        alert.show();
    }


    private void exitGame() {
        //shut down the game and all related application for this game
        Platform.exit();
        System.exit(0);
    }


    private void resetGame() {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
