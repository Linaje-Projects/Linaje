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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import linaje.LocalizedStrings;
import linaje.gui.LArrowButton;
import linaje.gui.LLabel;
import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.RoundedBorder;
import linaje.gui.cells.DataCell;
import linaje.gui.cells.LabelCell;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LWindow;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Lists;

/**
 * Se trata de un combo que nos permite seleccionar varios elementos en forma de checks en el desplegable de elementos.
 * Tiene los métodos de acceso habituales de un combo normal mas los propios de manipular varios elementos "setSelectedIndices", "getSelectedindices", "setItems", etc.
 * getSelectedItem devolverá un DataCell Multiple
 **/
@SuppressWarnings("serial")
public class ComboMultiSelect<E> extends LPanel implements ItemSelectable {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String empty;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LArrowButton arrowButton = null;
	private LLabel labelSelected = null;
	private LList<E> listItems = null;
	private JScrollPane scrollPane = null;
	private static LWindow popup = null;
	private DataCell selectedItem = null;
	
	private static final int MAX_LABEL_WIDTH = 300;
	private static final int MIN_LABEL_WIDTH = 60;
	private static final int MIN_LABEL_HEIGTH = 18;
	
	protected EventListenerList listenerList = new EventListenerList();	
	private MouseListener mouseListener = new MouseListener() {
		
		public void mousePressed(MouseEvent e) {
			showPopup();
		}
		public void mouseReleased(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
	};
	
	private ListSelectionListener listSelectionListener = new ListSelectionListener() {
		
		public void valueChanged(ListSelectionEvent e) {
			DataCell currentSelectedItem = obtainCurrentSelectedItem();
			setSelectedItem(currentSelectedItem);
		}
	};
		
	public ComboMultiSelect() {
		initialize();
	}

	public static LWindow getPopup() {
		if (popup == null) {
			popup = new LWindow();
			popup.setLayout(new BorderLayout());
		}
		return popup;
	}

	private void initialize() {
		
		setLayout(new BorderLayout());
		add(getLabelSelected(), BorderLayout.CENTER);
		add(getArrowButton(), BorderLayout.EAST);
				
		RoundedBorder roundedBorder = new RoundedBorder(false, ColorsGUI.getColorBorder());
		setBorder(roundedBorder);
		setFocusable(false);
		updateListItems(null);
		setTextLabel(null);
		
		//Cada vez que cambien los elementos de la lista actualizaremos el preferredSize de la label de elementos seleccionados
		getModel().addListDataListener(new ListDataListener() {
			
			public void intervalRemoved(ListDataEvent e) {
				adjustPreferredSize();
			}
			public void intervalAdded(ListDataEvent e) {
				adjustPreferredSize();
			}
			public void contentsChanged(ListDataEvent e) {
				adjustPreferredSize();
			}
		});
		
		adjustPreferredSize();
		int size = getLabelSelected().getPreferredSize().height;
		getArrowButton().setPreferredSize(new Dimension(size, size));
	}

	private Dimension getPreferredLabelSize() {
		
		FontMetrics fm = getFontMetrics(getLabelSelected().getFont());
		int heightLabel = fm.getHeight();
		
		int widthLabel = 0;
		Vector<E> items = getListItems().getElements();
		for (int i = 0; i < items.size(); i++) {
			
			String descItem =  getDescriptionItem(items.elementAt(i));
			int widthItem = fm.stringWidth(descItem);
			widthLabel = Math.max(widthLabel, widthItem);
		}
		
		return new Dimension(widthLabel, heightLabel);
	}
	private void adjustPreferredSize() {
		
		Dimension preferredLabelSize = getPreferredLabelSize();
		//Asignamos al textfield al preferredSize de la lista mas un poco de espacio para que se pueda ver mas de un elemento seleccionado a la vez
		int width = Math.min(preferredLabelSize.width + 40, MAX_LABEL_WIDTH);
		int height = Math.max(preferredLabelSize.height + 2, MIN_LABEL_HEIGTH);
		width = Math.max(width, MIN_LABEL_WIDTH);
		getLabelSelected().setPreferredSize(new Dimension(width, height));
		//getLabelSelected().validate();
	}
	
	private LArrowButton getArrowButton() {
		if (arrowButton == null) {
			arrowButton = new LArrowButton();
			arrowButton.setBorder(BorderFactory.createEmptyBorder());
			arrowButton.addMouseListener(mouseListener);
		}
		return arrowButton;
	}

	private LLabel getLabelSelected() {
		
		if (labelSelected == null) {
			labelSelected = new LLabel();
			labelSelected.setOpaque(true);
			labelSelected.setMargin(new Insets(0, 3, 0, 0));
			labelSelected.addMouseListener(mouseListener);
		}
		return labelSelected;
	}

	public LList<E> getListItems() {
		if (listItems == null) {
			listItems = new LList<E>();
			listItems.setOpaque(false);
			listItems.setMode(LList.MODE_MULTIPLE_SELECTION_WITHOUT_CTRL);
			LCellRenderer<E> render = new LCellRenderer<E>(LabelCell.TYPE_CHECKBOX);
			listItems.setCellRenderer(render);
			listItems.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			listItems.setBorder(BorderFactory.createEmptyBorder());
		}
		return listItems;
	}

	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setOpaque(false);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setViewportView(getListItems());
			scrollPane.getViewport().setOpaque(false);
		}
		return scrollPane;
	}
	
	public void addItem(E item) {
		getModel().addElement(item);
	}
	public void insertItemAt(E item, int index) {
		getModel().insertElementAt(item, index);
	}
	public void removeItem(E item) {
		getModel().removeElement(item);
	}
	public void removeItemAt(int index) {
		getModel().removeElementAt(index);
	}
	public void removeAllItems() {
		getModel().removeAllElements();
	}
	
	public void addAllItems(List<E> items) {
		if (items != null) {
			for (int i = 0; i < items.size(); i++) {
				addItem(items.get(i));
			}
		}
	}
	
	public void setItems(List<E> items) {
		removeAllItems();
		addAllItems(items);
	}
	
	public int getItemCount() {
		return getModel().getSize();
	}
	public E[] getSelectedObjects() {
		return getListItems().getSelectedValues();
	}
	public int[] getSelectedIndices() {
		return getListItems().getSelectedIndices();
	}
	public void setSelectedIndices(int[] indices) {
		getListItems().setSelectedIndices(indices);
    }
	public void setSelectedIndex(int index) {
		getListItems().setSelectedIndex(index);
    }
	
	private void showPopup() {
		
		if (getPopup().getWindow().isVisible()) {
			getPopup().closeWindow();
			return;
		}
		
		getPopup().removeAll();
		getPopup().add(getScrollPane(), BorderLayout.CENTER);
		
		final int MIN_HEIGTH_POPUP = 20;
		final int NUM_VISIBLE_LINES = 8;
		int heightLine = getItemCount() > 0 ? getListItems().getPreferredSize().height / getItemCount() : MIN_HEIGTH_POPUP;
		final int MAX_HEIGHT_POPUP = heightLine * NUM_VISIBLE_LINES;//145;
		int heightList = getListItems().getPreferredSize().height;
		if (heightList < MIN_HEIGTH_POPUP)
			heightList = MIN_HEIGTH_POPUP;
		int heightPopup = Math.min(MAX_HEIGHT_POPUP, heightList);
		
		Insets borderInsets = getPopup().getWindowBorder().getBorderInsets(this);
		int widthComponent = getWidth() - borderInsets.left - borderInsets.right;
		int widthList = getListItems().getPreferredSize().width + (heightList > heightPopup ? getScrollPane().getVerticalScrollBar().getPreferredSize().width : 0);
		int widthPopup = Math.max(widthComponent, widthList) + getPopup().getWindowBorder().getThicknessShadow();
		
		getPopup().setAncestor(this);
		getPopup().setSize(widthPopup, heightPopup);
		
		Point location = getLocationOnScreen();
		//location.x = location.x - getPopup().getWidth() + widthComponente;
		location.y = location.y + getHeight();
		
		getPopup().setAncestor(this);
		getPopup().showWindow(location);
	}
	
	public DefaultListModel<E> getModel() {
		return getListItems().getDefaultListModel();
	}
	
	private DataCell obtainCurrentSelectedItem() {
		
		return DataCell.getDataCellMultiple(getSelectedObjects());
	}
	
	@SuppressWarnings("unchecked")
	private void updateListItems(DataCell selectedItemMultiple) {
		
		getListItems().removeListSelectionListener(listSelectionListener);
		try {
			
			getListItems().clearSelection();
			if (selectedItemMultiple != null) {
				
				Object objetCodes = selectedItemMultiple.getCode();
				if (objetCodes != null) {
					
					List<Object> codes;
					if (objetCodes instanceof List) {
						codes = (List<Object>) objetCodes;
					}
					else {
						codes = Lists.newList();
						codes.add(objetCodes);
					}
					
					Vector<E> elements = getListItems().getElements();
					//Obtenemos los indices equivalentes del parámetro selectedItemMultiple
					List<Integer> selectedIndicesList = Lists.newList();
					for (int i = 0; i < elements.size(); i++) {
						try {
							
							E item = elements.elementAt(i);
							DataCell itemDataCell = item  instanceof DataCell ? (DataCell) item : null;
							Object codeItem = itemDataCell != null ? itemDataCell.getCode() : item;
							boolean selectItem = codes.contains(codeItem);
							if (!selectItem) {
								for (int j = 0; j < codes.size() && !selectItem; j++) {
									selectItem = codeItem.equals(codes.get(j));
								}
							}
							
							if (selectItem)
								selectedIndicesList.add(new Integer(i));
						}
						catch (Throwable ex) {
							Console.printException(ex);
						}
					}
					
					int[] selectedIndices = Lists.listToArrayInt(selectedIndicesList);
					getListItems().setSelectedIndices(selectedIndices);
				}
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		getListItems().addListSelectionListener(listSelectionListener);
	}
	
	public DataCell getSelectedItem() {
		if (selectedItem == null)
			selectedItem = obtainCurrentSelectedItem();
		return selectedItem;
	}
	public void setSelectedItem(DataCell newSelectedItem) {
		
		if (selectedItem != newSelectedItem && (selectedItem == null || !selectedItem.equals(newSelectedItem))) {
			
			this.selectedItem = newSelectedItem;
			updateListItems(newSelectedItem);
			Vector<String> descriptions = new Vector<String>();
			for (int i = 0; i < getSelectedObjects().length; i++) {
				
				String descItem = getDescriptionItem(getSelectedObjects()[i]);				
				descriptions.addElement(descItem);
			}
			String textLabel = descriptions.size() == 0 ? null : descriptions.size() == 1 ? descriptions.elementAt(0) : descriptions.toString();
			setTextLabel(textLabel);
			
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, newSelectedItem, ItemEvent.SELECTED));
		}
	}
	
	public String getDescriptionItem(E item) {
		
		String description = null;
		
		try {
			ListCellRenderer<? super E> render = getListItems().getCellRenderer();
			Component rendererComponent = render.getListCellRendererComponent(getListItems(), item, -1, false, false);
			if (rendererComponent instanceof LabelCell)
				description = ((LabelCell) rendererComponent).getDescription();
			else if (rendererComponent instanceof JLabel)
				description = ((JLabel) rendererComponent).getText();
			else if (rendererComponent instanceof JTextComponent)
				description = ((JTextComponent) rendererComponent).getText();
			else if (item instanceof DataCell)
				description = ((DataCell) item).getValue().toString();
			else
				description = item.toString();
		}
		catch (Exception e) {
			description = Constants.VOID;
		}
		
		return description;
	}
	
	private void setTextLabel(String text) {
		
		if (text == null) {
			getLabelSelected().setForeground(ColorsGUI.getColorTextDisabled(getLabelSelected().getBackground()));
			getLabelSelected().setHorizontalAlignment(SwingConstants.CENTER);
			getLabelSelected().setText(TEXTS.empty);
		}
		else {
			getLabelSelected().setForeground(ColorsGUI.getColorText());
			getLabelSelected().setHorizontalAlignment(SwingConstants.LEFT);
			getLabelSelected().setText(text);
		}
	}
	protected void fireItemStateChanged(ItemEvent e) {
		Object[] listeners = listenerList.getListenerList();
		// Procesamos los listeners desde el último al primero
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ItemListener.class) {
				((ItemListener)listeners[i+1]).itemStateChanged(e);
			}          
		}
	}
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getListItems().setEnabled(enabled);
		getLabelSelected().setEnabled(enabled);
	}
}
