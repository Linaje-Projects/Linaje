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
package linaje.gui.cells;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.renderers.ActionsRenderer;
import linaje.gui.table.LTableObject;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;
import linaje.utils.Colors;
import linaje.utils.Dates;
import linaje.utils.Strings;
import linaje.utils.Utils;

/**
 * Label en la que renderizar los DataCell de los diferentes componentes
 * 
 * Revisar: Mirar la posibilidad de pintarlo como se pintan los LComponents (que cuando no cabe  el texto se degrade y no pinte "..." (clippedText))
 **/
@SuppressWarnings("serial")
public class LabelCell extends LPanel implements ActionsRenderer {
	
	public static final int TYPE_LABEL = 0;
	public static final int TYPE_CHECKBOX = 1;
	
	private int type = TYPE_LABEL;
	
	public static final String DEFAULT_SEPARATOR_ENCODE = Constants.HASH;
	
	private String separatorEncode = DEFAULT_SEPARATOR_ENCODE;
	
	private boolean selected = false;
	
	private Color foregroundCode = null;
	private Color foregroundTextAux = null;
	
	private Color backgroundCode = null;
	
	private Color selectedForeground = null;
	private Color selectedForegroundCode = null;
	private Color selectedForegroundTextAux = null;
	
	private Color selectedBackground = null;
	private Color selectedBackgroundCode = null;
	
	private Color selectedBorderColor = null;
	private Color borderColor = null;
	
	private String formatter = null;
	
	private boolean indentEnabled = true;
	private boolean descOverCode = false;
	private boolean hideComplexDesc = true;
	
	private boolean showCodesAlways = false;
	private boolean selectedBackgroundGradient = false;
	private boolean swapCodeDesc = false;
	
	public static final int MARGIN_DEFAULT = 2;
	public static final int MARGIN_DEFAULT_MULTICOLUMN = 5;
	
	private Insets margin = null;
	private boolean selectable = true;
	private String textSearch = null;
	
	private Vector<Rectangle> actionsRects = null;
	
	private Rectangle selectionRects = null;
	
	private String errorText = null;
	private boolean errorFound = false;
	
	private LLabel label = null;
	private LLabel labelCode = null;
	private LLabel labelTextAux = null;
	private LLabel labelIndent = null;
	
	public LabelCell() {
		super();
		initialize();
	}
	public LabelCell(int type) {
		this(type, Constants.VOID, null);
	}
	public LabelCell(int type, String encodedLabel) {
		this(type, encodedLabel, null);
	}
	public LabelCell(String encodedLabel) {
		this(TYPE_LABEL, encodedLabel, null);
	}
	public LabelCell(String encodedLabel, Icon icon) {
		this(TYPE_LABEL, encodedLabel, icon);
	}
	public LabelCell(int type, String encodedLabel, Icon icon) {
		super();
		setType(type);
		setEncodedLabel(encodedLabel);
		setIcon(icon);
		initialize();
	}
	
	public LabelCell(boolean indentEnabled) {
		this(indentEnabled, false);
	}
	public LabelCell(boolean indentEnabled, boolean descOverCode) {
		super();
		setIndentEnabled(indentEnabled);
		setDescOverCode(descOverCode);
		initialize();
	}
	
	private void initialize() {
		setForeground(GeneralUIProperties.getInstance().getColorText());
		setBackground(GeneralUIProperties.getInstance().getColorPanelsBrightest());
		if (getType() != TYPE_CHECKBOX) {
			setSelectedBorderColor(ColorsGUI.getColorRolloverDark());
			setSelectedBackground(ColorsGUI.getColorRollover());
			setSelectedBackgroundGradient(true);
		}
		setOpaque(false);
		setFont(getFont());
		setLayout(new GridBagLayout());
		setErrorText(Constants.NON_EXIST_ELEMENT);
		addComponents();
	}
	
	public static LabelCell createLabelCell(Object value, boolean isSelected, Color background) {
		return initLabelCell(null, value, isSelected, background);
	}
	public static LabelCell initLabelCell(LabelCell lCell, Object value, boolean isSelected, Color background) {
	
		if (lCell == null)
			lCell = new LabelCell();
	
		if (value == null)
			value = Constants.VOID;
	
		String code = null;
		String description = null;
		String textAux = null;
		
		String indentText = lCell.getIndentText();
		Icon icon = lCell.getIcon();
		Color foreground = lCell.getForeground();
		boolean enabled = lCell.isEnabled();
		
		if (value != null) {
	
			if (value instanceof DefaultMutableTreeNode) {
	
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (node.getUserObject() != null)
					value = node.getUserObject();
			}
			else if (value instanceof TreeNodeVector) {
				
				TreeNodeVector<?> node = (TreeNodeVector<?>) value;
				if (node.getUserObject() != null)
					value = node.getUserObject();
			}
			
			if (value instanceof LTableObject) {
				
				LTableObject<?> tableObject = (LTableObject<?>) value;
				value = tableObject.getText();
				indentText = Strings.getIndent(tableObject.getNivel());
				enabled = tableObject.isEnabled();
			}
			
			if (value instanceof DataCell) {

				DataCell dataCell = (DataCell) value;
				if (dataCell.getCode() instanceof Date) {

					Date date = (Date) dataCell.getCode();
					String format = lCell.getFormatter();
					if (format == null)
						format = Dates.FORMAT_DD_MMM_YYYY;
					description = Dates.getFormattedDate(date, format);
				}
				else {

					code = dataCell.getCode().toString();
					description = dataCell.getValue().toString();
					description = Strings.replace(description, Constants.LINE_SEPARATOR_DOLLAR, Constants.SPACE);
					//description = description.replaceAll(ConstantesGUI.REGEX_LINE_SEPARATOR_AMPLIADO, Constants.SPACE);
					
					//Añadimos la indentación según el nivel a la description
					indentText = Strings.getIndent(dataCell.getLevel());
					enabled = dataCell.isEnabled();
					icon = dataCell.getIcon();
				}
			}
			else if (value instanceof Component) {
				
				Component component = (Component) value;
	
				enabled = component.isEnabled();
				
				if (value instanceof JTextComponent) {
					
					JTextComponent textComponent = (JTextComponent) value;
					value = textComponent.getText();
				}
				else if (value instanceof JLabel) {
					JLabel label = (JLabel) value;
					lCell.setHorizontalTextPosition(label.getHorizontalTextPosition());
					icon = label.getIcon();
					
					value = label.getText();
				}
				
			}
			else if (value instanceof Entry<?, ?>) {
				
				Entry<?, ?> entry = (Entry<?, ?>) value;
				code = entry.getKey() != null ? entry.getKey().toString() : null;
				description = entry.getValue() != null ? entry.getValue().toString() : null;
			}
			
			if (description == null) {
	
				String texto = value.toString();
				String[] elements = Strings.split(texto, Constants.HASH);
				if (elements.length > 0)
					description = elements[0];
				if (elements.length > 1)
					code = elements[1];
				if (elements.length > 2)
					textAux = elements[2];
			}
			
			if (description == null)
				description = Constants.VOID;
			if (code == null)
				code = Constants.VOID;
			
			if (icon == null) {
				
				if (description.matches(Constants.REGEX_YES)) {
					description = Constants.YES;
					lCell.setHorizontalTextPosition(SwingConstants.LEFT);
					icon = Icons.getIconCheckMark(Icons.SIZE_ICONS, false, ColorsGUI.getColorPositive());
				}
				else if (description.matches(Constants.REGEX_NO)) {
					description = Constants.NO;
					lCell.setHorizontalTextPosition(SwingConstants.LEFT);
					icon = Icons.getIconX(Icons.SIZE_ICONS, 2, ColorsGUI.getColorNegative());
				}
			}
		}
		lCell.setIcon(icon);
		lCell.setForeground(foreground);
		lCell.setEnabled(enabled);
		lCell.setIndentText(indentText);
		
		lCell.setCode(code);
		lCell.setDescription(description);
		lCell.setTextAux(textAux);
		lCell.setBackground(background);
		lCell.setSelected(isSelected);
		
		lCell.setSize(lCell.getPreferredSize());
	
		return lCell;
	}
	
	@Override
	public boolean isOpaque() {
		//Pintaremos el degradado de la selección en paintComponent() para poder hacer un solo degradado de varias filas seleccionadas
		return (isSelected() && getType() != TYPE_CHECKBOX) || super.isOpaque();
	}
	
	@Override
	public Color getBackground() {
		Color background = isSelected() ? getSelectedBackground() : null;
		return background != null ? background : super.getBackground();
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		getLabel().setForeground(fg);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getLabel().setEnabled(enabled);
		getLabelCode().setEnabled(enabled);
		getLabelTextAux().setEnabled(enabled);
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		getLabel().setFont(font);
		getLabelCode().setFont(font);
		getLabelTextAux().setFont(font);
	}
	
	public void setFont(boolean enabled) {
		super.setEnabled(enabled);
		getLabel().setEnabled(enabled);
		getLabelCode().setEnabled(enabled);
		getLabelTextAux().setEnabled(enabled);
	}
	
	public LLabel getLabel() {
		if (label == null) {
			label = new LLabel();
			label.setTooltipEnabled(false);
		}
		return label;
	}
	public LLabel getLabelCode() {
		if (labelCode == null) {
			labelCode = new LLabel() {
				@Override
				public boolean isVisible() {
					return isShowingCodes();
				}
				@Override
				public boolean isOpaque() {
					return isSelected();
				}
				@Override
				public boolean isGradientBackground() {
					return isSelectedBackgroundGradient();
				}
				@Override
				public Color getBackground() {
					Color bg = getCurrentColor(getBackgroundCode(), getSelectedBackgroundCode(), null);
					if (bg == null)
						bg = getSelectedBorderColor();
					
					if (bg != null && isErrorFound()) {
						bg = Colors.colorize(bg, ColorsGUI.getColorNegative());
						bg = Colors.optimizeColor(bg, getForeground());
					}
					return bg; 
				}
				@Override
				public Color getForeground() {
					return getCurrentColor(getForegroundCode(), getSelectedForegroundCode(), null);
				}
			};
			labelCode.setTooltipEnabled(false);
		}
		
		return labelCode;
	}
	public LLabel getLabelTextAux() {
		if (labelTextAux == null) {
			labelTextAux = new LLabel() {
				@Override
				public Color getForeground() {
					return getCurrentColor(getForegroundTextAux(), getSelectedForegroundTextAux(), null);
				}
			};
			labelTextAux.setTooltipEnabled(false);
		}
		return labelTextAux;
	}
	public LLabel getLabelIndent() {
		if (labelIndent == null) {
			labelIndent = new LLabel();
		}
		return labelIndent;
	}
	
	public String getSeparatorEncode() {
		if (separatorEncode == null)
			separatorEncode = DEFAULT_SEPARATOR_ENCODE;
		return separatorEncode;
	}
	public void setSeparatorEncode(String separatorEncode) {
		this.separatorEncode = separatorEncode;
	}
	
	public static String getDescription(String encodedLabel) {
		return getDescription(encodedLabel, null);
	}
	public static String getDescription(String encodedLabel, String separatorEncode) {
		
		if (separatorEncode == null)
			separatorEncode = DEFAULT_SEPARATOR_ENCODE;
		
		String[] elements = Strings.split(encodedLabel, separatorEncode);
		if (elements.length > 0)
			return elements[0];
		else
			return Constants.VOID;
	}
	
	public static String getCode(String encodedLabel) {
		return getCode(encodedLabel, null);
	}
	public static String getCode(String encodedLabel, String separatorEncode) {
	
		if (separatorEncode == null)
			separatorEncode = DEFAULT_SEPARATOR_ENCODE;
		
		String[] elements = Strings.split(encodedLabel, separatorEncode);
		if (elements.length > 1)
			return elements[1];
		else
			return Constants.VOID;
	}
	
	public String getEncodedData() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDescription());
		if (!getCode().equals(Constants.VOID)) {
			sb.append(getSeparatorEncode());
			sb.append(getCode());
		}
		if (!getTextAux().equals(Constants.VOID)) {
			sb.append(getSeparatorEncode());
			sb.append(getTextAux());
		}
		
		return sb.toString();
	}
	
	public void setEncodedLabel(String encodedLabel) {
		
		try {
	
			if (encodedLabel == null)
				encodedLabel = Constants.VOID;
			
			String code = Constants.VOID;
			String description = Constants.VOID;
			String textAux = Constants.VOID;
			
			String[] elements = Strings.split(encodedLabel, getSeparatorEncode());
			
			int numElements = elements.length;
			if (numElements > 3) {
				if (isHideComplexDesc())
					description = "...";
				else
					description = encodedLabel;
			}
			else if (numElements > 0) {
				description = elements[0];
				if (numElements > 1)
					code = elements[1];
				if (numElements > 2)
					textAux = elements[2];
			}
				
			setDescription(description);
			setCode(code);
			setTextAux(textAux);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public Insets getMargin() {
		if (margin == null)
			 margin = new Insets(1,MARGIN_DEFAULT,1,MARGIN_DEFAULT);
		return margin;
	}
	public void setMargin(Insets margin) {
		this.margin = margin;
	}
	
	public boolean isSwapCodeDesc() {
		return swapCodeDesc;
	}
	public void setSwapCodeDesc(boolean swapCodeDesc) {
		boolean oldValue = this.swapCodeDesc;
		boolean newValue = swapCodeDesc;
		if (Utils.propertyChanged(oldValue, newValue)) {
			String code = getDescription();
			String description =  getCode();
			setCode(code);
			setDescription(description);
			this.swapCodeDesc = swapCodeDesc;
			addComponents();
		}
	}
	
	public String getCode() {
		return isSwapCodeDesc() ? getLabel().getText() : getLabelCode().getText();
	}
	public String getDescription() {
		return isSwapCodeDesc() ? getLabelCode().getText() : getLabel().getText();
	}
	public String getTextAux() {
		return getLabelTextAux().getText();
	}
	public String getIndentText() {
		return getLabelIndent().getText();
	}
	
	public void setCode(String code) {
		if (isSwapCodeDesc())
			getLabel().setText(code);
		else
			getLabelCode().setText(code);
		
		setErrorFound(getErrorText() != null && (getCode().contains(getErrorText()) || getDescription().contains(getErrorText())));
	}	
	public void setDescription(String description) {
		if (isSwapCodeDesc())
			getLabelCode().setText(description);
		else
			getLabel().setText(description);
		
		setErrorFound(getErrorText() != null && (getCode().contains(getErrorText()) || getDescription().contains(getErrorText())));
	}
	public void setTextAux(String textAux) {
		getLabelTextAux().setText(textAux);
	}
	public void setIndentText(String indentText) {
		getLabelIndent().setText(indentText);
	}
	
	public Icon getIcon() {
		return getLabel().getIcon();
	}
	public void setIcon(Icon icon) {
		getLabel().setIcon(icon);
	}
	
	public int getHorizontalTextPosition() {
		return getLabel().getHorizontalTextPosition();
	}
	public void setHorizontalTextPosition(int textPosition) {
		getLabel().setHorizontalTextPosition(textPosition);
	}
	
	public boolean isSelected() {
		return selected && isSelectable();
	}
	public void setIndentEnabled(boolean indentEnabled) {
		this.indentEnabled = indentEnabled;
	}
		
	public void setSelected(boolean selected) {
	
		if (!isEnabled() && getType() != TYPE_CHECKBOX)
			selected = false;
	
		this.selected = selected;
	
		if (getType() == TYPE_CHECKBOX) {
	
			Icon icono = selected ? isEnabled() ? Icons.STATEICON_CHECK_ON : Icons.STATEICON_CHECK_ON.getDisabledIcon() : isEnabled() ? Icons.STATEICON_CHECK_OFF : Icons.STATEICON_CHECK_OFF.getDisabledIcon();
			setIcon(icono);
		}
	}
	
	public boolean isIndentEnabled() {
		return indentEnabled;
	}
	public boolean isDescOverCode() {
		return descOverCode;
	}
	
	public int getType() {
		return type;
	}
	
	public void setDescOverCode(boolean descOverCode) {
		boolean oldValue = this.descOverCode;
		boolean newValue = descOverCode;
		if (Utils.propertyChanged(oldValue, newValue)) {
			this.descOverCode = descOverCode;
			addComponents();
		}
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
	public String getFormatter() {
		return formatter;
	}
	public boolean isHideComplexDesc() {
		return hideComplexDesc;
	}
	public void setHideComplexDesc(boolean hideComplexDesc) {
		this.hideComplexDesc = hideComplexDesc;
	}
	public boolean isShowingCodes() {
		return isShowCodesAlways() || AppGUI.getCurrentAppGUI().isShowingCodes();
	}
	public boolean isShowCodesAlways() {
		return showCodesAlways;
	}
	public void setShowCodesAlways(boolean showCodesAlways) {
		this.showCodesAlways = showCodesAlways;
	}
	public Color getForegroundCode() {
		if (foregroundCode == null)
			foregroundCode = ColorsGUI.getColorPositive();
		return foregroundCode;
	}
	public void setForegroundCode(Color foregroundCode) {
		this.foregroundCode = foregroundCode;
	}
	
	public Color getBackgroundCode() {
		return backgroundCode;
	}
	public void setBackgroundCode(Color backgroundCode) {
		this.backgroundCode = backgroundCode;
	}
	public Color getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	public Color getSelectedForeground() {
		return selectedForeground;
	}
	public void setSelectedForeground(Color selectedForeground) {
		this.selectedForeground = selectedForeground;
	}
	public Color getSelectedForegroundCode() {
		return selectedForegroundCode;
	}
	public void setSelectedForegroundCode(Color selectedForegroundCode) {
		this.selectedForegroundCode = selectedForegroundCode;
	}
	public Color getSelectedBackground() {
		return selectedBackground;
	}
	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackground = selectedBackground;
	}
	public Color getSelectedBackgroundCode() {
		return selectedBackgroundCode;
	}
	public void setSelectedBackgroundCode(Color selectedBackgroundCode) {
		this.selectedBackgroundCode = selectedBackgroundCode;
	}
	public Color getSelectedBorderColor() {
		return selectedBorderColor;
	}
	public void setSelectedBorderColor(Color selectedBorderColor) {
		this.selectedBorderColor = selectedBorderColor;
	}
	public boolean isSelectedBackgroundGradient() {
		return selectedBackgroundGradient;
	}
	public void setSelectedBackgroundGradient(boolean selectedBackgroundGradient) {
		this.selectedBackgroundGradient = selectedBackgroundGradient;
	}
	public Color getForegroundTextAux() {
		if (foregroundTextAux == null)
			foregroundTextAux = ColorsGUI.getColorInfo();
		return foregroundTextAux;
	}
	public void setForegroundTextAux(Color foregroundTextAux) {
		this.foregroundTextAux = foregroundTextAux;
	}
	public Color getSelectedForegroundTextAux() {
		return selectedForegroundTextAux;
	}
	public void setSelectedForegroundTextAux(Color selectedForegroundTextAux) {
		this.selectedForegroundTextAux = selectedForegroundTextAux;
	}
		
	private Color getCurrentColor(Color color, Color colorSelected, Color colorDisabled) {
		
		Color currentColor = null;
		
		if (!isEnabled())
			currentColor = colorDisabled;
		else if (isSelected())
			currentColor = colorSelected;
		
		if (currentColor == null)
			currentColor = color;
		
		return currentColor;
	}
	
	public void copyPropertiesFrom(LabelCell labelCell) {
	
		if (labelCell != null) {
			
			setType(labelCell.getType());
			setIndentEnabled(labelCell.isIndentEnabled());
			setDescOverCode(labelCell.isDescOverCode());
			setFormatter(labelCell.getFormatter());
			setSelectedBackgroundGradient(labelCell.isSelectedBackgroundGradient());
			setShowCodesAlways(labelCell.isShowCodesAlways());
			setSwapCodeDesc(labelCell.isSwapCodeDesc());
			setMargin(new Insets(labelCell.getMargin().top, labelCell.getMargin().left, labelCell.getMargin().bottom, labelCell.getMargin().right));
			setSelectable(labelCell.isSelectable());
									
			setForeground(labelCell.getForeground());
			setForegroundCode(labelCell.getForegroundCode());
			setForegroundTextAux(labelCell.getForegroundTextAux());
			
			setOpaque(labelCell.isOpaque());
			
			setBackground(labelCell.getBackground());
			setBackgroundCode(labelCell.getBackgroundCode());
			
			setSelectedBackground(labelCell.getSelectedBackground());
			setSelectedBackgroundCode(labelCell.getSelectedBackgroundCode());
			setSelectedBorderColor(labelCell.getSelectedBorderColor());
			setSelectedForeground(labelCell.getSelectedForeground());
			setSelectedForegroundCode(labelCell.getSelectedForegroundCode());
			setSelectedForegroundTextAux(labelCell.getSelectedForegroundTextAux());
			
			//
			setEnabled(labelCell.isEnabled());
			setFont(labelCell.getFont());
			setCode(labelCell.getCode());
			setDescription(labelCell.getDescription());
			setSelected(labelCell.isSelected());
			setTextAux(labelCell.getTextAux());
			//
			setTextSearch(labelCell.getTextSearch());
			setIndentText(labelCell.getIndentText());
			setIcon(labelCell.getIcon());
			//
			setSize(labelCell.getPreferredSize());
		}
	}
	
	public Rectangle getBoundsIcon() {
		
		Rectangle boundsIcon = null;
		if (getIcon() != null) {
	
			int widthIndentacion = 0;
			if (isIndentEnabled())
				widthIndentacion = getTextWidth(getIndentText());
	
			Insets insetsExtra = new Insets(getMargin().top, getMargin().left + widthIndentacion, 0, 0);
			Point location = UISupportUtils.getLocation(this, getIcon(), SwingConstants.WEST, insetsExtra);
			boundsIcon = new Rectangle(location, new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));
		}
		
		return boundsIcon;
	}
	public boolean isMouseOverIcon(Point mousePosition) {
		
		boolean mouseOverIcon = false;
		Rectangle boundsIcono = getBoundsIcon();
		
		if (boundsIcono != null && mousePosition != null) {
			mouseOverIcon = boundsIcono.contains(mousePosition);
		}
		
		return mouseOverIcon;
	}
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	public String getTextSearch() {
		return textSearch;
	}
	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}
	
	public void setFontName(String fontName) {
		UtilsGUI.setFontName(this, fontName);
	}
	public void setFontSize(int fontSize) {
		UtilsGUI.setFontSize(this, fontSize);
	}
	public void setFontStyle(int fontStyle) {
		UtilsGUI.setFontStyle(this, fontStyle);
	}
	public void setFontLayout(int fontLayout) {
		UtilsGUI.setFontLayout(this, fontLayout);
	}
	
	@Override
	public Vector<Rectangle> getActionsRects() {
		if (actionsRects == null) {
			actionsRects = new Vector<Rectangle>();
		}
		return actionsRects;
	}
	
	public Rectangle getSelectionRects() {
		return selectionRects;
	}
	public void setSelectionRects(Rectangle selectionRects) {
		this.selectionRects = selectionRects;
	}
	
	public String getErrorText() {
		return errorText;
	}
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}
	
	public boolean isErrorFound() {
		return errorFound;
	}
	private void setErrorFound(boolean errorFound) {
		this.errorFound = errorFound;
	}
	
	private void addComponents() {
	
		boolean descOverCode = isDescOverCode() && !swapCodeDesc || !isDescOverCode() && swapCodeDesc;
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets.top = 1;
		gbc.insets.bottom = 1;
		gbc.insets.left = getMargin().left;
		add(getLabelIndent(), gbc);
		
		gbc.gridx = 2;
		gbc.fill = descOverCode ? GridBagConstraints.NONE : GridBagConstraints.BOTH;
		gbc.weightx = descOverCode ? 0 : 1;
		gbc.insets.top = 1;
		gbc.insets.bottom = 1;
		gbc.insets.right = getMargin().right;
		add(getLabel(), gbc);
		
		gbc.gridx = 3;
		gbc.fill = descOverCode ? GridBagConstraints.BOTH : GridBagConstraints.NONE;
		gbc.weightx = descOverCode ? 1 : 0;
		add(getLabelCode(), gbc);
		
		gbc.gridx = 4;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		add(getLabelTextAux(), gbc);
	}
	
	public void paintComponent(Graphics g) {
		
		Color background = paintBackground(g);
		getUI().paint(g, this);
		paintHighlightSearchBold(g, background);
	}
	
	protected Color paintBackground(Graphics g) {
	
		Color background = null;
		
		try {
	
			background = isOpaque() ? getBackground() : ColorsGUI.getFirstOpaqueParentBackground(this);//getCurrentColor(getBackground(), getSelectedBackground(), null);
			Color borderColor = getCurrentColor(getBorderColor(), getSelectedBorderColor(), null);
			
			if (isErrorFound()) {
				if (background == null || Colors.getLuminance(background) > 0.95)
					background = Color.lightGray;
				
				background = Colors.colorize(background, ColorsGUI.getColorNegative());
				background = Colors.optimizeColor(background, getForeground());
				
				if (borderColor != null)
					borderColor = Colors.colorize(borderColor, ColorsGUI.getColorNegative());
			}
			
			boolean isFirstRowSelected = true;
			boolean isLastRowSelected = true;
			
			if (isOpaque() || isErrorFound()) {
				//Pintamos el fondo
				boolean paintGradientBackground = isSelected() && isSelectedBackgroundGradient();
				Rectangle gradientRects = null;
				Rectangle labelPaintRects = new Rectangle(0,0,getBounds().width, getBounds().height);
				if (getSelectionRects() == null || !getSelectionRects().contains(getBounds()))
					gradientRects = labelPaintRects;
				else {
					isFirstRowSelected = getBounds().y - getSelectionRects().y < 4;
					isLastRowSelected = getSelectionRects().y + getSelectionRects().height - getBounds().y - getBounds().height < 4;
					gradientRects = new Rectangle(0, getSelectionRects().y - getBounds().y, getBounds().width, getSelectionRects().height);
				}
				
				if (paintGradientBackground) {
					Paint gp = GraphicsUtils.getGradientPaint(gradientRects, background, GraphicsUtils.GRADIENT_TYPE_VERTICAL);
					GraphicsUtils.fillRect(g, labelPaintRects, gp, 0);
				}
				else
					GraphicsUtils.paintBackground(g, this, background);
			}
			if (borderColor != null) {
	
				g.setColor(borderColor);
				if (isFirstRowSelected)
					g.drawLine(0, 0, getWidth()-1, 0);
				if (isLastRowSelected)
					g.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
			}
			
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
		return background;
	}
	
	protected void paintHighlightSearchBold(Graphics g, Color background) {
		try {
			
			boolean paintGradientBackground = isSelected() && isSelectedBackgroundGradient();
			
			if (!paintGradientBackground && getTextSearch() != null && !getTextSearch().equals(Constants.VOID)) {
				Rectangle textRects = getLabel().getTextRectTrim();
				textRects.x += getLabel().getX();
				textRects.y += getLabel().getY();
				GraphicsUtils.paintHighlightSearchBold(g, textRects.x, textRects.y, getDescription(), getTextSearch(), getForeground(), background);
			}

			if (!paintGradientBackground && getTextSearch() != null && !getTextSearch().equals(Constants.VOID)) {
				Rectangle textRects = getLabelCode().getTextRectTrim();
				textRects.x += getLabelCode().getX();
				textRects.y += getLabelCode().getY();
				GraphicsUtils.paintHighlightSearchBold(g, textRects.x, textRects.y, getCode(), getTextSearch(), getForegroundCode(), getBackgroundCode());
			}
			
			if (!paintGradientBackground && getTextSearch() != null && !getTextSearch().equals(Constants.VOID)) {
				Rectangle textRects = getLabelTextAux().getTextRectTrim();
				textRects.x += getLabel().getX();
				textRects.y += getLabel().getY();
				GraphicsUtils.paintHighlightSearchBold(g, textRects.x, textRects.y, getTextAux(), getTextSearch(), getForegroundTextAux(), background);
			}
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
}
