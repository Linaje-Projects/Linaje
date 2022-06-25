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
package linaje.comunications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import linaje.LocalizedStrings;
import linaje.statics.Constants;
import linaje.tests.TestConnection;
import linaje.utils.Numbers;
import linaje.utils.Security;
import linaje.utils.Strings;

public class Connection {

	public static final int PORT_MAX = 65535;
	public static final int PORT_MIN = 0;
	public static final int PORT_DEFAULT = 8877;
	
	public static final String END_CONNECTION = "@@END_CONNECTION";
	public static final String LINE_SEPARATOR_VIRTUAL = "@#@";
	
	private InputStreamReader isrSocket = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private Socket socket = null;
	private int port = 0;
	private String name = null;
	private boolean encodeComunications = true;
	private boolean retrying = false;
	private boolean cancelRetries = false;
	
	protected EventListenerList listenerList = new EventListenerList();
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String alreadyConnectedTo;
		public String alreadyRetryingConnection;
		public String serverNotFound;
		public String cantConnectWithServer;
		public String retryingConnection;
		public String exceptionListeningNewClient;
		public String cantDecriptText;
		public String cantChangePortConnected;
		public String portBetween;
		public String and;
		public String connection;
		public String localhost;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public Connection(int port) throws IOException {
		setPort(port);
	}

	/**
	 * Para hacer una prueba de conexión ejecutar antes la clase ConnectionsServer
	 */
	public static void main(String[] args) {
		TestConnection.main(args);
	}

	/**
	 * Iniciará la conexión al servidor cuando creemos una conexión cliente independiente
	 */
	public void initConnection(final String hostName) throws IOException {
		initConnectionRetrying(hostName, 0);
	}
	
	/**
	 * Iniciará la conexión al servidor cuando creemos una conexión cliente independiente
	 */
	public void initConnectionRetrying(final String hostName, final int retries) throws IOException {
		cancelRetries = false;
		String errorMessage = isConnected() ? TEXTS.alreadyConnectedTo + getName() : isRetrying() ? TEXTS.alreadyRetryingConnection : null;
		if (errorMessage == null) {
			Thread thread = new Thread() {
				public void run() {
					try {
						initConnectionInThread(hostName, retries);
					}
					catch (Throwable ex) {
						//ex.printStackTrace();
						System.err.println(ex.getMessage() != null ? ex.getMessage() : ex.toString());
					}
				}
			};
			thread.start();
		}
		else throw new IOException(errorMessage);
	}
	
	private void initConnectionInThread(String hostName, int retries) throws IOException {
		
		try {
			InetAddress host = hostName != null ? InetAddress.getByName(hostName) : InetAddress.getLocalHost();		
			Socket socketClient = new Socket(host, getPort());
			init(socketClient, 0);
			setRetrying(false);
		}
		catch (UnknownHostException ex) {
			setRetrying(retries > 0);
			if (!isRetrying()) {
				setRetrying(false);
				cancelRetries = true;
				connectionFailed(TEXTS.serverNotFound + hostName);
				throw ex;
			}
		}
		catch (IOException ex) {
			setRetrying(retries > 0);
			if (!isRetrying()) {
				setRetrying(false);
				cancelRetries = true;
				String descHost = (hostName != null ? hostName : TEXTS.localhost) + Constants.COLON + getPort();
				connectionFailed(TEXTS.cantConnectWithServer + descHost + Constants.LINE_SEPARATOR+ex.getMessage());
				throw ex;
			}
		}
		
		if (isRetrying()) {
			connectionFailed(TEXTS.retryingConnection + retries + Constants.PARENTHESIS_CLOSE);
			try { Thread.sleep(1000); }	catch (InterruptedException iex) {}
			retries--;
			initConnectionInThread(hostName, retries);
		}
    }
	
	/**
	 * Iniciará la escucha de conexiones cliente internamente en el servidor
	 */
	protected void initListening(final ServerSocket serverSocket) throws IOException {
		Thread thread = new Thread() {
			public void run() {
				try {
					initListeningInThread(serverSocket);
				}
				catch (Throwable ex) {
					//ex.printStackTrace();
					System.err.println(ex.getMessage() != null ? ex.getMessage() : ex.toString());
				}
			}
		};
		thread.start();
	}
	private void initListeningInThread(ServerSocket serverSocket) throws IOException {
		try {
			
			Socket clientSocket = serverSocket.accept();
			init(clientSocket, 500);
		}
		catch (IOException ex) {
			//Si el socket servidor está cerrado es que hemos finalizado el servidor
			if (!serverSocket.isClosed()) {
				connectionFailed(TEXTS.exceptionListeningNewClient + getPort() + Constants.LINE_SEPARATOR+ex.getMessage());
				throw ex;
			}
		}
	}
	
	private void init(Socket socket, int readWaitTime) throws IOException {
        
		try {

			this.socket = socket;
			
			isrSocket = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(isrSocket);
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			Thread thread = new Thread() {
				public void run() {
					try {
						connectionDone();
					}
					catch (Throwable ex) {
						//ex.printStackTrace();
						System.err.println(ex.getMessage() != null ? ex.getMessage() : ex.toString());
					}
				}
			};
			thread.start();
			
			try {
				//Damos tiempo a que el servidor inicie la conexión a través de conexionEstablecida()
				Thread.sleep(readWaitTime);
			}
			catch (InterruptedException ex) {
			}
			
			//Nos quedamos a la espera de recibir comunicación a través de la conexión
			String comunication;
			while (socket != null && (comunication = socketReading()) != null) {
				receiveComunicacion(comunication);
			}
		}
		finally {
			if (isrSocket != null) {
				try { isrSocket.close(); } catch (IOException e) {}
			}
			if (reader != null) {
				try { reader.close(); } catch (IOException e) {}
			}
			if (writer != null)
				writer.close();
			//if (socket != null) {
			//	try { socket.close(); } catch (IOException e) {}
			//}
			finalizeSocket();
			connectionEnd();
			
			this.socket = null;
			reader = null;
			writer = null;
		}
    }
	
	private String socketReading() throws IOException {
		
		String inputLine = reader.readLine();
		
		String readedText = null;
		if (inputLine != null) {
			//Restauramos los saltos de linea originales
			readedText = Strings.replace(inputLine, LINE_SEPARATOR_VIRTUAL, Constants.LINE_SEPARATOR);
			
			if (isEncodeComunications()) {
				try {
					//Desencriptamos el texto
					readedText = Security.getEncryptor().decryptText(readedText);
				}
				catch (Throwable ex) {
					throw new IOException(TEXTS.cantDecriptText);
				}
			}
			
			if (readedText.equals(END_CONNECTION))
				readedText = null;
		}
		return readedText;
	}
	
	/**
	 * Escribirá al servidor cuando creemos una ConexionCliente independiente y escribirá al cliente conectado cuando se use internamente en el servidor
	 */
	public void sendComunication(String text) {
		
		String comunication = text;
		if (isEncodeComunications()) {
			try {
				//Encriptamos el texto
				comunication = Security.getEncryptor().encryptText(text);
			}
			catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		
		//Sustituimos los saltos de línea por el texto definido para ellos
		comunication = comunication.replaceAll(Constants.REGEX_LINE_SEPARATOR, LINE_SEPARATOR_VIRTUAL);
		
		writer.println(comunication);
	}
	
	public void finalizeConnection() {
		//finalizeSocket();
		cancelRetries = true;
		if (isConnected())
			sendComunication(END_CONNECTION);
	}
	
	private void finalizeSocket() {
		if (socket != null) {
			if (!socket.isClosed()) {
				try { socket.close(); } catch (IOException e) {}
			}
			socket = null;
		}
	}
	
	public boolean isConnected() {
		return getSocket() != null;
	}
	
	public void addConnectionListener(ConnectionListener l) {
		listenerList.add(ConnectionListener.class, l);
	}
	public void removeConnectionListener(ConnectionListener l) {
		listenerList.remove(ConnectionListener.class, l);
	}
	
	private void fireConnectionEvent(Object source, int action, String comunicationReceived) {
		//Lanzamos el evento
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConnectionListener.class) {
				ConnectionEvent connectionEvent = new ConnectionEvent(this, action, comunicationReceived);
				ConnectionListener connectionListener = (ConnectionListener) listeners[i + 1];
				switch(connectionEvent.getAction()) {
		        	case ConnectionEvent.COMUNICATION_RECEIVED:
		        		connectionListener.comunicationReceived(connectionEvent);
		        		break;
		        	case ConnectionEvent.CONNECTION_DONE:
		        		connectionListener.connectionDone(connectionEvent);
		        		break;
			    	case ConnectionEvent.CONNECTION_END:
			    		connectionListener.connectionEnd(connectionEvent);
			    		break;
			    	case ConnectionEvent.CONNECTION_FAILED:
			    		connectionListener.connectionFailed(connectionEvent);
			    		break;
			    	default:
			    		break;
	        	}
			}
		}
	}
	
	/**
	 * Leerá del servidor cuando creemos una ConexionCliente independiente y leerá del cliente conectado cuando se use internamente en el servidor
	 */
	private void receiveComunicacion(String text) {
		fireConnectionEvent(this, ConnectionEvent.COMUNICATION_RECEIVED, text);
	}
	private void connectionDone() {
		setRetrying(false);
		cancelRetries = true;
		fireConnectionEvent(this, ConnectionEvent.CONNECTION_DONE, null);
	}
	private  void connectionEnd() {
		fireConnectionEvent(this, ConnectionEvent.CONNECTION_END, null);
	}
	private  void connectionFailed(String error) {
		fireConnectionEvent(this, ConnectionEvent.CONNECTION_FAILED, error);
	}
	
	public String getDefaultName() {
		
		String defaultName = getHost() != null ? getHost().getHostName() : TEXTS.connection;
		int indexPoint = defaultName != null ? defaultName.indexOf('.') : -1;
		
		if (indexPoint != -1) {
			//Quitamos el grupo de trabajo (ej: .group.myCompany.com)
			String shortName = defaultName.substring(0, indexPoint);
			if (Numbers.isIntegerNumber(shortName))
				return defaultName;//El hostName seguramente es una IP, por lo que devolvemos el nombre entero
			else
				return shortName;
		}
		else return defaultName;
	}

	public Socket getSocket() {
		return socket;
	}
	public int getPort() {
		return port;
	}
	public InetAddress getHost() {
		return getSocket() != null ? getSocket().getInetAddress() : null;
	}
	public String getName() {
		if (name == null)
			name = getDefaultName();
		return name;
	}
	
	public void setPort(int port) throws IOException {
		
		if (isConnected())
			throw new IOException(TEXTS.cantChangePortConnected);
			
		if (port < PORT_MIN || port > PORT_MAX)
			throw new IOException(TEXTS.portBetween + PORT_MIN + TEXTS.and + PORT_MAX);
			
		this.port = port;
	}
	public void setName(String connectionName) {
		this.name = connectionName;
	}
	public String toString() {
		return getName();
	}
	public boolean isEncodeComunications() {
		return encodeComunications;
	}
	public void setEncodeComunications(boolean encodeComunications) {
		this.encodeComunications = encodeComunications;
	}
	public boolean isRetrying() {
		return retrying && !cancelRetries;
	}
	private void setRetrying(boolean retrying) {
		this.retrying = retrying;
	}
}
