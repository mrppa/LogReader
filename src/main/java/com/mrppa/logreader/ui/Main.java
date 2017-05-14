package com.mrppa.logreader.ui;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.ui.controller.MainController;
import com.mrppa.logreader.ui.data.UIData;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private MainController mainController ;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));

		Pane pane = fxmlLoader.load();
		mainController = fxmlLoader.getController();

		primaryStage = new Stage();
		mainController.setStage(primaryStage);
		UIData uiData=new UIData();
		GenericObjectPool<Text> textPool =new GenericObjectPool<>(new TextPoolFactory());
		textPool.setMaxTotal(-1);
		textPool.setMaxIdle(-1);
		textPool.setMinIdle(0);
		uiData.setTextPool(textPool);
		mainController.setUiData(uiData);
		
		primaryStage.setScene(new Scene(pane, 800, 500));
		primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
		primaryStage.getIcons().add(new Image(getClass().getResource("/img/appicon_Small.png").toExternalForm()));

		primaryStage.setTitle("Log Reader");
		primaryStage.show();
		mainController.setActions();

	}

	@Override
	public void stop() throws Exception {
		if(mainController!=null){
			mainController.finalize();
		}
	}
}
