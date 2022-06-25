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

import java.awt.*;

import linaje.logs.Console;
import linaje.statics.Constants;
 
public final class Colors {

	private static final String COLOR_HEX_BLACK = "000000";
	//public static final String COLOR_ANSI_RESET = "\033[0;00m";
	public static final String COLOR_ANSI_RESET = "\033[0m";
	
	public static final double FACTOR_DEFAULT = 0.4;
	
	public static Color getInverseColor(Color color) {
	
		if (color == null)
			return null;
	
		int r = Math.abs(255 - color.getRed());
		int g = Math.abs(255 - color.getGreen());
		int b = Math.abs(255 - color.getBlue());
		
		Color inverseColor = new Color(r, g, b);
	
		//Si el color es gris ponemos el inverso blanco o negro según sea gris claro o gris oscuro
		if (r == g && g == b) {
			if (isColorDark(color))
				inverseColor = Color.white;
			else
				inverseColor = Color.black;
		}
		
		return inverseColor;
	}
	
	public static Color getPureColorNextTo(Color color) {
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int alpha = color.getAlpha();
	
		r = r > 128 ? 255 : 0;
		g = g > 128 ? 255 : 0;
		b = b > 128 ? 255 : 0;
		
		Color pureColor = new Color(r, g, b, alpha);
		
		return pureColor;
	}
	
	/**
	 * Creates a darker version of this color.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB 
	 * components of the color to create a darker version of the same 
	 * color. Although <code>brighter</code> and <code>darker</code> are 
	 * inverse operations, the results of a series of invocations of 
	 * these two methods may be inconsistent because of rounding errors. 
	 * @return  a new <code>Color</code> object, 
	 *                              a darker version of this color.
	 * @see        java.awt.Color#brighter
	 * @since      JDK1.0
	 */
	public static Color darker(Color color, double factor) {
	
		if (factor <= 0)
			return color;
		else if (factor >= 1)
			return Color.black;
			
		//Ajustamos el color para que darker funcione con colores muy oscuros o saturados
		Color adjustedColor = color;//adjustColor(color, (float) factor);
		int r = adjustedColor.getRed();
		int g = adjustedColor.getGreen();
		int b = adjustedColor.getBlue();
		int alpha = color.getAlpha();
		
		Color darkerCol = new Color(Math.max((int) (r * (1.0 - factor)), 0),
							  Math.max((int) (g * (1.0 - factor)), 0),
							  Math.max((int) (b * (1.0 - factor)), 0),
							  alpha);
	
		return darkerCol;
	}
	public static Color getDarkerColor(Color color) {
		return darker(color, FACTOR_DEFAULT);
	}
	
	private static double getLuminanceFactor(Color color, double factor) {
		
		float luminance = getLuminance(color);
		double luminanceFactor = factor + (1-factor) * (1 - luminance)/6;
		
		return luminanceFactor;
	}
	
	/**
	 * Creates a brighter version of this color.
	 */
	public static Color brighter(Color color, double factor) {
		return brighter(color, factor, true);
	}	
	public static Color brighter(Color color, double factor, boolean increaseFactorLuminance) {
		
		if (factor <= 0)
			return color;
		else if (factor >= 1)
			return Color.white;
		
		//Aumentamos ligeramente el factor para que se asemeje finalmente al inverso de darker
		double factorIncrease = Math.min(factor*0.5, 0.1);
		factor = Math.min(factor + factorIncrease, 1);
		
		if (increaseFactorLuminance)
			factor = getLuminanceFactor(color, factor);
		
		//Ajustamos el color para que brighter funcione mejor con colores muy oscuros o saturados
		Color adjustedColor = adjustBrighterColor(color, (float) factor);
		
		int r = adjustedColor.getRed();
		int g = adjustedColor.getGreen();
		int b = adjustedColor.getBlue();
		int alpha = color.getAlpha();
		
		//Los colores grises devuelven blanco con factores mayores de MAX_GRAY_FACTOR, lo cambiamos para que se asmeje a otros colores
		final double MAX_GRAY_FACTOR = 0.89;
		if (factor > MAX_GRAY_FACTOR-0.1 && r == g && r == b) {
			factor = Numbers.getNumberBetween(factor, MAX_GRAY_FACTOR-0.1, MAX_GRAY_FACTOR).doubleValue();
		}
	
		//A continuación hacemos lo mismo que hace el método Color.brighter(), pero con el factor ajustado
		
		/* From 2D group:
		 * 1. black.brighter() should return grey
		 * 2. applying brighter to blue will always return blue, brighter
		 * 3. non pure color (non zero rgb) will eventually return white
		 */

		int i = (int) (1.0 / factor);
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i);
		}
		if (r > 0 && r < i) r = i;
		if (g > 0 && g < i) g = i;
		if (b > 0 && b < i)	b = i;
	
		Color brighterCol = new Color(Math.min((int) (r / (1.0 - factor)), 255),
								Math.min((int) (g / (1.0 - factor)), 255),
								Math.min((int) (b / (1.0 - factor)), 255),
								alpha);
	
		return brighterCol;
	}
	
	/**
	 * Ajusta el color para su correcta transformación antes de pasarlo por brighter o darker
	 * @param color
	 * @param factor
	 * @return
	 */
	private static Color adjustBrighterColor(Color color, float factor) {
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		//Si el color es negro, lo cambiamos ligeramente ya que los valores de saturación y brillo son muy distintos de los casi negros
		if (color.equals(Color.black)) {
			r = b = g = 1;
		}
				
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		float hue = hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];
		
		float adjustedHue = hue;
		float adjustedSat = saturation;
		float adjustedBright = brightness;
		
		//Si el color es casi gris cambiamos el origen a la gama de grises
		int rgbAvg = (r+g+b)/3;
		final int MAX_DIF = 10;
		boolean almostGray = Math.abs(rgbAvg-r) < MAX_DIF && Math.abs(rgbAvg-g) < MAX_DIF && Math.abs(rgbAvg-b) < MAX_DIF;
		
		Color adjustedColor = color;
		//if (!almostGray || brightness < 0.35) {

			if (r != g && r != b && almostGray) {
				Color grayColor = getGrayScaleColor(color);
				r = g = b = grayColor.getRed();
			}
				
			float changeFactor = Math.min(0.3f, (float)factor);
			float satFactor = changeFactor;
			if (saturation > 0.5)
				adjustedSat = saturation - saturation * satFactor;
			else
				adjustedSat = saturation + (0.5f - saturation) * satFactor;
			
			if (brightness < 0.4) {
				float brightFactor = Math.min(0.4f, changeFactor*2);
				adjustedBright = brightness + (0.4f - brightness) * brightFactor;
			}
			
			adjustedColor = new Color(Color.HSBtoRGB(adjustedHue, adjustedSat, adjustedBright));
		//}
		
		if (almostGray && adjustedColor.getRed() != adjustedColor.getGreen() && adjustedColor.getRed() != adjustedColor.getBlue()) {
			//Si el origen es casi gris, cambiamos el color ajustado a la gama de grises para que no tenga tono rojizo
			adjustedColor = getGrayScaleColor(adjustedColor);
		}
		
		return adjustedColor;
	}
		
	public static Color getBrighterColor(Color color) {
		return brighter(color, FACTOR_DEFAULT);
	}
	
	public static Color degrade(Color color, int degradeScale) {
		
		int r = numNextTo(color.getRed());
		int g = numNextTo(color.getGreen());
		int b = numNextTo(color.getBlue());
		
		boolean semiPureColor = false;
		boolean graysColor = false;
		boolean pureColor = false;
		Color colorNext = new Color(r, g, b);
		
		if (r == g && r == b) {
			graysColor = true;
		} else
			if (r != g && r != b && g != b && r != 0 && g != 0 && b != 0) {
				semiPureColor = true;
			} else {
				pureColor = true;
				colorNext = getPureColorNextTo(colorNext);
			}
			
		r = colorNext.getRed();
		g = colorNext.getGreen();
		b = colorNext.getBlue();
		
		if (degradeScale == 0) {
			return colorNext;
		} 
		else if (graysColor) {
			switch (degradeScale) {
				case 1 :
					r = 0;
					g = 0;
					b = 0;
					break;
				case 2 :
					r = 96;
					g = 96;
					b = 96;
					break;
				case 3 :
					r = 128;
					g = 128;
					b = 128;
					break;
				case 4 :
					r = 192;
					g = 192;
					b = 192;
					break;
				case 5 :
					r = 216;
					g = 216;
					b = 216;
					break;
			}
		} 
		else if (pureColor) {
			switch (degradeScale) {
				case 1 :
					if (r == 255)
						r = 128;
					if (g == 255)
						g = 128;
					if (b == 255)
						b = 128;
					break;
				case 2 :
					if (r == 255)
						r = 192;
					if (g == 255)
						g = 192;
					if (b == 255)
						b = 192;
					break;
				case 3 :
					if (r == 0)
						r = 64;
					if (g == 0)
						g = 64;
					if (b == 0)
						b = 64;
					break;
				case 4 :
					if (r == 0)
						r = 128;
					if (g == 0)
						g = 128;
					if (b == 0)
						b = 128;
					break;
				case 5 :
					if (r == 0)
						r = 192;
					if (g == 0)
						g = 192;
					if (b == 0)
						b = 192;
					break;
			}
		} 
		else if (semiPureColor) {
			switch (degradeScale) {
				case 1 :
					if (r == 255)
						r = 224;
					else
						if (r == 192)
							r = 168;
						else
							if (r == 64)
								r = 56;
					if (g == 255)
						g = 224;
					else
						if (g == 192)
							g = 168;
						else
							if (g == 64)
								g = 56;
					if (b == 255)
						b = 224;
					else
						if (b == 192)
							b = 168;
						else
							if (b == 64)
								b = 56;
					break;
				case 2 :
					if (r == 255)
						r = 192;
					else
						if (r == 192)
							r = 144;
						else
							if (r == 64)
								r = 48;
					if (g == 255)
						g = 192;
					else
						if (g == 192)
							g = 144;
						else
							if (g == 64)
								g = 48;
					if (b == 255)
						b = 192;
					else
						if (b == 192)
							b = 144;
						else
							if (b == 64)
								b = 48;
					break;
				case 3 :
					if (r == 192)
						r = 208;
					else
						if (r == 64)
							r = 112;
					if (g == 192)
						g = 208;
					else
						if (g == 64)
							g = 112;
					if (b == 192)
						b = 208;
					else
						if (b == 64)
							b = 56;
					break;
				case 4 :
					if (r == 192)
						r = 228;
					else
						if (r == 64)
							r = 160;
					if (g == 192)
						g = 228;
					else
						if (g == 64)
							g = 160;
					if (b == 192)
						b = 228;
					else
						if (b == 64)
							b = 160;
					break;
				case 5 :
					if (r == 192)
						r = 240;
					else
						if (r == 64)
							r = 208;
					if (g == 192)
						g = 240;
					else
						if (g == 64)
							g = 208;
					if (b == 192)
						b = 240;
					else
						if (b == 64)
							b = 208;
					break;
			}
		}
		Color degradedColor = new Color(r,g,b);
		
		return degradedColor;
	}
	
	public static boolean isColorVeryLight(Color color) {
		return getLuminance(color) > 0.9;
		/*float brightness = Colors.getBrightness(color);
		if (brightness > 0.9)
			return true;
		else if (brightness > 0.8) {
			//Los colores verdaceos son mas claros que el resto y no tendremos en cuanta la saturación
			return color.getGreen() > 200 || Colors.getSaturation(color) < 0.2;
		}
		else {
			return false;
		}*/
	}
	/**
	 * <b>Descripción:</b><br>
	 * Este método nos dice si un color es oscuro o no basandose en el valor "brightness" 
	 * del color HSB coorespondiente al color RGB de entrada.
	 *
	 * Un color será oscuro cuando su brillo sea inferior al 70%
	 * 
	 * @return boolean
	 * @param color java.awt.Color
	 */
	public static boolean isColorDark(Color color) {
		return getLuminance(color) < 0.6;
		//Los colores verdaceos son mas claros que el resto y no tendremos en cuanta la saturación
		//if (color.getGreen() > 200)
			//return getBrightness(color) < 0.7;
		//else
			//return getBrillo(color) < 70 || getSaturacion(color) > 30;
	}
	
	public static float getBrightness(Color color) {
		
		try {
	
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			float[] hsbColor = Color.RGBtoHSB(r, g, b, null);
			
			return hsbColor[2];
			//El brillo estará comprendido entre 0 y 100 siendo 0 lo mas oscuro y 100 lo mas claro
			//int brillo = (int)(hsbColor[2] * 100 + 0.5);
			//return brillo;
		}
		catch (Throwable ex) {
			return 1f;
		}
	}
	
	public final static Color getColor(String colorCodificado) {
		return decode(colorCodificado);
	}
	/**
	 * Este método nos devuelve un literal del color que hemos pasado como argumento
	 */
	public static String convertColorToHex(Color color) {
		
		if (color != null) {
			
			try {
				
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				
				String redHex = Integer.toHexString(r);
				String greenHex = Integer.toHexString(g);
				String blueHex = Integer.toHexString(b);
				
				if (r < 16) {
					redHex = 0 + redHex;
				}
				if (g < 16) {
					greenHex = 0 + greenHex;
				}
				if (b < 16) {
					blueHex = 0 + blueHex;
				}
					
				return (redHex + greenHex + blueHex).toUpperCase();
				
			} catch (Throwable ex) {
				return COLOR_HEX_BLACK;	
			}
		}
		
		return COLOR_HEX_BLACK;
	}
	
	public static String convertColorToAnsi(Color color) {
		return convertColorToAnsi(color, false, false);
	}
	public static String convertColorToAnsi(Color color, boolean isBackground, boolean isAnsi256) {
		
		if (color != null) {
			
			final String ESCAPE_CODE = "\033";
			final String COMMAND = isBackground ? "[48" : "[38";
			final String FORMAT_8_BIT = ";5";
			final String FORMAT_24_BIT = ";2";
			
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			
			final String colorAnsi;
			if (isAnsi256) {
				int ansi256 = convertRGBToAnsi256(r, g, b);
				colorAnsi = ESCAPE_CODE+COMMAND+FORMAT_8_BIT+";"+ansi256+"m";
			}
			else {
				colorAnsi = ESCAPE_CODE+COMMAND+FORMAT_24_BIT+";"+r+";"+g+";"+b+"m";
			}
			
			return colorAnsi;
		}
		return COLOR_ANSI_RESET;
	}
	
	public static int convertRGBToAnsi256(int r, int g, int b) {
		
		int ansi256;
		if (r == g && g == b) {
	        if (r < 8) {
	            return 16;
	        }
	
	        if (r > 248) {
	            return 231;
	        }
	
	        ansi256 = Math.round(((r - 8) / 247) * 24) + 232;
	    }
		else {
			ansi256 = 16
			        + (36 * Math.round(r / 255 * 5))
			        + (6 * Math.round(g / 255 * 5))
			        + Math.round(b / 255 * 5);
		}
		return ansi256;
	}
	
	public static float getHue(Color color) {
		
		try {
	
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			float[] hsbColor = Color.RGBtoHSB(r, g, b, null);
			
			return hsbColor[0];
			//Devolvemos un valor entre 0 y 255
			//int hue = (int)(hsbColor[0] * 255 + 0.5);
	
			//return hue;
		}
		catch (Throwable ex) {
			return 1f;
		}
	}
	
	public static int getHueInt(Color color) {
		return (int)(getHue(color) * 255 + 0.5);
	}
	
	public static float getSaturation(Color color) {
		
		try {
	
			//La saturacion estará comprendida entre 0 y 100 siendo 0 gris y 100 el color puro
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			float[] hsbColor = Color.RGBtoHSB(r, g, b, null);
			
			return hsbColor[1];
			//int brillo = (int)(hsbColor[1] * 100 + 0.5);
			//return brillo;
		}
		catch (Throwable ex) {
			return 1f;
		}
	}
	/**
	  * Convert a "#FFFFFF" hex string to a Color.
	  * If the color specification is bad, an attempt
	  * will be made to fix it up.
	  */
	public static final Color convertHexToColor(String value) {
		
		if (value.startsWith(Constants.HASH)) {
			String digits = value.substring(1, Math.min(value.length(), 7));
			String hstr = "0x" + digits;
			Color c = Color.decode(hstr);
			return c;
		}
		return Color.white;
	}
	
	private static int numNextTo(int n) {
		
		int incremento = 0;
		if ((n/64.0-n/64) > 0.5)
			incremento = 64;
		int numero = (n/64)*64+incremento;
		if (numero == 128)
			numero = 64;
		if (numero > 255)
			numero = 255;
		
		return numero;
	}
	
	public static Color optimizeColor(Color color, Color background) {
	
		try {
	
			if (isColorDark(background)) {
				if (isColorDark(color)) {
					//Si el fondo y la fuente son oscuros, ponemos la fuente mas clara
					color = getFirstBrightColor(color);
				}
			}
			else {
				if (!isColorDark(color)) {
					//Si el fondo y la fuente son claros, ponemos la fuente mas oscura
					color = getFirstDarkColor(color);
				}
			}
			return color;
		}
		catch (Throwable ex) {
			Console.printException(ex);
			return color;
		}
	}
	
	public static Color getFirstBrightColor(Color color) {
		
		if (color != null && isColorDark(color)) {
			Color firstBrightColor = Colors.brighter(color, 0.1);
			int n = 10;
			while (Colors.isColorDark(firstBrightColor) && n > 0) {
				n--;
				firstBrightColor = Colors.brighter(firstBrightColor, 0.1);
			}
			return firstBrightColor;
		}
		return color;
	}
	
	public static Color getFirstDarkColor(Color color) {
		
		if (color != null && !isColorDark(color)) {
			Color firstDarkColor = Colors.darker(color, 0.1);
			int n = 10;
			while (!Colors.isColorDark(firstDarkColor) && n > 0) {
				n--;
				firstDarkColor = Colors.darker(firstDarkColor, 0.1);
			}
			return firstDarkColor;
		}
		return color;
	}
	
	public static Color getColorAlpha(Color colorBase, int transparency) {
		
		int alpha = 255 - transparency * 255 / 100;
		Color colorAlpha = new Color(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), alpha);
		
		return colorAlpha;
	}
	
	public static String encode(Color color) {
		if (color != null) {
			if (color instanceof StateColor) {
				return ((StateColor) color).encode();
			}
			else if (color instanceof ReferencedColor) {
				return ((ReferencedColor) color).encode();
			}
			else {
				StringBuffer sb = new StringBuffer();
				sb.append(color.getRed());
				sb.append(Constants.COMMA);
				sb.append(color.getGreen());
				sb.append(Constants.COMMA);
				sb.append(color.getBlue());
				return sb.toString();
			}
		}
		else return encode(Color.white); 
	}
	
	public static Color decode(String colorText) {
		try {
			if (colorText.startsWith(StateColor.class.getSimpleName()) || colorText.startsWith(StateColor.class.getName())) {
				return StateColor.decode(colorText);
			}
			else if (colorText.startsWith(ReferencedColor.class.getSimpleName()) || colorText.startsWith(ReferencedColor.class.getName())) {
				return ReferencedColor.decode(colorText);
			}
			else {
				String[] rgb = Strings.split(colorText, Constants.COMMA);
				if (rgb.length == 3) {
					String posibleRed = rgb[0].trim();
					String posibleGreen = rgb[1].trim();
					String posibleBlue = rgb[2].trim();
					int r,g,b;
					if (Numbers.isIntegerNumber(posibleRed)) {
						r = Integer.parseInt(posibleRed);
						g = Integer.parseInt(posibleGreen);
						b = Integer.parseInt(posibleBlue);
					}
					else {
						r = Integer.parseInt(posibleRed.substring(posibleRed.indexOf(Constants.EQUAL)+1));
						g = Integer.parseInt(posibleGreen.substring(posibleGreen.indexOf(Constants.EQUAL)+1));
						b = Integer.parseInt(posibleBlue.substring(posibleBlue.indexOf(Constants.EQUAL)+1, posibleBlue.length()-1));
					}
					return new Color(r,g,b);
				}
				else if (colorText.startsWith(Constants.HASH)) {
					return convertHexToColor(colorText);
				}
				else if (Numbers.isIntegerNumber(colorText)) {
					return Color.decode(colorText);
				}
			}
		}
		catch (Exception e) {
		}
		return Color.white;
	}
	
	/**
	 *	Devuelve la luminosidad de un color percibida por el ojo humano
	 */
	public static float getLuminance(Color color) {
		return getLuminance(color, true);
	}
	/**
	 *	Devuelve la luminosidad de un color
	 * Podemos especificar si queremos obtener la luminosidad exacta o la percibida por el ojo humano
	 */
	public static float getLuminance(Color color, boolean perceivedByHumanEye) {
		
		try {
			
			int r = color.getRed();
			int g = color.getGreen();
			int b = color.getBlue();
			
			//Valor entre 0 y 255
			//float luminance = perceived ? 0.299f*r + 0.587f*g + 0.114f*b : 0.2126f*r + 0.7152f*g + 0.0722f*b;
			float luminance = perceivedByHumanEye ? 0.2126f*r + 0.7152f*g + 0.0722f*b : 0.299f*r + 0.587f*g + 0.114f*b;
			
			return luminance/255;
		}
		catch (Throwable ex) {
			return 1f;
		}
	}
	
	public static Color getGrayScaleColor(Color sourceColor) {

		int gray = (int) ((sourceColor.getRed() * 0.299) + (sourceColor.getGreen() * 0.587) + (sourceColor.getBlue() * 0.114));

		//if (gray < 0) gray = 0;
		//if (gray > 255)	gray = 255;

		Color grayColor = new Color(gray, gray, gray, sourceColor.getAlpha());
		
		return grayColor;
    }
	
	public static Color colorize(Color sourceColor, Color color) {
		return colorize(sourceColor, color, true);
	}
	public static Color colorize(Color sourceColor, Color color, boolean grayScaleFirst) {
		
		if (grayScaleFirst)
			sourceColor = getGrayScaleColor(sourceColor);
		
		int red = Math.max(0, Math.min(0xff, sourceColor.getRed() + color.getRed()));
		int green = Math.max(0, Math.min(0xff, sourceColor.getGreen() + color.getGreen()));
		int blue = Math.max(0, Math.min(0xff, sourceColor.getBlue() + color.getBlue()));

		Color colorizedColor = new Color(red, green, blue, sourceColor.getAlpha());
		
		return colorizedColor;
    }
	
	public static String getColorProperties(Color color) {
		
		String rgb = encode(color);
		String hexColor = convertColorToHex(color);
		float hue = getHue(color);
		float brightness = getBrightness(color);
		float saturation = getSaturation(color);
		float luminance = getLuminance(color, false);
		float luminancePercieved = getLuminance(color);
		
		StringBuffer sb = new StringBuffer();
		sb.append(rgb);
		sb.append(" "+hexColor);
		sb.append(" H:"+hue);
		sb.append(" B:"+brightness);
		sb.append(" S:"+saturation);
		sb.append(" L:"+luminance);
		sb.append(" LP:"+luminancePercieved);
		
		return sb.toString();
	}
}
