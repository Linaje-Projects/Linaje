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
package linaje.gui.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import linaje.comunications.Connection;
import linaje.comunications.ConnectionEvent;
import linaje.comunications.ConnectionListener;
import linaje.comunications.ConnectionsServer;
import linaje.gui.AppGUI;
import linaje.gui.FieldsPanel;
import linaje.gui.LButton;
import linaje.gui.LButtonProperties;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.LoadSaveActions;
import linaje.gui.LoadSaveEvent;
import linaje.gui.LoadSaveListener;
import linaje.gui.LoadSavePanel;
import linaje.gui.components.FileNameExtensionFilter;
import linaje.gui.tests.TestPanel;
import linaje.gui.tests.UITest;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Files;
import linaje.utils.Lists;
import linaje.utils.Processes;
import linaje.utils.Resources;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class UIConfig extends LPanel {

	//Puerto para conectar con la ventana de pruebas 'UITest' y refrescarla según cambiemos cosas en UIConfig
	public static String PREFIX_PARAM_PORT = "port:";
	private int connectionPort = Connection.PORT_DEFAULT;
	ConnectionsServer servidor = null;
	
	private LTabbedPane tabbedPane = new LTabbedPane();
	private List<FieldsPanel> fieldPanels = Lists.newList();
	
	private LoadSaveActions loadSaveActions = null;
	private LoadSavePanel loadSavePanel = null;
	
	private boolean loadingConfigFile = false;
	
	private LDialogContent ldialogContent = null;
	
	private static File defaultConfigFile = null;
			
	private static final String CONFIG_DIR_NAME = "UI Config";
	private static final File CONFIG_DIR_DEFAULT = new File(Directories.getAppGeneratedFiles(), CONFIG_DIR_NAME);
	private static final String CONFIG_FILE_NAME = "defaultUIConfig.cfg";
	private static final File CONFIG_FILE_DEFAULT = new File(CONFIG_DIR_DEFAULT, CONFIG_FILE_NAME);
	public static final File CONFIG_FILE_TEST = new File(Directories.getAppGeneratedFiles(), "testUIConfig.cfg");
	
	private static FileNameExtensionFilter CONFIG_FILES_FILTER = new FileNameExtensionFilter("Config files", "cfg");
	
	List<ComponentUIProperties> allComponentUIsProperties = Lists.newList();
	
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			
			final String propertyName = evt.getPropertyName();
			final int fromIndex = loadingConfigFile ? 0 : tabbedPane.getSelectedIndex();
			//Actualizamos los fieldsPanels que puedan referenciar ReferencedColors del valor cambiado
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (int i = fromIndex; i < fieldPanels.size(); i++) {
						fieldPanels.get(i).reload(propertyName);
					}
				}
			});
			
			if (!loadingConfigFile) {
				getLoadSaveActions().setCurrentFileModified(true);
				saveConfigFile(CONFIG_FILE_TEST);
				if (servidor != null && !servidor.getConnections().isEmpty())
					servidor.getConnections().elementAt(0).sendComunication("propertyChanged");
				reloadUI();
			}
		}
	};
	
	
	public UIConfig() throws UnsupportedLookAndFeelException {
		super();
		initialize();
	}
	
	private void initialize() throws UnsupportedLookAndFeelException {
		
		//UIManager.setLookAndFeel(new LinajeLookAndFeel(UIConfig.CONFIG_FILE));
		
		//GeneralProperties
		GeneralUIProperties.getInstance().addPropertyChangeListener(propertyChangeListener);
		
		TestPanel testPanelGeneral = new TestPanel(new JPanel(), false);
		testPanelGeneral.getFieldsPanel().addAccessComponentsFromFields(GeneralUIProperties.getInstance(), GeneralUIProperties.getInstance().getFieldsChangeSupport().getFieldsChangedValues());
		testPanelGeneral.getScrollPaneFields().setBorder(BorderFactory.createEmptyBorder(5,1,5,1));
		tabbedPane.addTab("General", testPanelGeneral.getScrollPaneFields());
		fieldPanels.add(testPanelGeneral.getFieldsPanel());
		
		//Buttons Properties
		Collection<ButtonUIProperties> buttonUIsProperties = UISupportButtons.getDefaultButtonUIPropertiesMap().values();
		for (Iterator<ButtonUIProperties> iterator = buttonUIsProperties.iterator(); iterator.hasNext();) {
			
			ButtonUIProperties buttonUIProperties = iterator.next();
			buttonUIProperties.addPropertyChangeListener(propertyChangeListener);
			buttonUIProperties.getLButtonProperties().addPropertyChangeListener(propertyChangeListener);
			
			TestPanel testPanel = new TestPanel(new JPanel(), false);
			testPanel.getFieldsPanel().addAccessComponentsFromFields(buttonUIProperties, buttonUIProperties.getFieldsChangeSupport().getFieldsChangedValues());
			testPanel.getFieldsPanel().addAccessComponentsFromFields(buttonUIProperties.getLButtonProperties(), buttonUIProperties.getLButtonProperties().getFieldsChangeSupport().getFieldsChangedValues());
			
			String componentName = LinajeLookAndFeel.getUIName(buttonUIProperties.getUiClass());
			testPanel.getScrollPaneFields().setBorder(BorderFactory.createEmptyBorder(5,1,5,1));
			tabbedPane.addTab(componentName, testPanel.getScrollPaneFields());
			fieldPanels.add(testPanel.getFieldsPanel());
			allComponentUIsProperties.add(buttonUIProperties);
		}
		
		//Other components Properties
		Collection<ComponentUIProperties> componentUIsProperties = UISupport.getDefaultComponentUIPropertiesMap().values();
		for (Iterator<ComponentUIProperties> iterator = componentUIsProperties.iterator(); iterator.hasNext();) {
			
			ComponentUIProperties componentUIProperties = iterator.next();
			componentUIProperties.addPropertyChangeListener(propertyChangeListener);
			
			TestPanel testPanel = new TestPanel(new JPanel(), false);
			testPanel.getFieldsPanel().addAccessComponentsFromFields(componentUIProperties, componentUIProperties.getFieldsChangeSupport().getFieldsChangedValues());
			
			String componentName = LinajeLookAndFeel.getUIName(componentUIProperties.getUiClass());
			testPanel.getScrollPaneFields().setBorder(BorderFactory.createEmptyBorder(5,1,5,1));
			tabbedPane.addTab(componentName, testPanel.getScrollPaneFields());
			fieldPanels.add(testPanel.getFieldsPanel());
			allComponentUIsProperties.add(componentUIProperties);
		}
				
		//Test Button
		final LButton btnTestUI = new LButton("Test changes");
		btnTestUI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					btnTestUI.setEnabled(false);
					servidor = ConnectionsServer.getServer(connectionPort);
					servidor.addConnectionListener(new ConnectionListener() {
						
						public void connectionDone(ConnectionEvent evt) {	
							Console.println("Conexión establecida de " + evt.getConnection().getName());
						}
						
						public void connectionEnd(ConnectionEvent evt) {
							Console.println("Conexión finalizada de " + evt.getConnection().getName());
							btnTestUI.setEnabled(true);
							servidor.removeConnectionListener(this);
						}
						
						public void comunicationReceived(ConnectionEvent evt) {
							Console.println("Comunicación recibida de " + evt.getConnection().getName() + ": ");
							Console.println(" - " + evt.getComunicationReceived());
						}
						public void connectionFailed(ConnectionEvent evt) {
							Console.println(evt.getComunicationReceived());
							btnTestUI.setEnabled(true);
							servidor.removeConnectionListener(this);
						}
					});
					
					Processes.executeJava(UITest.class, null, PREFIX_PARAM_PORT + connectionPort);
					
					servidor.initServer();
					
					while (servidor.getConnections().isEmpty()) {
						Thread.sleep(1000);
					}
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
		});
		
		//setLayout(new BorderLayout());
		
		//add(btnTestUI, BorderLayout.NORTH);
		//add(tabbedPane, BorderLayout.CENTER);
		
		setLayout(new GridBagLayout());
		
		//btnTestUI.setMaximumSize(btnTestUI.getPreferredSize());
		//btnTestUI.setMinimumSize(btnTestUI.getPreferredSize());
		
		GridBagConstraints gbcLoadSavePanel = new GridBagConstraints();
		gbcLoadSavePanel.anchor = GridBagConstraints.NORTH;
		gbcLoadSavePanel.insets = new Insets(5, 5, 5, 5);
		gbcLoadSavePanel.gridx = 1;
		gbcLoadSavePanel.gridy = 1;
		gbcLoadSavePanel.weightx = 1.0;
		gbcLoadSavePanel.weighty = 0.0;
		gbcLoadSavePanel.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints gbcBtnTest = new GridBagConstraints();
		gbcBtnTest.anchor = GridBagConstraints.NORTH;
		gbcBtnTest.insets = new Insets(0, 5, 5, 5);
		gbcBtnTest.gridx = 1;
		gbcBtnTest.gridy = 2;
		gbcBtnTest.weightx = 1.0;
		gbcBtnTest.weighty = 0.0;
		gbcBtnTest.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints gbcTabbedPane = new GridBagConstraints();
		gbcTabbedPane.insets = new Insets(0, 5, 5, 5);
		gbcTabbedPane.gridx = 1;
		gbcTabbedPane.gridy = 3;
		gbcTabbedPane.weightx = 1.0;
		gbcTabbedPane.weighty = 1.0;
		gbcTabbedPane.fill = GridBagConstraints.BOTH;
		
		add(getLoadSavePanel(), gbcLoadSavePanel);
		add(btnTestUI, gbcBtnTest);
		add(tabbedPane, gbcTabbedPane);
		
		AppGUI.getCurrentAppGUI().getFrame().setJMenuBar(getLoadSavePanel().getMenuBar());
		
		saveConfigFile(CONFIG_FILE_TEST);
		
		if (getLoadSaveActions().getHistoryPaths().size() < 2) {
			String themesDirName = "themes";
			File dirThemes = new File(CONFIG_DIR_DEFAULT, themesDirName);
			if (!dirThemes.exists()) {
				//Si no existe el directorio  de temas, lo creamos y copiamos en él los temas almacenados como recurso
				try {			
					List<String> resourceNames = Resources.getResourceNamesFromResourceDir(themesDirName);
					for (String resourceName : resourceNames) {
						String fileName = resourceName.substring(resourceName.lastIndexOf(Resources.RESOURCE_PATH_SEPARATOR));
						File themeFile = new File(dirThemes, fileName);
						Files.copyResource(resourceName, themeFile);
						if (CONFIG_FILES_FILTER.accept(themeFile))
							getLoadSaveActions().registerFile(themeFile);
					}
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
			else {
				List<File> themes = Files.getFilesFromDir(dirThemes, CONFIG_FILES_FILTER);
				for (File themeFile : themes) {
					getLoadSaveActions().registerFile(themeFile);
				}
			}
		}
	}
	
	private LoadSaveActions getLoadSaveActions() {
		if (loadSaveActions == null) {
			loadSaveActions = new LoadSaveActions(CONFIG_DIR_NAME);
			loadSaveActions.setFileNameExtensionFilter(CONFIG_FILES_FILTER);
			loadSaveActions.setDefaultFile(UIConfig.getDefaultConfigFile());
			loadSaveActions.setSaveNoFileAllowed(true);
			
			loadSaveActions.addLoadSaveListener(new LoadSaveListener() {
				
				public String save(LoadSaveEvent lsEvent) {
					return saveConfigFile(lsEvent.getFile());
				}
				public String load(LoadSaveEvent lsEvent) {
					return loadConfigFile(lsEvent.getFile());
				}
				public String close(LoadSaveEvent lsEvent) {
					return loadConfigFile(null);
				}
			});
		}
		return loadSaveActions;
	}
	
	private LoadSavePanel getLoadSavePanel() {
		if (loadSavePanel == null) {
			loadSavePanel = new LoadSavePanel(getLoadSaveActions());
			
		}
		return loadSavePanel;
	}
	
	private String saveConfigFile(File configFile) {
		try {
			StringBuffer sb = new StringBuffer();
			//UISupport.encodeUIsFieldsChanged(sb);
			encodeUIsFieldsChanged(sb);
			Files.saveText(sb.toString(), configFile);
			return null;
		}
		catch (Exception ex) {
			return Strings.getExceptionMessage(ex);
		}
	}
	
	/*
	 * En reloadUI cambian las instancias de los ComponentUIProperties y ButtonUIProperties
	 * por lo que cojemos los de los fieldsPanelActuales para guardar
	 */
	private void encodeUIsFieldsChanged(StringBuffer sb) {
		
		GeneralUIProperties.getInstance().encodeFieldsChanged(sb);
		
		for (ComponentUIProperties uiProp : allComponentUIsProperties) {
			uiProp.encodeFieldsChanged(sb);
		}
	}
	
	private String loadConfigFile(final File configFile) {
		
		try {
			loadingConfigFile = true;
			resetDefaultFieldsValues();
			String[] encodedFields = LinajeLookAndFeel.getEncodedFields(configFile);
			updateUIsPropertiesFromEncodedFields(encodedFields);
			for (int i = 0; i < fieldPanels.size(); i++) {
				FieldsPanel fp = fieldPanels.get(i);
				fp.selectModifiedFields();
			}
			if (servidor != null && !servidor.getConnections().isEmpty())
				servidor.getConnections().elementAt(0).sendComunication("propertyChanged");
			
			saveConfigFile(CONFIG_FILE_TEST);
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					reloadUI();
					if (configFile == null)
						reloadUI();//Si no cargamos fichero de configuración, refrescamos dos veces para que se actualicen bien los colores de los Textfields
				}
			});
			
			return null;
		}
		catch (Exception ex) {
			return Strings.getExceptionMessage(ex);
		}
		finally {
			loadingConfigFile = false;
		}
	}
	
	private void resetDefaultFieldsValues() {
		
		GeneralUIProperties.getInstance().getFieldsChangeSupport().resetDefaultFieldsValues();
		
		for (int i = 0; i < allComponentUIsProperties.size(); i++) {
			ComponentUIProperties componentUIProperties = allComponentUIsProperties.get(i);
			componentUIProperties.getFieldsChangeSupport().resetDefaultFieldsValues();
			if (componentUIProperties instanceof ButtonUIProperties) {
				LButtonProperties lButtonProperties = ((ButtonUIProperties) componentUIProperties).getLButtonProperties();
				lButtonProperties.getFieldsChangeSupport().resetDefaultFieldsValues();
			}
		}
	}
	
	private void updateUIsPropertiesFromEncodedFields(String... encodedFields) {
		
		GeneralUIProperties.getInstance().updateUIPropertiesFromEncodedFields(encodedFields);
		
		for (int i = 0; i < allComponentUIsProperties.size(); i++) {
			ComponentUIProperties componentUIProperties = allComponentUIsProperties.get(i);
			componentUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
		}
	}
	
	private void reloadUI() {
		LinajeLookAndFeel.getInstance().reloadDefaults(CONFIG_FILE_TEST);
		SwingUtilities.updateComponentTreeUI(AppGUI.getCurrentAppGUI().getFrame());
		if (ldialogContent != null) {
			ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
			buttonsPanel.setAutoCloseOnAccept(true);
			ldialogContent.setButtonsPanel(buttonsPanel);
		}
	}
	
	public static File getDefaultConfigFile() {
		
		if (defaultConfigFile == null) {
			//Primero buscaremos el fichero de configuración en el directorio de guardado de de la aplicación,
			//luego en el directorio de ejecución y finalmente en los recursos de la aplicación
			defaultConfigFile = CONFIG_FILE_DEFAULT;
			
			try {
				
				File execDirConfigFile = new File(Directories.APP_EXECUTION_DIR, CONFIG_FILE_NAME);
				if (!defaultConfigFile.exists() || !defaultConfigFile.isFile()) {
					
					if (defaultConfigFile.exists() && !defaultConfigFile.isFile())
						defaultConfigFile.delete();
					
					
					if (execDirConfigFile != null && execDirConfigFile.exists() && execDirConfigFile.isFile()) {
						Files.copyFile(execDirConfigFile, defaultConfigFile);
					}
					else {	
						Files.copyResource(CONFIG_FILE_NAME, defaultConfigFile);
					}
				}
				else if (execDirConfigFile.isFile()) {
					//Si existe fichero de configuración del directorio de aplicación y es mas nuevo que el de guardado
					//actualizamos el de guardado
					Files.update(defaultConfigFile, execDirConfigFile);
				}
			}
			catch (Exception ex) {
				Console.println("No existe fichero de configuración de UI, creamos uno nuevo en " + defaultConfigFile.getPath());
				try {
					Files.saveText(Constants.VOID, defaultConfigFile);
				} catch (Exception ex2) {
					Console.printException(ex2);
				}
			}
		}
		return defaultConfigFile;
	}

	public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init();
			
			Console.setConsoleWindowEnabled(true);
			Console.println("Iniciando UIConfig...");
			final UIConfig uiConfig = new UIConfig();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					uiConfig.getLoadSaveActions().loadFile(UIConfig.getDefaultConfigFile());
				}
			});
			
			
			for (int i = 0; args != null && i < args.length; i++) {
				try {
					String param = args[0];
					String paramPrefix = UIConfig.PREFIX_PARAM_PORT;
					if (param.toLowerCase().startsWith(paramPrefix))
						uiConfig.connectionPort = Integer.parseInt(param.substring(paramPrefix.length()));
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
			
			uiConfig.ldialogContent = LDialogContent.showComponentInFrame(uiConfig);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
