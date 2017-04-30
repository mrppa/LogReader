package com.mrppa.logreader.reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This utility class is for testing purposes only
 * 
 * @author Pasindu
 *
 */
public class LogFileGenerator {

	public static void main(String arg[]) throws IOException {
		LogFileGenerator logFileGenerator = new LogFileGenerator();
		logFileGenerator.generateSampleFile("test.log", 40000000);
	}

	public void generateSampleFile(String fileName, long nuOfLines) throws IOException {
		File file = new File(fileName);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		for (long i = 1; i <= nuOfLines; i++) {
			bufferedWriter.write("LINE \t" + i + "\t GENERATED \n");
		}
		bufferedWriter.close();

	}

}
