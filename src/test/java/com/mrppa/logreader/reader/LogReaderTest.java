package com.mrppa.logreader.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogReaderTest {

	private static final Logger LOG = LoggerFactory.getLogger(LogReaderTest.class);
	private String testFile1 = "TMP_TEST_FILE1.txt";

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
	public void test1() throws IOException {
		LOG.info("TEST1");
		String filePath = testFile1;

		File file = new File(filePath);
		FileInputStream inp = new FileInputStream(file);
		byte[] fileData = new byte[(int) file.length()];
		inp.read(fileData);
		StringBuilder sb1 = new StringBuilder();
		sb1.append(new String(fileData));

		StringBuffer sb2 = new StringBuffer();
		LogReader logReader = new LogReader(filePath, 1024);
		long availChunks = logReader.getAvailableChunks();
		for (int i = 0; i < availChunks; i++) {
			byte[] bytedata = logReader.loadChunk(i);
			if (bytedata != null) {
				sb2.append(new String(bytedata));
			}
		}
		logReader.close();
		assertEquals(sb1.toString(), sb2.toString());
	}

	@Test
	public void test2() throws IOException {
		String filePath = testFile1;
		File file = new File(filePath);

		LogReader logReader = new LogReader(filePath, 1024);
		byte[] arr;

		arr = logReader.loadChunk(logReader.getAvailableChunks() - 2);
		System.out.println(arr.length);
		assertEquals(arr.length, 1024);

		arr = logReader.loadChunk(logReader.getAvailableChunks() - 1);
		System.out.println(arr.length);
		assertEquals(file.length() % 1024, arr.length);

		arr = logReader.loadChunk(logReader.getAvailableChunks());
		assertNull(arr);
	}

	@Test(expected = java.io.FileNotFoundException.class)
	public void test3() throws IOException {
		LogReader logReader = new LogReader("EmptyPath", 1024);
	}

	@Test
	public void testSearch1() throws IOException {
		String filePath = testFile1;
		String searchString = "9565";
		LogReader logReader = new LogReader(filePath, 1024);
		Progress progress = new Progress();
		Set<Long> searchRes = logReader.search(searchString, 0, logReader.getAvailableChunks() - 1, progress);
		System.out.println(searchRes);

		RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath), "r");
		randomAccessFile.seek((Long) searchRes.toArray()[0]);
		byte[] res = new byte[searchString.length()];
		randomAccessFile.read(res);
		System.out.println(new String(res));
		assertEquals(new String(res), searchString);
	}

	@Test
	public void testSearch2() throws IOException {
		String filePath = testFile1;
		String searchString = "LINE";
		LogReader logReader = new LogReader(filePath, 1024);
		Progress progress = new Progress();
		Set<Long> searchRes = logReader.search(searchString, 0, logReader.getAvailableChunks() - 1, progress);
		System.out.println(searchRes);
		assertEquals(10000, searchRes.size());
	}

	@Test
	public void testSearchNext() throws IOException {
		String filePath = testFile1;
		String searchString = "LINE";
		LogReader logReader = new LogReader(filePath, 1024);
		Progress progress = new Progress();
		Long searchRes = logReader.searchNextItem(searchString, 0l, progress);

		RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filePath), "r");
		randomAccessFile.seek(searchRes);
		byte[] res = new byte[searchString.length()];
		randomAccessFile.read(res);
		System.out.println(new String(res));

		assertEquals(new String(res), searchString);
	}

}
