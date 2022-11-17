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
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.DefaultEditorKit;

import sun.awt.AppContext;
import linaje.gui.Icons;
import linaje.gui.LComponentBorder;
import linaje.gui.PaintedImageIcon;
import linaje.gui.RoundedBorder;
import linaje.gui.StateIcon;
import linaje.gui.TranslucentPopupFactory;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Files;
import linaje.utils.LFont;
import linaje.utils.Lists;
import linaje.utils.ReferencedColor;
import linaje.utils.ReflectAccessSupport;
import linaje.utils.Security;
import linaje.utils.StateColor;

@SuppressWarnings("serial")
public class LinajeLookAndFeel extends BasicLookAndFeel {

	private static LinajeLookAndFeel lastInstance = null;
	private File configFile = null;
	
	public static final List<String> UI_TEXT_COMPONENTS = Lists.newList(
			"TextField",
			"PasswordField",
			"TextArea",
			"TextPane",
			"EditorPane");
	
	public static final List<String> UI_NON_LCOMPONENTS = Lists.newList(
			"ColorChooser",
			"PopupMenu",
			"OptionPane",
			"Panel",
			"ScrollPane",
			"Viewport",
			"TableHeader",
			"TitledBorder",
			"ToolBar",
			"ToolTip",
			"Tree");
	
	public static final Map<Class<?>, String> UI_LCOMPONENTS_BUTTONS_MAP = new LinkedHashMap<Class<?>, String>();
	public static final Map<Class<?>, String> UI_LCOMPONENTS_OTHER_MAP = new LinkedHashMap<Class<?>, String>();
	public static final List<String> UI_COMPONENTS = Lists.newList();
	
	public static String DEFAULT_UI_NAME = "Component";
	
	static {
		UI_LCOMPONENTS_BUTTONS_MAP.put(LButtonUI.class, "Button");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LToggleButtonUI.class, "ToggleButton");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LRadioButtonUI.class, "RadioButton");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LCheckBoxUI.class, "CheckBox");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LMenuItemUI.class, "MenuItem");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LRadioButtonMenuItemUI.class, "RadioButtonMenuItem");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LCheckBoxMenuItemUI.class, "CheckBoxMenuItem");
		UI_LCOMPONENTS_BUTTONS_MAP.put(LMenuUI.class, "Menu");
		
		UI_LCOMPONENTS_OTHER_MAP.put(LComboUI.class, "ComboBox");
		UI_LCOMPONENTS_OTHER_MAP.put(LLabelUI.class, "Label");
		UI_LCOMPONENTS_OTHER_MAP.put(LScrollBarUI.class, "ScrollBar");
		UI_LCOMPONENTS_OTHER_MAP.put(LSliderUI.class, "Slider");
		UI_LCOMPONENTS_OTHER_MAP.put(LTabbedPaneUI.class, "TabbedPane");
		UI_LCOMPONENTS_OTHER_MAP.put(LTableUI.class, "Table");
		UI_LCOMPONENTS_OTHER_MAP.put(LTableHeaderUI.class, "TableHeader");
		UI_LCOMPONENTS_OTHER_MAP.put(LTextFieldUI.class, "TextField");
		UI_LCOMPONENTS_OTHER_MAP.put(LTextAreaUI.class, "TextArea");
		UI_LCOMPONENTS_OTHER_MAP.put(LTextPaneUI.class, "TextPane");
		UI_LCOMPONENTS_OTHER_MAP.put(LTreeUI.class, "Tree");
		UI_LCOMPONENTS_OTHER_MAP.put(LProgressBarUI.class, "ProgressBar");
		
		UI_LCOMPONENTS_OTHER_MAP.put(LFileChooserUI.class, "FileChooser");
		UI_LCOMPONENTS_OTHER_MAP.put(LToolBarUI.class, "ToolBar");
		
		UI_COMPONENTS.addAll(UI_LCOMPONENTS_BUTTONS_MAP.values());
		UI_COMPONENTS.addAll(UI_LCOMPONENTS_OTHER_MAP.values());
		UI_COMPONENTS.addAll(UI_TEXT_COMPONENTS);
		UI_COMPONENTS.addAll(UI_NON_LCOMPONENTS);
	}
	
	private GeneralUIProperties generalUIProperties = null;
	
	public LinajeLookAndFeel() {
		this(UIConfig.getDefaultConfigFile());
	}
	
	public LinajeLookAndFeel(File configFile) {
		lastInstance = this;
		this.configFile = configFile;
	}
	
	public static void init() {
		init(UIConfig.getDefaultConfigFile());
	}
	public static void init(File configFile) {
		try {
			if (configFile == null)
				configFile = UIConfig.getDefaultConfigFile();
			
			LinajeLookAndFeel linajeLookAndFeel = new LinajeLookAndFeel(configFile);
			UIManager.installLookAndFeel(linajeLookAndFeel.getName(), linajeLookAndFeel.getClass().getName());
			UIManager.setLookAndFeel(linajeLookAndFeel);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public static LinajeLookAndFeel getInstance() {
		LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
		if (currentLookAndFeel != null && currentLookAndFeel instanceof LinajeLookAndFeel)
			return (LinajeLookAndFeel) currentLookAndFeel;
		else {
			if (lastInstance == null)
				lastInstance = new LinajeLookAndFeel();
			return lastInstance;
		}
	}
	
	public GeneralUIProperties getGeneralUIProperties() {
		if (generalUIProperties == null) {
			generalUIProperties = new GeneralUIProperties();
		}
		return generalUIProperties;
	}
	
	protected void initClassDefaults(UIDefaults table) {
		
		super.initClassDefaults(table);
		
		/**
		 * Rellenamos el array de uiDefaults con todos los UIs personalizados
		 * de forma que quedará una cosa así,
		 * String[] uiDefaults = {
		 *		"ButtonUI", LButtonUI.class.getName(),
		 *		"ToggleButtonUI", LToggleButtonUI.class.getName(),
		 * 		...
		 * */
		int arraySize = UI_LCOMPONENTS_BUTTONS_MAP.size()*2 + UI_LCOMPONENTS_OTHER_MAP.size()*2;
		String[] uiDefaults = new String[arraySize];
		
		int index = 0;
		final String UI = "UI";
		Iterator<Entry<Class<?>, String>> lComponentsButtons = UI_LCOMPONENTS_BUTTONS_MAP.entrySet().iterator();
		for (Iterator<Entry<Class<?>, String>> iterator = lComponentsButtons; iterator.hasNext();) {
			Entry<Class<?>, String> entry = iterator.next();
			uiDefaults[index] = entry.getValue()+UI;
			index++;
			uiDefaults[index] = entry.getKey().getName();
			index++;
		}
		Iterator<Entry<Class<?>, String>> lComponentsOther = UI_LCOMPONENTS_OTHER_MAP.entrySet().iterator();
		for (Iterator<Entry<Class<?>, String>> iterator = lComponentsOther; iterator.hasNext();) {
			Entry<Class<?>, String> entry = iterator.next();
			uiDefaults[index] = entry.getValue()+UI;
			index++;
			uiDefaults[index] = entry.getKey().getName();
			index++;
		}
		
		table.putDefaults(uiDefaults);
    }
	
	 protected void initSystemColorDefaults(UIDefaults table) {
		 
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		
		String[] encodedFields = getEncodedFields();
		GeneralUIProperties.getInstance().updateUIPropertiesFromEncodedFields(encodedFields);
		
		table.put("desktop", createResourceValue(generalUIProperties.getColorAppDark())); /* Color of the desktop background */
		table.put("activeCaption", createResourceValue(generalUIProperties.getColorRolloverDark())); /* Color for captions (title bars) when they are active. */
		table.put("activeCaptionText", createResourceValue(generalUIProperties.getColorTextBrightest())); /* Text color for text in captions (title bars). */
		table.put("activeCaptionBorder", createResourceValue(generalUIProperties.getColorBorderDark())); /* Border color for caption (title bar) window borders. */
		table.put("inactiveCaption", createResourceValue(generalUIProperties.getColorPanelsBright())); /* Color for captions (title bars) when not active. */
		table.put("inactiveCaptionText", createResourceValue(generalUIProperties.getColorTextBright())); /* Text color for text in inactive captions (title bars). */
		table.put("inactiveCaptionBorder", createResourceValue(generalUIProperties.getColorBorderBright())); /* Border color for inactive caption (title bar) window borders. */
		table.put("window", createResourceValue(generalUIProperties.getColorPanelsBrightest())); /* Default color for the interior of windows */
		table.put("windowBorder", createResourceValue(generalUIProperties.getColorBorderDark())); /* ??? */
		table.put("windowText", createResourceValue(generalUIProperties.getColorText())); /* ??? */
		table.put("menu", createResourceValue(generalUIProperties.getColorPanelsBright())); /* Background color for menus */
		table.put("menuText", createResourceValue(generalUIProperties.getColorText())); /* Text color for menus  */
		table.put("text", createResourceValue(generalUIProperties.getColorText())); /* Text background color */
		table.put("textText", createResourceValue(generalUIProperties.getColorText())); /* Text foreground color */
		table.put("textHighlight", createResourceValue(generalUIProperties.getColorRolloverDark())); /* Text background color when selected */
		table.put("textHighlightText", createResourceValue(generalUIProperties.getColorTextBrightest())); /* Text color when selected */
		table.put("textInactiveText", createResourceValue(generalUIProperties.getColorTextBright())); /* Text color when disabled */
		table.put("control", createResourceValue(generalUIProperties.getColorPanels())); /* Default color for controls (buttons, sliders, etc) */
		table.put("controlText", createResourceValue(generalUIProperties.getColorText())); /* Default color for text in controls */
		table.put("controlHighlight", createResourceValue(Colors.brighter(generalUIProperties.getColorPanels(), 0.08))); /* Specular highlight (opposite of the shadow) */
		table.put("controlLtHighlight", createResourceValue(Colors.brighter(generalUIProperties.getColorPanels(), 0.1))); /* Highlight color for controls */
		table.put("controlShadow", createResourceValue(Colors.darker(generalUIProperties.getColorPanels(), 0.4))); /* Shadow color for controls */
		table.put("controlDkShadow", createResourceValue(Colors.darker(generalUIProperties.getColorPanels(), 0.5))); /* Dark shadow color for controls */
		table.put("scrollbar", createResourceValue(generalUIProperties.getColorPanels())); /* Scrollbar background (usually the "track") */
		table.put("info", createResourceValue(generalUIProperties.getColorInfo())); /* ??? */
		table.put("infoText", createResourceValue(generalUIProperties.getColorText()));  /* ??? */
    }
	
	 /**
     * Initialize the defaults table with the name of the ResourceBundle
     * used for getting localized defaults.  Also initialize the default
     * locale used when no locale is passed into UIDefaults.get()
     */
	private void initResourceBundle(UIDefaults table) {
		table.addResourceBundle("linaje.gui.localization.fchooser");
	}
	    
	@Override
	protected void initComponentDefaults(UIDefaults table) {
		
		//Iniciamos el LookAndFeel general con los colores de sistema cambiados
		super.initComponentDefaults(table);
		
		initResourceBundle(table);
		
		//Enable text Antialiasing
		enableAAText(table);
        
       	//Iniciamos las propiedades comunes a todos los componentes
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
		putComponentsUIPropertyValue(table, "font", generalUIProperties.getFontApp());
		
		initTextComponents(table);
		initInputMaps(table);
		
		//Iniciamos todas las propiedades de los componentes (botones, RadioButtons, Checks...)
		String[] encodedFields = getEncodedFields();
		UISupport.initComponentsUIDefaults(table, encodedFields);
		
		//Iniciamos otras propiedades
		final Color PANEL_BACKGROUND = generalUIProperties.getColorPanels();
		final Color TREE_BACKGROUND = generalUIProperties.getColorPanelsBrightest();
		final Color SCROLL_TRACK = Colors.isColorDark(PANEL_BACKGROUND) ? Colors.brighter(PANEL_BACKGROUND, 0.025) : Colors.darker(PANEL_BACKGROUND, 0.025);
		final int SCROLL_SIZE = generalUIProperties.getFontApp().getSize() + 4;
		final Border tableHeaderBorder = UISupport.getDefaultComponentUIProperties(LTableHeaderUI.class).getBorder();
		
		 //Ponemos borde de sombra semitransparente a los popups y tooltips
		TranslucentPopupFactory.install();
		
		RoundedBorder popupBorder = new RoundedBorder();
		RoundedBorder toolTipBorder = new RoundedBorder();
		//Quitamos la sombra del propio borde, ya que la pintará el TranslucentPopup (Si usaramos la sombra del borde no se aplicaría bien la transparencia de la sombra sobre otros componentes TranslucentPopup la fuerza)
		popupBorder.setThicknessShadow(0);
		toolTipBorder.setThicknessShadow(0);
		toolTipBorder.setLineBorderColor(generalUIProperties.getColorInfo());
		
		Icon treeExpandedIcon = Icons.getIconArrow(SwingConstants.SOUTH, true, true, false);
		Icon treeCollapsedIcon = Icons.getIconArrow(SwingConstants.EAST, true, true, false);
		
		Object[] defaults = {
			"ScrollBar.track", SCROLL_TRACK,
			"ScrollBar.width", SCROLL_SIZE,
			"Panel.background", PANEL_BACKGROUND,
			"ComboBox.disabledBackground", generalUIProperties.getColorPanelsBright(),
			"TitledBorder.position", TitledBorder.ABOVE_TOP,
			"TitledBorder.titleColor", generalUIProperties.getColorText(),
			"TitledBorder.font", UtilsGUI.getFontWithStyle(generalUIProperties.getFontApp(), Font.BOLD),
			"ScrollPane.background", PANEL_BACKGROUND,
			"ScrollPane.border", BorderFactory.createEmptyBorder(),
			"PopupMenu.border", popupBorder,
			"PopupMenu.background", generalUIProperties.getColorPanelsBright(),
			"PopupMenu.opaque", Boolean.TRUE,
			"Separator.background", generalUIProperties.getColorPanelsBright(),
			"Separator.foreground", generalUIProperties.getColorTextBright(),
			"MenuBar.background", generalUIProperties.getColorPanelsBright(),
			"MenuBar.shadow", generalUIProperties.getColorPanelsBright(),
			"MenuBar.highlight", generalUIProperties.getColorTextBright(),
			"TableHeader.focusCellBorder", tableHeaderBorder,
			"TableHeader.cellBorder", tableHeaderBorder,
			"Tree.expandedIcon", treeExpandedIcon,
            "Tree.collapsedIcon", treeCollapsedIcon,
            //"Tree.background", TREE_BACKGROUND,
            "Tree.textBackground", TREE_BACKGROUND,
            "ToolBar.nonrolloverBorder", BorderFactory.createEmptyBorder(1,1,1,1),
            "ToolBar.rolloverBorder", BorderFactory.createEmptyBorder(1,1,1,1),
            "ToolBar.isRollover", Boolean.FALSE,
            
            "ToolTip.font", generalUIProperties.getFontApp(),
            "ToolTip.background", generalUIProperties.getColorPanelsBrightest(),
            "ToolTip.foreground", generalUIProperties.getColorText(),
            "ToolTip.border", toolTipBorder,
            
            "FileChooser.listFont", generalUIProperties.getFontApp(),
            //"FileChooser.listViewBackground", generalUIProperties.getColorPanelsBrightest(),
            //"FileChooser.listViewBorder", BorderFactory.createLineBorder(generalUIProperties.getColorApp()),
            //"FileChooser.listViewWindowsStyle", Boolean.TRUE,
            "FileChooser.useSystemExtensionHiding", Boolean.TRUE,
            "FileChooser.usesSingleFilePane", Boolean.TRUE,
            //"FileChooser.noPlacesBar", new DesktopProperty("win.comdlg.noPlacesBar", Boolean.FALSE),
            "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
																		"ESCAPE", "cancelSelection",
																		"F2", "editFileName",
																		"F5", "refresh",
																		"BACK_SPACE", "Go Up"
																		}),
            
           /* "FileChooser.homeFolderIcon",  new LazyWindowsIcon(null, "icons/HomeFolder.gif"),
            "FileChooser.listViewIcon", new LazyWindowsIcon("fileChooserIcon ListView", "icons/ListView.gif"),
            "FileChooser.detailsViewIcon", new LazyWindowsIcon("fileChooserIcon DetailsView", "icons/DetailsView.gif"),
            "FileChooser.viewMenuIcon", new LazyWindowsIcon("fileChooserIcon ViewMenu", "icons/ListView.gif"),
            "FileChooser.upFolderIcon",    new LazyWindowsIcon("fileChooserIcon UpFolder", "icons/UpFolder.gif"),
            "FileChooser.newFolderIcon",   new LazyWindowsIcon("fileChooserIcon NewFolder", "icons/NewFolder.gif"),
            "FileView.directoryIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/Directory.gif"),
            "FileView.fileIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/File.gif"),
            "FileView.computerIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/Computer.gif"),
            "FileView.hardDriveIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/HardDrive.gif"),
            "FileView.floppyDriveIcon", SwingUtilities2.makeIcon(getClass(), WindowsLookAndFeel.class, "icons/FloppyDrive.gif")
            */

            "FileChooser.homeFolderIcon",  Icons.COMPUTER,
            "FileChooser.listViewIcon", Icons.DOCUMENT,
            "FileChooser.detailsViewIcon", Icons.MESSAGE_INFO,
            "FileChooser.viewMenuIcon", Icons.DOCUMENT,
            "FileChooser.upFolderIcon", Icons.FOLDER_UP,
            "FileChooser.newFolderIcon", Icons.FOLDER_NEW,
            "FileView.directoryIcon", Icons.getColorizedIcon(Icons.FOLDER, generalUIProperties.getColorText()),
            "FileView.fileIcon", Icons.DOCUMENT,
            "FileView.computerIcon", Icons.COMPUTER,
            "FileView.hardDriveIcon", Icons.CALENDAR,
            "FileView.floppyDriveIcon", Icons.SAVE
		};
	    
		createResourceValues(defaults);
		table.putDefaults(defaults);
	}
	
	private void enableAAText(UIDefaults table) {
		
		//Enable text Antialiasing
		String javaVersion = Security.getSystemProperty(Security.KEY_JAVA_SPECIFICATION_VERSION);
		if (javaVersion.compareTo("1.8") <= 0) {
			//Object aaTextInfo = SwingUtilities2.AATextInfo.getAATextInfo(true);
        	//table.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);
			
			//Llamamos por reflexión para que no tengamos errores de compilación en MV superiores a la 1.8
			try {
				Class<?> AATextInfo_class = Class.forName("sun.swing.SwingUtilities2$AATextInfo");
				Class<?> parameterTypes = boolean.class;
				Object aaTextInfo = ReflectAccessSupport.findMethod("getAATextInfo", parameterTypes, AATextInfo_class).invoke(null, true);
				
				Class<?> SwingUtilities2_class = Class.forName("sun.swing.SwingUtilities2");
				Object AA_TEXT_PROPERTY_KEY = ReflectAccessSupport.findField("AA_TEXT_PROPERTY_KEY", SwingUtilities2_class).get(null);
				
				table.put(AA_TEXT_PROPERTY_KEY, aaTextInfo);
			} 
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
     	System.setProperty("awt.useSystemAAFontSettings","on");
     	System.setProperty("swing.aatext", "true");
	}
	
	private void initTextComponents(UIDefaults table) {
		
		GeneralUIProperties generalUIProperties = GeneralUIProperties.getInstance();
        
		List<String> textComponents = Lists.newList();
		textComponents.addAll(UI_TEXT_COMPONENTS);
		textComponents.add("ComboBox");
		textComponents.add("Label");
		textComponents.add("List");
		
		putComponentsUIPropertyValue(table, "background", generalUIProperties.getColorPanelsBrightest(), textComponents);
		putComponentsUIPropertyValue(table, "foreground", generalUIProperties.getColorText(), textComponents);
		//selectionColor
		putComponentsUIPropertyValue(table, "selectionBackground", generalUIProperties.getColorRollover(), textComponents);
		//selectedTextColor
		putComponentsUIPropertyValue(table, "selectionForeground", generalUIProperties.getColorText(), textComponents);
		
		textComponents.remove("Label");
		//textComponents.remove("List");
		
		putComponentsUIPropertyValue(table, "margin", new Insets(1,3,1,3), textComponents);
		putComponentsUIPropertyValue(table, "border", new BorderUIResource.CompoundBorderUIResource(new LComponentBorder(), new MarginBorder()), textComponents);
	}
	
	public static void createResourceValues(Object[] keyValueList) {
		for (int i = 0; i < keyValueList.length; i += 2) {
			keyValueList[i + 1] = createResourceValue(keyValueList[i + 1]);
		}
	}
	
	public static Object createResourceValue(Object value) {
		
		try {
			if (value != null && !(value instanceof UIResource)) {
				if (value instanceof ReferencedColor)
					return new ReferencedColorUIResource((ReferencedColor) value);
				else if (value instanceof StateColor)
					return new StateColorUIResource((StateColor) value);
				else if (value instanceof Color)
					return new ColorUIResource((Color) value);
				if (value instanceof LFont)
					return new LFontUIResource((LFont) value);
				if (value instanceof Font)
					return new FontUIResource((Font) value);
				else if (value instanceof StateIcon)
					return new StateIconUIResource((StateIcon) value);
				else if (value instanceof Icon)
					return new IconUIResource((Icon) value);
				else if (value instanceof Insets) {
					Insets insets = (Insets) value;
					return new InsetsUIResource(insets.top, insets.left, insets.bottom, insets.right);
				}
				else if (value instanceof Dimension) {
					Dimension dim = (Dimension) value;
					return new DimensionUIResource(dim.width, dim.height);
				}
			}
		} catch (Exception ex) {
			Console.printException(ex);
		}
		return value;
	}
	
	private void initInputMaps(UIDefaults table) {
		
		Object fieldFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                "ctrl C", DefaultEditorKit.copyAction,
	                "ctrl V", DefaultEditorKit.pasteAction,
	                "ctrl X", DefaultEditorKit.cutAction,
	                  "COPY", DefaultEditorKit.copyAction,
	                 "PASTE", DefaultEditorKit.pasteAction,
	                   "CUT", DefaultEditorKit.cutAction,
	        "control INSERT", DefaultEditorKit.copyAction,
	          "shift INSERT", DefaultEditorKit.pasteAction,
	          "shift DELETE", DefaultEditorKit.cutAction,
	            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
	         "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
	           "shift RIGHT", DefaultEditorKit.selectionForwardAction,
	        "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
	             "ctrl LEFT", DefaultEditorKit.previousWordAction,
	          "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
	            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
	         "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
	       "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
	    "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
	      "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
	   "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
	                "ctrl A", DefaultEditorKit.selectAllAction,
	                  "HOME", DefaultEditorKit.beginLineAction,
	                   "END", DefaultEditorKit.endLineAction,
	            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
	             "shift END", DefaultEditorKit.selectionEndLineAction,
	            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	      "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	                "ctrl H", DefaultEditorKit.deletePrevCharAction,
	                "DELETE", DefaultEditorKit.deleteNextCharAction,
	           "ctrl DELETE", DefaultEditorKit.deleteNextWordAction,
	       "ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
	                 "RIGHT", DefaultEditorKit.forwardAction,
	                  "LEFT", DefaultEditorKit.backwardAction,
	              "KP_RIGHT", DefaultEditorKit.forwardAction,
	               "KP_LEFT", DefaultEditorKit.backwardAction,
	                 "ENTER", JTextField.notifyAction,
	       "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
	        "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
		});
		
		Object passwordFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                "ctrl C", DefaultEditorKit.copyAction,
	                "ctrl V", DefaultEditorKit.pasteAction,
	                "ctrl X", DefaultEditorKit.cutAction,
	                  "COPY", DefaultEditorKit.copyAction,
	                 "PASTE", DefaultEditorKit.pasteAction,
	                   "CUT", DefaultEditorKit.cutAction,
	        "control INSERT", DefaultEditorKit.copyAction,
	          "shift INSERT", DefaultEditorKit.pasteAction,
	          "shift DELETE", DefaultEditorKit.cutAction,
	            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
	         "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
	           "shift RIGHT", DefaultEditorKit.selectionForwardAction,
	        "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
	             "ctrl LEFT", DefaultEditorKit.beginLineAction,
	          "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
	            "ctrl RIGHT", DefaultEditorKit.endLineAction,
	         "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
	       "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
	    "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
	      "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
	   "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
	                "ctrl A", DefaultEditorKit.selectAllAction,
	                  "HOME", DefaultEditorKit.beginLineAction,
	                   "END", DefaultEditorKit.endLineAction,
	            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
	             "shift END", DefaultEditorKit.selectionEndLineAction,
	            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	      "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	                "ctrl H", DefaultEditorKit.deletePrevCharAction,
	                "DELETE", DefaultEditorKit.deleteNextCharAction,
	                 "RIGHT", DefaultEditorKit.forwardAction,
	                  "LEFT", DefaultEditorKit.backwardAction,
	              "KP_RIGHT", DefaultEditorKit.forwardAction,
	               "KP_LEFT", DefaultEditorKit.backwardAction,
	                 "ENTER", JTextField.notifyAction,
	       "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
	        "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
		});
		
		Object multilineFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                "ctrl C", DefaultEditorKit.copyAction,
	                "ctrl V", DefaultEditorKit.pasteAction,
	                "ctrl X", DefaultEditorKit.cutAction,
	                  "COPY", DefaultEditorKit.copyAction,
	                 "PASTE", DefaultEditorKit.pasteAction,
	                   "CUT", DefaultEditorKit.cutAction,
	        "control INSERT", DefaultEditorKit.copyAction,
	          "shift INSERT", DefaultEditorKit.pasteAction,
	          "shift DELETE", DefaultEditorKit.cutAction,
	            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
	         "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
	           "shift RIGHT", DefaultEditorKit.selectionForwardAction,
	        "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
	             "ctrl LEFT", DefaultEditorKit.previousWordAction,
	          "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
	            "ctrl RIGHT", DefaultEditorKit.nextWordAction,
	         "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
	       "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
	    "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
	      "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
	   "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
	                "ctrl A", DefaultEditorKit.selectAllAction,
	                  "HOME", DefaultEditorKit.beginLineAction,
	                   "END", DefaultEditorKit.endLineAction,
	            "shift HOME", DefaultEditorKit.selectionBeginLineAction,
	             "shift END", DefaultEditorKit.selectionEndLineAction,
	
	                    "UP", DefaultEditorKit.upAction,
	                 "KP_UP", DefaultEditorKit.upAction,
	                  "DOWN", DefaultEditorKit.downAction,
	               "KP_DOWN", DefaultEditorKit.downAction,
	               "PAGE_UP", DefaultEditorKit.pageUpAction,
	             "PAGE_DOWN", DefaultEditorKit.pageDownAction,
	         "shift PAGE_UP", "selection-page-up",
	       "shift PAGE_DOWN", "selection-page-down",
	    "ctrl shift PAGE_UP", "selection-page-left",
	  "ctrl shift PAGE_DOWN", "selection-page-right",
	              "shift UP", DefaultEditorKit.selectionUpAction,
	           "shift KP_UP", DefaultEditorKit.selectionUpAction,
	            "shift DOWN", DefaultEditorKit.selectionDownAction,
	         "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
	                 "ENTER", DefaultEditorKit.insertBreakAction,
	            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	      "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
	                "ctrl H", DefaultEditorKit.deletePrevCharAction,
	                "DELETE", DefaultEditorKit.deleteNextCharAction,
	           "ctrl DELETE", DefaultEditorKit.deleteNextWordAction,
	       "ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
	                 "RIGHT", DefaultEditorKit.forwardAction,
	                  "LEFT", DefaultEditorKit.backwardAction,
	              "KP_RIGHT", DefaultEditorKit.forwardAction,
	               "KP_LEFT", DefaultEditorKit.backwardAction,
	                   "TAB", DefaultEditorKit.insertTabAction,
	       "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
	             "ctrl HOME", DefaultEditorKit.beginAction,
	              "ctrl END", DefaultEditorKit.endAction,
	       "ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
	        "ctrl shift END", DefaultEditorKit.selectionEndAction,
	                "ctrl T", "next-link-action",
	          "ctrl shift T", "previous-link-action",
	            "ctrl SPACE", "activate-link-action",
	        "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
		});

		Object buttonFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
			         "SPACE", "pressed",
			"released SPACE", "released"
		});
		
		Object comboBoxAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
				    "ESCAPE", "hidePopup",
				   "PAGE_UP", "pageUpPassThrough",
				 "PAGE_DOWN", "pageDownPassThrough",
				      "HOME", "homePassThrough",
				       "END", "endPassThrough",
				      "DOWN", "selectNext2",
				   "KP_DOWN", "selectNext2",
				        "UP", "selectPrevious2",
				     "KP_UP", "selectPrevious2",
				     "ENTER", "enterPressed",
				        "F4", "togglePopup",
				  "alt DOWN", "togglePopup",
			   "alt KP_DOWN", "togglePopup",
				    "alt UP", "togglePopup",
				 "alt KP_UP", "togglePopup"
		});

		Object desktopAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                   "ctrl F5", "restore",
                   "ctrl F4", "close",
                   "ctrl F7", "move",
                   "ctrl F8", "resize",
                     "RIGHT", "right",
                  "KP_RIGHT", "right",
                      "LEFT", "left",
                   "KP_LEFT", "left",
                        "UP", "up",
                     "KP_UP", "up",
                      "DOWN", "down",
                   "KP_DOWN", "down",
                    "ESCAPE", "escape",
                   "ctrl F9", "minimize",
                  "ctrl F10", "maximize",
                   "ctrl F6", "selectNextFrame",
                  "ctrl TAB", "selectNextFrame",
               "ctrl alt F6", "selectNextFrame",
         "shift ctrl alt F6", "selectPreviousFrame",
                  "ctrl F12", "navigateNext",
            "shift ctrl F12", "navigatePrevious"
        });
        
        Object listFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
                    "ctrl C", "copy",
                    "ctrl V", "paste",
                    "ctrl X", "cut",
                      "COPY", "copy",
                     "PASTE", "paste",
                       "CUT", "cut",
            "control INSERT", "copy",
              "shift INSERT", "paste",
              "shift DELETE", "cut",
                        "UP", "selectPreviousRow",
                     "KP_UP", "selectPreviousRow",
                  "shift UP", "selectPreviousRowExtendSelection",
               "shift KP_UP", "selectPreviousRowExtendSelection",
             "ctrl shift UP", "selectPreviousRowExtendSelection",
          "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                   "ctrl UP", "selectPreviousRowChangeLead",
                "ctrl KP_UP", "selectPreviousRowChangeLead",
                      "DOWN", "selectNextRow",
                   "KP_DOWN", "selectNextRow",
                "shift DOWN", "selectNextRowExtendSelection",
             "shift KP_DOWN", "selectNextRowExtendSelection",
           "ctrl shift DOWN", "selectNextRowExtendSelection",
        "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                 "ctrl DOWN", "selectNextRowChangeLead",
              "ctrl KP_DOWN", "selectNextRowChangeLead",
                      "LEFT", "selectPreviousColumn",
                   "KP_LEFT", "selectPreviousColumn",
                "shift LEFT", "selectPreviousColumnExtendSelection",
             "shift KP_LEFT", "selectPreviousColumnExtendSelection",
           "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
        "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                 "ctrl LEFT", "selectPreviousColumnChangeLead",
              "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
                     "RIGHT", "selectNextColumn",
                  "KP_RIGHT", "selectNextColumn",
               "shift RIGHT", "selectNextColumnExtendSelection",
            "shift KP_RIGHT", "selectNextColumnExtendSelection",
          "ctrl shift RIGHT", "selectNextColumnExtendSelection",
       "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                "ctrl RIGHT", "selectNextColumnChangeLead",
             "ctrl KP_RIGHT", "selectNextColumnChangeLead",
                      "HOME", "selectFirstRow",
                "shift HOME", "selectFirstRowExtendSelection",
           "ctrl shift HOME", "selectFirstRowExtendSelection",
                 "ctrl HOME", "selectFirstRowChangeLead",
                       "END", "selectLastRow",
                 "shift END", "selectLastRowExtendSelection",
            "ctrl shift END", "selectLastRowExtendSelection",
                  "ctrl END", "selectLastRowChangeLead",
                   "PAGE_UP", "scrollUp",
             "shift PAGE_UP", "scrollUpExtendSelection",
        "ctrl shift PAGE_UP", "scrollUpExtendSelection",
              "ctrl PAGE_UP", "scrollUpChangeLead",
                 "PAGE_DOWN", "scrollDown",
           "shift PAGE_DOWN", "scrollDownExtendSelection",
      "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
            "ctrl PAGE_DOWN", "scrollDownChangeLead",
                    "ctrl A", "selectAll",
                "ctrl SLASH", "selectAll",
           "ctrl BACK_SLASH", "clearSelection",
                     "SPACE", "addToSelection",
                "ctrl SPACE", "toggleAndAnchor",
               "shift SPACE", "extendTo",
          "ctrl shift SPACE", "moveSelectionTo"
		});
		
		Object scrollBarAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                  "RIGHT", "positiveUnitIncrement",
	               "KP_RIGHT", "positiveUnitIncrement",
	                   "DOWN", "positiveUnitIncrement",
	                "KP_DOWN", "positiveUnitIncrement",
	              "PAGE_DOWN", "positiveBlockIncrement",
	         "ctrl PAGE_DOWN", "positiveBlockIncrement",
	                   "LEFT", "negativeUnitIncrement",
	                "KP_LEFT", "negativeUnitIncrement",
	                     "UP", "negativeUnitIncrement",
	                  "KP_UP", "negativeUnitIncrement",
	                "PAGE_UP", "negativeBlockIncrement",
	           "ctrl PAGE_UP", "negativeBlockIncrement",
	                   "HOME", "minScroll",
	                    "END", "maxScroll"
		});
		
		Object scrollPaneAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                    "RIGHT", "unitScrollRight",
	                 "KP_RIGHT", "unitScrollRight",
	                     "DOWN", "unitScrollDown",
	                  "KP_DOWN", "unitScrollDown",
	                     "LEFT", "unitScrollLeft",
	                  "KP_LEFT", "unitScrollLeft",
	                       "UP", "unitScrollUp",
	                    "KP_UP", "unitScrollUp",
	                  "PAGE_UP", "scrollUp",
	                "PAGE_DOWN", "scrollDown",
	             "ctrl PAGE_UP", "scrollLeft",
	           "ctrl PAGE_DOWN", "scrollRight",
	                "ctrl HOME", "scrollHome",
	                 "ctrl END", "scrollEnd"
		});
		
		Object sliderFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "positiveUnitIncrement",
                     "KP_RIGHT", "positiveUnitIncrement",
                         "DOWN", "negativeUnitIncrement",
                      "KP_DOWN", "negativeUnitIncrement",
                    "PAGE_DOWN", "negativeBlockIncrement",
                         "LEFT", "negativeUnitIncrement",
                      "KP_LEFT", "negativeUnitIncrement",
                           "UP", "positiveUnitIncrement",
                        "KP_UP", "positiveUnitIncrement",
                      "PAGE_UP", "positiveBlockIncrement",
                         "HOME", "minScroll",
                          "END", "maxScroll"
		});   
		Object spinnerAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                          "UP", "increment",
                       "KP_UP", "increment",
                        "DOWN", "decrement",
                     "KP_DOWN", "decrement",
		});
		Object splitPaneAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                           "UP", "negativeIncrement",
                         "DOWN", "positiveIncrement",
                         "LEFT", "negativeIncrement",
                        "RIGHT", "positiveIncrement",
                        "KP_UP", "negativeIncrement",
                      "KP_DOWN", "positiveIncrement",
                      "KP_LEFT", "negativeIncrement",
                     "KP_RIGHT", "positiveIncrement",
                         "HOME", "selectMin",
                          "END", "selectMax",
                           "F8", "startResize",
                           "F6", "toggleFocus",
                     "ctrl TAB", "focusOutForward",
               "ctrl shift TAB", "focusOutBackward"
		});
		Object tabbedPaneFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "navigateRight",
                     "KP_RIGHT", "navigateRight",
                         "LEFT", "navigateLeft",
                      "KP_LEFT", "navigateLeft",
                           "UP", "navigateUp",
                        "KP_UP", "navigateUp",
                         "DOWN", "navigateDown",
                      "KP_DOWN", "navigateDown",
                    "ctrl DOWN", "requestFocusForVisibleComponent",
                 "ctrl KP_DOWN", "requestFocusForVisibleComponent",
		});
		Object tabbedPaneAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                    "ctrl TAB", "navigateNext",
	              "ctrl shift TAB", "navigatePrevious",
	              "ctrl PAGE_DOWN", "navigatePageDown",
	                "ctrl PAGE_UP", "navigatePageUp",
	                     "ctrl UP", "requestFocus",
	                  "ctrl KP_UP", "requestFocus",
		});
		Object  tableAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                          "ctrl C", "copy",
                          "ctrl V", "paste",
                          "ctrl X", "cut",
                            "COPY", "copy",
                           "PASTE", "paste",
                             "CUT", "cut",
                  "control INSERT", "copy",
                    "shift INSERT", "paste",
                    "shift DELETE", "cut",
                           "RIGHT", "selectNextColumn",
                        "KP_RIGHT", "selectNextColumn",
                     "shift RIGHT", "selectNextColumnExtendSelection",
                  "shift KP_RIGHT", "selectNextColumnExtendSelection",
                "ctrl shift RIGHT", "selectNextColumnExtendSelection",
             "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                      "ctrl RIGHT", "selectNextColumnChangeLead",
                   "ctrl KP_RIGHT", "selectNextColumnChangeLead",
                            "LEFT", "selectPreviousColumn",
                         "KP_LEFT", "selectPreviousColumn",
                      "shift LEFT", "selectPreviousColumnExtendSelection",
                   "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                 "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
              "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                       "ctrl LEFT", "selectPreviousColumnChangeLead",
                    "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
                            "DOWN", "selectNextRow",
                         "KP_DOWN", "selectNextRow",
                      "shift DOWN", "selectNextRowExtendSelection",
                   "shift KP_DOWN", "selectNextRowExtendSelection",
                 "ctrl shift DOWN", "selectNextRowExtendSelection",
              "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                       "ctrl DOWN", "selectNextRowChangeLead",
                    "ctrl KP_DOWN", "selectNextRowChangeLead",
                              "UP", "selectPreviousRow",
                           "KP_UP", "selectPreviousRow",
                        "shift UP", "selectPreviousRowExtendSelection",
                     "shift KP_UP", "selectPreviousRowExtendSelection",
                   "ctrl shift UP", "selectPreviousRowExtendSelection",
                "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                         "ctrl UP", "selectPreviousRowChangeLead",
                      "ctrl KP_UP", "selectPreviousRowChangeLead",
                            "HOME", "selectFirstColumn",
                      "shift HOME", "selectFirstColumnExtendSelection",
                 "ctrl shift HOME", "selectFirstRowExtendSelection",
                       "ctrl HOME", "selectFirstRow",
                             "END", "selectLastColumn",
                       "shift END", "selectLastColumnExtendSelection",
                  "ctrl shift END", "selectLastRowExtendSelection",
                        "ctrl END", "selectLastRow",
                         "PAGE_UP", "scrollUpChangeSelection",
                   "shift PAGE_UP", "scrollUpExtendSelection",
              "ctrl shift PAGE_UP", "scrollLeftExtendSelection",
                    "ctrl PAGE_UP", "scrollLeftChangeSelection",
                       "PAGE_DOWN", "scrollDownChangeSelection",
                 "shift PAGE_DOWN", "scrollDownExtendSelection",
            "ctrl shift PAGE_DOWN", "scrollRightExtendSelection",
                  "ctrl PAGE_DOWN", "scrollRightChangeSelection",
                             "TAB", "selectNextColumnCell",
                       "shift TAB", "selectPreviousColumnCell",
                           "ENTER", "selectNextRowCell",
                     "shift ENTER", "selectPreviousRowCell",
                          "ctrl A", "selectAll",
                      "ctrl SLASH", "selectAll",
                 "ctrl BACK_SLASH", "clearSelection",
                          "ESCAPE", "cancel",
                              "F2", "startEditing",
                           "SPACE", "addToSelection",
                      "ctrl SPACE", "toggleAndAnchor",
                     "shift SPACE", "extendTo",
                "ctrl shift SPACE", "moveSelectionTo",
                              "F8", "focusHeader"
		});
		Object toolBarAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                              "UP", "navigateUp",
                           "KP_UP", "navigateUp",
                            "DOWN", "navigateDown",
                         "KP_DOWN", "navigateDown",
                            "LEFT", "navigateLeft",
                         "KP_LEFT", "navigateLeft",
                           "RIGHT", "navigateRight",
                        "KP_RIGHT", "navigateRight"
		});
		Object treeFocusInputMap = new UIDefaults.LazyInputMap(new Object[] {
                            "ADD", "expand",
                       "SUBTRACT", "collapse",
                         "ctrl C", "copy",
                         "ctrl V", "paste",
                         "ctrl X", "cut",
                           "COPY", "copy",
                          "PASTE", "paste",
                            "CUT", "cut",
                 "control INSERT", "copy",
                   "shift INSERT", "paste",
                   "shift DELETE", "cut",
                             "UP", "selectPrevious",
                          "KP_UP", "selectPrevious",
                       "shift UP", "selectPreviousExtendSelection",
                    "shift KP_UP", "selectPreviousExtendSelection",
                  "ctrl shift UP", "selectPreviousExtendSelection",
               "ctrl shift KP_UP", "selectPreviousExtendSelection",
                        "ctrl UP", "selectPreviousChangeLead",
                     "ctrl KP_UP", "selectPreviousChangeLead",
                           "DOWN", "selectNext",
                        "KP_DOWN", "selectNext",
                     "shift DOWN", "selectNextExtendSelection",
                  "shift KP_DOWN", "selectNextExtendSelection",
                "ctrl shift DOWN", "selectNextExtendSelection",
             "ctrl shift KP_DOWN", "selectNextExtendSelection",
                      "ctrl DOWN", "selectNextChangeLead",
                   "ctrl KP_DOWN", "selectNextChangeLead",
                          "RIGHT", "selectChild",
                       "KP_RIGHT", "selectChild",
                           "LEFT", "selectParent",
                        "KP_LEFT", "selectParent",
                        "PAGE_UP", "scrollUpChangeSelection",
                  "shift PAGE_UP", "scrollUpExtendSelection",
             "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                   "ctrl PAGE_UP", "scrollUpChangeLead",
                      "PAGE_DOWN", "scrollDownChangeSelection",
                "shift PAGE_DOWN", "scrollDownExtendSelection",
           "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                 "ctrl PAGE_DOWN", "scrollDownChangeLead",
                           "HOME", "selectFirst",
                     "shift HOME", "selectFirstExtendSelection",
                "ctrl shift HOME", "selectFirstExtendSelection",
                      "ctrl HOME", "selectFirstChangeLead",
                            "END", "selectLast",
                      "shift END", "selectLastExtendSelection",
                 "ctrl shift END", "selectLastExtendSelection",
                       "ctrl END", "selectLastChangeLead",
                             "F2", "startEditing",
                         "ctrl A", "selectAll",
                     "ctrl SLASH", "selectAll",
                "ctrl BACK_SLASH", "clearSelection",
                      "ctrl LEFT", "scrollLeft",
                   "ctrl KP_LEFT", "scrollLeft",
                     "ctrl RIGHT", "scrollRight",
                  "ctrl KP_RIGHT", "scrollRight",
                          "SPACE", "addToSelection",
                     "ctrl SPACE", "toggleAndAnchor",
                    "shift SPACE", "extendTo",
               "ctrl shift SPACE", "moveSelectionTo"
		});
		Object treeAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
                         "ESCAPE", "cancel"
		});
		Object fileChooserAncestorInputMap = new UIDefaults.LazyInputMap(new Object[] {
	                     "ESCAPE", "cancelSelection",
	                         "F2", "editFileName",
	                         "F5", "refresh",
	                 "BACK_SPACE", "Go Up"
		});
		
		//Text Components
		putComponentUIPropertyValue(table, "focusInputMap", fieldFocusInputMap, "TextField");
		putComponentUIPropertyValue(table, "focusInputMap", passwordFocusInputMap, "PasswordField");
		putComponentUIPropertyValue(table, "focusInputMap", multilineFocusInputMap, "TextArea");
		putComponentUIPropertyValue(table, "focusInputMap", multilineFocusInputMap, "TextPane");
		putComponentUIPropertyValue(table, "focusInputMap", multilineFocusInputMap, "EditorPane");
		
		//Buttons
		putComponentUIPropertyValue(table, "focusInputMap", buttonFocusInputMap, "Button");
		putComponentUIPropertyValue(table, "focusInputMap", buttonFocusInputMap, "CheckBox");
		putComponentUIPropertyValue(table, "focusInputMap", buttonFocusInputMap, "RadioButton");
		putComponentUIPropertyValue(table, "focusInputMap", buttonFocusInputMap, "ToggleButton");
		
		putComponentUIPropertyValue(table, "focusInputMap", listFocusInputMap, "List");
		putComponentUIPropertyValue(table, "focusInputMap", sliderFocusInputMap, "Slider");
		
		putComponentUIPropertyValue(table, "ancestorInputMap", comboBoxAncestorInputMap, "ComboBox");
		putComponentUIPropertyValue(table, "ancestorInputMap", desktopAncestorInputMap, "Desktop");
		putComponentUIPropertyValue(table, "ancestorInputMap", scrollBarAncestorInputMap, "ScrollBar");
		putComponentUIPropertyValue(table, "ancestorInputMap", scrollPaneAncestorInputMap, "ScrollPane");
		putComponentUIPropertyValue(table, "ancestorInputMap", splitPaneAncestorInputMap, "SplitPane");
		putComponentUIPropertyValue(table, "ancestorInputMap", spinnerAncestorInputMap, "Spinner");
		putComponentUIPropertyValue(table, "ancestorInputMap", tableAncestorInputMap, "Table");
		putComponentUIPropertyValue(table, "ancestorInputMap", toolBarAncestorInputMap, "ToolBar");
		putComponentUIPropertyValue(table, "ancestorInputMap", fileChooserAncestorInputMap, "FileChooser");
		
		putComponentUIPropertyValue(table, "focusInputMap", tabbedPaneFocusInputMap, "TabbedPane");
		putComponentUIPropertyValue(table, "ancestorInputMap", tabbedPaneAncestorInputMap, "TabbedPane");
		
		putComponentUIPropertyValue(table, "focusInputMap", treeFocusInputMap, "Tree");
		putComponentUIPropertyValue(table, "ancestorInputMap", treeAncestorInputMap, "Tree");
	}
	
	private String[] getEncodedFields () {
		return getEncodedFields(configFile);
	}
	public static synchronized String[] getEncodedFields(File configFile) {
		
		if (configFile != null) {
			try {
				List<String> linesConfig = Files.readLines(configFile);
				String[] encodedFields = Lists.listToArray(linesConfig, String.class);
				return encodedFields;
			}
			catch (Exception ex) {
				Console.printException(ex);
			}
		}
		return null;
	}
	
	public static String getUIName(Class<?> uiClass) {
		
		String uiName = null;
		if (uiClass == GeneralUIProperties.class) {
			uiName = GeneralUIProperties.UI_NAME;
		}
		else {
			uiName = UI_LCOMPONENTS_BUTTONS_MAP.get(uiClass);
			if (uiName == null)
				uiName = UI_LCOMPONENTS_OTHER_MAP.get(uiClass);
		}
		
		if (uiName == null)
			uiName = DEFAULT_UI_NAME;
		
		return uiName;
	}
	
	private void putComponentsUIPropertyValue(UIDefaults table, String propertyName, Object value) {
		putComponentsUIPropertyValue(table, propertyName, value, UI_COMPONENTS);
	}
	
	private void putComponentsUIPropertyValue(UIDefaults table, String propertyName, Object value, List<String> uiComponents) {
		for (int i = 0; i < uiComponents.size(); i++) {
			putComponentUIPropertyValue(table, propertyName, value, uiComponents.get(i));
		}
	}
	private void putComponentUIPropertyValue(UIDefaults table, String propertyName, Object value, String uiComponent) {
		String propertyKey = uiComponent + Constants.POINT + propertyName;
		table.put(propertyKey, createResourceValue(value));
	}
	
	protected void reloadDefaults(File configFile) {
		if (configFile != null)
			this.configFile = configFile;
		UISupport.uninstallComponentUIs();
		UISupportButtons.uninstallButtonUIs();
		initComponentDefaults(UIManager.getDefaults());
		initClassDefaults(UIManager.getDefaults());
		initSystemColorDefaults(UIManager.getDefaults());
		
	}
	
	@Override
	public void uninitialize() {
		super.uninitialize();
		
		TranslucentPopupFactory.uninstall();
		
		AppContext.getAppContext().remove(LButtonUI.L_BUTTON_UI_KEY);
		AppContext.getAppContext().remove(LCheckBoxUI.L_CHECK_BOX_UI_KEY);
		AppContext.getAppContext().remove(LLabelUI.L_LABEL_UI_KEY);
		AppContext.getAppContext().remove(LRadioButtonUI.L_RADIO_BUTTON_UI_KEY);
		AppContext.getAppContext().remove(LToggleButtonUI.L_TOGGLE_BUTTON_UI_KEY);
		
		generalUIProperties = null;
		UISupport.uninstallComponentUIs();
		UISupportButtons.uninstallButtonUIs();
	}

	@Override
	public Icon getDisabledIcon(JComponent component, Icon icon) {
		if (icon != null && icon instanceof ImageIcon) {
			
			ImageIcon imageIcon = (ImageIcon) icon;
			if (imageIcon.getImage() != null) {
				Color disabledForeground = component != null ? UISupport.getDisabledForeground(component) : new Color(140, 140, 140);
				//Oscurecemos primero la imagen para que luego se adapte bien a cualquier color (sólo si el color final no es demasiado claro)
				boolean obscureImageFirst = true;//Colors.getLuminance(disabledForeground) < 0.9;
				Image image = imageIcon instanceof PaintedImageIcon ? ((PaintedImageIcon) imageIcon).getImage(component) : imageIcon.getImage();
				disabledForeground = UISupport.optimizeForegroundForColorize(disabledForeground);
				return new ImageIcon(Icons.createColorizedImage(image, disabledForeground, obscureImageFirst));
				//return Icons.getColorizedIcon(imageIcon, disabledColor);
				//return super.getDisabledIcon(component, imageIcon);
			}
			else
				return Icons.getIconVoid(1, 1);
		}
		return null;
	}
	
	@Override
	public String getName() {
		return "Linaje";
	}

	@Override
	public String getID() {
		return "Linaje";
	}

	@Override
	public String getDescription() {
		return "Linaje Look and Feel";
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return true;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
}
