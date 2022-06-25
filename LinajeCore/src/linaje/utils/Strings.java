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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import linaje.statics.Constants;


/**
 * Multiples utilidades para trabajar con Strings. Crear, modificar, comparar, dividir, reemplazar, medir, etc.
 **/
public class Strings {

	public static final String LETTERS = "abcdefghijklmnñopqrstuvwxyz";
	public static final String ACCENTS = "áéíóúàèìòùâêîôûäëïöü";
	public static final String NUMBERS = "0123456789";
	public static final String SYMBOLS = "\\ªº|!\"@·#$~%&¬/()=?¿^*¨Ç{}[]<>;,:._-+";
	
	private static final int MAX_INDENT_LEVEL = 20;
	
	public static String[] split(String text, String separator) {
		
		//Ponemos limit -1 para que tenga en cuenta también separadores al final del texto
		//Si quisieramos que se ignoren los separadores al final habría que poner limit 0
		int limit = -1;
		return split(text, separator, limit);
	}

	public static String[] split(String text, String separator, int limit) {
		
		if (text == null || text.length() == 0)
			return new String[0];
		
		//Convertimos el separador en un literal para que no se tengan en cuenta expresiones regulares
		String separatorNoRegex = Pattern.quote(separator);
		return text.split(separatorNoRegex, limit);
	}
	
	public static String replace(String text, String textSearch, String textReplace) {
		
		if (text != null && textSearch != null) {
			if (textReplace == null)
				textReplace = Constants.VOID;
			
			text = text.replace(textSearch, textReplace);
		}	
		return text;
	}
	
	public static String replace(String text, String[] textsSerach, String textReplace) {
		
		if (text != null && textsSerach != null && textsSerach.length > 0) {
			for (int i = 0; i < textsSerach.length; i++) {
				String textSearch = textsSerach[i];
				text = text.replace(textSearch, textReplace);
			}
		}	
		return text;
	}
	
	public static String replace(String text, String[] textsSerach, String[] textsReplace) throws Exception {
		
		if (text != null && textsSerach != null && textsReplace != null && textsSerach.length > 0) {
			
			if (textsSerach.length != textsReplace.length)
				throw new Exception("Tiene que haber el mismo número de textos a buscar que a reemplazar");
				
			for (int i = 0; i < textsSerach.length; i++) {
				String textSerach = textsSerach[i];
				String textReplace = textsReplace[i];
				text = replace(text, textSerach, textReplace);
			}
		}
		
		return text;
	}
	
	public static String replaceFirst(String text, String textSearch, String textReplace) {
		
		if (text != null && textSearch != null) {
			if (textReplace == null)
				textReplace = Constants.VOID;
			
			Pattern patern = Pattern.compile(textSearch.toString(), Pattern.LITERAL);
			text = patern.matcher(text).replaceFirst(Matcher.quoteReplacement(textReplace.toString()));
		}
		
		return text;
	}
	
	public static String fillWithSpaces(String text, int finalTextLength, int textAlignment) {
		
		//Valores de SwingConstants
		final int CENTER  = 0;
		final int RIGHT  = 4;
		
		String formattedText;
		
		int length = text.length();
		if (length == finalTextLength) {
			//Devolvemos el texto tal cual
			formattedText = text;
		}
		else if (length > finalTextLength) {
			//Truncamos el texto
			//if (textAlignment == RIGHT)
			//	formattedText = text.substring(length - finalTextLength);
			//else
				formattedText = text.substring(0, finalTextLength);
		}
		else {
			
			//Añadimos espacios a izquierda o derecha
			if (textAlignment == CENTER) {
				//Primero añadimos la mitad de los blancos delante y luego el resto de blancos detrás
				int blanks = finalTextLength - length;
				int positionsWithoutBlanksRight = length + blanks/2;
				formattedText = String.format("%"+(positionsWithoutBlanksRight)+"s", text);
				formattedText = String.format("%-"+(finalTextLength)+"s", formattedText);
			}
			else if (textAlignment == RIGHT)
				formattedText = String.format("%"+finalTextLength+"s", text);
			else
				formattedText = String.format("%-"+finalTextLength+"s", text);
		}
		
		return formattedText;
	}
	
	public static boolean isIntegerNumber(String text) {
		
		return text != null ? text.matches("\\d+") : false;
	}

	public static boolean containsNumbers(String text) {
		
		return text != null ? text.matches(".*\\d+.*") : false;
	}
	
	public static boolean containsOtherCharacters(String text) {
		
		return text != null ? text.matches(".*[^a-zA-Z0-9]+.*") : false;
	}

	public static String removeNumbers(String text) {
		
		if (text != null)
			text = text.replaceAll("\\d", Constants.VOID);
		
		return text;
	}

	public static String removeOtherCharacters(String text) {
		
		if (text != null)//Con \\W está incluido el carácter _ por lo que no nos vale (sería el equivalente a [^a-zA-Z0-9_])
			text = text.replaceAll("[^a-zA-Z0-9]+", Constants.VOID);
		
		return text;
	}
	
	/**
	 * Asigna un nuevo nombre al que pasemos por parámetro
	 * 		nombre --> nombre (1)
	 * 		nombre (3) --> nombre (4)
	 **/
	public static String newName(String currentName) {
		
		String newName = null;
		if (currentName != null && !currentName.equals(Constants.VOID)) {
			
			int length = currentName.length();
			int numeroNuevo = 1;
			int indexParenthesisOpen = currentName.lastIndexOf(Constants.PARENTHESIS_OPEN);
			if (currentName.endsWith(Constants.PARENTHESIS_CLOSE) && indexParenthesisOpen > 0 && indexParenthesisOpen < length - 2 && currentName.charAt(indexParenthesisOpen - 1) == ' ') {
				String numeroActual = currentName.substring(indexParenthesisOpen+1, length-1);
				try{
					numeroNuevo = Integer.parseInt(numeroActual) + 1;
					newName = currentName.substring(0, indexParenthesisOpen - 1);
				}
				catch(Throwable ex) {
					newName = currentName;
					//No es un numero por lo que ni incrementamos ni hacemos nada, se añadirá (1) al nombre del fichero actual
				}
			}
			else {
				newName = currentName;
			}
			newName = newName + Constants.SPACE + Constants.PARENTHESIS_OPEN + numeroNuevo + Constants.PARENTHESIS_CLOSE;
		}
		else {
			newName = "Nuevo";
		}
		
		return newName;
	}
	
	public static String replaceForbiddenCharacters(String text, char[] forbiddenCharacters, boolean removeSpacesExtra) {
		
		if (text != null && forbiddenCharacters != null && forbiddenCharacters.length > 0) {
			
			int length = forbiddenCharacters.length;
			String[] forbiddenTexts = new String[length];
			for (int i = 0; i < length; i++)
				forbiddenTexts[i] = Character.toString(forbiddenCharacters[i]);
			
			text = replace(text, forbiddenTexts, Constants.SPACE);
			
			if (removeSpacesExtra)
				text = removeSpacesExtra(text);
		}
		
		return text;
	}
	
	/**
	 * Quitamos espacios seguidos extra
	 **/
	public static String removeSpacesExtra(String text) {
		
		if (text != null)
			text = text.replaceAll("\\s\\s+", Constants.SPACE);

		return text;
	}
	
	/**
	 * Quitamos espacios a la izquierda
	 **/
	public static String removeSpacesLeading(String text) {
		
		if (text != null)
			text = text.replaceFirst("^\\s+", Constants.SPACE);

		return text;
	}

	/**
	 * Quitamos espacios a la derecha
	 **/
	public static String removeSpacesTrailing(String text) {
	
		if (text != null)
			text = text.replaceFirst("\\s+$", Constants.SPACE);
	
		return text;
	}
	
	public static String capitalizeFirstWord(String text) {
		return capitalizeWords(text, 1, null, true, null);
	}
	public static String capitalizeFirstWord(String text, String regexExcludeWords) {
		return capitalizeWords(text, 1, null, true, regexExcludeWords);
	}
	public static String capitalizeAllWords(String text) {
		return capitalizeWords(text, 0, null, true, null);
	}
	public static String capitalizeAllWords(String text, List<String> excludeWords) {
		return capitalizeWords(text, 0, excludeWords, true, null);
	}
	public static String capitalizeAllWords(String text, List<String> excludeWords, String regexExcludeWords) {
		return capitalizeWords(text, 0, excludeWords, true, regexExcludeWords);
	}
	public static String capitalizeWords(String text, int first_n_words, List<String> excludeWords, boolean firstWordAlways, String regexExcludeWords) {
		
		String[] words = split(text, Constants.SPACE);
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			//Si first_n_words es mayor de 0, sólo capitalizaremos las palabras que se especifiquen
			boolean capitalize = (first_n_words < 1 || i < first_n_words) &&  (excludeWords == null || !excludeWords.contains(word));
			//Si firstWordAlways es true, capitalizamos la primera palabra aunque esté en la lista de excluidos
			capitalize = capitalize || (firstWordAlways && i == 0);
			//Si la palabra cumple la expresión regular regexPalabraExcluir, la excluiremos aunque sea la primera palabra y firstWordAlways sea true
			capitalize = capitalize && (regexExcludeWords == null || !word.matches(regexExcludeWords));
			
			String wordChecked = capitalize ? capitalize(word) : word;
			sb.append(wordChecked);
			if (i < words.length - 1)
				sb.append(Constants.SPACE);
		}
		text = sb.toString();
		
		return text;
	}
	
	public static String capitalize(String text) {
		return capitalize(text, true);
	}
	public static String capitalize(String text, boolean restOfLettersToLowercase) {
		if (text != null && text.length() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(text.substring(0, 1).toUpperCase());
			if (restOfLettersToLowercase)
				sb.append(text.substring(1).toLowerCase());
			else
				sb.append(text.substring(1));
			text = sb.toString();
		}
		return text;
	}
	
	public static String getIndent(int nivel) {
		
		if (nivel > MAX_INDENT_LEVEL)
			nivel = MAX_INDENT_LEVEL;
		
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < nivel; i++) {
			sb.append(Constants.TAB_SPACES);
		}
		return sb.toString();
	}
	
	public static int compare(String s1, String s2) {
		return compare(s1, s2, true, false);
	}
	public static int compareIgnoreCase(String s1, String s2) {
		return compare(s1, s2, true, true);
	}
	public static int compare(String s1, String s2, boolean nullsLast, boolean ignoreCase) {
		
		if (s1 == null)
			return nullsLast ? 1 : -1;
		else if (s2 == null)
			return nullsLast ? -1 : 1;
		else
			return ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
	}
	
	public static String getExceptionMessage(Exception ex) {
		String exMessage = ex.getMessage() != null ? ex.getMessage() : ex.toString();
		return exMessage;
	}

	public static Rectangle getStringBounds(Graphics2D g2d, String str, float x, float y) {
		FontRenderContext frc = g2d.getFontRenderContext();
		Font font = g2d.getFont();
		return getStringBounds(str, font, frc, x, y);
	}
	public static Rectangle getStringBounds(String str, FontMetrics fm, float x, float y) {
		Font font = fm.getFont();
		FontRenderContext frc = fm.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	public static Rectangle getStringBounds(String str, Font font, FontRenderContext frc, float x, float y) {
		GlyphVector gv = font.createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	
	public static int getStringWidth(String str, FontMetrics fm) {
		return fm.stringWidth(str);
	}

	public static String[] getLines(String text) {
	
		if (text == null)
			text = Constants.VOID;
		//Tendremos en cuenta tanto los saltos de línea normales como los de $$
		String textAux = text.replaceAll(Constants.REGEX_LINE_SEPARATOR, Matcher.quoteReplacement(Constants.LINE_SEPARATOR_DOLLAR));
		String[] lines = split(textAux, Constants.LINE_SEPARATOR_DOLLAR);
	
		return lines;
	}

	public static int getMaxStringWidth(FontMetrics fm, String[] lines) {
	
		int maxStringWidth = 0;
		for (int i = 0; i < lines.length; i++) {
	
			int stringWidth = fm.stringWidth(lines[i]);
			if (stringWidth > maxStringWidth)
				maxStringWidth = stringWidth;
		}
	
		return maxStringWidth;
	}
}
