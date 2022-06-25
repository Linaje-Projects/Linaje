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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.plaf.FileChooserUI;

import linaje.gui.AppGUI;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialog;
import linaje.gui.windows.LDialogContent;

public class TestLFileChooserUI {

	public static void main(String[] args) {
		
		//Locale.setDefault(Locale.FRANCE);
		LinajeLookAndFeel.init();
		//try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
		
		JPanel mainPanel = new JPanel(new LFlowLayout());
		
		final JButton button = new JButton("File chooser");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("serial")
				JFileChooser fileChooser =  new JFileChooser() {
					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException {
						FileChooserUI ui = getUI();
				        String title = ui.getDialogTitle(this);
				        putClientProperty(AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY, title);

				        LDialog dialog = new LDialog(AppGUI.getCurrentAppGUI().getFrame());
				        dialog.setTitle(title);
				        dialog.setModal(true);
				        
				        dialog.setComponentOrientation(this.getComponentOrientation());
				        dialog.setSize(new Dimension(1000, 600));

				        Container contentPane = dialog.getContentPane();
				        contentPane.setLayout(new BorderLayout());
				        contentPane.add(this, BorderLayout.CENTER);

				       // dialog.pack();
				        dialog.setLocationRelativeTo(parent);

				        return dialog;
					}
				};
				fileChooser.showOpenDialog(button);	
			}
		});
		
		mainPanel.add(button);
		LDialogContent.showComponentInFrame(mainPanel);
	}
}
