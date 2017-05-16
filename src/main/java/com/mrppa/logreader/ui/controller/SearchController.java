package com.mrppa.logreader.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.LineReader;
import com.mrppa.logreader.reader.Progress;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
/**
 * 
 * @author Pasindu Ariyarathna (pasindu@mrppa.com)
 *
 */
public class SearchController implements Initializable {
	private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

	@FXML
	private Label searchInfo;
	@FXML
	private ListView<String> searchList;
	@FXML
	private Label searchStatLbl;
	@FXML
	private ProgressBar searchProgressBar;

	private String searchString = "";

	private LineReader lineReader;

	private MainController mainController;
	
	private Thread searchStatThread,searchThread;
	
	private boolean shutdownSearchOpps=false;
	
	private Progress progress ;

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

	public Label getSearchStatLbl() {
		return searchStatLbl;
	}

	public void setSearchStatLbl(Label searchStatLbl) {
		this.searchStatLbl = searchStatLbl;
	}

	public ProgressBar getSearchProgressBar() {
		return searchProgressBar;
	}

	public void setSearchProgressBar(ProgressBar searchProgressBar) {
		this.searchProgressBar = searchProgressBar;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchInfo.setText("Loading");
		this.searchStatLbl.setText("Searching ...");
		this.searchProgressBar.setProgress(0d);
		this.searchList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Long item = Long.parseLong(searchList.getSelectionModel().getSelectedItem());
				System.out.println("clicked on " + item);
				
				mainController.getUiData().loadLinesFromPos(item,mainController.getLineList(),mainController.getLineReader(),MainController.NU_OF_REC );
				mainController.getUiData().refreshText(mainController.getTextDispFlow(), mainController.getLineList(), mainController.getNavSlider());
			}

		});
	}

	public void search() {
		searchInfo.setText("Search Results for " + searchString);

		progress = new Progress();

		class SearchRun implements Runnable {
			Set<Long> resultSet = null;
			boolean finishedFlag = false;

			@Override
			public void run() {
				try {
					resultSet = lineReader.getCachedLogReader().search(searchString, 0,
							lineReader.getCachedLogReader().getAvailableChunks(), progress);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					finishedFlag = true;
				}
			}
		}
		;
		SearchRun searchRun = new SearchRun();
		searchThread = new Thread(searchRun);
		searchThread.setDaemon(true);
		searchThread.start();

		Runnable searchStatRun = new Runnable() {
			@Override
			public void run() {

				while (true) {
					double progressAmt = (double)progress.getProgress() / 100;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							searchProgressBar.setProgress(progressAmt);
						}
					});

					if (searchRun.finishedFlag) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								searchInfo.setText(searchRun.resultSet.size() + " records found for search text \t:"
										+ searchString);

								List<Long> searchListObj = new LinkedList<>();
								for (Long resultItem : searchRun.resultSet) {
									searchListObj.add(resultItem);
								}
								Collections.sort(searchListObj);

								for (Long resultItem : searchListObj) {
									searchList.getItems().add(Long.toString(resultItem));
								}
								searchStatLbl.setText("Searching Completed!");
								searchProgressBar.setProgress(10d);

							}
						});

						break;
					}
					if(SearchController.this.shutdownSearchOpps)
					{
						LOG.info("BREAKING SEARCH STATUS THREAD");
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};
		searchStatThread = new Thread(searchStatRun);
		searchStatThread.setDaemon(true);
		searchStatThread.start();
	}
	
	@Override
	public void finalize(){
		LOG.info("FINALIZING SEARCH CONTROLLER");
		shutdownSearchOpps=true;
		if(this.progress!=null){
			progress.setShutDownCommand(true);
		}

	}
	


}
