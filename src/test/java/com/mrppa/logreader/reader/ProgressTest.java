package com.mrppa.logreader.reader;

import org.junit.Test;

public class ProgressTest {

	@Test
	public void test() {
		Progress progress = new Progress();
		progress.setMinVal(100);
		progress.setMaxVal(1100);
		progress.setValue(600);
		System.out.println(progress.getProgress());
	}

}
