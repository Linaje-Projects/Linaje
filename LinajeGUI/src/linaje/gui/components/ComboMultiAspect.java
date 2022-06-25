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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LCheckBox;
import linaje.gui.LCombo;
import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.LRadioButton;
import linaje.gui.LTextField;
import linaje.gui.LToggleButton;
import linaje.gui.RoundedBorder;
import linaje.gui.ToolTip;
import linaje.gui.cells.DataCell;
import linaje.gui.cells.LabelCell;
import linaje.gui.layouts.HorizBagLayout;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Dates;
import linaje.utils.FormattedData;
import linaje.utils.Lists;
import linaje.utils.Numbers;

@SuppressWarnings("serial")
public class ComboMultiAspect extends LPanel implements ItemListener, ItemSelectable, ListSelectionListener {
	//
	public static final int ASPECT_DEFAULT = 0;
	public static final int ASPECT_MULTISELECT = 1;	
	public static final int ASPECT_TEXTFIELD_CONTAINER = 2;
	public static final int ASPECT_RADIOBUTTON = 3;
	public static final int ASPECT_TOGGLE = 4;
	public static final int ASPECT_TOGGLE_MIN = 5;
	public static final int ASPECT_BUTTON_TABLE = 6;
	public static final int ASPECT_TABLE = 7;
	public static final int ASPECT_MULTICOMBO_DATES = 8;//Hay que rellenar el combo con fechas
	public static final int ASPECT_YES_NO = 9;//Se ignorarán los items del combo
	public static final int ASPECT_VOID = 10;
	private static final int ASPECT_TEXTFIELD = 11;//Este aspect es privado porque se usará automáticamente cuando el combo sólo tenga un elemento o esté deshabilitado
		
	//
	private int aspect = ASPECT_DEFAULT;
	private int aspectVisualized = ASPECT_DEFAULT;
	
	private int listNumberOfColumns = 1;//Es el número de columnas que se verán a la vez cuando tenemos aspecto de lista (combo vertical)
	private boolean comboVertical = false;
	private boolean enabled = true;
	private boolean visibleCodes = false;
	//
	public static final String SEPARATOR_TIP = "-TIP-";
	
	private DataCell selectedItem = null;
	private String name = "Combo";
	
	private Vector<DataCell> items = null;
	private AbstractButton[] radioButtons = new AbstractButton[0];
	private boolean containsError = false;
	
	private List<String> headerNames = null;//Si tenemos aspecto tabla y queremos mas de una cabecera (los items tendrán que ser acordes al número de cabeceras)
	private String dataSourceName = null;//Se mostrará cuando AppGUI.getCurrentAppGUI().isDesignTime()
	private FormattedData formattedData = null;
	private List<Component> selectableComponentes = null;
	
	protected EventListenerList listenerList = new EventListenerList();
		
	//Components
	private LCombo<DataCell> combo = null;
	private MultiComboDates multiComboDates = null;
	private LTextField textField = null;
	private ComboButtonTable<DataCell> comboButtonTable = null;
	private JPanel panelRadioButton = null;
	private LTextFieldContainer lTextFieldContainer = null;
	private JScrollPane scrollPaneList = null;
	private LList<DataCell> list = null;
	private TableItemsCombo<DataCell> tableItemsCombo = null;
	private ComboMultiSelect<DataCell> comboMultiSelect = null;
	private LCheckBox checkBox = null;
	private JPanel listsContainer = null;
	
	private boolean selectingItem = false;
	
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			String propertyName = evt.getPropertyName();
			//Object oldValue = evt.getOldValue();
			//Object newValue = evt.getNewValue();
			
			if (propertyName.equals(FormattedData.PROPERTY_FORMATTED_TEXT)) {
				formattedTextChanged();				
			}
			else if (propertyName.equals(FormattedData.PROPERTY_PERIOD)) {
				updatePeriodComponents();
			}
			else if (propertyName.equals(FormattedData.PROPERTY_VALUE)) {
				if (!selectingItem && getAspect() == ASPECT_TEXTFIELD_CONTAINER)
					setSelectedItem(getSelectedItem());
			}
			//firePropertyChange(propertyName, oldValue, newValue);
		}
	};
	
	public ComboMultiAspect() {
		super();
		initialize();
	}
	
	private void updateAspect() {
	
	   int aspect = getAspect();
	   if (aspect != ASPECT_VOID && aspect != ASPECT_MULTISELECT) {
	
		   if (!AppGUI.getCurrentAppGUI().isDesignTime()) {
	
	    	   if (!isEnabled()
			    || (getItemCount() <= 1 && aspect != ASPECT_TEXTFIELD_CONTAINER && aspect != ASPECT_TABLE)) {   
			    	aspect = ASPECT_TEXTFIELD;
			    }
			}
			else if (!isEnabled()) {
				aspect = ASPECT_TEXTFIELD;
			}
	   }
	
	   setAspectVisualized(aspect);
	   
	   if (comboMultiSelect != null) {
		   getComboMultiSelect().setEnabled(isEnabled());
	   }
	}
	
	private void updateComponents() {
	
		finalizeConnections();
		
		try {
	
			this.selectedItem = null;
			
			getCombo().removeAllItems();
			
			if (multiComboDates != null)
				getMultiComboDates().clearDates();
			
			if (textField != null)
				getTextField().setText(Constants.VOID);
	
			if (comboButtonTable != null)
				getComboButtonTable().getTextFieldSelectedItem().setText(Constants.VOID);
	
			if (comboMultiSelect != null)
				getComboMultiSelect().removeAllItems();
			
			//Insertamos todos los items de golpe en lugar de hacer addItem() para que sea mas rapido
			for (int i = 0; i < getItemCount(); i++) {
	
				DataCell itemDataCell = getItemAt(i);
	
				getCombo().addItem(itemDataCell);
				if (multiComboDates != null && itemDataCell.getCode() instanceof Date)
					getMultiComboDates().addItem((Date) itemDataCell.getCode());
				if (comboMultiSelect != null)
					getComboMultiSelect().addItem(itemDataCell);
			}
			
			if (getAspectVisualized() != ASPECT_MULTISELECT)
				updateAspect();
			
			if (panelRadioButton != null)
				updateRadioButtons();
				
			if (scrollPaneList != null)
				updateList();
			
			if (tableItemsCombo != null)
				updateTableItemsCombo();	
				
			updatePeriodComponents();		
			setVisibleCodes(getVisibleCodes());	
			validate();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		initConnections();
	
		//Seleccionamos el primer elemento enabled que encontremos
		selectItemsDefault();
	}
	
	private void updateList() {
		try {
			if (scrollPaneList != null) {
				getList().setElements(getItems());
			}
			setListNumberOfColumns(getListNumberOfColumns());
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void updateRadioButtons() {
	
		try {
	
			if (panelRadioButton != null) {
	
				finalizeRadioButons();
				ButtonGroup bgroup = new ButtonGroup();
				radioButtons = new AbstractButton[getItemCount()];
				
				for (int i = 0; i < getItemCount(); i++) {
	
					String tooltip = null;
					DataCell itemDataCell = getItemAt(i);
					String nombreRbtn;
					if (itemDataCell.getCode() instanceof Date) {

						Date fecha = (Date) itemDataCell.getCode();
						nombreRbtn = Dates.getFormattedDate(fecha, getPeriod());
					}
					else {

						nombreRbtn = itemDataCell.getValue().toString();
						String[] nombre_tip = nombreRbtn.split(SEPARATOR_TIP);
						if (nombre_tip.length > 1) {
							nombreRbtn = nombre_tip[0];
							tooltip = nombre_tip[1];
						}

						nombreRbtn = getFormattedData().getFormattedData(nombreRbtn);
					}

					AbstractButton radioButton;
					if (getAspectVisualized() == ASPECT_TOGGLE || getAspectVisualized() == ASPECT_TOGGLE_MIN) {
						LToggleButton tbtnVista = new LToggleButton(nombreRbtn);
						if (getAspectVisualized() == ASPECT_TOGGLE_MIN) {
							tbtnVista.setMargin(new Insets(2,2,2,2));
							tbtnVista.setMinimumSize(new Dimension(20, 20));
						}
						tbtnVista.setIcon(itemDataCell.getIcon());
						radioButton = tbtnVista;
					}
					else {
						radioButton = new LRadioButton(nombreRbtn);
						radioButton.setOpaque(false);
						radioButton.setIcon(itemDataCell.getIcon());
					}
					boolean habilitado = itemDataCell.isEnabled();
					radioButton.setEnabled(habilitado);
					radioButton.addItemListener(this);
					radioButton.setToolTipText(tooltip);
					//Hacemos el radioButon seleccionable
					setSelectableComponent(radioButton, true);

					radioButtons[i] = radioButton;

					//Necesitamos el panel auxiliar porque si añadimos los radiobutton directamente al panel no funciona bien el HorizBagLayout
					JPanel panelAux = new JPanel();
					panelAux.setOpaque(false);
					panelAux.setLayout(new BorderLayout());
					panelAux.add(radioButton, BorderLayout.WEST);

					getPanelRadioButton().add(panelAux);
					bgroup.add(radioButton);
				}
				int selectedIndex = getSelectedIndex();
				if (selectedIndex > -1 && selectedIndex < getItemCount())
					getRadioButton(selectedIndex).setSelected(true);
	
				updateRadioButtonsMargin();
				
				getPanelRadioButton().revalidate();
				getPanelRadioButton().repaint();
			}
	
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void updateRadioButtonsMargin() {
		
		for (int i = 0; i < getRadioButtons().length; i++) {
	
			AbstractButton radioButton = getRadioButtons()[i];
			
			if (radioButton instanceof JRadioButton) {
				
				int top = 3;//Con verticalAlignment CENTER no se tiene en cuenta
				int bottom = 2;//Con verticalAlignment CENTER no se tiene en cuenta
				int left = 0;
				int right = 5;
				
				if (isComboVertical()) {
					
					right = 0;
					radioButton.setVerticalAlignment(SwingConstants.TOP);
				}
				else {
					
					radioButton.setVerticalAlignment(SwingConstants.CENTER);
				}
				
				//if (i == 0)
				//	top = 6;
					
				radioButton.setMargin(new Insets(top, left, bottom, right));
			}
		}
	}
	
	private void updateTableItemsCombo() {
	
		try {
	
			List<ColumnDataCombo> columnsDataCombo = Lists.newList();
			
			if (getHeaderNames() != null && !getHeaderNames().isEmpty()) {
	
				for (int i = 0; i < headerNames.size(); i++) {
					ColumnDataCombo columnaCT = new ColumnDataCombo(headerNames.get(i));
					columnsDataCombo.add(columnaCT);
				}
			}
			else {
	
				ColumnDataCombo columnaCT = new ColumnDataCombo();
				columnaCT.setHeaderName(getName());
	
				columnsDataCombo.add(columnaCT);
			}
	
			boolean selectableRows = true;
			boolean showHeaders = true;
			getTableItemsCombo().initTable(getItems(), showHeaders, columnsDataCombo, selectableRows);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Creado por: Pablo Linaje (20/09/2004 14:56:30)
	 * 
	 * @param newItem Object
	 */
	public void addItem(DataCell newItem) {
	
		getItems().addElement(newItem);
		updateComponents();
	}
	
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}
	
	
	public void destroy() {
	
		try {
	
			finalizeConnections();
			
			selectedItem = null;
			name = null;
			
			combo = null;
			multiComboDates = null;
			textField = null;
			comboButtonTable = null;
			panelRadioButton = null;
			scrollPaneList = null;
			list = null;
			tableItemsCombo = null;
			listsContainer = null;
			
			radioButtons = null;	
			
			getItems().removeAllElements();
			items = null;
			dataSourceName = null;
			
			finalize();
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	public boolean isComboVertical() {
		return comboVertical;
	}
	
	private void finalizeRadioButons() {
	
		AbstractButton radioButton;
		for (int i = 0; i < getRadioButtons().length; i++) {
	
			radioButton = getRadioButton(i);
			radioButton.removeItemListener(this);
			setSelectableComponent(radioButton, false);
		}
		getPanelRadioButton().removeAll();
	}
	
	private void finalizeConnections() {
	
		try {
	
			getCombo().removeItemListener(this);
			if (multiComboDates != null)
				getMultiComboDates().removeItemListener(this);
			
			for (int i = 0; i < getRadioButtons().length; i++) {
	
				getRadioButtons()[i].removeItemListener(this);
			}
			if (list != null)
				getList().removeListSelectionListener(this);
			
			if (comboMultiSelect != null)
				getComboMultiSelect().removeItemListener(this);
			
			if (checkBox != null)
				getCheckBox().removeItemListener(this);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * 
	 */
	protected void fireItemStateChanged(ItemEvent e) {
		Object[] listeners = listenerList.getListenerList();
		// Procesamos los listeners desde el último al primero
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ItemListener.class) {
				((ItemListener)listeners[i+1]).itemStateChanged(e);
			}          
		}
	}
	
	private void formatTextComponents() {
	
		try {
	
			//updateTexts(getSelectedItem());
			
			if (panelRadioButton != null)
				updateRadioButtons();
			if (scrollPaneList != null)
				updateList();
			//if (tableItemsCombo != null)
				//updateTableItemsCombo();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void updateTexts(DataCell item) {
		
		String tooltip = null;
		String text;
		
		if (item == null) {
			text = Constants.VOID;
		}
		else if (item.getCode() instanceof Date) {

			Date date = (Date) item.getCode();
			text = Dates.getFormattedDate(date, getPeriod());
		}
		else {

			text = item.getValue().toString();
			String[] name_tip = text.split(SEPARATOR_TIP);
			if (name_tip.length > 1) {
				text = name_tip[0];
				tooltip = name_tip[1];
			}

			text = getFormattedData().getFormattedData(text);
		}
	
		if (lTextFieldContainer != null) {
			lTextFieldContainer.getLTextField().setText(text);
			lTextFieldContainer.getLTextField().setToolTipText(tooltip);
		}
		
		if (textField != null) {
			textField.setText(text);
			textField.setToolTipText(tooltip);
		}
		
		if (comboButtonTable != null) {
			comboButtonTable.getTextFieldSelectedItem().setText(text);
			comboButtonTable.getTextFieldSelectedItem().setToolTipText(tooltip);
		}
	}
	
	private void updatePeriodComponents() {
		
		String formatter = Dates.getDateFormat(getPeriod());
		getRenderCellCombo().setFormatter(formatter);
		if (tableItemsCombo != null)
			getRenderCellTable().setFormatter(formatter);
		if (comboButtonTable != null)
			getRenderCellComboButtonTable().setFormatter(formatter);
		if (multiComboDates != null)
			getMultiComboDates().setPeriod(getFormattedData().getPeriod());
	}
	
	public int getAspect() {
		return aspect;
	}
	private int getAspectVisualized() {
		return aspectVisualized;
	}
	
	//
	// Visual components
	//
	
	public LCombo<DataCell> getCombo() {
		if (combo == null)
			combo = new LCombo<DataCell>();
		return combo;
	}
	public ComboButtonTable<DataCell> getComboButtonTable() {
		if (comboButtonTable == null) {
			comboButtonTable = new ComboButtonTable<DataCell>(getCombo());
			comboButtonTable.setUpdateTextOnChange(false);
			setSelectableComponent(comboButtonTable.getTextFieldSelectedItem(), true);
			comboButtonTable.getTextFieldSelectedItem().setFormattedData(getFormattedData());
			comboButtonTable.getCombo().setRenderer(getCombo().getRenderer());		
		}
		return comboButtonTable;
	}
	
	public List<Component> getSelectableComponents() {
	
		List<Component> selectableComponents = Lists.newList();
	
		selectableComponents.add(getCombo());
		if (textField != null)
			selectableComponents.add(getTextField());
		if (checkBox != null)
			selectableComponents.add(getCheckBox());
		if (comboButtonTable != null)
			selectableComponents.add(getComboButtonTable().getTextFieldSelectedItem());
		if (lTextFieldContainer != null)
			selectableComponents.add(getLTextFieldContainer().getLTextField());
		if (list != null)
			selectableComponents.add(getList());
		if (tableItemsCombo != null) {
			for (int i = 0; i < getTableItemsCombo().getSelectableComponents().size(); i++){
				selectableComponents.add(getTableItemsCombo().getSelectableComponents().get(i));
			}
		}
		
		for (int i = 0; i < getRadioButtons().length; i++)
			selectableComponents.add(getRadioButton(i));
	
		if (multiComboDates != null) {
			
			List<Component> dateCombos = getMultiComboDates().getSelectableComponents();
			for (int i = 0; i < dateCombos.size(); i++)
				selectableComponents.add(dateCombos.get(i));
		}
		if (comboMultiSelect != null) {
			selectableComponents.add(getComboMultiSelect());
			//añadimos la lista si el combo es de alto de mas de 1
			selectableComponents.add(getComboMultiSelect().getListItems());
		}
		return selectableComponents;
	}
	
	public LTextFieldContainer getLTextFieldContainer() {
		if (lTextFieldContainer == null) {
			lTextFieldContainer = new LTextFieldContainer();
			lTextFieldContainer.getLTextField().setFormattedData(getFormattedData());
		}
		return lTextFieldContainer;
	}
	
	public boolean getContainsError() {
		return containsError;
	}
	
	
	public DataCell getItemAt(int index) {
		return getItems().elementAt(index);
	}
	
	private DataCell getItemTextFieldContainer() {
	
		LTextField lTextField = getLTextFieldContainer().getLTextField();
	
		Object value = lTextField.getFormattedData().getValue();
		//volvemos a obtener la descripción porque si estamos en el proceso de cambio de valor del formattedData, puede que no se haya actualizado el texto formateado
		String desc = lTextField.getFormattedData().getFormattedText();//getFormattedData(value);
		return new DataCell(value, desc);
	}
	
	private DataCell getItemCheckBox() {
		return getItemCheckBox(getCheckBox().isSelected());
	}
	private DataCell getItemCheckBox(boolean isSelected) {
		Boolean booleanValue = new Boolean(isSelected);
		return new DataCell(booleanValue, booleanValue.toString());
	}
	
	public DataCell getItemWithCode(Object code) {
	
		try {
	
			if (code != null) {
			
				if (getAspect() == ASPECT_TEXTFIELD_CONTAINER) {
	
					LTextField lTextField = getLTextFieldContainer().getLTextField();
					String desc = lTextField.getFormattedData().getFormattedData(code);
					return new DataCell(code, desc);
				}
				else if (getAspect() == ASPECT_YES_NO) {
	
					Boolean selected;
					if (code instanceof Boolean)
						selected = (Boolean) code;
					else
						selected = new Boolean(code.toString().matches(Constants.REGEX_YES));

					return new DataCell(selected, selected.toString());
				}
				else if (getAspectVisualized() == ASPECT_MULTISELECT && code instanceof List) {
	
					@SuppressWarnings("unchecked")
					Vector<DataCell> itemsWithCode = getItemsWithCode((List<Object>) code);
					DataCell[] arrayItems = Lists.listToArray(itemsWithCode);
					DataCell dataCellMultiple = DataCell.getDataCellMultiple(arrayItems);
					
					return dataCellMultiple;
				}
				else {
					
					for (int i = 0; i < getItemCount(); i++) {
						if (getItemAt(i).getCode() != null && getItemAt(i).getCode().toString().trim().equalsIgnoreCase(code.toString().trim()))
							return getItemAt(i);
					}
	
					//No hemos encontrado el item con codigo
					//Miramos si puede ser númerico y comparamos los números
					try {
	
						String codeString = code.toString().trim();
						if (Numbers.isIntegerNumber(codeString)) {
							int code_int = Integer.parseInt(codeString);
							for (int i = 0; i < getItemCount(); i++) {
								try {
									String codeItemString = getItemAt(i).getCode().toString().trim();
									if (Numbers.isIntegerNumber(codeItemString)) {
										int codeItem = Integer.parseInt(codeItemString);
										if (code_int == codeItem)
											return getItemAt(i);
									}	
								} catch (NumberFormatException ex) {
								}
							}
						}
					}
					catch (NumberFormatException ex) {
						return null;
					}
				}
			}
		} catch (Throwable ex) {
			Console.printException(ex);
		}
		return null;
	}
	public Vector<DataCell> getItemsWithCode(List<Object> codigos) {
		
		Vector<DataCell> items = new Vector<DataCell>();
		for (int i = 0; i < codigos.size(); i++) {
			DataCell item = getItemWithCode(codigos.get(i));
			if (item != null)
				items.addElement(item);
		}
		return items;
	}
	@SuppressWarnings("unchecked")
	public Vector<DataCell> getSelectedItems() {
		
		DataCell selectedItem = getSelectedItem();
		Vector<DataCell> items;
		if (selectedItem != null && selectedItem.getCode() instanceof List) {
			List<Object> codes = (List<Object>) selectedItem.getCode();
			items = getItemsWithCode(codes);
		}
		else {
			items = new Vector<DataCell>();
			if (selectedItem != null)
				items.addElement(selectedItem);
		}
		return items;
	}
	
	public DataCell getItemWithDescription(Object description) {
	
		try {
			
			if (getAspect() == ASPECT_TEXTFIELD_CONTAINER) {
				
				LTextField lTextField = getLTextFieldContainer().getLTextField();
				Object code = lTextField.getFormattedData().getValueObject(description.toString());
				String desc = lTextField.getFormattedData().getFormattedData(description);
				return new DataCell(code, desc);
			}
			else if (getAspect() == ASPECT_YES_NO) {

				Boolean selected;
				if (description instanceof Boolean)
					selected = (Boolean) description;
				else
					selected = new Boolean(description.toString().matches(Constants.REGEX_YES));

				return new DataCell(selected, selected.toString());
			}
			else {
				for (int i = 0; i < getItemCount(); i++) {
					if (getItemAt(i).getValue() != null && getItemAt(i).getValue().toString().equalsIgnoreCase(description.toString()))
						return getItemAt(i);
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		return null;
	}
	
	public int getItemCount() {
		return getItems().size();
	}

	public Vector<DataCell> getItems() {
		if (getAspect() == ASPECT_TEXTFIELD_CONTAINER && items == null) {
			Vector<DataCell> vecItems = new Vector<>();
			vecItems.addElement(getItemTextFieldContainer());
			return vecItems;
		}
		else if (getAspect() == ASPECT_YES_NO && items == null) {
			Vector<DataCell> vecItems = new Vector<>();
			vecItems.addElement(getItemCheckBox(true));
			vecItems.addElement(getItemCheckBox(false));
			return vecItems;
		}
		else {
			if (items == null)
				items = new Vector<DataCell>();
			return items;
		}
	}
	
	public boolean containsItem(Object item) {
		
		if (getAspectVisualized() == ASPECT_MULTISELECT) {
			//Adminitmos cualquier cosa ya que si luego no contiene ninguno de los items dejaremos todos los checks sin seleccionar
			return true;
		}
		else {
			return getItems().contains(item);
		}
	}
	
	private LList<DataCell> getList() {
		if (list == null) {
			list = new LList<>();
			list.addListSelectionListener(this);
			list.setBorder(BorderFactory.createEmptyBorder());
		}
		return list;
	}
	
	private MultiComboDates getMultiComboDates() {
		if (multiComboDates == null) {
			try {
				multiComboDates = new MultiComboDates();
				multiComboDates.setOpaque(false);
				multiComboDates.setPeriod(getPeriod());
				multiComboDates.clearDates();
				
				for (int i = 0; i < getItemCount(); i++) {
	
					DataCell dataCell = getItemAt(i);
					if (dataCell.getCode() instanceof Date)
						getMultiComboDates().addItem((Date) dataCell.getCode());
				}
				
				//Iniciamos la fecha seleccionada
				DataCell selectedItem = getSelectedItem();
				if (selectedItem != null && selectedItem.getCode() instanceof Date) {
	
					Date date = (Date) selectedItem.getCode();
					getMultiComboDates().setSelectedDate(date);
				}
				//Iniciamos conexiones
				getMultiComboDates().addItemListener(this);
				
				//Hacemos los combos seleccionables
				List<Component> dateCombos = getMultiComboDates().getSelectableComponents();
				for (int i = 0; i < dateCombos.size(); i++) {
					setSelectableComponent(dateCombos.get(i), true);
				}
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
		}
		return multiComboDates;
	}
	public String getName() {
		return name;
	}
	public String getDataSourceName() {
		if (dataSourceName == null)
			dataSourceName = Constants.VOID;
		return dataSourceName;
	}
	
	private JPanel getPanelRadioButton() {
		if (panelRadioButton == null) {
			try {
				panelRadioButton = new JPanel();
				panelRadioButton.setOpaque(false);
				panelRadioButton.setLayout(new HorizBagLayout());
				
				updateRadioButtons();
				//Iniciamos conexiones
				for (int i = 0; i < getRadioButtons().length; i++) {
	
					getRadioButtons()[i].addItemListener(this);
					//Hacemos los radioButons seleccionables
					setSelectableComponent(getRadioButtons()[i], true);
				}
				if (isComboVertical())
					getPanelRadioButton().setLayout(new VerticalBagLayout());
				else
					getPanelRadioButton().setLayout(new HorizBagLayout());
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
		}
		return panelRadioButton;
	}
	
	private JScrollPane getScrollPaneList() {
		if (scrollPaneList == null) {
			scrollPaneList = new JScrollPane();
			scrollPaneList.setAutoscrolls(true);
			scrollPaneList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPaneList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPaneList.setViewportView(getList());
			scrollPaneList.setOpaque(false);
			scrollPaneList.getViewport().setOpaque(false);
			
			scrollPaneList.setBorder(BorderFactory.createEmptyBorder());
			
			updateList();
		}
		return scrollPaneList;
	}
	
	private JPanel getListsContainer() {
		if (listsContainer == null) {
			listsContainer = new JPanel(new BorderLayout());
			listsContainer.setBorder(new RoundedBorder(false));
			listsContainer.setBackground(ColorsGUI.getColorPanelsBrightest());
		}
		return listsContainer;
	}
	
	private AbstractButton getRadioButton(int index) {
		try {		
			return getRadioButtons()[index];
		}
		catch (Throwable ex) {
			Console.printException(ex);
			return null;
		}
	}
	
	private AbstractButton[] getRadioButtons() {
		return radioButtons;
	}
	
	public int getSelectedIndex() {
		if (getAspect() == ASPECT_TEXTFIELD_CONTAINER)
			return 0;
		else
			return getItems().indexOf(getSelectedItem());
	}
	
	public DataCell getSelectedItem() {
		if (getAspect() == ASPECT_TEXTFIELD_CONTAINER) {
			return getItemTextFieldContainer();
		}
		else {
			if (selectedItem == null && getAspectVisualized() == ASPECT_MULTISELECT) {
				selectedItem = getComboMultiSelect().getSelectedItem();
			}
			return selectedItem;
		}
	}
	
	public Object[] getSelectedObjects() {
	
	    Object selectedObject = getSelectedItem();
	    if (selectedObject == null)
	        return new Object[0];
	    else {
	        Object result[] = new Object[1];
	        result[0] = selectedObject;
	        return result;
	    }
	}
	
	public TableItemsCombo<DataCell> getTableItemsCombo() {
		if (tableItemsCombo == null) {
			tableItemsCombo = new TableItemsCombo<DataCell>();
			
			for (int i = 0; i < getTableItemsCombo().getSelectableComponents().size(); i++){
				setSelectableComponent((Component) getTableItemsCombo().getSelectableComponents().get(i), true);
			}
		}
		return tableItemsCombo;
	}
	
	public LTextField getTextField() {
		if (textField == null) {
			try {
				
				textField = new LTextField();
				textField.setEnabled(true);
				textField.setHorizontalAlignment(SwingConstants.CENTER);
				textField.setEditable(false);
				
				Insets insets = textField.getInsets();
				insets.left = 3;
				insets.right = 3;
				textField.setMargin(insets);
				textField.setMinimumSize(new Dimension(200, 20));
				textField.setFormattedData(getFormattedData());
				
				setSelectableComponent(getTextField(), true);
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
		}
		return textField;
	}
	
	public int getPeriod() {
		return getFormattedData().getPeriod();
	}

	
	@SuppressWarnings("unchecked")
	public LCellRenderer<DataCell> getRenderCellCombo() {
		return (LCellRenderer<DataCell>) getCombo().getRenderer();
	}
	
	public LCellRenderer<DataCell> getRenderCellTable() {
		return (LCellRenderer<DataCell>) getTableItemsCombo().getTable().getColumn(0).getCellRenderer();
	}

	public LCellRenderer<DataCell> getRenderCellComboButtonTable() {
		return (LCellRenderer<DataCell>) getComboButtonTable().getDialogComboButtonTable().getTablaDatoCombo().getTable().getColumn(0).getCellRenderer();
	}

	private void setSelectableComponent(Component component, boolean selectable) {
		if (selectable && !getSelectableComponentes().contains(component)) {
			getSelectableComponentes().add(component);
		}
		else if (!selectable && getSelectableComponentes().contains(component)) {
			getSelectableComponentes().remove(component);
		}
	}
	
	private void initConnections() {
	
		try {
	
			finalizeConnections();
			getCombo().addItemListener(this);
			if (multiComboDates != null)
				getMultiComboDates().addItemListener(this);
		
			for (int i = 0; i < getRadioButtons().length; i++) {
	
				getRadioButtons()[i].addItemListener(this);
			}
			if (list != null)
				getList().addListSelectionListener(this);
			
			if (comboMultiSelect != null)
				getComboMultiSelect().addItemListener(this);
			
			if (checkBox != null)
				getCheckBox().addItemListener(this);
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		
		setLayout(new BorderLayout());
		setSize(230, 20);
		setOpaque(false);
		ToolTip.getInstance().registerComponent(getCombo());
		initConnections();
	}
	
	public void insertItemAt(DataCell item, int posicion) {
	
		getItems().insertElementAt(item, posicion);
		updateComponents();
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Invoked when an item has been selected or deselected.
	 * The code written for this method performs the operations
	 * that need to occur when an item is selected (or deselected).
	 */
	public void itemStateChanged(ItemEvent e) {
	
		if (e.getStateChange() == ItemEvent.SELECTED || getAspectVisualized() == ASPECT_YES_NO) {
	
			DataCell selectedItem = null;
			if (e.getSource() == getCombo()) {
				selectedItem = getCombo().getSelectedItem();
			}
			else if (checkBox != null && e.getSource() == getCheckBox()) {
				selectedItem = getItemCheckBox();
			}
			else if (e.getSource() instanceof AbstractButton) {
	
				//No devolvemos rbtn.getText() porque el item seleccionado puede ser algo compuesto como "código#descripción"
				AbstractButton rbtn = (AbstractButton) e.getSource();
				for (int i = 0; i < getRadioButtons().length; i++) {
					if (getRadioButtons()[i] == rbtn)
						selectedItem = getItemAt(i);
				}
			}
			//En caso de que haya fechas damos por supuesto que no hay fechas repetidas
			else if (multiComboDates != null && e.getSource() == getMultiComboDates()) {
				selectedItem = getItemWithCode(getMultiComboDates().getSelectedDate());
			}
			else if (comboMultiSelect != null && e.getSource() == getComboMultiSelect()) {
				selectedItem = (DataCell) getComboMultiSelect().getSelectedItem();
			}
			
			setSelectedItem(selectedItem);
		}
	}
	
	public void paint(Graphics g) {
	
		super.paint(g);
		
		if (AppGUI.getCurrentAppGUI().isDesignTime() && getDataSourceName() != null && !getDataSourceName().equals(Constants.VOID)) {
			//Pintamos el name del servidor asociado al combo ejecutable
			try {
	
				String text = getDataSourceName();
				Font font = UtilsGUI.getFontWithSizeFactor(AppGUI.getFont(), 1.25f);
				font = UtilsGUI.getFontWithStyle(font, Font.BOLD);
						
				Point offset = new Point(-9, 1);
				JComponent componente = getCombo();
				
				if (getAspectVisualized() == ASPECT_TABLE) {
					offset.x = 0;
					offset.y = 10;
					componente = getTableItemsCombo();
				}
				
				//Obtenemos las coordenadas del texto según la alineación
				Point location = UISupportUtils.getLocation(componente, text, getFontMetrics(font), SwingConstants.CENTER, new Insets(offset.y, offset.x, 0, 0));
				
				if (getContainsError())
					g.setColor(ColorsGUI.getColorTextError());
				else
					g.setColor(ColorsGUI.getColorTextBrightest());
				
				g.setFont(font);
				GraphicsUtils.drawString(g, text, location.x, location.y);
	
				if (getAspectVisualized() == ASPECT_TABLE && tableItemsCombo != null) {
	
					//Pintamos información relativa a las columnas
					for (int i = getTableItemsCombo().getColumnsDataCombo().size() - 1; i >= 0; i--) {
	
						ColumnDataCombo columnDataCombo = getTableItemsCombo().getColumnsDataCombo().get(i);
						
						//Campo Data
						String dataFieldName = columnDataCombo.getDataFieldName();
						
						//Alineación
						String descAlignment = "Align: ";
						
						String textAlignment = "Left";
						int dataAlignment = columnDataCombo.getDataAlignment();
						if (dataAlignment == SwingConstants.RIGHT)
							textAlignment = "Right";
						else if (dataAlignment == SwingConstants.CENTER)
							textAlignment = "Center";
						
						//Ancho columna
						String descWidth = "Width: ";
						
						String textWidth = "Auto";
						int percent = columnDataCombo.getWidth();
						if (percent > 0)
							textWidth = percent + " %";
						
						
						//Coordenadas donde pintar
						Font fontDataCols = UtilsGUI.getFontWithSizeFactor(AppGUI.getFont(), 0.8f);
						FontMetrics fontMetrics = getFontMetrics(fontDataCols);
	
						Rectangle headerRect = getTableItemsCombo().getTable().getTableHeader().getHeaderRect(i);
						
						//Calculamos las posiciones x según la alineación
						int xData, xAlign, xWidth;
						if (dataAlignment == SwingConstants.LEFT) {
							
							xData = headerRect.x + 5;
							xAlign = xData;
							xWidth = xData;
						}
						else if (dataAlignment == SwingConstants.RIGHT) {
	
							xData = (int) (headerRect.x + headerRect.getWidth() - UtilsGUI.getStringWidth(dataFieldName, fontMetrics) - 3);
							xAlign = (int) (headerRect.x + headerRect.getWidth() - UtilsGUI.getStringWidth(descAlignment + textAlignment, fontMetrics) - 3);
							xWidth = (int) (headerRect.x + headerRect.getWidth() - UtilsGUI.getStringWidth(descWidth + textWidth, fontMetrics) - 3);
						}
						else {
	
							xData = (int) (headerRect.x + ((headerRect.getWidth() - UtilsGUI.getStringWidth(dataFieldName, fontMetrics)) / 2));
							xAlign = (int) (headerRect.x + ((headerRect.getWidth() - UtilsGUI.getStringWidth(descAlignment + textAlignment, fontMetrics)) / 2));
							xWidth = (int) (headerRect.x + ((headerRect.getWidth() - UtilsGUI.getStringWidth(descWidth + textWidth, fontMetrics)) / 2));
						}
	
						int y = (int) headerRect.getHeight() + fontMetrics.getHeight() + 2;
						
						//Pintamos la información de las columnas
						g.setFont(fontDataCols);
	
						//Campo
						g.setColor(ColorsGUI.getColorTextCode());
						GraphicsUtils.drawString(g, dataFieldName, xData, y);
	
						//Alineación
						y = y + fontMetrics.getHeight() + 3;
						g.setColor(Color.black);
						GraphicsUtils.drawString(g, descAlignment, xAlign, y);
	
						g.setColor(ColorsGUI.getColorTextError());
						int widthDescAlign = SwingUtilities.computeStringWidth(fontMetrics, descAlignment);
						GraphicsUtils.drawString(g, textAlignment, xAlign + widthDescAlign, y);
	
						//Ancho
						y = y + fontMetrics.getHeight() + 3;
						g.setColor(Color.black);
						GraphicsUtils.drawString(g, descWidth, xWidth, y);
	
						g.setColor(Color.blue);
						int anchoDescAncho = SwingUtilities.computeStringWidth(fontMetrics, descWidth);
						GraphicsUtils.drawString(g, textWidth, xWidth + anchoDescAncho, y);
					}
				}
				
			} catch (Throwable ex) {
				Console.printException(ex);
			}
		}
	}
	
	public void removeAllItems() {
		getItems().removeAllElements();
		updateComponents();
	}
	
	public void removeItem(DataCell item) {
		getItems().removeElement(item);
		updateComponents();
	}
	
	public void removeItemAt(int posicion) {
		getItems().removeElementAt(posicion);
		updateComponents();
	}
	
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}
	
	public void requestFocus() {
		super.requestFocus();
		if (getAspectVisualized() == ASPECT_DEFAULT)
			getCombo().requestFocus();
	}
	
	public void setAspect(int aspect) {
		if (aspect < ASPECT_DEFAULT || aspect > ASPECT_TEXTFIELD)
			aspect = ASPECT_DEFAULT;
		try {
			finalizeConnections();
			
			this.aspect = aspect;
			updateAspect();
			formatTextComponents();
		} 
		finally {
			initConnections();
		}
		
	}
	
	private void setAspectVisualized(int aspectVisualized) {
		this.aspectVisualized = aspectVisualized;
		updateVisualizedComponent();
	}
	
	private void updateVisualizedComponent() {
		
		removeAll();
		switch (getAspectVisualized()) {
			
			case ASPECT_RADIOBUTTON: case ASPECT_TOGGLE: case ASPECT_TOGGLE_MIN: {
				add(getPanelRadioButton(), BorderLayout.CENTER);
				break;
			}
			case ASPECT_TEXTFIELD: {
				add(getTextField(), BorderLayout.CENTER);
				break;
			}
			case ASPECT_MULTICOMBO_DATES: {
				add(getMultiComboDates(), BorderLayout.CENTER);
				break;
			}
			case ASPECT_BUTTON_TABLE: {
				add(getComboButtonTable(), BorderLayout.CENTER);
				break;
			}
			case ASPECT_VOID: {
				break;
			}
			case ASPECT_TEXTFIELD_CONTAINER: {
				add(getLTextFieldContainer(), BorderLayout.CENTER);
				break;
			}
			case ASPECT_TABLE: {
				add(getTableItemsCombo(), BorderLayout.CENTER);
				updateTableItemsCombo();
				break;
			}
			case ASPECT_MULTISELECT: {
				if (isComboVertical()) {
					getListsContainer().removeAll();
					getListsContainer().add(getComboMultiSelect().getScrollPane(), BorderLayout.CENTER);
					add(getListsContainer(), BorderLayout.CENTER);
				}
				else {
					add(getComboMultiSelect(), BorderLayout.CENTER);
					getComboMultiSelect().getListItems().setBackground(ColorsGUI.getColorPanelsBrightest());
				}
				break;
			}
			case ASPECT_YES_NO: {
				add(getCheckBox(), BorderLayout.CENTER);
				break;
			}
			default: {
				if (isComboVertical()) {
					getListsContainer().removeAll();
					getListsContainer().add(getScrollPaneList(), BorderLayout.CENTER);
					add(getListsContainer(), BorderLayout.CENTER);
				}
				else
					add(getCombo(), BorderLayout.CENTER);
				break;
			}
		}
		
		updateTexts(getSelectedItem());
		updatePeriodComponents();
		
		revalidate();
		repaint();
	}
		
	public void setComboVertical(boolean comboVertical) {
	
		if (comboVertical != isComboVertical()) {
			
			this.comboVertical = comboVertical;
	
			if (panelRadioButton != null) {
				
				if (comboVertical)
					getPanelRadioButton().setLayout(new VerticalBagLayout());
				else
					getPanelRadioButton().setLayout(new HorizBagLayout());
			}
			if (getAspectVisualized() == ASPECT_TEXTFIELD || getAspectVisualized() == ASPECT_MULTISELECT)
				updateVisualizedComponent();
			else if (getAspectVisualized() == ASPECT_DEFAULT && isEnabled())
				updateVisualizedComponent();
			else if (getAspectVisualized() == ASPECT_RADIOBUTTON)
				updateRadioButtonsMargin();
		}
	}
	
	public void setContainsError(boolean containsError) {
		this.containsError = containsError;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateAspect();
	}
	
	public void setItems(Vector<DataCell> items) {
	
		Vector<DataCell> v = new Vector<DataCell>();
	
		for (int i = 0; i < items.size(); i++) {
			if (!v.contains(items.elementAt(i))) {
				v.addElement(items.elementAt(i));
			}
		}
		this.items = v;
		updateComponents();
	}
	
	public void setName(String name) {
		this.name = name;
		if (comboButtonTable != null)
			getComboButtonTable().setDialogTitle(name);
		if (tableItemsCombo != null)
			updateTableItemsCombo();
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public void setSelectedIndex(int index) {
	
		DataCell itemDataCell = null;
		if (index != -1)
			itemDataCell = getItemAt(index);
		setSelectedItem(itemDataCell);
	}
	
	public void setSelectedItem(DataCell newSelectedItem) {
	
		finalizeConnections();
		selectingItem = true;
		try {
			DataCell oldSelectedItem = getSelectedItem();
	
			if (oldSelectedItem == null
			 || !oldSelectedItem.equals(newSelectedItem)
			 || getAspect() == ASPECT_TEXTFIELD_CONTAINER
			 || getAspect() == ASPECT_YES_NO
			 || getAspectVisualized() == ASPECT_MULTISELECT) {
	
				if (newSelectedItem == null
				 || getItems().indexOf(newSelectedItem) != -1
				 || getAspect() == ASPECT_TEXTFIELD_CONTAINER
				 || getAspect() == ASPECT_YES_NO
				 || getAspectVisualized() == ASPECT_MULTISELECT) {
	
					if (oldSelectedItem != null)
						fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, oldSelectedItem, ItemEvent.DESELECTED));
	
					this.selectedItem = newSelectedItem;
	
					getCombo().setSelectedItem(newSelectedItem);
	
					if (newSelectedItem != null || getAspectVisualized() == ASPECT_MULTISELECT) {
	
						updateTexts(newSelectedItem);
						
						if (multiComboDates != null && newSelectedItem != null && newSelectedItem.getCode() instanceof Date) {
							Date fecha = (Date) selectedItem.getCode();
							getMultiComboDates().setSelectedDate(fecha);
						}
						
						if (panelRadioButton != null && getSelectedIndex() != -1) {
							getRadioButton(getSelectedIndex()).setSelected(true);
						}
						
						if (scrollPaneList != null && getSelectedIndex() != -1) {
							getList().setSelectedIndex(getSelectedIndex());	
						}
						
						if (comboMultiSelect != null){
							getComboMultiSelect().setSelectedItem(newSelectedItem);	
						}
						
						if (checkBox != null){
							boolean selected = new Boolean(newSelectedItem.getCode().toString()).booleanValue();
							getCheckBox().setSelected(selected);
							getCheckBox().setText(selected ? Constants.YES : Constants.NO);
						}
						
						fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, newSelectedItem, ItemEvent.SELECTED));
					}
				}
				else {
					Console.println("ComboMultiAspect: " + getName() + " -- Not found item to select: " + newSelectedItem, Color.red);
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		selectingItem = false;
		initConnections();
	}
	
	public void setSelectedItem(String codigoSelectedItem) {
		//recupero el elemento y realizo la llamada
		for (int i = 0; i < getItems().size(); i++) {
			DataCell datoCombo = getItems().elementAt(i);
			if (datoCombo.getCode().toString().equals(codigoSelectedItem)) {
				setSelectedItem(datoCombo);
				return;
			}
		}
	
	}

	public void setPeriod(int period) {
		getFormattedData().setPeriod(period);
	}
	
	/** 
	   * Called whenever the value of the selection changes.
	   * @param e the event that characterizes the change.
	   */
	public void valueChanged(ListSelectionEvent e) {
	
		DataCell selectedItem = null;
		if (list != null && e.getSource() == getList()) {
			selectedItem = getList().getSelectedValue();
		}
		
		setSelectedItem(selectedItem);
	}
	
	public ComboMultiSelect<DataCell> getComboMultiSelect() {
		
		if (comboMultiSelect == null) {
			comboMultiSelect = new ComboMultiSelect<>();
			comboMultiSelect.addAllItems(getItems());
			//Iniciamos conexiones
			comboMultiSelect.addItemListener(this);
			//Hacemos los combos seleccionables
			setSelectableComponent(comboMultiSelect, true);
			setSelectableComponent(comboMultiSelect.getListItems(), true);
		}
		return comboMultiSelect;
	}
	
	
	public LCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new LCheckBox(Constants.VOID);
			checkBox.addItemListener(this);
		}
		return checkBox;
	}

	/**
	 * Seleccionamos el primer elemento habilitado que encontremos
	 **/
	public void selectItemsDefault() {
		
		boolean multiSelect = getAspectVisualized() == ASPECT_MULTISELECT;
		
		if (multiSelect)
			getComboMultiSelect().getListItems().clearSelection();
		
		for (int i = 0; i < getItemCount(); i++) {
		
			DataCell item = getItemAt(i);
			if (item != null && (item.isEnabled() || multiSelect) && item.isSelectedDefault()) {
				
				if (multiSelect) {
					getComboMultiSelect().getListItems().addSelectionInterval(i, i);
				}
				else {
					setSelectedIndex(i);
					return;
				}
			}
		}
		
		if (!multiSelect)
			selectFirstEnabledItem();
	}
	
	public void selectFirstEnabledItem() {
		
		for (int i = 0; i < getItemCount(); i++) {
		
			DataCell item = getItemAt(i);
			if (item != null && item.isEnabled()) {
				
				setSelectedIndex(i);
				return;
			}
		}
	}
	public int getListNumberOfColumns() {
		return listNumberOfColumns;
	}
	@SuppressWarnings("unchecked")
	public void setListNumberOfColumns(int listNumberOfColumns) {
		
		this.listNumberOfColumns = listNumberOfColumns;
		
		if (list != null || comboMultiSelect != null) {
			
			int margin = listNumberOfColumns > 1 ? LabelCell.MARGIN_DEFAULT_MULTICOLUMN : LabelCell.MARGIN_DEFAULT;
			int horizScrollPolicy = listNumberOfColumns > 1 ? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
			int vertScrollPolicy = listNumberOfColumns > 1 ? JScrollPane.VERTICAL_SCROLLBAR_NEVER : JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
			
			Dimension prefSize = new Dimension(getCombo().getPreferredSize());
						
			if (list != null) {
				
				LList<DataCell> list = getList();
				JScrollPane scrollPane = getScrollPaneList();
				
				list.setNumberOfColumns(listNumberOfColumns);
				LCellRenderer<DataCell> renderCell = (LCellRenderer<DataCell>) list.getCellRenderer();
				//renderCell.getMargin().left = margin;
				renderCell.getMargin().right = margin;
				
				scrollPane.setHorizontalScrollBarPolicy(horizScrollPolicy);
				scrollPane.setVerticalScrollBarPolicy(vertScrollPolicy);
			}
			
			if (comboMultiSelect != null) {
				
				LList<DataCell> list = getComboMultiSelect().getListItems();
				JScrollPane scrollPane = getComboMultiSelect().getScrollPane();
				
				list.setNumberOfColumns(listNumberOfColumns);
				LCellRenderer<DataCell> renderCell = (LCellRenderer<DataCell>) list.getCellRenderer();
				renderCell.getMargin().left = margin;
				renderCell.getMargin().right = margin;
				
				scrollPane.setHorizontalScrollBarPolicy(horizScrollPolicy);
				scrollPane.setVerticalScrollBarPolicy(vertScrollPolicy);
				
				prefSize.width += Icons.SIZE_ICONS + 4;
			}
			
			if (listNumberOfColumns > 1)
				prefSize.width = (prefSize.width+1)*listNumberOfColumns;
			prefSize.height = prefSize.height*4;
			
			getListsContainer().setPreferredSize(prefSize);
			
		}
	}
	public boolean getVisibleCodes() {
		return visibleCodes;
	}
	@SuppressWarnings("unchecked")
	public void setVisibleCodes(boolean visibleCodes) {
		
		this.visibleCodes = visibleCodes;
		
		Color foregroundCode = visibleCodes ? ColorsGUI.getColorTextBright() : ColorsGUI.getColorTextCode();
		
		LCellRenderer<DataCell> labelCell = (LCellRenderer<DataCell>) getCombo().getRenderer();
		labelCell.setForegroundCode(foregroundCode);
		labelCell.setSwapCodeDesc(visibleCodes);
		labelCell.setShowCodesAlways(visibleCodes);
		labelCell.setSelectedBackgroundCode(visibleCodes ? labelCell.getSelectedBackground() : null);
		
		if (list != null) {
			
			labelCell = (LCellRenderer<DataCell>) getList().getCellRenderer();
			labelCell.setForegroundCode(foregroundCode);
			labelCell.setSwapCodeDesc(visibleCodes);
			labelCell.setShowCodesAlways(visibleCodes);
		}
		if (comboMultiSelect != null) {
			
			labelCell = (LCellRenderer<DataCell>) getComboMultiSelect().getListItems().getCellRenderer();
			labelCell.setForegroundCode(foregroundCode);
			labelCell.setSwapCodeDesc(visibleCodes);
			labelCell.setShowCodesAlways(visibleCodes);
		}	
	}

	public FormattedData getFormattedData() {
		if (formattedData == null) {
			formattedData = new FormattedData(getSelectedItem());
			formattedData.addPropertyChangeListener(propertyChangeListener);
		}
		return formattedData;
	}
	
	private void formattedTextChanged() {
		if (panelRadioButton != null) {
			formatTextComponents();
		}
	}
	
	public void setCapitalizeType(int capitalizeType) {
		getFormattedData().setCapitalizeType(capitalizeType);
	}
	
	public List<String> getHeaderNames() {
		return headerNames;
	}

	public void setHeaderNames(List<String> headerNames) {
		this.headerNames = headerNames;
		if (tableItemsCombo != null)
			updateTableItemsCombo();
	}

	public List<Component> getSelectableComponentes() {
		if (selectableComponentes == null)
			selectableComponentes = Lists.newList();
		return selectableComponentes;
	}

	public MouseListener[] getMouseListeners() {
        return listenerList.getListeners(MouseListener.class);
    }
	
	public void addMouseListener(MouseListener mouseListener) {
		listenerList.add(MouseListener.class, mouseListener);
		for (int l = 0; l < getMouseListeners().length; l++) {
			MouseListener listener = getMouseListeners()[l];
			for (int i = 0; i < getSelectableComponentes().size(); i++) {
				Component selectableComponent = getSelectableComponentes().get(i);
				selectableComponent.removeMouseListener(listener);
				selectableComponent.addMouseListener(listener);
			}
		}
	}
	
	public void removeMouseListener(MouseListener mouseListener) {
		listenerList.add(MouseListener.class, mouseListener);
		for (int l = 0; l < getMouseListeners().length; l++) {
			MouseListener listener = getMouseListeners()[l];
			for (int i = 0; i < getSelectableComponentes().size(); i++) {
				Component selectableComponent = getSelectableComponentes().get(i);
				selectableComponent.removeMouseListener(listener);
			}
		}
		listenerList.remove(MouseListener.class, mouseListener);
    }
	
	@Override
	public void updateUI() {
		super.updateUI();
		updateUiHideComponents();
	}
	
	public void updateUiHideComponents() {
		updateUiHideComponent(combo);
		updateUiHideComponent(multiComboDates);
		updateUiHideComponent(textField);
		updateUiHideComponent(comboButtonTable);
		updateUiHideComponent(panelRadioButton);
		updateUiHideComponent(lTextFieldContainer);
		updateUiHideComponent(scrollPaneList);
		updateUiHideComponent(list);
		updateUiHideComponent(tableItemsCombo);
		updateUiHideComponent(comboMultiSelect);
		updateUiHideComponent(checkBox);
		updateUiHideComponent(listsContainer);
	}
	
	private void updateUiHideComponent(JComponent c) {
		if (c != null && !c.isShowing())
			c.updateUI();
	}
}
