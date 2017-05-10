package com.mrppa.logreader.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.Line;
import com.mrppa.logreader.reader.LineReader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	private static final int NU_OF_REC = 40;
	private final Clipboard clipboard = Clipboard.getSystemClipboard();

	@FXML
	private MenuItem openFileMnuItem;
	@FXML
	private MenuItem exitFileMnuItem;
	@FXML
	private Button pageStartBtn;
	@FXML
	private Button pageEndBtn;
	@FXML
	private TextField searchTxtField;
	@FXML
	private Button searchBtn;
	@FXML
	private Button copyBtn;
	@FXML
	private VBox textDispArea;

	private Stage stage;

	private List<TextField> fieldList;
	private List<Line> lineList;

	private File selectedLogFile;
	private LineReader lineReader;

	public MenuItem getOpenFileMnuItem() {
		return openFileMnuItem;
	}

	public void setOpenFileMnuItem(MenuItem openFileMnuItem) {
		this.openFileMnuItem = openFileMnuItem;
	}

	public MenuItem getExitFileMnuItem() {
		return exitFileMnuItem;
	}

	public void setExitFileMnuItem(MenuItem exitFileMnuItem) {
		this.exitFileMnuItem = exitFileMnuItem;
	}

	public Button getPageStartBtn() {
		return pageStartBtn;
	}

	public void setPageStartBtn(Button pageStartBtn) {
		this.pageStartBtn = pageStartBtn;
	}

	public Button getPageEndBtn() {
		return pageEndBtn;
	}

	public void setPageEndBtn(Button pageEndBtn) {
		this.pageEndBtn = pageEndBtn;
	}

	public TextField getSearchTxtField() {
		return searchTxtField;
	}

	public void setSearchTxtField(TextField searchTxtField) {
		this.searchTxtField = searchTxtField;
	}

	public Button getSearchBtn() {
		return searchBtn;
	}

	public void setSearchBtn(Button searchBtn) {
		this.searchBtn = searchBtn;
	}

	public Button getCopyBtn() {
		return copyBtn;
	}

	public void setCopyBtn(Button copyBtn) {
		this.copyBtn = copyBtn;
	}

	public VBox getTextDispArea() {
		return textDispArea;
	}

	public void setTextDispArea(VBox textDispArea) {
		this.textDispArea = textDispArea;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.populateTextDispArea();
	}

	/**
	 * Populate dynamic text fields
	 */
	private void populateTextDispArea() {
		this.fieldList = new ArrayList<>();
		for (int i = 0; i < NU_OF_REC; i++) {
			TextField tf = new TextField("");
			tf.setEditable(false);
			tf.getStyleClass().add("logcontent_textbox");
			this.textDispArea.getChildren().add(tf);
			fieldList.add(tf);
		}
	}

	/**
	 * Set the action of each components
	 */
	protected void setActions() {
		// Exit Menu
		this.exitFileMnuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		// Open File Menu
		this.openFileMnuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Log File");
				selectedLogFile = fileChooser.showOpenDialog(MainController.this.stage);
				LOG.info("FILE FETCHED\t" + selectedLogFile.getAbsolutePath());
				try {
					MainController.this.lineReader = new LineReader(selectedLogFile.getAbsolutePath(), 1024);
					lineList = new LinkedList<Line>();
					MainController.this.loadLinesFromPos(0);
					MainController.this.refreshText();
				} catch (IOException e) {
					LOG.error("FILE READ ERROR", e);
				}
			}
		});

		// Scroll Event
		this.stage.getScene().setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					LOG.debug("SCROLLING UP");
					if (lineList.size() > 1) {
						lineList.remove(0);
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
								lineList.remove(lineList.size() - 1);
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
		this.pageStartBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MainController.this.loadLinesFromPos(0);
				MainController.this.refreshText();
			}
		});

		// End Nav Button
		this.pageEndBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MainController.this.loadLinesFromPos(lineReader.getNuOfBytes() - 1);
				MainController.this.refreshText();
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
		this.searchBtn.setOnAction(new EventHandler<ActionEvent>() {
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
				searchController.setSearchString(searchTxtField.getText());
				searchController.setLineReader(lineReader);
				searchController.setMainController(MainController.this);
				searchController.search();

				Stage searchResultStage = new Stage();
				searchResultStage.setScene(new Scene(vbox, 800, 500));
				searchResultStage.setTitle("SearchResult");
				searchResultStage.show();

			}
		});

	}

	/**
	 * Fill the textfields based on current lines
	 */
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

	/**
	 * Clear and load lines from given absolute position
	 * 
	 * @param absolutePos
	 */
	protected void loadLinesFromPos(long absolutePos) {
		try {
			lineList.clear();
			Line line = new Line();
			line = lineReader.getNearestPrevLine(absolutePos);
			long nextLineStartPos = 0l;
			if (line != null) {
				nextLineStartPos = line.getStartPos();
			}
			for (int i = 0; i < NU_OF_REC; i++) {
				line = lineReader.getNextPosition(nextLineStartPos);
				if (line == null) {
					break;
				}
				nextLineStartPos = line.getEndPos() + 1;
				lineList.add(line);
			}
			refreshText();
		} catch (IOException e) {
			LOG.error("FILE READ ERROR", e);
		}
	}

}
