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
import linaje.statics.Constants;

/**
 * Para hacer una prueba de conexión ejecutar antes la clase TestConnectionsServer
 * y luego TestConnection tantas veces como se quiera
 */
public class TestConnection {

	public static class Texts extends LocalizedStrings {

		public String connectionEstablishedWithServer;
		public String connectionEndedWithServer;
		public String comunicationReceivedFromServer;
		public String textFromClient;
		
		public Texts(String resourceBundle) {
			super(resourceBundle);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts(LocalizedStrings.DEFAULT_TESTS_RESOURCE_BUNDLE);
	
	public static void main(String[] args) {

		try {
			final Connection clientConnection = new Connection(Connection.PORT_DEFAULT);
			clientConnection.addConnectionListener(new ConnectionListener() {
				
				public void connectionDone(ConnectionEvent evt) {
					
					System.out.println(TEXTS.connectionEstablishedWithServer + clientConnection.getName() + Constants.COLON + clientConnection.getPort());
					for (int i = 0; i < 10; i++) {
						//Escribimos al servidor
						clientConnection.sendComunication("Texto desde cliente " + i);
						try {
							Thread.sleep(1000);
						}
						catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
					clientConnection.finalizeConnection();
				}
				
				public void connectionEnd(ConnectionEvent evt) {
					System.out.println(TEXTS.connectionEndedWithServer + clientConnection.getName() + Constants.COLON + clientConnection.getPort());
				}
				
				public void comunicationReceived(ConnectionEvent evt) {
					System.out.println(TEXTS.comunicationReceivedFromServer + evt.getConnection().getName() + Constants.COLON);
					System.out.println(" - " + evt.getComunicationReceived());
				}
				public void connectionFailed(ConnectionEvent evt) {
					System.out.println(evt.getComunicationReceived());
				}
			});
			clientConnection.initConnection(null);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
