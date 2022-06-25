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

import java.util.EventObject;

@SuppressWarnings("serial")
public class ConnectionEvent extends EventObject {

	public static final int COMUNICATION_RECEIVED = 0;
	public static final int CONNECTION_DONE = 1;
	public static final int CONNECTION_END = 2;
	public static final int CONNECTION_FAILED = 3;
	
	private int action = -1;
	private String comunicationReceived = null;
	
	public ConnectionEvent(Connection source, int action) {
		this(source, action, null);
	}
	public ConnectionEvent(Connection source, int action, String comunicationReceived) {
		super(source);
		setAction(action);
		setComunicationReceived(comunicationReceived);
	}

	public int getAction() {
		return action;
	}
	public Connection getConnection() {
		return (Connection) getSource();
	}
	public String getComunicationReceived() {
		return comunicationReceived;
	}
	
	private void setAction(int action) {
		this.action = action;
	}
	private void setComunicationReceived(String comunicationReceived) {
		this.comunicationReceived = comunicationReceived;
	}
}
