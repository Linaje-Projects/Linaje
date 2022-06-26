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
package linaje.gui.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import linaje.App;
import linaje.LocalizedStrings;
import linaje.comunications.Connection;
import linaje.comunications.ConnectionEvent;
import linaje.comunications.ConnectionListener;
import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.Task;
import linaje.gui.Tasks;
import linaje.gui.components.ColorButton;
import linaje.gui.components.DialogFindText;
import linaje.gui.editor.LEditor;
import linaje.gui.editor.LTextPane;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;
import linaje.logs.ConsoleProtocol;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Colors;
import linaje.utils.Files;
import linaje.utils.Lists;
import linaje.utils.Strings;

/**
 * <b>Funcionalidad:</b><br>
 * Mostrar las trazas de Consola
 * <p>
 * <b>Uso:</b><br>
 *
 * Ejecutarlo a la vez que la aplicación que se quiera ver las trazas por consola
 *
 * @author Pablo Linaje
 */
@SuppressWarnings("serial")
public class ConsoleWindow extends JFrame implements MouseListener, WindowListener {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String console;
		public String consoleFull;
		public String previousTraces;
		public String btnConnectTip;
		public String openAuto;
		public String optimizeForeground;
		public String waitingForApp;
		public String checkSomething;
		public String connectionDone;
		public String connectionDoneTip;
		public String connectionEnd;
		public String connectionEndTip;
		public String connectionFailed;
		public String at;
		public String findText;
		public String ksFindText;
		
				
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final String FONT_CONSOLE = "Consolas";
	
	private JPanel mainPanel = null;
	private JPanel panelEast = null;
	private JPanel panelWest = null;
	private JPanel panelOptions = null;
	
	private LCheckBox chkOpenAuto = null;
	private LCheckBox chkOptimizeForeground = null;
	
	private LEditor editor = null;
	private LEditor editorAux = null;
	private LButton btnConnect = null;
	private LButton btnSearch = null;
	private DialogFindText dialogFindText = null;
	
	private HashMap<String, LCheckBox> mapDataChecks = null;
	private static final String DATA_CHECK_NAME = "dataConsole";
	
	private static InetAddress IP = null;
	
	private ConsoleProtocol csp = null;
	
	private Color foreground = new Color(216, 216, 216);
	private Color background = Color.black;
	private int numConsoles = 0;
	private String currentText = null;
	private Color currentColor = null;
	private int currentTraceCode = -1;
	private boolean checkPressedSelec = false;
	
	private Connection connection = null;
	private File tempDir = null;
	private String appName = null;
	private List<String> dataTypeNames = null;
	
	public ConsoleWindow() {
		this(null, null);
	}
	public ConsoleWindow(String appName, List<String> dataTypeNames) {
		super();
		setAppName(appName);
		setDataTypeNames(dataTypeNames);
		initialize();
	}
	
	private HashMap<String, LCheckBox> getMapDataChecks() {
		if (mapDataChecks == null) {
			mapDataChecks = new LinkedHashMap<String, LCheckBox>();
		}
		return mapDataChecks;
	}
	
	private LCheckBox getDataCheck(int index) {
		String key = DATA_CHECK_NAME + index;
		LCheckBox dataCheck = getMapDataChecks().get(key);
		if (dataCheck == null) {
			dataCheck = new LCheckBox(key, true);
			dataCheck.setName(key);
			//dataCheck.setFontSize(10);
			getMapDataChecks().put(key, dataCheck);
		}
		return dataCheck;
	}
	
	private void addButtonsToEditor() {
		getEditor().getToolBar().add(getBtnConnect(), 0);
		getEditor().getToolBar().add(getBtnSearch(), 1);
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		getEditor().getToolBar().add(separator, 1);
	}
	
	private synchronized void assignForeground(Color foreground) {
		try {
			LTextPane textPane = getEditor().getTextPane();
			ColorButton buttonForeground = getEditor().getColorButtonForeground();
			if (foreground == null) {
				try {
					boolean caretPositionAtEnd = textPane.getTextPlain().length() == textPane.getCaretPosition();
					Color colorDefault = buttonForeground.getDefaultColor();
					if (colorDefault != null && colorDefault != getForeground() && caretPositionAtEnd) {
						setForeground(colorDefault);
					}
				}
				finally {
					foreground = getForeground();
				}
			}
	
			//Optimizamos el color de la fuente
			if (getChkOptimizeForeground().isSelected()) {
				foreground = Colors.optimizeColor(foreground, textPane.getBackground());
			}
			
			//Asignamos el color de la fuente
			getEditor().changeForeground(foreground);
				
		} catch (Throwable ex){
			handleException(ex);
		}
	}
	
	private void loadConsoleConfig() {
		
		try {
			
			File fichero = Console.getConsolePropertiesFile();
			if (fichero.exists()) {		
				
				Properties properties = Files.readProperties(fichero);
			
				//boolean openAuto = new Boolean(properties.getProperty("openAuto")).booleanValue();
				//boolean optimizeForeground = new Boolean(properties.getProperty("optimizeForeground")).booleanValue();
				
				int r, g, b;
				int x, y, width, height;
				StringTokenizer st;
	
				st = new StringTokenizer(properties.getProperty("bounds"), Constants.SPACE);
				x = Integer.parseInt(st.nextToken());
				y = Integer.parseInt(st.nextToken());
				width = Integer.parseInt(st.nextToken());
				height = Integer.parseInt(st.nextToken());
				Rectangle bounds = new Rectangle(x, y, width, height);
				
				st = new StringTokenizer(properties.getProperty("foreground"), Constants.SPACE);
				r = Integer.parseInt(st.nextToken());
				g = Integer.parseInt(st.nextToken());
				b = Integer.parseInt(st.nextToken());
				Color foreground = new Color(r, g, b);
	
				st = new StringTokenizer(properties.getProperty("background"), Constants.SPACE);
				r = Integer.parseInt(st.nextToken());
				g = Integer.parseInt(st.nextToken());
				b = Integer.parseInt(st.nextToken());
				Color background = new Color(r, g, b);
				
				getChkOpenAuto().setSelected(true);
				getChkOptimizeForeground().setSelected(true);
				
				Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
				boolean anyCheckSelected = false;
				while (it.hasNext()) {
					LCheckBox check = it.next();
					boolean selected = new Boolean(properties.getProperty(check.getName())).booleanValue();
					if (!anyCheckSelected && selected)
						anyCheckSelected = true;
					check.setSelected(selected);
				}
				//Si se ha guardado la consola sin seleccionar nada, ponemos las trazas normales para que salga con algo
				if (!anyCheckSelected)
					getDataCheck(0).setSelected(true);
				
				setBounds(bounds);
				setForeground(foreground);
				assignForeground(foreground);
				setBackground(background);
	
				//Las siguientes propiedades las pongo al final y compruebo que no vengan a null 
				//porque se han añadido mas tarde y puede que alguien no las tenga
				String fontSize = "12";
				boolean bold = false;
				boolean italic = false;
				boolean underline = false;
	
				if (properties.getProperty("fontSize") != null)
					fontSize = properties.getProperty("fontSize").toString();
				if (properties.getProperty("bold") != null)
					bold = new Boolean(properties.getProperty("bold")).booleanValue();
				if (properties.getProperty("italic") != null)
					italic = new Boolean(properties.getProperty("italic")).booleanValue();
				if (properties.getProperty("underline") != null)
					underline = new Boolean(properties.getProperty("underline")).booleanValue();
	
				getEditor().changeFontSize(Integer.parseInt(fontSize));
	
				print(Constants.VOID, foreground);
				getEditor().getTextPane().requestFocus();
				
				if (bold) {
					getEditor().getTbtnBold().doClick();
				}
				if (italic) {
					getEditor().getTbtnItalic().doClick();
				}
				if (underline) {
					getEditor().getTbtnUnderline().doClick();
				}
				
			} else {
				loadDefaultConfig();	
			}
			getEditor().changeFontName(FONT_CONSOLE);
			setLocation(UtilsGUI.getWindowLocationAdjusted(this, getLocation()));
			
		} catch (Throwable ex) {
			loadDefaultConfig();
			handleException(ex);
		}
	}
	
	private void loadDefaultConfig() {
		try {
			setSize(600, 800);
			UtilsGUI.centerWindow(this);
			setForeground(getForeground());
			assignForeground(getForeground());
			setBackground(getBackground());
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	private void destroy() {
		
		try {
		
			saveConsoleConfig();
			getConnection().finalizeConnection();
	
			finalizeConnections();
	
			setVisible(false);
	
			getMainPanel().removeAll();
			getPanelEast().removeAll();
			getPanelWest().removeAll();
			getPanelOptions().removeAll();
			
			mainPanel = null;
			panelEast = null;
			panelWest = null;
			panelOptions = null;
			chkOpenAuto = null;
			chkOptimizeForeground = null;
			
			foreground = null;
			background = null;
			currentText = null;
			currentColor = null;
	
			if (editor != null)
				getEditor().destruir();
	
			editor = null;
			
			super.finalize();
			System.gc();
	
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	private synchronized void write() {
		
		JTextPane textPane = getEditor().getTextPane();
		StyledDocument doc = textPane.getStyledDocument();
		
		try {
			doc.insertString(doc.getLength(), currentText, textPane.getInputAttributes());
		} 
		catch (Throwable ex1) {
			ex1.printStackTrace();
			try {
				//Guardamos la consola
				numConsoles++;
				saveConsole();
				//Reiniciamos la consola
				getEditor().delete();
				assignForeground(Color.red);
				
				doc.insertString(doc.getLength(), TEXTS.consoleFull, textPane.getInputAttributes());
				doc.insertString(doc.getLength(), TEXTS.previousTraces + getTempDir().getAbsolutePath() + ": \n", textPane.getInputAttributes());
				for (int i = 1; i <= numConsoles; i++) {
					doc.insertString(doc.getLength(), "Console"+i+".html\n", textPane.getInputAttributes());
				}
				assignForeground(currentColor);
				doc.insertString(doc.getLength(), currentText, textPane.getInputAttributes());
				
				JViewport viewport = getEditor().getScrollPane().getViewport();
				viewport.setViewPosition(new Point(0, viewport.getViewSize().height));
				
				setVisible(true);
				toFront();
			} catch (Throwable ex2) {
				ex2.printStackTrace();
			}
		}
	}
	
	private void writeInConsole() {
		
		try {
			//Hacemos la consola visible si procede
			if (!isVisible()) {
				if (getChkOpenAuto().isSelected()) {
					setVisible(true);
				} else {
					Console.setConsoleWindowEnabled(false);
				}
			}
	
			//Asignamos el color de la fuente
			assignForeground(currentColor);
			getEditor().reasignAttributes();
			getEditor().changeFontName(FONT_CONSOLE);
	
			//Insertamos el texto con los atributos que procedan
			
			//Llamamos a "write()" con "invokeAndWait" porque sino se queda colgado por problemas de repintado
			SwingUtilities.invokeAndWait(new Runnable() {
				public synchronized void run() {
					write();
				}
			});
		}
		catch (Throwable ex) {
			ex.printStackTrace();	
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * 
	 */
	private void finalizeConnections() {
		
		this.removeWindowListener(this);
		
		Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
		while (it.hasNext()) {
			LCheckBox check = it.next();
			check.removeMouseListener(this);
		}
	}
	
	public Color getBackground() {
		return background;
	}
	
	private LButton getBtnSearch() {
		if (btnSearch == null) {
			
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					getDialogFindText().setJTextComponent(getEditor().getTextPane());
					getDialogFindText().showInDialog();
				}
			};
			
			btnSearch = new LButton(action);
			btnSearch.setToolTipText(TEXTS.findText + LocalizedStrings.getCtrlDesc(TEXTS.ksFindText));
			btnSearch.setIcon(Icons.SEARCH);
						
			KeyStroke keyStroke = KeyStroke.getKeyStroke(LocalizedStrings.getKeyCode(TEXTS.ksFindText), KeyEvent.CTRL_DOWN_MASK);
			btnSearch.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, keyStroke.toString());
			btnSearch.getActionMap().put(keyStroke.toString(), action);

			LEditor.asignButtonProperties(btnSearch);
		}
		return btnSearch;
	}
	
	private LButton getBtnConnect() {
		if (btnConnect == null) {
			btnConnect = new LButton();
			btnConnect.setToolTipText(TEXTS.btnConnectTip + getAppName());
			btnConnect.setIcon(Icons.CONNECT_OFF);
			btnConnect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (getConnection().isConnected() || getConnection().isRetrying()) {
							getConnection().finalizeConnection();
						}
						else {
							initConnection(false);
						}
					}
					catch (Throwable ex) {
						handleException(ex);
					}
				}
			});

			LEditor.asignButtonProperties(btnConnect);
		}
		return btnConnect;
	}
	
	protected LCheckBox getChkOpenAuto() {
		if (chkOpenAuto == null) {
			chkOpenAuto = new LCheckBox();
			chkOpenAuto.setSelected(true);
			chkOpenAuto.setText(TEXTS.openAuto);
		}
		return chkOpenAuto;
	}
	
	public LCheckBox getChkOptimizeForeground() {
		if (chkOptimizeForeground == null) {
			chkOptimizeForeground = new LCheckBox();
			chkOptimizeForeground.setSelected(true);
			chkOptimizeForeground.setText(TEXTS.optimizeForeground);
		}
		return chkOptimizeForeground;
	}
		
	private DialogFindText getDialogFindText() {
		if (dialogFindText == null) {
			dialogFindText = new DialogFindText(this, getEditor().getTextPane());
		}
		return dialogFindText;
	}
	
	private String getChecksStatus() {
		
		boolean consoleActive = getChkOpenAuto().isSelected() || this.isVisible();
		StringBuffer sb = new StringBuffer();
		sb.append(consoleActive);
		Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
		while (it.hasNext()) {
			LCheckBox check = it.next();
			sb.append(Console.SEPARATOR_DATA_CONSOLE);
			sb.append(check.isSelected());
		}
		
		return sb.toString();
	}
	
	public Color getForeground() {
		return foreground;
	}
	
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(getPanelOptions(), BorderLayout.SOUTH);
			mainPanel.add(getPanelEast(), BorderLayout.EAST);
			mainPanel.add(getPanelWest(), BorderLayout.WEST);
			mainPanel.add(getEditor(), BorderLayout.CENTER);
		}
		return mainPanel;
	}
	
	private String getHostName() {
		String[] tokens = Strings.split(IP.toString(), "/");
		return tokens[0];
	}
	
	private JPanel getPanelEast() {
		if (panelEast == null) {
			panelEast = new JPanel();
			panelEast.setOpaque(false);
			panelEast.setLayout(null);
			panelEast.setMaximumSize(new Dimension(10, 0));
			panelEast.setPreferredSize(new Dimension(10, 0));
			panelEast.setMinimumSize(new Dimension(10, 0));
		}
		return panelEast;
	}
	
	private JPanel getPanelWest() {
		if (panelWest == null) {
			panelWest = new JPanel();
			panelWest.setOpaque(false);
			panelWest.setLayout(null);
			panelWest.setMaximumSize(new Dimension(10, 0));
			panelWest.setPreferredSize(new Dimension(10, 0));
			panelWest.setMinimumSize(new Dimension(10, 0));
		}
		return panelWest;
	}
	
	private JPanel getPanelOptions() {
		if (panelOptions == null) {
			panelOptions = new JPanel();
			panelOptions.setOpaque(false);
			panelOptions.setLayout(new LFlowLayout(FlowLayout.LEFT, 5, 1, true));
			//panelOptions.add(getChkOpenAuto());
			//panelOptions.add(getChkOptimizeForeground());
			Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
			while (it.hasNext()) {
				LCheckBox check = it.next();
				panelOptions.add(check, check.getName());
			}
		}
		return panelOptions;
	}
	
	public LEditor getEditor() {
		if (editor == null) {
			editor = new LEditor(LEditor.FORMAT_CONSOLE);
		}
		return editor;
	}
	
	private File getTempDir() {
		if (tempDir == null) {
			tempDir = new File(Directories.getAppGeneratedFiles(), "console");
		}
		return tempDir;
	}
	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}
	
	private void saveConsoleConfig() {
		try {
			
			Properties properties = new Properties();
	
			String openAuto = new Boolean(getChkOpenAuto().isSelected()).toString();
			String optimizeForeground = new Boolean(getChkOptimizeForeground().isSelected()).toString();
			
			Rectangle bounds = getBounds();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int maxWidth = screenSize.width - 50;
			int maxHeight = screenSize.height - 50;
			if (bounds.width > maxWidth)
				bounds.width = maxWidth;
			if (bounds.height > maxHeight)
				bounds.height = maxHeight;
				
			Color foreground = getForeground();
			Color background = getEditor().getColorButtonBackground().getSelectedColor();
			
			String fontSize = getEditor().getComboFontSize().getSelectedItem().toString();
			
			AttributeSet attributes = getEditor().getTextPane().getInputAttributes();
			
			Font font = getEditor().getTextPane().getStyledDocument().getFont(attributes);
			
			boolean bold = font.isBold();
			boolean italic = font.isItalic();
			boolean underline = StyleConstants.isUnderline(attributes);
					
			properties.put("openAuto", openAuto);
			properties.put("optimizeForeground", optimizeForeground);
			
			Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
			while (it.hasNext()) {
				LCheckBox check = it.next();
				properties.put(check.getName(), new Boolean(check.isSelected()).toString());
			}
			
			properties.put("bounds", bounds.x+" "+bounds.y+" "+bounds.width+" "+bounds.height);
			properties.put("foreground", foreground.getRed()+" "+foreground.getGreen()+" "+foreground.getBlue());
			properties.put("background", background.getRed()+" "+background.getGreen()+" "+background.getBlue());
			
			properties.put("fontSize", fontSize);
			properties.put("bold", ""+bold);
			properties.put("italic", ""+italic);
			properties.put("underline", ""+underline);
			
			final String comments = "Console config - " + getAppName();
			File fichero = Console.getConsolePropertiesFile();
			Files.saveProperties(properties, fichero, comments);
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	private void saveConsole() {
		
		try {
			
			//Creamos una copia del Document serializandola
			StyledDocument doc = getEditor().getTextPane().getStyledDocument();
			
			File fichero = new File(getTempDir(), "console_serialize");
			Files.serializeObject(doc, fichero);
			
			//Recuperamos la copia serializada
			StyledDocument docCopy = (StyledDocument) Files.readSerializedObject(fichero);
			
			//Iniciamos el editor auxiliar con la copia del Document
			editorAux = new LEditor(LEditor.FORMAT_CONSOLE);
			editorAux.getTextPane().setStyledDocument(docCopy);
			editorAux.getColorButtonForeground().setDefaultColor(getForeground());
			editorAux.getColorButtonBackground().setSelectedColor(getBackground());
			editorAux.getTextPane().setBackground(getBackground());
		}
		catch (Throwable exception) {
			handleException(exception);
		}
		
		//Guardamos el documento HTML en un Hilo
		Task<Void, Void> taskSave = new Task<Void, Void>() {
			protected Void doInBackground() throws Exception {
				try {
					File file = new File(getTempDir(), "Console"+numConsoles+".html");
					editorAux.save(file);
					editorAux = null;
				} catch (Throwable ex) {
					editorAux = null;
				}
				return null;
			}
			
		};
		
		Tasks.executeTask(taskSave, this);
	}
	
	private void handleException(Throwable exception) {
		printException(exception);
	}
	
	private boolean initConnection(boolean retry) {
	
		try {
			
			if (IP == null) {
				IP = InetAddress.getLocalHost();
			}
			if (retry) {
				println(TEXTS.waitingForApp + getAppName() + TEXTS.at + getHostName() + " : " + Console.getInstance().getPort(), null);
				getBtnConnect().setIcon(Icons.CONNECTING);
				
				int reintentos = 4;
				getConnection().initConnectionRetrying(IP.getHostName(), reintentos);
			}
			else {
				getConnection().initConnection(IP.getHostName());
			}
		}
		catch (Throwable ex) {
			//Si intentamos escribir en consola desde el Thread de eventos se queda colgada
			//handleException(ex);
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(600, 800);
		setContentPane(getMainPanel());
		
		setTitle(TEXTS.console + getAppName());
				
		addButtonsToEditor();
		loadConsoleConfig();
		
		//initConnections
		this.addWindowListener(this);
		
		Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
		while (it.hasNext()) {
			LCheckBox check = it.next();
			check.addMouseListener(this);
		}
		////////////////////////////
		
		getChkOpenAuto().setSelected(false);
	
		initConnection(false);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		
		try {
			
			String appName = null;
			String envID = null;
			List<String> dataTypeNames = null;
			if (args != null) {
				String host = App.getParamValue(args, "host");
				if (host != null) {
					try {
						IP = InetAddress.getByName(host);
					}catch (Throwable ex) {
					}
				}
				String port = App.getParamValue(args, "port");
				if (port != null) {
					try {
						Console.getInstance().setPort(Integer.parseInt(port));
					}catch (Throwable ex) {
					}
				}
				appName = App.getParamValue(args, "appName");
				if (appName != null) {
					appName = Strings.replace(appName, Console.SEPARATOR_DATA_CONSOLE, Constants.SPACE);
					appName = Strings.replace(appName, Console.OPEN_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_OPEN);
					appName = Strings.replace(appName, Console.CLOSE_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_CLOSE);
				}
				envID = App.getParamValue(args, "envID");
				if (envID != null) {
					envID = Strings.replace(envID, Console.SEPARATOR_DATA_CONSOLE, Constants.SPACE);
					envID = Strings.replace(envID, Console.OPEN_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_OPEN);
					envID = Strings.replace(envID, Console.CLOSE_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_CLOSE);
				}
				String dataTypes = App.getParamValue(args, "dataTypes");
				if (dataTypes != null) {
					String[] dataTypeNamesArray = Strings.split(dataTypes, Console.SEPARATOR_DATA_CHECKS);
					for (int i = 0; i < dataTypeNamesArray.length; i++) {
						dataTypeNamesArray[i] = Strings.replace(dataTypeNamesArray[i], Console.SEPARATOR_DATA_CONSOLE, Constants.SPACE);
						dataTypeNamesArray[i] = Strings.replace(dataTypeNamesArray[i], Console.OPEN_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_OPEN);
						dataTypeNamesArray[i] = Strings.replace(dataTypeNamesArray[i], Console.CLOSE_PARENTHESIS_EQUIVALENT, Constants.PARENTHESIS_CLOSE);
					}
					dataTypeNames = Lists.newList(dataTypeNamesArray);
					
				}
			}
			if (appName != null)
				AppGUI.getCurrentAppGUI().setName(appName);
			if (envID != null)
				AppGUI.getCurrentAppGUI().setEnvironmentID(envID);
			
			LinajeLookAndFeel.init();
			
			ConsoleWindow consoleWindow = new ConsoleWindow(null, dataTypeNames);
			
			AppGUI.getCurrentAppGUI().setFrame(consoleWindow);
			AppGUI.getCurrentAppGUI().setFrameIcon(Icons.getColorizedIcon(Icons.CONSOLE_48x48, ColorsGUI.getColorApp()));
			
			consoleWindow.setVisible(true);
		}
		catch (Throwable exception) {
			exception.printStackTrace();
		}
	}
	
	public static void executeConsoleWindow() {
		Console.setConsoleWindowEnabled(true);
		Console.getInstance().executeConsoleWindow();
	}
	
	private boolean showData(int dataType) {
		return getDataCheck(dataType).isSelected();
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
	
		if (e.getSource() instanceof JCheckBox) {
			
			//Guardamos el estado del check al presionar para cambiarlo a la fuerza en el mouseReleased(..)
			//en caso de que no cambie de estado solo (Bug que pasa con la maquina virtual 1.4.x)
			JCheckBox checkPressed = (JCheckBox) e.getSource();
			checkPressedSelec = checkPressed.isSelected();
		}
	}
	public void mouseReleased(MouseEvent e) {
	
		if (e.getSource() instanceof JCheckBox) {
			
			JCheckBox checkPressed = (JCheckBox) e.getSource();
			boolean checkReleasedSelec = checkPressed.isSelected();
			//Si el check sigue igual que cuando lo presionamos lo forzamos a cambiar
			if (checkReleasedSelec && checkPressedSelec) {
	
				checkPressed.setSelected(!checkPressed.isSelected());
			}
	
			try {
				getConnection().sendComunication(getChecksStatus());
			}
			catch (Throwable ex) {
				handleException(ex);
			}
		}
	}
	
	private void print(String t, Color c) {
		currentText = t;
		currentColor = c;
		writeInConsole();
	}
	
	private void printException(Throwable ex) {
		ex.printStackTrace(Console.CONSOLE_WRITER);
		String backTrace = Console.CONSOLE_WRITER.getBacktrace();
		String textException = Constants.LINE_SEPARATOR + ex + Constants.LINE_SEPARATOR+ backTrace;
		println(textException, Color.red);
	}
	
	private void println(String t, Color c) {
		currentText = t + Constants.LINE_SEPARATOR;
		currentColor = c;
		writeInConsole();
	}
	
	public void setBackground(Color background) {
		this.background = background;
		try {
			getEditor().changeBackground(background);
		} catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	public void setForeground(Color foreground) {
		this.foreground = foreground;
		try {
			getEditor().getColorButtonForeground().setDefaultColor(foreground);
		} catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	private boolean requestNewHost(String message, String hostName) {
		try {
			
			DlgConsoleConnection dlgConsoleConnection = new DlgConsoleConnection(this);
			dlgConsoleConnection.setLocationRelativeTo(this);
			dlgConsoleConnection.setModal(true);
			String host = dlgConsoleConnection.show(message, hostName);
			if (host != null) {
				boolean retry = dlgConsoleConnection.isRetryAlways();
				IP = InetAddress.getByName(host);
				getConnection().setPort(Console.getInstance().getPort());
				if (retry) {
					getBtnConnect().setIcon(Icons.CONNECTING);
				}
				initConnection(retry);
				return true;
			} else {
				getBtnConnect().setIcon(Icons.CONNECT_OFF);
				return false;
			}
		} catch (Throwable ex) {
			return false;
		}
		
	}
	
	private boolean traceIsValid(String trace) {
		
		try {
			if (trace != null) {
				getCSP().processTrace(trace);
	
				int traceCode = getCSP().getTraceCode();
				int typeData = getCSP().getTypeData();
				
				if (this.currentTraceCode != traceCode && showData(typeData)) {
					this.currentTraceCode = traceCode;
					this.currentColor = getCSP().getColor();
					this.currentText = getCSP().getText();
					return true;
				}
				return false;
			}
			return false;
		} catch (Throwable ex) {
			handleException(ex);
			return false;
		}
	}
	
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
	
		try {
				
			if (getChkOpenAuto().isSelected() && getConnection().isConnected()) {
	
				boolean anyCheckSelected = false;
				Iterator<LCheckBox> it = getMapDataChecks().values().iterator();
				while (it.hasNext() && !anyCheckSelected) {
					LCheckBox check = it.next();
					anyCheckSelected = check.isSelected();
				}
				
				if (!anyCheckSelected) {
						
					String mensaje = TEXTS.checkSomething;
					MessageDialog.showMessage(mensaje, MessageDialog.ICON_WARNING);
				}
				else {
					setVisible(false);
				}
			}
			else {
	
				setVisible(false);
				if (getConnection().isConnected())
					getConnection().sendComunication(getChecksStatus());
				
				destroy();
				System.exit(0);
			}
		}
		catch (Throwable ex) {
			destroy();
			System.exit(0);
		}
	}
	
	public Connection getConnection() throws IOException {
		if (connection == null) {
			connection = new Connection(Console.getInstance().getPort());
			connection.addConnectionListener(new ConnectionListener() {
				
				public void comunicationReceived(ConnectionEvent evt) {
					String trace = evt.getComunicationReceived();
					if (traceIsValid(trace)) {
						writeInConsole();
					}
				}
				
				public void connectionDone(ConnectionEvent evt) {
					try {
						println(TEXTS.connectionDone + getAppName() + TEXTS.at + getHostName() + " : " + Console.getInstance().getPort(), Color.green);
						getBtnConnect().setToolTipText(TEXTS.connectionDoneTip + getAppName());
						getBtnConnect().setIcon(Icons.CONNECT_ON);
						
						getConnection().sendComunication(getChecksStatus());
					}
					catch (Throwable ex) {
						handleException(ex);
					}
				}
				public void connectionEnd(ConnectionEvent evt) {
					if (isVisible()) {
						println(TEXTS.connectionEnd + getAppName(), ColorsGUI.RED);
						getBtnConnect().setToolTipText(TEXTS.connectionEndTip + getAppName());
						getBtnConnect().setIcon(Icons.CONNECT_OFF);
					}
					else {
						destroy();
						System.exit(0);
					}
				}
				
				public void connectionFailed(ConnectionEvent evt) {
					String errorMessage = evt.getComunicationReceived();
					println(errorMessage, ColorsGUI.RED);
					if (errorMessage.contains("Connection refused")) {
						String hostName = getHostName();
						requestNewHost(TEXTS.connectionFailed + getAppName() + TEXTS.at + hostName, hostName);
					}
				}
			});
		}
		return connection;
	}
	private ConsoleProtocol getCSP() {
		if (csp == null)
			csp = new ConsoleProtocol();
		return csp;
	}
	
	private String getAppName() {
		if (appName == null) {
			appName = Console.getInstance().obtainAppName();
		}
		return appName;
	}
	private void setAppName(String appName) {
		this.appName = appName;
	}
	
	private List<String> getDataTypeNames() {
		if (dataTypeNames == null) {
			dataTypeNames = Console.getInstance().getDataTypeNames();
		}
		return dataTypeNames;
	}
	private void setDataTypeNames(List<String> dataTypeNames) {
		this.dataTypeNames = dataTypeNames;
		updateChecks();
	}
	
	private void updateChecks() {
		mapDataChecks = null;
		for (int i = 0; i < getDataTypeNames().size(); i++) {
			getDataCheck(i).setText(getDataTypeNames().get(i));
		}
	}
}
