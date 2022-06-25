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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import linaje.gui.LLabel;
import linaje.gui.ToolTip;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class LabelComponent extends JPanel {
	
	//ORIENTATION
	public static final int HORIZONTAL = SwingConstants.HORIZONTAL;
	public static final int VERTICAL = SwingConstants.VERTICAL;
	
	private LLabel label = null;
	private JComponent component = null;
	private JPanel panelAux = null;
	
	private int orientation = HORIZONTAL;
	private boolean autoSizeLabel = false;
	private int widthLabel;
	
	//Cómo se expandirá el componente en caso de que LabelComponent sea mas grande que su preferredSize
	//GridBagConstraints.NONE, GridBagConstraints.BOTH, GridBagConstraints.HORIZONTAL, GridBagConstraints.VERTICAL
	private int fillComponent;
	private Color lineColor = null;
	
	private boolean initializing = true;

	public LabelComponent() {
		this(null);
	}
	public LabelComponent(JComponent component) {
		this(null, component);
	}
	public LabelComponent(String text, JComponent component) {
		this(text, component, HORIZONTAL);
	}
	public LabelComponent(String text, JComponent component, int orientation) {
		this(text, component, orientation, 100);
	}
	public LabelComponent(String text, JComponent component, int orientation, int widthLabel) {
		this(text, component, orientation, 100, GridBagConstraints.HORIZONTAL);
	}
	public LabelComponent(String text, JComponent component, int orientation, int widthLabel, int fillComponent) {
		this(text, component, orientation, 100, GridBagConstraints.HORIZONTAL, null);
	}
	public LabelComponent(String text, JComponent component, int orientation, int widthLabel, int fillComponent, Color lineColor) {
		super();
		if (text == null)
			text = Constants.VOID;
		getLabel().setText(text);
		setComponent(component);
		setOrientation(orientation);
		setFillComponent(fillComponent);
		this.lineColor = lineColor;
		this.autoSizeLabel = widthLabel < 0;
		if (!autoSizeLabel)
			this.widthLabel = widthLabel;
		
		initializing = false;
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		addComponents();
		setSize(getPreferredSize());
		resizeLabel();
	}
	
	public int getWidthLabel() {
		return isAutoSizeLabel() ? getLabel().getPreferredSize().width : widthLabel;
	}
	public boolean isAutoSizeLabel() {
		return autoSizeLabel;
	}
	public JComponent getComponent() {
		return component;
	}

	public Font getFontLabel() {
		return getLabel().getFont();
	}
	
	public LLabel getLabel() {
		if (label == null) {
			label = new LLabel() {
				@Override
				protected void paintComponent(Graphics g) {
					if (getLineColor() != null) {
						g.setColor(getLineColor());
						g.drawLine(1, getHeight()-1, getWidth()-1, getHeight()-1);
					}
					super.paintComponent(g);
				}
			};
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setMargin(new Insets(0,0,0,0));
		}
		return label;
	}
	
	private JPanel getPanelAux() {
		if (panelAux == null) {
			panelAux = new JPanel();
			panelAux.setPreferredSize(new Dimension(0, 0));
			panelAux.setOpaque(false);
		}
		return panelAux;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public Color getLineColor() {
		return lineColor;
	}
	
	/**
	 * Cómo se expandirá el componente en caso de que LabelComponent sea mas grande que su preferredSize
	 * GridBagConstraints.NONE, GridBagConstraints.BOTH, GridBagConstraints.HORIZONTAL, GridBagConstraints.VERTICAL
	 **/
	public int getFillComponent() {
		return fillComponent;
	}
	
	public String getTextLabel() {
		return getLabel().getText();
	}

	public boolean isEnabledComponent() {
		if (getComponent() != null)
			return getComponent().isEnabled();
		else
			return false;
	}
	
	public boolean isEnabledLabel() {
		return getLabel().isEnabled();
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setEnabledLabel(enabled);
		setEnabledComponent(enabled);
	}

	public void setEnabledComponent(boolean enabled) {
		if (getComponent() != null)
			getComponent().setEnabled(enabled);
	}

	public void setEnabledLabel(boolean enabled) {
		getLabel().setEnabled(enabled);
	}

	public void setFontLabel(Font font) {
		getLabel().setFont(font);
	}
	
	public void setTextLabel(String textLabel) {
		
		if (textLabel == null)
			textLabel = Constants.VOID;
		
		getLabel().setText(textLabel);
		resizeLabel();
	}
	
	public void setTooltipEnabled(boolean tooltipEnabled) {
	
		if (tooltipEnabled) {
			ToolTip.getInstance().registerComponent(getLabel());
		}
		else {
			ToolTip.getInstance().unRegisterComponent(getLabel());
		}
	}
	
	public void setLineColor(Color lineColor) {
		if (this.lineColor != lineColor) {
			this.lineColor = lineColor;
			addComponents();
		}
	}
	
	public void setOrientation(int orientation) {
	
		//No dejamos poner una orientación que no sea Horizontal o Vertical
		if (orientation > 1)
			orientation = HORIZONTAL;
		
		if (this.orientation != orientation) {
			this.orientation = orientation;
			addComponents();
		}
	}

	public void setWidthLabel(int widthLabel) {
		if (this.widthLabel != widthLabel) {
			this.widthLabel = widthLabel;
			if (widthLabel < 0)
				setAutoSizeLabel(true);
			else
				resizeLabel();
		}
	}

	public void setAutoSizeLabel(boolean autoSizeLabel) {
		if (this.autoSizeLabel != autoSizeLabel) {
			this.autoSizeLabel = autoSizeLabel;
			resizeLabel();
		}
	}
	
	private void resizeLabel() {
		getLabel().setPreferredSize(null);
		Dimension sizeLabel = isAutoSizeLabel() ? null : new Dimension(getWidthLabel(), getLabel().getPreferredSize().height);
		getLabel().setPreferredSize(sizeLabel);
		getLabel().setMinimumSize(sizeLabel);
	}

	public void setComponent(JComponent component) {
		if (this.component != component) {
			this.component = component;
			addComponents();
		}
	}
	
	/**
	 * Cómo se expandirá el componente en caso de que LabelComponent sea mas grande que su preferredSize
	 * GridBagConstraints.NONE, GridBagConstraints.BOTH, GridBagConstraints.HORIZONTAL, GridBagConstraints.VERTICAL
	 **/
	public void setFillComponent(int fillComponent) {
		
		if (fillComponent < GridBagConstraints.NONE || fillComponent > GridBagConstraints.VERTICAL)
			fillComponent = GridBagConstraints.HORIZONTAL;
		
		if (this.fillComponent != fillComponent) {
			this.fillComponent = fillComponent;
			addComponents();
		}
	}
	
	private void addComponents() {
		
		if (initializing)
			return;
		
		removeAll();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		
		boolean expandComponentVertical = getFillComponent() == GridBagConstraints.VERTICAL || getFillComponent() == GridBagConstraints.BOTH;
		
		if (getOrientation() == HORIZONTAL) {
			
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.CENTER;
			
			add(getLabel(), gbc);
			
			gbc.weightx = 1.0;
			gbc.weighty = expandComponentVertical ? 1.0 : 0.0;
			
			if (getComponent() != null) {
				gbc.anchor = GridBagConstraints.WEST;
				gbc.gridx = 2;
				gbc.fill = getFillComponent();
				gbc.insets.left = 5;
				
				add(getComponent(), gbc);
			}
			
			gbc.insets.left = 0;
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
		}
		else  { //VERTICAL
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTH;
			
			add(getLabel(), gbc);
			
			gbc.weightx = 1.0;
			gbc.weighty = expandComponentVertical ? 1.0 : 0.0;
			
			if (getComponent() != null) {
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.gridy = 2;
				gbc.fill = getFillComponent();
				gbc.insets.top = getLineColor() != null ? 3 : 1;
				
				add(getComponent(), gbc);
			}
			
			gbc.insets.top = 0;
			gbc.gridy = 3;
		}
		
		if (!expandComponentVertical) {
			
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.fill = GridBagConstraints.BOTH;
			
			add(getPanelAux(), gbc);
		}
	}
}
