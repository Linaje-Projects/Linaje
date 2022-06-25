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
package linaje.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linaje.App;
import linaje.gui.AppGUI;
import linaje.gui.LCombo;
import linaje.gui.LList;
import linaje.gui.LTextField;
import linaje.gui.cells.LabelCell;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.ui.ButtonUIProperties;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Lists;
import linaje.utils.Numbers;
import linaje.utils.ReferencedColor;
import linaje.utils.Utils;

@SuppressWarnings("serial")
public class UIColorChooserPanel extends AbstractColorChooserPanel {

	private LList<ReferencedColor> list = null;
	private JScrollPane scrollPane = null;
	//private LinkedHashMap<String, Color> colors = null;
	private JSlider slider = null;
	private LCombo<Object> comboSources = null;
	private LabelTextField lblTxtSliderValue = null;
	
	private boolean selectingColor = false;
	private boolean building = true;
	
	public UIColorChooserPanel() {
		super();
	}

	public LCombo<Object> getComboSources() {
		if (comboSources == null) {
			comboSources = new LCombo<>();
			comboSources.addItem(GeneralUIProperties.getInstance());
			
			Collection<ButtonUIProperties> listUIcomps = UISupportButtons.getDefaultButtonUIPropertiesMap().values();
			for (Iterator<ButtonUIProperties> iterator = listUIcomps.iterator(); iterator.hasNext();) {
				ButtonUIProperties componentUIProperties = iterator.next();
				comboSources.addItem(componentUIProperties);
			}
			
			LCellRenderer<Object> renderCombo = new LCellRenderer<Object>() {
				@Override
				public LabelCell getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					String componentName = App.getObjectName(value);
					return super.getListCellRendererComponent(list, componentName, index, isSelected, cellHasFocus);
				}
			};
			comboSources.setRenderer(renderCombo);
			comboSources.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						refreshList(e.getItem());
					}
				}
			});
		}
		return comboSources;
	}
	
	private void refreshList(Object selectedItem) {
		String childFieldColors = null;
		if (selectedItem instanceof ButtonUIProperties)
			childFieldColors = ButtonUIProperties.PROPERTY_lButtonProperties;
		int oldSelectedIndex = getList().getSelectedIndex();
		List<ReferencedColor> colorsList = ReferencedColor.getColors(selectedItem, childFieldColors);
		getList().setElements(colorsList);
		if (!building && oldSelectedIndex != -1 && !colorsList.isEmpty())
			getList().setSelectedIndex(0);
	}
	
	@Override
	public void updateChooser() {
		
		if (!selectingColor) {
			getList().clearSelection();
			
			if (getColorFromModel() != null && getColorFromModel() instanceof ReferencedColor) {
				ReferencedColor refColor = (ReferencedColor) getColorFromModel();
				if (getComboSources().getItems().contains(refColor.getSource())) {
					getComboSources().setSelectedItem(refColor.getSource());
					List<ReferencedColor> listColors = getList().getElements();
					boolean colorFound = false;
					for (int i = 0; i < listColors.size() && !colorFound; i++) {
						ReferencedColor listColor = listColors.get(i);
						if (listColor.getPath().equals(refColor.getPath())) {
							getList().setSelectedValue(listColor, true);
							getSlider().setValue((int)(refColor.getLuminanceFactor()*100));
							JTabbedPane tabbedPane = (JTabbedPane) UtilsGUI.getParentInstanceOf(this, JTabbedPane.class);
							if (tabbedPane != null) {
								tabbedPane.setSelectedComponent(this.getParent());
							}
							colorFound = true;
						}
					}
				}
			}
		}
	}

	@Override
	protected void buildChooser() {
		
		setLayout(new BorderLayout());
		add(getComboSources(), BorderLayout.NORTH);
		add(getScrollPane(), BorderLayout.CENTER);
		
		GridBagConstraints gbcSlider = new GridBagConstraints();
		gbcSlider.gridx = 1;
		gbcSlider.gridy = 1;
		gbcSlider.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints gbcTxt = new GridBagConstraints();
		gbcTxt.gridx = 1;
		gbcTxt.gridy = 2;
		gbcTxt.fill = GridBagConstraints.NONE;
		
		JPanel panelSouth = new JPanel(new GridBagLayout());
		panelSouth.add(getSlider(), gbcSlider);
		panelSouth.add(getLblTxtSliderValue(), gbcTxt);
		
		add(panelSouth, BorderLayout.SOUTH);
		
		int w = Math.round(getPreferredSize().width*2.0f);
		int h = AppGUI.getFont().getSize()*16;
		Dimension prefSize = new Dimension(w, h); 
		setSize(prefSize);
		setPreferredSize(prefSize);
		refreshList(getComboSources().getSelectedItem());
		building = false;
	}

	@Override
	public String getDisplayName() {
		return "UI colors";
	}

	@Override
	public Icon getSmallDisplayIcon() {
		return null;
	}

	@Override
	public Icon getLargeDisplayIcon() {
		return null;
	}
	
	public JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider(JSlider.HORIZONTAL, -70, 70, 0);
			
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					changeSelectedColor();
				}
			});
		}
		return slider;
	}
	
	private LabelTextField getLblTxtSliderValue() {
		if (lblTxtSliderValue == null) {
			lblTxtSliderValue = new LabelTextField();
			lblTxtSliderValue.getTextField().setType(LTextField.TYPE_NUMBER);
			lblTxtSliderValue.getTextField().setDecimals(0);
			lblTxtSliderValue.getTextField().getFormattedData().setPostfix(" %");
			lblTxtSliderValue.getTextField().setMaxValue(70);
			lblTxtSliderValue.getTextField().setMinValue(-70);
			//lblTxtSliderValue.setAutoSizeLabel(true);
			lblTxtSliderValue.getTextField().getFormattedData().addPropertyChangeListener(new PropertyChangeListener() {
				
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("value"))
						getSlider().setValue(Numbers.getNumberValue(evt.getNewValue().toString()).intValue());
				}
			});
		}
		return lblTxtSliderValue;
	}
	
	private LList<ReferencedColor> getList() {
		if (list == null) {
			list = new LList<>();
			list.setCellRenderer(new ListCellRenderer<ReferencedColor>() {

				@Override
				public Component getListCellRendererComponent(
						JList<? extends ReferencedColor> list, ReferencedColor value, int index, boolean isSelected, boolean cellHasFocus) {

					Color color = value.getColor();
					String colorFieldName = Lists.getLastElement(value.getColorFieldTree()).getName();
					colorFieldName = colorFieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
					colorFieldName = colorFieldName.replaceAll("[Cc]olor", Constants.VOID);
					
					String colorFieldDesc =	Constants.SPACE + Colors.encode(color);
					if (color instanceof ReferencedColor) {
						ReferencedColor refColor = (ReferencedColor) color;
						colorFieldDesc = colorFieldDesc + Constants.SPACE + Colors.encode(refColor.getColor());
					}
					LabelCell label = new LabelCell(colorFieldName);
					label.setCode(colorFieldDesc);
					label.setOpaque(true);
					label.setBackground(color);
					label.setForeground(Colors.isColorDark(color) ? Color.white : Color.black);
					label.setShowCodesAlways(true);
					label.setDescOverCode(true);
					label.setForegroundCode(Colors.isColorDark(color) ? Colors.darker(label.getForeground(), 0.1) : Colors.brighter(label.getForeground(), 0.3));
					label.setFontStyle(Font.BOLD);
					label.getLabelCode().setFont(Utils.getFontWithStyle(label.getFont(), Font.PLAIN));
					if (isSelected) {
						label.setBorder(BorderFactory.createLineBorder(label.getForeground(), 1));
					}
					return label;
				}
			});
			
			list.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						getSlider().setValue(0);
						changeSelectedColor();
					}
				}
			});
		}
		return list;
	}
	
	private void changeSelectedColor() {
		try {
			selectingColor = true;
			boolean colorSelected = getList().getSelectedValue() != null;
			if (colorSelected) {
				try {	
					ReferencedColor selectedColor = getList().getSelectedValue();
					int sliderValue = getSlider().getValue();
					double luminanceFactor = sliderValue/100d;
					ReferencedColor modifiedUIColor = new ReferencedColor(selectedColor.getSource(), selectedColor.getColorFieldTree(), luminanceFactor);
					
					Color oldColor = getColorFromModel();
					if (oldColor.getRGB() == modifiedUIColor.getRGB() && !oldColor.toString().equals(modifiedUIColor.toString())) {
						//El colorSelectionModel del ColorChooser no detecta cuando cambiamos un referencedColor con RGB igual que el antiguo,
						//por lo que lo tenemos que detectar aquí y provocar un cambio previo
						Color tempColor = new Color(oldColor.getRGB() != Color.white.getRGB() ? Color.white.getRGB() : Color.black.getRGB());
						getColorSelectionModel().setSelectedColor(tempColor);
					}
					
					getColorSelectionModel().setSelectedColor(modifiedUIColor);
					getLblTxtSliderValue().getTextField().setValue(sliderValue);
					String label = sliderValue == 0 ? "Same luminance" : sliderValue < 0 ? "Brighter" : "Darker";
					getLblTxtSliderValue().setTextLabel(label);
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
			
			getSlider().setEnabled(colorSelected);
			getLblTxtSliderValue().setEnabled(colorSelected);
		}
		finally {
			selectingColor = false;
		}
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}
}
