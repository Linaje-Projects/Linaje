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
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

import linaje.App;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.FieldsChangeSupport;
import linaje.utils.FieldsChangesNotifier;
import linaje.utils.LFont;
import linaje.utils.ReferencedColor;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.Utils;

public class GeneralUIProperties implements FieldsChangesNotifier {

	//Colors obligatorios que si no están definidos o se ponen a null hay que inicializarlos a algo
	private static final Color DEF_COLOR_APP = new Color(137,180,216);
	private static final Color DEF_COLOR_APP_DARK = new Color(64, 64, 64);
	private static final Color DEF_COLOR_PANELS = new Color(246,246,246);
	private static final Color DEF_COLOR_IMPORTANT = new Color(255,157,28);
	private static final Color DEF_COLOR_INFO = new Color(138,205,222);
	private static final Color DEF_COLOR_WARNING = new Color(255,205,0);
	private static final Color DEF_COLOR_POSITIVE = new Color(168,201,95);
	private static final Color DEF_COLOR_NEGATIVE = new Color(181,3,2);
	private static final Color DEF_COLOR_SHADOW = Color.black;
	private static final LFont DEF_APP_FONT = new LFont("Dialog", Font.PLAIN, 12);
	
	private Color colorApp = null;
	private Color colorAppDark = null;
	
	private Color colorPanels = null;
	private Color colorPanelsBright = null;
	private Color colorPanelsBrightest = null;
	private Color colorPanelsDark = null;
	private Color colorPanelsDarkest = null;
	
	private Color colorImportant = null;
	private Color colorInfo = null;
	private Color colorWarning = null;
	private Color colorPositive = null;
	private Color colorNegative = null;
	
	private Color colorText = null;
	private Color colorTextBright = null;
	private Color colorTextBrightest = null;
	
	private Color colorBorder = null;
	private Color colorBorderBright = null;
	private Color colorBorderDark = null;
	
	private Color colorRollover = null;
	private Color colorRolloverDark = null;
	
	private Color colorShadow = null;
			
	private LFont fontApp = null;
	
	private FieldsChangeSupport fieldsChangeSupport = null;
	
	protected static final String PREFIX = "General";
	public static final String PROPERTY_colorApp = "colorApp";
	public static final String PROPERTY_colorAppDark = "colorAppDark";
	public static final String PROPERTY_colorPanels = "colorPanels";
	public static final String PROPERTY_colorPanelsBright = "colorPanelsBright";
	public static final String PROPERTY_colorPanelsBrightest = "colorPanelsBrightest";
	public static final String PROPERTY_colorPanelsDark = "colorPanelsDark";
	public static final String PROPERTY_colorPanelsDarkest = "colorPanelsDarkest";
	public static final String PROPERTY_colorImportant = "colorImportant";
	public static final String PROPERTY_colorInfo = "colorInfo";
	public static final String PROPERTY_colorWarning = "colorWarning";
	public static final String PROPERTY_colorPositive = "colorPositive";
	public static final String PROPERTY_colorNegative = "colorNegative";
	public static final String PROPERTY_colorText = "colorText";
	public static final String PROPERTY_colorTextBright = "colorTextBright";
	public static final String PROPERTY_colorTextBrightest = "colorTextBrightest";
	public static final String PROPERTY_colorBorder = "colorBorder";
	public static final String PROPERTY_colorBorderBright = "colorBorderBright";
	public static final String PROPERTY_colorBorderDark = "colorBorderDark";
	public static final String PROPERTY_colorRollover = "colorRollover";
	public static final String PROPERTY_colorRolloverDark = "colorRolloverDark";
	public static final String PROPERTY_colorShadow = "colorShadow";
	public static final String PROPERTY_fontApp = "fontApp";
	
	public static final String UI_NAME = "General";
	
	GeneralUIProperties() {
		App.getMapObjectsByName().put(UI_NAME, this);
		initDefaultValues();
	}
	
	public static GeneralUIProperties getInstance() {
		return LinajeLookAndFeel.getInstance().getGeneralUIProperties();
	}
	
	public void initDefinedValues() {
		
	}
	public void initDefaultValues() {
		
		/*colorApp = new Color(196,214,0);
		colorAppDark = new Color(53,38,26);
		
		colorPanels = new Color(246,246,246);
		colorPanelsBright = new Color(251,251,251);
		colorPanelsBrightest = new Color(255,255,255);
		colorPanelsDark = new Color(101,101,93);
		colorPanelsDarkest = new Color(33,24,16);
		
		colorImportant = new Color(255,157,28);
		colorInfo = new Color(138,205,222);
		colorWarning = new Color(255,205,0);
		colorPositive = new Color(168,201,95);
		colorNegative = new Color(181,3,2);
		
		colorText = new Color(100,90,79);
		colorTextBright = new Color(136,139,141);
		colorTextBrightest = new Color(255,255,255);
		
		colorBorder =  new Color(192,192,192);
		colorBorderBright =  new Color(216,207,202);
		colorBorderDark =  new Color(160,160,160);
		
		colorRollover = new Color(209,191,173);
		colorRolloverDark = new Color(140,129,117);*/
		
		//fontApp = new LFont("Segoe Print", Font.PLAIN, 12);
		//fontApp = new Font("Segoe UI", Font.PLAIN, 12);
		//fontApp = new Font("MankSans-Medium", Font.PLAIN, 12);
		//fontApp = new Font("Leelawadee UI", Font.PLAIN, 12);
		//fontApp = new Font("Microsoft JhengHei UI", Font.PLAIN, 12);
		
	}
	
	//
	// PropertyChange field methods
	//
	
	public FieldsChangeSupport getFieldsChangeSupport() {
		if (fieldsChangeSupport == null)
			fieldsChangeSupport = new FieldsChangeSupport(this);
		return fieldsChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getFieldsChangeSupport().addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getFieldsChangeSupport().addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getFieldsChangeSupport().removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getFieldsChangeSupport().removePropertyChangeListener(propertyName, listener);
	}

	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getFieldsChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public void resetDefaultFieldsValues() {
		getFieldsChangeSupport().resetDefaultFieldsValues();
	}
	
	//
	// GETTERS
	//
	
	public Color getColorApp() {
		if (colorApp == null)
			colorApp = DEF_COLOR_APP;
		return colorApp;
	}
	public Color getColorAppDark() {
		if (colorAppDark == null)
			colorAppDark = DEF_COLOR_APP_DARK;
		return colorAppDark;
	}
	
	public Color getColorPanels() {
		if (colorPanels == null)
			colorPanels = DEF_COLOR_PANELS;
		return colorPanels;
	}
	public Color getColorPanelsBright() {
		if (colorPanelsBright == null) {
			try {
				colorPanelsBright = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, -0.03);
			} catch (Exception e) {
				colorPanelsBright = Colors.brighter(getColorPanels(), 0.03);
			}
		}
		return colorPanelsBright;
	}
	public Color getColorPanelsBrightest() {
		if (colorPanelsBrightest == null)
			colorPanelsBrightest = Color.white;
		return colorPanelsBrightest;
	}
	public Color getColorPanelsDark() {
		if (colorPanelsDark == null) {
			try {
				colorPanelsDark = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, 0.6);
			} catch (Exception e) {
				colorPanelsDark = Colors.darker(getColorPanels(), 0.6);
			}
		}
		return colorPanelsDark;
	}
	public Color getColorPanelsDarkest() {
		if (colorPanelsDarkest == null) {
			try {
				colorPanelsDarkest = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, 0.9);
			} catch (Exception e) {
				colorPanelsDarkest = Colors.darker(getColorPanels(), 0.9);
			}
		}
		return colorPanelsDarkest;
	}
	
	public Color getColorImportant() {
		if (colorImportant == null)
			colorImportant = DEF_COLOR_IMPORTANT;
		return colorImportant;
	}
	public Color getColorInfo() {
		if (colorInfo == null)
			colorInfo = DEF_COLOR_INFO;
		return colorInfo;
	}
	public Color getColorWarning() {
		if (colorWarning == null)
			colorWarning = DEF_COLOR_WARNING;
		return colorWarning;
	}
	public Color getColorPositive() {
		if (colorPositive == null)
			colorPositive = DEF_COLOR_POSITIVE;
		return colorPositive;
	}
	public Color getColorNegative() {
		if (colorNegative == null)
			colorNegative = DEF_COLOR_NEGATIVE;
		return colorNegative;
	}
	
	public Color getColorText() {
		if (colorText == null) {
			try {
				colorText = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanelsDarkest)}, 0);
			} catch (Exception e) {
				colorText = getColorPanelsDarkest();
			}
		}
		return colorText;
	}
	public Color getColorTextBright() {
		if (colorTextBright == null) {
			try {
				colorTextBright = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorText)}, -0.48);
			} catch (Exception e) {
				colorTextBright = Colors.brighter(getColorText(), 0.48);
			}
		}
		return colorTextBright;
	}
	public Color getColorTextBrightest() {
		if (colorTextBrightest == null)
			colorTextBrightest = Color.white;
		return colorTextBrightest;
	}
	
	public Color getColorBorder() {
		if (colorBorder == null) {
			try {
				colorBorder = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, 0.22);
			} catch (Exception e) {
				colorBorder = Colors.darker(getColorPanels(), 0.22);
			}
		}
		return colorBorder;
	}
	public Color getColorBorderBright() {
		if (colorBorderBright == null) {
			try {
				colorBorderBright = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, 0.15);
			} catch (Exception e) {
				colorBorderBright = Colors.darker(getColorPanels(), 0.15);
			}
		}
		return colorBorderBright;
	}
	public Color getColorBorderDark() {
		if (colorBorderDark == null) {
			try {
				colorBorderDark = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorPanels)}, 0.35);
			} catch (Exception e) {
				colorBorderDark = Colors.darker(getColorPanels(), 0.35);
			}
		}
		return colorBorderDark;
	}
	
	public Color getColorRollover() {
		if (colorRollover == null) {
			try {
				colorRollover = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorAppDark)}, -0.55);
			} catch (Exception e) {
				colorRollover = Colors.brighter(getColorAppDark(), 0.55);
			}
		}
		return colorRollover;
	}
	public Color getColorRolloverDark() {
		if (colorRolloverDark == null) {
			try {
				colorRolloverDark = new ReferencedColor(this, new Field[]{getClass().getDeclaredField(PROPERTY_colorRollover)}, 0.32);
			} catch (Exception e) {
				colorRolloverDark = Colors.darker(getColorRollover(), 0.32);
			}
		}
		return colorRolloverDark;
	}
	
	public Color getColorShadow() {
		if (colorShadow == null)
			colorShadow = DEF_COLOR_SHADOW;
		return colorShadow;
	}
	
	public LFont getFontApp() {
		if (fontApp == null)
			fontApp = DEF_APP_FONT;
		return fontApp;
	}
	
	//
	// SETTERS
	//
	
	public void setColorApp(Color colorApp) {
		Color oldValue = this.colorApp;
		Color newValue = colorApp;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorApp = colorApp;
			firePropertyChange(PROPERTY_colorApp, oldValue, newValue);
		}
	}
	public void setColorAppDark(Color colorAppDark) {
		Color oldValue = this.colorAppDark;
		Color newValue = colorAppDark;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorAppDark = colorAppDark;
			firePropertyChange(PROPERTY_colorAppDark, oldValue, newValue);
		}
	}
	
	public void setColorPanels(Color colorPanels) {
		Color oldValue = this.colorPanels;
		Color newValue = colorPanels;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPanels = colorPanels;
			firePropertyChange(PROPERTY_colorPanels, oldValue, newValue);
		}
	}
	public void setColorPanelsBright(Color colorPanelsBright) {
		Color oldValue = this.colorPanelsBright;
		Color newValue = colorPanelsBright;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPanelsBright = colorPanelsBright;
			firePropertyChange(PROPERTY_colorPanelsBright, oldValue, newValue);
		}
	}
	public void setColorPanelsBrightest(Color colorPanelsBrightest) {
		Color oldValue = this.colorPanelsBrightest;
		Color newValue = colorPanelsBrightest;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPanelsBrightest = colorPanelsBrightest;
			firePropertyChange(PROPERTY_colorPanelsBrightest, oldValue, newValue);
		}
	}
	public void setColorPanelsDark(Color colorPanelsDark) {
		Color oldValue = this.colorPanelsDark;
		Color newValue = colorPanelsDark;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPanelsDark = colorPanelsDark;
			firePropertyChange(PROPERTY_colorPanelsDark, oldValue, newValue);
		}
	}
	public void setColorPanelsDarkest(Color colorPanelsDarkest) {
		Color oldValue = this.colorPanelsDarkest;
		Color newValue = colorPanelsDarkest;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPanelsDarkest = colorPanelsDarkest;
			firePropertyChange(PROPERTY_colorPanelsDarkest, oldValue, newValue);
		}
	}
	
	public void setColorImportant(Color colorImportant) {
		Color oldValue = this.colorImportant;
		Color newValue = colorImportant;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorImportant = colorImportant;
			firePropertyChange(PROPERTY_colorImportant, oldValue, newValue);
		}
	}
	public void setColorInfo(Color colorInfo) {
		Color oldValue = this.colorInfo;
		Color newValue = colorInfo;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorInfo = colorInfo;
			firePropertyChange(PROPERTY_colorInfo, oldValue, newValue);
		}
	}
	public void setColorWarning(Color colorWarning) {
		Color oldValue = this.colorWarning;
		Color newValue = colorWarning;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorWarning = colorWarning;
			firePropertyChange(PROPERTY_colorWarning, oldValue, newValue);
		}
	}
	public void setColorPositive(Color colorPositive) {
		Color oldValue = this.colorPositive;
		Color newValue = colorPositive;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorPositive = colorPositive;
			firePropertyChange(PROPERTY_colorPositive, oldValue, newValue);
		}
	}
	public void setColorNegative(Color colorNegative) {
		Color oldValue = this.colorNegative;
		Color newValue = colorNegative;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorNegative = colorNegative;
			firePropertyChange(PROPERTY_colorNegative, oldValue, newValue);
		}
	}
	
	public void setColorText(Color colorText) {
		Color oldValue = this.colorText;
		Color newValue = colorText;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorText = colorText;
			firePropertyChange(PROPERTY_colorText, oldValue, newValue);
		}
	}
	public void setColorTextBright(Color colorTextBright) {
		Color oldValue = this.colorTextBright;
		Color newValue = colorTextBright;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorTextBright = colorTextBright;
			firePropertyChange(PROPERTY_colorTextBright, oldValue, newValue);
		}
	}
	public void setColorTextBrightest(Color colorTextBrightest) {
		Color oldValue = this.colorTextBrightest;
		Color newValue = colorTextBrightest;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorTextBrightest = colorTextBrightest;
			firePropertyChange(PROPERTY_colorTextBrightest, oldValue, newValue);
		}
	}
	
	public void setColorBorder(Color colorBorder) {
		Color oldValue = this.colorBorder;
		Color newValue = colorBorder;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorBorder = colorBorder;
			firePropertyChange(PROPERTY_colorBorder, oldValue, newValue);
		}
	}
	public void setColorBorderBright(Color colorBorderBright) {
		Color oldValue = this.colorBorderBright;
		Color newValue = colorBorderBright;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorBorderBright = colorBorderBright;
			firePropertyChange(PROPERTY_colorBorderBright, oldValue, newValue);
		}
	}
	public void setColorBorderDark(Color colorBorderDark) {
		Color oldValue = this.colorBorderDark;
		Color newValue = colorBorderDark;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorBorderDark = colorBorderDark;
			firePropertyChange(PROPERTY_colorBorderDark, oldValue, newValue);
		}
	}
	
	public void setColorRollover(Color colorRollover) {
		Color oldValue = this.colorRollover;
		Color newValue = colorRollover;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorRollover = colorRollover;
			firePropertyChange(PROPERTY_colorRollover, oldValue, newValue);
		}
	}
	
	public void setColorRolloverDark(Color colorRolloverDark) {
		Color oldValue = this.colorRolloverDark;
		Color newValue = colorRolloverDark;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorRolloverDark = colorRolloverDark;
			firePropertyChange(PROPERTY_colorRolloverDark, oldValue, newValue);
		}
	}
	
	public void setColorShadow(Color colorShadow) {
		Color oldValue = this.colorShadow;
		Color newValue = colorShadow;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.colorShadow = colorShadow;
			firePropertyChange(PROPERTY_colorShadow, oldValue, newValue);
		}
	}
	
	public void setFontApp(LFont fontApp) {
		LFont oldValue = this.fontApp;
		LFont newValue = fontApp;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.fontApp = fontApp;
			firePropertyChange(PROPERTY_fontApp, oldValue, newValue);
		}
	}
	
	protected void encodeFieldsChanged(StringBuffer sb) {
		//init from ComponentUIProperties fields
		String prefix = UI_NAME + Constants.POINT;
		getFieldsChangeSupport().encodeFieldsChanged(sb, prefix);
	}
	
	public void updateUIPropertiesFromEncodedFields(String... encodedFields) {
		
		try {
			String prefix = UI_NAME + Constants.POINT;
			ReflectAccessSupport ras = new ReflectAccessSupport(this);
			ras.setEncodedFieldValues(prefix, encodedFields);
		}
		catch (Exception ex) {
			Console.printException(ex);
		}
	}
}
