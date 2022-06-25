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
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LPanel;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Colors;

@SuppressWarnings("serial")
public class TestColors extends LPanel {

public static void main(String[] args) {
		
	try {
			LinajeLookAndFeel.init();	
			LDialogContent.showComponentInFrame(getTestComponent());
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
	
	public static JComponent getTestComponent() {
		
		final LButton button1 = new LButton();
		final LButton button2 = new LButton();
		final LButton button3 = new LButton();
		final LButton button4 = new LButton();
		
		final ImageIcon icono1 = Icons.ARROW_UP;
		final ImageIcon icono2 = Icons.ARROW_DOWN;
		final ImageIcon icono3 = Icons.ARROW_LEFT;
		final ImageIcon icono4 = Icons.ARROW_RIGHT;
		
		button1.setIcon(icono1);
		button2.setIcon(icono2);
		button3.setIcon(icono3);
		button4.setIcon(icono4);
		button1.getButtonProperties().setIgnoreIconHeight(false);
		//button1.getButtonProperties().setIconForegroundEnabled(false);
		button2.getButtonProperties().setIgnoreIconHeight(false);
		button3.getButtonProperties().setIgnoreIconHeight(false);
		button4.getButtonProperties().setIgnoreIconHeight(false);
		
		button1.setText("Botón");
		button2.setText("Botón");
		button3.setText("Botón");
		button4.setText("Botón");
		
		Dimension preferredSize = new Dimension(30, 30);
		final LPanel panelColor = new LPanel();
		final LPanel panelColorBright = new LPanel();
		final LPanel panelColorDark = new LPanel();
		
		panelColor.setPreferredSize(preferredSize);
		panelColorBright.setPreferredSize(preferredSize);
		panelColorDark.setPreferredSize(preferredSize);
		
		final JColorChooser colorChooser = new JColorChooser();
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				Color color = colorChooser.getColor();
				Console.println(Colors.getColorProperties(color));
				
				button1.setForeground(color);
				button2.setForeground(color);
				button3.setForeground(color);
				button4.setForeground(color);
				/*Image colorizedImage1 = StateIcon.createColorizedImage(icono1.getImage(), color);
				Image colorizedImage2 = StateIcon.createColorizedImage(icono2.getImage(), color);
				Image colorizedImage3 = StateIcon.createColorizedImage(icono3.getImage(), color);
				Image colorizedImage4 = StateIcon.createColorizedImage(icono4.getImage(), color);
				button1.setIcon(new ImageIcon(colorizedImage1));
				button2.setIcon(new ImageIcon(colorizedImage2));
				//button3.setIcon(new ImageIcon(colorizedImage3));
				button4.setIcon(new ImageIcon(colorizedImage4));
				
				Color invertColor = Colors.colorInverso(color);
				//button1.setBackground(color);
				button2.setBackground(invertColor);
				button3.setBackground(color);
				button4.setBackground(color);
				
				panelColor.setBackground(color);
				
				Color brighterColor = Colors.brighter(color, 0.1);
				Color darkerColor = Colors.darker(color, 0.1);
				
				Consola.println("color: " + Colors.getColorProperties(color));
				Consola.println("brighterColor: " + Colors.getColorProperties(brighterColor));
				Consola.println("darkerColor: " + Colors.getColorProperties(darkerColor));
				
				panelColorBright.setBackground(brighterColor);
				panelColorDark.setBackground(darkerColor);*/
			}
		});
		
		LPanel panel = new LPanel(new FlowLayout());
		panel.add(colorChooser);
		panel.add(button1);
		panel.add(button2);
		panel.add(button3);
		button4.setBorder(BorderFactory.createEmptyBorder());
		panel.add(button4);
		panel.add(panelColorBright);
		panel.add(panelColor);
		panel.add(panelColorDark);
		
		panel.setSize(panel.getPreferredSize().width + 20, panel.getPreferredSize().height + 20);
		return panel;
	}
}