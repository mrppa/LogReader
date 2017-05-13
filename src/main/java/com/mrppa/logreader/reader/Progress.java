package com.mrppa.logreader.reader;

public class Progress {
	private long value;
	private long minVal;
	private long maxVal;
	private boolean shutDownCommand=false;

	public long getValue() {
		return value;
	}

	protected void setValue(long value) {
		this.value = value;
	}

	public long getMinVal() {
		return minVal;
	}

	protected void setMinVal(long minVal) {
		this.minVal = minVal;
	}

	public long getMaxVal() {
		return maxVal;
	}

	protected void setMaxVal(long maxVal) {
		this.maxVal = maxVal;
	}

	public boolean isShutDownCommand() {
		return shutDownCommand;
	}

	public void setShutDownCommand(boolean shutDownCommand) {
		this.shutDownCommand = shutDownCommand;
	}

	public int getProgress() {
		int progress = (int) (((double) (value - minVal) / (maxVal - minVal)) * 100);
		return progress;
	}

}
