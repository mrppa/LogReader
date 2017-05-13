package com.mrppa.logreader.reader;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is wrapped object for LogReader.with cached functionality for
 * performance
 * 
 * @author Pasindu
 *
 */
public class CachedLogReader extends LogReader {
	private static final Logger LOG = LoggerFactory.getLogger(CachedLogReader.class);
	private Map<Long, byte[]> chunkMap = new HashMap<Long, byte[]>();
	private Queue<Long> chachedChunkList = new LinkedList<Long>();
	private static final int MAX_CHCHE_SIZE = 10;

	private synchronized byte[] readFromCache(long chunkNo) throws IOException {
		byte[] chunkData = null;
		LOG.debug("LOAD CHUNK FROM CACHE\t:" + chunkNo);
		chunkData = this.chunkMap.get(chunkNo);
		if (chunkData == null) {
			LOG.debug("CACHE MISS");
			chunkData = super.loadChunk(chunkNo);
			if (chunkData != null && this.getChunkSize() == chunkData.length)// Completed
																				// data
																				// will
																				// be
																				// cached
			{
				this.chachedChunkList.add(chunkNo);
				this.chunkMap.put(chunkNo, chunkData);
				if (chachedChunkList.size() > MAX_CHCHE_SIZE) {
					Long removeCacheNo = this.chachedChunkList.poll();
					this.chunkMap.remove(removeCacheNo);
					LOG.debug("ITEM " + removeCacheNo + " REMOVED FROM CACHE DUE TO OVERFLOW");
				}
			}
		} else {
			LOG.debug("CACHE HIT");
		}

		return chunkData;
	}

	public CachedLogReader(String filePath, int chunkSize) throws IOException {
		super(filePath, chunkSize);
	}

	@Override
	public long getNuOfBytes() {
		return super.getNuOfBytes();
	}

	@Override
	public int getChunkSize() {
		return super.getChunkSize();
	}

	@Override
	public void setChunkSize(int chunkSize) {
		super.setChunkSize(chunkSize);
	}

	@Override
	public byte[] loadChunk(long chunkNo) throws IOException {
		byte[] chunkData = this.chunkMap.get(chunkNo);
		chunkData = readFromCache(chunkNo);
		return chunkData;
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public long getAvailableChunks() throws IOException {
		return super.getAvailableChunks();
	}

	@Override
	public Set<Long> search(String searchStr, long fromChunk, long toChunk, Progress progress) throws IOException {
		LogReader logReader = null;
		Set<Long> result = null;
		try {
			logReader = new LogReader(this.getFilePath(), this.getChunkSize());// Because
																				// we
																				// do
																				// not
																				// want
																				// to
																				// clear
																				// cache
																				// during
																				// file
																				// search
			result = logReader.search(searchStr, fromChunk, toChunk, progress);
		} catch (IOException e) {
			LOG.error("ERROR WHILE SEARCHING" + e);
		} finally {
			if (logReader != null) {
				logReader.close();
			}
		}
		return result;
	}
}
