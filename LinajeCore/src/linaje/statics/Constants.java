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
package linaje.statics;

import java.nio.charset.StandardCharsets;

import linaje.LocalizedStrings;
import linaje.utils.Security;

public class Constants {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.localization.linaje.properties
		
		public String yes;
		public String no;
		public String regexYes;
		public String regexNo;
		public String nonExistElement;
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final String VOID = "";
	public static final String SPACE = " ";
	public static final String POINT =".";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String HASH = "#";
	public static final String ASTERISK = "*";
	public static final String EQUAL = "=";
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String AT = "@";
	public static final String CARAT = "^";
	public static final String ALT_126 = "~";
	public static final String CEDILLA = "Ç";
	public static final String QUOTE = "\"";
	public static final String GT = ">";
	public static final String lT = "<";
	public static final String PARENTHESIS_OPEN = "(";
	public static final String PARENTHESIS_CLOSE = ")";
	public static final String BRACKET_OPEN = "[";
	public static final String BRACKET_CLOSE = "]";
	public static final String COLON = ":";
	public static final String LINE_SEPARATOR = "\n";
	public static final String LINE_SEPARATOR_SYSTEM = Security.getSystemProperty(Security.KEY_LINE_SEPARATOR);
	public static final String LINE_SEPARATOR_DOLLAR = "$$";
	public static final String TAB = "\t";
	public static final String TAB_SPACES = "     ";
	
	public static  final String GET = "get";
	public static  final String SET = "set";
	public static  final String IS = "is";
	public static final String NULL = "null";
	
	public static final String REGEX_LINE_SEPARATOR = "["+LINE_SEPARATOR+LINE_SEPARATOR_SYSTEM+"]";
	public static final String REGEX_LINE_SEPARATOR_EXTENDED = REGEX_LINE_SEPARATOR+"|\\$\\$";//Incluye LINE_SEPARATOR_DOLLAR
	public static final String REGEX_LETTERS_NUMBERS = "[a-zA-Z0-9]";//La diferencia con "\\w" es que éste último admite '_' ([a-zA-Z_0-9])
	public static final String REGEX_LETTERS_NUMBERS_OPPOSITE = "[^a-zA-Z0-9]";//La diferencia con "\\W" es que éste último no admite '_' ([^a-zA-Z_0-9])
	public static final String REGEX_NUMBERS_AND_NUMERIC_FORMAT_CHARS = "[^0-9\\.,E]";//Números, separadores de miles, separadores de decimales y Exponencial
	public static final String REGEX_NUMBERS_AND_NUMERIC_FORMAT_CHARS_OPPOSITE = "[^0-9\\.,E]";//Todo lo que no sean Números, separadores de miles, separadores de decimales y Exponencial
	
	public static final String REGEX_YES = TEXTS.regexYes; //"[tT]rue|[yY]es|[sS][iíIÍ]";
	public static final String REGEX_NO = TEXTS.regexNo; //"[fF]alse|[nN][oO]";
	
	public static final String YES = TEXTS.yes; //"Sí";
	public static final String NO = TEXTS.no; //"No";
	public static final String NON_EXIST_ELEMENT = TEXTS.nonExistElement; //"???";

	public static final String CHARSET_US_ASCII = StandardCharsets.US_ASCII.name();		//Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	public static final String CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1.name();	//ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	public static final String CHARSET_UTF_8 = StandardCharsets.UTF_8.name();			//Eight-bit UCS Transformation Format
	public static final String CHARSET_UTF_16 = StandardCharsets.UTF_16.name();			//Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark

	public static final String CHARSET_DEFAULT = CHARSET_UTF_8;
		
}
