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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import linaje.logs.Console;
import linaje.statics.Constants;

public class Processes {
	
	public static Process executeJava(Class<?> mainClass, String javaParams, String appParams) {

		Process process = null;
		
		try {
				
			StringBuffer sb = new StringBuffer();
			sb.append(Security.getSystemProperty(Security.KEY_JAVA_HOME));
			sb.append(Files.FILE_SEPARATOR);
			sb.append("bin");
			sb.append(Files.FILE_SEPARATOR);
			sb.append("java");
			
			if (javaParams != null) {
				sb.append(' ');
				sb.append(javaParams);
			}
			sb.append(" -cp \"");
			sb.append(Security.getSystemProperty(Security.KEY_JAVA_CLASS_PATH));
			sb.append("\" ");

			sb.append(mainClass.getName());

			if (appParams != null) {
				sb.append(' ');
				sb.append(appParams);
			}
			
			String commandLine = sb.toString();
			Console.println(commandLine);
				
			process = exec(commandLine, mainClass.getName());
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}

		return process;
	}
	
	public static void readProcessStreams(Process process, final String name) {
		readProcessStream(process.getErrorStream(), name, true);
		readProcessStream(process.getInputStream(), name, false);
	}
	
	public static void readProcessStream(final InputStream is, final String name, final boolean isErrorStream) {
		
		Thread thread = new Thread() {
			public void run() {
				try {
		            InputStreamReader isr = new InputStreamReader (is);
		            BufferedReader br = new BufferedReader (isr); 
		            String s = br.readLine();
		            while (s != null) {
		                int tipoDato = isErrorStream ? Console.TYPE_DATA_ERROR : Console.TYPE_DATA_DEFAULT;
		                Console.println("[" + name + "] " + s, tipoDato);
		                s = br.readLine();
		            }
		              
		        } catch (Throwable ex) {
		        	Console.println("Error reading stream " + name + "... :" + ex);
		        	Console.printException(ex);
		        }
				finally {
					Console.println((isErrorStream ? "Error " : "") + "InputStream closed "+name);
					try { is.close(); } catch (IOException ioex) {}
				}
			}
		};
		thread.start();
	}
	
	static Process executeProcess(String executableCommand, String url, String processName) throws IOException {

		Process process = null;
		
		if (url != null && !url.trim().equals(Constants.VOID)) {

			StringBuffer sb = new StringBuffer();
			sb.append(executableCommand.trim());
			sb.append(Constants.SPACE);
			sb.append(url);

			String commandLine = sb.toString();
			Console.println(commandLine);
			
			process = exec(commandLine, processName);
		}
		
		return process;
	}
	
	public static Process executeFile(File file) throws IOException {
		
		if (Security.isOSWindows()) {
			return ProcessesWindows.executeFile(file);
		}
		else {
			Desktop.getDesktop().open(file);
			return null;
		}
	}
	
	public static Process executeURL(String url) throws IOException {
		//La url a ejecutar puede ser un fichero o una dirección web
		if (Security.isOSWindows()) {
			return ProcessesWindows.executeURL(url);
		}
		else {
			try {
				Desktop.getDesktop().browse(new URI(url));
			}
			catch (IOException | URISyntaxException ex) {
				Console.printException(ex);
			}
			return null;
		}
	}
	
	public static Process executeMailTo(String email) throws IOException {
		
		if (Security.isOSWindows()) {
			return ProcessesWindows.executeMailTo(email);
		}
		else {
			try {
				Desktop.getDesktop().mail(new URI("mailto:"+email));
			}
			catch (IOException | URISyntaxException ex) {
				Console.printException(ex);
			}
			return null;
		}
	}
	
	public static Process exec(String command, String processName) {
		
		Process process = null;
		try {
			if (Security.isOSLinux()) {
				String[] cmd = {"/bin/sh", "-c", command};
				process = Runtime.getRuntime().exec(cmd);
			}
			else {
				process = Runtime.getRuntime().exec(command);
			}
		}
		catch (IOException ex) {
			Console.printException(ex);
		}
		
		if (processName == null)
			processName = "Process";
		
		readProcessStreams(process, processName);
		
		return process;
	}
}
