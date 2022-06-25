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
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.border.Border;

import linaje.gui.AppGUI;
import linaje.gui.RoundedBorder;
import linaje.gui.utils.ColorsGUI;
import linaje.utils.StateColor;


@SuppressWarnings("serial")
public class LDialog extends JDialog {

	private LWindowComponents windowComponents = null;	
	
	public LDialog() {
		super(AppGUI.getCurrentAppGUI().getFrame());
		initialize();
	}
	public LDialog(Frame owner) {
		super(owner);
		initialize();
	}
	public LDialog(Dialog owner) {
		super(owner);
		initialize();
	}
	public LDialog(Window owner) {
		super(owner);
		initialize();
	}
	public LDialog(Frame owner, boolean modal) {
		super(owner, modal);
		initialize();
	}
	public LDialog(Frame owner, String title) {
		super(owner, title);
		initialize();
	}
	public LDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		initialize();
	}
	public LDialog(Dialog owner, String title) {
		super(owner, title);
		initialize();
	}
	public LDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		initialize();
	}
	public LDialog(Window owner, String title) {
		super(owner, title);
		initialize();
	}
	public LDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		initialize();
	}
	public LDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		initialize();
	}
	public LDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		initialize();
	}
	public LDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initialize();
	}
	public LDialog(Dialog owner, String title, boolean modal,	GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initialize();
	}
	public LDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
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
	
	public void setUseContentWindowWhenOpaque(boolean useContentWindowWhenOpaque) {
		getWindowComponents().setUseContentWindowWhenOpaque(useContentWindowWhenOpaque);
	}
	public boolean isUseContentWindowWhenOpaque() {
		return getWindowComponents().isUseContentWindowWhenOpaque();
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
	
	public void setBorderColor(Color color) {
		getDefaultRoundedBorder().setLineBorderColor(color);
	}
	
	public Font getTitleFont() {
		return getWindowComponents().getLabelTitle().getFont();
	}
	public void setTitleFont(Font f) {
		getWindowComponents().getLabelTitle().setFont(f);
	}
}
