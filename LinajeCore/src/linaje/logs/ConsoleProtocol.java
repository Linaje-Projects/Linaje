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

/**
 * <b>Funcionalidad:</b><br>
 * Facilitar la lectura de trazas para la comunicación de los sockets de la consola
 * 
 * @author Pablo Linaje
 * 
 * @see linaje.logs.Console
 * @see linaje.gui.console.ConsoleWindow
 * 
 */
import java.awt.Color;

import linaje.utils.Strings;

public class ConsoleProtocol {
	
	private int traceCode = -1;
	private int typeData = -1;
	private String text;
	private Color color;
	
	private boolean consoleEnabled = true;
	
	private boolean[] checkStates = null;
	
	protected void processChecks(String estadoChecks) {
	
		try {
	
			String[] elements = Strings.split(estadoChecks, Console.SEPARATOR_DATA_CONSOLE);
			boolean[] checkStates = null;
			boolean isConsoleEnabled = false;
			
			if (elements != null && elements.length > 0) {
				isConsoleEnabled = new Boolean(elements[0]).booleanValue();
				checkStates = new boolean[elements.length-1];
				for (int i = 1; i < elements.length; i++) {
					checkStates[i-1] = new Boolean(elements[i]).booleanValue();
				}
			}
			
			setConsoleEnabled(isConsoleEnabled);
			setCheckStates(checkStates);
		}
		catch (Throwable ex) {
			//ex.printStackTrace();
		}
	}
	
	public void processTrace(String trace) {
	
		try {
	
			String[] elements = Strings.split(trace, Console.SEPARATOR_DATA_CONSOLE);
			
			int ct = Integer.parseInt(elements[0]);
			int td = Integer.parseInt(elements[1]);
			String text = elements[2];
			text = Strings.replace(text, Console.SEPARATOR_EQUIVALENT, Console.SEPARATOR_DATA_CONSOLE);
			int rgb = Integer.parseInt(elements[3]);
	
			Color c;
			if (rgb != -1) {
				c = new Color(rgb);
			}
			else {
				c = null;
			}
	
			setTraceCode(ct);
			setTypeData(td);
			setColor(c);
			setText(text);
		}
		catch (Throwable ex) {
			//ex.printStackTrace();
		}
	}
	
	public boolean showData(int dataType) {
		
		try {
			return getCheckStates()[dataType];
		} catch (Exception e) {
			return false;
		}
	}
	
	public int getTraceCode() {
		return traceCode;
	}
	public Color getColor() {
		return color;
	}
	public String getText() {
		return text;
	}
	public int getTypeData() {
		return typeData;
	}
	protected boolean isConsoleEnabled() {
		return consoleEnabled;
	}
	private boolean[] getCheckStates() {
		return checkStates;
	}
	
	private void setTraceCode(int traceCode) {
		this.traceCode = traceCode;
	}
	private void setColor(java.awt.Color color) {
		this.color = color;
	}
	private void setText(String text) {
		this.text = text;
	}
	private void setTypeData(int typeData) {
		this.typeData = typeData;
	}
	private void setConsoleEnabled(boolean consoleEnabled) {
		this.consoleEnabled = consoleEnabled;
	}
	private void setCheckStates(boolean[] checkStates) {
		this.checkStates = checkStates;
	}
}
