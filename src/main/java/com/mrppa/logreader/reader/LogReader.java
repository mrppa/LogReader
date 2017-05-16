package com.mrppa.logreader.reader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Random accesss log file reader. Not to use directly
 * @author Pasindu Ariyarathna (pasindu@mrppa.com)
 *
 */
public class LogReader {
	private static final Logger LOG = LoggerFactory.getLogger(LogReader.class);

	public int chunkSize;
	private String filePath;
	private RandomAccessFile randomAccessFile;

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	protected String getFilePath() {
		return filePath;
	}

	protected void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getNuOfBytes() {
		File file = new File(this.getFilePath());
		return file.length();
	}

	/**
	 * Initiate Logreader
	 * 
	 * @param filePath
	 *            path for file to load
	 * @param chunkSize
	 *            size of chunk read
	 * @throws IOException
	 */
	public LogReader(String filePath, int chunkSize) throws IOException {
		this.chunkSize = chunkSize;
		this.filePath = filePath;
		this.randomAccessFile = new RandomAccessFile(filePath, "r");
	}

	/**
	 * Load chunk data
	 * 
	 * @param chunkNo
	 *            chunk number to random access data
	 * @return return byte array of data size equivalent to possible read. Null
	 *         if no data
	 * @throws IOException
	 */
	public byte[] loadChunk(long chunkNo) throws IOException {
		LOG.debug("LOAD CHUNK \t:" + chunkNo);
		long pos = this.chunkSize * chunkNo;
		LOG.debug("POSITION SEEK\t:" + pos);
		this.randomAccessFile.seek(pos);
		byte[] bytes = new byte[this.chunkSize];
		int nuOfBytesRead = this.randomAccessFile.read(bytes);
		LOG.debug("BYTES READ COUNT\t:" + nuOfBytesRead);
		if (nuOfBytesRead == -1) {
			return null;
		} else if (nuOfBytesRead != this.chunkSize) {
			byte[] newArr = new byte[nuOfBytesRead];
			System.arraycopy(bytes, 0, newArr, 0, nuOfBytesRead);
			return newArr;
		}
		return bytes;
	}

	/**
	 * Close the file opperation
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		LOG.debug("CLOSING LOG READER");
		this.randomAccessFile.close();
	}

	/**
	 * Get currently available size of chunk data. This may not accurate for
	 * currently writing file
	 * 
	 * @return
	 * @throws IOException
	 */
	public long getAvailableChunks() throws IOException {
		LOG.debug("GET AVAILABLE CHUNKS");
		double nuOfChunksAvailable = (double) this.randomAccessFile.length() / this.chunkSize;
		LOG.debug("CALCULATED AVAILABLE\t:" + nuOfChunksAvailable);
		long availChunks = (long) Math.ceil(nuOfChunksAvailable);
		LOG.debug("ROUNDED AVAILABLE\t:" + availChunks);
		return availChunks;
	}

	/**
	 * Search a string in complete file
	 * 
	 * @param searchStr
	 *            String to search.length of searchStr should be less than the
	 *            given chunk size
	 * @param fromChunk
	 * @param toChunk
	 * @param progress
	 *            progress object
	 * @return list of positions.Null if no data
	 * @throws IOException
	 */
	public Set<Long> search(String searchStr, long fromChunk, long toChunk, Progress progress) throws IOException {
		LOG.debug("SEARCH OPPERATION\t+" + searchStr);
		Pattern pattern = Pattern.compile(searchStr);

		Set<Long> searchResults = new HashSet<Long>();
		progress.setMinVal(fromChunk);
		progress.setMaxVal(toChunk);
		byte[] prevChunkData = new byte[0];
		for (long currChunkNo = fromChunk; currChunkNo <= toChunk; currChunkNo++) {
			progress.setValue(currChunkNo);
			byte[] chunkdata = this.loadChunk(currChunkNo);
			if (chunkdata != null) {
				byte[] totalSearchData = new byte[prevChunkData.length + chunkdata.length];// Merging
																							// previous
																							// results
																							// with
																							// current
																							// results
																							// because
																							// there
																							// might
																							// be
																							// results
																							// on
																							// the
																							// edges
				System.arraycopy(prevChunkData, 0, totalSearchData, 0, prevChunkData.length);
				System.arraycopy(chunkdata, 0, totalSearchData, prevChunkData.length, chunkdata.length);

				String searchSpace = new String(totalSearchData);
				Matcher matcher = pattern.matcher(searchSpace);
				while (matcher.find()) {
					int pos = matcher.start();
					LOG.debug("MATCH POS IN CURRENT CHUNK\t:" + pos);
					long realPos = this.chunkSize * currChunkNo + pos - prevChunkData.length;
					LOG.debug("REAL POS\t:" + realPos);
					searchResults.add(realPos);
				}
				prevChunkData = chunkdata;
			}
			if(progress.isShutDownCommand())
			{
				throw new IOException("THREAD SHUTDOWN REQUESTED");
			}
		}
		progress.setValue(progress.getMaxVal());
		return searchResults;
	}

}
