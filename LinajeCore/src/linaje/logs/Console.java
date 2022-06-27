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
package linaje.logs;

import java.awt.*;
import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.util.List;

import linaje.App;
import linaje.LocalizedStrings;
import linaje.comunications.Connection;
import linaje.comunications.ConnectionEvent;
import linaje.comunications.ConnectionListener;
import linaje.comunications.ConnectionsServer;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Colors;
import linaje.utils.Encryptor;
import linaje.utils.Dates;
import linaje.utils.Files;
import linaje.utils.FileNameExtensionFilter;
import linaje.utils.Lists;
import linaje.utils.Processes;
import linaje.utils.Strings;
 
public class Console {
	
	public static final int TYPE_DATA_DEFAULT = 0;
	public static final int TYPE_DATA_ERROR = 1;
	public static final int TYPE_DATA_ERROR_DETAIL = 2;
	public static final int TYPE_DATA_IN = 3;
	public static final int TYPE_DATA_OUT = 4;
	public static final int TYPE_DATA_OTHER = 5;
	
	private List<String> dataTypeNames = null;
	
	public static final String SEPARATOR_DATA_CHECKS = "-";
	public static final String SEPARATOR_DATA_CONSOLE = "~";
	public static final String LINE_BREAK = "@#@";
	public static final String SEPARATOR_EQUIVALENT = "&#126;";
	
	public static final String OPEN_PARENTHESIS_EQUIVALENT = "#40#";
	public static final String CLOSE_PARENTHESIS_EQUIVALENT = "#41#";
	
	public static final String TAG_FONT_COLOR = "<font color=#";
	public static final String TAG_FONT_CLOSE = "</font>";
	public static final String TAG_BR = "<br>";
	public static final String TAG_CLOSE = ">";
	
	public static final int SYSTEM_CONSOLE_COLORS_NOT_COMPATIBLE = 0;
	public static final int SYSTEM_CONSOLE_COLORS_8_BIT = 1;
	public static final int SYSTEM_CONSOLE_COLORS_24_BIT = 2;
	
	public static final ConsoleWriter CONSOLE_WRITER = new ConsoleWriter();
	
	public static final FileNameExtensionFilter FILTER_HTML_EXTENSION = new FileNameExtensionFilter("Html files", new String[]{"htm", "html"});
	
	private static File consolePropertiesFile = null;
	
	private Encryptor consoleEncrypter = null;
	
	private int port = -1;
	
	private FileWriter output = null;
	private int typeData = TYPE_DATA_DEFAULT;
	protected boolean isHtmlOutputEnabled = false;
	
	private Color background = null;
	private Color foreground = null;
	
	private int traceCode = 0;
	
	private ConnectionsServer server = null; 
	
	private Hashtable<Connection, ConsoleProtocol> consolesStates = null;
	
	private File outputFile = null;
	
	private boolean tracesPaused = false;
	private boolean enabled = false;
	
	//System.out y System.err no están sincronizados entre si
	//Si mostramos los errores en System.err se pueden superponer a trazas de System.out
	//Si queremos que siempre salgan las trazas en orden mostraremos todo por System.out
	private boolean errorsOnSystemOut = true;
	
	private boolean printDate = true;
	private int printDateWait = 2*60*1000;//Time in milis to print date
	private Timer timerDate = null;
	
	//System console traces config
	private boolean tracesSystemEnabled = true;
	private Color systemBackground = null;
	private int systemConsoleAnsiColorsCompatibility = getDefaultSystemConsoleAnsiColorsCompatibility();//SYSTEM_CONSOLE_COLORS_NOT_COMPATIBLE;	
	
	private static Console instance = null;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String dataTypeDefault;
		public String dataTypeErrors;
		public String dataTypeErrorsDetail;
		public String dataTypeIn;
		public String dataTypeOut;
		public String dataTypeOther;
		public String startConsoleWindow;
		public String cannotWriteInFile;
		public String developmentIDE;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	protected Console() {
		super();
	}
	
	public static Console getInstance() {
		if (instance == null)
			instance = new Console();
		return instance;
	}
	
	public static void setInstance(Console instance) {
		Console.instance = instance;
	}
	
	public static void print(String text) {	
		print(text, null, TYPE_DATA_DEFAULT);
	}
	
	public static void print(String text, int typeData) {
		print(text, null, typeData);
	}
	
	public static void print(String text, Color color) {
		print(text, color, TYPE_DATA_DEFAULT);
	}
	
	public static void print(String text, Color color, int typeData) {
		getInstance().setTypeData(typeData);
		getInstance().setText(text, color);
	}
	
	public static void printException(Throwable exception) {
		
		//Añadimos el espacio porque si añadimos colores ANSI no sale bien el link a la excepción en Consolas de Sistema tipo Eclipse
		String descripcionExcepcion = Constants.SPACE + exception.toString();
		String backTrace = getStacktrace(exception);
		
		println(descripcionExcepcion, Color.red, TYPE_DATA_ERROR);
		
		println(backTrace, Color.red, TYPE_DATA_ERROR_DETAIL);
	}
	
	public static void printLineBreak() {
		Console.printLineBreak(TYPE_DATA_DEFAULT);
	}
	public static void printLineBreak(int typeData) {
		Console.println(Constants.VOID, typeData);
	}
	
	public static void println(String text) {
		println(text, null, TYPE_DATA_DEFAULT);
	}
	
	public static void println(String text, int typeData) {
		println(text, null, typeData);
	}
	
	public static void println(String text, Color color) {
		println(text, color, TYPE_DATA_DEFAULT);
	}
	/**
	 * <b>Descripción:</b><br>
	 * Haremos un println prescindiendo de la consola (Solo saldrá por System.out o por fichero)
	 * siempre que no tengamos chequeado el tipo de datos en la consola.
	 *
	 * Esto es útil cuando se saca algo muy grande que satura la consola.
	 *
	 * @param text java.lang.String
	 * @param color java.awt.Color
	 * @param typeData int
	 */
	public static void println(String text, Color color, int typeData) {
		
		String dateLine = getInstance().getDateLine();
		if (dateLine != null)
			print(dateLine, Color.cyan);
		print(text + Constants.LINE_SEPARATOR, color, typeData);
	}
	
	public static boolean isConsoleWindowEnabled() {
		return getInstance().isEnabled();
	}
	public static void setConsoleWindowEnabled(boolean enabled) {
		getInstance().setEnabled(enabled);
	}
	
	public static String getStacktrace(Throwable exception) {
		return getStacktrace(exception, Constants.TAB);
	}
	public static String getStacktrace(Throwable exception, String tabTracePrefix) {
			
		String stackTrace = Constants.VOID;
		try {
	
			StackTraceElement[] traces = exception.getStackTrace();
	
			StringBuffer sbTraces = new StringBuffer();
			for (int i = 0; i < traces.length; i++) {
		
				StackTraceElement trace = traces[i];
				if (trace != null) {
					if (tabTracePrefix != null)
						sbTraces.append(tabTracePrefix);
					sbTraces.append(trace.toString());
					sbTraces.append(Constants.LINE_SEPARATOR);
				}
			}
	
			stackTrace = sbTraces.toString();
		}
		catch (Throwable ex) {
		}
	
		return stackTrace;
	}
	
	public List<String> getDataTypeNames() {
		if (dataTypeNames == null) {
			dataTypeNames = Lists.newList(TEXTS.dataTypeDefault, TEXTS.dataTypeErrors, TEXTS.dataTypeErrorsDetail, TEXTS.dataTypeIn, TEXTS.dataTypeOut, TEXTS.dataTypeOther);
		}
		return dataTypeNames;
	}
	
	public void setDataTypeNames(List<String> dataTypeNames) {
		this.dataTypeNames = dataTypeNames;
	}
	
	private void loadConsoleColors() {
		try {
			
			File fichero = getConsolePropertiesFile();
			if (fichero.exists()) {		
				
				Properties properties = Files.readProperties(fichero);
			
				int r, g, b;
				StringTokenizer st;
	
				st = new StringTokenizer(properties.getProperty("foreground"), Constants.SPACE);
				r = Integer.parseInt(st.nextToken());
				g = Integer.parseInt(st.nextToken());
				b = Integer.parseInt(st.nextToken());
				Color fg = new Color(r, g, b);
	
				st = new StringTokenizer(properties.getProperty("background"), Constants.SPACE);
				r = Integer.parseInt(st.nextToken());
				g = Integer.parseInt(st.nextToken());
				b = Integer.parseInt(st.nextToken());
				Color bg = new Color(r, g, b);
				
				foreground = fg;
				background = bg;
				return;
			}
		} catch (Throwable ex) {
		}
		foreground = Color.black;
		background = Color.white;	
	}
	
	public void destroy() {
		try {
			getServer().finalizeServer();
			closeHtmlOutputFile();
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	
	/**
	 * Ejecuta una ventana de consola como una aplicación independiente
	 * El classpath será el mismo que el de la aplicación que llame este método,
	 * por lo que será necesario que use el framework LinajeGUI
	 */
	public void executeConsoleWindow() {
		
		try {
	
			if (!getServer().isConnected())
				Console.println(TEXTS.startConsoleWindow);
			
			Class<?> claseAEjecutar = Class.forName("linaje.gui.console.ConsoleWindow");
			
			//Actualizamos el nombre y el puerto de la aplicación en el momento de abrir la consola
			App app = App.getCurrentApp();
			String appName = app.getName();
			String envID = app.getEnvironmentID();
			
			String param1 = "port ";
			String param2 = Constants.VOID + getInstance().getPort();
			String param3 = " appName ";
			String param4 = Strings.replace(appName, Constants.SPACE, SEPARATOR_DATA_CONSOLE);
			String param5 = " dataTypes ";
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < getDataTypeNames().size(); i++) {
				String checkName = getDataTypeNames().get(i);
				if (i > 0)
					sb.append(SEPARATOR_DATA_CHECKS);
				sb.append(Strings.replace(checkName, Constants.SPACE, SEPARATOR_DATA_CONSOLE));
			}
			String param6 = sb.toString();
			String param7 = Constants.VOID;
			String param8 = Constants.VOID;
			if (envID != null) {
				param7 = " envID ";
				param8 = Strings.replace(envID, Constants.SPACE, SEPARATOR_DATA_CONSOLE);
			}
			
			//Quitamos los parentesis, que no le gustan a la linea de comandos de Linux
			String params = param1 + param2 + param3 + param4 + param5 + param6 + param7 + param8;
			params = Strings.replace(params, Constants.PARENTHESIS_OPEN, OPEN_PARENTHESIS_EQUIVALENT);
			params = Strings.replace(params, Constants.PARENTHESIS_CLOSE, CLOSE_PARENTHESIS_EQUIVALENT);
			
			Processes.executeJava(claseAEjecutar, null, params);
			
			if (!getServer().isConnected())
				getServer().initServer();
		}
		catch (Throwable ex) {
			handleException(ex);
		}	
	}
	
	private String getHmlHeader() {
		loadConsoleColors();
		Color color = Colors.optimizeColor(foreground, background);
		String colorFondoHex = Colors.convertColorToHex(background);
		String colorFuenteHex = Colors.convertColorToHex(color);
		
		return "<html>\n<head>\n</head>\n\n<body style=\"background-color:#"+colorFondoHex+";color:#"+colorFuenteHex+";font-size:-1;font-family:sans-serif\">";
	}
	
	private String getHtmlClose() {
		return "\n</body>\n</html>";
	}
	
	public int getPort() {
		if (port == -1)
			port = obtainPort();
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public static File getConsolePropertiesFile() {
		if (consolePropertiesFile == null) {
			consolePropertiesFile = new File(Directories.getAppGeneratedFiles(), "Console.properties");
		}
		return consolePropertiesFile;
	}
	public static void setConsolePropertiesFile(File consolePropertiesFile) {
		Console.consolePropertiesFile = consolePropertiesFile;
	}
		
	private void handleException(Throwable exception) {
	
		exception.printStackTrace(System.out);
	}
	
	private boolean showData() {
		
		//Comprobamos todas las consolas y devolveremos true en cuanto alguna tenga activado el tipo de datos actual
		for (int i = 0; i < getServer().getConnections().size(); i++) {
			
			Connection connectedConsole = getServer().getConnections().elementAt(i);
			ConsoleProtocol consoleState = getConsolesStates().get(connectedConsole);
			if (consoleState != null && consoleState.showData(getTypeData()))
				return true;
		}
		return false;
	}
	

	
	protected void closeHtmlOutputFile() {
		try {
			if (isHtmlOutputEnabled) {
				//Escribimos el cierre de tags del fichero html
				File outputFile = getOutputFile();
				output = new FileWriter(outputFile, true);
				output.write(getHtmlClose());
				output.close();
				output = null;
			}
		} catch (Throwable ex) {
			if (isHtmlOutputEnabled && output != null) {
				try { output.close(); } catch (IOException e) {}
			}
			handleException(ex);
		}
	}
	
	public synchronized void setText(String text, Color color) {
	
		try {
	
			if (isConsoleWindowEnabled()) {
				if (!getServer().isConnected()) {
					try {
						getServer().initServer();
					}
					catch (SocketException socEx) {
						if (getServer().getServerSocket() == null) {
							//El puerto del servidor está ocupado por lo que lo incrementamos en mil (incrementamos la primera cifra)
							getInstance().setPort(getInstance().getPort() + 1000);
							server = null;
							getServer().initServer();
						}
					}
				}
			}
			else if (server != null && getServer().isConnected()) {
				getServer().finalizeServer();
			}
			
			if (isConsoleWindowEnabled() && showData() && !isTracesPaused()) {
				try {
	
					//Quitamos el caracter separador ~ (si viene) del texto y lo sustituimos por el equivalente en html
					String textWithoutSeparators = Strings.replace(text, SEPARATOR_DATA_CONSOLE, SEPARATOR_EQUIVALENT);
					
					int rgb = -1;
					if (color != null)
						rgb = color.getRGB();
	
					String trace = traceCode + SEPARATOR_DATA_CONSOLE + getTypeData() + SEPARATOR_DATA_CONSOLE + textWithoutSeparators + SEPARATOR_DATA_CONSOLE + rgb;
					traceCode++;
					for (int i = 0; i < getServer().getConnections().size(); i++) {
						Connection connectedConsole = getServer().getConnections().elementAt(i);
						ConsoleProtocol consoleState = getConsolesStates().get(connectedConsole);
						if (consoleState != null && consoleState.showData(getTypeData()))
							connectedConsole.sendComunication(trace);
					}
				}
				catch (Throwable ex) {
					handleException(ex);
				}
			}
			
			File outputFile = getOutputFile();
			if (outputFile != null) {
				try {
					if (output == null) {
						//La 1ª vez, borramos el fichero de salida en caso de que exista
						if (outputFile.exists())
							outputFile.delete();
						
						if (FILTER_HTML_EXTENSION.accept(outputFile)) {
							output = new FileWriter(outputFile, true);
							output.write(getHmlHeader());
							output.close();
							isHtmlOutputEnabled = true;
						}
					}
					output = new FileWriter(outputFile, true);
					if (getTypeData() == TYPE_DATA_ERROR ||
						getTypeData() == TYPE_DATA_ERROR_DETAIL ||
						getTypeData() == TYPE_DATA_DEFAULT) {
						
						if (isHtmlOutputEnabled) {
							//Sacamos el fichero en formato HTML
							StringBuffer htmlText = new StringBuffer(text);
							final String LINE_SEPARATOR = Constants.LINE_SEPARATOR;
							int textLength = text.length();
							if (color != null) {
								Color optimizedColor = Colors.optimizeColor(color, background);
								String colorHex = Colors.convertColorToHex(optimizedColor);
								htmlText.insert(0, TAG_FONT_COLOR + colorHex + TAG_CLOSE);
								htmlText.append(TAG_FONT_CLOSE);
							}
							if (textLength > 0 && text.endsWith(LINE_SEPARATOR))
								htmlText.append(TAG_BR);
							htmlText.append(LINE_SEPARATOR);
							output.write(htmlText.toString());
						}
						else {
							//Sacamos el fichero en formato texto
							output.write(text);
						}
					}
				}
				catch (IOException ioex) {
					println(TEXTS.cannotWriteInFile + outputFile);
					//Desactivamos las trazas a fichero
					setOutputFile(null);
				}
				finally {
					if (output != null) {
						try { output.close(); } catch (IOException e) {}
					}
				}
			}
			if (isTracesSystemEnabled()) {
				String systemText = text;
				if (getSystemConsoleAnsiColorsCompatibility() != SYSTEM_CONSOLE_COLORS_NOT_COMPATIBLE && color != null) {
					Color systemForeground = systemBackground != null ? Colors.optimizeColor(color, systemBackground) : color;
					String colorAnsi = Colors.convertColorToAnsi(systemForeground, false, getSystemConsoleAnsiColorsCompatibility() == SYSTEM_CONSOLE_COLORS_8_BIT);
					systemText = colorAnsi + systemText + Colors.COLOR_ANSI_RESET;
				}
				if (!isErrorsOnSystemOut() && (getTypeData() == TYPE_DATA_ERROR || getTypeData() == TYPE_DATA_ERROR_DETAIL))
					System.err.print(systemText);
				else {
					System.out.print(systemText);
				}
			}
		}
		catch (Throwable ex) {
			handleException(ex);
		}
	}
	protected Encryptor getConsoleEncrypter() {
		if (consoleEncrypter == null) {
			char[] p = {'e','n','c','r','y','p','t','C','o','n','s','o','l','e','K','e','y','1','2','3','4'};
			byte[] s;
			try {
				s = "saltConsole1".getBytes(Encryptor.CHARSET_DEFAULT);
			}
			catch (Exception e) {
				s = null;
			}
			
			consoleEncrypter = new Encryptor(p, s);
		}
		return consoleEncrypter;
	}
	
	private void setTypeData(int newValue) {
		typeData = newValue;
	}
	
	protected int obtainPort() {
		
		int cifra1 = 7;
		int cifra2 = 7;
		
		App app = App.getCurrentApp();
		String codigoEntorno = app.getEnvironmentID();
		int indexEntorno = app.getEnvironmentIDs().indexOf(codigoEntorno);
		int cifra3 = indexEntorno + 1;
	
		String codigoAplicacion = app.getId();
		int indexAplicacion = app.getAppIDs().indexOf(codigoAplicacion);
		int cifra4 = indexAplicacion + 1;
	
		StringBuffer sb = new StringBuffer();
		sb.append(cifra1);
		sb.append(cifra2);
		sb.append(cifra3);
		sb.append(cifra4);
		
		return Integer.parseInt(sb.toString());
	}
	
	public String obtainAppName() {
		
		App app = App.getCurrentApp();
		String appName = app.getName();
		String envID = app.getEnvironmentID();
		String prodID = app.getEnvironmentProductionID();
		
		StringBuffer sb = new StringBuffer();
		sb.append(appName);
		if (envID != null && prodID != null && !envID.equals(prodID) && envID.length() > 0) {
			sb.append(" - ");
			sb.append(envID);
		}
	
		return sb.toString();
	}
	
	public ConnectionsServer getServer() {
		if (server == null) {
			try {
				server = ConnectionsServer.getServer(getInstance().getPort());
				server.addConnectionListener(new ConnectionListener() {
					
					public void connectionEnd(ConnectionEvent evt) {
						getConsolesStates().remove(evt.getConnection());
					}
					
					public void connectionFailed(ConnectionEvent evt) {
						System.err.println(evt.getComunicationReceived());
					}
					
					public void connectionDone(ConnectionEvent evt) {
						getConsolesStates().put(evt.getConnection(), new ConsoleProtocol());
					}
					
					public void comunicationReceived(ConnectionEvent evt) {
						ConsoleProtocol csp = getConsolesStates().get(evt.getConnection());
						csp.processChecks(evt.getComunicationReceived());
					}
				});
			} catch (IOException e) {
				handleException(e);
			}
		}
		return server;
	}
	
	private Hashtable<Connection, ConsoleProtocol> getConsolesStates() {
		if (consolesStates == null)
			consolesStates = new Hashtable<Connection, ConsoleProtocol>();
		return consolesStates;
	}
	
	public File getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(File newFicheroSalida) {
		outputFile = newFicheroSalida;
		output = null;
	}
	
	
	public boolean isTracesPaused() {
		return tracesPaused;
	}
	public boolean isTracesSystemEnabled() {
		return tracesSystemEnabled;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public int getPrintDateWait() {
		return printDateWait;
	}
	public int getTypeData() {
		return typeData;
	}
	
	public void setTracesPaused(boolean tracesPaused) {
		this.tracesPaused = tracesPaused;
	}
	public void setTracesSystemEnabled(boolean tracesSystemEnabled) {
		this.tracesSystemEnabled = tracesSystemEnabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public void setPrintDateWait(int printDateWait) {
		this.printDateWait = printDateWait;
	}
	
	private Timer getTimerDate() {
		if (timerDate == null) {
			timerDate = new Timer();
		}
		return timerDate;
	}
	
	protected String getDateLine() {
		String dateLine = null;
		if (printDate) {
			printDate = false;
			dateLine = "**************** " + Dates.getFormattedDate(new Date(), Dates.FORMAT_DD_MM_YYYY_HH_MM_SS) + " ****************\n";
			TimerTask timerTask = new TimerTask() {
				public void run() {
					printDate = true;
				}
			};
			getTimerDate().schedule(timerTask, getPrintDateWait());
		}
		return dateLine;
	}
	
	public boolean isErrorsOnSystemOut() {
		return errorsOnSystemOut;
	}
	public void setErrorsOnSystemOut(boolean errorsOnSystemOut) {
		this.errorsOnSystemOut = errorsOnSystemOut;
	}
	
	public Color getSystemBackground() {
		return systemBackground;
	}
	public void setSystemBackground(Color systemBackground) {
		this.systemBackground = systemBackground;
	}
	
	public int getSystemConsoleAnsiColorsCompatibility() {
		return systemConsoleAnsiColorsCompatibility;
	}
	public void setSystemConsoleAnsiColorsCompatibility(int systemConsoleAnsiColorsCompatibility) {
		this.systemConsoleAnsiColorsCompatibility = systemConsoleAnsiColorsCompatibility;
	}
	
	/**
	 * Se optimizará el color de las trazas del sistema para que resalten sobre el color que aquí se defina
	 **/
	public static void setCurrentSystemBackground(Color systemBackground) {
		getInstance().setSystemBackground(systemBackground);
	}
	/**
	 * Definiremos si la consola de sistema actual admite colores Ansi o no
	 **/
	public static void setCurrentSystemConsoleAnsiColorsCompatibility(int systemConsoleAnsiColorsCompatibility) {
		getInstance().setSystemConsoleAnsiColorsCompatibility(systemConsoleAnsiColorsCompatibility);
	}
	
	private static int getDefaultSystemConsoleAnsiColorsCompatibility() {
		return System.getenv().get("TERM") != null ? SYSTEM_CONSOLE_COLORS_24_BIT : SYSTEM_CONSOLE_COLORS_NOT_COMPATIBLE;
	}
}
