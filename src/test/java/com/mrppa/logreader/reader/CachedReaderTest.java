package com.mrppa.logreader.reader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedReaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(CachedReaderTest.class);
	private String testFile1 = "TMP_TEST_CACHEDLOGREADER.txt";

	@Before
	public void generateFiles() throws IOException {
		LogFileGenerator logFileGenerator = new LogFileGenerator();
		logFileGenerator.generateSampleFile(testFile1, 10000);
	}

	@After
	public void cleanupFiles() {
		new File(testFile1).delete();
	}

	@Test
	public void testDataLoading() throws IOException {
		String filePath = testFile1;

		CachedLogReader cachedLogReader = new CachedLogReader(filePath, 1024);
		LogReader logReader = new LogReader(testFile1, 1024);

		assertEquals(logReader.getAvailableChunks(), cachedLogReader.getAvailableChunks());

		for (int i = 0; i < logReader.getAvailableChunks(); i++) {
			byte[] bytedataCached = cachedLogReader.loadChunk(i);
			byte[] bytedataNonCached = logReader.loadChunk(i);
			assertArrayEquals(bytedataNonCached, bytedataCached);
		}

		byte[] bytedataAttempt1 = cachedLogReader.loadChunk(0);
		byte[] bytedataAttempt2 = cachedLogReader.loadChunk(0);
		assertArrayEquals(bytedataAttempt1, bytedataAttempt2);

	}

	@Test
	public void testSearch1() throws IOException {
		String filePath = testFile1;
		String searchString = "9565";
		LogReader logReader = new LogReader(filePath, 1024);
		Progress progress1 = new Progress();
		Set<Long> searchRes1 = logReader.search(searchString, 0, logReader.getAvailableChunks() - 1, progress1);
		System.out.println(searchRes1);

		CachedLogReader cachedLogReader = new CachedLogReader(filePath, 1024);
		Progress progress2 = new Progress();
		Set<Long> searchRes2 = cachedLogReader.search(searchString, 0, logReader.getAvailableChunks() - 1, progress2);
		System.out.println(searchRes2);

		assertEquals(searchRes1, searchRes2);
	}

	// @Test
	// public void test2() throws IOException{
	// String filePath=testFile1;
	// File file=new File(filePath);
	//
	// LogReader logReader=new LogReader(filePath, 1024);
	// byte[] arr;
	//
	// arr=logReader.loadChunk(logReader.getAvailableChunks()-2);
	// System.out.println(arr.length);
	// assertEquals(arr.length, 1024);
	//
	// arr=logReader.loadChunk(logReader.getAvailableChunks()-1);
	// System.out.println(arr.length);
	// assertEquals(file.length()%1024, arr.length);
	//
	// arr=logReader.loadChunk(logReader.getAvailableChunks());
	// assertNull(arr);
	// }
	//
	// @Test(expected=java.io.FileNotFoundException.class)
	// public void test3() throws IOException{
	// LogReader logReader=new LogReader("EmptyPath", 1024);
	// }
	//
	// @Test
	// public void testSearch1() throws IOException{
	// String filePath=testFile1;
	// String searchString="9565";
	// LogReader logReader=new LogReader(filePath, 1024);
	// Progress progress=new Progress();
	// Set<Long>
	// searchRes=logReader.search(searchString,0,logReader.getAvailableChunks()-1,
	// progress);
	// System.out.println(searchRes);
	//
	// RandomAccessFile randomAccessFile=new RandomAccessFile(new
	// File(filePath), "r");
	// randomAccessFile.seek((Long)searchRes.toArray()[0]);
	// byte[] res=new byte[searchString.length()];
	// randomAccessFile.read(res);
	// System.out.println(new String(res));
	// assertEquals(new String(res),searchString );
	// }
	//
	// @Test
	// public void testSearch2() throws IOException{
	// String filePath=testFile1;
	// String searchString="LINE";
	// LogReader logReader=new LogReader(filePath, 1024);
	// Progress progress=new Progress();
	// Set<Long>
	// searchRes=logReader.search(searchString,0,logReader.getAvailableChunks()-1,
	// progress);
	// System.out.println(searchRes);
	// assertEquals(10000,searchRes.size() );
	// }

}
