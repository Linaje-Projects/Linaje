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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import linaje.statics.Constants;

/**
 * Multiples utilidades para formatear/desformatear números, así como para generar números aleatorios
 **/
public final class Numbers {

	public static final int MAX_INTEGER_NUMBER = Integer.MAX_VALUE;
    public static final int MIN_INTEGER_NUMBER = Integer.MIN_VALUE;

    public static final double MAX_DECIMAL_NUMBER = Double.MAX_VALUE;
    public static final double MIN_DECIMAL_NUMBER = -Double.MAX_VALUE;
    public static final double MIN_DECIMAL_NUMBER_POSITIVE = Double.MIN_VALUE;
    
    //public static final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();
	
    public static String formatWithZeros(int number, int positions) {
		
		String valorFormateado = String.format("%0"+(positions)+"d", number);
		
		if (valorFormateado.length() > positions) {
			//Truncamos el dato si es mas grande
			valorFormateado = valorFormateado.substring(0, positions);
		}
					
		return valorFormateado;
	}
	
	public static boolean isIntegerNumber(String text) {
		return Strings.isIntegerNumber(text);
	}
	
	public static String formatMonetaryNumber(Number number) {
		return formatMonetaryNumber(number, -1);
	}
	public static String formatMonetaryNumber(Number number, int decimals) {
		
		NumberFormat decimalformat = DecimalFormat.getInstance();
		if (decimals >= 0) {
			decimalformat.setMaximumFractionDigits(decimals);
			decimalformat.setMinimumFractionDigits(decimals);
		}
		return decimalformat.format(number);
	}
	
	public static String formatNumber(Number number) {
		return formatNumber(number, -1);
	}
	public static String formatNumber(Number number, int decimals) {
		
		NumberFormat decimalformat = new DecimalFormat(getNumberFormat(decimals));
		return decimalformat.format(number);
	}
	
	private static String getNumberFormat(int decimals) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(0);//Constants.HASH);
		if (decimals != 0) {
			sb.append(Constants.POINT);
			if (decimals < 0) {
				final int MAX_DECIMALS = 6;
				for (int i = 0; i < MAX_DECIMALS; i++) {
					sb.append(0);//Constants.HASH);
				}
			}
			else {
				for (int i = 0; i < decimals; i++) {
					sb.append(0);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Nos devolverá el valor de un NÚMERO o IMPORTE FORMATEADO con separador de miles (p.ej. 1.234,56 o 1234,56), 
	 * pero NO el valor de un número no formateado como por ejemplo un double en formato String 1234.56
	 */
	public static Number getFormattedNumberValue(String formattedNumber) {
		
		try {
			if (formattedNumber.startsWith(Constants.PLUS))
				formattedNumber = formattedNumber.substring(1);
			
			NumberFormat decimalformat = DecimalFormat.getInstance();
			return decimalformat.parse(formattedNumber);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Nos devolverá el valor de un NÚMERO FORMATEADO O NO FORMATEADO (p.ej. 1234,56 o 1234.56), 
	 * pero NO el valor de un importe formateado con separador de miles 1.234,56 
	 */
	public static Number getUnformattedNumberValue(String unformattedNumber) {
		
		try {
			if (unformattedNumber.startsWith(Constants.PLUS))
				unformattedNumber = unformattedNumber.substring(1);
			
			final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();
			String formattedNumber = unformattedNumber;
			if (DECIMAL_SEPARATOR == ',')
				formattedNumber = Strings.replaceFirst(unformattedNumber, Constants.POINT, Constants.COMMA);
			else
				formattedNumber = Strings.replaceFirst(unformattedNumber, Constants.COMMA, Constants.POINT);
				
			return getFormattedNumberValue(formattedNumber);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Nos devolverá el valor de cualquier número formateado o sin formatear
	 * Si el texto tiene números y comas se tratará como número formateado con separador de miles y sino como número sin formatear
	 */
	public static Number getNumberValue(String number) {
		
		try {
			if (number.indexOf(Constants.POINT) != -1 && number.indexOf(Constants.COMMA) != -1)
				return getFormattedNumberValue(number);
			else
				return getUnformattedNumberValue(number);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static int getRandomNumberInt() {
		return getRandomNumberInt(0, 100);
	}
	/**
	 * Genera un número entero aleatorio entre 'desde' y 'hasta', ambos incluidos
	 */
	public static int getRandomNumberInt(int desde, int hasta) {
		return getRandomNumber(desde, hasta+1).intValue();
	}
	
	public static Number getRandomNumber() {
		return getRandomNumber(0, 101);
	}
	/**
	 * Genera un número aleatorio entre 'desde' y 'hasta', dónde 'desde' está incluido pero 'hasta' no 
	 */
	public static Number getRandomNumber(double desde, double hasta) {
		return getNumberBetween(Math.random(), desde, hasta);
	}
	
	/**
	 * Genera un número en base a factor (0.0 a 1.0) entre 'desde' y 'hasta', dónde 'desde' está incluido pero 'hasta' no 
	 */
	public static Number getNumberBetween(double factor, double desde, double hasta) {
		if (desde > hasta) {
			double oldDesde = desde;
			desde = hasta;
			hasta = oldDesde;
		}
		return factor*(hasta-desde) + desde;
	}
	
	public static boolean isNumberClass(Class<?> classType) {
		return isNumberDecimalClass(classType) || isNumberNonDecimalClass(classType);
	}
	public static boolean isNumberDecimalClass(Class<?> classType) {
		boolean isNumberDecimalClass = 
				Double.class.isAssignableFrom(classType)
			 || Float.class.isAssignableFrom(classType)
			 || double.class.isAssignableFrom(classType)
			 || float.class.isAssignableFrom(classType);
		
		return isNumberDecimalClass;
	}
	public static boolean isNumberNonDecimalClass(Class<?> classType) {
		boolean isNumberNonDecimalClass = 
				Integer.class.isAssignableFrom(classType)
			 || Long.class.isAssignableFrom(classType)
			 || Short.class.isAssignableFrom(classType)
			 || Byte.class.isAssignableFrom(classType)
			 || int.class.isAssignableFrom(classType)
			 || long.class.isAssignableFrom(classType)
			 || short.class.isAssignableFrom(classType)
			 || byte.class.isAssignableFrom(classType)
			 || Number.class.isAssignableFrom(classType);
		
		return isNumberNonDecimalClass;
	}
	
	public static Number getNumberByType(Number number, Class<?> numberType) {
		if (Integer.class.isAssignableFrom(numberType) || int.class.isAssignableFrom(numberType))
			return number.intValue();
		if (Float.class.isAssignableFrom(numberType) || float.class.isAssignableFrom(numberType))
			return number.floatValue();
		if (Double.class.isAssignableFrom(numberType) || double.class.isAssignableFrom(numberType))
			return number.doubleValue();
		if (Long.class.isAssignableFrom(numberType) || long.class.isAssignableFrom(numberType))
			return number.longValue();
		if (Short.class.isAssignableFrom(numberType) || short.class.isAssignableFrom(numberType))
			return number.shortValue();
		if (Byte.class.isAssignableFrom(numberType) || byte.class.isAssignableFrom(numberType))
			return number.byteValue();
		else
			return number;
	}
}
