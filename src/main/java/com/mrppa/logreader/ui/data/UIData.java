package com.mrppa.logreader.ui.data;

import java.text.NumberFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UIData {
	private static final Logger LOG = LoggerFactory.getLogger(UIData.class);

	public String getStstemBarStat(){
		LOG.trace("GET SYS INFO");
		NumberFormat numberFormat=NumberFormat.getInstance();
		numberFormat.setGroupingUsed(true);
		
		long heapSize = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024); 
		long heapMaxSize = Runtime.getRuntime().totalMemory()/(1024*1024);
		StringBuilder sysInfo=new StringBuilder();
		sysInfo.append(numberFormat.format(heapSize)+" MB ");
		sysInfo.append("/");
		sysInfo.append(numberFormat.format(heapMaxSize)+" MB " );
		return sysInfo.toString();
	}

}
