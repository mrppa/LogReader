package com.mrppa.logreader.ui.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.Line;
import com.mrppa.logreader.reader.LineReader;
import com.mrppa.logreader.ui.data.UIData;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainController implements Initializable {

	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	public static final int NU_OF_REC = 60;
	private final Clipboard clipboard = Clipboard.getSystemClipboard();

	@FXML
	private MenuItem openFileMnuItem;
	@FXML
	private MenuItem exitFileMnuItem;
	@FXML
	private MenuItem abtMnuItem;
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
	@FXML
	private Hyperlink linktoSite;
	@FXML
	private Label sysStatLbl;
	@FXML
	private TextFlow textDispFlow;
	@FXML
	private Slider navSlider;

	private Stage stage;

	private List<Line> lineList;

	private File selectedLogFile;
	private LineReader lineReader;

	private Timer sysStatTimer;

	private UIData uiData;

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

	public Hyperlink getLinktoSite() {
		return linktoSite;
	}

	public void setLinktoSite(Hyperlink linktoSite) {
		this.linktoSite = linktoSite;
	}

	public Label getSysStatLbl() {
		return sysStatLbl;
	}

	public void setSysStatLbl(Label sysStatLbl) {
		this.sysStatLbl = sysStatLbl;
	}

	public TextFlow getTextDispFlow() {
		return textDispFlow;
	}

	public void setTextDispFlow(TextFlow textDispFlow) {
		this.textDispFlow = textDispFlow;
	}

	public List<Line> getLineList() {
		return lineList;
	}

	public void setLineList(List<Line> lineList) {
		this.lineList = lineList;
	}

	public LineReader getLineReader() {
		return lineReader;
	}

	public void setLineReader(LineReader lineReader) {
		this.lineReader = lineReader;
	}

	public UIData getUiData() {
		return uiData;
	}

	public void setUiData(UIData uiData) {
		this.uiData = uiData;
	}

	public Slider getNavSlider() {
		return navSlider;
	}

	public void setNavSlider(Slider navSlider) {
		this.navSlider = navSlider;
	}

	public MenuItem getAbtMnuItem() {
		return abtMnuItem;
	}

	public void setAbtMnuItem(MenuItem abtMnuItem) {
		this.abtMnuItem = abtMnuItem;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchBtn.setDisable(true);
		textDispArea.setDisable(true);
		copyBtn.setDisable(true);
		searchTxtField.setDisable(true);
		pageStartBtn.setDisable(true);
		pageEndBtn.setDisable(true);
		navSlider.setDisable(true);

		TimerTask sysStatTimerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						sysStatLbl.setText(uiData.getStstemBarStat());

					}
				});

			}
		};
		sysStatTimer = new Timer();
		sysStatTimer.scheduleAtFixedRate(sysStatTimerTask, 0, 5000);
	}

	/**
	 * Set the action of each components
	 */
	public void setActions() {
		// Exit Menu
		this.exitFileMnuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
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
					MainController.this.lineList = new LinkedList<Line>();

					uiData.loadLinesFromPos(0, MainController.this.lineList, MainController.this.lineReader,
							MainController.NU_OF_REC);
					uiData.refreshText(MainController.this.textDispFlow, MainController.this.lineList, MainController.this.navSlider);

					MainController.this.textDispArea.setDisable(false);
					MainController.this.copyBtn.setDisable(false);
					MainController.this.searchTxtField.setDisable(false);
					MainController.this.pageStartBtn.setDisable(false);
					MainController.this.pageEndBtn.setDisable(false);
					MainController.this.navSlider.setDisable(false);
					MainController.this.searchTxtField.setText("");
					MainController.this.navSlider.setMin(0d);
					MainController.this.navSlider.setMax(MainController.this.lineReader.getNuOfBytes());

				} catch (IOException e) {
					LOG.error("FILE READ ERROR", e);
				}
			}
		});
		
		// About Menu
		this.abtMnuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FXMLLoader fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(getClass().getResource("/fxml/about.fxml"));

				Pane aboutPane = null;
				try {
					aboutPane = fxmlLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Stage aboutStage = new Stage();
				aboutStage.setScene(new Scene(aboutPane));
				aboutStage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
				aboutStage.setTitle("About");
				aboutStage.initStyle(StageStyle.UTILITY);
				aboutStage.showAndWait();
			}
		});

		// Scroll Event
		this.stage.getScene().setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				if (selectedLogFile != null) {
					if (event.getDeltaY() > 0) {
						LOG.debug("SCROLLING UP");
						uiData.lineMoveUp(MainController.this.lineList, MainController.this.lineReader,
								MainController.NU_OF_REC);
						uiData.refreshText(MainController.this.textDispFlow, MainController.this.lineList, MainController.this.navSlider);
					} else {
						LOG.debug("SCROLLING DOWN");
						uiData.lineMoveDown(MainController.this.lineList, MainController.this.lineReader,
								MainController.NU_OF_REC);
						uiData.refreshText(MainController.this.textDispFlow, MainController.this.lineList, MainController.this.navSlider);
					}
				}
			}
		});

		// Start Nav Button
		this.pageStartBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				uiData.loadLinesFromPos(0, MainController.this.lineList, MainController.this.lineReader,MainController.NU_OF_REC);
				uiData.refreshText(MainController.this.textDispFlow, MainController.this.lineList, MainController.this.navSlider);
			}
		});

		// End Nav Button
		this.pageEndBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				uiData.loadLinesFromPos(lineReader.getNuOfBytes() - 1, MainController.this.lineList,
						MainController.this.lineReader, MainController.NU_OF_REC);
				uiData.refreshText(MainController.this.textDispFlow, MainController.this.lineList, MainController.this.navSlider);
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
				fxmlLoader.setLocation(getClass().getResource("/fxml/SearchResult.fxml"));

				Pane searchPane = null;
				try {
					searchPane = fxmlLoader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				SearchController searchController = fxmlLoader.getController();
				searchController.setSearchString(searchTxtField.getText());
				searchController.setLineReader(lineReader);
				searchController.setMainController(MainController.this);

				Stage searchResultStage = new Stage();
				searchResultStage.setScene(new Scene(searchPane, 800, 500));
				searchResultStage.setTitle("SearchResult");
				searchResultStage.initStyle(StageStyle.UTILITY);
				searchResultStage.show();
				searchResultStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent we) {
						LOG.info("SEARCH WINDOW CLOSING");
						searchController.finalize();
					}
				});
				searchController.search();

			}
		});

		this.searchTxtField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (MainController.this.searchTxtField.getText().trim().length() > 0) {
					searchBtn.setDisable(false);
				} else {
					searchBtn.setDisable(true);

				}

			}
		});

		// Clicking the link
		this.linktoSite.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URL("https://github.com/mrppa/LogReader").toURI());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});
		
		//Slider Change
		this.navSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				long selPos =(long) MainController.this.getNavSlider().getValue()-1;//Avoid last position to be end of file
				LOG.info("POS CHANGED\t" + selPos);
				uiData.loadLinesFromPos(selPos, MainController.this.getLineList(), MainController.this.getLineReader(),
						MainController.NU_OF_REC);
				uiData.refreshText(MainController.this.getTextDispFlow(), MainController.this.getLineList(),
						MainController.this.getNavSlider());

			}
		});

	}

	public void finalize() {
		LOG.info("FINALIZING MAIN CONTROLLER");
		if (this.sysStatTimer != null) {
			this.sysStatTimer.cancel();
		}
	}

}
