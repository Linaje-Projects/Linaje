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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import linaje.logs.Console;

/**
 * Simulamos el comportamiento de la clase javax.swing.UIDefaults para usar
 * recursos de texto localizados sin tener que iniciar un UI visual y sin
 * necesidad de usar clases de swing
 */
@SuppressWarnings("serial")
public class AppDefaults extends Hashtable<Object, Object> {

	private static final Object PENDING = "Pending";

	private Vector<String> resourceBundles;
	private Map<Locale, Map<String, Object>> resourceCache;

	private Locale defaultLocale = Locale.getDefault();

	public AppDefaults() {
		this(700, .75f);
	}

	public AppDefaults(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		resourceCache = new HashMap<Locale, Map<String, Object>>();
	}

	public AppDefaults(Object[] keyValueList) {
		super(keyValueList.length / 2);
		for (int i = 0; i < keyValueList.length; i += 2) {
			super.put(keyValueList[i], keyValueList[i + 1]);
		}
	}

	public interface LazyValue {
		/**
		 * Creates the actual value retrieved from the <code>UIDefaults</code> table.
		 * When an object that implements this interface is retrieved from the table,
		 * this method is used to create the real value, which is then stored in the
		 * table and returned to the calling method.
		 *
		 * @param table a <code>UIDefaults</code> table
		 * @return the created <code>Object</code>
		 */
		Object createValue(AppDefaults table);
	}

	public interface ActiveValue {
		/**
		 * Creates the value retrieved from the <code>UIDefaults</code> table. The
		 * object is created each time it is accessed.
		 *
		 * @param table a <code>UIDefaults</code> table
		 * @return the created <code>Object</code>
		 */
		Object createValue(AppDefaults table);
	}

	public synchronized void addResourceBundle(String bundleName) {
		if (bundleName == null) {
			return;
		}
		if (resourceBundles == null) {
			resourceBundles = new Vector<String>(5);
		}
		if (!resourceBundles.contains(bundleName)) {
			resourceBundles.add(bundleName);
			resourceCache.clear();
		}
	}

	public synchronized void removeResourceBundle(String bundleName) {
		if (resourceBundles != null) {
			resourceBundles.remove(bundleName);
		}
		resourceCache.clear();
	}

	public String getString(Object key) {
		Object value = get(key);
		return (value instanceof String) ? (String) value : null;
	}

	public String getString(Object key, Locale l) {
		Object value = get(key, l);
		return (value instanceof String) ? (String) value : null;
	}

	public Object get(Object key, Locale l) {
		Object value = getFromHashtable(key);
		return (value != null) ? value : getFromResourceBundle(key, l);
	}

	public Object get(Object key) {
		Object value = getFromHashtable(key);
		return (value != null) ? value : getFromResourceBundle(key, null);
	}

	private Object getFromHashtable(Object key) {
		/*
		 * Quickly handle the common case, without grabbing a lock.
		 */
		Object value = super.get(key);
		if ((value != PENDING) && !(value instanceof ActiveValue) && !(value instanceof LazyValue)) {
			return value;
		}

		/*
		 * If the LazyValue for key is being constructed by another thread then wait and
		 * then return the new value, otherwise drop the lock and construct the
		 * ActiveValue or the LazyValue. We use the special value PENDING to mark
		 * LazyValues that are being constructed.
		 */
		synchronized (this) {
			value = super.get(key);
			if (value == PENDING) {
				do {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
					value = super.get(key);
				} while (value == PENDING);
				return value;
			} else if (value instanceof LazyValue) {
				super.put(key, PENDING);
			} else if (!(value instanceof ActiveValue)) {
				return value;
			}
		}

		/*
		 * At this point we know that the value of key was a LazyValue or an
		 * ActiveValue.
		 */
		if (value instanceof LazyValue) {
			try {
				/*
				 * If an exception is thrown we'll just put the LazyValue back in the table.
				 */
				value = ((LazyValue) value).createValue(this);
			} finally {
				synchronized (this) {
					if (value == null) {
						super.remove(key);
					} else {
						super.put(key, value);
					}
					this.notifyAll();
				}
			}
		} else {
			value = ((ActiveValue) value).createValue(this);
		}

		return value;
	}

	/**
	 * Looks up given key in our resource bundles.
	 */
	private Object getFromResourceBundle(Object key, Locale l) {

		if (resourceBundles == null || resourceBundles.isEmpty() || !(key instanceof String)) {
			return null;
		}

		// A null locale means use the default locale.
		if (l == null) {
			if (defaultLocale == null)
				return null;
			else
				l = defaultLocale;
		}

		synchronized (this) {
			return getResourceCache(l).get(key);
		}
	}

	/**
	 * Returns a Map of the known resources for the given locale.
	 */
	private Map<String, Object> getResourceCache(Locale l) {
		Map<String, Object> values = resourceCache.get(l);

		if (values == null) {
			values = new TextAndMnemonicHashMap();
			for (int i = resourceBundles.size() - 1; i >= 0; i--) {
				
				String bundleName = resourceBundles.get(i);
				try {		
					ResourceBundle b = ResourceBundle.getBundle(bundleName, l);
					Enumeration<String> keys = b.getKeys();

					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();

						if (values.get(key) == null) {
							Object value = b.getObject(key);
							values.put(key, value);
						}
					}
				}
				catch (MissingResourceException mre) {
					Console.printException(mre);
				}
			}
			resourceCache.put(l, values);
		}
		return values;
	}

	private static class TextAndMnemonicHashMap extends HashMap<String, Object> {

		static final String AND_MNEMONIC = "AndMnemonic";
		static final String TITLE_SUFFIX = ".titleAndMnemonic";
		static final String TEXT_SUFFIX = ".textAndMnemonic";

		@Override
		public Object get(Object key) {

			Object value = super.get(key);

			if (value == null) {

				boolean checkTitle = false;

				String stringKey = key.toString();
				String compositeKey = null;

				if (stringKey.endsWith(AND_MNEMONIC)) {
					return null;
				}

				if (stringKey.endsWith(".mnemonic")) {
					compositeKey = composeKey(stringKey, 9, TEXT_SUFFIX);
				} else if (stringKey.endsWith("NameMnemonic")) {
					compositeKey = composeKey(stringKey, 12, TEXT_SUFFIX);
				} else if (stringKey.endsWith("Mnemonic")) {
					compositeKey = composeKey(stringKey, 8, TEXT_SUFFIX);
					checkTitle = true;
				}

				if (compositeKey != null) {
					value = super.get(compositeKey);
					if (value == null && checkTitle) {
						compositeKey = composeKey(stringKey, 8, TITLE_SUFFIX);
						value = super.get(compositeKey);
					}

					return value == null ? null : getMnemonicFromProperty(value.toString());
				}

				if (stringKey.endsWith("NameText")) {
					compositeKey = composeKey(stringKey, 8, TEXT_SUFFIX);
				} else if (stringKey.endsWith(".nameText")) {
					compositeKey = composeKey(stringKey, 9, TEXT_SUFFIX);
				} else if (stringKey.endsWith("Text")) {
					compositeKey = composeKey(stringKey, 4, TEXT_SUFFIX);
				} else if (stringKey.endsWith("Title")) {
					compositeKey = composeKey(stringKey, 5, TITLE_SUFFIX);
				}

				if (compositeKey != null) {
					value = super.get(compositeKey);
					return value == null ? null : getTextFromProperty(value.toString());
				}

				if (stringKey.endsWith("DisplayedMnemonicIndex")) {
					compositeKey = composeKey(stringKey, 22, TEXT_SUFFIX);
					value = super.get(compositeKey);
					if (value == null) {
						compositeKey = composeKey(stringKey, 22, TITLE_SUFFIX);
						value = super.get(compositeKey);
					}
					return value == null ? null : getIndexFromProperty(value.toString());
				}
			}

			return value;
		}

		String composeKey(String key, int reduce, String sufix) {
			return key.substring(0, key.length() - reduce) + sufix;
		}

		String getTextFromProperty(String text) {
			return text.replace("&", "");
		}

		String getMnemonicFromProperty(String text) {
			int index = text.indexOf('&');
			if (0 <= index && index < text.length() - 1) {
				char c = text.charAt(index + 1);
				return Integer.toString((int) Character.toUpperCase(c));
			}
			return null;
		}

		String getIndexFromProperty(String text) {
			int index = text.indexOf('&');
			return (index == -1) ? null : Integer.toString(index);
		}
	}
}
