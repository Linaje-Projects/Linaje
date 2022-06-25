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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import linaje.gui.LPanel;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;
import linaje.utils.Utils;

@SuppressWarnings("serial")
public class AuxDescriptionPanel extends LPanel {
	
	public static final int ASPECT_DEFAULT = 0;
	public static final int ASPECT_VOID = 1;
	
	private int aspect = ASPECT_DEFAULT;

	private JTextArea textAreaDescription = null;
	private JTextArea textAreaTitle = null;
	
	private JPanel panelTextAreas = null;
	private JPanel panelMarginEast = null;
	private JPanel panelMarginNorth = null;
	private JPanel panelMarginWest = null;
	private JPanel panelMarginSouth = null;
	private JPanel panelMarginVoid = null;
	private JPanel panelMain = null;
	
	public AuxDescriptionPanel() {
		super();
		initialize();
	}
	public AuxDescriptionPanel(int aspect) {
		super();
		setAspect(aspect);
		initialize();
	}
	
	public void setBackgroundMain(Color color) {
		getPanelTextAreas().setBackground(color);	
	}
	
	public void destruir() {
	
		//Dejamos los objetos globales a null
		textAreaDescription = null;
		textAreaTitle = null;
		
		panelTextAreas = null;
		panelMarginEast = null;
		panelMarginNorth = null;
		panelMarginWest = null;
		panelMarginSouth = null;
		panelMarginVoid = null;
		panelMain = null;
			
		try {
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);	
		}
	}
	
	public int getAspect() {
		return aspect;
	}
	public String getDescription() {
		return getTextAreaDescription().getText();
	}
	public int getMargin() {
		return getPanelMarginVoid().getPreferredSize().width;
	}
	
	private JPanel getPanelTextAreas() {
		if (panelTextAreas == null) {
			panelTextAreas = new JPanel(new BorderLayout());
			panelTextAreas.add(getTextAreaTitle(), BorderLayout.NORTH);
			panelTextAreas.add(getTextAreaDescription(), BorderLayout.CENTER);
		}
		return panelTextAreas;
	}

	private JPanel getPanelMarginEast() {
		if (panelMarginEast == null) {
			panelMarginEast = new JPanel();
			panelMarginEast.setOpaque(false);
		}
		return panelMarginEast;
	}
	
	private JPanel getPanelMarginVoid() {
		if (panelMarginVoid == null) {
			panelMarginVoid = new JPanel();
			panelMarginVoid.setOpaque(false);
		}
		return panelMarginVoid;
	}
	
	private JPanel getPanelMarginNorth() {
		if (panelMarginNorth == null) {
			panelMarginNorth = new JPanel();
			panelMarginNorth.setOpaque(false);
		}
		return panelMarginNorth;
	}

	private JPanel getPanelMarginWest() {
		if (panelMarginWest == null) {
			panelMarginWest = new JPanel();
			panelMarginWest.setOpaque(false);
		}
		return panelMarginWest;
	}

	private JPanel getPanelMarginSouth() {
		if (panelMarginSouth == null) {
			panelMarginSouth = new JPanel();
			panelMarginSouth.setOpaque(false);
		}
		return panelMarginSouth;
	}

	private JPanel getPanelMain() {
		if (panelMain == null) {
			panelMain = new JPanel(new BorderLayout());
			panelMain.setPreferredSize(new Dimension(180, 0));
			panelMain.setOpaque(false);
			panelMain.add(getPanelTextAreas(), BorderLayout.CENTER);
			panelMain.add(getPanelMarginNorth(), BorderLayout.NORTH);
			panelMain.add(getPanelMarginSouth(), BorderLayout.SOUTH);
			panelMain.add(getPanelMarginEast(), BorderLayout.EAST);
			panelMain.add(getPanelMarginWest(), BorderLayout.WEST);
		}
		return panelMain;
	}
	
	private JTextArea getTextAreaDescription() {
		if (textAreaDescription == null) {
			textAreaDescription = new JTextArea();
			textAreaDescription.setLineWrap(true);
			textAreaDescription.setOpaque(false);
			textAreaDescription.setWrapStyleWord(true);
			textAreaDescription.setText("Descripción");
			textAreaDescription.setEditable(false);
			textAreaDescription.setMargin(new Insets(0, 6, 0, 1));
		}
		return textAreaDescription;
	}
	
	private JTextArea getTextAreaTitle() {
		if (textAreaTitle == null) {
			textAreaTitle = new JTextArea();
			textAreaTitle.setLineWrap(true);
			textAreaTitle.setOpaque(false);
			textAreaTitle.setWrapStyleWord(true);
			textAreaTitle.setText("Título");
			textAreaTitle.setMargin(new Insets(2, 2, 4, 1));
			textAreaTitle.setEditable(false);
			textAreaTitle.setFont(Utils.getFontWithStyle(textAreaTitle.getFont(), Font.BOLD));
		}
		return textAreaTitle;
	}
	
	public String getTitle() {
		return getTextAreaTitle().getText();
	}
	
	private void initialize() {
		
		setOpaque(false);
		setLayout(new BorderLayout());
		setSize(204, 164);
		
		getPanelTextAreas().setBackground(ColorsGUI.getColorPanelsBrightest());
		
		add(getPanelMarginVoid(), BorderLayout.WEST);
		if (getAspect() != ASPECT_VOID)		
			add(getPanelMain(), BorderLayout.CENTER);
		
		setMarginTextArea(0, 0, 0, getMargin());
	}
	
	private void setAspect(int aspect) {
		this.aspect = aspect;
	}
	
	public void setDescription(String description) {
		getTextAreaDescription().setText(description);
	}
	
	public void setMargin(int margin) {
		getPanelMarginVoid().setPreferredSize(new Dimension(margin, 0));
		getPanelMarginEast().setPreferredSize(new Dimension(getMargin(), 0));
	}
	
	public void setMarginTextArea(int top, int left, int bottom, int right) {
		getPanelMarginNorth().setPreferredSize(new Dimension(0, top));
		getPanelMarginWest().setPreferredSize(new Dimension(left, 0));
		getPanelMarginSouth().setPreferredSize(new Dimension(0, bottom));
		getPanelMarginEast().setPreferredSize(new Dimension(right, 0));
	}
	
	public void setTitle(String title) {
		getTextAreaTitle().setText(title);	
	}
}
