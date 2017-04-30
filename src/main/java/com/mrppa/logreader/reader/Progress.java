package com.mrppa.logreader.reader;

public class Progress {
	private long value;
	private long minVal;
	private long maxVal;

	protected long getValue() {
		return value;
	}

	protected void setValue(long value) {
		this.value = value;
	}

	protected long getMinVal() {
		return minVal;
	}

	protected void setMinVal(long minVal) {
		this.minVal = minVal;
	}

	protected long getMaxVal() {
		return maxVal;
	}

	protected void setMaxVal(long maxVal) {
		this.maxVal = maxVal;
	}

	public int getProgress() {
		int progress = (int) (((double) (value - minVal) / (maxVal - minVal)) * 100);
		return progress;
	}

}
