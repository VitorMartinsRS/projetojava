package com.chess;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        GameController controller = new GameController();
        controller.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
