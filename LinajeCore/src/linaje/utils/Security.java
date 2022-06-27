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

import java.io.File;

import linaje.LocalizedStrings;
import linaje.logs.Console;
import linaje.statics.Constants;

public class Security {

	public static final String KEY_USER_DIR = "user.dir";
	public static final String KEY_USER_HOME = "user.home";
	public static final String KEY_USER_NAME = "user.name";
	public static final String KEY_OS_NAME = "os.name";
	
	public static final String KEY_JAVA_HOME = "java.home";
	public static final String KEY_JAVA_CLASS_PATH = "java.class.path";
	public static final String KEY_JAVA_SPECIFICATION_VERSION = "java.specification.version";
	public static final String KEY_JAVA_IO_TMPDIR = "java.io.tmpdir";
	
	public static final String KEY_FILE_SEPARATOR = "file.separator";
	public static final String KEY_LINE_SEPARATOR = "line.separator";
	
	public static final String ENV_WINDOWS_ALLUSERSPROFILE = "ALLUSERSPROFILE";
	public static final String ENV_WINDOWS_APPDATA = "APPDATA";
	public static final String ENV_WINDOWS_LOCALAPPDATA = "LOCALAPPDATA";
	
	private static Encryptor encryptor = null;
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String separatorChar;
		public String exceedsLength;
		public String javaVersionNotExpected;
		public String dirNotExists;
		public String isNotDir;
		public String securityError;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static String getSystemProperty(String key) {
		
		String value = System.getProperty(key);
		String error = null;
		
		if (value != null) {
			
			//Comprobaciones por si se han manipulado las variables de sistema
			
			if (key.equals(KEY_FILE_SEPARATOR) || key.equals(KEY_LINE_SEPARATOR)) {
				//El separador de línea o de fichero nunca tendrá mas de dos posiciones
				if (value.length() > 2)
					error = TEXTS.separatorChar + Constants.QUOTE + key + Constants.EQUAL + value + Constants.QUOTE + TEXTS.exceedsLength;
			}
			else if (key.equals(KEY_JAVA_SPECIFICATION_VERSION)) {
				try {
					//Nos aseguramos que la versión es un nº entero o decimal separado por punto
					Double.parseDouble(value);
				}
				catch (NumberFormatException e) {
					error = TEXTS.javaVersionNotExpected;
				}					
			}
			else if (key.equals(KEY_USER_DIR) || key.equals(KEY_USER_HOME) || key.equals(KEY_JAVA_HOME) || key.equals(KEY_JAVA_IO_TMPDIR)) {
				//Nos aseguramos de que son directorios y de que existen
				File file = new File(value);
				if (!file.exists())
					error = TEXTS.dirNotExists + Constants.QUOTE + key + Constants.EQUAL + value + Constants.QUOTE;
				else if (!file.isDirectory())
					error = Constants.QUOTE + key + Constants.EQUAL + value + Constants.QUOTE + TEXTS.isNotDir;
			}
		}
		
		if (error == null) {
			return value;
		}
		else {
			Console.println(TEXTS.securityError + error, Console.TYPE_DATA_ERROR);
			return Constants.VOID;
		}
	}
	
	public static String getSystemEnvProperty(String envKey) {
		return System.getenv(envKey);
	}
	
	public static Encryptor getEncryptor() {
		if (encryptor == null) {
			char[] p = {'e','n','c','r','y','p','t','L','I','N','A','J','E','1','2','3','4'};
			byte[] s;
			try {
				s = "saltLinaje1".getBytes(Encryptor.CHARSET_DEFAULT);
			}
			catch (Exception e) {
				s = null;
			}
			
			encryptor = new Encryptor(p, s);
		}
		return encryptor;
	}
	
	public static void setEncryptor(Encryptor encryptor) {
		Security.encryptor = encryptor;
	}
	
	public static boolean isOSWindows() {
		return getSystemProperty(KEY_OS_NAME).toLowerCase().contains("windows");
	}
	
	public static boolean isOSLinux() {
		return getSystemProperty(KEY_OS_NAME).toLowerCase().contains("linux");
	}
}
