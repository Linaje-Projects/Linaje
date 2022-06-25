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
package linaje.gui.windows;

import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.border.Border;

import linaje.gui.AppGUI;
import linaje.gui.RoundedBorder;
import linaje.gui.utils.ColorsGUI;
import linaje.utils.StateColor;


@SuppressWarnings("serial")
public class LFrame extends JFrame {

	private LWindowComponents windowComponents = null;	
	
	
	public LFrame() {
		super();
		initialize();
	}
	public LFrame(String title) throws HeadlessException {
		super(title);
		initialize();
	}
	public LFrame(GraphicsConfiguration gc) {
		super(gc);
		initialize();
	}
	public LFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		initialize();
	}
	
	private LWindowComponents getWindowComponents() {
		if (windowComponents == null)
			windowComponents = new LWindowComponents(this);
		return windowComponents;
	}
	
	public void setTransparency(float transparency) {
		getWindowComponents().setTransparency(transparency);
	}
	public float getTransparency() {
		return getWindowComponents().getTransparency();
	}
	
	@Deprecated
	/**
	 * Use setBackgroundContent instead
	 * */
	public void setBackground(Color bgColor) {
		super.setBackground(bgColor);
	}
	
	public void setBackgroundContent(Color backgroundContent) {
		getWindowComponents().setBackgroundContent(backgroundContent);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		getWindowComponents().setForeground(fg);
	}
	
	public Border getBorder() {
		return getWindowComponents().getBorder();
	}
	public void setBorder(Border border) {
		getWindowComponents().setBorder(border);
	}
	
	private void initialize() {
		super.setUndecorated(true);
		super.setContentPane(getWindowComponents().getWindowBorderPanel());
		setIconImage(AppGUI.getCurrentAppGUI().getFrameIcon().getImage());
		setForeground(new StateColor(ColorsGUI.getColorText(), null, ColorsGUI.getColorApp()));	
	}
	
	@Override
	public void setContentPane(Container contentPane) {
		getWindowComponents().setContentPane(contentPane);
	}
	
	@Override
	public Container getContentPane() {
		return getWindowComponents().getContentPane();
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		getWindowComponents().setTitle(title);
	}
	
	public void setOpacity(float opacity) {
		setTransparency((int)((1-opacity)*100));
	}
	
	public void setUndecorated(boolean undecorated) {
		super.setUndecorated(true);
	}
	
	@Override
	public void setVisible(boolean b) {
		getWindowComponents().setVisible(b);
		super.setVisible(b);
		//UtilsGUI.showDialogDarkenOwner(this);		
	}
	
	@Override
	public void setResizable(boolean resizable) {
		super.setResizable(resizable);
		getWindowComponents().updateResizable();
	}
	
	@Override
	public boolean isLightweight() {
		return super.isLightweight();
		//return true;
	}
	
	@Override
	public boolean isResizable() {
		return super.isResizable();
		//return true;
	}
	
	public Insets getLDialogInsets() {
		return getWindowComponents().getLWindowInsets();
	}
	
	public RoundedBorder getDefaultRoundedBorder() {
		return getWindowComponents().getDefaultRoundedBorder();
	}
	
	public void setUseContentWindowWhenOpaque(boolean useContentWindowWhenOpaque) {
		getWindowComponents().setUseContentWindowWhenOpaque(useContentWindowWhenOpaque);
	}
	public boolean isUseContentWindowWhenOpaque() {
		return getWindowComponents().isUseContentWindowWhenOpaque();
	}
	
	public boolean isMaximizable() {
		return getWindowComponents().isMaximizable();
	}
	public boolean isMinimizable() {
		return getWindowComponents().isMinimizable();
	}
	public boolean isCloseable() {
		return getWindowComponents().isCloseable();
	}
	public boolean isMinimizeCompact() {
		return getWindowComponents().isMinimizeCompact();
	}
	
	public void setMaximizable(boolean maximizable) {
		getWindowComponents().setMaximizable(maximizable);
	}
	public void setMinimizable(boolean minimizable) {
		getWindowComponents().setMinimizable(minimizable);
	}
	public void setCloseable(boolean closeable) {
		getWindowComponents().setCloseable(closeable);
	}
	public void setMinimizeCompact(boolean minimizeCompact) {
		getWindowComponents().setMinimizeCompact(minimizeCompact);
	}
}
