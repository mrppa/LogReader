package com.mrppa.logreader.ui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.logreader.reader.LineReader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AboutController implements Initializable {
	private static final Logger LOG = LoggerFactory.getLogger(AboutController.class);

	@FXML
	private ImageView aboutImg;
	@FXML
	private Label versionLabel;
	@FXML
	private Label javaVersionLabel;
	@FXML
	private Label osNameLabel;
	@FXML
	private Hyperlink linktoSite;

	public ImageView getAboutImg() {
		return aboutImg;
	}

	public void setAboutImg(ImageView aboutImg) {
		this.aboutImg = aboutImg;
	}

	public Label getVersionLabel() {
		return versionLabel;
	}

	public void setVersionLabel(Label versionLabel) {
		this.versionLabel = versionLabel;
	}

	public Label getJavaVersionLabel() {
		return javaVersionLabel;
	}

	public void setJavaVersionLabel(Label javaVersionLabel) {
		this.javaVersionLabel = javaVersionLabel;
	}

	public Label getOsNameLabel() {
		return osNameLabel;
	}

	public void setOsNameLabel(Label osNameLabel) {
		this.osNameLabel = osNameLabel;
	}

	public Hyperlink getLinktoSite() {
		return linktoSite;
	}

	public void setLinktoSite(Hyperlink linktoSite) {
		this.linktoSite = linktoSite;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.aboutImg.setImage(new Image(getClass().getResource("/img/appicon_Large.png").toExternalForm()));
		final Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/LogReader.properties"));
		} catch (IOException e) {
			LOG.error("ERROR LOADING PROPERTIES");
			e.printStackTrace();
		}
		this.versionLabel.setText("LogReader Version \t\t:" + properties.getProperty("LogReader.version"));
		this.javaVersionLabel.setText("Java Version \t\t\t:" + System.getProperty("java.version"));
		this.osNameLabel.setText("OS \t\t\t\t\t:" + System.getProperty("os.name"));

		// Clicking the link
		this.linktoSite.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URL("https://github.com/mrppa/LogReader").toURI());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});
	}

}
