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
package linaje.tests;

import linaje.LocalizedStrings;
import linaje.comunications.Connection;
import linaje.comunications.ConnectionEvent;
import linaje.comunications.ConnectionListener;
import linaje.comunications.ConnectionsServer;

public class TestConnectionsServer {

	public static class Texts extends LocalizedStrings {

		public String connectionEstablished;
		public String connectionEnded;
		public String comunicationReceived;
		public String textFromServer;
		
		public Texts(String resourceBundle) {
			super(resourceBundle);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts(LocalizedStrings.DEFAULT_TESTS_RESOURCE_BUNDLE);
	
	/**
	 * Para hacer una prueba de conexión ejecutar después la clase Connection
	 * (Se pueden ejecutar tantos TestConnection en paralelo como se quiera)
	 */
	public static void main(String[] args) {

		try {
			
			ConnectionsServer server = ConnectionsServer.getServer(Connection.PORT_DEFAULT);
			server.addConnectionListener(new ConnectionListener() {
				
				public void connectionDone(ConnectionEvent evt) {	
					System.out.println(TEXTS.connectionEstablished + evt.getConnection().getName());
				}
				
				public void connectionEnd(ConnectionEvent evt) {
					System.out.println(TEXTS.connectionEnded + evt.getConnection().getName());
				}
				
				public void comunicationReceived(ConnectionEvent evt) {
					System.out.println(TEXTS.comunicationReceived + evt.getConnection().getName() + ": ");
					System.out.println(" - " + evt.getComunicationReceived());
				}
				public void connectionFailed(ConnectionEvent evt) {
					System.out.println(evt.getComunicationReceived());
				}
			});
			server.initServer();
			while (server.getConnections().isEmpty()) {
				Thread.sleep(1000);
			}
			for (int i = 0; i < 10; i++) {
				if (!server.getConnections().isEmpty()) {
					server.getConnections().elementAt(0).sendComunication(TEXTS.textFromServer + i);
					try {
						Thread.sleep(2000);
					}
					catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			}
			server.finalizeServer();
			//server.initServer();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
