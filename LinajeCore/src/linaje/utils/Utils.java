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

import java.util.*;
import java.util.Map.Entry;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import linaje.statics.Constants;
import linaje.logs.Console;

public class Utils {

	//
	// URLs
	//
	
	public static File downloadURL(File downloadedFile, final String url) throws MalformedURLException, IOException {
		
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		
		try {
		
			in = new BufferedInputStream(new URL(url).openStream());
		    fout = new FileOutputStream(downloadedFile);
		
		    final byte data[] = new byte[1024];
		    int count;
		    while ((count = in.read(data, 0, 1024)) != -1) {
		        fout.write(data, 0, count);
		    }
		}
		finally {
			if (in != null) {
				try { in.close(); } catch (IOException e) {}
			}
			if (fout != null) {
				try { fout.close(); } catch (IOException e) {}
			}
		}
		
		return downloadedFile;
	}
	
	public static String readURL(String url) throws Throwable {
		return readURL(new URL(url));
	}
	public static String readURL(URL url) throws Throwable {
		
		return Reader.read(new InputStreamReader(url.openStream()));
	}
	
	//
	// Fonts
	//
	
	public static Font getFontWithName(Font font, String fontName) {
		return new Font(fontName, font.getStyle(), font.getSize());
	}
	public static Font getFontWithStyle(Font font, int fontStyle) {
		return font.deriveFont(fontStyle);
	}
	public static Font getFontWithSize(Font font, int fontSize) {
		return font.deriveFont((float)fontSize);
	}
	public static Font getFontWithSizeFactor(Font font, float factor) {
		return getFontWithSize(font, Math.round(font.getSize()*0.8f));
	}
	public static LFont getFontWithLayout(Font font, int fontLayout) {
		return new LFont(font, fontLayout);
	}

	/**
	 * INTERLINEADO
	 * 
	 * Attribute key to control tracking.  Values are instances of
	 * <b><code>Number</code></b>.  The default value is
	 * <code>0</code>, which means no additional tracking.
	 * 
	 * <p>The constant values {@link #TRACKING_TIGHT} and {@link
	 * #TRACKING_LOOSE} are provided.
	 * 
	 * <p>The tracking value is multiplied by the font point size and
	 * passed through the font transform to determine an additional
	 * amount to add to the advance of each glyph cluster.  Positive
	 * tracking values will inhibit formation of optional ligatures.
	 * Tracking values are typically between <code>-0.1</code> and
	 * <code>0.3</code>; values outside this range are generally not
	 * desireable.
	 */
	public static Font getFontWithTracking(Font font, float tracking) {

		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put(TextAttribute.TRACKING, tracking);
		Font newFonttWithTracking = font.deriveFont(attributes);
		
		return newFonttWithTracking;
	}
	
	public static Font[] getFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}
	
	
	/**
	 * Obtenemos una fuente a partir de su fontObject.toString()
	 **/
	public static Font decodeFont(String encodedFont) {
		
		try {
			HashMap<String, String> fontFieldsMap = decodeFieldValuesMap(encodedFont);
			String name = fontFieldsMap.get("name");
			String style = fontFieldsMap.get("style");
			String size = fontFieldsMap.get("size");
			String layoutMode = fontFieldsMap.get("layoutMode");
			
			if (name != null && style != null && size != null) {
				
				int sizeInt = Integer.parseInt(size);
				int styleInt;
				if (style.equalsIgnoreCase("plain"))
	                styleInt = Font.PLAIN;
				else if (style.equalsIgnoreCase("bold"))
	                styleInt = Font.BOLD;
				else if (style.equalsIgnoreCase("italic"))
	                styleInt = Font.ITALIC;
				else if (style.equalsIgnoreCase("bolditalic"))
	                styleInt = Font.BOLD | Font.ITALIC;
	            else if (Numbers.isIntegerNumber(style))
	            	styleInt = Integer.parseInt(style);
	            else
	            	styleInt = Font.PLAIN;
				
				if (layoutMode == null)
					return new Font(name, styleInt, sizeInt);
				else {
					int layoutModeInt = Integer.parseInt(layoutMode);
					return new LFont(name, styleInt, sizeInt, layoutModeInt);
				}
			}
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
		
		return Font.decode(encodedFont);
	}
	
	//
	// Compare objects
	//
	
	public static <T> int compare(T o1, T o2) {
		return compare(o1, o2, true);
	}
	@SuppressWarnings("unchecked")
	public static <T> int compare(T o1, T o2, boolean nullsLast) {
		
		if (o1 == null)
			return nullsLast ? 1 : -1;
		else if (o2 == null)
			return nullsLast ? -1 : 1;
		else {
			if (o1 instanceof Comparable) {
				if (o1.getClass() == o2.getClass())
					return ((Comparable<T>) o1).compareTo(o2);
				else if (o1 instanceof Number && o2 instanceof Number)
					return new Double(((Number) o1).doubleValue()).compareTo(new Double(((Number) o2).doubleValue()));
				else
					return Strings.compare(o1.toString(), o2.toString(), nullsLast, false);
			}
			else
				return Strings.compare(o1.toString(), o2.toString(), nullsLast, false);
		}
	}
	
	public static boolean propertyChanged(Object oldValue, Object newValue) {
		boolean bothNull = oldValue == null && newValue == null;
		if (bothNull)
			return false;
		else if (oldValue == null || newValue == null)
			return true;
		else {
			boolean equals = oldValue.equals(newValue);
			//Si alguno es un StateColor o un ReferencedColor, tenemos que comparar los toString
			if (equals && (oldValue instanceof StateColor || oldValue instanceof ReferencedColor || newValue instanceof StateColor || newValue instanceof ReferencedColor))
				equals = oldValue.toString().equals(newValue.toString());
			return !equals;
		}
	}
	
	
	//
	// Encoded fields
	//
	
	/**
	 * Returns a map with String encoded field values form a complex String encoded class values
	 * if encodedFieldValues String is:
	 * 		"java.awt.Font[family=Arial,name=Arial,style=bold,size=12]"
	 * this method will return a map like this:
	 * Map: key:"family", value:"Arial"
	 * 		key:"name", value:"Arial"
	 * 		key:"style", value:"bold"
	 * 		key:"size", value:"12"
	 */
	public static HashMap<String, String> decodeFieldValuesMap(String encodedFieldValues) {
		String fieldsSeparator = encodedFieldValues.contains(Constants.SEMICOLON) ? Constants.SEMICOLON : Constants.COMMA;
		return decodeFieldValuesMap(encodedFieldValues, fieldsSeparator);
	}
	public static HashMap<String, String> decodeFieldValuesMap(String encodedFieldValues, String fieldsSeparator) {
		
		LinkedHashMap<String, String> encodedFieldValuesMap = new LinkedHashMap<>();
		
		final String VALUE_ASIGNMENT = Constants.EQUAL;
		
		int beginIndex = encodedFieldValues.indexOf(Constants.BRACKET_OPEN);
		int endIndex = encodedFieldValues.lastIndexOf(Constants.BRACKET_CLOSE);
		
		String encodedFieldValuesText = endIndex != -1 ? encodedFieldValues.substring(beginIndex+1, endIndex) : encodedFieldValues;
		String[] encodedFieldValuesSplitted = Strings.split(encodedFieldValuesText, fieldsSeparator);
		
		for (int i = 0; i < encodedFieldValuesSplitted.length; i++) {
			try {	
				String encodedFieldValue = encodedFieldValuesSplitted[i];
				String[] fieldValue = Strings.split(encodedFieldValue, VALUE_ASIGNMENT);
				if (fieldValue.length > 1) {
					String fieldName = fieldValue[0];
					String encodedValue = fieldValue[1];
					encodedFieldValuesMap.put(fieldName, encodedValue);
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		
		return encodedFieldValuesMap;
	}
	
	/**
	 * Returns a complex String encoded class values form a map with String encoded field values 
	 * if Map is:
	 *		key:"family", value:"Arial"
	 * 		key:"name", value:"Arial"
	 * 		key:"style", value:"bold"
	 * 		key:"size", value:"12"
	 * and sourceClass is java.awt.Font
	 * this method will return a String like this: "java.awt.Font[family=Arial,name=Arial,style=bold,size=12]"
	 */
	public static String encodeFieldValuesMap(HashMap<String, String> encodedFieldValuesMap, Class<?> sourceClass) {
		String fieldsSeparator = Constants.COMMA;
		for (Iterator<String> iterator = encodedFieldValuesMap.values().iterator(); fieldsSeparator.equals(Constants.SEMICOLON) || iterator.hasNext();) {
			//Si algún valor tiene comas, cambiamos el separador de campos a punto y coma
			String value = iterator.next();
			if (value.contains(Constants.COMMA))
				fieldsSeparator = Constants.SEMICOLON;
		}
		return encodeFieldValuesMap(encodedFieldValuesMap, sourceClass, fieldsSeparator);
	}
	public static String encodeFieldValuesMap(HashMap<String, String> encodedFieldValuesMap, Class<?> sourceClass, String fieldsSeparator) {
		
		final String VALUE_ASIGNMENT = Constants.EQUAL;
		
		StringBuffer sb = new StringBuffer();
		if (sourceClass != null)
			sb.append(sourceClass.getName());
		sb.append(Constants.BRACKET_OPEN);
		int numFields = 0;
		for (Iterator<Entry<String, String>> iterator = encodedFieldValuesMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			String fieldName = entry.getKey();
			String value = entry.getValue();
			if (value != null) {
				if (numFields > 0)
					sb.append(fieldsSeparator);
				sb.append(fieldName);
				sb.append(VALUE_ASIGNMENT);
				sb.append(value);
				numFields++;
			}
		}
		sb.append(Constants.BRACKET_CLOSE);
		
		return sb.toString();
	}
	/*
	public static <T> Class<T> getCurrentGenericType(Object source) {
		return getCurrentGenericType(source, 0);
	}
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getCurrentGenericType(Object source, int paramIndex) {
		return (Class<T>)((ParameterizedType) source.getClass().getGenericSuperclass()).getActualTypeArguments()[paramIndex];
	}*/
}
