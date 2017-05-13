package com.mrppa.logreader.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.mrppa.logreader.reader.LineReader;
import com.mrppa.logreader.reader.Progress;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class SearchController implements Initializable {

	@FXML
	private Label searchInfo;
	@FXML
	private ListView<String> searchList;

	private String searchString = "";

	private LineReader lineReader;

	private MainController mainController;

	protected Label getSearchInfo() {
		return searchInfo;
	}

	protected void setSearchInfo(Label searchInfo) {
		this.searchInfo = searchInfo;
	}

	protected ListView<String> getSearchList() {
		return searchList;
	}

	protected void setSearchList(ListView<String> searchList) {
		this.searchList = searchList;
	}

	protected String getSearchString() {
		return searchString;
	}

	protected void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	protected LineReader getLineReader() {
		return lineReader;
	}

	protected void setLineReader(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchInfo.setText("Loading");
		this.searchList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Long item = Long.parseLong(searchList.getSelectionModel().getSelectedItem());
				System.out.println("clicked on " + item);
				mainController.loadLinesFromPos(item);
			}

		});
	}

	public void search() {
		searchInfo.setText("Search Results for " + searchString);

		Set<Long> resultSet = null;
		try {
			resultSet = lineReader.getCachedLogReader().search(this.searchString, 0,
					lineReader.getCachedLogReader().getAvailableChunks(), new Progress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		searchInfo.setText(resultSet.size() + " records found for search text \t:" + searchString);

		List<Long> searchListObj = new LinkedList<>();
		for (Long resultItem : resultSet) {
			searchListObj.add(resultItem);
		}
		Collections.sort(searchListObj);

		for (Long resultItem : searchListObj) {
			searchList.getItems().add(Long.toString(resultItem));
		}
	}

}
