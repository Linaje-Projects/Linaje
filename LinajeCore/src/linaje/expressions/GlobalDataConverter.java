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
package linaje.expressions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import linaje.statics.Constants;
import linaje.utils.Numbers;
import linaje.utils.Strings;

public final class GlobalDataConverter {

	public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = ((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols();
	public static final String DECIMAL_SEPARATOR = String.valueOf(DECIMAL_FORMAT_SYMBOLS.getDecimalSeparator());
	public static final String THOUSANDS_SEPARATOR = String.valueOf(DECIMAL_FORMAT_SYMBOLS.getGroupingSeparator());
	
	private final static Double unformatMonetaryNumber(String[] tokensThousandSeparator, String[] tokensDecimalSeparator) {
	
		// Este método desformatea importes positivos solamente
		// El signo se comprueba en desformatearImporte(String)
		
		try {
	
			//Para que un importe este bien formateado se tiene que cuplir que entre punto y punto no haya mas de 3 cifras (excepto en la última parte que contenga la cantidad decimal)
			if (tokensDecimalSeparator.length == 1) {
				//No hay coma
	
				if (tokensThousandSeparator.length > 1) {
					//Hay puntos separadores de miles
	
					StringBuffer unformattedNumber = new StringBuffer();
					for (int i = 0; i < tokensThousandSeparator.length; i++) {
	
						String numberPart = tokensThousandSeparator[i];
						if (i == 0) {
							//La primera parte del número tiene que ser menor o igual de 3 cifras
							if (numberPart.length() > 0 && numberPart.length() <= 3)
								unformattedNumber.append(numberPart);
							else
								return null;
						}
						else {
							//El resto de partes tiene que ser de 3 cifras
							if (numberPart.length() == 3)
								unformattedNumber.append(numberPart);
							else
								return null;
						}
					}
					return new Double(unformattedNumber.toString());
				}
				else {
					//No hay puntos separadores de miles ni comas
	
					String unformattedNumber = tokensThousandSeparator[0];
					return new Double(unformattedNumber);
				}
			}
			else if (tokensDecimalSeparator.length == 2) {
				//Hay una coma
	
				if (tokensThousandSeparator.length > 1) {
					//Hay puntos separadores de miles
	
					StringBuffer unformattedNumber = new StringBuffer();
					for (int i = 0; i < tokensThousandSeparator.length; i++) {
	
						String numberPart = tokensThousandSeparator[i];
						if (i == 0) {
							//La primera parte del número tiene que ser menor o igual de 3 cifras
							if (numberPart.length() > 0 && numberPart.length() <= 3)
								unformattedNumber.append(numberPart);
							else
								return null;
						}
						else if (i == tokensThousandSeparator.length - 1 && numberPart.length() > 4) {
							//En la última parte tenemos que separar la parte entera de la decimal y tiene que haber al menos un decimal además de que la parte entera sea de 3 cifras
							int indexDecimalSeparator = numberPart.indexOf(DECIMAL_SEPARATOR);
							if (indexDecimalSeparator == 3) {
								if (!(DECIMAL_SEPARATOR.equals(Constants.POINT))) {
									unformattedNumber.append(numberPart.substring(0, indexDecimalSeparator));
									unformattedNumber.append(Constants.POINT);
									unformattedNumber.append(numberPart.substring(indexDecimalSeparator + 1));
								}
								else {
									unformattedNumber.append(numberPart);
								}
								
							}
							else return null;
						}
						else {
							//El resto de partes tiene que ser de 3 cifras
							if (numberPart.length() == 3)
								unformattedNumber.append(numberPart);
							else
								return null;
						}
					}
					return new Double(unformattedNumber.toString());
				}
				else {
					String unformattedNumber;
					if (!(DECIMAL_SEPARATOR.equals(Constants.POINT))) //No hay puntos separadores pero si una coma
						unformattedNumber = Strings.replace(tokensThousandSeparator[0], DECIMAL_SEPARATOR, Constants.POINT);
					else
						unformattedNumber = tokensThousandSeparator[0];
					
					return new Double(unformattedNumber);
				}
			}
		
		} catch (Throwable ex) {
			return null;
		}
		return null;
	}
	
	public final static Double unformatMonetaryNumber(String monetaryFormattedNumber) {
	
		if (monetaryFormattedNumber != null && monetaryFormattedNumber.trim().length() > 0) {
	
			monetaryFormattedNumber = monetaryFormattedNumber.trim();
			
			//Si viene signo lo quitamos para ponerselo despues de analizar el dato
			boolean esImporteNegativo = monetaryFormattedNumber.charAt(0) == '-';
			if (esImporteNegativo || monetaryFormattedNumber.charAt(0) == '+') {
				monetaryFormattedNumber = monetaryFormattedNumber.substring(1);
			}
	
			String[] tokensThousandSeparator = Strings.split(monetaryFormattedNumber, THOUSANDS_SEPARATOR);
			String[] tokensDecimalSeparator = Strings.split(monetaryFormattedNumber, DECIMAL_SEPARATOR);
	
			Double number = unformatMonetaryNumber(tokensThousandSeparator, tokensDecimalSeparator);
			if (number != null && esImporteNegativo)
				number = new Double(- number.doubleValue());
	
			return number;
		}
	
		return null;
	}
	
	public final static Object getNonAlphanumericData(String globalData) {
		return getNonAlphanumericData(globalData, true);
	}
	
	private final static Object getNonAlphanumericData(String originalGlobalData, boolean firstTime) {
	
		try {
	
			//Comprobamos que el dato global viene cargado
			if (originalGlobalData == null || originalGlobalData.trim().length() == 0)
				return null;
				
			String globalData = originalGlobalData.trim();
	
			//Si viene signo lo quitamos para ponerselo despues de analizar el dato
			String sign = Constants.VOID;
			if (globalData.charAt(0) == '-' || globalData.charAt(0) == '+') {
	
				if (globalData.charAt(0) == '-')
					sign = Constants.MINUS;
				
				globalData = globalData.substring(1);
			}
			
			//Comprobamos que el dato global empieza por un número
			if (!Character.isDigit(globalData.charAt(0)) || globalData.length() == 0)
				return null;
	
			String[] tokensDot = Strings.split(globalData, Constants.POINT);
			String[] tokensComa = Strings.split(globalData, Constants.COMMA);
	
			if (tokensDot.length == 1) {
				//1. No hay puntos en el texto
	
				if (tokensComa.length == 1) {
					//2. No hay puntos ni comas en el texto
	
					try {
						
						return new Integer(sign + globalData);
					}
					catch (Throwable ex) {
	
						if (originalGlobalData.indexOf("E") != -1 || originalGlobalData.indexOf("e") != -1) {
	
							//Puede ser un exponencial
							try {
						
								return new Double(sign + globalData);
							}
							catch (Throwable ex2) {
								//No es un numero exponencial
							}
						} else {
	
							//Puede ser un numero demasiado grande para Integer
							try {
								
								return new Long(sign + globalData);
							}
							catch (Throwable ex3) {
							}
						}
						//No es un número entero, pero como no hay puntos ni comas puede ser una fecha
						Date date = getDate(globalData);
						if (date != null) {
							
							return date;
						}
						else if (firstTime) {
						
							//Si tampoco es una fecha obtenemos al parte numérica y la separamos de la alfanumérica
							String numericPart = getNumericPart(globalData);
							return getNonAlphanumericData(sign + numericPart, false);
						}
					}
				}
				else if (tokensComa.length == 2) {
					//3. Hay una coma en el texto
					
					try {
	
						//Reemplazamos la coma por un punto e intentamos convertir a Double
						String doubleFormatNumber = Strings.replace(globalData, Constants.COMMA, Constants.POINT);
						return new Double(sign + doubleFormatNumber);
					}
					catch (Throwable ex) {
	
						if (firstTime) {
							
							//Si no es un número decimal obtenemos al parte numérica y la separamos de la alfanumérica
							String numericPart = getNumericPart(globalData);
							return getNonAlphanumericData(sign + numericPart, false);
						}
					}
				}
			}
			else if (tokensDot.length == 2) {
				//4. Hay un punto en el texto
	
				if (tokensComa.length == 1) {
					//5. Hay un punto en el texto y no hay comas
					
					//Se pueden dar dos casos en uno, que sea un double o una cantidad de mil (1.345 por ej),
					//si se da este caso daremos prioridad a la hipótesis de la cantidad de mil antes que a la cantidad decimal
					if (THOUSANDS_SEPARATOR.equals(Constants.POINT) && tokensDot[0].length() < 4 && tokensDot[1].length() == 3 && Numbers.isIntegerNumber(tokensDot[0]) && Integer.parseInt(tokensDot[0]) != 0) {
	
						//En caso de que todo sean números tenemos una cantidad de miles
						String number = tokensDot[0] + tokensDot[1];
						try {
						
							return new Integer(sign + number);
						}
						catch (Throwable ex) {
	
							//Puede ser un numero demasiado grande para Integer
							try {
								
								return new Long(sign + number);
							}
							catch (Throwable ex2) {
							}
	
							if (firstTime) {
								
								//Si no es un número obtenemos al parte numérica y la separamos de la alfanumérica
								String numericPart = getNumericPart(globalData);
								return getNonAlphanumericData(sign + numericPart, false);
							}	
						}
					}
					else {
	
						//Como no se cumplen las condiciones de que sea una cantidad de miles es un número decimal
						try {
	
							return new Double(sign + globalData);
						}
						catch (Throwable ex) {
	
							if (firstTime) {
								
								//Si no es un número decimal obtenemos al parte numérica y la separamos de la alfanumérica
								String numericPart = getNumericPart(globalData);
								return getNonAlphanumericData(sign + numericPart, false);
							}
						}
					}
				}
				else if (tokensComa.length == 2) {
					//6. Hay un punto y una coma en el texto
					String[] tokensThousandSeparator = DECIMAL_SEPARATOR.equals(Constants.COMMA) ? tokensDot : tokensComa;
					String[] tokensDecimalSeparator = DECIMAL_SEPARATOR.equals(Constants.COMMA) ? tokensComa : tokensDot;
					
					Double number = unformatMonetaryNumber(tokensThousandSeparator, tokensDecimalSeparator);
					if (number != null) {
						
						return new Double(sign + number);
					}
					else if (firstTime) {
						
						//Si no es un número decimal obtenemos al parte numérica y la separamos de la alfanumérica
						String numericPart = getNumericPart(globalData);
						return getNonAlphanumericData(sign + numericPart, false);
					}
				}
				else {
					//7. Hay un punto y varias comas en el texto 
					Double number = null;
					if (THOUSANDS_SEPARATOR.equals(Constants.COMMA)) {
						//Suponemos que es un importe formateado
						String[] tokensThousandSeparator = tokensComa;
						String[] tokensDecimalSeparator = tokensDot;
						
						number = unformatMonetaryNumber(tokensThousandSeparator, tokensDecimalSeparator);
					}
					
					if (number != null) {
						return new Double(sign + number);
					}
					else if (firstTime) {
						//Si no es un número decimal obtenemos al parte numérica y la separamos de la alfanumérica
						String numericPart = getNumericPart(globalData);
						return getNonAlphanumericData(sign + numericPart, false);
					}
				}
			}
			else {
				//8. Vienen varios puntos 
				Double number = null;
				if (THOUSANDS_SEPARATOR.equals(Constants.POINT)) {
					//Suponemos que es un importe formateado
					String[] tokensThousandSeparator = tokensDot;
					String[] tokensDecimalSeparator = tokensComa;
					
					number = unformatMonetaryNumber(tokensThousandSeparator, tokensDecimalSeparator);
				}
				
				if (number != null) {
					return new Double(sign + number);
				}
				else if (firstTime) {
					//Si no es un número decimal obtenemos al parte numérica y la separamos de la alfanumérica
					String numericPart = getNumericPart(globalData);
					return getNonAlphanumericData(sign + numericPart, false);
				}
			}
		
		} catch (Throwable ex) {
			return null;
		}
	
		return null;
	}
	
	public final static Date getDate(String globalData) {
	
		//Comprobamos que el dato global viene cargado
		if (globalData == null || globalData.trim().length() == 0)
			return null;
		else
			return StackElement.getDate(globalData);
	}
	
	public final static String getNumericPart(String data) {
	
		if (data == null || data.trim().length() == 0)
			return null;
			
		String dataTrim = data.trim();
	
		//Si viene signo lo quitamos para ponerselo despues de analizar el dato
		String sign = Constants.VOID;
		if (dataTrim.charAt(0) == '-' || dataTrim.charAt(0) == '+') {
	
			if (dataTrim.charAt(0) == '-')
				sign = Constants.MINUS;
			
			dataTrim = dataTrim.substring(1);
		}
		
		if (dataTrim.length() > 0 && Character.isDigit(dataTrim.charAt(0))) {
	
			StringBuffer numericPart = new StringBuffer();
			boolean exponentialFound = false;
			
			for (int i = 0; i < dataTrim.length(); i++) {
	
				char character = dataTrim.charAt(i);
				if (Character.isDigit(character) || character == ',' || character == '.') {
	
					numericPart.append(character);
				}
				else if ((character == 'E' || character == 'e') && !exponentialFound && i < dataTrim.length() - 1) {
	
					char previousCharacter = dataTrim.charAt(i - 1);
					char nextCharacter = dataTrim.charAt(i + 1);
					if (Character.isDigit(previousCharacter) && Character.isDigit(nextCharacter)) {
	
						numericPart.append(character);
						numericPart.append(nextCharacter);
						i++;
					}
					else {
	
						return sign + numericPart.toString();
					}
				}
				else {
	
					return sign + numericPart.toString();
				}
			}
	
			return sign + numericPart.toString();
		}
		return null;
	}
}
