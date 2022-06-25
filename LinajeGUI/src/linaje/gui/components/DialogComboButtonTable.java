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

import java.awt.event.*;

import javax.swing.*;

import java.awt.*;

import javax.swing.event.*;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.table.LTable;
import linaje.gui.table.LTableModel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.MessageDialog;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.tree.TreeNodeVector;

@SuppressWarnings("serial")
public class DialogComboButtonTable<E> extends LDialogContent implements ActionListener, MouseListener, ListSelectionListener {
	
	private LButton btnAccept = null;
	private LButton btnCancel = null;
	private LButton btnExpand = null;
	private LButton btnCollapse = null;
	
	private JPanel panelSouth = null;
	private JPanel panelButtons = null;
	private JPanel panelButtonsTree = null;
	private JPanel panelSeparaBotones = null;
	
	private ComboButtonTable<E> comboButtonTable = null;
	private TableItemsCombo<E> tableItemsCombo = null;
	
	public DialogComboButtonTable(ComboButtonTable<E> comboBotonTabla) {
		
		super(AppGUI.getCurrentAppGUI().getFrame());
		this.comboButtonTable = comboBotonTabla;
		initialize();
	}
	
	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent evento) {
	
		try {
			
		    if (evento.getSource() == getBtnAccept()) {
				btnAceptar_ActionPerformed();
			}
		    else if (evento.getSource() == getBtnCancel()) {
				dispose();
			}
			else if (evento.getSource() == getBtnExpand()) {
				getTablaDatoCombo().expandRows();
			} 
			else if (evento.getSource() == getBtnCollapse()) {
				getTablaDatoCombo().collapseRows();
			}
		}
		catch (Throwable exception) {
			handleException(exception);
		}
		
	}
	
	private void btnAceptar_ActionPerformed() {
	
		try {
	
			LTable<E> table = getTablaDatoCombo().getTable();
			LTableModel<E> model = table.getModel();
			TreeNodeVector<E> nodoSeleccionado = model.getSelectedRow();
			
			if (nodoSeleccionado == null) {
				MessageDialog.showMessage("Seleccione un epígrafe, por favor", MessageDialog.ICON_WARNING);
				return;
			}
	
			if (!table.isEnabledRow(nodoSeleccionado)) {
				return;
			}
	
			E selectedItem = nodoSeleccionado.getUserObject();
	
			getComboBotonTabla().getCombo().setSelectedItem(selectedItem);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
		dispose();
	}
	
	private LButton getBtnAccept() {
		if (btnAccept == null) {
			btnAccept = new LButton(Constants.SPACE);
			btnAccept.setMargin(new Insets(0, 0, 1, 0));
			int h = Math.round(btnAccept.getPreferredSize().height*0.8f);
			btnAccept.setPreferredSize(new Dimension(80, h));
			btnAccept.setText(Constants.VOID);
			//btnAccept.getButtonProperties().setIconForegroundEnabled(false);
			//btnAccept.setIcon(Icons.getIconCheckMark(Icons.SIZE_ICONS, false, ColorsGUI.getColorPositive()));
			btnAccept.setIcon(Icons.ACCEPT);
			//btnAccept.setForeground(new StateColor(ColorsGUI.getColorPositive()));
			btnAccept.setForeground(ColorsGUI.getColorPositive());
		}
		return btnAccept;
	}
	
	private LButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new LButton(Constants.VOID);
			btnCancel.setPreferredSize(getBtnAccept().getPreferredSize());
			//btnCancel.getButtonProperties().setIconForegroundEnabled(false);
			//btnCancel.setIcon(Icons.getIconX(Icons.SIZE_ICONS, 2, ColorsGUI.getColorNegative()));
			btnCancel.setIcon(Icons.CANCEL);
			//btnCancel.setForeground(new StateColor(ColorsGUI.getColorNegative()));
			btnCancel.setForeground(ColorsGUI.getColorNegative());
		}
		return btnCancel;
	}
	
	private LButton getBtnExpand() {
		if (btnExpand == null) {
			btnExpand = new LButton(Constants.VOID);
			btnExpand.setPreferredSize(getBtnCollapse().getPreferredSize());
			btnExpand.setToolTipText("Desplegar epígrafes");
			btnExpand.setIcon(Icons.EXPAND);
		}
		return btnExpand;
	}
	
	private LButton getBtnCollapse() {
		if (btnCollapse == null) {
			btnCollapse = new LButton(Constants.SPACE);
			btnCollapse.setMargin(new Insets(0, 0, 0, 0));
			int size = Math.round(btnCollapse.getPreferredSize().height*0.8f);
			btnCollapse.setPreferredSize(new Dimension(size, size));
			btnCollapse.setText(Constants.VOID);
			btnCollapse.setToolTipText("Plegar epígrafes");
			btnCollapse.setIcon(Icons.COLLAPSE);
		}
		return btnCollapse;
	}
	
	private JPanel getPanelBotonesMin() {
		if (panelButtons == null) {
			panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 2));
			panelButtons.setOpaque(false);
			panelButtons.add(getBtnAccept(), getBtnAccept().getName());
			panelButtons.add(getPanelSeparaBotones(), getPanelSeparaBotones().getName());
			panelButtons.add(getBtnCancel(), getBtnCancel().getName());
		}
		return panelButtons;
	}
	
	private JPanel getPanelButtonsTree() {
		if (panelButtonsTree == null) {
			panelButtonsTree = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			//panelButtonsTree.setPreferredSize(new Dimension(35, 0));
			panelButtonsTree.setOpaque(false);
			panelButtonsTree.add(getBtnCollapse(), getBtnCollapse().getName());
			panelButtonsTree.add(getBtnExpand(), getBtnExpand().getName());
		}
		return panelButtonsTree;
	}
	
	private JPanel getPanelSeparaBotones() {
		if (panelSeparaBotones == null) {
			panelSeparaBotones = new JPanel();
			panelSeparaBotones.setPreferredSize(new Dimension(5, 5));
			panelSeparaBotones.setOpaque(false);
		}
		return panelSeparaBotones;
	}
	
	private JPanel getPanelSouth() {
		if (panelSouth == null) {
			panelSouth = new JPanel(new BorderLayout());
			//panelSouth.setPreferredSize(new Dimension(117, 16));
			panelSouth.setOpaque(false);
			panelSouth.add(getPanelBotonesMin(), BorderLayout.CENTER);
			panelSouth.add(getPanelButtonsTree(), BorderLayout.WEST);
		}
		return panelSouth;
	}
	
	public TableItemsCombo<E> getTablaDatoCombo() {
		if (tableItemsCombo == null) {
			tableItemsCombo = new TableItemsCombo<E>();
		}
		return tableItemsCombo;
	}
	
	private void handleException(Throwable exception) {
		Console.printException(exception);
	}

	private void iniciarConexiones() {
	
		getBtnAccept().addActionListener(this);
		getBtnCancel().addActionListener(this);
		getBtnCollapse().addActionListener(this);
		getBtnExpand().addActionListener(this);
		
		getTablaDatoCombo().getTable().addMouseListener(this);
	
		getTablaDatoCombo().getTable().getSelectionModel().addListSelectionListener(this);
	}
	
	private void iniciarTabla() {
	
		setTitle(getComboBotonTabla().getDialogTitle());
	
		E selecteditem = getComboBotonTabla().getCombo().getSelectedItem();
		getTablaDatoCombo().iniciarTabla(getComboBotonTabla().getCombo().getItems(), false, getComboBotonTabla().getDialogTitle());
		
		int nivelDesglose = getComboBotonTabla().getInitialExpandLevel();
		if (nivelDesglose <= 0)			
			getTablaDatoCombo().collapseRows();
		else	
			getTablaDatoCombo().modifyExpandLevel(nivelDesglose);
		
		getTablaDatoCombo().setSelectedItem(selecteditem);
	}
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setSize(335, 240);
		setTitle("Selección de elemento");
		setResizable(false);
		
		setLayout(new BorderLayout());
		add(getPanelSouth(), BorderLayout.SOUTH);
		add(getTablaDatoCombo(), BorderLayout.CENTER);
		
		iniciarConexiones();
		iniciarTabla();
	
		getTablaDatoCombo().setBorder(BorderFactory.createEmptyBorder());
		getBtnAccept().setEnabled(getTablaDatoCombo().getTable().getSelectedRows().length > 0);
	}
	
	public void mouseClicked(MouseEvent evento) {}
	public void mouseEntered(MouseEvent evento) {}
	public void mouseExited(MouseEvent evento) {}
	public void mousePressed(MouseEvent evento) {}
	public void mouseReleased(MouseEvent evento) {
	
		if (evento.getSource() == getTablaDatoCombo().getTable()) {
			
			LTable<E> tabla = getTablaDatoCombo().getTable();

			if (evento.getClickCount() == 2 && tabla.getActionMouseOver() == -1)
				btnAceptar_ActionPerformed();
		}
	}
	
	/** 
	   * Called whenever the value of the selection changes.
	   * @param e the event that characterizes the change.
	   */
	public void valueChanged(ListSelectionEvent e) {
	
		if (e.getSource() == getTablaDatoCombo().getTable().getSelectionModel()) {
	
			if (!e.getValueIsAdjusting()) {
	
				LTableModel<E> modelo = getTablaDatoCombo().getModel();
				TreeNodeVector<E> nodoSeleccionado = modelo.getSelectedRow();
				
				boolean habilitarAceptar = getTablaDatoCombo().getTable().isEnabledRow(nodoSeleccionado);
				
				getBtnAccept().setEnabled(habilitarAceptar);
			}
		}
	}

	private ComboButtonTable<E> getComboBotonTabla() {
		return comboButtonTable;
	}
}
