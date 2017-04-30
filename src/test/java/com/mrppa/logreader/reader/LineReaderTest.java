package com.mrppa.logreader.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LineReaderTest {
	private String testFile1 = "TMP_TEST_LINEREADER.txt";

	@Before
	public void generateFiles() throws IOException {
		LogFileGenerator logFileGenerator = new LogFileGenerator();
		logFileGenerator.generateSampleFile(testFile1, 10);
	}

	@After
	public void cleanupFiles() {
		new File(testFile1).delete();
		System.out.println("RESOUCE DELETED");
	}

	@Test
	public void testGetDataByAbsolutePOS() throws IOException {

		File file = new File(testFile1);
		FileInputStream inp = new FileInputStream(file);
		byte[] fileData = new byte[(int) file.length()];
		inp.read(fileData);

		LineReader lineReader = new LineReader(testFile1, 2);
		for (int i = 0; i < fileData.length; i++) {
			byte byteData = lineReader.getDataByAbsolutePOS(i);
			System.out.println("ACTUAL DATA:" + fileData[i] + "\t READ DATA:" + byteData);
			assertEquals(fileData[i], byteData);
		}

		// overflow
		Byte byteData = lineReader.getDataByAbsolutePOS(file.length());
		System.out.println(" READ DATA:" + byteData);
		lineReader.close();
		assertNull(byteData);

	}

	@Test
	public void testGetNextPOS() throws IOException {

		File file = new File(testFile1);
		FileInputStream inp = new FileInputStream(file);
		byte[] fileData = new byte[(int) file.length()];
		inp.read(fileData);

		StringBuilder sb = new StringBuilder();
		LineReader lineReader = new LineReader(testFile1, 2);
		long pos = 0;
		Line line = null;
		do {
			line = lineReader.getNextPosition(pos);
			if (line == null) {
				break;
			}
			System.out.println(line.getContent());
			pos = line.getEndPos() + 1;
			sb.append(line.getContent());
		} while (true);
		System.out.println(sb);
		lineReader.close();

		String fileText = new String(fileData);
		assertEquals(fileText, sb.toString());
	}

	@Test
	public void testGetPrevPOS() throws IOException {

		File file = new File(testFile1);
		FileInputStream inp = new FileInputStream(file);
		byte[] fileData = new byte[(int) file.length()];
		inp.read(fileData);

		List<String> dataList = new LinkedList<String>();
		LineReader lineReader = new LineReader(testFile1, 2);
		long pos = file.length() - 1;
		Line line = null;
		do {
			line = lineReader.getPrevPosition(pos);
			if (line == null) {
				break;
			}
			System.out.println(line.getContent());
			pos = line.getStartPos() - 1;
			dataList.add(line.getContent());
		} while (true);
		lineReader.close();
		Collections.reverse(dataList);
		System.out.println(dataList);
		StringBuffer sb = new StringBuffer();
		for (String data : dataList) {
			sb.append(data);
		}

		String fileText = new String(fileData);
		assertEquals(fileText, sb.toString());
	}

	@Test
	public void zigzagTest() throws IOException {
		LineReader lineReader = new LineReader(testFile1, 2);
		int pos = 0;
		Line nextLine = new Line();
		nextLine.setEndPos(-1);
		Line prevLine;
		do {
			nextLine = lineReader.getNextPosition(nextLine.getEndPos() + 1);
			if (nextLine == null) {
				return;
			} else {
				prevLine = lineReader.getPrevPosition(nextLine.getEndPos());
				assertEquals(nextLine.getContent(), prevLine.getContent());
			}
		} while (true);

	}

	@Test
	public void getNearestPrevLineTest() throws IOException {
		File file = new File(testFile1);
		LineReader lineReader = new LineReader(testFile1, 2);
		Line line = lineReader.getNearestPrevLine(file.length() - 1);
		assertNotNull(line);
	}
}
