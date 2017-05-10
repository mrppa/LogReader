package com.mrppa.logreader.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.Line;
import com.mrppa.logreader.reader.LineReader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final int NU_OF_REC = 40;
	private MenuItem exitApp;
	private MenuItem openFile;
	private Stage primaryStage;
	private File selectedLogFile;
	private LineReader lineReader;
	private VBox topContainer;
	private List<Line> lineList;
	private List<TextField> fieldList;
	private Button pageStart;
	private Button pageEnd;
	private Button copyBtn;
	private Button btnSearch;
	private Stage searchResultStage;
	private TextField searchField;

	final Clipboard clipboard = Clipboard.getSystemClipboard();

	protected LineReader getLineReader() {
		return lineReader;
	}

	protected void setLineReader(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		BorderPane root = new BorderPane();
		topContainer = new VBox();
		MenuBar mainMenu = new MenuBar();
		ToolBar toolBar = new ToolBar();
		topContainer.getChildren().add(mainMenu);
		topContainer.getChildren().add(toolBar);
		root.setTop(topContainer);
		topContainer.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		Menu file = new Menu("File");
		mainMenu.getMenus().add(file);

		openFile = new MenuItem("Open File");
		file.getItems().add(openFile);

		exitApp = new MenuItem("Exit");
		file.getItems().add(exitApp);

		pageStart = new Button();
		pageStart.setText("Start");
		toolBar.getItems().add(pageStart);

		pageEnd = new Button();
		pageEnd.setText("End");
		toolBar.getItems().add(pageEnd);

		Separator separator1 = new Separator();
		toolBar.getItems().add(separator1);

		searchField = new TextField();
		toolBar.getItems().add(searchField);

		btnSearch = new Button();
		btnSearch.setText("Search");
		toolBar.getItems().add(btnSearch);

		Separator separator2 = new Separator();
		toolBar.getItems().add(separator2);
		
		copyBtn = new Button();
		copyBtn.setText("Copy");
		toolBar.getItems().add(copyBtn);

		fieldList = new ArrayList<>();
		for (int i = 0; i < NU_OF_REC; i++) {
			TextField tf = new TextField("");
			tf.setEditable(false);
			tf.getStyleClass().add("logcontent_textbox");
			topContainer.getChildren().add(tf);
			fieldList.add(tf);
		}

		primaryStage.setTitle("LogViewer");
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();
		this.setActions();

	}

	public void setActions() {
		// Exit Menu
		this.exitApp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		// Open File Menu
		this.openFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Log File");
				selectedLogFile = fileChooser.showOpenDialog(primaryStage);
				LOG.info("FILE FETCHED\t" + selectedLogFile.getAbsolutePath());
				try {
					lineReader = new LineReader(selectedLogFile.getAbsolutePath(), 1024);
					lineList = new LinkedList<Line>();
					Line line = new Line();
					line.setEndPos(-1);
					for (int i = 0; i < NU_OF_REC; i++) {
						line = lineReader.getNextPosition(line.getEndPos() + 1);
						lineList.add(line);
					}
					refreshText();
				} catch (IOException e) {
					LOG.error("FILE READ ERROR", e);
				}
			}
		});

		// Scroll Event
		primaryStage.getScene().setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					LOG.debug("SCROLLING UP");
					if (lineList.size() > 1) {
						Line firstLine = lineList.remove(0);
						Line lastLine = lineList.get(lineList.size() - 1);
						try {
							Line line = lineReader.getNextPosition(lastLine.getEndPos() + 1);
							if (line != null) {
								lineList.add(line);
							}

						} catch (IOException e) {
							LOG.error("ERROR SCROLLING UP");
						}
						refreshText();
					}
				} else {
					LOG.debug("SCROLLING DOWN");
					Line firstLine = lineList.get(0);
					try {
						Line line = lineReader.getPrevPosition(firstLine.getStartPos() - 1);
						if (line != null) {
							lineList.add(0, line);
							if (lineList.size() > fieldList.size()) {
								Line lastLine = lineList.remove(lineList.size() - 1);
							}
						}

					} catch (IOException e) {
						LOG.error("ERROR SCROLLING UP");
					}
					refreshText();
				}

			}
		});

		// Start Nav Button
		this.pageStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					lineList.clear();
					Line line = new Line();
					line.setEndPos(-1);
					for (int i = 0; i < NU_OF_REC; i++) {
						line = lineReader.getNextPosition(line.getEndPos() + 1);
						lineList.add(line);
					}
					refreshText();
				} catch (IOException e) {
					LOG.error("FILE READ ERROR", e);
				}
			}
		});

		// End Nav Button
		this.pageEnd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					lineList.clear();
					Line line1 = lineReader.getNearestPrevLine(lineReader.getNuOfBytes() - 1);
					lineList.add(line1);
					refreshText();
				} catch (IOException e) {
					LOG.error("FILE READ ERROR", e);
				}
			}
		});

		// Copy Button
		this.copyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				StringBuilder sb = new StringBuilder();
				for (Line line : lineList) {
					sb.append(line.getContent());
				}
				ClipboardContent content = new ClipboardContent();
				content.putString(sb.toString());
				clipboard.setContent(content);
			}
		});

		// Search Button
		this.btnSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FXMLLoader fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(getClass().getResource("/SearchResult.fxml"));

				VBox vbox = null;
				try {
					vbox = fxmlLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				SearchController searchController = fxmlLoader.getController();
				searchController.setSearchString(searchField.getText());
				searchController.setLineReader(lineReader);
				searchController.setMain(Main.this);
				searchController.search();

				searchResultStage = new Stage();
				searchResultStage.setScene(new Scene(vbox, 800, 500));
				searchResultStage.setTitle("SearchResult");
				searchResultStage.show();

			}
		});

	}

	private void refreshText() {
		for (int i = 0; i < fieldList.size(); i++) {
			TextField tf = fieldList.get(i);
			if (i >= lineList.size()) {
				tf.setText("");
			} else {
				Line line = lineList.get(i);
				if (line != null) {
					tf.setText(line.getContent());
				}
			}
		}
	}

	protected synchronized void goAbsolutePosition(Long position) {
		try {
			lineList.clear();
			Line line = new Line();
			line = lineReader.getNearestPrevLine(position - 1);
			for (int i = 0; i < NU_OF_REC; i++) {
				line = lineReader.getNextPosition(line.getEndPos() +1);
				if(line ==null)
				{
					break;
				}
				lineList.add(line);
			}
			refreshText();
		} catch (IOException e) {
			LOG.error("FILE READ ERROR", e);
		}
	}

}
