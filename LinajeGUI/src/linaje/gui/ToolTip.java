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
package linaje.gui;

/**
 * <b>Funcionalidad:</b><br>
 * Mostrar tooltips en listas, combos y tablas con renders complejos (Varios colores, iconos, etc).
 * También muestra tooltips en componentes con texto como textFields, Buttons, Checkbox o RadionButons, entre otros.
 * <p>
 * <b>Uso:</b><br>
 * Hacer una llamada estática al método ToolTip.getToolTipCompartido().registrarComponente(Component)
 * pasando como parámetro el componente que queremos que tenga tooltip (combo, lista, tabla, ...).
 *
 * En el caso de los combos hay que registrar tanto el JComboBox como la lista de items. 
 * Como la lista no es accesible desde el propio JComboBox hay que registrarla en el 
 * getCellRendererComponent(...) del render del combo donde si que es accesible.
 *
 * A partir de la versión 00.05 se ha añadido un tooltip normal (Solo texto) para los componentes de antes con render de texto (JLabel).
 * Además se ha añadido soporte para TEXTFIELDS
 *
 * A partir de la versión 00.06 se ha añadido el que se pueda asignar un retardo para sacar el tooltip (1 seg. por defecto)
 *
 * A partir de la versión 00.07 se ha añadido soporte para LABELS
 *
 * A partir de la versión 00.08 se han añadido Tooltips especiales para las propiedades de la aplicación
 *
 * A partir de la versión 00.27 se ha añadido soporte para cualquier clase que herede de AbstractButton
 *
 * A partir de la versión 00.32 si un toolTip estándar mide mas de 600 pixeles de ancho,
 * se hace multilínea automáticamente.
 *
 * Ver: - ListCellRendererCodigoDescripcion
 *		- ListCellRendererComponenteView
 *		- ListCellRendererMultiCodigoDescripcion
 * <p>
 * 
 * @author: Pablo Linaje (01/02/2005 13:24:41)
 * @version 00.09
 */
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.*;

import linaje.gui.cells.LabelCell;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LWindow;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;
import linaje.utils.Lists;

public class ToolTip extends MouseAdapter {
	
	private int lastIndex = -1;
	private int row = -1;
	private int column = -1;
	
	private Component renderObject = null;
	private Component activeComponent = null;
	
	private LWindow lWindow = null;
	private List<Component> registeredComponents = null;

	private static ToolTip instance = null;
	
	private Timer timer = null;
	private int showDelay = 1000;
	private Point cursorLocation = null;
	
	private HashMap<Window, LWindow> lWindowForOwnerMap = null;
	//We will enable this when tooltip is over JPopupMenu (JComboBox Popup)
	private boolean privilegedMousePressed = false;
	
	private JList<Object> listForCombos = null;
	private LLabel labelObject = null;
	private LabelCell labelCellObject = null;
	private JTextArea textAreaForLongTexts = null;
	private JPanel panelTextArea = null;
	private JLabel labelIconForTextArea = null;
	
	public ToolTip() {
		super();
		initialize();
	}
	
	private void initialize() {
		//En los tooltips sobre Popups no llega el mousePressed sobre el Tooltip ya que se procesa antes en el Toolkit y se cierra antes de poder procesarlo
		//Por eso lo contemplamos aquí, para poder procesar el mouseEvent antes de que se cierre
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			@Override
			public void eventDispatched(AWTEvent event) {
				
				if (privilegedMousePressed && event.getID() == MouseEvent.MOUSE_PRESSED && getLWindow().getWindow().isVisible()) {
					mousePressed((MouseEvent) event);
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}
	
	private JList<Object> getListForCombos() {
		if (listForCombos == null) {
			listForCombos = new JList<Object>();
			DefaultListModel<Object> defaultModel = new DefaultListModel<Object>();
			listForCombos.setModel(defaultModel);
		}
		return listForCombos;
	}
	
	public LabelCell getLabelCellObject() {
		if (labelCellObject == null) {
			labelCellObject = new LabelCell();
		}
		return labelCellObject;
	}
	
	private LLabel getLabelObject() {
		if (labelObject == null) {
			labelObject = new LLabel();
			ToolTip.getInstance().unRegisterComponent(labelObject);
		}
		return labelObject;
	}
	
	private JTextArea getTextAreaForLongTexts() {
		if (textAreaForLongTexts == null) {
			textAreaForLongTexts = new JTextArea();
			textAreaForLongTexts.setMargin(new Insets(1, 3, 1, 3));
			textAreaForLongTexts.setLineWrap(true);
			textAreaForLongTexts.setWrapStyleWord(true);
			textAreaForLongTexts.setOpaque(false);
			textAreaForLongTexts.setEditable(false);
			textAreaForLongTexts.addMouseListener(this);
			
		}
		return textAreaForLongTexts;
	}
	
	private JPanel getPanelTextArea() {
		if (panelTextArea == null) {
			panelTextArea = new JPanel(new BorderLayout());
			panelTextArea.add(getTextAreaForLongTexts(), BorderLayout.EAST);
			panelTextArea.add(getLabelIconForTextArea(), BorderLayout.WEST);
		}
		return panelTextArea;
	}
	
	private JLabel getLabelIconForTextArea() {
		if (labelIconForTextArea == null) {
			labelIconForTextArea = new JLabel();
		}
		return labelIconForTextArea;
	}
	
	public static final ToolTip getInstance() {
		if (instance == null)
			instance = new ToolTip();
		return instance;
	}
	
	public void registerComponent(Component component) {
	
		if (component != null && !getRegisteredComponents().contains(component)) {
	
			getRegisteredComponents().add(component);
			component.removeMouseListener(this);
			component.removeMouseMotionListener(this);
			component.addMouseListener(this);
			component.addMouseMotionListener(this);
	
			//Si el componente es un JCombo tambien registramos su editor por si fuese editable
			if (component instanceof JComboBox) {
	
				JComboBox<?> combo = (JComboBox<?>) component;
				if (combo.getEditor() != null && combo.getEditor().getEditorComponent() != null) {
					registerComponent(combo.getEditor().getEditorComponent());
				}
			}
		}
	}
	public void unRegisterComponent(Component component) {
	
		if (component != null && getRegisteredComponents().contains(component)) {
	
			getRegisteredComponents().remove(component);
			component.removeMouseListener(this);
			component.removeMouseMotionListener(this);
		}
	
		//Si el componente es un JCombo tambien destruimos el registro de su editor
		if (component instanceof JComboBox) {
	
			JComboBox<?> combo = (JComboBox<?>) component;
			if (combo.getEditor() != null && combo.getEditor().getEditorComponent() != null) {
				unRegisterComponent(combo.getEditor().getEditorComponent());
			}
		}
	}
	
	public void unRegisterComponents() {
	
		Component registeredComponent;
		for (int i = 0; i < getRegisteredComponents().size(); i++) {
	
			if (getRegisteredComponents().get(i) != null) {
			
				registeredComponent = (Component) getRegisteredComponents().get(i);
				registeredComponent.removeMouseMotionListener(this);
				registeredComponent.removeMouseListener(this);
			}
		}
	
		getRegisteredComponents().clear();
		
		renderObject = null;
		activeComponent = null;
		lWindow = null;
		lWindowForOwnerMap = null;
		registeredComponents = null;
		timer = null;
		lastIndex = -1;
		row = -1;
		column = -1;
	}
	
	public List<Component> getRegisteredComponents() {
		if (registeredComponents == null)
			registeredComponents = Lists.newList();
		return registeredComponents;
	}
	
	private void show(Point toolTipLocation) {
		
		if (getActiveComponent().isShowing() && getRenderObject().getPreferredSize().width > 2) {
			
			if (toolTipLocation == null)
				toolTipLocation = getToolTipLocation();
			
			getLWindow().showWindow(toolTipLocation);
			
			getLWindow().getWindow().setVisible(true);
			getLWindow().getWindow().invalidate();
			getLWindow().getWindow().validate();
			getActiveComponent().repaint();
		}
	}
	
	private void showTooltip() {
	
		Runnable runnable = new Runnable() {
			public void run() {
				
				show(null);
			}
		};
		SwingUtilities.invokeLater(runnable);
	}
	
	public void showTooltip(Component activeComponent, Component renderObject, Point toolTipLocation) {
	
		setActiveComponent(activeComponent);
		Object valor = null;
		if (renderObject instanceof TextComponent)
			valor = ((TextComponent) renderObject).getText();
		boolean isCustomTooltip = true;
		setRenderObject(renderObject, valor, isCustomTooltip);
		show(toolTipLocation);
	}
	
	public void hideTooltip() {
		getTimer().stop();
		getLWindow().closeWindow();
	}
	
	protected Timer getTimer() {
		if (timer == null) {	
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showTooltip();
				}
			};
			timer = new Timer(getShowDelay(), actionListener);
		}
		return timer;
	}
	
	protected Point getToolTipLocation() {
	
		Point point;
		boolean alignRight = false;
		
		if (getActiveComponent() instanceof JList) {
	
			JList<?> list = (JList<?>) getActiveComponent();
			point = list.getCellBounds(getLastIndex(), getLastIndex()).getLocation();
			SwingUtilities.convertPointToScreen(point, getActiveComponent());
		}
		else if (getActiveComponent() instanceof JTable) {
	
			JTable table = (JTable) getActiveComponent();
			point = table.getCellRect(row, column, false).getLocation();
			SwingUtilities.convertPointToScreen(point, getActiveComponent());
		}
		else {
	
			boolean isCombo = false;
			point = getActiveComponent().getLocationOnScreen();
			if (getActiveComponent() instanceof JTree) {
	
				JTree tree = (JTree) getActiveComponent();
				if (tree.getParent() instanceof JViewport) {
	
					JViewport viewPort = (JViewport) tree.getParent();
					point = viewPort.getLocationOnScreen();
				}
			}
			else if (getActiveComponent() instanceof JComboBox) {
				point.x = point.x + 2; //Ponemos el tooltip a continuación del borde del combo
				isCombo = true;
			}
			//Si el componente es mas alto que el toolTip, lo centramos
			if (getActiveComponent().getHeight() > getLWindow().getWindow().getHeight()) {
	
				if (getActiveComponent() instanceof JTree) {
	
					JTree arbol = (JTree) getActiveComponent();
					if (arbol.getParent() instanceof JViewport) {
	
						JViewport viewPort = (JViewport) arbol.getParent();
						int y = viewPort.getExtentSize().height - getLWindow().getWindow().getHeight();
						point = new Point(point.x, point.y + y);
					}
				}
				else {
	
					int y = (getActiveComponent().getHeight() - getLWindow().getWindow().getHeight()) / 2;
					point = new Point(point.x, point.y + y);
				}
			}
			
			//Si el componente es mas largo que el toolTip, ponemos el tooltip alieneado a la derecha
			//(Esto podría pasar en radiobuttons o checkbox que ocupan mas con los iconos)
			if (getActiveComponent().getWidth() > getLWindow().getWindow().getWidth()) {
	
				if (!isCombo || getActiveComponent().getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) {
					int x = getActiveComponent().getWidth() - getLWindow().getWindow().getWidth();
					point = new Point(point.x + x, point.y);
					alignRight = true;
				}
			}		
		}
		
		Insets borderInsets = getLWindow().getWindowBorder().getBorderInsets(getLWindow().getContentPane());
		point.y = point.y - borderInsets.top;
		if (alignRight)
			point.x = point.x + borderInsets.right;
		else
			point.x = point.x - borderInsets.left;
		
		return point;
	}
	
	protected boolean isThereSpace() {
	
		if (getTimer().isRunning())
			return true;
	
		if (getActiveComponent() instanceof JList) {
	
			JList<?> list = (JList<?>) getActiveComponent();
			//Insets borderInsets = getContentPane().getBorder().getBorderInsets(getContentPane());
			//int anchoBordeTip = borderInsets.left + borderInsets.right;
			boolean fixedCells = list.getFixedCellWidth() > 0;
			int widthList = fixedCells ? list.getFixedCellWidth() : list.getParent().getSize().width;
			
			boolean isThereSpace = widthList > getLWindow().getWidth() - 2;
						
			if (!fixedCells && getRenderObject() instanceof LabelCell) {
				//Si mostramos el código alineado a la derecha sacaremos el tooltip según el ancho de la lista (elemento de mayor tamaño)
				LabelCell labelCell = (LabelCell) getRenderObject();
				if (!labelCell.isDescOverCode() && labelCell.isShowingCodes())
					isThereSpace = widthList > list.getWidth() - 2;
			}		
			
			if (!isThereSpace) {
				//No dejamos que el tip sea mas pequeño que la celda
				if (getLWindow().getWidth() < widthList)
					getLWindow().setSize(widthList, getLWindow().getHeight());
			}
			
			return isThereSpace;
		}
		else if (getActiveComponent() instanceof JTable) {
	
			JTable table = (JTable) getActiveComponent();
			int widthColumn = table.getColumnModel().getColumn(column).getWidth();
			
			return widthColumn > getLWindow().getWidth() - 2;
		}
		else if (getActiveComponent() instanceof JTableHeader) {
	
			return true;
		}
		else {
	
			if (getRenderObject() instanceof JLabel || getRenderObject() instanceof LabelCell) {
				
				if (getActiveComponent() instanceof AbstractButton) {
	
					AbstractButton abstractButton = (AbstractButton) getActiveComponent();
					//Obtenemos el ancho optimo con el que entraría todo el texto
					int optimumWidth = abstractButton.getUI().getPreferredSize(abstractButton).width - abstractButton.getInsets().left - abstractButton.getInsets().right;
					
					return abstractButton.getSize().width >= optimumWidth;
				}
				else {
	
					//Hacemos el tooltip tan alto como el componente
					getLWindow().setSize(getLWindow().getWidth(), getActiveComponent().getHeight());
					if (getActiveComponent() instanceof LLabel) {
	
						//En labels multilinea puede quedar muy apretado el tooltip por lo que nos aseguramos de dejar margen
						LLabel lLabel = (LLabel) getActiveComponent();
						Rectangle textRect = lLabel.getTextRect();
						if (textRect.y + textRect.height + 3 > getActiveComponent().getHeight())
							getLWindow().setSize(getLWindow().getWidth(), textRect.y + textRect.height + 3);
					}
						
					int widthToIgnore = 0;
					if (getActiveComponent() instanceof JComboBox) {
						getLWindow().setSize(getLWindow().getWidth(), getActiveComponent().getHeight() - 2);
						//No hay que tener en cuenta el tamaño del botón del combo (Que medirá el mísmo alto que el combo)
						widthToIgnore = getActiveComponent().getSize().height;
					}
					/*else if (getActiveComponent() instanceof JTextField) {
						//No hay que tener en cuenta el margen izquierdo y derecho del textField
						JTextField textField = (JTextField) getActiveComponent();
						widthToIgnore = textField.getMargin().left + textField.getMargin().right;
					}*/
					
					return getActiveComponent().getSize().width - widthToIgnore >= getLWindow().getWidth();
				}
			}
			else {
	
				if (getLWindow().getWidth() < getActiveComponent().getWidth())
					getLWindow().setSize(getActiveComponent().getWidth(), getLWindow().getHeight());
						
				return false;
			}
		}
	}
	
	
	public void mouseReleased(MouseEvent e) {
		getTimer().stop();
		hideTooltip();
	}
	public void mouseClicked(MouseEvent e) {
		getTimer().stop();
	}
	public void mouseDragged(MouseEvent e) {
		getTimer().stop();
	}
	public void mouseEntered(MouseEvent e) {
		getTimer().stop();
	}
	
	public void mouseExited(MouseEvent e) {
	
		getTimer().stop();
		if (e.getSource() != getActiveComponent() && getLWindow().getWindow().isVisible()) {
			hideTooltip();
		}
	}
	
	public void mousePressed(MouseEvent e) {
		
		getTimer().stop();
		hideTooltip();
		
		if (e.getSource() != getActiveComponent() && getActiveComponent().isShowing()) {
			
			//Simulamos click en el componente que ha sacado el tooltip
			int id = MouseEvent.MOUSE_PRESSED;
			if (getActiveComponent() instanceof JList)
				id = MouseEvent.MOUSE_RELEASED;
			
			//int x = getCursorLocation().x;
			//int y = getCursorLocation().y;
			
			int x = e.getXOnScreen() - getActiveComponent().getLocationOnScreen().x;
			int y = e.getYOnScreen() - getActiveComponent().getLocationOnScreen().y;
			
			int limitX = getActiveComponent().getLocationOnScreen().x + getActiveComponent().getWidth() - 2;
			int limitY = getActiveComponent().getLocationOnScreen().y + getActiveComponent().getHeight() - 2;
			if (x > limitX)
				x = limitX;
			if (y > limitY)
				y = limitY;
			
			MouseEvent mouseEvent = new MouseEvent(getActiveComponent(),
					id,
					e.getWhen(),
					e.getModifiers(),
					x,
					y,
					e.getClickCount(),
					e.isPopupTrigger());
			
			getActiveComponent().dispatchEvent(mouseEvent);
		}
	}

	public void mouseMoved(MouseEvent e) {
	
		getTimer().stop();
		setCursorLocation(e.getPoint());
		
		if (e.getSource() instanceof Component)		
			setActiveComponent((Component) e.getSource());
		
		if (!getLWindow().getWindow().isVisible()) {
			
			if (e.getSource() instanceof JList) {
		
				@SuppressWarnings("unchecked")
				JList<Object> list = (JList<Object>) e.getSource();
				int tmpIndex = list.locationToIndex(e.getPoint());
				
				if (tmpIndex > -1) {
					
					//if (tmpIndex != getLastIndex())
					//	getTimer().stop();
					
					setLastIndex(tmpIndex);
		
					Object value = list.getModel().getElementAt(getLastIndex());
					Component render = list.getCellRenderer().getListCellRendererComponent(list, value, getLastIndex(), false, false);
					setRenderObject(render, value);
				}
			}
			else if (e.getSource() instanceof JTable) {
		
				JTable table = (JTable) e.getSource();
				
				int tmpRow = table.rowAtPoint(e.getPoint());
				int tmpColumn = table.columnAtPoint(e.getPoint());
				
				if (((tmpRow > -1 && tmpColumn > -1))) {
					
					//if (tmpRow != row || tmpColumn != column)
					//	getTimer().stop();
					
					row = tmpRow;
					column = tmpColumn;
		
					Object value = table.getModel().getValueAt(row, table.convertColumnIndexToModel(column));
					Component render = table.getCellRenderer(row, column).getTableCellRendererComponent(table, value, false, false, row, column);
					setRenderObject(render, value);
				}
			}
			else if (e.getSource() instanceof JComboBox) {
		
				@SuppressWarnings("unchecked")
				JComboBox<Object> combo = (JComboBox<Object>) e.getSource();
				
				Object selectedItem = combo.getSelectedItem();
				
				if (selectedItem != null) {
					
					setLastIndex(combo.getSelectedIndex());
		
					getListForCombos().setFont(combo.getFont());
					getListForCombos().setForeground(combo.getForeground());
					
					DefaultListModel<Object> defaultModel = (DefaultListModel<Object>) getListForCombos().getModel();
					Object value = selectedItem;
					defaultModel.removeAllElements();
					defaultModel.addElement(value);
					
					Component render = combo.getRenderer().getListCellRendererComponent(getListForCombos(), value, getLastIndex(), false, false);
					setRenderObject(render, value);
				}
			}
			else if (e.getSource() instanceof JTextField) {
		
				JTextField textField = (JTextField) e.getSource();
				LLabel labelRender = getLabelForComponent(textField);
				setRenderObject(labelRender, textField.getText());
			}
			else if (e.getSource() instanceof JLabel) {
		
				JLabel label = (JLabel) e.getSource();
				LLabel labelRender = getLabelForComponent(label);
				setRenderObject(labelRender, label.getText());
			}
			else if (e.getSource() instanceof AbstractButton) {
		
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				LLabel labelRender = getLabelForComponent(abstractButton);
				setRenderObject(labelRender, abstractButton.getText());
			}
			else if (e.getSource() instanceof JTableHeader) {
		
				JTableHeader tableHeader = (JTableHeader) e.getSource();
				JTable tabla = tableHeader.getTable();
		
				int indexColumn = tabla.getColumnModel().getColumnIndexAtX(e.getX());
				if (indexColumn != -1) {
					
					String text = Constants.VOID;
					
					if (!text.trim().equals(Constants.VOID)) {
			
						JLabel labelRender = new JLabel(text);	
						setRenderObject(labelRender, text);
					}
				}
			}
			else if (e.getSource() instanceof JTree) {
		
				JTree tree = (JTree) e.getSource();
				
				Object treeNode = tree.getClosestPathForLocation(e.getX(), e.getY()).getLastPathComponent();
	
				if (treeNode instanceof DefaultMutableTreeNode) {
	
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode;
	
					Object value = node.getUserObject();
					int row = 0;
					if (node.getParent() != null)
						row = node.getParent().getIndex(node);
					Component render = tree.getCellRenderer().getTreeCellRendererComponent(tree, value, false, false, false, row, false);
					
					setRenderObject(render, value);
				}
				else if (treeNode instanceof TreeNodeVector) {
					
					@SuppressWarnings("unchecked")
					TreeNodeVector<Object> node = (TreeNodeVector<Object>) treeNode;
	
					Object value = node.getUserObject();
					int row = 0;
					if (node.getParent() != null)
						row = node.getParent().getIndex(node);
					Component render = tree.getCellRenderer().getTreeCellRendererComponent(tree, value, false, false, false, row, false);
					
					setRenderObject(render, value);
				}
			}
		}
	}
	
	private LLabel getLabelForComponent(Component component) {
		
		Font font = component.getFont();
		Color fg = component.getForeground();
		Color bg = component.getBackground();
		String text = null;
		Icon icon = null;
		Insets margin = null;
		int vAlign = SwingConstants.CENTER;
		int hAlign = SwingConstants.LEADING;
		int vTextPos = SwingConstants.CENTER;
		int hTextPos = SwingConstants.TRAILING;
		
		if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			if (label instanceof LLabel)
				margin = ((LLabel) label).getMargin();
						
			text = label.getText();
			icon = label.getIcon();
			vAlign = label.getVerticalAlignment();
			hAlign = label.getHorizontalAlignment();
			vTextPos = label.getVerticalTextPosition();
			hTextPos = label.getHorizontalTextPosition();
		}
		else if (component instanceof AbstractButton) {
			
			AbstractButton abstractButton = (AbstractButton) component;
			
			text = abstractButton.getText();
			margin = abstractButton.getMargin();
			icon = abstractButton.getIcon();
			vAlign = abstractButton.getVerticalAlignment();
			hAlign = abstractButton.getHorizontalAlignment();
			vTextPos = abstractButton.getVerticalTextPosition();
			hTextPos = abstractButton.getHorizontalTextPosition();
		}
		else if (component instanceof JTextComponent) {
			
			JTextComponent textComponent = (JTextComponent) component;
			text = textComponent.getText();
			margin = textComponent.getMargin();
			if (margin.left > 1)
				margin.left--;
			if (margin.right > 1)
				margin.right--;
			if (component instanceof JTextField) {
				JTextField textField = (JTextField) component;
				hAlign = textField.getHorizontalAlignment();
			}
		}
		else {
			text = Constants.VOID;
		}
		
		LLabel labelForComponent = getLabelObject();
		labelForComponent.setText(text);
		labelForComponent.setMargin(margin);
		labelForComponent.setFont(font);
		labelForComponent.setForeground(fg);
		labelForComponent.setBackground(bg);
		labelForComponent.setIcon(icon);
		labelForComponent.setVerticalAlignment(vAlign);
		labelForComponent.setHorizontalAlignment(hAlign);
		labelForComponent.setVerticalTextPosition(vTextPos);
		labelForComponent.setHorizontalTextPosition(hTextPos);
		
		return labelForComponent;
	}
	
	protected Component getActiveComponent() {
		return activeComponent;
	}
	
	private void setActiveComponent(Component newActiveComponent) {
	
		Component oldActiveComponent = activeComponent;
		activeComponent = newActiveComponent;
	
		if (newActiveComponent != null && oldActiveComponent != newActiveComponent) {
			
			LWindow lWindow = getLWindowForComponent(newActiveComponent);
			setLWindow(lWindow);
			
			//En los tooltips sobre Popups no llega el mousePressed sobre el Tooltip ya que se procesa antes en el Toolkit y se cierra antes de poder procesarlo
			//Identificamos si estamos en Popup para que se procese el mousePressed de forma prioritaria
			boolean componentInPopup = newActiveComponent instanceof JList<?> && UtilsGUI.getParentInstanceOf(newActiveComponent, JPopupMenu.class) != null;
			privilegedMousePressed = componentInPopup;
		}
	}
	
	public Component getRenderObject() {
		return renderObject;
	}
	private void setRenderObject(Component renderObject, Object value) {
		setRenderObject(renderObject, value, false);
	}
	protected void setRenderObject(Component renderObject, Object value, boolean isCustomTooltip) {
	
		try {
	
			 if (renderObject != null) {
	
				if (renderObject instanceof LabelCell) {
					
					LabelCell lCellOrig = (LabelCell) renderObject;
					LabelCell lCellNew = getLabelCellObject();
					lCellNew.copyPropertiesFrom(lCellOrig);
					renderObject = lCellNew;
				}
				else if (renderObject instanceof JLabel) {
	
					JLabel label = (JLabel) renderObject;
					if (renderObject != getLabelObject())
						renderObject = getLabelForComponent(renderObject);
					
					final int MAX_TOOLTIP_WIDTH = 600;
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int maxTooltipWidth = screenSize.width - 50;
					if (maxTooltipWidth > MAX_TOOLTIP_WIDTH)
						maxTooltipWidth = MAX_TOOLTIP_WIDTH;
					if (maxTooltipWidth < getActiveComponent().getWidth())
						maxTooltipWidth = getActiveComponent().getWidth();
					
					if (renderObject.getPreferredSize().width > maxTooltipWidth) {
						//Si el tooltip mide mas del máximo permitido, 
						//lo convertimos en un textArea para presentar mejor el texto
						JTextArea textArea = getTextAreaForLongTexts();
						//Hay que iniciar el size con un ancho que tome como base para el prefrerredSize,
						//de forma que el prefSize será (600, x) según el número de lineas
						textArea.setSize(maxTooltipWidth, 32);
						textArea.setText(label.getText());
						textArea.setFont(label.getFont());
						textArea.setForeground(label.getForeground());
						
						Icon icon = label.getIcon();
						getLabelIconForTextArea().setIcon(icon);
						if (icon != null)
							getPanelTextArea().add(getLabelIconForTextArea(), BorderLayout.WEST);
						else
							getPanelTextArea().remove(getLabelIconForTextArea());
							
						renderObject = getPanelTextArea();
					}
				}
	
				if (getActiveComponent() instanceof JTable && renderObject instanceof JComponent) {
	
					int roHeight = renderObject.getPreferredSize().height;
					int cellHeight = ((JTable) getActiveComponent()).getRowHeight();
					if (roHeight < cellHeight)
						((JComponent) renderObject).setPreferredSize(new Dimension(renderObject.getPreferredSize().width, cellHeight));
				}
				
				renderObject.removeMouseListener(this);
				renderObject.addMouseListener(this);
	
				getLWindow().setBackground(ColorsGUI.getColorTip());
				getLWindow().removeAll();
				getLWindow().setSize(renderObject.getPreferredSize().width, renderObject.getPreferredSize().height);
				//Ajustamos el tamaño de la JWindow de LWindow para calcular luego bien el TooltipLocation
				getLWindow().adjustWindowSize();
				getLWindow().add(renderObject, BorderLayout.CENTER);
			}
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
		
		this.renderObject = renderObject;
		
		if (!isCustomTooltip && !isThereSpace() && !getLWindow().getWindow().isVisible()) {
			getTimer().restart();
		}
	}
	
	public LWindow getLWindowForComponent(Component component) {
		
		Window owner = component != null ? SwingUtilities.windowForComponent(component) : null;
		if (owner == null)
			owner = AppGUI.getCurrentAppGUI().getFrame();
			
		LWindow lWindow = getLWindowForOwnerMap().get(owner);
		if (lWindow == null) {
			lWindow = new LWindow(owner);
			lWindow.setLayout(new BorderLayout());
			lWindow.getWindowBorder().setCornersCurveSize(new Dimension(10, 10));
			lWindow.getWindowBorder().setLineBorderColor(ColorsGUI.getColorInfo());
			//lWindow.getBordeVentana().setColorSombra(Colors.darker(ColorsGUI.getColorInfo(), 0.3));
			getLWindowForOwnerMap().put(owner, lWindow);
		}
		
		return lWindow;
	}
	
	private HashMap<Window, LWindow> getLWindowForOwnerMap() {
		if (lWindowForOwnerMap == null)
			lWindowForOwnerMap = new HashMap<>();
		return lWindowForOwnerMap;
	}
	public LWindow getLWindow() {
		if (lWindow == null)
			lWindow = getLWindowForComponent(null);
		return lWindow;
	}
	private void setLWindow(LWindow lWindow) {
		this.lWindow = lWindow;
	}
	public int getShowDelay() {
		return showDelay;
	}
	public void setShowDelay(int newRetrasoMostrar) {
		showDelay = newRetrasoMostrar;
	}
	public Point getCursorLocation() {
		if (cursorLocation == null)
			cursorLocation = new Point(0, 0);
		return cursorLocation;
	}
	private void setCursorLocation(Point newCursorLocation) {
		cursorLocation = newCursorLocation;
	}
	private int getLastIndex() {
		return lastIndex;
	}
	private void setLastIndex(int newLastIndex) {
		lastIndex = newLastIndex;
	}
}
