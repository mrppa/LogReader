package com.mrppa.logreader.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.ui.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/main.fxml"));

		Pane pane = fxmlLoader.load();
		MainController mainController = fxmlLoader.getController();

		primaryStage = new Stage();
		mainController.setStage(primaryStage);

		primaryStage.setScene(new Scene(pane, 800, 500));
		primaryStage.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		primaryStage.setTitle("Log Reader");
		primaryStage.show();
		mainController.setActions();

	}
}
