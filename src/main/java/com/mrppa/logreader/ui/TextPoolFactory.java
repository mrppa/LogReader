package com.mrppa.logreader.ui;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.text.Text;
/**
 * 
 * @author Pasindu Ariyarathna (pasindu@mrppa.com)
 *
 */
public class TextPoolFactory extends BasePooledObjectFactory<Text> {

	private static final Logger LOG = LoggerFactory.getLogger(TextPoolFactory.class);

	
	@Override
	public Text create() throws Exception {
		Text text = new Text();
		LOG.debug("INITIATING NEW TEXT");
		text.setText("");
		return text;
	}

	@Override
	public PooledObject<Text> wrap(Text arg0) {
		return new DefaultPooledObject<Text>(arg0);
	}

	@Override
	public void passivateObject(PooledObject<Text> p) throws Exception {
		LOG.trace("TEXT RETURN TO POOL");
		p.getObject().setText("");
		p.getObject().getStyleClass().clear();
		super.passivateObject(p);
	}

}
