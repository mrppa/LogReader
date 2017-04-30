package com.mrppa.logreader.reader;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read Logs line by line
 * 
 * @author Pasindu
 *
 */
public class LineReader {
	private static final Logger LOG = LoggerFactory.getLogger(LineReader.class);
	private CachedLogReader cachedLogReader;

	public LineReader(String fileName, int chunksize) throws IOException {
		this.cachedLogReader = new CachedLogReader(fileName, chunksize);
	}

	public CachedLogReader getCachedLogReader() {
		return cachedLogReader;
	}

	protected void setCachedLogReader(CachedLogReader cachedLogReader) {
		this.cachedLogReader = cachedLogReader;
	}

	public long getNuOfBytes() {
		return this.cachedLogReader.getNuOfBytes();
	}

	public Line getNearestPrevLine(long position) throws IOException {
		long absolutePos = position;
		long nearestLineBreak = 0l;
		Byte readByte = null;
		char charVal;

		do {
			if (absolutePos < 0) {
				return null;
			}
			readByte = this.getDataByAbsolutePOS(absolutePos);
			if (readByte != null) {
				charVal = (char) readByte.byteValue();
				if (charVal == '\n') {
					nearestLineBreak = absolutePos;
					break;
				}
			} else {
				LOG.debug("END OF DATA");
				return null;
			}
			absolutePos--;
		} while (true);

		Line line = this.getPrevPosition(nearestLineBreak);
		return line;
	}

	/**
	 * Return next Line
	 * 
	 * @param lineStartPosition
	 *            start of the line
	 * @return
	 * @throws IOException
	 */
	public Line getNextPosition(long lineStartPosition) throws IOException {
		long absolutePos = lineStartPosition;
		StringBuffer lineBuffer = new StringBuffer();
		Line line = new Line();
		line.setStartPos(lineStartPosition);
		Byte readByte = null;
		char charVal;
		do {
			readByte = this.getDataByAbsolutePOS(absolutePos);
			line.setEndPos(absolutePos);
			absolutePos++;
			if (readByte != null) {
				charVal = (char) readByte.byteValue();
				lineBuffer.append(charVal);
			} else {
				LOG.debug("END OF DATA");
				break;
			}
		} while (readByte != null && charVal != '\n');
		line.setContent(lineBuffer.toString());
		if (lineBuffer.length() == 0) {
			return null;
		}
		return line;
	}

	/**
	 * return previous line
	 * 
	 * @param lineEndPosition
	 *            ending position of the line
	 * @return
	 * @throws IOException
	 */
	public Line getPrevPosition(long lineEndPosition) throws IOException {
		long absolutePos = lineEndPosition;
		StringBuffer lineBuffer = new StringBuffer();
		Line line = new Line();
		line.setEndPos(lineEndPosition);
		Byte readByte = null;
		char charVal;

		if (absolutePos < 0) {
			return null;
		}
		readByte = this.getDataByAbsolutePOS(absolutePos);
		charVal = (char) readByte.byteValue();
		if (charVal != '\n') // In prev line last char should be a new line
		{
			return null;
		}
		lineBuffer.append(charVal);
		absolutePos--;

		do {
			if (absolutePos < 0) {
				break;
			}
			readByte = this.getDataByAbsolutePOS(absolutePos);
			if (readByte != null) {
				charVal = (char) readByte.byteValue();
				if (charVal == '\n') {
					break;
				} else {
					lineBuffer.append(charVal);
					line.setStartPos(absolutePos);
				}
			} else {
				LOG.debug("END OF DATA");
				break;
			}
			absolutePos--;
		} while (true);
		if (lineBuffer.length() == 0) {
			return null;
		}
		lineBuffer.reverse();
		line.setContent(lineBuffer.toString());
		return line;
	}

	/**
	 * Get tate by absolute byte position
	 * 
	 * @param absolutePosition
	 * @return byte representation of the data(return null if no data for
	 *         location)
	 * @throws IOException
	 */
	protected Byte getDataByAbsolutePOS(long absolutePosition) throws IOException {
		LOG.info("GET DATA BY ABSOLUTE POSITION\t:" + absolutePosition);
		if (absolutePosition < 0) {
			return null;
		}
		long chunkNo = (long) Math.floor(absolutePosition / this.cachedLogReader.getChunkSize());
		int chunkPos = (int) (absolutePosition % this.cachedLogReader.getChunkSize());
		LOG.debug("CHUNK NO\t:" + chunkNo + "\tCHUNK POS\t" + chunkPos);
		byte[] chunkData = this.cachedLogReader.loadChunk(chunkNo);
		Byte data = null;
		if (chunkData != null && chunkPos < chunkData.length && chunkPos >= 0) {
			data = chunkData[chunkPos];
		}
		LOG.debug("READ DATA\t:" + data);
		return data;
	}

	public void close() throws IOException {
		this.cachedLogReader.close();
	}

}
