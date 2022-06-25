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

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.text.ParseException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import linaje.App;
import linaje.LocalizedStrings;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.utils.Lists;
import linaje.utils.ReflectAccessSupport;

public class AppGUI extends App {

	private boolean showingCodes = false;
	private boolean designTime = false;
	
	private ImageIcon logoAppName = null;
	private ImageIcon logoAppImage = null;
	private ImageIcon frameIcon = null;
	
	private JFrame frame = null;
	
	private LSplashScreen splashScreen = null;
	
	//Si tenemos iconos planos dejarlo a false, sino (Como en SwingSet demo) ponerlo a true
	private boolean optimizeNonPlainImagesColorize = false;
	
	public AppGUI() {
		super();
		initialize();
	}
	public AppGUI(JFrame frame) {
		super();
		setFrame(frame);
		initialize();
	}
	public AppGUI(JFrame frame, String codigo) throws Exception {
		super(codigo);
		setFrame(frame);
		initialize();
	}
		
	public static AppGUI getCurrentAppGUI() {
		if (App.getCurrentApp() == null || !(App.getCurrentApp() instanceof AppGUI)) {
			App.setCurrentApp(new AppGUI());
		}
		return (AppGUI) App.getCurrentApp();
	}

	public static Font getFont() {
		return GeneralUIProperties.getInstance().getFontApp();
	}
	
	private void initialize() {
		App.getDefaults().addResourceBundle(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
	}
	
	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame(getName());
			frame.setSize(800, 600);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setIconImage(getFrameIcon().getImage());
			UtilsGUI.centerWindow(frame);
		}
		return frame;
	}
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	
	public boolean isShowingCodes() {
		return showingCodes;
	}
	public void setShowingCodes(boolean showingCodes) {
		this.showingCodes = showingCodes;
	}
	public boolean isDesignTime() {
		return designTime;
	}
	public void setDesignTime(boolean designTime) {
		this.designTime = designTime;
	}
	
	public ImageIcon getLogoAppImage() {
		if (logoAppImage == null) {
			Color bg = ColorsGUI.getColorApp();
			Color fg = Color.white;
			Font font = GeneralUIProperties.getInstance().getFontApp().deriveFont(30f);
			String text = getId() != null ? getId() : getName();
			final int MAX_CHARS = 5;
			if (text.length() > MAX_CHARS)
				text = text.substring(0, MAX_CHARS);
			Image image = Icons.createImageText(text, getFrame().getFontMetrics(font), fg, bg, new Insets(5,5,5,5), true);
			logoAppImage = new ImageIcon(image);
		}
		return logoAppImage;
	}
	public ImageIcon getLogoAppName() {
		if (logoAppName == null) {
			Color bg = null;
			Color fg = ColorsGUI.getColorApp();
			Font font = GeneralUIProperties.getInstance().getFontApp().deriveFont(20f);
			String text = getName() != null ? getName() : getId();
			Image image = Icons.createImageText(text, getFrame().getFontMetrics(font), fg, bg, new Insets(2,2,2,2), true);
			logoAppName = new ImageIcon(image);
		}
		return logoAppName;
	}
	public ImageIcon getFrameIcon() {
		if (frameIcon == null) {
			Color bg = ColorsGUI.getColorApp();
			Color fg = Color.white;
			Font font = new Font("Sanserif", Font.BOLD, 9);
			String text = getId() != null ? getId() : getName();
			final int MAX_CHARS = 1;
			if (text.length() > MAX_CHARS)
				text = text.substring(0, MAX_CHARS);
			Image image = Icons.createImageText(text, getFrame().getFontMetrics(font), fg, bg, new Insets(1,1,1,1), true);
			frameIcon = new ImageIcon(image);
		}
		return frameIcon;
	}
	public void setLogoAppImage(ImageIcon logoAppImage) {
		this.logoAppImage = logoAppImage;
	}
	public void setLogoAppName(ImageIcon logoAppName) {
		this.logoAppName = logoAppName;
	}
	public void setFrameIcon(ImageIcon frameIcon) {
		this.frameIcon = frameIcon;
		getFrame().setIconImage(frameIcon.getImage());
	}
	
	public LSplashScreen getSplashScreen() {
		if (splashScreen == null)
			splashScreen = new LSplashScreen();
		return splashScreen;
	}
	public void setSplashScreen(LSplashScreen splashScreen) {
		this.splashScreen = splashScreen;
	}
	public boolean isOptimizeNonPlainImagesColorize() {
		return optimizeNonPlainImagesColorize;
	}
	public void setOptimizeNonPlainImagesColorize(boolean optimizeNonPlainImages) {
		this.optimizeNonPlainImagesColorize = optimizeNonPlainImages;
	}
	
	/*
	 * Sobreescribimos para poder inicializar logoAppImage
	 */
	@Override
	protected void initFromConfig(List<String> linesConfig) {
		
		String[] encodedFields = Lists.listToArray(linesConfig, String.class);
		ReflectAccessSupport ras = new ReflectAccessSupport(this) {
			@Override
			protected Object decodeValue(String encodedValue, Class<?> classType) throws ParseException {
				Object value;
				if (Icon.class.isAssignableFrom(classType))
					value = new ImageIcon(encodedValue);
				else
					value = super.decodeValue(encodedValue, classType);		
				return value;
			}
		};
		ras.setEncodedFieldValues(null, encodedFields);
	}
}
