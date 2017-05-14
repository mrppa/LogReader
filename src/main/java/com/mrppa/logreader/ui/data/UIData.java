package com.mrppa.logreader.ui.data;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.Line;
import com.mrppa.logreader.reader.LineReader;
import com.mrppa.logreader.ui.controller.MainController;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UIData {
	private static final Logger LOG = LoggerFactory.getLogger(UIData.class);

	private GenericObjectPool<Text> textPool;

	public GenericObjectPool<Text> getTextPool() {
		return textPool;
	}

	public void setTextPool(GenericObjectPool<Text> textPool) {
		this.textPool = textPool;
	}

	public String getStstemBarStat() {
		LOG.trace("GET SYS INFO");
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);

		long heapSize = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		long heapMaxSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		StringBuilder sysInfo = new StringBuilder();
		sysInfo.append(numberFormat.format(heapSize) + " MB ");
		sysInfo.append("/");
		sysInfo.append(numberFormat.format(heapMaxSize) + " MB ");
		return sysInfo.toString();
	}

	/**
	 * Fill the given text flow with the line set
	 * 
	 * @param textDispFlow
	 * @param lineList
	 * @throws Exception
	 */
	public synchronized void refreshText(TextFlow textDispFlow, List<Line> lineList) {

		for (Node node : textDispFlow.getChildren()) {
			Text text = (Text) node;
			this.textPool.returnObject(text);
		}
		textDispFlow.getChildren().clear();
		LOG.debug("SIZE\t-" + lineList.size());
		for (int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			if (line != null) {
				this.refreshTextLine(textDispFlow, line);
			}
		}
	}
	
	
	protected void refreshTextLine(TextFlow textDispFlow,Line line)
	{
		List<String> lineTextList=new ArrayList<>();
		String lineCSSClass="";
		
		if (line.getContent().toUpperCase().contains("ERROR")) {
			lineCSSClass = "errorText";
		} else if (line.getContent().toUpperCase().contains("WARN")) {
			lineCSSClass = "warnText";
		} else if (line.getContent().toUpperCase().contains("DEBUG")) {
			lineCSSClass = "debugText";
		} else if (line.getContent().toUpperCase().contains("TRACE")) {
			lineCSSClass = "traceText";
		}
			
		
		lineTextList.add(line.getContent());	
		for(String lineContent:lineTextList)
		{
			Text text = null;
			try {
				text = this.textPool.borrowObject();
			} catch (Exception e) {
				LOG.error("ERROR WHILE GETTING OBJ FROM TEXT POOL", e);
			}
			text.setText(lineContent);
			text.getStyleClass().add(lineCSSClass);
			textDispFlow.getChildren().add(text);

		}
	}

	/**
	 * Clear and load lines from given absolute position
	 * 
	 * @param absolutePos
	 * @param lineList
	 * @param lineReader
	 * @param nuOfRec
	 */
	public synchronized void loadLinesFromPos(long absolutePos, List<Line> lineList, LineReader lineReader,
			int nuOfRec) {
		try {
			lineList.clear();
			Line line = new Line();
			line = lineReader.getNearestPrevLine(absolutePos);
			long nextLineStartPos = 0l;
			if (line != null) {
				nextLineStartPos = line.getStartPos();
			}
			for (int i = 0; i < nuOfRec; i++) {
				line = lineReader.getNextPosition(nextLineStartPos);
				if (line == null) {
					break;
				}
				nextLineStartPos = line.getEndPos() + 1;
				lineList.add(line);
			}
		} catch (IOException e) {
			LOG.error("FILE READ ERROR", e);
		}
	}

	/**
	 * Move up a Line
	 * 
	 * @param lineList
	 * @param lineReader
	 * @param nuOfRec
	 */
	public synchronized void lineMoveUp(List<Line> lineList, LineReader lineReader, int nuOfRec) {
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
		}
	}

	/**
	 * Move down a Line
	 * 
	 * @param lineList
	 * @param lineReader
	 * @param nuOfRec
	 */
	public synchronized void lineMoveDown(List<Line> lineList, LineReader lineReader, int nuOfRec) {
		Line firstLine = lineList.get(0);
		try {
			Line line = lineReader.getPrevPosition(firstLine.getStartPos() - 1);
			if (line != null) {
				lineList.add(0, line);
				if (lineList.size() > MainController.NU_OF_REC) {
					lineList.remove(lineList.size() - 1);
				}
			}

		} catch (IOException e) {
			LOG.error("ERROR SCROLLING UP");
		}
	}

}
