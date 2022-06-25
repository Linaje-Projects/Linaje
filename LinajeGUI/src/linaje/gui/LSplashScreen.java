/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import linaje.LocalizedStrings;
import linaje.User;
import linaje.gui.components.FileNameExtensionFilter;
import linaje.gui.components.LClock;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Numbers;
import linaje.utils.Resources;

@SuppressWarnings("serial")
public class LSplashScreen extends LDialogContent {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String hello;
		public String welcome;
		public String welcomeMale;
		public String welcomeFemale;
		public String version;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LLabel labelUser = null;
	private LLabel labelApp = null;
	private LLabel labelVersion = null;
	private LLabel labelEnvironment = null;
	
	private LPanel panelNorth = null;
	private LPanel panelSouth = null;
	private LPanel panelEast = null;
	private LPanel panelWest = null;
	private LPanel panelCenter = null;
	
	private String pathImagesBackground = null;
	
	private boolean userAuthenticated = true;
	
	public LSplashScreen() {
		
		super();
		initialize();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		GraphicsUtils.obscureRect(g, getVisibleRect(), 0.2f);
	}
	
	private void initialize() {
		
		JFrame frame = new JFrame("LSplashFrame");
		frame.setIconImage(AppGUI.getCurrentAppGUI().getFrame().getIconImage());
		frame.setUndecorated(true);
		setFrame(frame);
		
		setSize(new Dimension(700, 500));
		setOpaque(false);
		setLayout(new BorderLayout());	
		setAdjustImageBackground(true);
		setImageBackground(getImageBackgroundFromPath());
		
		add(getPanelNorth(), BorderLayout.NORTH);
		add(getPanelSouth(), BorderLayout.SOUTH);
		add(getPanelEast(), BorderLayout.EAST);
		add(getPanelWest(), BorderLayout.WEST);
		add(getPanelCenter(), BorderLayout.CENTER);
		
		
		getPanelSouth().setLayout(new LFlowLayout(FlowLayout.RIGHT, SwingConstants.BOTTOM, 10, 0, true));
		getPanelSouth().add(getLabelVersion());
		
		getPanelCenter().setLayout(new LFlowLayout(FlowLayout.LEFT, SwingConstants.TOP, 20, 10, true));
		getPanelCenter().add(getLabelUser());
		
		LPanel panelNorthEast = new LPanel(new LFlowLayout(FlowLayout.RIGHT, SwingConstants.TOP, 20, 10, true));
		panelNorthEast.setOpaque(false);
		String envID = AppGUI.getCurrentApp().getEnvironmentID();
		String envIDProd = AppGUI.getCurrentApp().getEnvironmentProductionID();
		if (envID != null && envIDProd != null && !envID.equalsIgnoreCase(envIDProd)) {
			String envName = AppGUI.getCurrentAppGUI().getEnvironmentName();
			getLabelEnvironment().setText(envName != null ? envName : envID);
			getLabelEnvironment().setForeground(AppGUI.getCurrentAppGUI().getEnvironmentColor());
			panelNorthEast.add(getLabelEnvironment());
		}
		
		LPanel panelNorthWest = new LPanel(new LFlowLayout(FlowLayout.LEFT, SwingConstants.TOP, 20, 10));
		panelNorthWest.setOpaque(false);
		panelNorthWest.add(getLabelApp());
		
		getPanelNorth().setLayout(new BorderLayout());
		getPanelNorth().add(panelNorthEast, BorderLayout.EAST);
		getPanelNorth().add(panelNorthWest, BorderLayout.WEST);
		
		LClock clock = new LClock();
		clock.setForeground(Color.white);
		
		getPanelEast().setLayout(new LFlowLayout(FlowLayout.RIGHT, SwingConstants.TOP, 20, 10));
		getPanelEast().add(clock);
		
		update();
	}
	
	
	public void update() {
		
		String appName = AppGUI.getCurrentAppGUI().getName();
		getLabelApp().setText(appName);
		
		String versionName = AppGUI.getCurrentAppGUI().getVersionName();
		double version = AppGUI.getCurrentAppGUI().getVersion();
		if (versionName != null)
			getLabelVersion().setText(TEXTS.version + versionName + Constants.SPACE + version);
		else
			getLabelVersion().setText(TEXTS.version + String.valueOf(version));
		
		if (isUserAuthenticated()) {
			User currentUser = AppGUI.getCurrentAppGUI().getCurrentUser();
			String userFirstName = currentUser.getFirstName();
			int gender = currentUser.getGender();
			String wellcomeText = gender == User.GENDER_MALE ? TEXTS.welcomeMale : gender == User.GENDER_FEMALE ? TEXTS.welcomeFemale : TEXTS.welcome;
			if (userFirstName != null)
				getLabelUser().setText(TEXTS.hello + userFirstName + ", \n" + wellcomeText);
			else
				getLabelUser().setText(wellcomeText);
		}
		else {
			getLabelUser().setText(Constants.VOID);
		}
		/*getLabelUser().revalidate();
		getPanelWest().revalidate();
		revalidate();*/
		repaint();
	}
	
	private ImageIcon getImageBackgroundFromPath() {
		
			
		ImageIcon imageBackground = null;
		if (getPathImagesBackground() != null) {
			
			try {
				FileNameExtensionFilter fnef = new FileNameExtensionFilter("Image files", "jpg", "gif", "png");
				List<URL> urls = Resources.getResourceURLsFromResourceDir(getPathImagesBackground(), fnef);
				if (!urls.isEmpty()) {
					
					int index = Numbers.getRandomNumberInt(0, urls.size()-1);
					imageBackground = new ImageIcon(urls.get(index));
				}
				else {
					imageBackground = new ImageIcon(getPathImagesBackground());
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		return imageBackground;
	}
	public String getPathImagesBackground() {
		if (pathImagesBackground == null)
			pathImagesBackground = Icons.RESOURCE_DIR_IMAGES + "splash/";
		return pathImagesBackground;
	}
	public void setPathImagesBackground(String pathImagesBackground) {
		this.pathImagesBackground = pathImagesBackground;
	}
	
	
	private LLabel getLabelUser() {
		if (labelUser == null) {
			labelUser = new LLabel();
			labelUser.setFontSize(40);
			labelUser.setForeground(Color.white);
		}
		return labelUser;
	}
	private LLabel getLabelApp() {
		if (labelApp == null) {
			labelApp = new LLabel();
			labelApp.setFontSize(20);
			labelApp.setForeground(ColorsGUI.getColorApp());
		}
		return labelApp;
	}
	private LLabel getLabelVersion() {
		if (labelVersion == null) {
			labelVersion = new LLabel();
			labelVersion.setFontSize(12);
			labelVersion.setForeground(Color.white);
		}
		return labelVersion;
	}
	private LLabel getLabelEnvironment() {
		if (labelEnvironment == null) {
			labelEnvironment = new LLabel();
			labelEnvironment.setFontSize(16);
			labelEnvironment.setFontStyle(Font.BOLD);
		}
		return labelEnvironment;
	}
	private LPanel getPanelNorth() {
		if (panelNorth == null) {
			panelNorth = new LPanel();
			panelNorth.setBackground(Color.black);
			panelNorth.setOpacity(0.2f);
		}
		return panelNorth;
	}
	private LPanel getPanelSouth() {
		if (panelSouth == null) {
			panelSouth = new LPanel();
			panelSouth.setOpaque(false);
		}
		return panelSouth;
	}
	private LPanel getPanelEast() {
		if (panelEast == null) {
			panelEast = new LPanel();
			panelEast.setOpaque(false);
		}
		return panelEast;
	}
	private LPanel getPanelWest() {
		if (panelWest == null) {
			panelWest = new LPanel();
			panelWest.setOpaque(false);
		}
		return panelWest;
	}
	private LPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new LPanel();
			panelCenter.setOpaque(false);
		}
		return panelCenter;
	}

	public boolean isUserAuthenticated() {
		return userAuthenticated;
	}
	public void setUserAuthenticated(boolean userAuthenticated) {
		this.userAuthenticated = userAuthenticated;
		update();
	}
	
	@Override
	protected void prepareLDialogContent(Point location) {
		Tasks.getWaitPanel().setMaxFade(0);
		super.prepareLDialogContent(location);
	}
	@Override
	public void dispose() {
		super.dispose();
		Tasks.getWaitPanel().setMaxFade(WaitPanel.DEFAULT_MAX_FADE);
	}
}
