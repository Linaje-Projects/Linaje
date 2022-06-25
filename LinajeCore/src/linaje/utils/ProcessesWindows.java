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
package linaje.utils;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import linaje.logs.Console;
import linaje.statics.Constants;

public final class ProcessesWindows extends Processes {

	public final static String[] CMD_EXTENSIONS = {"bat", "cmd"};
	
	public static Process executeBat(String batPath) throws IOException {
	
		return executeCMD(Files.preparePathWithSpacesByDirectory(batPath));
	}

	public static Process executeCMD(String processURL) throws IOException {
	
		return executeCMD(processURL, true);
	}

	public static Process executeCMD(String processURL, boolean minimized) throws IOException {
		
		String executableCommand = "cmd /c start";
		if (minimized)
			executableCommand += " /min";
		return executeProcess(executableCommand, processURL, "CMD");
	}

	public static Process executeUrlWithIE(String url) {
		
		Rectangle bounds = null;
		return executeUrlWithIE(url, bounds);
	}

	/**
	 * Aunque Internet explorer esté desfasado, puede ser muy útil abrir por ejemplo una URL de ayuda en PDF con IE,
	 * ya que permite abrirlo sin decoración (barras de herramientas, menús, etc.) y con el tamaño y posición que queramos 
	 * para mostrarlo de forma óptima en la aplicación sin que oculte partes que queramos seguir manteniendo visibles.
	 * (Si lo abriesemos con otra aplicación externa o con el navegador del sistema, se abriría con el tamaño por defecto, no controlado, por encima de nuestra aplicación)
	 **/
	public static Process executeUrlWithIE(String url, Rectangle bounds) {
		
		try {
			//Al Visual Basic no le gusta que se le pasen parámetros con & y con ^, por lo que los sustituimos por otros y luego se vuelven a poner los buenos dentro del execute_ie.vbs
			String[] tokens = Strings.split(url,"&");
			for (int i = 0; i < tokens.length; i++) {
				if (i==0)
					url = tokens[i];
				else
					url = url + "#" + tokens[i];
			}
			tokens = Strings.split(url,"^");
			for (int i = 0; i < tokens.length; i++) {
				if (i==0)
					url = tokens[i];
				else
					url = url + "$" + tokens[i];
			}
			
			//Si padre es distinto de null el navegador se abrirá en en su posición y con su tamaño
			String rutaVBS = getResourcePath("execute_ie.vbs");
			String boundsNav = bounds != null ? Constants.SPACE+bounds.x+Constants.SPACE+bounds.y+Constants.SPACE+bounds.width+Constants.SPACE+bounds.height : Constants.VOID;
			String processURL = rutaVBS + Constants.SPACE + url + boundsNav;
			
			return executeWScript(processURL);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
		return null;
	}
	
	public static String getResourcePath(String resourceName) {
		
		String path = Resources.getResourceURL(resourceName).getFile();
		if (path.startsWith("/"))
			path = path.substring(1);
		return Files.preparePathWithSpaces(path);
	}

	public static Process executeWScript(String url) throws IOException {
	
		String executableCommand = "wscript";
		return executeProcess(executableCommand, url, "wscript");
	}

	public static Process executeFileWith(File file, String app) throws IOException {
		
		Process process = null;
		
		if (app == null) {
			process = Processes.executeFile(file);
		}
		else {
					
			StringBuffer sb = new StringBuffer();
			//Si la aplicación es null, el fichero se abrirá con la aplicación por defecto del SO
			if (app != null) {
				sb.append(app);
			}
			//Si el fichero es null abrimos la aplicación
			if (file != null) {
				if (sb.length() > 0)
					sb.append(Constants.SPACE);
				sb.append(Files.preparePathWithSpaces(file.getAbsolutePath()));
			}
			
			String processURL = sb.toString();
			process = executeCMD(processURL);
		}
		
		return process;
	}
	
	public static Process executeURL(String url) throws IOException {
		//La url a ejecutar puede ser un fichero o una dirección web
		String executableCommand = "rundll32 url.dll,FileProtocolHandler";
		return executeProcess(executableCommand, url, "URL");
	}
	
	public static Process executeMailTo(String email) throws IOException {
		return executeURL("mailto:"+email);
	}
	
	public static Process executeFile(File file) throws IOException {
		
		Process process = null;
		int result = 0;//Por si falla para ejecutar luego de forma alternativa
		
		if (file != null) {
			
			String path = file.getAbsolutePath();
			try {
				String ext = Files.getExt(file);
				if (Lists.arrayContains(CMD_EXTENSIONS, ext)) {
					process = executeBat(path);
					//Esperamos por si falla
					result = process.waitFor();
				}
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
			
			if (process == null || result != 0) {
				//Si el path tiene espacios en blanco lo ponemos entre comillas para abrir el fichero.
				path = Files.preparePathWithSpaces(path);
				return executeURL(path);
			}
		}
		
		return process;
	}
}
