package com.mrppa.logreader.reader;

/**
 * Line Object
 * @author Pasindu Ariyarathna (pasindu@mrppa.com)
 *
 */
public class Line {
	private String content;
	private long startPos;
	private long endPos;

	public Line() {
		super();
	}

	public Line(String content, long startPos, long endPos) {
		super();
		this.content = content;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public long getEndPos() {
		return endPos;
	}

	public void setEndPos(long endPos) {
		this.endPos = endPos;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
