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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import linaje.gui.LTextField;

@SuppressWarnings("serial")
public class LabelTextField extends LabelComponent {
	
	private LTextFieldContainer lTextFieldContainer = null;
	
	public LabelTextField() {
		this(null);
	}
	public LabelTextField(String text) {
		this(text, HORIZONTAL);
	}
	public LabelTextField(String text, int orientation) {
		this(text, orientation, 100);
	}
	public LabelTextField(String text, int orientation, int widthLabel) {
		this(text, orientation, 100, GridBagConstraints.HORIZONTAL);
	}
	public LabelTextField(String text, int orientation, int widthLabel, int fillComponent) {
		this(text, orientation, 100, GridBagConstraints.HORIZONTAL, null);
	}
	public LabelTextField(String text, int orientation, int widthLabel, int fillComponent, Color lineColor) {
		super(text, null, orientation, 100, fillComponent, lineColor);
		setComponent(getLTextFieldContainer());
	}
	
	public LTextField getTextField() {
		return getLTextFieldContainer().getLTextField();
	}
	
	public LTextFieldContainer getLTextFieldContainer() {
		if (lTextFieldContainer == null) {
			lTextFieldContainer = new LTextFieldContainer();
		}
		return lTextFieldContainer;
	}
	
	public boolean isEditable() {
		return getTextField().isEditable();
	}
	public Font getFont() {
		return getTextField().getFont();
	}
	public String getText() {
		return getTextField().getText();
	}
	public int getType() {
		return getTextField().getType();
	}
	public int getHorizontalAlignment() {
		return getTextField().getHorizontalAlignment();
	}
	public boolean isSpinNumericsVisible() {
		return getLTextFieldContainer().isSpinNumericsVisible();
	}
		
	public void setEditable(boolean editable) {
		getTextField().setEditable(editable);
	}
	public void setFont(Font font) {
		getTextField().setFont(font);
	}
	public void setHorizontalAlignment(int alignment) {
		getTextField().setHorizontalAlignment(alignment);
	}
	public void setText(String text) {
		getTextField().setText(text);	
	}
	public void setType(int type) {
		getTextField().setType(type);
	}
	public void setSpinNumericsVisible(boolean spinNumericsVisible) {
		getLTextFieldContainer().setSpinNumericsVisible(spinNumericsVisible);
	}
}
