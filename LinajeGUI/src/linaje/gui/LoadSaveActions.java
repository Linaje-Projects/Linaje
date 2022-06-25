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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.EventListenerList;

import linaje.LocalizedStrings;
import linaje.gui.cells.LabelCell;
import linaje.gui.components.FileNameExtensionFilter;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.utils.FilesGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Colors;
import linaje.utils.Files;
import linaje.utils.Lists;
import linaje.utils.Utils;

/**
 * Automatiza las acciones de abrir, salvar, salvar como, etc. habilitando o deshabilitando las que sean necesarias según lo que hagamos
 * Crea y ordena el historial de los ficheros utilizados en un menú
 **/
@SuppressWarnings("serial")
public class LoadSaveActions {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String noFile;
		public String savedFiles;
		public String loadFrom;
		public String close;
		public String save;
		public String saveAs;
		public String saveAsDefault;
		public String openInSystem;
		public String recentFiles;
		public String hystory;
		public String messageSaveCurrent;
		public String messageOverwriteCurrent;
		public String processCancelled;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private String name = null;
	private Component parent = null;
	
	private File defaultFolder = null;
	private File currentLoadedFile = null;
	private File defaultFile = null;
	
	private File historyFile = null;
	private int maxHistoryFiles = 10;
	
	private boolean currentFileModified = false;
	
	protected EventListenerList eventListenerList = null;
	private FileNameExtensionFilter fileNameExtensionFilter = null;
	
	private JFileChooser fileChooser = null;
	
	private static final File NO_FILE = new File(" < None > ");
	private final String MODIFIED_PREFIX = "* ";
	
	private List<String> historyPaths = null;
	private LCombo<File> comboLoadFastFiles = null;
	private JMenu menuLoadFastFiles = null;
	private ButtonGroup menuItemsGroup = null;
	
	private Action actionLoadFrom = null;
	private Action actionClose = null;
	private Action actionSave = null;
	private Action actionSaveAs = null;
	private Action actionSaveAsDefault = null;
	private Action actionOpenInSystem = null;
	
	//Si lo ponemos a true dejará 'salvar como' y 'salvar como predeterminado' cuando carguemos NO_FILE
	private boolean saveNoFileAllowed = false;
	
	class FileAction extends AbstractAction {

		private File file = null;
		
		public FileAction(File file) {
			super(file.getName());
			setFile(file);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			loadFile(getFile());
		}
		
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
	}
	
	public class FileRender extends LCellRenderer<File> {
		
		public LabelCell getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {
			
			LabelCell labelCell = (LabelCell) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			labelCell.setCode(value != null ? value.getName() : "< None >");
			labelCell.setShowCodesAlways(true);
			labelCell.setSwapCodeDesc(true);
			
			if (isCurrentFileModified() && value != null && getCurrentLoadedFile() != null && value.getAbsolutePath().equals(getCurrentLoadedFile().getAbsolutePath())) {
				labelCell.setCode(MODIFIED_PREFIX + labelCell.getCode());
			}
			
			if (value != null && getDefaultFile() != null && value.getAbsolutePath().equals(getDefaultFile().getAbsolutePath())) {
				labelCell.setForeground(Colors.optimizeColor(Color.blue, list.getBackground()));
			}
				
			return labelCell;
		}
	}
	
	private ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				loadFile(comboLoadFastFiles.getSelectedItem());
			}
		}
	};
	
	public LoadSaveActions(String name) {
		super();
		if (name == null)
			name = TEXTS.savedFiles;
		else
			name = Files.getNameCompatibleWithWindowsFiles(name);
		this.name = name;
		initialize();
	}

	private void initialize() {
		//refreshHistory(-1);
		setCurrentLoadedFile(null);
	}
	
	public Action getActionLoadFrom() {
		if (actionLoadFrom == null) {
			actionLoadFrom = new AbstractAction(TEXTS.loadFrom) {
				public void actionPerformed(ActionEvent e) {
					loadFrom();
				}
			};
		}
		return actionLoadFrom;
	}
	public Action getActionClose() {
		if (actionClose == null) {
			actionClose = new AbstractAction(TEXTS.close) {
				public void actionPerformed(ActionEvent e) {
					closeFile(getCurrentLoadedFile());
				}
			};
		}
		return actionClose;
	}
	public Action getActionSave() {
		if (actionSave == null) {
			actionSave = new AbstractAction(TEXTS.save) {
				public void actionPerformed(ActionEvent e) {
					saveFile(comboLoadFastFiles.getSelectedItem());
				}
			};	
		}
		return actionSave;
	}
	public Action getActionSaveAs() {
		if (actionSaveAs == null) {
			actionSaveAs = new AbstractAction(TEXTS.saveAs) {
				public void actionPerformed(ActionEvent e) {
					saveAS();
				}
			};
		}
		return actionSaveAs;
	}
	public Action getActionSaveAsDefault() {
		if (actionSaveAsDefault == null) {
			actionSaveAsDefault = new AbstractAction(TEXTS.saveAsDefault) {
				public void actionPerformed(ActionEvent e) {
					saveFile(getDefaultFile());
				}
			};
			actionSaveAsDefault.setEnabled(false);
		}
		return actionSaveAsDefault;
	}
	
	public Action getActionOpenInSystem() {
		if (actionOpenInSystem == null) {
			actionOpenInSystem = new AbstractAction(TEXTS.openInSystem) {
				public void actionPerformed(ActionEvent e) {
					try {
						Files.open(getCurrentLoadedFile());
					} catch (Exception ex) {
						Console.printException(ex);
					}
				}
			};
		}
		return actionOpenInSystem;
	}
	
	public LCombo<File> getComboLoadFastFiles() {
		if (comboLoadFastFiles == null) {
			comboLoadFastFiles = new LCombo<File>() {
				@Override
				public Dimension getPreferredSize() {
					Dimension size = super.getPreferredSize();
					int w = Math.min(250, size.width);
					int h = size.height;
					return new Dimension(w, h);
				}
			};
			//comboLoadFastFiles.setPreferredSize(new Dimension(150, comboLoadFastFiles.getPreferredSize().height));
			//comboLoadFastFiles.addItem(NO_FILE);
			//comboLoadFastFiles.setSelectedIndex(0);
			comboLoadFastFiles.addItemListener(itemListener);
			comboLoadFastFiles.setRenderer(new FileRender());
			refreshCombo(-1);
		}
		return comboLoadFastFiles;
	}

	public JMenu getMenuLoadFastFiles() {
		if (menuLoadFastFiles == null) {
			menuLoadFastFiles = new JMenu(TEXTS.recentFiles);
			refreshMenu(-1);
		}
		return menuLoadFastFiles;
	}
	
	private ButtonGroup getMenuItemsGroup() {
		if (menuItemsGroup == null) {
			menuItemsGroup = new ButtonGroup();
		}
		return menuItemsGroup;
	}
	
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser(getDefaultFolder());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		return fileChooser;
	}
	
	private File getHistoryFile() {
		if (historyFile == null) {
			File root = new File(getDefaultFolder(), TEXTS.hystory);
			String fileName = getName() != null && !getName().trim().equals(Constants.VOID) ? getName() : "default";
			historyFile = new File(root, fileName + ".history");
		}
		return historyFile;
	}
	
	public List<String> getHistoryPaths() {
		if (historyPaths == null) {
			try {
				historyPaths = Files.readLines(getHistoryFile());
			} catch (IOException ioex) {
				historyPaths = Lists.newList();
			}
		}
		return historyPaths;
	}
	
	public String registerFile(File file) {
		
		String errorMessage = null;
		if (file != null) {
			
			int indexFile = getHistoryPaths().indexOf(file.getAbsolutePath());
			int indexDefault = getDefaultFile() != null ? getHistoryPaths().indexOf(getDefaultFile().getAbsolutePath()) : -1;
			if (indexFile != -1)
				getHistoryPaths().remove(indexFile);
			int newIndex = getDefaultFile() == null || file.getAbsolutePath().equals(getDefaultFile().getAbsolutePath()) || indexDefault == -1 ? 0 : 1;
			
			getHistoryPaths().add(newIndex, file.getAbsolutePath());
			try {
				Files.saveText(getHistoryPaths(), getHistoryFile());
				refreshHistoryComponents(newIndex);
			}
			catch (IOException ioex) {
				Console.printException(ioex);
				errorMessage = ioex.toString();
			}
		}
		return errorMessage;
	}
	
	private void refreshHistoryComponents(int selectedIndex) {
		refreshCombo(selectedIndex);
		refreshMenu(selectedIndex);
	}
	
	private void refreshCombo(int selectedIndex) {
		if (comboLoadFastFiles != null) {
			try {
				getComboLoadFastFiles().removeItemListener(itemListener);
				getComboLoadFastFiles().removeAllItems();
				for (int i = 0; i < getHistoryPaths().size(); i++) {
					getComboLoadFastFiles().addItem(new File(getHistoryPaths().get(i)));
				}
				getComboLoadFastFiles().addItem(NO_FILE);
				if (selectedIndex == -1)
					selectedIndex = getComboLoadFastFiles().getItemCount() - 1;
				getComboLoadFastFiles().setSelectedIndex(selectedIndex);
			}
			finally {
				getComboLoadFastFiles().addItemListener(itemListener);
			}
		}
	}
	
	private void refreshMenu(int selectedIndex) {

		if (menuLoadFastFiles != null) {
			menuItemsGroup = null;
			getMenuLoadFastFiles().removeAll();
			JRadioButtonMenuItem selectedMenuItem = null;
			for (int i = 0; i < getHistoryPaths().size(); i++) {
				FileAction fileAction = new FileAction(new File(getHistoryPaths().get(i)));
				JRadioButtonMenuItem radioMenuItem = new JRadioButtonMenuItem(fileAction);
				getMenuItemsGroup().add(radioMenuItem);
				getMenuLoadFastFiles().add(radioMenuItem);
				if (i == selectedIndex)
					selectedMenuItem = radioMenuItem;
			}
			FileAction fileActionNofile = new FileAction(NO_FILE);
			JRadioButtonMenuItem radioMenuItemNoFile = new JRadioButtonMenuItem(fileActionNofile);
			getMenuItemsGroup().add(radioMenuItemNoFile);
			getMenuLoadFastFiles().add(radioMenuItemNoFile);
			if (selectedIndex == -1)
				selectedMenuItem = radioMenuItemNoFile;
			
			selectedMenuItem.setSelected(true);
		}
	}
	
	public String loadFile(File file) {
		
		if (file == NO_FILE)
			file = null;
		
		String errorMessage = closeFile(getCurrentLoadedFile());
		
		if (errorMessage == null) {
			errorMessage = registerFile(file);
			if (errorMessage == null) {
				if (file != null)
					errorMessage = fireLoadSaveEvent(LoadSaveEvent.TYPE_LOAD, file);
			}
		}
		
		if (errorMessage == null) {
			setCurrentLoadedFile(file);
		}
		else {
			selectFastFile(getCurrentLoadedFile());
			MessageDialog.showMessage(errorMessage, MessageDialog.ICON_ERROR);
			if (getDefaultFile() == null || !getDefaultFile().getAbsolutePath().equals(file.getAbsolutePath()))
				getHistoryPaths().remove(file.getAbsolutePath());
			refreshHistoryComponents(-1);
		}
		
		return errorMessage;
	}
	
	private void selectFastFile(File file) {
		
		if (comboLoadFastFiles != null) {
			try {
				getComboLoadFastFiles().removeItemListener(itemListener);
				getComboLoadFastFiles().setSelectedItem(file);	
			}
			finally {
				getComboLoadFastFiles().addItemListener(itemListener);
			}
		}
		if (menuLoadFastFiles != null) {
			for (int i = 0; i < getMenuLoadFastFiles().getItemCount(); i++) {
				JMenuItem menuItem = getMenuLoadFastFiles().getItem(i);
				FileAction fAction = (FileAction) menuItem.getAction();
				menuItem.setSelected(fAction.getFile() == file);
			}
		}
	}
	
	public String saveFile(File file) {
		String errorMessage = fireLoadSaveEvent(LoadSaveEvent.TYPE_SAVE, file);
		if (errorMessage != null) {
			MessageDialog.showMessage(errorMessage, MessageDialog.ICON_ERROR);
		}
		else {
			setCurrentFileModified(false);
		}
		return errorMessage;
	}
	
	public String closeFile(File file) {
		String errorMessage = null;
		if (getCurrentLoadedFile() != null) {
			if (isCurrentFileModified()) {
				int respuesta = MessageDialog.showMessage(TEXTS.messageSaveCurrent, MessageDialog.ICON_WARNING, ButtonsPanel.ASPECT_YES_NO_CANCEL);
				if (respuesta == ButtonsPanel.RESPONSE_ACCEPT_YES) {
					errorMessage = saveFile(getCurrentLoadedFile());
				}
				else {
					errorMessage =  respuesta == ButtonsPanel.RESPONSE_NO ? null : TEXTS.processCancelled;
				}
			}
			
			if (errorMessage == null) {
				errorMessage = fireLoadSaveEvent(LoadSaveEvent.TYPE_CLOSE, file);
				if (errorMessage == null) {
					setCurrentLoadedFile(null);
					refreshHistoryComponents(-1);
				}
			}
		}
		
		return errorMessage;
	}
	
	public void loadDefault() {
		if (getDefaultFile() != null)
			loadFile(getDefaultFile());
	}
	
	private void loadFrom() {
		
		File selectedFile = FilesGUI.chooseFile(FilesGUI.MODE_LOAD, getDefaultFolder());
		if (selectedFile != null) {
			loadFile(selectedFile);
		}
	}
	private String saveAS() {
		
		String errorMessage = null;
		File saveFile = FilesGUI.chooseFile(FilesGUI.MODE_SAVE, getDefaultFolder());
		if (saveFile != null) {
			String fileName = saveFile.getName();
			if (saveFile.exists()) {
				int respuesta = MessageDialog.showMessage(TEXTS.messageOverwriteCurrent+"\""+fileName+"\"?", MessageDialog.ICON_QUESTION);
				if (respuesta == ButtonsPanel.RESPONSE_NO)
					return null;
			}
			else if (getFileNameExtensionFilter() != null && fileName.indexOf(Constants.POINT) == -1) {
				String ext = getFileNameExtensionFilter().getExtensions()[0];
				String fileNameWithExt = ext.startsWith(Constants.POINT) ? fileName + ext : fileName + Constants.POINT + ext;
				saveFile = new File(saveFile.getParentFile(), fileNameWithExt);
			}
			errorMessage = saveFile(saveFile);
			if (errorMessage == null)
				errorMessage = registerFile(saveFile);
		}
		return errorMessage;
	}
	
	protected EventListenerList getEventListenerList() {
		if (eventListenerList == null)
			eventListenerList = new EventListenerList();
		return eventListenerList;
	}
	public void addLoadSaveListener(LoadSaveListener l) {
		getEventListenerList().add(LoadSaveListener.class, l);
	}
	public void removeLoadSaveListener(LoadSaveListener l) {
		getEventListenerList().remove(LoadSaveListener.class, l);
	}
	
	private String fireLoadSaveEvent(int eventType, File file) {
		//Lanzamos el evento
		String errorMessage = null;
		Object[] listeners = getEventListenerList().getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == LoadSaveListener.class) {
				LoadSaveEvent lsEvent = new LoadSaveEvent(this, file, eventType);
				LoadSaveListener loadSaveListener = (LoadSaveListener) listeners[i + 1];
				if (eventType == LoadSaveEvent.TYPE_LOAD)
					errorMessage = loadSaveListener.load(lsEvent);
				else if (eventType == LoadSaveEvent.TYPE_SAVE)
					errorMessage = loadSaveListener.save(lsEvent);
				else if (eventType == LoadSaveEvent.TYPE_CLOSE)
					errorMessage = loadSaveListener.close(lsEvent);
				//Si no termina bien, cancelamos la propagación a otros posibles escuchadores
				if (errorMessage != null)
					return errorMessage;
			}
		}
		return errorMessage;
	}
	
	public File getDefaultFolder() {
		if (defaultFolder == null) {
			defaultFolder = new File(Directories.getAppGeneratedFiles(), getName());
			if (!defaultFolder.exists())
				Files.createDir(defaultFolder);
			getFileChooser().setCurrentDirectory(defaultFolder);
		}
		return defaultFolder;
	}
	public void setDefaultFolder(File defaultFolder) {
		this.defaultFolder = defaultFolder;
		if (!defaultFolder.exists())
			Files.createDir(defaultFolder);
		getFileChooser().setCurrentDirectory(defaultFolder);
	}

	public File getCurrentLoadedFile() {
		return currentLoadedFile;
	}
	private void setCurrentLoadedFile(File currentLoadedFile) {
		this.currentLoadedFile = currentLoadedFile;
		setCurrentFileModified(false);
		boolean fileLoaded = currentLoadedFile != null;
		boolean isDefaultFile = fileLoaded && getDefaultFile() != null && getDefaultFile().equals(currentLoadedFile);
		getActionClose().setEnabled(fileLoaded);
		getActionSave().setEnabled(false);
		getActionSaveAs().setEnabled(isSaveNoFileAllowed() || fileLoaded);
		getActionSaveAsDefault().setEnabled(!isDefaultFile && (isSaveNoFileAllowed() || fileLoaded));
		getActionOpenInSystem().setEnabled(fileLoaded);
	}

	public File getDefaultFile() {
		return defaultFile;
	}
	public void setDefaultFile(File defaultFile) {
		
		File oldDefaultFile = this.defaultFile;
		if (Utils.propertyChanged(oldDefaultFile, defaultFile)) {
			
			this.defaultFile = defaultFile;
			File selectedItem = oldDefaultFile == getCurrentLoadedFile() ? NO_FILE : getCurrentLoadedFile();
			if (defaultFile != null)
				registerFile(defaultFile);
			getActionSaveAsDefault().setEnabled(defaultFile != null);
			
			selectFastFile(selectedItem);
		}
	}

	public boolean isCurrentFileModified() {
		return currentFileModified;
	}
	public void setCurrentFileModified(boolean currentFileModified) {
		this.currentFileModified = currentFileModified;
		boolean fileLoaded = getCurrentLoadedFile() != null;
		getActionSave().setEnabled(fileLoaded && currentFileModified);
		getActionSaveAs().setEnabled(currentFileModified);
		getActionSaveAsDefault().setEnabled(currentFileModified);
		if (comboLoadFastFiles != null)
			getComboLoadFastFiles().repaint();
	}

	public int getMaxHistoryFiles() {
		return maxHistoryFiles;
	}
	public void setMaxHistoryFiles(int maxHistoryFiles) {
		this.maxHistoryFiles = maxHistoryFiles;
	}
	
	public FileNameExtensionFilter getFileNameExtensionFilter() {
		return fileNameExtensionFilter;
	}
	public void setFileNameExtensionFilter(FileNameExtensionFilter fileNameExtensionFilter) {
		if (this.fileNameExtensionFilter != null)
			getFileChooser().removeChoosableFileFilter(this.fileNameExtensionFilter);
		
		this.fileNameExtensionFilter = fileNameExtensionFilter;
		
		if (fileNameExtensionFilter != null) {
			getFileChooser().addChoosableFileFilter(fileNameExtensionFilter);
			getFileChooser().setFileFilter(fileNameExtensionFilter);
		}
	}
	
	public String getName() {
		return name;
	}
	public Component getParent() {
		return parent;
	}
	public void setParent(Component parent) {
		this.parent = parent;
	}
	
	public boolean isSaveNoFileAllowed() {
		return saveNoFileAllowed;
	}

	public void setSaveNoFileAllowed(boolean saveNoFileAllowed) {
		this.saveNoFileAllowed = saveNoFileAllowed;
	}
}
