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
package linaje.gui.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import linaje.gui.Icons;
import linaje.gui.LArrowButton;
import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LPanel;
import linaje.gui.LRadioButton;
import linaje.gui.LTitledBorder;
import linaje.gui.LToggleButton;
import linaje.gui.RoundedBorder;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.UtilsGUI;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class TestButtons extends LPanel {

	private LPanel panelButtons = null;
	private LPanel panelToggleButtons = null;
	private LPanel panelRadioButtons = null;
	private LPanel panelChecks = null;
	private LPanel panelArrowButtons = null;
	
	public TestButtons() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		setName("Buttons");
		setLayout(new VerticalBagLayout(10));
		
		LTitledBorder border1 = UtilsGUI.setTitledBorder(getPanelButtons(), "Buttons");
		LTitledBorder border2 = UtilsGUI.setTitledBorder(getPanelToggleButtons(), "ToggleButtons");
		LTitledBorder border3 = UtilsGUI.setTitledBorder(getPanelRadioButtons(), "RadioButtons");
		UtilsGUI.setTitledBorder(getPanelArrowButtons(), "ArrowButtons");
		
		border1.setTitlePosition(TitledBorder.BELOW_TOP);
		//border2.setTitlePosition(TitledBorder.BELOW_BOTTOM);
		border3.setTitlePosition(TitledBorder.BOTTOM);
		//border4.setTitlePosition(TitledBorder.TOP);
		LTitledBorder borderChecks = new LTitledBorder(new RoundedBorder(), "Checks", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP);
		getPanelChecks().setBorder(borderChecks);
		
		getPanelButtons().setBackground(GeneralUIProperties.getInstance().getColorPanelsBright());
		getPanelToggleButtons().setBackground(GeneralUIProperties.getInstance().getColorPanelsBright());
		getPanelRadioButtons().setBackground(GeneralUIProperties.getInstance().getColorPanelsBright());
		getPanelChecks().setBackground(GeneralUIProperties.getInstance().getColorPanelsBright());
		
		add(getPanelToggleButtons());
		add(getPanelButtons());
		add(getPanelRadioButtons());
		add(getPanelChecks());
		add(getPanelArrowButtons());
		
		setPreferredSize(new Dimension(900,  600));
	}
	
	private LPanel getPanelButtons() {
		if (panelButtons == null) {
			
			panelButtons = new LPanel(new LFlowLayout());
			
			JButton jButton = new JButton("JButton normal");
			JButton jButton2 = new JButton("JButton normal\notra línea");
			jButton2.setIcon(Icons.ARROW_UP);
			
			LButton lButton1 = new LButton("LButton");
			LButton lButton2 = new LButton("LButton\nOtra línea");
			LButton lButton3 = new LButton("LButtoN");
			lButton3.setIcon(Icons.ARROW_DOWN);
			LButton lButton4 = new LButton("LButtoN\nOTRA LÍNEA");
			LButton lButton5 = new LButton("LButton Deshabilitado");
			lButton5.setEnabled(false);
			
			LButton lButton6 = new LButton("POSITIVE");
			lButton6.setOpaque(false);
			lButton6.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorPositive());
			LButton lButton7 = new LButton("NEGATIVE");
			lButton7.setBorderPainted(false);
			lButton7.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorNegative());
			LButton lButton8 = new LButton("WARNING");
			lButton8.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorWarning());
			lButton8.setIcon(Icons.ARROW_LEFT);
			LButton lButton9 = new LButton("INFO");
			lButton9.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorInfo());
			lButton9.setIcon(Icons.ARROW_RIGHT);
			LButton lButton10 = new LButton("IMPORTANT");
			lButton10.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorImportant());
			lButton10.setIcon(Icons.CANCEL);
			
			panelButtons.add(jButton);
			panelButtons.add(jButton2);
			panelButtons.add(lButton1);
			panelButtons.add(lButton2);
			panelButtons.add(lButton3);
			panelButtons.add(lButton4);
			panelButtons.add(lButton5);
			panelButtons.add(lButton6);
			panelButtons.add(lButton7);
			panelButtons.add(lButton8);
			panelButtons.add(lButton9);
			panelButtons.add(lButton10);
		}
		return panelButtons;
	}

	private LPanel getPanelChecks() {
		if (panelChecks == null) {
			
			panelChecks = new LPanel(new LFlowLayout());
			
			JCheckBox checkBox1 = new JCheckBox("JCheckBox normal", true);
			JCheckBox checkBox2 = new JCheckBox("JCheckBox normal/nmultilínea", true);
			
			LCheckBox lCheckBox1 = new LCheckBox("LCheckBox 1", true);
			LCheckBox lCheckBox2 = new LCheckBox("LCheckBox 2", true);
			LCheckBox lCheckBox3 = new LCheckBox("LCheckBox 3", true);
			
			LCheckBox lCheckBox4 = new LCheckBox("LCheckBox 4 (mark unexpanded)", true);
			LCheckBox lCheckBox5 = new LCheckBox("LCheckBox\nmultilinea", true);
	
			lCheckBox2.setEnabled(false);
			lCheckBox3.setFontSize(20);
			lCheckBox4.getButtonProperties().setMarkExpanded(false);
			
			LCheckBox lCheckBox9 = new LCheckBox("POSITIVE", true);
			LCheckBox lCheckBox10 = new LCheckBox("NEGATIVE", true);
			LCheckBox lCheckBox11 = new LCheckBox("WARNING", true);
			LCheckBox lCheckBox12 = new LCheckBox("INFO", true);
			LCheckBox lCheckBox13 = new LCheckBox("IMPORTANT", true);
			
			lCheckBox10.setForeground(GeneralUIProperties.getInstance().getColorNegative());
			lCheckBox11.setForeground(GeneralUIProperties.getInstance().getColorWarning());
			lCheckBox9.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorPositive());
			lCheckBox10.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorNegative());
			lCheckBox11.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorWarning());
			lCheckBox12.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorInfo());
			lCheckBox13.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorImportant());		
			
			panelChecks.add(checkBox1);
			panelChecks.add(checkBox2);
			panelChecks.add(lCheckBox1);
			panelChecks.add(lCheckBox2);
			panelChecks.add(lCheckBox3);
			panelChecks.add(lCheckBox4);
			panelChecks.add(lCheckBox5);
			panelChecks.add(lCheckBox9);
			panelChecks.add(lCheckBox10);
			panelChecks.add(lCheckBox11);
			panelChecks.add(lCheckBox12);
			panelChecks.add(lCheckBox13);
		}
		return panelChecks;
	}

	private LPanel getPanelRadioButtons() {
		
		if (panelRadioButtons == null) {
			
			panelRadioButtons = new LPanel(new LFlowLayout());
			
			JRadioButton jRadioButton1 = new LRadioButton("JRadioButton", true);
			JRadioButton jRadioButton2 = new LRadioButton("JRadioButton\notra línea", true);
			
			LRadioButton lRadioButton1 = new LRadioButton("LRadioButton 1");
			LRadioButton lRadioButton2 = new LRadioButton("LRadioButton 2");
			LRadioButton lRadioButton3 = new LRadioButton("LRadioButton 3");
			LRadioButton lRadioButton4 = new LRadioButton("LRadioButton 4", true);
			
			LRadioButton lRadioButton5 = new LRadioButton("LRadioButton\nmultilinea", true);
			
			LRadioButton lRadioButton6 = new LRadioButton("LRadioButton Toggle Aspect", true);
			LRadioButton lRadioButton7 = new LRadioButton("LRadioButton Toggle Aspect\nmultilinea");
			LRadioButton lRadioButton8 = new LRadioButton("LRadioButton Toggle Aspect\nmultilinea\notra línea");
			
			LRadioButton lRadioButton9 = new LRadioButton("POSITIVE", true);
			LRadioButton lRadioButton10 = new LRadioButton("NEGATIVE", true);
			LRadioButton lRadioButton11 = new LRadioButton("WARNING", true);
			LRadioButton lRadioButton12 = new LRadioButton("INFO", true);
			LRadioButton lRadioButton13 = new LRadioButton("IMPORTANT", true);
			
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(lRadioButton1);
			bg.add(lRadioButton2);
			bg.add(lRadioButton3);
			bg.add(lRadioButton4);
			
			lRadioButton1.setFontSize(11);
			lRadioButton2.setFontSize(13);
			lRadioButton3.setFontSize(15);
			lRadioButton4.setFontSize(17);
			
			jRadioButton2.setEnabled(false);
			
			lRadioButton6.setFontStyle(Font.BOLD);
			lRadioButton6.setIcon(Icons.ARROW_UP);
			lRadioButton7.setIcon(Icons.ARROW_DOWN);
			lRadioButton8.setIcon(Icons.ARROW_LEFT);
			
			lRadioButton6.setToggleAspect(true);
			lRadioButton7.setToggleAspect(true);
			lRadioButton8.setToggleAspect(true);
			
			lRadioButton10.setForeground(GeneralUIProperties.getInstance().getColorNegative());
			lRadioButton11.setForeground(GeneralUIProperties.getInstance().getColorWarning());
			lRadioButton9.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorPositive());
			lRadioButton10.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorNegative());
			lRadioButton11.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorWarning());
			lRadioButton12.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorInfo());
			lRadioButton13.getButtonProperties().setMarkColor(GeneralUIProperties.getInstance().getColorImportant());
			
			panelRadioButtons.add(jRadioButton1);
			panelRadioButtons.add(jRadioButton2);
			panelRadioButtons.add(lRadioButton1);
			panelRadioButtons.add(lRadioButton2);
			panelRadioButtons.add(lRadioButton3);
			panelRadioButtons.add(lRadioButton4);
			panelRadioButtons.add(lRadioButton5);
			panelRadioButtons.add(lRadioButton6);
			panelRadioButtons.add(lRadioButton7);
			panelRadioButtons.add(lRadioButton8);
			panelRadioButtons.add(lRadioButton9);
			panelRadioButtons.add(lRadioButton10);
			panelRadioButtons.add(lRadioButton11);
			panelRadioButtons.add(lRadioButton12);
			panelRadioButtons.add(lRadioButton13);
		}
		return panelRadioButtons;
	}

	private LPanel getPanelToggleButtons() {
		if (panelToggleButtons == null) {
			panelToggleButtons = new LPanel(new LFlowLayout());
			
			JToggleButton jToggleButton = new JToggleButton("JToggleButton", true);
			JToggleButton jToggleButton2 = new JToggleButton("JToggleButton\nOtra línea");
			LToggleButton lToggleButton1 = new LToggleButton("LToggleButton 1", true);
			LToggleButton lToggleButton2 = new LToggleButton("Transparent");
			lToggleButton2.setOpaque(false);
			LToggleButton lToggleButton3 = new LToggleButton("BorderPainted false");
			lToggleButton3.setBorderPainted(false);
			
			LToggleButton lToggleButton4 = new LToggleButton("LToggleButton 1\nOtra línea", true);
			LToggleButton lToggleButton5 = new LToggleButton("LToggleButton 2\nOtra línea");
			LToggleButton lToggleButton6 = new LToggleButton("LToggleButton 3\nOtra línea");
			LToggleButton lToggleButton7 = new LToggleButton("LToggleButton 4\nOtra línea");
			lToggleButton7.setEnabled(false);
			
			LToggleButton lToggleButton8 = new LToggleButton("LineBackground");
			lToggleButton8.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorPositive());
			lToggleButton8.setIcon(Icons.ARROW_UP);
			LToggleButton lToggleButton9 = new LToggleButton(Constants.SPACE);
			lToggleButton9.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorNegative());
			LToggleButton lToggleButton10 = new LToggleButton(Constants.SPACE);
			lToggleButton10.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorWarning());
			LToggleButton lToggleButton11 = new LToggleButton(Constants.SPACE);
			lToggleButton11.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorInfo());
			LToggleButton lToggleButton12 = new LToggleButton(Constants.SPACE);
			lToggleButton12.getButtonProperties().setLineBackgroundColor(GeneralUIProperties.getInstance().getColorImportant());
			
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(lToggleButton1);
			bg.add(lToggleButton2);
			bg.add(lToggleButton3);
			
			ButtonGroup bg2 = new ButtonGroup();
			bg2.add(lToggleButton4);
			bg2.add(lToggleButton5);
			bg2.add(lToggleButton6);
			bg2.add(lToggleButton7);
			
			ButtonGroup bg3 = new ButtonGroup();
			bg3.add(lToggleButton8);
			bg3.add(lToggleButton9);
			bg3.add(lToggleButton10);
			bg3.add(lToggleButton11);
			bg3.add(lToggleButton12);
			
			
			panelToggleButtons.add(jToggleButton);
			panelToggleButtons.add(jToggleButton2);
			panelToggleButtons.add(lToggleButton1);
			panelToggleButtons.add(lToggleButton2);
			panelToggleButtons.add(lToggleButton3);
			panelToggleButtons.add(lToggleButton4);
			panelToggleButtons.add(lToggleButton5);
			panelToggleButtons.add(lToggleButton6);
			panelToggleButtons.add(lToggleButton7);
			panelToggleButtons.add(lToggleButton8);
			panelToggleButtons.add(lToggleButton9);
			panelToggleButtons.add(lToggleButton10);
			panelToggleButtons.add(lToggleButton11);
			panelToggleButtons.add(lToggleButton12);
		}
		return panelToggleButtons;
	}

	public LPanel getPanelArrowButtons() {
		if (panelArrowButtons == null) {
			panelArrowButtons = new LPanel(new LFlowLayout());
			
			LArrowButton lArrowButton = new LArrowButton();
			LArrowButton lArrowButton2 = new LArrowButton();
			LArrowButton lArrowButton3 = new LArrowButton();
			LArrowButton lArrowButton4 = new LArrowButton();
			LArrowButton lArrowButton5 = new LArrowButton();
			LArrowButton lArrowButton6 = new LArrowButton();
			LArrowButton lArrowButton7 = new LArrowButton();
			LArrowButton lArrowButton8 = new LArrowButton();
	
			lArrowButton.setDirection(SwingConstants.NORTH);
			lArrowButton2.setDirection(SwingConstants.SOUTH);
			lArrowButton3.setDirection(SwingConstants.WEST);
			lArrowButton4.setDirection(SwingConstants.EAST);
			lArrowButton5.setDirection(SwingConstants.NORTH);
			lArrowButton6.setDirection(SwingConstants.SOUTH);
			lArrowButton7.setDirection(SwingConstants.WEST);
			lArrowButton8.setDirection(SwingConstants.EAST);
	
			lArrowButton2.setArrowColor(Color.orange.darker());
	
			int size = 20;
			lArrowButton.setPreferredSize(new Dimension(size, size));
			lArrowButton2.setPreferredSize(new Dimension(size, size));
			lArrowButton3.setPreferredSize(new Dimension(size, size));
			lArrowButton4.setPreferredSize(new Dimension(size, size));
			
			lArrowButton5.setPreferredSize(new Dimension(50, 50));
			lArrowButton6.setPreferredSize(new Dimension(50, 50));
			lArrowButton7.setPreferredSize(new Dimension(50, 50));
			//lArrowButton8.setPreferredSize(new Dimension(50, 50));
			
			/*lArrowButton5.setBackground(Colors.COLOR_ROLLOVER_BORDE);
			lArrowButton6.setBackground(Colors.COLOR_ROLLOVER_BORDE);
			lArrowButton7.setBackground(Colors.COLOR_ROLLOVER_BORDE);
			lArrowButton8.setBackground(Colors.COLOR_ROLLOVER_BORDE);
			*/		
			lArrowButton4.setEnabled(false);
					
			panelArrowButtons.add(lArrowButton);
			panelArrowButtons.add(lArrowButton2);
			panelArrowButtons.add(lArrowButton3);
			panelArrowButtons.add(lArrowButton4);
			panelArrowButtons.add(lArrowButton5);
			panelArrowButtons.add(lArrowButton6);
			panelArrowButtons.add(lArrowButton7);
			panelArrowButtons.add(lArrowButton8);
		}
		return panelArrowButtons;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
}
