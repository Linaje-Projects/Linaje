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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.windows.ButtonsPanel;
import linaje.statics.Constants;
import linaje.utils.Dates;

@SuppressWarnings("serial")
public class LTextFieldContainer extends LPanel {
	
	private LButton button = null;
	private SpinButton spinButton = null;
	private LTextField lTextField = null;
	private CalendarComponent calendarComponent = null;
	private boolean spinNumericsVisible = true;

	public LTextFieldContainer() {
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		setSize(160, getLTextField().getHeight());
		add(getLTextField(), BorderLayout.CENTER);
		updateAspect();
	}
	
	public LTextField getLTextField() {
		if (lTextField == null) {
			lTextField = new LTextField();
			
			lTextField.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					
					String propertyName = evt.getPropertyName();
					if (propertyName.equals(LTextField.PROPERTY_TYPE) || propertyName.equals(LTextField.PROPERTY_DECIMALS)) {
						updateAspect();
					}
					else if (spinButton != null) {
						
						if (propertyName.equals(LTextField.PROPERTY_MAX_VALUE)) {
							int max = new Double(evt.getNewValue().toString()).intValue();
							getSpinButton().setMaximum(max);
						}
						else if (propertyName.equals(LTextField.PROPERTY_MIN_VALUE)) {
							int min = new Double(evt.getNewValue().toString()).intValue();
							getSpinButton().setMinimum(min);
						}
					}
				}
			});
		}
		return lTextField;
	}
	
	private LButton getButton() {
		if (button == null) {
			button = new LButton();
			int buttonSize = getLTextField().getPreferredSize().height;
			button.setPreferredSize(new Dimension(buttonSize, buttonSize));
			button.setText(Constants.VOID);
			//button.setIcon(Iconos.ICONO_CALENDARIO);
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int type = getLTextField().getType();
					if (type == LTextField.TYPE_DATE) {
						getCalendarComponent().setSelectedDate(getLTextField().getValueDate());
						int response = getCalendarComponent().showInDialog(getButton());
						if (response == ButtonsPanel.RESPONSE_ACCEPT_YES) {
							String selectedDateFormatted = Dates.getFormattedDate(getCalendarComponent().getSelectedDate());
							getLTextField().setText(selectedDateFormatted);
							//Provocamos el focusLost para que se repinten los LTextFields
							getLTextField().requestFocus();
							getLTextField().transferFocus();
							boolean isfocusable = getLTextField().isFocusable();
							getLTextField().setFocusable(false);
							getLTextField().setFocusable(isfocusable);
						}
					}
					else if (type == LTextField.TYPE_COLOR) {
						Color color = getLTextField().getValueColor();		
						Color newColor = LColorChooser.showDialog(null, "Color", color, getLTextField().getClassType());
						if (newColor != null && (newColor.getRGB() != color.getRGB() || !newColor.toString().equals(color.toString())))
							getLTextField().setValue(newColor);
					}
					else if (type == LTextField.TYPE_FONT) {
						Font font = getLTextField().getValueFont();
						FontChooser fontChooser = new FontChooser();
						fontChooser.setSelectedFont(font);
						fontChooser.setSize(new Dimension(AppGUI.getCurrentAppGUI().getFrame().getWidth()-50, AppGUI.getCurrentAppGUI().getFrame().getHeight()-100));
						int response = fontChooser.showInDialog();
						if (response == ButtonsPanel.RESPONSE_ACCEPT_YES) {
							Font newFont = fontChooser.getSelectedFont();
							if (newFont != null && !newFont.toString().equals(font.toString()))
								getLTextField().setValue(newFont);
						}
					}
				}
			});
		}
		return button;
	}
	
	private CalendarComponent getCalendarComponent() {
		if (calendarComponent == null)
			calendarComponent = new CalendarComponent();
		return calendarComponent;
	}
	
	public SpinButton getSpinButton() {
		if (spinButton == null) {
			spinButton = new SpinButton();
			spinButton.setMaximum(new Double(getLTextField().getMaxValue()).intValue());
			spinButton.setMinimum(new Double(getLTextField().getMinValue()).intValue());
		}
		return spinButton;
	}
	
	private void updateAspect() {
		
		int type = getLTextField().getType();
	
		if (spinButton != null)
			getSpinButton().setTextField(null);
	
		if (button != null)
			remove(getButton());
		if (spinButton != null)
			remove(getSpinButton());
		
		if (this.isEnabled()) {
			
			if (type == LTextField.TYPE_NUMBER) {
				int maxDecimals = getLTextField().getDecimals();
				if (maxDecimals == 0 && isSpinNumericsVisible()) {
					add(getSpinButton(), BorderLayout.EAST);
					getSpinButton().setTextField(getLTextField());
				}
			}
			else if (type == LTextField.TYPE_DATE || type == LTextField.TYPE_COLOR || type == LTextField.TYPE_FONT) {
				add(getButton(), BorderLayout.EAST);
				Icon icon = type == LTextField.TYPE_DATE ? Icons.CALENDAR : null;
				getButton().setIcon(icon);
			}
		}
		
		revalidate();
		repaint();
	}
	
	@Override
	public void setEnabled(boolean b) {
		
		super.setEnabled(b);
		getLTextField().setEnabled(b);
		
		if (button != null)
			getButton().setEnabled(b);
		if (spinButton != null)
			getSpinButton().setEnabled(b);
		
		updateAspect();
	}
	
	public boolean isSpinNumericsVisible() {
		return spinNumericsVisible;
	}
	public void setSpinNumericsVisible(boolean spinNumericsVisible) {
		this.spinNumericsVisible = spinNumericsVisible;
		updateAspect();
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		updateUiHideComponents();
	}
	
	public void updateUiHideComponents() {
		
		updateUiHideComponent(button);
		updateUiHideComponent(spinButton);
		updateUiHideComponent(lTextField);
		updateUiHideComponent(calendarComponent);
	}
	
	private void updateUiHideComponent(JComponent c) {
		if (c != null && !c.isShowing())
			c.updateUI();
	}
}
