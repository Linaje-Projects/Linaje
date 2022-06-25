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
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import linaje.gui.LButtonProperties;
import linaje.gui.LComponentBorder;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.FieldsChangeSupport;
import linaje.utils.Lists;
import linaje.utils.ReferencedColor;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.StateColor;
import linaje.utils.Utils;

/**
 * Los valores de propiedades que aquí se definan se iniciaran en
 * UIDefaults.put(K, V) para cada UI de
 * LinajeLookAndFeel.UI_LCOMPONENTS_BUTTONS_MAP
 * K = ComponentName.propertyName, V = value
 * Ej: 	UIDefaults.put(Button.background, Color.white)
 * 		UIDefaults.put(Button.lButtonProperties.iconForegroundEnabled, false)
 * Se podrán sobreescribir estos valores a través del
 * fichero de configuración de LinajeLookAndFeel (defaultUIConfig.cfg)
 * El fichero de configuración se puede editar a mano o ejecutando UIConfig.class
 */
public class ButtonUIProperties extends ComponentUIProperties {

	//Si se asigna un background o foreground normal (que no sea StateColor) al botón, obtendremos los colores de estado del defaultStateBackground y defaultStateForeground
	private StateColor defaultStateBackground = null;
	private StateColor defaultStateForeground = null;
	private boolean rollover = true;
	
	private LButtonProperties lButtonProperties = null;
	
	public static final String PROPERTY_defaultStateBackground = "defaultStateBackground";
	public static final String PROPERTY_defaultStateForeground = "defaultStateForeground";
	public static final String PROPERTY_lButtonProperties = "lButtonProperties";
	public static final String PROPERTY_rollover = "rollover";
	
	public ButtonUIProperties(Class<?> uiClass) {
		super(uiClass);
		try {
			initDefaultUIValues();
		} catch (Exception ex) {
			Console.printException(ex);
			initDefaultUIValuesAux();
		}
	}

	protected void initDefaultUIValues() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		lButtonProperties = new LButtonProperties();
		
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		
		//Hacemos la fuente de abstractbuttons un 15% mas grande de la fuente por defecto de la app
		font = Utils.getFontWithSize(generalUIProperties.getFontApp(), Math.round(generalUIProperties.getFontApp().getSize()*1.15f));
		
		final Color BUTTON_BACKGROUND = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorPanels)}, 0.025);
		final Color BUTTON_FOREGROUND = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorText)}, 0);
		
		//boolean isButtonOrToggle = getUiClass() == LButtonUI.class || getUiClass() == LToggleButtonUI.class;
		//boolean isMenuItem = !isButtonOrToggle && (getUiClass() == LMenuItemUI.class || getUiClass() == LCheckBoxMenuItemUI.class || getUiClass() == LRadioButtonMenuItemUI.class || getUiClass() == LMenuUI.class);
		//if (isButtonOrToggle || isMenuItem) {
		boolean isButton = getUiClass() == LButtonUI.class;
		boolean isMenuItem = getUiClass() == LMenuItemUI.class;
		boolean isToggle = getUiClass() == LToggleButtonUI.class;
		boolean isMenuItemOther = getUiClass() == LMenuItemUI.class || getUiClass() == LCheckBoxMenuItemUI.class || getUiClass() == LRadioButtonMenuItemUI.class || getUiClass() == LMenuUI.class;
		
		if (isButton || isMenuItem) {
				
			final Color BUTTON_BACKGROUND_SELECTED =  isMenuItem ? null : new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorApp)}, 0.27);
			final Color BUTTON_BACKGROUND_ROLLOVER = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorRolloverDark)}, 0);
			final Color BUTTON_BACKGROUND_ROLLOVER_SELECTED =  isMenuItem ? null : new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorApp)}, 0.37);
			
			final StateColor BUTTON_BACKGROUND_COLORS = new StateColor(BUTTON_BACKGROUND, null, BUTTON_BACKGROUND_SELECTED, BUTTON_BACKGROUND_ROLLOVER, BUTTON_BACKGROUND_ROLLOVER_SELECTED, null, null, null);
			
			final Color BUTTON_FOREGROUND_SELECTED = isMenuItem ? null : new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorTextBrightest)}, 0);
			final Color BUTTON_FOREGROUND_ROLLOVER = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorTextBrightest)}, 0);
			
			final StateColor BUTTON_FOREGROUND_COLORS = new StateColor(BUTTON_FOREGROUND, null, BUTTON_FOREGROUND_SELECTED, BUTTON_FOREGROUND_ROLLOVER, null, null, null, null);
			
			border = new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder());
			if (isButton)
				font = Utils.getFontWithStyle(font, Font.BOLD);
			
			defaultStateBackground = BUTTON_BACKGROUND_COLORS;
			defaultStateForeground = BUTTON_FOREGROUND_COLORS;
			
			//background = BUTTON_BACKGROUND_COLORS;
			//foreground = BUTTON_FOREGROUND_COLORS;
			background = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_defaultStateBackground)}, 0);
			foreground = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_defaultStateForeground)}, 0);
			
			//int shadowTextMode = uiClass == LButtonUI.class ? LButtonProperties.SHADOW_TEXT_MODE_ON_ROLLOVER : LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND;
			int shadowTextMode = LButtonProperties.SHADOW_TEXT_MODE_ON_ROLLOVER;//LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND;
			lButtonProperties.setShadowTextMode(shadowTextMode);
			
			final Color MARK_COLOR = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorApp)}, 0.27);
			final Color MARK_COLOR_ROLLOVER = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorApp)}, 0);
			
			final StateColor MARK_COLORS = new StateColor(MARK_COLOR, null, null, MARK_COLOR_ROLLOVER, null, null, null, null);
			lButtonProperties.setMarkColor(MARK_COLORS);
		}
		else if (isToggle || isMenuItemOther) {
			
			Field defaultStateBackgroundField = getClass().getDeclaredField(PROPERTY_defaultStateBackground);
			Field defaultStateForegroundField = getClass().getDeclaredField(PROPERTY_defaultStateForeground);
			
			ButtonUIProperties uiPropertiesOrigin = UISupportButtons.getDefaultButtonUIProperties(isToggle ? LButtonUI.class : LMenuItemUI.class);
			defaultStateBackground = StateColor.getReferencedStateColor(uiPropertiesOrigin.getDefaultStateBackground(), defaultStateBackgroundField, uiPropertiesOrigin);
			defaultStateForeground = StateColor.getReferencedStateColor(uiPropertiesOrigin.getDefaultStateForeground(), defaultStateForegroundField, uiPropertiesOrigin);
			
			background = new ReferencedColor(uiPropertiesOrigin, new Field[]{ComponentUIProperties.class.getDeclaredField(PROPERTY_background)}, 0);
			foreground = new ReferencedColor(uiPropertiesOrigin, new Field[]{ComponentUIProperties.class.getDeclaredField(PROPERTY_foreground)}, 0);
			
			lButtonProperties.setShadowTextMode(uiPropertiesOrigin.getLButtonProperties().getShadowTextMode());
			border = new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder());
			if (isToggle)
				font = Utils.getFontWithStyle(font, Font.BOLD);
			
			Field lButtonPropertiesField = getClass().getDeclaredField(PROPERTY_lButtonProperties);
			Field markColorField = LButtonProperties.class.getDeclaredField(LButtonProperties.PROPERTY_markColor);
			Color markColor = new ReferencedColor(UISupportButtons.getDefaultButtonUIProperties(LButtonUI.class), new Field[]{lButtonPropertiesField, markColorField}, 0);
			lButtonProperties.setMarkColor(markColor);
		}
		else if (getUiClass() == LRadioButtonUI.class) {
			
			final Color RADIOBUTTON_BACKGROUND = BUTTON_BACKGROUND;
			final Color RADIOBUTTON_BACKGROUND_ROLLOVER = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorRollover)}, 0);
			
			final Color RADIOBUTTON_FOREGROUND = BUTTON_FOREGROUND;
			final Color RADIOBUTTON_FOREGROUND_ROLLOVER = new ReferencedColor(GeneralUIProperties.getInstance(), new Field[]{GeneralUIProperties.class.getDeclaredField(GeneralUIProperties.PROPERTY_colorApp)}, 0.27);
			
			final StateColor RADIOBUTTON_BACKGROUND_COLORS = new StateColor(RADIOBUTTON_BACKGROUND, null, null, RADIOBUTTON_BACKGROUND_ROLLOVER, null, null, null, null);
			final StateColor RADIOBUTTON_FOREGROUND_COLORS = new StateColor(RADIOBUTTON_FOREGROUND, null, null, RADIOBUTTON_FOREGROUND_ROLLOVER, null, null, null, null);
			final Color RADIOBUTTON_MARK_COLOR = RADIOBUTTON_FOREGROUND_ROLLOVER;
			
			border = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createEmptyBorder(), new MarginBorder());
			
			background = RADIOBUTTON_BACKGROUND_COLORS;
			defaultStateBackground = RADIOBUTTON_BACKGROUND_COLORS;
			foreground = RADIOBUTTON_FOREGROUND_COLORS;
			defaultStateForeground = RADIOBUTTON_FOREGROUND_COLORS;
			
			lButtonProperties.setMarkColor(RADIOBUTTON_MARK_COLOR);
			lButtonProperties.setPressedSelectedOffset(0);
		}
		else {//LCheckBox
			
			Field defaultStateBackgroundField = getClass().getDeclaredField(PROPERTY_defaultStateBackground);
			Field defaultStateForegroundField = getClass().getDeclaredField(PROPERTY_defaultStateForeground);
			
			ButtonUIProperties uiPropertiesOrigin = UISupportButtons.getDefaultButtonUIProperties(LRadioButtonUI.class);
			defaultStateBackground = StateColor.getReferencedStateColor(uiPropertiesOrigin.getDefaultStateBackground(), defaultStateBackgroundField, uiPropertiesOrigin);
			defaultStateForeground = StateColor.getReferencedStateColor(uiPropertiesOrigin.getDefaultStateForeground(), defaultStateForegroundField, uiPropertiesOrigin);
			
			background = new ReferencedColor(uiPropertiesOrigin, new Field[]{ComponentUIProperties.class.getDeclaredField(PROPERTY_background)}, 0);
			foreground = new ReferencedColor(uiPropertiesOrigin, new Field[]{ComponentUIProperties.class.getDeclaredField(PROPERTY_foreground)}, 0);
			
			border = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createEmptyBorder(), new MarginBorder());
			lButtonProperties.setPressedSelectedOffset(uiPropertiesOrigin.getLButtonProperties().getPressedSelectedOffset());
						
			Field lButtonPropertiesField = getClass().getDeclaredField(PROPERTY_lButtonProperties);
			Field markColorField = LButtonProperties.class.getDeclaredField(LButtonProperties.PROPERTY_markColor);
			Color markColor = new ReferencedColor(uiPropertiesOrigin, new Field[]{lButtonPropertiesField, markColorField}, 0);
			lButtonProperties.setMarkColor(markColor);
		}
		
		lButtonProperties.getFieldsChangeSupport().getFieldsChangedValues().clear();
	}
	private void initDefaultUIValuesAux() {
		
		lButtonProperties = new LButtonProperties();
		
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		
		font = Utils.getFontWithSize(generalUIProperties.getFontApp(), generalUIProperties.getFontApp().getSize()+2);
		
		final Color BUTTON_BACKGROUND = Colors.darker(generalUIProperties.getColorPanels(), 0.025);
		final Color BUTTON_FOREGROUND = generalUIProperties.getColorText();
		
		if (getUiClass() == LButtonUI.class || getUiClass() == LToggleButtonUI.class
		 || getUiClass() == LMenuItemUI.class || getUiClass() == LCheckBoxMenuItemUI.class || getUiClass() == LRadioButtonMenuItemUI.class) {
			
			final Color BUTTON_BACKGROUND_SELECTED = Colors.darker(generalUIProperties.getColorApp(), 0.27);
			final Color BUTTON_BACKGROUND_ROLLOVER = generalUIProperties.getColorRolloverDark();
			final Color BUTTON_BACKGROUND_ROLLOVER_SELECTED = Colors.darker(BUTTON_BACKGROUND_SELECTED, 0.10);
			
			final StateColor BUTTON_BACKGROUND_COLORS = new StateColor(BUTTON_BACKGROUND, null, BUTTON_BACKGROUND_SELECTED, BUTTON_BACKGROUND_ROLLOVER, BUTTON_BACKGROUND_ROLLOVER_SELECTED, null, null, null);
			
			final Color BUTTON_FOREGROUND_SELECTED = generalUIProperties.getColorTextBrightest();
			final Color BUTTON_FOREGROUND_ROLLOVER = generalUIProperties.getColorTextBrightest();
			
			final StateColor BUTTON_FOREGROUND_COLORS = new StateColor(BUTTON_FOREGROUND, null, BUTTON_FOREGROUND_SELECTED, BUTTON_FOREGROUND_ROLLOVER, null, null, null, null);
			
			border = new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder());
			font = Utils.getFontWithStyle(font, Font.BOLD);
			
			background = BUTTON_BACKGROUND_COLORS;
			defaultStateBackground = BUTTON_BACKGROUND_COLORS;
			
			foreground = BUTTON_FOREGROUND_COLORS;
			defaultStateForeground = BUTTON_FOREGROUND_COLORS;
			
			//int shadowTextMode = uiClass == LButtonUI.class ? LButtonProperties.SHADOW_TEXT_MODE_ON_ROLLOVER : LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND;
			int shadowTextMode = LButtonProperties.SHADOW_TEXT_MODE_ON_BRIGHT_FOREGROUND;
			lButtonProperties.setShadowTextMode(shadowTextMode);
		}
		else {
			
			final Color CHECKBOX_BACKGROUND = BUTTON_BACKGROUND;
			final Color CHECKBOX_BACKGROUND_ROLLOVER = generalUIProperties.getColorRollover();
			
			final Color CHECKBOX_FOREGROUND = BUTTON_FOREGROUND;
			final Color CHECKBOX_FOREGROUND_ROLLOVER = Colors.darker(generalUIProperties.getColorApp(), 0.27);//=BUTTON_BACKGROUND_SELECTED
			
			final StateColor CHECKBOX_BACKGROUND_COLORS = new StateColor(CHECKBOX_BACKGROUND, null, null, CHECKBOX_BACKGROUND_ROLLOVER, null, null, null, null);
			final StateColor CHECKBOX_FOREGROUND_COLORS = new StateColor(CHECKBOX_FOREGROUND, null, null, CHECKBOX_FOREGROUND_ROLLOVER, null, null, null, null);
			final Color CHECKBOX_MARK_COLOR = CHECKBOX_FOREGROUND_ROLLOVER;
			
			border = new BorderUIResource.CompoundBorderUIResource(BorderFactory.createEmptyBorder(), new MarginBorder());
			
			background = CHECKBOX_BACKGROUND_COLORS;
			defaultStateBackground = CHECKBOX_BACKGROUND_COLORS;
			foreground = CHECKBOX_FOREGROUND_COLORS;
			defaultStateForeground = CHECKBOX_FOREGROUND_COLORS;
			
			lButtonProperties.setMarkColor(CHECKBOX_MARK_COLOR);
			lButtonProperties.setPressedSelectedOffset(0);
		}
		
		lButtonProperties.getFieldsChangeSupport().getFieldsChangedValues().clear();
	}
	
	protected void initComponentDefaults(UIDefaults table) {
		
		//init ComponentUIProperties fields
		super.initComponentDefaults(table);
		
		//init ButtonUIProperties fields
		Field[] fields = ReflectAccessSupport.filterFields(getClass().getDeclaredFields(), null, false, LButtonProperties.class);
		String prefix = UISupport.getPropertyPrefix(getUiClass());
		initUIComponentDefaultsFromFields(table, prefix, fields, this);
		
		//init LButtonProperties fields
		fields = ReflectAccessSupport.filterFields(getLButtonProperties().getClass().getDeclaredFields(), null, false, FieldsChangeSupport.class);
		prefix = prefix + PROPERTY_lButtonProperties + Constants.POINT;
		initUIComponentDefaultsFromFields(table, prefix, fields, getLButtonProperties());
	}
	
	private static void initUIComponentDefaultsFromFields(UIDefaults table, String prefix, Field[] fields, Object source) {
		
		ReflectAccessSupport reflectAccess = new ReflectAccessSupport(source);
		
		List<Object> defaults = Lists.newList();
		for (int i = 0; i < fields.length; i++) {
			try {
				
				Field field = fields[i];
				int fieldModifiers = field.getModifiers();
				if (!Modifier.isFinal(fieldModifiers) && !Modifier.isStatic(fieldModifiers)) {
					String fieldName = field.getName();
					String defaultKey = prefix + fieldName;
					Object value = reflectAccess.getFieldValue(field, false);
					defaults.add(defaultKey);
					defaults.add(LinajeLookAndFeel.createResourceValue(value));
				}
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		
		if (!defaults.isEmpty())
			table.putDefaults(Lists.listToArray(defaults, Object.class));
	}
	
	//
	// GETTERS
	//
	
	public StateColor getDefaultStateBackground() {
		return defaultStateBackground;
	}
	public StateColor getDefaultStateForeground() {
		return defaultStateForeground;
	}
	public LButtonProperties getLButtonProperties() {
		return lButtonProperties;
	}
	public boolean isRollover() {
		return rollover;
	}
	
	//
	// SETTERS
	//
	
	public void setDefaultStateBackground(StateColor defaultStateBackground) {
		StateColor oldValue = this.defaultStateBackground;
		StateColor newValue = defaultStateBackground;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.defaultStateBackground = defaultStateBackground;
			firePropertyChange(PROPERTY_defaultStateBackground, oldValue, newValue);
		}
	}
	public void setDefaultStateForeground(StateColor defaultStateForeground) {
		StateColor oldValue = this.defaultStateForeground;
		StateColor newValue = defaultStateForeground;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.defaultStateForeground = defaultStateForeground;
			firePropertyChange(PROPERTY_defaultStateForeground, oldValue, newValue);
		}
	}
	public void setlButtonProperties(LButtonProperties lButtonProperties) {
		LButtonProperties oldValue = this.lButtonProperties;
		LButtonProperties newValue = lButtonProperties;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.lButtonProperties = lButtonProperties;
			firePropertyChange(PROPERTY_lButtonProperties, oldValue, newValue);
		}
	}
	public void setRollover(boolean rollover) {
		boolean oldValue = this.rollover;
		boolean newValue = rollover;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.rollover = rollover;
			firePropertyChange(PROPERTY_rollover, oldValue, newValue);
		}
	}
	
	public void encodeFieldsChanged(StringBuffer sb) {
		
		//init from ComponentUIProperties fields
		super.encodeFieldsChanged(sb);
		
		//init from ButtonUIProperties fields
		String prefix = UISupport.getPropertyPrefix(getUiClass());
		getFieldsChangeSupport().encodeFieldsChanged(sb, prefix);
		
		//init from LButtonProperties fields
		prefix = prefix + PROPERTY_lButtonProperties + Constants.POINT;
		getLButtonProperties().getFieldsChangeSupport().encodeFieldsChanged(sb, prefix);
	}
}
