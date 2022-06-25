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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import linaje.LocalizedStrings;
import linaje.tests.TestConnectionsServer;
import linaje.utils.Strings;

public class ConnectionsServer {

	private ServerSocket serverSocket = null;
	private static HashMap<Integer, ConnectionsServer> servers = null;
	private Vector<Connection> connections = null;
	
	private int port = 0;
	
	private boolean connectionsBlocked = false;
	
	private EventListenerList listenerList = null;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String serverCreatedOnPort;
		public String serverFinalized;
		public String connectionsFinalized;
		public String finalizingConnectionsToPort;
		public String finalizingServerOnPort;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private ConnectionsServer(int port) throws IOException {
		setPort(port);
	}

	/**
	 * Para hacer una prueba de conexión ejecutar después la clase Connection
	 */
	public static void main(String[] args) {
		TestConnectionsServer.main(args);
	}
	
	public static synchronized ConnectionsServer getServer(int port) throws IOException {
		
		ConnectionsServer connectionsServer = getServers().get(port);
		if (connectionsServer == null) {
			connectionsServer = new ConnectionsServer(port);
			getServers().put(port, connectionsServer);
		}
		return connectionsServer;
	}
	
	public void initServer() throws IOException {
		
		try {
			
			if (!isConnected()) {
				setConnectionsBlocked(false);
				serverSocket = new ServerSocket(getPort());
				
				Thread thread = new Thread() {
					public void run() {
						try {
							initConnectionsListening();
						}
						catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				};
				thread.start();
				
				System.out.println(TEXTS.serverCreatedOnPort+port);
			}
		}
		catch (IOException ex) {
			finalizeServer();
			throw ex;
		}
	}
	
	public boolean isConnected() {
		return serverSocket != null;
	}
	
	public void finalizeConnections() {
		
		setConnectionsBlocked(true);
		System.out.println(TEXTS.finalizingConnectionsToPort + getPort() + "...");
		
		for (int i = 0; i < getConnections().size(); i++) {
			Connection connection = getConnections().elementAt(i);
			connection.finalizeConnection();
		}
		
		System.out.println(TEXTS.connectionsFinalized);
		setConnectionsBlocked(false);
	}
	
	public void finalizeServer() {
		
		if (isConnected()) {
			finalizeConnections();
			
			setConnectionsBlocked(true);
			System.out.println(TEXTS.finalizingServerOnPort + getPort() + "...");
			
			try {
				serverSocket.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
			finally {
				serverSocket = null;
				connections = null;
				try {
					//Damos tiempo a que finalice la espera de conexión
					Thread.sleep(1000);
				}
				catch (InterruptedException ex) {
				}
				
				System.out.println(TEXTS.serverFinalized);
			}
		}
	}
	
	private synchronized void initConnectionsListening() throws IOException, InterruptedException {
		
		Connection connection = null;
		
		while (getServerSocket() != null) {
			
			if (connection == null || connection.isConnected()) {
				
				if (!areConnectionsBlocked()) {
					//Nos pondremos a la escucha de una nueva conexión la primera vez o cuando se conecte un cliente
					connection = new Connection(getPort());
					connection.addConnectionListener(new ConnectionListener() {
						
						public void connectionDone(ConnectionEvent evt) {
							
							Connection connection = evt.getConnection();
							assignConnectionName(connection);
							getConnections().addElement(connection);
							fireConnectionEvent(evt);
						}
						
						public void connectionEnd(ConnectionEvent evt) {
							Connection connection = evt.getConnection();
							getConnections().removeElement(connection);
							fireConnectionEvent(evt);
						}
						
						public void comunicationReceived(ConnectionEvent evt) {
							fireConnectionEvent(evt);
						}
						public void connectionFailed(ConnectionEvent evt) {
							fireConnectionEvent(evt);
						}
					});
					connection.initListening(getServerSocket());
				}
			}
			else {
				Thread.sleep(100);
			}
		}
	}
	
	private Connection getConnection(String connectionName) {
		for (int i = 0; i < getConnections().size(); i++) {
			Connection connection = getConnections().elementAt(i);
			if (connection.getName().equals(connectionName))
				return connection;
		}
		return null;
	}
	
	private void assignConnectionName(Connection connection) {
		
		String connectionName = connection.getDefaultName();
		
		Connection existingConnection = getConnection(connectionName);
		while (existingConnection != null) {
			connectionName = Strings.newName(connectionName);
			existingConnection = getConnection(connectionName);
		}
		
		connection.setName(connectionName);
	}
	
	public void addConnectionListener(ConnectionListener l) {
		getListenerList().add(ConnectionListener.class, l);
	}
	public void removeConnectionListener(ConnectionListener l) {
		getListenerList().remove(ConnectionListener.class, l);
	}
	
	protected void fireConnectionEvent(ConnectionEvent connectionEvent) {
		//Lanzamos el evento
		Object[] listeners = getListenerList().getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConnectionListener.class) {
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
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	private static HashMap<Integer, ConnectionsServer> getServers() {
		if (servers == null)
			servers = new LinkedHashMap<Integer, ConnectionsServer>();
		return servers;
	}
	public Vector<Connection> getConnections() {
		if (connections == null)
			connections = new Vector<Connection>();
		return connections;
	}
	public int getPort() {
		return port;
	}
	
	private EventListenerList getListenerList() {
		if (listenerList == null)
			listenerList = new EventListenerList();
		return listenerList;
	}

	private void setPort(int port) throws IOException {
		
		if (port < Connection.PORT_MIN || port > Connection.PORT_MAX)
			throw new IOException(Connection.TEXTS.portBetween + Connection.PORT_MIN+ Connection.TEXTS.and + Connection.PORT_MAX);
		
		this.port = port;
	}

	public boolean areConnectionsBlocked() {
		return connectionsBlocked;
	}

	public void setConnectionsBlocked(boolean connectionsBlocked) {
		this.connectionsBlocked = connectionsBlocked;
	}
}
