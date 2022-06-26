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
package linaje;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.statics.Directories;
import linaje.utils.Files;
import linaje.utils.Lists;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.Resources;

/**
 * Clase donde encapsularemos los parámetros principales de una aplicación
 *  - name: Nombre de la aplicación
 *  - id: Identificador único de la aplicación
 *  - environmentName: Nombre del entorno de ejecución
 *  - environmentID: Identificador único del entorno de ejecución
 **/
public class App {

	private User currentUser = null;
	
	private String name = null;
	private String id = null;
	private String environmentName = null;
	private String environmentID = null;
	private String environmentProductionID = null;
	private Color environmentColor = null;
	
	private String versionName = null;
	private String locale = null;
	private double version = 1.0;
	
	//Used by Console to assign a diferent port for each app and evironment
	private List<String> environmentIDs = null;
	private List<String> appIDs = null;
		
	private static App currentApp = null;
	
	private static HashMap<String, Object> mapObjectsByName = null;
	
	private static AppDefaults defaults = null;
	
	public App() {
		initialize();
	}
	
	public App(String name) throws Exception {
		setName(name);
		initialize();
	}
	
	public App(String name, String id) throws Exception {
		setName(name);
		setId(id);
		initialize();
	}
	
	private void initialize() {
		try {
			//Si encontramos un fichero de configuración en el directorio de aplicación, lo iniciamos
			String configResourceName = "defaultAppConfig.cfg";

			File configFile = new File(Directories.APP_EXECUTION_DIR, configResourceName);
			if (configFile.exists()) {
				initFromConfig(configFile);
			}
			else {
				URL url = Resources.getResourceURL(configResourceName);
				if (url != null)
					initFromConfig(configResourceName);
			}

		} catch (Exception ex) {
			Console.printException(ex);
		}
	}
	
	public static App getCurrentApp() {
		if (currentApp == null)
			currentApp = new App();
		return currentApp;
	}

	public static void setCurrentApp(App aplicacionActual) {
		App.currentApp = aplicacionActual;
	}
	
	/**
	 * get param value from param key in main methods
	 */
	public static String getParamValue(String[] params, String paramKey) {
		try {
			int paramIndex = Lists.arrayIndexOf(params, paramKey);
			int paramValueIndex = paramIndex+1;
			return paramIndex != -1 ? params[paramValueIndex] : null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		if (name == null)
			name = "Linaje App";
		return name;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		if (id == null)
			id = "LA";
		return id;
	}
	public void setEnvironmentID(String environmentID) {
		this.environmentID = environmentID;
	}
	public String getEnvironmentID() {
		return environmentID;
	}
	public String getEnvironmentName() {
		return environmentName;
	}
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	
	public User getCurrentUser() {
		if (currentUser == null) {
			currentUser = new User();
		}
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
	
	public void setEnvironmentIDs(List<String> environmentIDs) {
		this.environmentIDs = environmentIDs;
	}
	public List<String> getEnvironmentIDs() {
		if (environmentIDs == null)
			environmentIDs = Lists.newList();
		return environmentIDs;
	}
	public String getEnvironmentProductionID() {
		if (environmentProductionID == null)
			environmentProductionID = "PROD";
		return environmentProductionID;
	}
	public void setEnvironmentProductionID(String environmentProductionID) {
		this.environmentProductionID = environmentProductionID;
	}
	public Color getEnvironmentColor() {
		return environmentColor;
	}
	public void setEnvironmentColor(Color environmentColor) {
		this.environmentColor = environmentColor;
	}
	
	public void setAppIDs(List<String> appIDs) {
		this.appIDs = appIDs;
	}
	public List<String> getAppIDs() {
		if (appIDs == null)
			appIDs = Lists.newList();
		return appIDs;
	}
	
	public static HashMap<String, Object> getMapObjectsByName() {
		if (mapObjectsByName == null) {
			mapObjectsByName = new LinkedHashMap<String, Object>();
		}
		return mapObjectsByName;
	}
		
	public static String getObjectName(Object object) {
		if (object != null) {
			HashMap<String, Object> mapUIObjectsByPrefix = getMapObjectsByName();
			Set<Entry<String, Object>> entries = mapUIObjectsByPrefix.entrySet();
			for (Iterator<Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				if (entry.getValue() == object) {
					return entry.getKey();
				}
			}
			return object.getClass().getSimpleName();
		}
		return null;
	}
	
	public static AppDefaults getDefaults() {
		if (defaults == null) {
			defaults = new AppDefaults();
			//Init default App first to get locale form config file
			if (currentApp == null)
				getCurrentApp();
		}
		return defaults;
	}
	
	//Métodos similares a los de UIManager (Los utilizamos para la localización de textos)
	
	public static Object get(Object key) {
        return getDefaults().get(key);
    }

    public static Object get(Object key, Locale l) {
        return getDefaults().get(key,l);
    }
    
	public static String getString(Object key) {
        return getDefaults().getString(key);
    }

   public static String getString(Object key, Locale l) {
        return getDefaults().getString(key,l);
    }
   
   public static int getInt(Object key) {
       return getInt(key, 0);
   }

   public static int getInt(Object key, Locale l) {
       return getInt(key, l, 0);
   }

   public static int getInt(Object key, int defaultValue) {
       return getInt(key, null, defaultValue);
   }

   public static int getInt(Object key, Locale l, int defaultValue) {
       Object value = get(key, l);

       if (value instanceof Integer) {
           return ((Integer)value).intValue();
       }
       if (value instanceof String) {
           try {
               return Integer.parseInt((String)value);
           } catch (NumberFormatException nfe) {}
       }
       return defaultValue;
   }

	public void initFromConfig(File configFile) throws IOException {
		initFromConfig(Files.readLines(configFile, -1, false));
	}

	public void initFromConfig(String configResourceName) throws IOException {
		URL url = Resources.getResourceURL(configResourceName);
		initFromConfig(url);
	}
	public void initFromConfig(URL url) throws IOException {
		if (url != null)
			initFromConfig(Resources.readLines(url, -1, false));
	}

	protected void initFromConfig(List<String> linesConfig) {
		
		String[] encodedFields = Lists.listToArray(linesConfig, String.class);
		ReflectAccessSupport ras = new ReflectAccessSupport(this);
		ras.setEncodedFieldValues(null, encodedFields);
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
		if (locale != null && !locale.equals(Constants.VOID))
			Locale.setDefault(Locale.forLanguageTag(locale));
	}
	
	public String getLocale() {
		if (locale == null)
			locale = Locale.getDefault().toLanguageTag();
		return locale;
	}
}
