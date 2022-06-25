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
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import linaje.logs.Console;
import linaje.statics.Constants;

public class Resources {

	public static URL getResourceURL(String resourceName) {
		if (resourceName == null)
			return null;
		
		if (!resourceName.startsWith(Resources.RESOURCE_PATH_SEPARATOR))
			resourceName = Resources.RESOURCE_PATH_SEPARATOR+resourceName;
		return Files.class.getResource(resourceName);
	}

	public static InputStream getResourceAsStream(String resourceName) {
		if (resourceName == null)
			return null;
		
		if (!resourceName.startsWith(Resources.RESOURCE_PATH_SEPARATOR))
			resourceName = Resources.RESOURCE_PATH_SEPARATOR+resourceName;	
		return Files.class.getResourceAsStream(resourceName);
	}

	public static List<String> getResourceNamesFromResourceDir(String resourceDir) throws IOException {
		return getResourceNamesFromResourceDir(resourceDir, true);
	}
	public static List<String> getResourceNamesFromResourceDir(String resourceDir, boolean fullResourceNames) throws IOException {
		return getResourceNamesFromResourceDir(resourceDir, fullResourceNames, false);
	}
	public static List<String> getResourceNamesFromResourceDir(String resourceDir, boolean fullResourceNames, boolean includeSubDirs) throws IOException {
		
		List<String> resourceNames = Lists.newList();
	
		URL url = getResourceURL(resourceDir);
	    if (url != null) {
		    	
	    	String resourcePrefix = resourceDir.startsWith(Resources.RESOURCE_PATH_SEPARATOR) ? resourceDir.substring(1) : resourceDir;
    		resourcePrefix = !resourcePrefix.endsWith(Resources.RESOURCE_PATH_SEPARATOR) ? resourcePrefix + Resources.RESOURCE_PATH_SEPARATOR : resourcePrefix;
			
    		if (url.getProtocol().equals("file")) {
	    		
	    		InputStream in = getResourceAsStream(resourceDir);
	    		List<String> lines = Reader.readLines(new InputStreamReader(in));
	    		for (String fileName : lines) {
					if (fullResourceNames)
						fileName = resourcePrefix + fileName;
					resourceNames.add(fileName);
				}
	    	}
	    	else if (url.getProtocol().equals("jar")) {
	    		
	    		String path = url.getPath();
				String jarPath = path.substring(5, path.indexOf("!"));
				
				JarFile jar = new JarFile(URLDecoder.decode(jarPath, Constants.CHARSET_UTF_8));
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if (entryName.startsWith(resourcePrefix) && !entryName.equals(resourcePrefix)) {
						String simpleName = entryName.substring(resourcePrefix.length());
						String resourceName = fullResourceNames ? entryName : simpleName;
						if (includeSubDirs || !simpleName.contains(Resources.RESOURCE_PATH_SEPARATOR))
							resourceNames.add(resourceName);
					}
				}
			}
		}
		return resourceNames;
	}

	public static List<URL> getResourceURLsFromResourceDir(String resourceDir) throws IOException {
		return getResourceURLsFromResourceDir(resourceDir, null);
	}

	public static List<URL> getResourceURLsFromResourceDir(String resourceDir, FileFilter fileFilter) throws IOException {
		
		List<URL> resourceURLs = Lists.newList();
		List<String> resourceNames = getResourceNamesFromResourceDir(resourceDir);
		for (int i = 0; i < resourceNames.size(); i++) {
			String resourceName = resourceNames.get(i);
			if (fileFilter == null || fileFilter.accept(new File(resourceName))) {
				URL resourceURL = getResourceURL(resourceName);
				resourceURLs.add(resourceURL);
			}
		}
		return resourceURLs;
	}

	public static byte[] read(String resourceName) throws IOException {
		return read(getResourceURL(resourceName));
	}
	public static String readFirstLine(String resourceName) throws IOException {
		return readFirstLine(getResourceURL(resourceName));
	}
	public static List<String> readLines(String resourceName) throws IOException {
		return readLines(resourceName, -1, true);
	}
	public static List<String> readLines(String resourceName, int numLines, boolean readBlankLines) throws IOException {
		return readLines(getResourceURL(resourceName), numLines, readBlankLines);
	}
	public static Properties readProperties(String resourceName) throws IOException {
		return readProperties(getResourceURL(resourceName));
	}
	public static Object readSerializedObject(String resourceName) throws IOException, ClassNotFoundException {
		return readSerializedObject(getResourceURL(resourceName));
	}
	public static String readText(String resourceName) throws IOException {
		return readText(getResourceURL(resourceName));
	}
	public static void playSound(String resourceName) {
		playSound(getResourceURL(resourceName));
	}
	
	public static byte[] read(URL url) throws IOException {
		return Reader.read(url.openStream());
	}
	public static String readFirstLine(URL textUrl) throws IOException {
		return Reader.readFirstLine(new InputStreamReader(textUrl.openStream()));
	}
	public static List<String> readLines(URL textUrl) throws IOException {
		return Reader.readLines(new InputStreamReader(textUrl.openStream()));
	}
	public static List<String> readLines(URL textUrl, int numLines, boolean readBlankLines) throws IOException {
		return Reader.readLines(new InputStreamReader(textUrl.openStream()), numLines, readBlankLines);
	}
	public static Properties readProperties(URL propertiesUrl) throws IOException {
		return Reader.readProperties(propertiesUrl.openStream());
	}
	public static Object readSerializedObject(URL url) throws IOException, ClassNotFoundException {
		return Reader.readSerializedObject(url.openStream());
	}
	public static String readText(URL textUrl) throws IOException {
		return Reader.read(new InputStreamReader(textUrl.openStream()));
	}
	/*
	 * Lo metemos en try catch para no interrumpir el código por no poder reproducir un audio
	 * Si se quiere controlar la excepción usar el playSound de Reader
	 */
	public static void playSound(URL url) {
		try {
			Reader.playSound(url.openStream());
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
	public static final String RESOURCE_PATH_SEPARATOR = "/";
}
