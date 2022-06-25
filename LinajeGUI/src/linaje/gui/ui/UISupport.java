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
package linaje.gui.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.text.JTextComponent;

import sun.swing.SwingUtilities2;
import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LLabel;
import linaje.gui.PaintedImageIcon;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.LFont;
import linaje.utils.Lists;
import linaje.utils.ReferencedColor;
import linaje.utils.StateColor;
import linaje.utils.Strings;

public class UISupport {

	private static final List<Class<?>> COMPONENT_UIS = Lists.iteratorToList(LinajeLookAndFeel.UI_LCOMPONENTS_OTHER_MAP.keySet().iterator());
	
	private static HashMap<Class<?>, ComponentUIProperties> defaultComponentUIPropertiesMap = null;
	
	//
	// Maping default UI properties
	//
	
	public static HashMap<Class<?>, ComponentUIProperties> getDefaultComponentUIPropertiesMap() {
		if (defaultComponentUIPropertiesMap == null) {
			defaultComponentUIPropertiesMap = new LinkedHashMap<Class<?>, ComponentUIProperties>();
			for (int i = 0; i < COMPONENT_UIS.size(); i++) {
				Class<?> uiClass = COMPONENT_UIS.get(i);
				ComponentUIProperties componentUIProperties = new ComponentUIProperties(uiClass);
				defaultComponentUIPropertiesMap.put(uiClass, componentUIProperties);
			}
		}
		return defaultComponentUIPropertiesMap;
	}
	
	public static void uninstallComponentUIs() {
		UISupport.getDefaultComponentUIPropertiesMap().clear();
		defaultComponentUIPropertiesMap = null;
	}
	
	public static GeneralUIProperties getGeneralUIProperties() {
		return LinajeLookAndFeel.getInstance().getGeneralUIProperties();
	}
	
	//
	// Get UI property prefix and names
	//
	
	public static String getPropertyPrefix(Class<?> componentUIClass) {
		 return LinajeLookAndFeel.getUIName(componentUIClass) + Constants.POINT;
    }
	
	public static String getPropertyKey(Class<?> componentUIClass, String propertyName) {
        return getPropertyPrefix(componentUIClass) + propertyName;
    }
	
	//
	// Get default UI properties
	//
	
	public static ComponentUIProperties getDefaultComponentUIProperties(Class<?> componentUIClass) {
		ComponentUIProperties defaultComponentUIProperties = getDefaultComponentUIPropertiesMap().get(componentUIClass);
		if (defaultComponentUIProperties == null)
			defaultComponentUIProperties = UISupportButtons.getDefaultButtonUIProperties(componentUIClass);
		
		return defaultComponentUIProperties;
	}
	
	//
	// Init Components UI defaults 
	//
	
	public static void initComponentsUIDefaults(UIDefaults table, String[] encodedFields) {
		
		UISupportButtons.initButtonsUIDefaults(table, encodedFields);
		
		for (int i = 0; i < COMPONENT_UIS.size(); i++) {
			ComponentUIProperties componentUIProperties = getDefaultComponentUIProperties(COMPONENT_UIS.get(i));
			componentUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
			componentUIProperties.initComponentDefaults(table);
		}
	}
	
	//Save fields
	protected static void encodeUIsFieldsChanged(StringBuffer sb) {
		
		GeneralUIProperties.getInstance().encodeFieldsChanged(sb);
		
		UISupportButtons.encodeUIsFieldsChanged(sb);
		
		HashMap<Class<?>, ComponentUIProperties> uisMap = getDefaultComponentUIPropertiesMap();
		for (Iterator<ComponentUIProperties> iterator = uisMap.values().iterator(); iterator.hasNext();) {
			ComponentUIProperties uiProp = iterator.next();
			uiProp.encodeFieldsChanged(sb);
		}
	}
	
	//Load fields
	public static void updateUIsPropertiesFromEncodedFields(String... encodedFields) {
		
		GeneralUIProperties.getInstance().getFieldsChangeSupport().resetDefaultFieldsValues();
		GeneralUIProperties.getInstance().updateUIPropertiesFromEncodedFields(encodedFields);
		
		UISupportButtons.updateUIsPropertiesFromEncodedFields(encodedFields);
		
		for (int i = 0; i < COMPONENT_UIS.size(); i++) {
			ComponentUIProperties componentUIProperties = getDefaultComponentUIProperties(COMPONENT_UIS.get(i));
			componentUIProperties.getFieldsChangeSupport().resetDefaultFieldsValues();
			componentUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
		}
	}
	
	public static void updateUIsPropertiesFromEncodedFields(List<ComponentUIProperties> componentUIs, String... encodedFields) {
		
		GeneralUIProperties.getInstance().getFieldsChangeSupport().resetDefaultFieldsValues();
		GeneralUIProperties.getInstance().updateUIPropertiesFromEncodedFields(encodedFields);
		
		for (int i = 0; i < componentUIs.size(); i++) {
			ComponentUIProperties componentUIProperties = componentUIs.get(i);
			componentUIProperties.getFieldsChangeSupport().resetDefaultFieldsValues();
			componentUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
		}
	}
	
	public static Insets getInsets(JComponent c) {
		
		Insets insets = c.getInsets();
		
		Insets margin = null;
		if (c instanceof AbstractButton) {
			AbstractButton boton = (AbstractButton) c;
			margin = boton.getMargin();
		}
		else if (c instanceof JTextComponent) {
			JTextComponent textComponent = (JTextComponent) c;
			margin = textComponent.getMargin();
		}
		else if (c instanceof LLabel) {
			LLabel lLabel = (LLabel) c;
			margin = lLabel.getMargin();
		}
		
		//Si cambiamos el borde del componente no funcionará el margin, por lo que lo añadimos aquí
		if (margin != null && !UISupportUtils.isMarginCompatibleBorder(c.getBorder())) {
			insets.left += margin.left;
			insets.right += margin.right;
			insets.top += margin.top;
			insets.bottom += margin.bottom;
		}
		
		return insets;
	}
	//
	// Get preferredSize
	//
	
	@SuppressWarnings("unused")
	public static Dimension getPreferredSize(JComponent c, String text, Icon icon, int iconTextGap, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition, boolean ignoreIconHeight, boolean respectMaxMinSize) {
		
		if (text == null)
			text = Constants.VOID;
			
		Insets insets = c.getInsets();
        if (icon == null && text == null) {
            return new Dimension(insets.left + insets.right, insets.bottom + insets.top);
        }

        Dimension preferredSize = new Dimension();
       // Rectangle globalTextRect = new Rectangle();
        Rectangle iconRect = new Rectangle();
        layoutTextIcon(c, null, null, null, iconRect, preferredSize, null, text, icon, iconTextGap, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition);
		
        /*Rectangle fixedIconRect = new Rectangle(iconRect);
        if (ignoreIconHeight)
        	iconRect.height = font.getSize();
		
        Dimension preferredSize = globalTextRect.union(fixedIconRect).getSize();
        */
      	preferredSize.width += insets.left + insets.right;
      	preferredSize.height += insets.bottom + insets.top;
		
      	Insets margin = null;
		if (c instanceof AbstractButton) {
			AbstractButton boton = (AbstractButton) c;
			margin = boton.getMargin();
		}
		else if (c instanceof JTextComponent) {
			JTextComponent textComponent = (JTextComponent) c;
			margin = textComponent.getMargin();
		}
		else if (c instanceof LLabel) {
			LLabel lLabel = (LLabel) c;
			margin = lLabel.getMargin();
		}
		
		//Si cambiamos el borde del componente no funcionará el margin, por lo que lo añadimos aquí
		if (margin != null && !UISupportUtils.isMarginCompatibleBorder(c.getBorder())) {
			preferredSize.width += margin.left + margin.right;
			preferredSize.height += margin.top + margin.bottom;
		}
		
		if (respectMaxMinSize) {
			
			//si respectMaxMinSize viene a true, getPreferredSize() no se podrá salir de los márgenes de getMinSize() y getMaxSize()
			Dimension minSize = c.isMinimumSizeSet() ? c.getMinimumSize() : new Dimension(0, 0);
			Dimension maxSize = c.isMaximumSizeSet() ? c.getMaximumSize() : new Dimension(0, 0);
			
			if (minSize.width > 0 && preferredSize.width < minSize.width)
				preferredSize.width = minSize.width;
			else if (maxSize.width > 0 && preferredSize.width > maxSize.width)
				preferredSize.width = maxSize.width;
			
			if (minSize.height > 0 && preferredSize.height < minSize.height)
				preferredSize.height = minSize.height;
			else if (maxSize.height > 0 && preferredSize.height > maxSize.height)
				preferredSize.height = maxSize.height;
		}
		
		return preferredSize;
	}
	
	//
	// Painting components
	//
	
	public static void paintIcon(Graphics g, JComponent c, Rectangle iconRect, Icon icon) {
		paintIcon(g, c, iconRect, icon, 0, false, false, -1);
	}
	public static void paintIcon(Graphics g, JComponent c, Rectangle iconRect, Icon icon, int iconShiftOffset, boolean iconForegroundEnabled, boolean isShadowTextEnabled, int shadowPosition) {
		
		if (icon != null) {

			int x = iconRect.x + iconShiftOffset;
			int y = iconRect.y + iconShiftOffset;

			Color foreground = getForeground(c);
			Color background = getBackground(c);
			
			// NOTA: Estudiar la posibilidad de crear un StateIcon para no tener que calcular la imagen colorizada tantas veces
			Image originalImage = Icons.getImage(icon, c);
			if (c.isEnabled() && iconForegroundEnabled && icon instanceof ImageIcon) {

				//Si es un paintedIcon con color=null se pintará con el color del foreground en el propio paintIcon del icono
				if (!(icon instanceof PaintedImageIcon) || ((PaintedImageIcon) icon).getColor() != null) {
					
					getForeground(c);
	
					//Oscurecemos primero la imagen para que el color final sea mas fiel al deseado
					boolean obscureImageFirst = true;
					foreground = optimizeForegroundForColorize(foreground);
									
					Image colorizedImage = Icons.createColorizedImage(originalImage, foreground, obscureImageFirst);
					icon = new ImageIcon(colorizedImage);
				}
			}
			
			if (isShadowTextEnabled || !c.isEnabled()) {
				// Color shadowColor = Colors.darker(getBackground(c), 0.7);
				Color shadowColor = getShadowColor(foreground, background);
				if (!c.isEnabled()) {
					boolean backgroundIsDark = Colors.isColorDark(background);
					shadowColor = backgroundIsDark ? Colors.darker(background, 0.7) : Colors.brighter(background, 0.7);
					shadowPosition = SwingConstants.NORTH_WEST;
				}
				shadowColor = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 255);
				Image colorizedShadowImage = Icons.createColorizedImage(originalImage, shadowColor, true);
				Icon shadowIcon = new ImageIcon(colorizedShadowImage);
				Point shadowLocation = getShadowLocation(new Point(x, y), shadowPosition);
				shadowIcon.paintIcon(c, g, shadowLocation.x, shadowLocation.y);
			}

			icon.paintIcon(c, g, x, y);
		}
	}
	
	public static Color optimizeForegroundForColorize(Color foreground) {
		Color optimizedForeground = foreground;
		float luminance = Colors.getLuminance(foreground);
		// Los iconos con colores demasiado claros no se ven bien. Los ponemos algo mas oscuros
		float lightLuminance = 0.8f;
		//Si AppGUI.getCurrentAppGUI().isOptimizeNonPlainImagesColorize() == true no dejamos poner colores muy claros como el blanco, porque sino no veríamos los contrastes
		//En caso contrario, con el modo colores planos, si que colorizamos a blanco o similares
		if (luminance > lightLuminance && (luminance < 0.98f || AppGUI.getCurrentAppGUI().isOptimizeNonPlainImagesColorize()))
			optimizedForeground = Colors.darker(foreground, luminance - lightLuminance);
		
		return optimizedForeground;
	}
	
	public static Color getBackground(JComponent c) {
		return getBackground(c, null);
	}
	public static Color getBackground(JComponent c, ButtonModel buttonModel) {
		
		Color bgColor = c.isOpaque() ? c.getBackground() : null;
		if (bgColor == null) {
			bgColor = ColorsGUI.getFirstOpaqueParentBackground(c);
			if (bgColor == null)
				bgColor = c.getBackground();
		}
		
		//Cogemos el primer color que no sea un ReferencedColor
		int refs = 4;
		while (bgColor != null && bgColor instanceof ReferencedColor && refs > 0) {
			bgColor = ((ReferencedColor) bgColor).getColor();
			refs--; 
		}
		
		Color background;
		if (c instanceof AbstractButton) {
			background = UISupportButtons.getBackground((AbstractButton) c, bgColor);
		}
		else {
			background = getStateColorValue(buttonModel, bgColor);
		}
		
		return background;
	}

	public static Color getForeground(JComponent c) {
		return getForeground(c, null);
	}
	public static Color getForeground(JComponent c, ButtonModel buttonModel) {
		
		Color fgColor = c.getForeground();
		
		//Cogemos el primer color que no sea un ReferencedColor
		int refs = 4;
		while (fgColor != null && fgColor instanceof ReferencedColor && refs > 0) {
			fgColor = ((ReferencedColor) fgColor).getColor();
			refs--; 
		}
		
		Color foreground;
		if (c instanceof AbstractButton) {
			foreground = UISupportButtons.getForeground((AbstractButton) c, fgColor);
		}
		else {
			foreground = getStateColorValue(buttonModel, fgColor);
		}
		
		return foreground;
	}
	
	public static Color getDisabledForeground(JComponent c) {
		
		Color foreground = getForeground(c);
		Color background = getBackground(c);
		
		return getDisabledForeground(c.getForeground(), foreground, background);
	}
	
	public static Color getDisabledForeground(Color componentForeground, Color calculatedForeground, Color calculatedBackground) {
		
		if (calculatedForeground != null && calculatedForeground.equals(componentForeground)) {
			boolean backgroundIsDark = Colors.isColorDark(calculatedBackground);
     		//No se ha definido un disabledBackground, así que lo calculamos
     		return backgroundIsDark ? Colors.brighter(calculatedBackground, 0.32) : Colors.darker(calculatedBackground, 0.32);
     	}
		else {
			return calculatedForeground;
		}
	}
	
	public static Color getStateColorValue(ButtonModel buttonModel, Color buttonColor) {
		
		Color color = buttonColor;
		
		if (buttonModel != null && buttonColor != null && buttonColor instanceof StateColor) {
			
			StateColor stateColor = (StateColor) buttonColor;
			color = ColorsGUI.getStateValue(stateColor, buttonModel);
		}
		return color;
	}

	public static Color getShadowColor(Color foreground, Color background) {
		
		//Sombra oscura para foregrounds claros
		Color shadowColor = Colors.darker(background, 0.7);
		
		float foregroundLuminance = Colors.getLuminance(foreground);
		float backgroundLuminance = Colors.getLuminance(background);
		if (foregroundLuminance < 0.5) {
			//La sobra oscura se puede superponer con el background o con el foreground, por lo que cambiamos el color de la sombra
			if (Math.abs(foregroundLuminance - backgroundLuminance) < 0.25)
				//foreground y background son oscuros por lo que la sombra será el background un poco aclarado
				shadowColor = Colors.brighter(background, 0.3);
			else
				//foreground es oscuro y background claro, por lo que la sombra será el background un poco oscurecido
				shadowColor = Colors.darker(background, 0.3);
		}
		else if (backgroundLuminance < 0.2) {
			//La sobra oscura se superpone con el background tambien oscuro por lo que cambiamos el color de la sombra
			shadowColor = Colors.brighter(background, 0.3);
		}
		
		return shadowColor;
	}
	
	public static Point getShadowLocation(Point origLocation, int shadowPosition) {
		int x = origLocation.x;
		int y = origLocation.y;
		switch (shadowPosition) {
			case SwingConstants.SOUTH:
				y++;
				break;
			case SwingConstants.EAST:
				x++;
				break;
			case SwingConstants.WEST:
				x--;
				break;
			case SwingConstants.NORTH:
				y--;
				break;
			case SwingConstants.SOUTH_EAST:
				x++;
				y++;
				break;
			case SwingConstants.SOUTH_WEST:
				x--;
				y++;
				break;
			case SwingConstants.NORTH_EAST:
				x++;
				y--;
				break;
			case SwingConstants.NORTH_WEST:
				x--;
				y--;
				break;	
			default:
				y++;
				break;
		}
		
		return new Point(x, y);
	}

	/**
	 * Pinta el texto alineado con verticalAlignment y horizontalAlignment respecto a container 
	 **/
	public static void paintText(Graphics g, String text, JComponent container, int verticalAlignment, int horizontalAlignment) {
		Insets insetsExtra = new Insets(0, 0, 0, 0);
		paintText(g, text, container, verticalAlignment, horizontalAlignment, insetsExtra);
	}
	/**
	 * Pinta el texto alineado con verticalAlignment y horizontalAlignment respecto a container
	 * Podemos además ajustar un poco mas la posición del texto con insetsExtra
	 **/
	public static void paintText(Graphics g, String text, JComponent container, int verticalAlignment, int horizontalAlignment, Insets insetsExtra) {
		
		boolean enabled = true;
		boolean isShadowTextEnabled = false;
		Color underlineColor = null;
		
		paintText(g, text, container, verticalAlignment, horizontalAlignment, insetsExtra, enabled, isShadowTextEnabled, underlineColor);
	}
	/**
	 * Pinta el texto alineado con verticalAlignment y horizontalAlignment respecto a container
	 * Podemos además ajustar un poco mas la posición del texto con insetsExtra
	 * Podemos además deshabilitar el texto o sombrearlo o subrallarlo
	 **/
	public static void paintText(Graphics g, String text, JComponent container, int verticalAlignment, int horizontalAlignment, Insets insetsExtra, boolean enabled, boolean isShadowTextEnabled, Color underlineColor) {

		int textShiftOffset = 0;
		boolean underline = underlineColor != null;
		int shadowPosition = isShadowTextEnabled ? SwingConstants.SOUTH_EAST : -1;
		
		Font font = g.getFont();
		FontMetrics fm = g.getFontMetrics(font);
		Color foreground = g.getColor();
		Color background = getBackground(container);

		Insets insets = (Insets) container.getInsets().clone();
		if (insetsExtra != null) {
			insets.top += insetsExtra.top;
			insets.left += insetsExtra.left;
			insets.bottom += insetsExtra.bottom;
			insets.right += insetsExtra.right;
		}

		Rectangle viewRect = container.getBounds();
		viewRect.x = insets.left;
		viewRect.y = insets.top;
		viewRect.width -= (insets.left + insets.right);
		viewRect.height -= (insets.top + insets.bottom);

		Rectangle textViewRect = new Rectangle();
		List<Rectangle> textLinesBounds = Lists.newList();
		List<Point> offsets = Lists.newList();

		UISupport.layoutText(viewRect, textViewRect, null, textLinesBounds, null, offsets, text, fm, verticalAlignment,	horizontalAlignment);

		// Escribimos el texto en la imagen
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setFont(fm.getFont());
		g2d.setColor(foreground);

		UISupport.paintText(g2d, foreground, background, textViewRect, textLinesBounds, offsets, text, enabled,	textShiftOffset, isShadowTextEnabled, shadowPosition, underline, underlineColor);

		g2d.dispose();
	}
	
	/**
	 * Pinta el texto en la posición x, y
	 **/
	public static void paintText(Graphics g, String text, int x, int y) {
		
		boolean enabled = true;
		boolean isShadowTextEnabled = false;
		Color underlineColor = null;
        
		paintText(g, text, x, y, enabled, isShadowTextEnabled, underlineColor);
	}
	/**
	 * Pinta el texto en la posición x, y
	 * Podemos además deshabilitar el texto o sombrearlo o subrallarlo
	 **/
	public static void paintText(Graphics g, String text, int x, int y, boolean enabled, boolean isShadowTextEnabled, Color underlineColor) {
		
		Rectangle textViewRect = new Rectangle();
		List<Rectangle> textLinesBounds = Lists.newList();
		List<Point> offsets = Lists.newList();
		
		FontMetrics fm = g.getFontMetrics();
		
		layoutText(null, textViewRect, null, textLinesBounds, null, offsets, text, fm, SwingConstants.TOP, SwingConstants.LEFT);
		
		textViewRect.x += x;
		textViewRect.y += y;
		for (int i = 0; i < textLinesBounds.size(); i++) {
			textLinesBounds.get(i).x += x;
			textLinesBounds.get(i).y += y;
		}
		
		int textShiftOffset = 0;
        int mnemonicIndex = -1;//Está pendiente de implementar
        int shadowPosition = isShadowTextEnabled ? SwingConstants.SOUTH_EAST : -1;
        boolean underline = underlineColor != null;
        Color foreground = null;
        Color background = null;
        
        UISupport.paintText(g, foreground, background, textViewRect, textLinesBounds, offsets, text, enabled, textShiftOffset, isShadowTextEnabled, shadowPosition, underline, underlineColor);
	}
	
	public static void paintText(Graphics g, JComponent c, Rectangle textViewRect, List<Rectangle> textLinesBounds, List<Point> offsets, String text, boolean enabled, int textShiftOffset, boolean isShadowTextEnabled, int shadowPosition, boolean underline, Color underlineColor) {
		
		g.setColor(c.getForeground());
		g.setFont(c.getFont());
	    Color background = getBackground(c);
	    Color foreground = getForeground(c);
	    
	    /*boolean optimizeForeground = true;
	    if (optimizeForeground) {
		    boolean backgroundIsDark = Colors.esColorOscuro(background);
	    	boolean foregroundIsDark = Colors.esColorOscuro(foreground);
	    	if (backgroundIsDark == foregroundIsDark)
	    		foreground = Colors.optimizarColor(foreground, background);
	    }*/
       
	    paintText(g, foreground, background, textViewRect, textLinesBounds, offsets, text, enabled, textShiftOffset, isShadowTextEnabled, shadowPosition, underline, underlineColor);
	}
	
	public static void paintText(Graphics g, Color foreground, Color background, Rectangle textViewRect, List<Rectangle> textLinesBounds, List<Point> offsets, String text, boolean enabled, int textShiftOffset, boolean isShadowTextEnabled, int shadowPosition, boolean underline, Color underlineColor) {
				
		Point textOffsets = new Point();
	    Color shadowColor = null;
	    if (foreground == null)
	    	foreground = g.getColor();
	    if (background == null)
	    	background = ColorsGUI.getColorPanels();
	    
	    /* Draw the Text */
	    if(enabled) {
	    	
	    	textOffsets.x += textShiftOffset;
	    	textOffsets.y += textShiftOffset;
	    	
	    	if (isShadowTextEnabled) {
	        	/*** paint the text shadow ***/
	    		//shadowColor = Colors.darker(background, 0.7);
	    		shadowColor = getShadowColor(foreground, background);
	        }
	    }
	    else {
	        /*** paint the text disabled shadow ***/
	    	boolean backgroundIsDark = Colors.isColorDark(background);
	    	shadowColor = backgroundIsDark ? Colors.darker(background, 0.7) : Colors.brighter(background, 0.7);
	        shadowPosition = SwingConstants.NORTH_WEST;
	        
	        foreground = getDisabledForeground(g.getColor(), foreground, background);
	        //if (foreground != null && foreground.equals(c.getForeground())) {
	        //if (foreground != null && foreground.equals(g.getColor())) {
	       		 //No se ha definido un disabledBackground, así que lo calculamos
	       	//	 foreground = backgroundIsDark ? Colors.brighter(background, 0.32) : Colors.darker(background, 0.32);
	       	//}
	    }
	    
	    if (shadowColor != null && shadowPosition != -1) {
	    	/*** paint the text shadow ***/
	    	 g.setColor(shadowColor);
	    	 Point shadowOffsets = getShadowLocation(textOffsets, shadowPosition);
	    	 drawText(g, text, textViewRect, textLinesBounds, shadowColor, offsets, shadowOffsets, underline ? shadowColor : null);
	    }
	            	
	    if (underline) {
	    	if (underlineColor == null || !enabled)
	    		underlineColor = foreground;
	  	}
	    else {
	    	underlineColor = null;
	    }
	    
	    /*** paint the text normally */
	    g.setColor(foreground);
	    
	    drawText(g, text, textViewRect, textLinesBounds, foreground, offsets, textOffsets, underlineColor);
	}

	public static void drawStringLine(Graphics g, String lineText, int viewWidth, Rectangle lineTextBounds, Color foreground, Point layoutOffsets, Point customOffsets, Color underlineTextColor) {
		
		if (foreground == null)
			foreground = g.getColor();
		
		Font font = g.getFont();
		FontMetrics fm = g.getFontMetrics(font);
		
		if (customOffsets == null)
			customOffsets = new Point();
		
		int xText = layoutOffsets.x;
		int yText = layoutOffsets.y + fm.getAscent();
	
		int fadeWidth = viewWidth < lineTextBounds.width ? font.getSize() : 0;
		
		int x = lineTextBounds.x + customOffsets.x;
		int y = lineTextBounds.y + customOffsets.y;
		int w = viewWidth - fadeWidth;
		int h = lineTextBounds.height;
		
		if (underlineTextColor != null) {
			
			g.setColor(underlineTextColor);
			int width = lineTextBounds.width;
			if (viewWidth < lineTextBounds.width)
				width = viewWidth;
			
			//Ignoramos los blancos que haya al inicio
			int numBlancos = 0;
			StringBuffer blancosInicio = new StringBuffer();
			while (lineText.length() > numBlancos && lineText.charAt(numBlancos) == ' ') {
				blancosInicio.append(Constants.SPACE);
				numBlancos++;
			}
	
			int anchoBlancosInicio = fm.stringWidth(blancosInicio.toString());
	
			//Pintamos la linea de subrrallado
			int baseline = y + fm.getAscent() + layoutOffsets.y;
			int distanceToBaseline = Math.round(font.getSize()/8f);
			
			int xUnderLine = x + anchoBlancosInicio;
			int yUnderLine = baseline + distanceToBaseline;
			int wUnderLine = width - anchoBlancosInicio;
			int hUnderLine = Math.max(1, Math.round(font.getSize()/40f));
			
			g.fillRect(xUnderLine, yUnderLine, wUnderLine, hUnderLine);
		}
		
		Graphics2D g2d = (Graphics2D) g.create(x, y, w, h);
		g2d.setColor(foreground);
		
		Object rhAntialiasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
		//Object rhAntialiasing = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		//Object rhAntialiasing = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, rhAntialiasing);
		
		g2d.drawString(lineText, xText, yText);		
		g2d.dispose();
		
		if (fadeWidth > 0) {
			
			x += viewWidth - fadeWidth;
			w = fadeWidth;
			
			Graphics2D g2dFade = (Graphics2D) g.create(x, y, w, h);
			Rectangle gradientRect = new Rectangle(0, 0, w, h);
			Paint paint = GraphicsUtils.getGradientPaint(gradientRect, Colors.getColorAlpha(foreground, 30), Colors.getColorAlpha(foreground, 100), GraphicsUtils.GRADIENT_TYPE_HORIZONTAL, false, false);
			g2dFade.setPaint(paint);
			g2dFade.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, rhAntialiasing);
			g2dFade.drawString(lineText, xText-viewWidth+fadeWidth, yText);
			g2dFade.dispose();
		}
	}

	@Deprecated
	public static void drawStringLineOld(Graphics g, String lineText, int viewWidth, Rectangle lineTextBounds, Color foreground, Color background, Point layoutOffsets, Point customOffsets, Color underlineTextColor) {
		
		/*
		 * EL PINTADO DEGRADADO DEL TEXTO COMPLETO NO RESPETA EL ANTIALIASING VALUE_TEXT_ANTIALIAS_LCD_HRGB, POR LO QUE EL TEXTO EN ALGUNAS FUENTES QUEDA FEO
		 * PARA SOLUCIONARLO SE HA CREADO OTRO MÉTODO drawStringLine QUE LO PINTA EN DOS PARTES Y ASÍ NO SE DISTORSIONE LA PARTE DEL TEXTO SIN DEGRADAR
		 * DEJAMOS ESTE MÉTODO PARA PRUEBAS
		 * */
		
		//Pintamos sólo dentro del viewRect para que no pintemos texto fuera de los márgenes del componente
		//boolean drawUnderTextBounds = true;//drawUnderTextBounds = true -> Si queremos que se pinte en el margen inferior en caso de que el espacio vertical sea menor que lo que ocupa el texto
		//Graphics2D g2d = (Graphics2D) g.create(lineTextViewRect.x, lineTextViewRect.y, lineTextViewRect.width, drawUnderTextBounds ? lineTextBounds.height : lineTextViewRect.height);
		Graphics2D g2d = (Graphics2D) g.create(lineTextBounds.x, lineTextBounds.y, viewWidth, lineTextBounds.height);
		
		try {
			if (foreground == null)
				foreground = g.getColor();
			if (background == null)
				background = Colors.brighter(foreground, 0.5);
			
			Font font = g.getFont();
			FontMetrics fm = g2d.getFontMetrics(font);
			
			//LineMetrics lineMetrics = font.getLineMetrics(lineText, fm.getFontRenderContext());
			//lineMetrics.getAscent();
			
			if (customOffsets == null)
				customOffsets = new Point();
			
			int xText = layoutOffsets.x + customOffsets.x;
			int yText = layoutOffsets.y + customOffsets.y + fm.getAscent();
		
			if (underlineTextColor != null) {
				
				g.setColor(underlineTextColor);
				int width = lineTextBounds.width;
				if (viewWidth < lineTextBounds.width)
					width = viewWidth;
				
				//Ignoramos los blancos que haya al inicio
				int numBlancos = 0;
				StringBuffer blancosInicio = new StringBuffer();
				while (lineText.length() > numBlancos && lineText.charAt(numBlancos) == ' ') {
					blancosInicio.append(" ");
					numBlancos++;
				}
		
				int anchoBlancosInicio = fm.stringWidth(blancosInicio.toString());
		
				//Pintamos la linea de subrrallado
				int baseline = lineTextBounds.y + fm.getAscent() + layoutOffsets.y + customOffsets.y;
				int distanceToBaseline = Math.round(font.getSize()/8f);
				g.fillRect(lineTextBounds.x + anchoBlancosInicio, baseline + distanceToBaseline, width - anchoBlancosInicio, 1);
			}
			
			g2d.setColor(foreground);
			
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
			//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
			//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			/*Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
			if (desktopHints != null) {
			   g2d.setRenderingHints(desktopHints);
			}*/
			//System.setProperty("awt.useSystemAAFontSettings", "on");
			//Consola.println("awt.useSystemAAFontSettings: "+System.getProperty("awt.useSystemAAFontSettings"));
				    
			if (viewWidth < lineTextBounds.width) {
				
				int fadeWidth = font.getSize()*2;
				int width = lineTextBounds.width;
				if (viewWidth < lineTextBounds.width)
					width = viewWidth;
				
				Rectangle gradientRect = new Rectangle(width - fadeWidth, 0, fadeWidth, lineTextBounds.height);
				//Paint paint = UtilsGUI.getGradientPaint(gradientRect, background, foreground, UtilsGUI.GRADIENT_TYPE_HORIZONTAL, false, true);
				Paint paint = GraphicsUtils.getGradientPaint(gradientRect, Colors.getColorAlpha(foreground, 100), foreground, GraphicsUtils.GRADIENT_TYPE_HORIZONTAL, false, true);
				g2d.setPaint(paint);
			}
			
			g2d.drawString(lineText, xText, yText);
		}
		finally {
			g2d.dispose();
		}
	}

	public static void drawText(Graphics g, String text, Rectangle textViewRect, List<Rectangle> textLinesBounds, Color foreground, List<Point> layoutOffsets, Point customOffsets, Color underlineTextColor) {
		
		String[] lines = Strings.getLines(text);
		for (int i = 0; i < lines.length; i++) {
			String lineText = lines[i];
			Point lOffsets = new Point(layoutOffsets.get(i));
			Rectangle lineTextBounds = textLinesBounds.get(i);
			drawStringLine(g, lineText, textViewRect.width, lineTextBounds, foreground, lOffsets, customOffsets, underlineTextColor);
		}
	}

	@Deprecated
	public static void layoutSigleLineTextIcon(Graphics g, Rectangle textViewRect, Rectangle textRect, Rectangle textBounds, Rectangle iconRect, Point offset, JComponent c, String text, Icon icon, int iconTextGap, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		if (c != null) {
			
			if (textViewRect == null)
				textViewRect = new Rectangle();
			if (textRect == null)
				textRect = new Rectangle();
			if (textBounds == null)
				textBounds = new Rectangle();
			if (iconRect == null)
				iconRect = new Rectangle();
			if (offset == null)
				offset = new Point();
			
			Graphics2D g2d = (Graphics2D) g;
			
			Insets insets = c.getInsets();
			Rectangle viewRect =  new Rectangle(insets.left, insets.top, c.getWidth() - insets.left - insets.right, c.getHeight() - insets.top - insets.bottom);
			
			if (icon != null) {
				iconRect.width = icon.getIconWidth();
				iconRect.height = icon.getIconHeight();
			}
			
			boolean iconVisible = iconRect.width > 0 && iconRect.height > 0;
			boolean textVisible = !text.equals(Constants.VOID);
			
			int verticalOffset = 0;
			int horizOffset = 0;
			Rectangle stringBounds = null;
			Point initialTextLocation = null;
			
			if (textVisible) {
				
				textViewRect.x = viewRect.x;
				textViewRect.y = viewRect.y;
				textViewRect.width = viewRect.width;
				textViewRect.height = viewRect.height;
				
				Font font = c.getFont();
				FontMetrics fm = g2d.getFontMetrics(font);
				
				//Este stringBounds es alineado LEFT-TOP sin contar los insets
				initialTextLocation = new Point(textViewRect.x, textViewRect.y);
				stringBounds = Strings.getStringBounds(g2d, text, textViewRect.x, textViewRect.y + fm.getAscent());
				
				//El vertical offset es el espacio superior que se da en las fuentes que omitiremos para que se empiece a pintar justo en la posición 'y' que queramos
				//El horizontal offset es el espacio que dejan algunas fuentes en horizontal que omitiremos para que se empiece a pintar justo en la posición 'x' que queramos
				verticalOffset = textViewRect.y - stringBounds.y;//Será negativo
				horizOffset = textViewRect.x - stringBounds.x;
			}
			
			Rectangle iconViewRect = null;
			if (iconVisible && textVisible) {
				
				if (verticalTextPosition != SwingConstants.CENTER || horizontalTextPosition != SwingConstants.CENTER) {
					
					iconViewRect = new Rectangle();
					
					//El icono quitará espacio al texto cuando el verticalTextPosition != CENTER o el horizontalTextPosition != CENTER
					iconViewRect.width = icon.getIconWidth();
					iconViewRect.height = icon.getIconHeight();
					
					//verticalTextPosition tendrá prioridad sobre horizontalTextPosition
					if (verticalTextPosition == SwingConstants.CENTER) {
						
						iconViewRect.width = iconViewRect.width + iconTextGap;
						iconViewRect.height = textViewRect.height;
						iconViewRect.y = textViewRect.y;
						textViewRect.width = textViewRect.width - iconViewRect.width;
						
						//text icon
						if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
							iconViewRect.x = viewRect.x + viewRect.width - iconViewRect.width;
						}
						//icon text
						else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
							iconViewRect.x = viewRect.x;
							textViewRect.x = viewRect.x + iconViewRect.width;
						}
					}
					else {
						
						iconViewRect.height += iconTextGap;
						iconViewRect.width = viewRect.width;
						iconViewRect.x = viewRect.x;
						textViewRect.height -= iconViewRect.height;
						
						//text
						//icon
						if (verticalTextPosition == SwingConstants.TOP) {
							iconViewRect.y = viewRect.y + viewRect.height - iconViewRect.height;
						}
						//icon
						//text
						else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
							iconViewRect.y = viewRect.y;
							//
							textViewRect.y = viewRect.y + iconViewRect.height;
							
							int textViewBottom = textViewRect.y + stringBounds.height;
							int viewBottom = textViewRect.y + textViewRect.height;
							if (textViewBottom > viewBottom) {
								//Subimos la posición del texto en caso de que el icono este encima y no haya espacio para el texto por abajo
								int yCorrection = Math.min(textViewRect.y - viewRect.y, textViewBottom - viewBottom);
								textViewRect.y -= yCorrection;
								textViewRect.height += yCorrection;
							}
						} 
					}
				}
			}
			
			//En este punto ya sabemos el espacio mínimo que tendremos para pintar el texto y el icono
			if (textVisible) {
				
				if (textViewRect.width < stringBounds.width) {
					//No tenemos espacio para pintar el texto y pintaremos como si horizontalAlignment es LEFT
					horizontalAlignment = SwingConstants.LEFT;
				}
				else {
					
					int freeSpace = textViewRect.width - stringBounds.width;
					if (horizontalAlignment == SwingConstants.CENTER)
						textViewRect.x += freeSpace/2;
					else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
						textViewRect.x = textViewRect.x + freeSpace;
					
					textViewRect.width = stringBounds.width;
				}
				
				if (textViewRect.height < stringBounds.height) {
					//No tenemos espacio para pintar el texto y pintaremos como si verticalAlignment es TOP
					verticalAlignment = SwingConstants.TOP;
				}
				else {
					
					int freeSpace = textViewRect.height - stringBounds.height;
					if (verticalAlignment == SwingConstants.CENTER)
						textViewRect.y += freeSpace/2;
					else if (verticalAlignment == SwingConstants.BOTTOM)
						textViewRect.y = textViewRect.y + freeSpace;
					
					textViewRect.height = stringBounds.height;
				}
				
				if (iconVisible && verticalTextPosition == SwingConstants.TOP && textViewRect.height < stringBounds.height) {
					//Esto es para que no se siga viendo el texto cuando no hay hueco y el icono de abajo se superponga con el texto
					textViewRect.height = Math.min(stringBounds.height, viewRect.height);
				}
			}
			
			if (iconVisible) {
				
				if (textVisible) {
					if (viewRect.width < iconRect.width)
						horizontalAlignment = SwingConstants.LEFT;
					if (viewRect.height < iconRect.height)
						verticalAlignment = SwingConstants.TOP;
				}
				
				//Pintamos el icono respecto al viewRect como si no hubiese texto
				if (verticalAlignment == SwingConstants.TOP)
					iconRect.y = viewRect.y;
				else if (verticalAlignment == SwingConstants.BOTTOM)
					iconRect.y = viewRect.y + viewRect.height - iconRect.height;
				else
					iconRect.y = viewRect.y + (viewRect.height - iconRect.height)/2;
				
				if (horizontalAlignment == SwingConstants.LEFT || horizontalAlignment == SwingConstants.LEADING)
					iconRect.x = viewRect.x;
				else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
					iconRect.x = viewRect.x + viewRect.width - iconRect.width;
				else	
					iconRect.x = viewRect.x + (viewRect.width - iconRect.width)/2;
				
				
				if (textVisible) {
					
					if (verticalTextPosition == SwingConstants.CENTER && horizontalTextPosition == SwingConstants.CENTER) {
						
						//Pintamos el icono centrado respecto al texto
						iconRect.y = textViewRect.y + (textViewRect.height - iconRect.height)/2;
						iconRect.x = textViewRect.x + (textViewRect.width - iconRect.width)/2;
					}
					else {
						
						//Ya tenemos el texto dimensionado y ahora pintaremos el icono respecto al texto
						//o el texto respecto al icono según quien sea mas grande
													
						boolean iconIntersectsText = iconViewRect.intersects(textViewRect);
						//Pintamos respecto al iconViewRect
						if (verticalTextPosition == SwingConstants.CENTER) {
							
							if (iconRect.height < stringBounds.height) {
								//Pintamos la posición y del icono centrada relativa al texto
								iconRect.y = Math.max(textViewRect.y, textViewRect.y + (textViewRect.height - iconRect.height)/2);
							}
							else {
								int iconVisibleHeight = Math.min(iconRect.height, viewRect.y + viewRect.height - iconRect.y);
								//Pintamos la posición y del texto centrada relativa al icono
								textViewRect.y = Math.max(iconRect.y, iconRect.y + (iconVisibleHeight - stringBounds.height)/2);
							}
							
							//text icon
							if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
								//iconRect.x = iconViewRect.x + iconTextGap;
								if (iconIntersectsText || horizontalAlignment == SwingConstants.RIGHT)
									iconRect.x = iconViewRect.x + iconTextGap;
								else {
									iconRect.x = textViewRect.x + textViewRect.width + iconTextGap;
								}
							}
							//icon text
							else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
								//iconRect.x = iconViewRect.x;
								if (iconIntersectsText || horizontalAlignment == SwingConstants.LEFT)
									iconRect.x = iconViewRect.x;
								else
									iconRect.x = textViewRect.x - iconRect.width - iconTextGap;
							}
						}
						else {							
							
							//text
							//icon
							if (verticalTextPosition == SwingConstants.TOP) {
								
								if (iconIntersectsText || verticalAlignment == SwingConstants.BOTTOM)
									iconRect.y = iconViewRect.y + iconTextGap;
								else {
									iconRect.y = textViewRect.y + textViewRect.height + iconTextGap;
								}
							}
							//icon
							//text
							else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
								if (iconIntersectsText || verticalAlignment == SwingConstants.TOP)
									iconRect.y = iconViewRect.y;
								else
									iconRect.y = textViewRect.y - iconRect.height - iconTextGap;
							}
							
							if (iconRect.width < stringBounds.width) {
								//Pintamos la posición x del icono relativa al texto
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									iconRect.x = textViewRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + textViewRect.width - iconRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + (textViewRect.width - iconRect.width)/2);
							}
							else {
								int iconVisibleWidth = Math.min(iconRect.width, viewRect.x + viewRect.width - iconRect.x);
								//Pintamos la posición x del texto relativa al icono
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									textViewRect.x = iconRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + iconVisibleWidth - textViewRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + (iconVisibleWidth - textViewRect.width)/2);
							}
						}
					}
				}
			}
			else {
				iconRect.x = 0;
				iconRect.y = 0;
				iconRect.width = 0;
				iconRect.height = 0;
			}
			
			/** Habría que descomentar estas líneas si queremos 
			 * que el textRect abarque el margen inferior 
			 * en caso de que no tengamos espacio en vertical para pintar el texto.
			 * Ahora se pinta en drawString(...) con drawUnderTextBounds = true
			 * pero el texto que se ve en el margen inferior está fuera del textRect
			 **/
			/*if (textViewRect.height < stringBounds.height) {
				//Esto es para que en caso de que no haya hueco se siga viendo el texto a través de un borde inferior transparente
				textViewRect.height = Math.min(stringBounds.height, c.getHeight() - insets.top);
			}*/
			/******************************************/
			
			//stringBounds = Strings.getStringBounds(g2d, text, textViewRect.x, textViewRect.y + fm.getAscent());
			if (textVisible) {
				stringBounds.x+=(textViewRect.x - initialTextLocation.x);
				stringBounds.y+=(textViewRect.y - initialTextLocation.y);
	
				textBounds.x = stringBounds.x + horizOffset;
				textBounds.y = stringBounds.y + verticalOffset;
				textBounds.width = stringBounds.width;
				textBounds.height = stringBounds.height;
							
				textRect.x = textBounds.x;
				textRect.y = textBounds.y;
				textRect.width = Math.min(textBounds.width, textViewRect.width);
				textRect.height = textBounds.height;
			}
			else {
				
				textViewRect.x = 0;
				textViewRect.y = 0;
				textViewRect.width = 0;
				textViewRect.height = 0;
				
				textBounds.x = 0;
				textBounds.y = 0;
				textBounds.width = 0;
				textBounds.height = 0;
				
				textRect.x = 0;
				textRect.y = 0;
				textRect.width = 0;
				textRect.height = 0;
			}
			offset.x = horizOffset;
			offset.y = verticalOffset;
	
			/////// TEST ///////
			g2d.setColor(Color.black);
			g2d.drawRect(0, 0, c.getWidth()-1, c.getHeight()-1);
			
			if (iconViewRect != null) {
				g2d.setColor(Color.green);
				g2d.drawRect(iconViewRect.x, iconViewRect.y, iconViewRect.width-1, iconViewRect.height-1);
			}
			
			
			g2d.setColor(Color.orange);
			g2d.drawRect(textBounds.x, textBounds.y, textBounds.width-1, textBounds.height-1);
			
			g2d.setColor(Color.blue);
			g2d.drawRect(textViewRect.x, textViewRect.y, textViewRect.width-1, textViewRect.height-1);
		}
	}

	public static void cleanRectangle(Rectangle rectangle) {
		if (rectangle != null) {
			rectangle.x = 0;
			rectangle.y = 0;
			rectangle.width = 0;
			rectangle.height = 0;
		}
	}

	public static void layoutText(Rectangle viewRect, Rectangle textViewRect, Rectangle textRect, List<Rectangle> textLinesBounds, Dimension preferredSize, List<Point> offsets, String text, FontMetrics fm, int verticalAlignment, int horizontalAlignment) {
		
		layoutTextIcon(viewRect, textViewRect, textRect, textLinesBounds, null, preferredSize, offsets, text, null, 0, fm, verticalAlignment, horizontalAlignment, SwingConstants.CENTER, SwingConstants.LEADING);
	}
	public static void layoutTextIcon(JComponent c, Rectangle textViewRect, Rectangle textRect, List<Rectangle> textLinesBounds, Rectangle iconRect, Dimension preferredSize, List<Point> offsets, String text, Icon icon, int iconTextGap, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		if (c != null) {
			
			Insets insets = c.getInsets();
			Rectangle viewRect =  new Rectangle(insets.left, insets.top, c.getWidth() - insets.left - insets.right, c.getHeight() - insets.top - insets.bottom);
			FontMetrics fm = c.getFontMetrics(c.getFont());
			
			layoutTextIcon(viewRect, textViewRect, textRect, textLinesBounds, iconRect, preferredSize, offsets, text, icon, iconTextGap, fm, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition);
		}
	}
	
	public static void layoutTextIcon(Rectangle viewRect, Rectangle textViewRect, Rectangle textRect, List<Rectangle> textLinesBounds, Rectangle iconRect, Dimension preferredSize, List<Point> offsets, String text, Icon icon, int iconTextGap, FontMetrics fm, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		if (fm != null) {
			
			if (text == null)
				text = Constants.VOID;
			
			boolean autoViewRect = false;
			if (viewRect == null) {
				autoViewRect = true;
				viewRect = new Rectangle();
			}
			
			if (textViewRect == null)
				textViewRect = new Rectangle();
			else
				cleanRectangle(textViewRect);
			
			if (textRect == null)
				textRect = new Rectangle();
			else
				cleanRectangle(textRect);
			
			if (textLinesBounds == null)
				textLinesBounds = Lists.newList();
			else
				textLinesBounds.clear();
			
			if (iconRect == null)
				iconRect = new Rectangle();
			else
				cleanRectangle(iconRect);
			
			if (preferredSize == null)
				preferredSize = new Dimension();
			else {
				preferredSize.width = 0;
				preferredSize.height = 0;
			}
			if (offsets == null)
				offsets = Lists.newList();
			else
				offsets.clear();
			
			//Graphics2D g2d = (Graphics2D) g;
			
			if (icon != null) {
				iconRect.width = icon.getIconWidth();
				iconRect.height = icon.getIconHeight();
			}
			
			boolean iconVisible = iconRect.width > 0 && iconRect.height > 0;
			boolean textVisible = !text.equals(Constants.VOID);
			
			//Guardamos el horizontalAlignment original para saber como alinear el texto con varias líneas
			int origHorizontalAlignment = horizontalAlignment;
			
			List<Rectangle> stringLinesBounds = Lists.newList();
			Rectangle globalStringBounds = null;
			Point initialTextLocation = null;
			int firstLineVerticalTextOffset = 0;
			
			if (textVisible) {
				
				textViewRect.x = viewRect.x;
				textViewRect.y = viewRect.y;
				textViewRect.width = viewRect.width;
				textViewRect.height = viewRect.height;
				
				Font font = fm.getFont();
				
				LFont lFont = font instanceof LFont ? (LFont) font : null;
		        int textHeightLayoutMode = lFont != null ? lFont.getLayoutMode() : LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
				
				//Este stringBounds es alineado LEFT-TOP sin contar los insets
				initialTextLocation = new Point(textViewRect.x, textViewRect.y);
				
				String[] lines = Strings.getLines(text);
				int interLineSpace = fm.getLeading();
				if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT)
					interLineSpace = font.getSize()/4;
				else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON)
					interLineSpace = font.getSize()/5;//interLineSpace*2;
				
				int maxFontHeight = fm.getAscent() + fm.getDescent();
				Rectangle customFontBounds = null;
				if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
					customFontBounds = Strings.getStringBounds(LLabelUI.TYPOGRAPHY_TEXT_MOST, fm, textViewRect.x, fm.getAscent());
				else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON)
					customFontBounds = Strings.getStringBounds(LLabelUI.TYPOGRAPHY_TEXT_COMMON, fm, textViewRect.x, fm.getAscent());
				
				for (int i = 0; i < lines.length; i++) {
					
					String lineText = lines[i];
					int y = textViewRect.y;
					if (globalStringBounds != null)
						y += globalStringBounds.height + interLineSpace;
					
					Rectangle stringBounds = Strings.getStringBounds(lineText, fm, textViewRect.x, y + fm.getAscent());
					int yTextPosition = stringBounds.y - y;
					
					int fontHeight = stringBounds.height;
					if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
						fontHeight = maxFontHeight;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
						fontHeight = customFontBounds.height;
					
					Point offset = new Point();
					//El vertical offset es el espacio superior que se da en las fuentes que omitiremos para que se empiece a pintar justo en la posición 'y' que queramos (si useExactTextHeight=true)
					//El horizontal offset es el espacio que dejan algunas fuentes en horizontal que omitiremos para que se empiece a pintar justo en la posición 'x' que queramos
					//offset.y = fontHeight - maxFontHeight;
					if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT)
						offset.y = -yTextPosition;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
						offset.y = 0;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
						offset.y = -customFontBounds.y;
					
					if (i == 0)
						firstLineVerticalTextOffset = yTextPosition + offset.y;
					
					offset.x = textViewRect.x - stringBounds.x;
					
					stringBounds.height = fontHeight;
					stringBounds.y -= (fontHeight - stringBounds.height)/2;
					
					if (globalStringBounds == null) {
						if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
							stringBounds.y = y - offset.y;
						else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
							stringBounds.y = y - offset.y;
						
						globalStringBounds = new Rectangle(stringBounds);
					}
					else {
						if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
							stringBounds.y = globalStringBounds.y + globalStringBounds.height + interLineSpace;
						else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
							stringBounds.y = globalStringBounds.y + globalStringBounds.height + interLineSpace;
						
						globalStringBounds.height += (stringBounds.height + interLineSpace);
						globalStringBounds.width = Math.max(globalStringBounds.width, stringBounds.width);
					}
					
					stringLinesBounds.add(stringBounds);
					offsets.add(offset);
				}
			}
			
			if (globalStringBounds != null) {
				preferredSize.width = globalStringBounds.width;
				preferredSize.height = globalStringBounds.height;
			}
			
			Rectangle iconViewRect = null;
			if (iconVisible) {
				
				if (textVisible) {
					if (verticalTextPosition != SwingConstants.CENTER || horizontalTextPosition != SwingConstants.CENTER) {
						
						iconViewRect = new Rectangle();
						
						//El icono quitará espacio al texto cuando el verticalTextPosition != CENTER o el horizontalTextPosition != CENTER
						iconViewRect.width = icon.getIconWidth();
						iconViewRect.height = icon.getIconHeight();
						
						//verticalTextPosition tendrá prioridad sobre horizontalTextPosition
						if (verticalTextPosition == SwingConstants.CENTER) {
							
							iconViewRect.width = iconViewRect.width + iconTextGap;
							iconViewRect.height = textViewRect.height;
							iconViewRect.y = textViewRect.y;
							textViewRect.width = textViewRect.width - iconViewRect.width;
							
							//text icon
							if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
								iconViewRect.x = viewRect.x + viewRect.width - iconViewRect.width;
							}
							//icon text
							else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
								iconViewRect.x = viewRect.x;
								textViewRect.x = viewRect.x + iconViewRect.width;
							}
							
							preferredSize.width += iconViewRect.width;
							preferredSize.height = Math.max(preferredSize.height, icon.getIconHeight());
						}
						else {
							
							iconViewRect.height += iconTextGap;
							iconViewRect.width = viewRect.width;
							iconViewRect.x = viewRect.x;
							textViewRect.height -= iconViewRect.height;
							
							//text
							//icon
							if (verticalTextPosition == SwingConstants.TOP) {
								iconViewRect.y = viewRect.y + viewRect.height - iconViewRect.height;
							}
							//icon
							//text
							else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
								iconViewRect.y = viewRect.y;
								//
								textViewRect.y = viewRect.y + iconViewRect.height;
								
								int textViewBottom = textViewRect.y + globalStringBounds.height;
								int viewBottom = textViewRect.y + textViewRect.height;
								if (textViewBottom > viewBottom) {
									//Subimos la posición del texto en caso de que el icono este encima y no haya espacio para el texto por abajo
									int yCorrection = Math.min(textViewRect.y - viewRect.y, textViewBottom - viewBottom);
									textViewRect.y -= yCorrection;
									textViewRect.height += yCorrection;
								}
							}
							preferredSize.height += iconViewRect.height;
							preferredSize.width = Math.max(preferredSize.width, icon.getIconWidth());
						}
					}
				}
				else {//textVisible=false
					preferredSize.width = iconRect.width;
					preferredSize.height = iconRect.height;
				}
			}
			
			int topCorrection = 0;
			
			//En este punto ya sabemos el espacio mínimo que tendremos para pintar el texto y el icono
			if (textVisible) {
				
				if (autoViewRect) {
					viewRect.setSize(preferredSize);
					textViewRect.setSize(preferredSize);
				}
				
				if (textViewRect.width < globalStringBounds.width) {
					//No tenemos espacio para pintar el texto y pintaremos como si horizontalAlignment es LEFT
					horizontalAlignment = SwingConstants.LEFT;
				}
				else {
					
					int freeSpace = textViewRect.width - globalStringBounds.width;
					if (horizontalAlignment == SwingConstants.CENTER)
						textViewRect.x += freeSpace/2;
					else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
						textViewRect.x = textViewRect.x + freeSpace;
					
					textViewRect.width = globalStringBounds.width;
				}
				
				if (textViewRect.height < globalStringBounds.height && (verticalAlignment != SwingConstants.CENTER || stringLinesBounds.size() > 1)) {
					//No tenemos espacio para pintar el texto y pintaremos como si verticalAlignment es TOP
					//(Excepto cuando verticalAlignment=SwingConstants.CENTER y sólo tengamos una línea, que mantendremos el centrado)
					verticalAlignment = SwingConstants.TOP;
					if (firstLineVerticalTextOffset > 0) {
						topCorrection = Math.min(firstLineVerticalTextOffset, globalStringBounds.height - textViewRect.height);
						textViewRect.y -= topCorrection;
						textViewRect.height += topCorrection;
					}
				}
				else {
					
					int freeSpace = textViewRect.height - globalStringBounds.height;
					if (verticalAlignment == SwingConstants.CENTER)
						textViewRect.y += freeSpace/2;
					else if (verticalAlignment == SwingConstants.BOTTOM)
						textViewRect.y = textViewRect.y + freeSpace;
					
					textViewRect.height = globalStringBounds.height;
				}
				
				if (iconVisible && verticalTextPosition == SwingConstants.TOP && textViewRect.height < globalStringBounds.height) {
					//Esto es para que no se siga viendo el texto cuando no hay hueco y el icono de abajo se superponga con el texto
					textViewRect.height = Math.min(globalStringBounds.height, viewRect.height);
				}
			}
			
			if (iconVisible) {
				
				if (textVisible) {
					if (viewRect.width < iconRect.width)
						horizontalAlignment = SwingConstants.LEFT;
					if (viewRect.height < iconRect.height)
						verticalAlignment = SwingConstants.TOP;
				}
				
				//Pintamos el icono respecto al viewRect como si no hubiese texto
				if (verticalAlignment == SwingConstants.TOP)
					iconRect.y = viewRect.y;
				else if (verticalAlignment == SwingConstants.BOTTOM)
					iconRect.y = viewRect.y + viewRect.height - iconRect.height;
				else
					iconRect.y = viewRect.y + (viewRect.height - iconRect.height)/2;
				
				if (horizontalAlignment == SwingConstants.LEFT || horizontalAlignment == SwingConstants.LEADING)
					iconRect.x = viewRect.x;
				else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
					iconRect.x = viewRect.x + viewRect.width - iconRect.width;
				else	
					iconRect.x = viewRect.x + (viewRect.width - iconRect.width)/2;
				
				
				if (textVisible) {
					
					if (verticalTextPosition == SwingConstants.CENTER && horizontalTextPosition == SwingConstants.CENTER) {
						
						//Pintamos el icono centrado respecto al texto
						iconRect.y = textViewRect.y + (textViewRect.height - iconRect.height)/2;
						iconRect.x = textViewRect.x + (textViewRect.width - iconRect.width)/2;
					}
					else {
						
						//Ya tenemos el texto dimensionado y ahora pintaremos el icono respecto al texto
						//o el texto respecto al icono según quien sea mas grande
													
						boolean iconIntersectsText = iconViewRect.intersects(textViewRect);
						//Pintamos respecto al iconViewRect
						if (verticalTextPosition == SwingConstants.CENTER) {
							
							if (iconRect.height < globalStringBounds.height) {
								//Pintamos la posición 'y' del icono centrada relativa al texto
								iconRect.y = Math.max(textViewRect.y + topCorrection, textViewRect.y + topCorrection + (textViewRect.height - iconRect.height - topCorrection)/2);
							}
							else {
								int iconVisibleHeight = Math.min(iconRect.height, viewRect.y + viewRect.height - iconRect.y);
								//Pintamos la posición 'y' del texto centrada relativa al icono
								textViewRect.y = Math.max(iconRect.y, iconRect.y + (iconVisibleHeight - globalStringBounds.height)/2);
							}
							
							//text icon
							if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
								if (iconIntersectsText || horizontalAlignment == SwingConstants.RIGHT)
									iconRect.x = iconViewRect.x + iconTextGap;
								else {
									iconRect.x = textViewRect.x + textViewRect.width + iconTextGap;
								}
							}
							//icon text
							else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
								if (iconIntersectsText || horizontalAlignment == SwingConstants.LEFT)
									iconRect.x = iconViewRect.x;
								else
									iconRect.x = textViewRect.x - iconRect.width - iconTextGap;
							}
						}
						else {							
							
							//text
							//icon
							if (verticalTextPosition == SwingConstants.TOP) {
								
								if (iconIntersectsText || verticalAlignment == SwingConstants.BOTTOM)
									iconRect.y = iconViewRect.y + iconTextGap;
								else {
									iconRect.y = textViewRect.y + textViewRect.height + iconTextGap;
								}
							}
							//icon
							//text
							else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
								if (iconIntersectsText || verticalAlignment == SwingConstants.TOP)
									iconRect.y = iconViewRect.y;
								else
									iconRect.y = textViewRect.y - iconRect.height - iconTextGap;
							}
							
							if (iconRect.width < globalStringBounds.width) {
								//Pintamos la posición 'x' del icono relativa al texto
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									iconRect.x = textViewRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + textViewRect.width - iconRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + (textViewRect.width - iconRect.width)/2);
							}
							else {
								int iconVisibleWidth = Math.min(iconRect.width, viewRect.x + viewRect.width - iconRect.x);
								//Pintamos la posición 'x' del texto relativa al icono
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									textViewRect.x = iconRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + iconVisibleWidth - textViewRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + (iconVisibleWidth - textViewRect.width)/2);
							}
						}
					}
				}
			}
			else {
				iconRect.x = 0;
				iconRect.y = 0;
				iconRect.width = 0;
				iconRect.height = 0;
			}
			
			/** Habría que descomentar estas líneas si queremos 
			 * que el textRect abarque el margen inferior 
			 * en caso de que no tengamos espacio en vertical para pintar el texto.
			 * Ahora se pinta en drawString(...) con drawUnderTextBounds = true
			 * pero el texto que se ve en el margen inferior está fuera del textRect
			 **/
			/*if (textViewRect.height < stringBounds.height) {
				//Esto es para que en caso de que no haya hueco se siga viendo el texto a través de un borde inferior transparente
				textViewRect.height = Math.min(stringBounds.height, c.getHeight() - insets.top);
			}*/
			/******************************************/
			
			if (textVisible) {
				
				int xCorrection = textViewRect.x - initialTextLocation.x;
				int yCorrection = textViewRect.y - initialTextLocation.y;
				
				globalStringBounds.x += xCorrection;
				globalStringBounds.y += yCorrection;
				
				for (int i = 0; i < stringLinesBounds.size(); i++) {
					
					Rectangle stringBounds = stringLinesBounds.get(i);
					Point offset = offsets.get(i);
					Rectangle textBounds = new Rectangle(stringBounds);
					textBounds.x += offset.x + xCorrection;
					textBounds.y += offset.y + yCorrection;
					
					//Alineamos las líneas de texto entre ellas
					if (stringBounds.width < textViewRect.width) {
						if (origHorizontalAlignment == SwingConstants.RIGHT || origHorizontalAlignment == SwingConstants.TRAILING)
							textBounds.x += textViewRect.width - stringBounds.width;
						else if (origHorizontalAlignment == SwingConstants.CENTER)
							textBounds.x += (textViewRect.width - stringBounds.width)/2;
					}
					
					textLinesBounds.add(textBounds);
				}
							
				textRect.x = globalStringBounds.x;
				textRect.y = globalStringBounds.y;
				textRect.width = Math.min(globalStringBounds.width, textViewRect.width);
				textRect.height = globalStringBounds.height;
			}
			else {
				
				textViewRect.x = 0;
				textViewRect.y = 0;
				textViewRect.width = 0;
				textViewRect.height = 0;
				
				textRect.x = 0;
				textRect.y = 0;
				textRect.width = 0;
				textRect.height = 0;
				
				textLinesBounds.add(new Rectangle());
				offsets.add(new Point());
			}
		}
	}

	//
	// Layout components
	//

	public static Rectangle paintTextClassic(Graphics g, JComponent c, Rectangle textRect, String text, Icon icon, boolean enabled, int iconTextGap, int shiftOffset, int mnemonicIndex, boolean isShadowTextEnabled, int shadowPosition, boolean iconForegroundEnabled, boolean underline, Color lineColor) {

		Rectangle iconRect = null;
		
		String[] lineas = Strings.getLines(text);

		if (lineas.length <= 1) {

			paintTextClassicLine(g, c, textRect, text, enabled, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, underline, lineColor);
		}
		else {

			int	horizontalTextPosition = SwingConstants.TRAILING;		
			int	verticalTextPosition = SwingConstants.CENTER;			
			int verticalAlignment = SwingConstants.CENTER;
			int horizontalAlignment = SwingConstants.CENTER;
			
			if (c instanceof AbstractButton) {
				AbstractButton b = (AbstractButton) c;
				horizontalTextPosition = b.getHorizontalTextPosition();			
				verticalTextPosition = b.getVerticalTextPosition();			
				verticalAlignment = b.getVerticalAlignment();
			}
			else if (c instanceof JLabel) {
				JLabel l = (JLabel) c;
				horizontalTextPosition = l.getHorizontalTextPosition();			
				verticalTextPosition = l.getVerticalTextPosition();			
				verticalAlignment = l.getVerticalAlignment();
			}
			
			//Pintamos el icono relativo al texto cuando el horizontalTextPosition sea CENTER, sino se pintará en paintIcon(...)
			Icon iconoRelativo = horizontalTextPosition == SwingConstants.CENTER ? icon : null;
			
			Rectangle textRectMulti = null;
			for (int i = 0; i < lineas.length; i++) {

				Rectangle textRectLinea = new Rectangle();
				String textLine = lineas[i];
				//String textLineClipped = UtilsGUI.layoutCompoundLabel(g.getFontMetrics(), c, textLine, textRectLinea, iconTextGap, icon, i, lineas.length);
				String textLineClipped = layoutTextClassicIcon(g.getFontMetrics(), c, textLine, textRectLinea, null, null, iconTextGap, icon, i, lineas.length, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition);
						
				if (c instanceof JMenuItem) {
					//En los menuItem respetamos la posición x precalculada, ya que es relativa al resto de menuItems
					textRectLinea.x = textRect.x;
				}
				
				if (iconoRelativo != null && verticalTextPosition != SwingConstants.CENTER) {
					
					if (verticalAlignment == SwingConstants.CENTER) {
						
						int ajusteVertical = (iconoRelativo.getIconHeight() / 2) + iconTextGap;
						if (verticalTextPosition == SwingConstants.TOP) {
							textRectLinea.y = textRectLinea.y - ajusteVertical;
						}
						else if (verticalTextPosition == SwingConstants.BOTTOM) {
							textRectLinea.y = textRectLinea.y + ajusteVertical;
						}
					}
					else if (verticalAlignment == SwingConstants.BOTTOM && verticalTextPosition == SwingConstants.TOP) {
						
						int ajusteVertical = iconoRelativo.getIconHeight() + iconTextGap;
						textRectLinea.y = textRectLinea.y - ajusteVertical;
					}
				}
				paintTextClassicLine(g, c, textRectLinea, textLineClipped, enabled, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, underline, lineColor);
				
				textRectMulti = textRectMulti == null ? textRectLinea : textRectMulti.union(textRectLinea);
			}
			
			if (iconoRelativo != null) {
				
				iconRect = UISupportUtils.getIconRectRelative(textRectMulti, iconoRelativo, iconTextGap, horizontalTextPosition, verticalTextPosition);
			}
		}
		return iconRect;
	}
	
	public static void paintTextClassicAndIcon(Graphics g, JComponent c, Rectangle textRect, String text, Icon icon, boolean enabled, int iconTextGap, int shiftOffset, int mnemonicIndex, boolean isShadowTextEnabled, int shadowPosition, boolean iconForegroundEnabled) {
		paintTextClassicAndIcon(g, c, textRect, text, icon, enabled, iconTextGap, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, iconForegroundEnabled, false, null);
	}
	
	public static void paintTextClassicAndIcon(Graphics g, JComponent c, Rectangle textRect, String text, Icon icon, boolean enabled, int iconTextGap, int shiftOffset, int mnemonicIndex, boolean isShadowTextEnabled, int shadowPosition, boolean iconForegroundEnabled, boolean underline, Color lineColor) {

		Rectangle iconRect = paintTextClassic(g, c, textRect, text, icon, enabled, iconTextGap, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, iconForegroundEnabled, underline, lineColor);
		
		//Pintamos el icono relativo al texto cuando el horizontalTextPosition sea CENTER, sino se pintará en paintIcon(...) iconRect=null
		if (iconRect != null)
			paintIcon(g, c, iconRect, icon, shiftOffset, iconForegroundEnabled, isShadowTextEnabled, shadowPosition);
	}
	
	public static void paintTextClassicLine(Graphics g, JComponent c, Rectangle textRect, String text, boolean enabled) {
		paintTextClassicLine(g, c, textRect, text, enabled, 0, -1, false, -1, false, null);
	}
	public static void paintTextClassicLine(Graphics g, JComponent c, Rectangle textRect, String text, boolean enabled, int textShiftOffset, int mnemonicIndex, boolean isShadowTextEnabled, int shadowPosition) {
		paintTextClassicLine(g, c, textRect, text, enabled, textShiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, false, null);
	}
	public static void paintTextClassicLine(Graphics g, JComponent c, Rectangle textRect, String text, boolean enabled, int textShiftOffset, int mnemonicIndex, boolean isShadowTextEnabled, int shadowPosition, boolean underline, Color lineColor) {
		
		FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        
        int xText = textRect.x;
        int yText = textRect.y + fm.getAscent();
        
        Color background = getBackground(c);
        Color foreground = getForeground(c);
        Color shadowColor = null;
        
        /* Draw the Text */
        if(enabled) {
        	
        	xText = xText + textShiftOffset;
            yText = yText + textShiftOffset;
            
        	if (isShadowTextEnabled) {
            	/*** paint the text shadow ***/
            	 shadowColor = Colors.darker(background, 0.7);
            }
        }
        else {
            /*** paint the text disabled shadow ***/
        	boolean backgroundIsDark = Colors.isColorDark(background);
        	shadowColor = backgroundIsDark ? Colors.darker(background, 0.7) : Colors.brighter(background, 0.7);
            shadowPosition = SwingConstants.NORTH_WEST;
            
            if (foreground != null && foreground.equals(c.getForeground())) {
	       		 //No se ha definido un disabledBackground, así que lo calculamos
	       		 foreground = backgroundIsDark ? Colors.brighter(background, 0.32) : Colors.darker(background, 0.32);
	       	}
            //PRUEBAS
            //shadowPosition = SwingConstants.NORTH;
            //shadowColor = Colors.brighter(background, 0.5);
            foreground = backgroundIsDark ? Colors.brighter(background, 0.22) : Colors.darker(background, 0.32);
            shadowColor = backgroundIsDark ? Colors.darker(background, 0.1) : Colors.brighter(background, 0.1);
        }
        
        g.setFont(c.getFont());
        if (shadowColor != null && shadowPosition != -1) {
        	/*** paint the text shadow ***/
        	 g.setColor(shadowColor);
        	 Point shadowLocation = getShadowLocation(new Point(xText, yText), shadowPosition);
        	 //SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, shadowLocation.x, shadowLocation.y);
        	//SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, xText, yText-1);
        	 GraphicsUtils.drawString(g,text, shadowLocation.x, shadowLocation.y);
        }
                	
       if (underline) {
        	
        	if (lineColor == null || !enabled)
        		lineColor = foreground;
        	
        	g.setColor(lineColor);
        	
			//Ignoramos los blancos que haya al inicio
			int numBlancos = 0;
			StringBuffer blancosInicio = new StringBuffer();
			while (text.length() > numBlancos && text.charAt(numBlancos) == ' ') {
				blancosInicio.append(" ");
				numBlancos++;
			}

			int anchoBlancosInicio = c.getFontMetrics(c.getFont()).stringWidth(blancosInicio.toString());

			//Pintamos la linea de subrrallado
			g.fillRect(xText + anchoBlancosInicio, yText + 2, textRect.width - anchoBlancosInicio, 1);
		}
        
        /*** paint the text normally */
        g.setColor(foreground);
        //SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, xText, yText);
        GraphicsUtils.drawString(g,text, xText, yText);
	}
	
	public static String layoutTextClassicIcon(FontMetrics fm, JComponent c, String texto, Rectangle textRect, Rectangle iconRect, Rectangle globalTextRect, int textIconGap, Icon icono, int linea, int lineas, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		Insets insets = c.getInsets();
	
		Rectangle viewRect = new Rectangle();
		if (iconRect == null)
			iconRect = new Rectangle();
		if (textRect == null)
			textRect = new Rectangle();
		if (globalTextRect == null)
			globalTextRect = new Rectangle();
		
		viewRect.x = insets.left;
		viewRect.y = insets.top;
		viewRect.width = c.getWidth() - (insets.right + viewRect.x);
		viewRect.height = c.getHeight() - (insets.bottom + viewRect.y);
	
		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
	
		if (texto == null)
			textIconGap = 0;
		
		// layout the text and icon
		String text = SwingUtilities.layoutCompoundLabel(
			c, fm, texto, icono, 
			verticalAlignment, horizontalAlignment,
			verticalTextPosition, horizontalTextPosition,
			viewRect, iconRect, textRect, 
			textIconGap
		);
	
		final int interlineado = textRect.height - fm.getAscent();
		int sizeRealFuente = fm.getAscent();
		int sizeRealTotal = sizeRealFuente*lineas + interlineado*(lineas - 1);
		
		int diferenciaIconoTexto = 0;
		if (iconRect.height > 0)
			diferenciaIconoTexto = (sizeRealFuente - iconRect.height)/2;
	
		if (verticalAlignment == SwingConstants.TOP) {
			
			textRect.y = textRect.y;// + insets.top;
		}
		else if (verticalAlignment == SwingConstants.CENTER) {
	
			textRect.y = (c.getHeight() - sizeRealTotal + 1) / 2;
			iconRect.y = (c.getHeight() - iconRect.height + 1) / 2;
		}
		else if (verticalAlignment == SwingConstants.BOTTOM) {
	
			textRect.y = c.getHeight() - sizeRealTotal;// - insets.bottom;
			
			if (iconRect.height > 0) {
				
				if (iconRect.height >= c.getHeight() - insets.bottom) {
	
					iconRect.y = c.getHeight() - iconRect.height - insets.bottom;
				}
				else {
					
					iconRect.y = c.getHeight() - iconRect.height - insets.bottom;
					iconRect.y = iconRect.y - diferenciaIconoTexto;
				}
			}
		}
		
		int yLinea = 0;
		if (linea > 0)
			yLinea = (sizeRealFuente + interlineado) * linea;
			
		//Este es el textRect de todas las lineas en conjunto
		globalTextRect.x = textRect.x;
		globalTextRect.y = textRect.y;// - posicionCeroTexto;
		globalTextRect.width = Math.max(textRect.width, globalTextRect.width);
		globalTextRect.height = textRect.height + yLinea;// + posicionCeroTexto;
		
		textRect.y = textRect.y + yLinea;
		
		//if (c instanceof JLabel)
		//	textRect.y = textRect.y + c.getFont().getSize();
		
		return text;
	}
	
	/*
	public static void layoutTextIcon(JComponent c, Rectangle textViewRect, Rectangle textRect, List<Rectangle> textLinesBounds, Rectangle iconRect, Dimension preferredSize, List<Point> offsets, int textHeightLayoutMode, String text, Icon icon, int iconTextGap, int verticalAlignment, int horizontalAlignment, int verticalTextPosition, int horizontalTextPosition) {
		
		if (c != null) {
			
			if (text == null)
				text = Constants.VOID;
			
			if (textViewRect == null)
				textViewRect = new Rectangle();
			else
				cleanRectangle(textViewRect);
			
			if (textRect == null)
				textRect = new Rectangle();
			else
				cleanRectangle(textRect);
			
			if (textLinesBounds == null)
				textLinesBounds = Lists.newList();
			else
				textLinesBounds.clear();
			
			if (iconRect == null)
				iconRect = new Rectangle();
			else
				cleanRectangle(iconRect);
			
			if (preferredSize == null)
				preferredSize = new Dimension();
			else {
				preferredSize.width = 0;
				preferredSize.height = 0;
			}
			if (offsets == null)
				offsets = Lists.newList();
			else
				offsets.clear();
			
			//Graphics2D g2d = (Graphics2D) g;
			
			Insets insets = c.getInsets();
			Rectangle viewRect =  new Rectangle(insets.left, insets.top, c.getWidth() - insets.left - insets.right, c.getHeight() - insets.top - insets.bottom);
			
			if (icon != null) {
				iconRect.width = icon.getIconWidth();
				iconRect.height = icon.getIconHeight();
			}
			
			boolean iconVisible = iconRect.width > 0 && iconRect.height > 0;
			boolean textVisible = !text.equals(Constants.VOID);
			
			//Guardamos el horizontalAlignment original para saber como alinear el texto con varias líneas
			int origHorizontalAlignment = horizontalAlignment;
			
			List<Rectangle> stringLinesBounds = Lists.newList();
			Rectangle globalStringBounds = null;
			Point initialTextLocation = null;
			int firstLineVerticalTextOffset = 0;
			
			if (textVisible) {
				
				textViewRect.x = viewRect.x;
				textViewRect.y = viewRect.y;
				textViewRect.width = viewRect.width;
				textViewRect.height = viewRect.height;
				
				Font font = c.getFont();
				FontMetrics fm = c.getFontMetrics(font);
				
				//Este stringBounds es alineado LEFT-TOP sin contar los insets
				initialTextLocation = new Point(textViewRect.x, textViewRect.y);
				
				String[] lines = UtilsGUI.getLineas(text);
				int interLineSpace = fm.getLeading();
				if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT)
					interLineSpace = font.getSize()/4;
				else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON)
					interLineSpace = interLineSpace*2;
				
				int maxFontHeight = fm.getAscent() + fm.getDescent();
				Rectangle customFontBounds = null;
				if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
					customFontBounds = Strings.getStringBounds(LLabelUI.TYPOGRAPHY_TEXT_MOST, fm, textViewRect.x, fm.getAscent());
				else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON)
					customFontBounds = Strings.getStringBounds(LLabelUI.TYPOGRAPHY_TEXT_COMMON, fm, textViewRect.x, fm.getAscent());
				
				for (int i = 0; i < lines.length; i++) {
					
					String lineText = lines[i];
					int y = textViewRect.y;
					if (globalStringBounds != null)
						y += globalStringBounds.height + interLineSpace;
					
					Rectangle stringBounds = Strings.getStringBounds(lineText, fm, textViewRect.x, y + fm.getAscent());
					int yTextPosition = stringBounds.y - y;
					
					int fontHeight = stringBounds.height;
					if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
						fontHeight = maxFontHeight;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
						fontHeight = customFontBounds.height;
					
					Point offset = new Point();
					//El vertical offset es el espacio superior que se da en las fuentes que omitiremos para que se empiece a pintar justo en la posición 'y' que queramos (si useExactTextHeight=true)
					//El horizontal offset es el espacio que dejan algunas fuentes en horizontal que omitiremos para que se empiece a pintar justo en la posición 'x' que queramos
					//offset.y = fontHeight - maxFontHeight;
					if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT)
						offset.y = -yTextPosition;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
						offset.y = 0;
					else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
						offset.y = -customFontBounds.y;
					
					if (i == 0)
						firstLineVerticalTextOffset = yTextPosition + offset.y;
					
					offset.x = textViewRect.x - stringBounds.x;
					
					stringBounds.height = fontHeight;
					stringBounds.y -= (fontHeight - stringBounds.height)/2;
					
					if (globalStringBounds == null) {
						if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
							stringBounds.y = y - offset.y;
						else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
							stringBounds.y = y - offset.y;
						
						globalStringBounds = new Rectangle(stringBounds);
					}
					else {
						if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX)
							stringBounds.y = globalStringBounds.y + globalStringBounds.height + interLineSpace;
						else if (textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON || textHeightLayoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
							stringBounds.y = globalStringBounds.y + globalStringBounds.height + interLineSpace;
						
						globalStringBounds.height += (stringBounds.height + interLineSpace);
						globalStringBounds.width = Math.max(globalStringBounds.width, stringBounds.width);
					}
					
					stringLinesBounds.add(stringBounds);
					offsets.add(offset);
				}
			}
			
			if (globalStringBounds != null) {
				preferredSize.width = globalStringBounds.width;
				preferredSize.height = globalStringBounds.height;
			}
			
			Rectangle iconViewRect = null;
			if (iconVisible) {
				
				if (textVisible) {
					if (verticalTextPosition != SwingConstants.CENTER || horizontalTextPosition != SwingConstants.CENTER) {
						
						iconViewRect = new Rectangle();
						
						//El icono quitará espacio al texto cuando el verticalTextPosition != CENTER o el horizontalTextPosition != CENTER
						iconViewRect.width = icon.getIconWidth();
						iconViewRect.height = icon.getIconHeight();
						
						//verticalTextPosition tendrá prioridad sobre horizontalTextPosition
						if (verticalTextPosition == SwingConstants.CENTER) {
							
							iconViewRect.width = iconViewRect.width + iconTextGap;
							iconViewRect.height = textViewRect.height;
							iconViewRect.y = textViewRect.y;
							textViewRect.width = textViewRect.width - iconViewRect.width;
							
							//text icon
							if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
								iconViewRect.x = viewRect.x + viewRect.width - iconViewRect.width;
							}
							//icon text
							else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
								iconViewRect.x = viewRect.x;
								textViewRect.x = viewRect.x + iconViewRect.width;
							}
							
							preferredSize.width += iconViewRect.width;
						}
						else {
							
							iconViewRect.height += iconTextGap;
							iconViewRect.width = viewRect.width;
							iconViewRect.x = viewRect.x;
							textViewRect.height -= iconViewRect.height;
							
							//text
							//icon
							if (verticalTextPosition == SwingConstants.TOP) {
								iconViewRect.y = viewRect.y + viewRect.height - iconViewRect.height;
							}
							//icon
							//text
							else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
								iconViewRect.y = viewRect.y;
								//
								textViewRect.y = viewRect.y + iconViewRect.height;
								
								int textViewBottom = textViewRect.y + globalStringBounds.height;
								int viewBottom = textViewRect.y + textViewRect.height;
								if (textViewBottom > viewBottom) {
									//Subimos la posición del texto en caso de que el icono este encima y no haya espacio para el texto por abajo
									int yCorrection = Math.min(textViewRect.y - viewRect.y, textViewBottom - viewBottom);
									textViewRect.y -= yCorrection;
									textViewRect.height += yCorrection;
								}
							}
							preferredSize.height += iconViewRect.height;
						}
					}
				}
				else {//textVisible=false
					preferredSize.width = iconRect.width;
					preferredSize.height = iconRect.height;
				}
			}
			
			int topCorrection = 0;
			
			//En este punto ya sabemos el espacio mínimo que tendremos para pintar el texto y el icono
			if (textVisible) {
				
				if (textViewRect.width < globalStringBounds.width) {
					//No tenemos espacio para pintar el texto y pintaremos como si horizontalAlignment es LEFT
					horizontalAlignment = SwingConstants.LEFT;
				}
				else {
					
					int freeSpace = textViewRect.width - globalStringBounds.width;
					if (horizontalAlignment == SwingConstants.CENTER)
						textViewRect.x += freeSpace/2;
					else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
						textViewRect.x = textViewRect.x + freeSpace;
					
					textViewRect.width = globalStringBounds.width;
				}
				
				if (textViewRect.height < globalStringBounds.height && (verticalAlignment != SwingConstants.CENTER || stringLinesBounds.size() > 1)) {
					//No tenemos espacio para pintar el texto y pintaremos como si verticalAlignment es TOP
					//(Excepto cuando verticalAlignment=SwingConstants.CENTER y sólo tengamos una línea, que mantendremos el centrado)
					verticalAlignment = SwingConstants.TOP;
					if (firstLineVerticalTextOffset > 0) {
						topCorrection = Math.min(firstLineVerticalTextOffset, globalStringBounds.height - textViewRect.height);
						textViewRect.y -= topCorrection;
						textViewRect.height += topCorrection;
					}
				}
				else {
					
					int freeSpace = textViewRect.height - globalStringBounds.height;
					if (verticalAlignment == SwingConstants.CENTER)
						textViewRect.y += freeSpace/2;
					else if (verticalAlignment == SwingConstants.BOTTOM)
						textViewRect.y = textViewRect.y + freeSpace;
					
					textViewRect.height = globalStringBounds.height;
				}
				
				if (iconVisible && verticalTextPosition == SwingConstants.TOP && textViewRect.height < globalStringBounds.height) {
					//Esto es para que no se siga viendo el texto cuando no hay hueco y el icono de abajo se superponga con el texto
					textViewRect.height = Math.min(globalStringBounds.height, viewRect.height);
				}
			}
			
			if (iconVisible) {
				
				if (textVisible) {
					if (viewRect.width < iconRect.width)
						horizontalAlignment = SwingConstants.LEFT;
					if (viewRect.height < iconRect.height)
						verticalAlignment = SwingConstants.TOP;
				}
				
				//Pintamos el icono respecto al viewRect como si no hubiese texto
				if (verticalAlignment == SwingConstants.TOP)
					iconRect.y = viewRect.y;
				else if (verticalAlignment == SwingConstants.BOTTOM)
					iconRect.y = viewRect.y + viewRect.height - iconRect.height;
				else
					iconRect.y = viewRect.y + (viewRect.height - iconRect.height)/2;
				
				if (horizontalAlignment == SwingConstants.LEFT || horizontalAlignment == SwingConstants.LEADING)
					iconRect.x = viewRect.x;
				else if (horizontalAlignment == SwingConstants.RIGHT || horizontalAlignment == SwingConstants.TRAILING)
					iconRect.x = viewRect.x + viewRect.width - iconRect.width;
				else	
					iconRect.x = viewRect.x + (viewRect.width - iconRect.width)/2;
				
				
				if (textVisible) {
					
					if (verticalTextPosition == SwingConstants.CENTER && horizontalTextPosition == SwingConstants.CENTER) {
						
						//Pintamos el icono centrado respecto al texto
						iconRect.y = textViewRect.y + (textViewRect.height - iconRect.height)/2;
						iconRect.x = textViewRect.x + (textViewRect.width - iconRect.width)/2;
					}
					else {
						
						//Ya tenemos el texto dimensionado y ahora pintaremos el icono respecto al texto
						//o el texto respecto al icono según quien sea mas grande
													
						boolean iconIntersectsText = iconViewRect.intersects(textViewRect);
						//Pintamos respecto al iconViewRect
						if (verticalTextPosition == SwingConstants.CENTER) {
							
							if (iconRect.height < globalStringBounds.height) {
								//Pintamos la posición 'y' del icono centrada relativa al texto
								iconRect.y = Math.max(textViewRect.y + topCorrection, textViewRect.y + topCorrection + (textViewRect.height - iconRect.height - topCorrection)/2);
							}
							else {
								int iconVisibleHeight = Math.min(iconRect.height, viewRect.y + viewRect.height - iconRect.y);
								//Pintamos la posición 'y' del texto centrada relativa al icono
								textViewRect.y = Math.max(iconRect.y, iconRect.y + (iconVisibleHeight - globalStringBounds.height)/2);
							}
							
							//text icon
							if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING) {
								if (iconIntersectsText || horizontalAlignment == SwingConstants.RIGHT)
									iconRect.x = iconViewRect.x + iconTextGap;
								else {
									iconRect.x = textViewRect.x + textViewRect.width + iconTextGap;
								}
							}
							//icon text
							else {//if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING) {
								if (iconIntersectsText || horizontalAlignment == SwingConstants.LEFT)
									iconRect.x = iconViewRect.x;
								else
									iconRect.x = textViewRect.x - iconRect.width - iconTextGap;
							}
						}
						else {							
							
							//text
							//icon
							if (verticalTextPosition == SwingConstants.TOP) {
								
								if (iconIntersectsText || verticalAlignment == SwingConstants.BOTTOM)
									iconRect.y = iconViewRect.y + iconTextGap;
								else {
									iconRect.y = textViewRect.y + textViewRect.height + iconTextGap;
								}
							}
							//icon
							//text
							else {//{if (verticalTextPosition == SwingConstants.BOTTOM) {
								if (iconIntersectsText || verticalAlignment == SwingConstants.TOP)
									iconRect.y = iconViewRect.y;
								else
									iconRect.y = textViewRect.y - iconRect.height - iconTextGap;
							}
							
							if (iconRect.width < globalStringBounds.width) {
								//Pintamos la posición 'x' del icono relativa al texto
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									iconRect.x = textViewRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + textViewRect.width - iconRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									iconRect.x = Math.max(textViewRect.x, textViewRect.x + (textViewRect.width - iconRect.width)/2);
							}
							else {
								int iconVisibleWidth = Math.min(iconRect.width, viewRect.x + viewRect.width - iconRect.x);
								//Pintamos la posición 'x' del texto relativa al icono
								if (horizontalTextPosition == SwingConstants.LEFT || horizontalTextPosition == SwingConstants.LEADING)
									textViewRect.x = iconRect.x;
								else if (horizontalTextPosition == SwingConstants.RIGHT || horizontalTextPosition == SwingConstants.TRAILING)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + iconVisibleWidth - textViewRect.width);
								else //if (horizontalTextPosition == SwingConstants.CENTER)
									textViewRect.x = Math.max(iconRect.x, iconRect.x + (iconVisibleWidth - textViewRect.width)/2);
							}
						}
					}
				}
			}
			else {
				iconRect.x = 0;
				iconRect.y = 0;
				iconRect.width = 0;
				iconRect.height = 0;
			}
			
			if (textVisible) {
				
				int xCorrection = textViewRect.x - initialTextLocation.x;
				int yCorrection = textViewRect.y - initialTextLocation.y;
				
				globalStringBounds.x += xCorrection;
				globalStringBounds.y += yCorrection;
				
				for (int i = 0; i < stringLinesBounds.size(); i++) {
					
					Rectangle stringBounds = stringLinesBounds.get(i);
					Point offset = offsets.get(i);
					Rectangle textBounds = new Rectangle(stringBounds);
					textBounds.x += offset.x + xCorrection;
					textBounds.y += offset.y + yCorrection;
					
					//Alineamos las líneas de texto entre ellas
					if (stringBounds.width < textViewRect.width) {
						if (origHorizontalAlignment == SwingConstants.RIGHT || origHorizontalAlignment == SwingConstants.TRAILING)
							textBounds.x += textViewRect.width - stringBounds.width;
						else if (origHorizontalAlignment == SwingConstants.CENTER)
							textBounds.x += (textViewRect.width - stringBounds.width)/2;
					}
					
					textLinesBounds.add(textBounds);
				}
							
				textRect.x = globalStringBounds.x;
				textRect.y = globalStringBounds.y;
				textRect.width = Math.min(globalStringBounds.width, textViewRect.width);
				textRect.height = globalStringBounds.height;
			}
			else {
				
				textViewRect.x = 0;
				textViewRect.y = 0;
				textViewRect.width = 0;
				textViewRect.height = 0;
				
				textRect.x = 0;
				textRect.y = 0;
				textRect.width = 0;
				textRect.height = 0;
				
				textLinesBounds.add(new Rectangle());
				offsets.add(new Point());
			}
		}
	}
	*/
}
