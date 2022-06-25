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

import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

import javax.swing.text.*;

import linaje.gui.AppGUI;
import linaje.gui.LPanel;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class HeaderPanel extends JPanel {
	
	public static final int ASPECT_DEFAULT = 0;
	public static final int ASPECT_VOID = 1;
	
	private int aspect = ASPECT_DEFAULT;
	
	private JTextArea textAreaDescription = null;
	private JTextArea textAreaTitle = null;
	
	private JLabel labelIcon = null;
	private JPanel panelSouth = null;

	private JPanel panelCenter = null;
	private JPanel panelEast = null;
	private JPanel panelMarginBottom = null;
	private JPanel panelMain = null;
	private JPanel panelIndentDescription = null;
	
	private JTextPane textPaneDescription = null;
	
	private boolean stylesActive = false;
	
	public HeaderPanel() {
		super();
		initialize();
	}
	public HeaderPanel(int aspect) {
		super();
		initialize();
		setAspect(aspect);
	}
	public HeaderPanel(String title, String description) {
		super();
		initialize();
		setTitle(title);
		setDescription(description);
	}
	
	public final void changeTextColor(Color foreground, String text) {

		int selectionStart = getTextPaneDescription().getText().indexOf(text);
		int selectionEnd = selectionStart + text.length();

		changeTextColor(foreground, selectionStart, selectionEnd);
	}
	
	public final void changeTextColor(Color foreground, int selectionStart, int selectionEnd) {

		getTextPaneDescription().select(selectionStart, selectionEnd);
		String rgbFont = Integer.toString(foreground.getRGB());
		Action action = new StyledEditorKit.ForegroundAction(rgbFont, foreground);
		// IMPORTANTE: el actionCommand tiene que ser el color de la fuente
		action.actionPerformed(new ActionEvent(getTextPaneDescription(), ActionEvent.ACTION_PERFORMED, rgbFont));
		getTextPaneDescription().setCaretPosition(0);
	}

	public final void makeTextBold(String text) {
		
	    int selectionStart = getTextPaneDescription().getText().indexOf(text);
	    int selectionEnd = selectionStart + text.length();
	    
	    makeTextBold(selectionStart, selectionEnd);
	}
	
	public final void makeTextBold(int selectionStart, int selectionEnd) {
		
	    getTextPaneDescription().select(selectionStart, selectionEnd);
	    Action action = new StyledEditorKit.BoldAction();
	    action.actionPerformed(new ActionEvent(getTextPaneDescription(), ActionEvent.ACTION_PERFORMED, Constants.VOID));
		getTextPaneDescription().setCaretPosition(0);
	}
	
	public void destroy() {
	
		//Dejamos los objetos globales a null
		textAreaDescription = null;
		textAreaTitle = null;
		labelIcon = null;
		panelSouth = null;
		panelCenter = null;
		panelEast = null;
		panelMarginBottom = null;
		panelMain = null;
		panelIndentDescription = null;
		textPaneDescription = null;
	
		try {		
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);	
		}
	}
	
	public boolean isStylesActive() {
		return stylesActive;
	}
	
	public int getAspect() {
		return aspect;
	}
	public String getDescripcion() {
		return getTextAreaDescription().getText();
	}
	public Icon getIcono() {
		return getlblIcon().getIcon();
	}
	
	private JLabel getlblIcon() {
		if (labelIcon == null) {
			labelIcon = new JLabel();
			labelIcon.setPreferredSize(new Dimension(48, 48));
			labelIcon.setText(Constants.VOID);
		}
		return labelIcon;
	}

	public int getMargin() {
		return getPanelMarginBottom().getPreferredSize().height;
	}

	private JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel();
			panelCenter.setOpaque(false);
			panelCenter.setLayout(new BorderLayout());
			panelCenter.add(getTextAreaTitle(), BorderLayout.NORTH);
			panelCenter.add(getPanelIndentDescription(), BorderLayout.WEST);
			panelCenter.add(getTextAreaDescription(), BorderLayout.CENTER);
		}
		return panelCenter;
	}

	private JPanel getPanelEast() {
		if (panelEast == null) {
			panelEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
			panelEast.setOpaque(false);
			panelEast.add(getlblIcon(), getlblIcon().getName());
		}
		return panelEast;
	}

	private JPanel getPanelIndentDescription() {
		if (panelIndentDescription == null) {
			panelIndentDescription = new JPanel();
			panelIndentDescription.setPreferredSize(new Dimension(5, 0));
			panelIndentDescription.setOpaque(false);
		}
		return panelIndentDescription;
	}

	private JPanel getPanelMarginBottom() {
		if (panelMarginBottom == null) {
			panelMarginBottom = new JPanel();
			panelMarginBottom.setPreferredSize(new Dimension(0, 10));
			panelMarginBottom.setOpaque(false);
		}
		return panelMarginBottom;
	}
	
	private JPanel getPanelMain() {
		if (panelMain == null) {
			panelMain = new JPanel();
			panelMain.setLayout(new BorderLayout());
			//Color bgColor = Colors.esColorOscuro(ColorsGUI.getColorPanels()) ? Colors.brighter(ColorsGUI.getColorPanels(), 0.1) : Colors.darker(ColorsGUI.getColorPanels(), 0.1);
			Color bgColor = ColorsGUI.getColorPanelsBrightest();
			panelMain.setBackground(bgColor);
			panelMain.add(getPanelEast(), BorderLayout.EAST);
			panelMain.add(getPanelCenter(), BorderLayout.CENTER);
			panelMain.add(getPanelSouth(), BorderLayout.SOUTH);
			int h = Math.max(getPanelCenter().getPreferredSize().height, getlblIcon().getPreferredSize().height+3) + panelSouth.getPreferredSize().height;
			panelMain.setPreferredSize(new Dimension(0, h));
			
		}
		return panelMain;
	}

	private JPanel getPanelSouth() {
		if (panelSouth == null) {
			panelSouth = new LPanel();
			panelSouth.setOpaque(false);
			panelSouth.setPreferredSize(new Dimension(0,10));
		}
		return panelSouth;
	}

	private JTextArea getTextAreaDescription() {
		if (textAreaDescription == null) {
			textAreaDescription = new JTextArea();
			textAreaDescription.setName("TextAreaDescripcion");
			textAreaDescription.setLineWrap(true);
			textAreaDescription.setOpaque(false);
			textAreaDescription.setWrapStyleWord(true);
			textAreaDescription.setEditable(false);
			textAreaDescription.setMargin(new Insets(0, 4, 0, 1));
		}
		return textAreaDescription;
	}

	private JTextArea getTextAreaTitle() {
		if (textAreaTitle == null) {
			textAreaTitle = new JTextArea();
			textAreaTitle.setName("TextAreaTitulo");
			textAreaTitle.setLineWrap(true);
			textAreaTitle.setOpaque(false);
			textAreaTitle.setWrapStyleWord(true);
			textAreaTitle.setMargin(new Insets(2, 4, 4, 1));
			textAreaTitle.setText(Constants.VOID);
			textAreaTitle.setEditable(false);
			textAreaTitle.setFont(textAreaTitle.getFont().deriveFont(Font.BOLD));
		}
		return textAreaTitle;
	}
	
	private JTextPane getTextPaneDescription() {
		if (textPaneDescription == null) {
			textPaneDescription = new JTextPane();
			textPaneDescription.setOpaque(false);
			textPaneDescription.setMargin(new Insets(0, 4, 0, 1));
			textPaneDescription.setEditable(false);
		}
		return textPaneDescription;
	}

	public String getTitle() {
		return getTextAreaTitle().getText();
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setSize(385, 65);
		add(getPanelMain(), BorderLayout.NORTH);
		add(getPanelMarginBottom(), BorderLayout.SOUTH);
		setTitle(null);
		ImageIcon logoIcon = AppGUI.getCurrentAppGUI().getLogoAppImage();
		getlblIcon().setIcon(logoIcon);
		int logoWidth = logoIcon.getIconWidth();
		if (logoWidth > 48)
			getlblIcon().setPreferredSize(new Dimension(logoWidth, 48));
	}
	
	private void setAspect(int newAspecto) {
		aspect = newAspecto;
	
		if (newAspecto == ASPECT_VOID) {
			removeAll();
			add(getPanelMarginBottom(), BorderLayout.SOUTH);
		}
	}
	
	public void setBackgroundMain(Color background) {	
		getPanelMain().setBackground(background);
	}
	
	public void setDescription(String description) {
		getTextPaneDescription().setText(description);
		getTextAreaDescription().setText(description);	
	}

	public void setStylesActive(boolean stylesActive) {
	
		this.stylesActive = stylesActive;
		
		if (stylesActive != isStylesActive()) {
			
			if (stylesActive) {
				getPanelCenter().remove(getTextAreaDescription());
				getPanelCenter().add(getTextPaneDescription(), BorderLayout.CENTER);
			}
			else {
				getPanelCenter().remove(getTextPaneDescription());
				getPanelCenter().add(getTextAreaDescription(), BorderLayout.CENTER);	
			}
		}
	}
	
	public void setIcon(Icon icon) {
		getlblIcon().setIcon(icon);	
	}
	
	public void setMargin(int margin) {
		getPanelMarginBottom().setPreferredSize(new Dimension(0, margin));	
	}
	
	public void setTitle(String title) {
	
		if (title == null || title.toString().trim().equals(Constants.VOID)) {
			getTextAreaTitle().setPreferredSize(new Dimension(0, 5));
			getPanelCenter().remove(getPanelIndentDescription());
		}
		else {
			getTextAreaTitle().setPreferredSize(null);
			getPanelCenter().add(getPanelIndentDescription(), BorderLayout.WEST);
		}
		getTextAreaTitle().setText(title);	
	}
}
