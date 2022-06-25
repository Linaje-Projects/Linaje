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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.RoundedBorder;
import linaje.gui.components.LabelTextField;
import linaje.gui.components.SpinButton;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LWindow;

public class TestRoundedBorder {

public static void main(String[] args) {
		
		LinajeLookAndFeel.init();
		
		final LPanel contentPane = new LPanel(new BorderLayout());
		final RoundedBorder roundedBorder = new RoundedBorder();
		//roundedBorder.setImagenSombra(new File(Directories.DIRECTORIO_ICONOS, "shadow.png"));
		
		LPanel panelPrincipal = new LPanel(new FlowLayout());	
		//panelPrincipal.setBackground(Color.red);

		// Add button with custom border
		final LButton button = new LButton("Hello");
		button.setBorder(roundedBorder);
		
		final LWindow lWindow = new LWindow();
		lWindow.setSize(200, 200);
		lWindow.getContentPane().setBorder(roundedBorder);
		
		LPanel contentPaneWindow = new LPanel(new BorderLayout());
		contentPaneWindow.setBorder(roundedBorder);
		contentPaneWindow.setOpaque(false);
		contentPaneWindow.add(new LPanel(), BorderLayout.CENTER);
		final JWindow window = new JWindow();
		window.setSize(200,  200);
		window.add(contentPaneWindow, BorderLayout.CENTER);
		//UtilsGUI.setWindowOpacity(window, 0.0f);
		UtilsGUI.setWindowOpaque(window, false);
		
		
		//button.setBackground(Color.YELLOW);
		//button.setOpaque(false);
		button.setPreferredSize(new Dimension(200, 200));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				lWindow.closeWindow();
				lWindow.showWindow(new Point(200, 200));
				
				window.setLocation(new Point(200, 500));
				window.setVisible(false);
				window.setVisible(true);
			}
		});
		panelPrincipal.add(button);
		
		final LButton button2 = new LButton("Hello");
		button2.setBorder(new LineBorder(Color.BLACK, 3, false));
		//button.setBackground(Color.YELLOW);
		button2.setOpaque(false);
		button2.setPreferredSize(new Dimension(200, 200));
		panelPrincipal.add(button2);
		
		final JLabel label = new JLabel("Hola");
		label.setBorder(roundedBorder);
		panelPrincipal.add(label);
		
		final JLabel label2 = new JLabel("Hola");
		label2.setBorder(new LineBorder(Color.BLACK, 3, false));
		panelPrincipal.add(label2);

		LabelTextField lblTxtTamanoCurva = new LabelTextField();
		lblTxtTamanoCurva.setTextLabel("Tamaño curva");
		lblTxtTamanoCurva.setSpinNumericsVisible(true);
		lblTxtTamanoCurva.setType(LTextField.TYPE_NUMBER);
		lblTxtTamanoCurva.getTextField().setMaxValue(500);
		
		LabelTextField lblTxtTranspSombra = new LabelTextField();
		lblTxtTranspSombra.setTextLabel("Transparencia sombra");
		lblTxtTranspSombra.setSpinNumericsVisible(true);
		lblTxtTranspSombra.setType(LTextField.TYPE_NUMBER);
		lblTxtTranspSombra.getTextField().setMaxValue(RoundedBorder.MAX_TRANSPARENCY);
		
		LabelTextField lblTxtAnchoInterior = new LabelTextField();
		lblTxtAnchoInterior.setTextLabel("Ancho interior extra");
		lblTxtAnchoInterior.setSpinNumericsVisible(true);
		lblTxtAnchoInterior.setType(LTextField.TYPE_NUMBER);
		lblTxtAnchoInterior.getTextField().setMaxValue(50);
		
		LabelTextField lblTxtAnchoBorde = new LabelTextField();
		lblTxtAnchoBorde.setTextLabel("Ancho borde");
		lblTxtAnchoBorde.setSpinNumericsVisible(true);
		lblTxtAnchoBorde.setType(LTextField.TYPE_NUMBER);
		lblTxtAnchoBorde.getTextField().setMaxValue(50);
		
		LabelTextField lblTxtAnchoSombra = new LabelTextField();
		lblTxtAnchoSombra.setTextLabel("Ancho sombra");
		lblTxtAnchoSombra.setType(LTextField.TYPE_NUMBER);
		lblTxtAnchoSombra.getTextField().setMaxValue(200);
		
		
		final SpinButton spinTamanoCurva = lblTxtTamanoCurva.getLTextFieldContainer().getSpinButton();
		spinTamanoCurva.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int valor = spinTamanoCurva.getValue();
				Dimension tamanoCurva = new Dimension(valor, valor);
				roundedBorder.setCornersCurveSize(tamanoCurva);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		final SpinButton spinTranspSombra = lblTxtTranspSombra.getLTextFieldContainer().getSpinButton();
		spinTranspSombra.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int valor = spinTranspSombra.getValue();
				roundedBorder.setShadowTransparency(valor);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		final SpinButton spinAnchoInterior = lblTxtAnchoInterior.getLTextFieldContainer().getSpinButton();
		spinAnchoInterior.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int valor = spinAnchoInterior.getValue();
				roundedBorder.setThicknessInsetsExtra(valor);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		final SpinButton spinAnchoBorde = lblTxtAnchoBorde.getLTextFieldContainer().getSpinButton();
		spinAnchoBorde.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int valor = spinAnchoBorde.getValue();
				roundedBorder.setThicknessLineBorder(valor);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		final SpinButton spinAnchoSombra = lblTxtAnchoSombra.getLTextFieldContainer().getSpinButton();
		spinAnchoSombra.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int valor = spinAnchoSombra.getValue();
				roundedBorder.setThicknessShadow(valor);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		spinTamanoCurva.setValue(roundedBorder.getCornersCurveSize().width);
		spinTranspSombra.setValue(roundedBorder.getShadowTransparency());
		spinAnchoBorde.setValue(roundedBorder.getThicknessLineBorder());
		spinAnchoInterior.setValue(roundedBorder.getThicknessInsetsExtra());
		spinAnchoSombra.setValue(roundedBorder.getThicknessShadow());
		
		LCheckBox chkSombraDegradada = new LCheckBox("Sombra degradada");
		chkSombraDegradada.setSelected(roundedBorder.isGradientShadow());
		chkSombraDegradada.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				roundedBorder.setGradientShadow(e.getStateChange() == ItemEvent.SELECTED);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		LCheckBox chkInteriorTransparente = new LCheckBox("Pintar interior siempre");
		chkInteriorTransparente.setSelected(roundedBorder.isPaintInsideAlways());
		//chkInteriorTransparente.setPreferredSize(new Dimension(150, 20));
		chkInteriorTransparente.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				roundedBorder.setPaintInsideAlways(e.getStateChange() == ItemEvent.SELECTED);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
		
		LPanel panelCombos = new LPanel(new LFlowLayout(SwingConstants.CENTER, SwingConstants.TOP, 5, 1, true));
		panelCombos.add(lblTxtAnchoBorde);
		panelCombos.add(lblTxtAnchoInterior);
		panelCombos.add(lblTxtAnchoSombra);
		panelCombos.add(lblTxtTranspSombra);
		panelCombos.add(lblTxtTamanoCurva);
		panelCombos.add(chkSombraDegradada);
		panelCombos.add(chkInteriorTransparente);
		
		contentPane.add(panelPrincipal, BorderLayout.CENTER);
		contentPane.add(panelCombos, BorderLayout.EAST);
		contentPane.setBorder(roundedBorder);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(contentPane, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
