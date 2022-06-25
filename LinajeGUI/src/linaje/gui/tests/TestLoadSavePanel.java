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
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import linaje.gui.AppGUI;
import linaje.gui.LoadSaveActions;
import linaje.gui.LoadSaveEvent;
import linaje.gui.LoadSaveListener;
import linaje.gui.LoadSavePanel;
import linaje.gui.components.FileNameExtensionFilter;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Files;

public class TestLoadSavePanel {

public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			final JTextArea textArea = new JTextArea("Test text");
			JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setPreferredSize(new Dimension(300, 300));
			scrollPane.setViewportView(textArea);
			
			final LoadSaveActions lsActions = new LoadSaveActions("Test LoadSave Actions");
			lsActions.setFileNameExtensionFilter(new FileNameExtensionFilter("Text files", "txt"));
			
			DocumentListener documentListener = new DocumentListener() {
				
				public void changedUpdate(DocumentEvent e) {
					lsActions.setCurrentFileModified(true);
				}
				public void removeUpdate(DocumentEvent e) {
					lsActions.setCurrentFileModified(true);
				}
				public void insertUpdate(DocumentEvent e) {
					lsActions.setCurrentFileModified(true);
				}
			};
			
			lsActions.addLoadSaveListener(new LoadSaveListener() {
				
				public String save(LoadSaveEvent lsEvent) {
					Console.println("Save event - file: "+(lsEvent.getFile() != null ? lsEvent.getFile().getName() : "null"));
					try {
						Files.saveText(textArea.getText(), lsEvent.getFile());
						return null;
					}
					catch (Exception ex) {
						return ex.getMessage();
					}
				}
				
				public String load(LoadSaveEvent lsEvent) {
					try {
						//textArea.getDocument().removeDocumentListener(documentListener);
						Console.println("Load event - file: "+(lsEvent.getFile() != null ? lsEvent.getFile().getName() : "null"));
						String text = Files.readText(lsEvent.getFile());
						textArea.setText(text);
						return null;
					}
					catch (Exception ex) {
						return ex.getMessage();
					}
					//finally {
						//textArea.getDocument().addDocumentListener(documentListener);
					//}
				}
				
				public String close(LoadSaveEvent lsEvent) {
					Console.println("Close event - file: "+(lsEvent.getFile() != null ? lsEvent.getFile().getName() : "null"));
					textArea.setText(Constants.VOID);
					return null;//"Ha ocurrido un error al cerrar";
				}
			});
			
			textArea.getDocument().addDocumentListener(documentListener);
			
			//lsActions.setDefaultFile(LinajeLookAndFeel.getDefaultConfigFile());
			//lsActions.loadFile(LinajeLookAndFeel.getDefaultConfigFile());
			
			final LoadSavePanel lsPanel = new LoadSavePanel(lsActions);
					
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(lsPanel, BorderLayout.NORTH);
			panel.add(scrollPane, BorderLayout.CENTER);
			
			AppGUI.getCurrentAppGUI().getFrame().setJMenuBar(lsPanel.getMenuBar());
			
			LDialogContent.showComponentInFrame(panel);
		}
		catch (Throwable ex) {
		}
	}
}
