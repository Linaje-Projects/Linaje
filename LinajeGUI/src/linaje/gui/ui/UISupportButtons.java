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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;

import linaje.gui.LButtonPropertable;
import linaje.gui.LButtonProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Lists;
import linaje.utils.StateColor;
import linaje.utils.Strings;
import linaje.gui.StateIcon;

public final class UISupportButtons {

	protected static final List<Class<?>> LBUTTON_UIS = Lists.iteratorToList(LinajeLookAndFeel.UI_LCOMPONENTS_BUTTONS_MAP.keySet().iterator());
	
	private static HashMap<Class<?>, ButtonUIProperties> defaultButtonUIPropertiesMap = null;
	
	//
	// Maping default UI properties
	//
	
	public static HashMap<Class<?>, ButtonUIProperties> getDefaultButtonUIPropertiesMap() {
		if (defaultButtonUIPropertiesMap == null) {
			defaultButtonUIPropertiesMap = new LinkedHashMap<Class<?>, ButtonUIProperties>();
			for (int i = 0; i < LBUTTON_UIS.size(); i++) {
				Class<?> uiClass = LBUTTON_UIS.get(i);
				ButtonUIProperties buttonUIProperties = new ButtonUIProperties(uiClass);
				defaultButtonUIPropertiesMap.put(uiClass, buttonUIProperties);
			}
		}
		return defaultButtonUIPropertiesMap;
	}
	
	
	//
	// Get default UI and button properties
	//
	
	public static ButtonUIProperties getDefaultButtonUIProperties(Class<?> buttonUIClass) {
		ButtonUIProperties defaultButtonUIProperties = getDefaultButtonUIPropertiesMap().get(buttonUIClass);
		if (defaultButtonUIProperties == null)
			defaultButtonUIProperties = getDefaultButtonUIPropertiesMap().get(LBUTTON_UIS.get(0));
		
		return defaultButtonUIProperties;
	}
	
	public static LButtonProperties getDefaultUIButtonProperties(AbstractButton b) {
		final Class<?> buttonUIClass = b.getUI().getClass();
		return getDefaultButtonUIProperties(buttonUIClass).getLButtonProperties();
	}
	public static StateColor getDefaultUIStateBackground(AbstractButton b) {
		final Class<?> buttonUIClass = b.getUI().getClass();
		return getDefaultButtonUIProperties(buttonUIClass).getDefaultStateBackground();
	}
	public static StateColor getDefaultUIStateForeground(AbstractButton b) {
		final Class<?> buttonUIClass = b.getUI().getClass();
		return getDefaultButtonUIProperties(buttonUIClass).getDefaultStateForeground();
	}
	
	//
	// Init Buttons UI defaults 
	//
	
	protected static void initButtonsUIDefaults(UIDefaults table, String[] encodedFields) {
		for (int i = 0; i < LBUTTON_UIS.size(); i++) {
			ButtonUIProperties buttonUIProperties = getDefaultButtonUIProperties(LBUTTON_UIS.get(i));
			buttonUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
			buttonUIProperties.initComponentDefaults(table);
		}
	}
	
	//
	// Get button properties
	//
	
	public static LButtonProperties getButtonProperties(AbstractButton b) {
		return b instanceof LButtonPropertable ? ((LButtonPropertable) b).getButtonProperties() : getDefaultUIButtonProperties(b);
	}
	
	/*public static Color getBackground(AbstractButton b) {
		
		Color bgColor = b.getBackground();
		if (bgColor == null)
			bgColor = ColorsGUI.getFirstOpaqueParentBackground(b);
		
		//Cogemos el primer color que no sea un ReferencedColor
		int refs = 4;
		while (bgColor != null && bgColor instanceof ReferencedColor && refs > 0) {
			bgColor = ((ReferencedColor) bgColor).getColor();
			refs--; 
		}
		
		ButtonModel buttonModel = b.getModel();
		boolean isNormalBackground = bgColor != null && !(bgColor instanceof StateColor);
		Color background = getStateColorValue(buttonModel, bgColor);
		if ((background == null || isNormalBackground) && buttonHasState(buttonModel)) {
			StateColor defaultStateBackground = getDefaultUIStateBackground(b);
			StateColor newStateColor = new StateColor(defaultStateBackground, bgColor);
			background = ColorsGUI.getStateValue(newStateColor, buttonModel);
		}
		return background;
	}*/
	
	public static Color getBackground(AbstractButton b) {
		Color background =  UISupport.getBackground(b);
		if (b.isEnabled() && b.isFocusPainted() && b.hasFocus())
			background = Colors.darker(background, 0.08);
		
		return background;
	}
	
	public static Color getBackground(AbstractButton b, Color bgColor) {
		
		ButtonModel buttonModel = b.getModel();
		boolean isNormalBackground = bgColor != null && !(bgColor instanceof StateColor);
		Color background = getStateColorValue(buttonModel, bgColor);
		if ((background == null || isNormalBackground) && buttonHasState(buttonModel)) {
			StateColor defaultStateBackground = getDefaultUIStateBackground(b);
			StateColor newStateColor = new StateColor(defaultStateBackground, bgColor);
			background = ColorsGUI.getStateValue(newStateColor, buttonModel);
		}
		return background;
	}

	/*public static Color getForeground(AbstractButton b) {
		
		Color fgColor = b.getForeground();
		
		//Cogemos el primer color que no sea un ReferencedColor
		int refs = 4;
		while (fgColor != null && fgColor instanceof ReferencedColor && refs > 0) {
			fgColor = ((ReferencedColor) fgColor).getColor();
			refs--; 
		}
				
		ButtonModel buttonModel = b.getModel();
		boolean isNormalForeground = fgColor != null && !(fgColor instanceof StateColor);
		Color foreground = getStateColorValue(buttonModel, fgColor);
		if ((foreground == null || isNormalForeground) && buttonHasState(buttonModel)) {
			StateColor defaultStateForeground = getDefaultUIStateForeground(b);
			StateColor newStateColor = new StateColor(defaultStateForeground, fgColor);
			foreground = ColorsGUI.getStateValue(newStateColor, buttonModel);
		}
		return foreground;
	}*/
	
	public static Color getForeground(AbstractButton b) {
		return UISupport.getForeground(b);
	}
	public static Color getForeground(AbstractButton b, Color fgColor) {
		
		ButtonModel buttonModel = b.getModel();
		boolean isNormalForeground = fgColor != null && !(fgColor instanceof StateColor);
		Color foreground = getStateColorValue(buttonModel, fgColor);
		if ((foreground == null || isNormalForeground) && buttonHasState(buttonModel)) {
			StateColor defaultStateForeground = getDefaultUIStateForeground(b);
			StateColor newStateColor = new StateColor(defaultStateForeground, fgColor);
			foreground = ColorsGUI.getStateValue(newStateColor, buttonModel);
		}
		return foreground;
	}
	
	public static Color getStateColorValue(ButtonModel buttonModel, Color buttonColor) {
		
		Color color = buttonColor;
		
		if (buttonModel != null) {
			
			if (buttonColor != null && buttonColor instanceof StateColor) {
				StateColor stateColor = (StateColor) buttonColor;
				color = ColorsGUI.getStateValue(stateColor, buttonModel);
			}
		}
		return color;
	}

	public static Icon getStateIconValue(ButtonModel buttonModel, Icon buttonIcon) {
		
		Icon icon = buttonIcon;
		
		if (buttonModel != null) {
			
			if (buttonIcon != null && buttonIcon instanceof StateIcon) {
				StateIcon stateIcon = (StateIcon) buttonIcon;
				stateIcon.getStateValue(buttonModel);
			}
		}
		return icon;
	}
	
	public static Icon getIcon(AbstractButton b) {
		
		ButtonModel buttonModel = b.getModel();
		Icon buttonIcon = getIconButtonValue(b);
		Icon icon = getStateIconValue(buttonModel, buttonIcon);
		
		return icon;
	}
	public static Icon getIconButtonValue(AbstractButton b) {
		
		ButtonModel model = b.getModel();
		Icon icon = null;
		if (!model.isEnabled()) {
			icon = b.getDisabledIcon();
		}
		else if (model.isPressed() && model.isArmed()) {
			icon = b.getPressedIcon();
			if (icon == null)
				icon = b.getIcon();
		}
		else if (b.isRolloverEnabled() && model.isRollover()) {
			icon = b.getRolloverIcon();
		}
	
		if (icon == null && model.isSelected())
			icon = b.getSelectedIcon();
		
		if (icon == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0)
			icon = b.getIcon();
		
		return icon;
	}

	public static boolean buttonHasState(ButtonModel buttonModel) {
		
		boolean disabled = !buttonModel.isEnabled();
		boolean pressed = buttonModel.isPressed() && buttonModel.isArmed();
		boolean rollover = buttonModel.isRollover();
		boolean selected = buttonModel.isSelected();
		
		return disabled || pressed || rollover || selected;
	}
	
	//
	// Get preferredSize
	//
	
	/*public static Dimension getPreferredSize(AbstractButton b) {
		
		String texto = b.getText();
		if (texto == null)
			texto = Constants.VOID;
			
		LButtonProperties buttonProperties = getButtonProperties(b);
		String[] lineas = UtilsGUI.getLineas(texto);

		Icon icon = getIcon(b);
		boolean ignoreIconHeight = buttonProperties.isIgnoreIconHeight();
		//Usamos un icono virtual con height del tamaño de la fuente para que en caso de que sea mas alto no afecte al preferredHeight del botón
		Icon fixedIcon = ignoreIconHeight ? Iconos.getFixedHeithIcon(icon, b.getFont().getSize()) : icon;
		
		int textIconGap = b.getIconTextGap();
		Dimension preferredSize = lineas.length > 0 ? UtilsGUI.getPreferredSize(b, lineas, fixedIcon, textIconGap) : BasicGraphicsUtils.getPreferredButtonSize(b, textIconGap);
		
		//Si cambiamos el borde del botón no funcionará el margin, por lo que lo añadimos aquí
		boolean addMarginManually = !UtilsGUI.isMarginCompatibleBorder(b.getBorder());
		
		if (addMarginManually) {
			Insets margin = b.getMargin();
			if (margin != null) {
				preferredSize.width = preferredSize.width + margin.left + margin.right;
				preferredSize.height = preferredSize.height + margin.top + margin.bottom;
			}
		}
		
		boolean respectMaxMinSize = buttonProperties.isRespectMaxMinSize();
		if (respectMaxMinSize) {
			
			Dimension minSize = b.isMinimumSizeSet() ? b.getMinimumSize() : new Dimension(0, 0);
			Dimension maxSize = b.isMaximumSizeSet() ? b.getMaximumSize() : new Dimension(0, 0);
			
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
	}*/

	public static Dimension getPreferredSize(AbstractButton b) {
		
		Icon icon = getIcon(b);
		String text = b.getText();
		if (text == null)
			text = Constants.VOID;
		
		int	horizontalTextPosition = b.getHorizontalTextPosition();			
		int	verticalTextPosition = b.getVerticalTextPosition();			
		int verticalAlignment = b.getVerticalAlignment();
		int horizontalAlignment = b.getHorizontalAlignment();
		
		int textIconGap = b.getIconTextGap();
		
		LButtonProperties buttonProperties = getButtonProperties(b);
		boolean ignoreIconHeight = buttonProperties.isIgnoreIconHeight();
		boolean respectMaxMinSize = buttonProperties.isRespectMaxMinSize();
		
		return UISupport.getPreferredSize(b, text, icon, textIconGap, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, ignoreIconHeight, respectMaxMinSize);
	}
	
	//
	// Painting components
	//
	
	/*public static void paintTextLine(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		
		ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);
        int mnemonicIndex = b.getDisplayedMnemonicIndex();
        
        int xText = textRect.x;
        int yText = textRect.y + fm.getAscent();
        
        Color background = getBackground(b);
        if (background == null)
        	background = ColorsGUI.getFirstOpaqueParentBackground(b);
        
        Color foreground = getForeground(b);
        Color shadowColor = null;
        int shadowPosition = -1;
        
        // Draw the Text //
        if(model.isEnabled()) {
        	
        	LButtonProperties buttonProperties = getButtonProperties(b);
    		int textShiftOffset = getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
        	
        	xText = xText + textShiftOffset;
            yText = yText + textShiftOffset;
            
        	boolean isShadowTextEnabled = isShadowTextEnabled(model, buttonProperties.getShadowTextMode(), foreground);
            if (isShadowTextEnabled) {
            	//// paint the text shadow ////
            	 shadowColor = Colors.darker(background, 0.7);
            	 shadowPosition = getShadowPosition(buttonProperties, model);
            }
        }
        else {
            //// paint the text disabled shadow ////
        	boolean backgroundIsDark = Colors.esColorOscuro(background);
        	shadowColor = backgroundIsDark ? Colors.darker(background, 0.7) : Colors.brighter(background, 0.7);
            shadowPosition = SwingConstants.NORTH_WEST;
            
            if (foreground != null && foreground.equals(b.getForeground())) {
	       		 //No se ha definido un disabledBackground, así que lo calculamos
	       		 foreground = backgroundIsDark ? Colors.brighter(background, 0.32) : Colors.darker(background, 0.32);
	       	}
            //PRUEBAS
            //shadowPosition = SwingConstants.NORTH;
            //shadowColor = Colors.brighter(background, 0.5);
            foreground = backgroundIsDark ? Colors.brighter(background, 0.22) : Colors.darker(background, 0.32);
            shadowColor = backgroundIsDark ? Colors.darker(background, 0.1) : Colors.brighter(background, 0.1);
        }
        
        g.setFont(b.getFont());
        if (shadowColor != null && shadowPosition != -1) {
        	//// paint the text shadow ////
        	 g.setColor(shadowColor);
        	 Point shadowLocation = getShadowLocation(new Point(xText, yText), shadowPosition);
        	 //SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, shadowLocation.x, shadowLocation.y);
        	//SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, xText, yText-1);
        	 UtilsGUI.drawString(g,text, shadowLocation.x, shadowLocation.y);
        }
                	
        //// paint the text normally /////
        g.setColor(foreground);
        //SwingUtilities2.drawStringUnderlineCharAt(c, g,text, mnemonicIndex, xText, yText);
        UtilsGUI.drawString(g,text, xText, yText);
	}
	*/
	/*
	public static void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {

		String[] lineas = UtilsGUI.getLineas(b.getText());

		if (lineas.length <= 1) {

			paintTextLine(g, b, textRect, text);
		}
		else {

			int	horizontalTextPosition = b.getHorizontalTextPosition();			
			int	verticalTextPosition = b.getVerticalTextPosition();			
			int verticalAlignment = b.getVerticalAlignment();
			
			//Pintamos el icono relativo al texto cuando el horizontalTextPosition sea CENTER, sino se pintará en paintIcon(...)
			Icon icono = horizontalTextPosition == SwingConstants.CENTER ? b.getIcon() : null;
			
			Rectangle textRectMulti = null;
			for (int i = 0; i < lineas.length; i++) {

				Rectangle textRectLinea = new Rectangle();
				String texto = layoutCompoundLabel(g, b, lineas[i], textRectLinea, i, lineas.length);
				
				if (b instanceof JMenuItem) {
					//En los menuItem respetamos la posición x precalculada, ya que es relativa al resto de menuItems
					textRectLinea.x = textRect.x;
				}
				
				if (icono != null && verticalTextPosition != SwingConstants.CENTER) {
					
					if (verticalAlignment == SwingConstants.CENTER) {
						
						int ajusteVertical = (icono.getIconHeight() / 2) + b.getIconTextGap();
						if (verticalTextPosition == SwingConstants.TOP) {
							textRectLinea.y = textRectLinea.y - ajusteVertical;
						}
						else if (verticalTextPosition == SwingConstants.BOTTOM) {
							textRectLinea.y = textRectLinea.y + ajusteVertical;
						}
					}
					else if (verticalAlignment == SwingConstants.BOTTOM && verticalTextPosition == SwingConstants.TOP) {
						
						int ajusteVertical = icono.getIconHeight() + b.getIconTextGap();
						textRectLinea.y = textRectLinea.y - ajusteVertical;
					}
				}
				paintTextLine(g, b, textRectLinea, texto);
				
				textRectMulti = textRectMulti == null ? textRectLinea : textRectMulti.union(textRectLinea);
			}
			
			if (icono != null) {
				
				Rectangle iconRect = UtilsGUI.getIconRectRelative(b, textRectMulti);
				UISupportButtons.paintIcon(g, b, iconRect);
			}
		}
	}
	
	public static void paintTextLine(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		
		ButtonModel model = b.getModel();
        boolean enabled = model.isEnabled();
        int textShiftOffset = 0;
        int mnemonicIndex = b.getDisplayedMnemonicIndex();
        boolean isShadowTextEnabled = false;
        int shadowPosition = -1;
        
        if (enabled) {
        	
        	LButtonProperties buttonProperties = getButtonProperties(b);
    		textShiftOffset = getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
        	
        	isShadowTextEnabled = isShadowTextEnabled(b, buttonProperties.getShadowTextMode());
            if (isShadowTextEnabled) {
            	shadowPosition = getShadowPosition(buttonProperties, model);
            }
        }
        
        UISupportAux.paintTextLine(g, b, textRect, text, enabled, textShiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition);
	}
	*/
	/*public static void paintIcon(Graphics g, AbstractButton b, Rectangle iconRect) {

		LButtonProperties buttonProperties = getButtonProperties(b);
		Icon icon = getIcon(b);
		
		if (icon != null) {
			
			ButtonModel model = b.getModel();
			Color foreground = getForeground(b);
			Icon origIcon = icon;
			if (buttonProperties.isIconForegroundEnabled() && icon instanceof ImageIcon) {
				//NOTA: Crear un StateIcon para no tener que calcular la imagen colorizada tantas veces
				Image originalImage = Iconos.getImage(icon, b);
				//Oscurecemos primero la imagen para que luego se adapte bien a cualquier color (sólo si el color final no es demasiado claro)
				boolean obscureImageFirst = Colors.getLuminance(foreground) < 0.9;
				Image colorizedImage = Iconos.createColorizedImage(originalImage, foreground, obscureImageFirst);
				icon = new ImageIcon(colorizedImage);
			}
			
			int iconShiftOffset = getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
			int x = iconRect.x + iconShiftOffset;
			int y = iconRect.y + iconShiftOffset;
			
			boolean isShadowTextEnabled = isShadowTextEnabled(model, buttonProperties.getShadowTextMode(), foreground);
            if (isShadowTextEnabled && buttonProperties.isIconForegroundEnabled() && icon instanceof ImageIcon) {
            	Color shadowColor = Colors.darker(getBackground(b), 0.7);
            	shadowColor = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 255);
            	Image colorizedImage = Iconos.createColorizedImage(Iconos.getImage(origIcon, b), shadowColor);
            	Icon shadowIcon = new ImageIcon(colorizedImage);
            	int shadowPosition = getShadowPosition(buttonProperties, model);
           	 	Point shadowLocation = getShadowLocation(new Point(x, y), shadowPosition);
				shadowIcon.paintIcon(b, g, shadowLocation.x, shadowLocation.y);
            }
            
           icon.paintIcon(b, g, x, y);
		}
	}
	*/
	public static void paintIcon(Graphics g, AbstractButton b, Rectangle iconRect) {

		Icon icon = getIcon(b);
		
		if (icon != null) {
		
			LButtonProperties buttonProperties = getButtonProperties(b);
        	ButtonModel model = b.getModel();
			
			int iconShiftOffset = 0;
	        boolean isShadowTextEnabled = false;
	        int shadowPosition = -1;
	        
	        if (model.isEnabled()) {
	        	
	        	iconShiftOffset = getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
	        	
	        	isShadowTextEnabled = isShadowTextEnabled(b, buttonProperties.getShadowTextMode());
	            if (isShadowTextEnabled) {
	            	shadowPosition = getShadowPosition(buttonProperties, model);
	            }
	        }
	        
			boolean iconForegroundEnabled = buttonProperties.isIconForegroundEnabled();
			 
			UISupport.paintIcon(g, b, iconRect, icon, iconShiftOffset, iconForegroundEnabled, isShadowTextEnabled, shadowPosition);
		}
	}
	
	public static boolean isBackgroundPainted(AbstractButton b) {
		
		boolean backgroundPainted = b.isOpaque();
		if (!backgroundPainted) {
			if (!(b instanceof JCheckBox) && !(b instanceof JRadioButton) && getButtonProperties(b).isPaintBgEffectsWhenTransparent()) {
				
				ButtonModel model = b.getModel();
				
				boolean isToggleButton = b instanceof JToggleButton;
						
				boolean isPressed = !isToggleButton && model.isPressed() && model.isArmed();
				boolean isRollover = model.isRollover() && b.isRolloverEnabled();
				boolean isSelected = model.isSelected();
				
				backgroundPainted = isPressed || isRollover || isSelected;
			}
		}
		
		return backgroundPainted;
	}
	
	public static void paintButtonBackground(Graphics g, AbstractButton b) {
		paintButtonBackground(g, b, isBackgroundPainted(b));
	}
	
	public static void paintButtonBackground(Graphics g, AbstractButton b, boolean paintBackground) {

		LButtonProperties buttonProperties = getButtonProperties(b);
		
		if (paintBackground) {
			Color color = getBackground(b);
			int gradientType = buttonProperties.isGradientBackgroundEnabled() ? GraphicsUtils.GRADIENT_TYPE_VERTICAL : GraphicsUtils.GRADIENT_TYPE_NONE;
			
			g.setColor(color);
			Rectangle bgRects = UISupportUtils.getBackgroundRects(b);
			GraphicsUtils.fillRect(g, bgRects, gradientType);
		}
		
		Color lineBackgroundColor = buttonProperties.getLineBackgroundColor();
		if (lineBackgroundColor != null)
			GraphicsUtils.paintLineBackground(g, b, lineBackgroundColor);
	}
	
	public static void paintTextClassicAndIcon(Graphics g, AbstractButton b, Rectangle textRect, String clippedText) {

		
		ButtonModel model = b.getModel();
		LButtonProperties buttonProperties = getButtonProperties(b);
 
		String[] lineas = Strings.getLines(b.getText());
		String text = lineas.length > 1 ? b.getText() : clippedText;
		Icon icon = getIcon(b);
		boolean iconForegroundEnabled = false;
		int iconTextGap = 0;
		if (icon != null) {
			iconForegroundEnabled = buttonProperties.isIconForegroundEnabled();
			iconTextGap = b.getIconTextGap();
		}
		
		boolean enabled = model.isEnabled();
        int shiftOffset = 0;
        int mnemonicIndex = b.getDisplayedMnemonicIndex();
        boolean isShadowTextEnabled = false;
        int shadowPosition = -1;
        
        if (enabled) {
        	
        	shiftOffset = getShiftOffset(model, buttonProperties.getPressedSelectedOffset());
        	
        	isShadowTextEnabled = isShadowTextEnabled(b, buttonProperties.getShadowTextMode());
            if (isShadowTextEnabled) {
            	shadowPosition = getShadowPosition(buttonProperties, model);
            }
        }
        
		/*int	horizontalTextPosition = b.getHorizontalTextPosition();			
		int	verticalTextPosition = b.getVerticalTextPosition();			
		int verticalAlignment = b.getVerticalAlignment();
		*/
		//UISupportAux.paintTextAndIcon(g, b, textRect, text, icon, enabled, iconTextGap, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, iconForegroundEnabled);
        UISupport.paintTextClassicAndIcon(g, b, textRect, text, icon, enabled, iconTextGap, shiftOffset, mnemonicIndex, isShadowTextEnabled, shadowPosition, iconForegroundEnabled);
	}
	
	public static int getShiftOffset(ButtonModel model, int pressedSelectedOffset) {
		return model.isSelected() || (model.isPressed() && model.isArmed()) ? pressedSelectedOffset : 0;
	}
	
	public static boolean isShadowTextEnabled(AbstractButton b, int shadowTextMode) {
		ButtonModel buttonModel = b.getModel();
		//Por rendimiento sólo obtenemos el foreground y background con SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND
		Color foreground = shadowTextMode == LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND ? getForeground(b) : null;
		Color background = shadowTextMode == LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND ? getBackground(b) : null;
		return isShadowTextEnabled(buttonModel, shadowTextMode, foreground, background);
	}
	public static boolean isShadowTextEnabled(ButtonModel buttonModel, int shadowTextMode, Color foreground, Color background) {
		
		boolean shadowTextEnabled;
		if (shadowTextMode == LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND) {
			float foregroundLuminance = Colors.getLuminance(foreground);
			float backgroundLuminance = Colors.getLuminance(background);
			//Si el fondo es muy oscuro no ponemos sombra ya que no se verá
			shadowTextEnabled = foregroundLuminance > 0.5 && backgroundLuminance > 0.2 || Math.abs(foregroundLuminance - backgroundLuminance) < 0.25;
		}
		else {
			shadowTextEnabled = shadowTextMode == LButtonProperties.SHADOW_TEXT_MODE_ALWAYS || (shadowTextMode == LButtonProperties.SHADOW_TEXT_MODE_ON_ROLLOVER && buttonModel.isRollover() && !buttonModel.isSelected());
		}
		return shadowTextEnabled;
	}
	
	public static int getShadowPosition(LButtonProperties buttonProperties, ButtonModel buttonModel) {
		int shadowPosition = buttonProperties.getShadowPosition();
		if (shadowPosition == -1) {
			//Si no se ha definido posición, cambiamos la sombra según sea rollover o no
			shadowPosition = buttonModel.isRollover() ? SwingConstants.NORTH_WEST : SwingConstants.SOUTH;
		}
		return shadowPosition;
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
	
	public static void uninstallButtonUIs() {
		UISupportButtons.getDefaultButtonUIPropertiesMap().clear();
		defaultButtonUIPropertiesMap = null;
	}
	
	protected static void encodeUIsFieldsChanged(StringBuffer sb) {
		
		HashMap<Class<?>, ButtonUIProperties> uisMap = getDefaultButtonUIPropertiesMap();
		for (Iterator<ButtonUIProperties> iterator = uisMap.values().iterator(); iterator.hasNext();) {
			ButtonUIProperties uiProp = iterator.next();
			uiProp.encodeFieldsChanged(sb);
		}
	}
	
	public static void updateUIsPropertiesFromEncodedFields(String... encodedFields) {
		
		for (int i = 0; i < LBUTTON_UIS.size(); i++) {
			ButtonUIProperties buttonUIProperties = getDefaultButtonUIProperties(LBUTTON_UIS.get(i));
			buttonUIProperties.getFieldsChangeSupport().resetDefaultFieldsValues();
			buttonUIProperties.updateUIPropertiesFromEncodedFields(encodedFields);
		}
	}
}
