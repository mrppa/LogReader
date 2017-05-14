package com.mrppa.logreader.ui.data;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import com.mrppa.logreader.reader.Line;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UIDataTest {

	@Test
	public void testGetStstemBarStat() {
		UIData uiData=new UIData();
		String sysInfo=uiData.getStstemBarStat();
		assertNotNull(sysInfo);
	}
}
