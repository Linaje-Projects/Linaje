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
 * Obtener las trazas de los métodos por los que pasa una una excepción.
 * Como cuando hacemos excepcion.printStackTrace() y nos sale por consola.
 * <p>
 * <b>Uso:</b><br>
 * Hacer un excepcion.printStackTrace(myConsoleWriter)
 * A continuación obtenemos la traza de la excepción con:
 *		myConsoleWriter.getBackTrace();
 *
 * NOTA: Dependiendo de la máquina virtual pasa por unos métodos u otros, por eso
 * hay algúnos métodos que parece que no sirven para nada, pero no es cierto.
 * Por ejemplo: en la máquina virtual 1.1.7 pasa por println(Object) y println(String),
 * mientras que en la 1.3 pasa por println(Object) y
 * por write(byte[], int, int).
 *		Si se va a ejecutar en otra máquina virtual hay que verificar el correcto funcionamiento
 * de esta clase
 * 
 * <p>
 * 
 * @author Pablo Linaje
 * @version 1.1
 * 
 * @see clase Console 
 * 
 */
import java.io.*;

public class ConsoleWriter extends PrintStream {
	
	private String backtrace = null;
	private String description = null;

		
	public ConsoleWriter() {
		super(System.err);
	}
	public ConsoleWriter(OutputStream out) {
		super(out);
	}
	public ConsoleWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}
	
	public String getBacktrace() {
		return backtrace;
	}
	
	public String getDescription() {
		return description;
	}
	/**
	 * <b>Descripción:</b><br>
	 * Sobreescribimos el método print(Object) de PrintStream
	 * Cuando pasa por aquí se esta escribiendo el título de la excepción
	 *
	 */
	public void print(Object obj) {
		synchronized (this) {
			setDescription(String.valueOf(obj));
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Sobreescribimos el método print(String) de PrintStream
	 * Cuando pasa por aquí se esta escribiendo la traza de la excepción
	 *
	 */
	public void print(String str) {
		synchronized (this) {
			setBacktrace(str);
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Sobreescribimos el método println(Object) de PrintStream
	 * Cuando pasa por aquí se esta escribiendo el título de la excepción
	 *
	 */
	public void println(Object obj) {
		synchronized (this) {
			setDescription(String.valueOf(obj));
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Sobreescribimos el método println(String) de PrintStream
	 * Cuando pasa por aquí se esta escribiendo la traza de la excepción
	 *
	 */
	public void println(String str) {
		synchronized (this) {
			setBacktrace(str);
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * 
	 * @param newValue java.lang.String
	 */
	protected void setBacktrace(String newValue) {
		this.backtrace = newValue;
	}
	/**
	 * <b>Descripción:</b><br>
	 * 
	 * @param newValue java.lang.String
	 */
	private void setDescription(String newValue) {
		this.description = newValue;
	}
	/**
	 * Write a portion of a byte array, blocking if necessary.
	 *
	 * @param  buf   A byte array
	 * @param  off   Offset from which to start taking bytes
	 * @param  len   Number of bytes to write
	 */
	public void write(byte buf[], int off, int len) {
		synchronized (this) {
			String str = new String(buf, off, len);
			if (getBacktrace() == null)
				setBacktrace(str);
			else
				setBacktrace(getBacktrace() + str);
		}
	}
}
