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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import linaje.gui.LButton;
import linaje.gui.LCombo;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.cells.DataCell;
import linaje.statics.Constants;
 
@SuppressWarnings("serial")
public class ComboButtonTable<E> extends LPanel {
	
	private LButton button = null;
	private LTextField textFieldSelectedItem = null;
	private LCombo<E> combo;
	private String dialogTitle = null;
	private int initialExpandLevel = 1;
	private boolean updateTextOnChange = true;
	
	private DialogComboButtonTable<E> dialogComboButtonTable = null;

	public ComboButtonTable(LCombo<E> combo) {
		super();
		setCombo(combo);
		initialize();
	}
	
	public void updateSelectedItem(E selectedItem) {
	
		getDialogComboButtonTable().dispose();
		
		if (isUpdateTextOnChange()) {
			String descItem = Constants.VOID;
			if (selectedItem != null)
				descItem = selectedItem instanceof DataCell ? ((DataCell) selectedItem).getValue().toString() : selectedItem.toString();
		
			getTextFieldSelectedItem().setText(descItem);
			getTextFieldSelectedItem().clearSelection();
		}
	}

	private int getPreferredHeight() {
		return getCombo().getPreferredSize().height;
	}
	
	public LButton getButton() {
		if (button == null) {
			button = new LButton(Constants.VOID);//new LArrowButton(SwingConstants.NORTH);
			int height = getPreferredHeight();//20;
			Dimension size = new Dimension(height, height);
			button.setPreferredSize(size);
			button.setMinimumSize(size);
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int locationHGap = -getDialogComboButtonTable().getWidth() + getButton().getWidth();
					int locationVGap = getButton().getHeight() + 2;
					
					getDialogComboButtonTable().showInDialog(getButton(), locationHGap, locationVGap);
				}
			});
		}
		return button;
	}
	
	protected LTextField getTextFieldSelectedItem() {
		if (textFieldSelectedItem == null) {
			
			textFieldSelectedItem = new LTextField();
			textFieldSelectedItem.setOpaque(true);
			textFieldSelectedItem.setEditable(false);
			int height = getPreferredHeight();//20;
			textFieldSelectedItem.setPreferredSize(new Dimension(125, height));
			textFieldSelectedItem.setMinimumSize(new Dimension(height, height));
			
			textFieldSelectedItem.clearSelection();
		}
		return textFieldSelectedItem;
	}

	private void initialize() {
		
		setLayout(new GridBagLayout());
		setOpaque(false);
		int height = getPreferredHeight();//20;
		setSize(145, height);
		
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 1;
		gbcTextField.anchor = GridBagConstraints.CENTER;
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.weightx = 1.0;
		gbcTextField.weighty = 0.0;
		
		GridBagConstraints gbcBoton = new GridBagConstraints();
		gbcBoton.gridx = 2;
		gbcBoton.gridy = 1;
		gbcBoton.anchor = GridBagConstraints.CENTER;
		gbcBoton.fill = GridBagConstraints.NONE;
		gbcBoton.weightx = 0.0;
		gbcBoton.weighty = 0.0;
		
		/*GridBagConstraints gbcPanelAux = new GridBagConstraints();
		gbcPanelAux.gridx = 1;
		gbcPanelAux.gridy = 2;
		gbcPanelAux.gridwidth = 2;
		gbcPanelAux.fill = GridBagConstraints.BOTH;
		gbcPanelAux.weightx = 1.0;
		gbcPanelAux.weighty = 1.0;
		
		JPanel panelAux = new JPanel();
		panelAux.setOpaque(false);
		*/
		add(getTextFieldSelectedItem(), gbcTextField);
		add(getButton(), gbcBoton);
		//add(panelAux, gbcPanelAux);
		
		if (combo != null)
			updateSelectedItem(getCombo().getSelectedItem());
	}

	public boolean getCerrado() {
		return !getDialogComboButtonTable().getDialog().isVisible();
	}
	public LCombo<E> getCombo() {
		return combo;
	}
	public int getInitialExpandLevel() {
		return initialExpandLevel;
	}
	public String getDialogTitle() {
		if (dialogTitle == null)
			dialogTitle = Constants.VOID;
		return dialogTitle;
	}
	
	private void setCombo(LCombo<E> combo) {
		this.combo = combo;
		if (combo != null) {
			combo.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						updateSelectedItem(getCombo().getSelectedItem());
					}
				}
			});
		}
	}
	public void setInitialExpandLevel(int initialExpandLevel) {
		this.initialExpandLevel = initialExpandLevel;
	}
	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}
	
	public DialogComboButtonTable<E> getDialogComboButtonTable() {
		if (dialogComboButtonTable == null)
			dialogComboButtonTable = new DialogComboButtonTable<>(this);
		return dialogComboButtonTable;
	}

	public boolean isUpdateTextOnChange() {
		return updateTextOnChange;
	}

	public void setUpdateTextOnChange(boolean updateTextOnChange) {
		this.updateTextOnChange = updateTextOnChange;
	}	
}
