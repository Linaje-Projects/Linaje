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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.LRadioButton;
import linaje.gui.LTitledBorder;
import linaje.gui.LToggleButton;
import linaje.gui.StateColorChooserPanel;
import linaje.gui.cells.LabelCell;
import linaje.gui.layouts.HorizBagLayout;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.UtilsGUI;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.Lists;
import linaje.utils.ReferencedColor;
import linaje.utils.StateColor;

@SuppressWarnings("serial")
public class LColorChooser extends JColorChooser {

	private PreviewPanel myPreviewPanel = null;
	private boolean dialogCancelled = true;
	private Class<?> sourceColorType = null;
	
	private ChangeListener colorChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			colorChanged();
		}
	};
	
	public class PreviewPanel extends JPanel {
		
		private JPanel panelBackgrounds = null;
		private JPanel panelForegrounds = null;
		
		public PreviewPanel() {
			super();
			initialize();
		}
		
		private void initialize() {
			
			setLayout(new HorizBagLayout(20));
			
			add(getPanelBackgrounds());
			add(getPanelForegrounds());
		}
		
		public JPanel getPanelBackgrounds() {
			if (panelBackgrounds == null) {
				
				JPanel panelBackgroundPlain = new JPanel();
				LPanel panelBackgroundGradient = new LPanel();
				LLabel bgLabelPlain = new LLabel("Label plain");
				LabelCell bgLabelGradient = new LabelCell("Label Gradient");
				bgLabelGradient.getPreferredSize();
				LToggleButton bgToggleButton = new LToggleButton("ToggleButton");
				LButton bgButton = new LButton("Button");
				LRadioButton bgRadioButton = new LRadioButton("RadioButton");
				LCheckBox bgCheckbox = new LCheckBox("Checkbox");
				
				Dimension dim = new Dimension(100, 80); 
				LTitledBorder titledBorderPlain = new LTitledBorder(BorderFactory.createLineBorder(GeneralUIProperties.getInstance().getColorText()), "Plain");
				titledBorderPlain.setTitleFont(UtilsGUI.getFontWithSizeFactor(titledBorderPlain.getTitleFont(), 0.8f)); 
				LTitledBorder titledBorderGradient = new LTitledBorder(BorderFactory.createLineBorder(GeneralUIProperties.getInstance().getColorText()), "Gradient");
				titledBorderGradient.setTitleFont(UtilsGUI.getFontWithSizeFactor(titledBorderGradient.getTitleFont(), 0.8f));
				titledBorderGradient.setInternalMargin(0);
				
				panelBackgroundPlain.setPreferredSize(dim);
				panelBackgroundPlain.setBorder(titledBorderPlain);
				
				panelBackgroundGradient.setPreferredSize(dim);
				panelBackgroundGradient.setGradientBackground(true);
				panelBackgroundGradient.setBorder(titledBorderGradient);
				
				bgLabelPlain.setOpaque(true);
				bgLabelGradient.setSelectedBackgroundGradient(true);
				bgLabelGradient.setSelected(true);
				
				panelBackgrounds = new JPanel(new HorizBagLayout(5));
				
				JPanel panelBG1 = new JPanel(new LFlowLayout(true));
				JPanel panelBG2 = new JPanel(new LFlowLayout(true));
				
				panelBackgrounds.add(panelBG1);
				panelBackgrounds.add(panelBG2);
				
				panelBG1.add(panelBackgroundPlain);
				panelBG1.add(panelBackgroundGradient);
				panelBG2.add(bgLabelPlain);
				panelBG2.add(bgLabelGradient);
				panelBG2.add(bgToggleButton);
				panelBG2.add(bgButton);
				panelBG2.add(bgRadioButton);
				panelBG2.add(bgCheckbox);
				
				LTitledBorder border = new LTitledBorder(BorderFactory.createEmptyBorder(), "Backgrounds");
				panelBG2.setBorder(border);
			}
			return panelBackgrounds;
		}
		
		public JPanel getPanelForegrounds() {
			if (panelForegrounds == null) {
				panelForegrounds = new JPanel(new LFlowLayout(true));
				
				LTitledBorder border = new LTitledBorder(BorderFactory.createEmptyBorder(), "Foregrounds");
				panelForegrounds.setBorder(border);
				
				LLabel fgLabelPlain = new LLabel("Label plain");
				LabelCell fgLabelGradient = new LabelCell("Label Gradient");
				LToggleButton fgToggleButton = new LToggleButton("ToggleButton");
				LButton fgButton = new LButton("Button");
				LRadioButton fgRadioButton = new LRadioButton("RadioButton");
				LCheckBox fgCheckbox = new LCheckBox("Checkbox");
				
				fgLabelPlain.setOpaque(true);
				fgLabelGradient.setSelectedBackgroundGradient(true);
				fgLabelGradient.setSelected(true);
				
				panelForegrounds.add(fgLabelPlain);
				panelForegrounds.add(fgLabelGradient);
				panelForegrounds.add(fgToggleButton);
				panelForegrounds.add(fgButton);
				panelForegrounds.add(fgRadioButton);
				panelForegrounds.add(fgCheckbox);
			}
			return panelForegrounds;
		}
		
		public void updateColors() {
			
			boolean isColorDark = Colors.getLuminance(getColor()) <= 0.5;
			Color bgColor = getColor();
			Color fgColor = isColorDark ? GeneralUIProperties.getInstance().getColorTextBrightest() : GeneralUIProperties.getInstance().getColorText();
			
			Component[] bgComponents1 = ((JPanel) getPanelBackgrounds().getComponent(0)).getComponents();
			Component[] bgComponents2 = ((JPanel) getPanelBackgrounds().getComponent(1)).getComponents();
			Component[] bgComponents = Lists.concat(Component.class, bgComponents1, bgComponents2);
			
			for (int i = 0; i < bgComponents.length; i++) {
				Component bgComponent = bgComponents[i];
				bgComponent.setBackground(bgColor);
				if (bgComponent instanceof LabelCell)
					((LabelCell) bgComponent).setSelectedBackground(bgColor);
				if (!(bgComponent instanceof LCheckBox) && !(bgComponent instanceof LRadioButton))
					bgComponent.setForeground(fgColor);
			}
			
			fgColor = getColor();
			bgColor = isColorDark ? GeneralUIProperties.getInstance().getColorPanels() : GeneralUIProperties.getInstance().getColorPanelsDark();
			
			Component[] fgComponents = getPanelForegrounds().getComponents();
			for (int i = 0; i < fgComponents.length; i++) {
				Component fgComponent = fgComponents[i];
				fgComponent.setForeground(fgColor);
				if (fgComponent instanceof LabelCell)
					((LabelCell) fgComponent).setSelectedBackground(bgColor);
				if (!(fgComponent instanceof LCheckBox) && !(fgComponent instanceof LRadioButton))
						fgComponent.setBackground(bgColor);
			}
			
			repaint();
		}
	}
	
	public LColorChooser() {
		this(Color.white, null);
	}
	public LColorChooser(Color initialColor) {
		this(new DefaultColorSelectionModel(initialColor), null);
	}
	public LColorChooser(ColorSelectionModel model) {
		this(model, null);
	}
	public LColorChooser(Class<?> sourceColorType) {
		this(Color.white, sourceColorType);
	}
	public LColorChooser(Color initialColor, Class<?> sourceColorType) {
		 this(new DefaultColorSelectionModel(initialColor), sourceColorType);
	}
	public LColorChooser(ColorSelectionModel model, Class<?> sourceColorType) {
		super(model);
		this.sourceColorType = sourceColorType;
		initialize();
	}

	private void initialize() {
		
		AbstractColorChooserPanel[] chooserPanels = getChooserPanelsByType();
		setChooserPanels(chooserPanels);
		
		getSelectionModel().addChangeListener(colorChangeListener);
		setPreviewPanel(getMyPreviewPanel());
		
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	private AbstractColorChooserPanel[] getChooserPanelsByType() {
		AbstractColorChooserPanel[] chooserPanels;
		if (sourceColorType != null && sourceColorType == StateColor.class) {
			chooserPanels = new AbstractColorChooserPanel[] {new StateColorChooserPanel()};
		}
		else if (sourceColorType != null && sourceColorType == ReferencedColor.class) {
			chooserPanels = new AbstractColorChooserPanel[] {new UIColorChooserPanel()};
		}
		else {
			chooserPanels = new AbstractColorChooserPanel[] {
					ColorChooserComponentFactory.getDefaultChooserPanels()[2],
					ColorChooserComponentFactory.getDefaultChooserPanels()[3],
					new UIColorChooserPanel(),
					new StateColorChooserPanel()
	        };
		}
		return chooserPanels;
	}
	public PreviewPanel getMyPreviewPanel() {
		if (myPreviewPanel == null) {
			myPreviewPanel = new PreviewPanel();
		}
		return myPreviewPanel;
	}
	
	/**
	 * El colorSelectionModel del ColorChooser no detecta cuando cambiamos uno de los stateColors distinto de defaultColor,
	 * o cuando cambiamos un campo de un ReferencedColor
	 * por lo que lo tenemos que detectar aquí y provocar un cambio temporal previo
	 */
	public void setColor(Color color) {
		
		Color oldColor = getColor();
		if (oldColor.getRGB() == color.getRGB() && !(oldColor.toString().equals(color.toString()))) {
			Color tempColor = new Color(oldColor.getRGB() != Color.white.getRGB() ? Color.white.getRGB() : Color.black.getRGB());
			getSelectionModel().removeChangeListener(colorChangeListener);
			super.setColor(tempColor);
			getSelectionModel().addChangeListener(colorChangeListener);
		}
		super.setColor(color);
    }
	
	private void colorChanged() {
		getMyPreviewPanel().updateColors();

		JDialog dialog = (JDialog) UtilsGUI.getParentInstanceOf(this, JDialog.class);
		if (dialog != null) {
			dialog.setTitle("Color " + getEncodedColor());
		}
	}
	
	private String getEncodedColor() {
		
		Color selectedColor = getColor();
		String encodedColor = Colors.encode(selectedColor);
		if (selectedColor instanceof StateColor) {
			StateColor stateColor = (StateColor) selectedColor;
			//encodedColor =	encodedColor + "StateColor defaultColor: " + Colors.encode(new Color(stateColor.getDefaultColor().getRGB()));
			encodedColor = Colors.encode(new Color(stateColor.getDefaultColor().getRGB())) + Constants.SPACE + encodedColor;
		}
		else if (selectedColor instanceof ReferencedColor) {
			ReferencedColor refColor = (ReferencedColor) selectedColor;
			encodedColor =	Colors.encode(new Color(refColor.getColor().getRGB())) + Constants.SPACE + encodedColor;
		}
				
		return encodedColor;
	}

	public static Color showDialog(Component component, String title, Color initialColor) throws HeadlessException {
		return showDialog(component, title, initialColor, null);
	}
	public static Color showDialog(Component component, String title, Color initialColor, Class<?> sourceColorType) throws HeadlessException {

		final LColorChooser lColorChooser = new LColorChooser(initialColor, sourceColorType);
		lColorChooser.dialogCancelled = true;
		
		ActionListener okListener = new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				lColorChooser.dialogCancelled = false;
			}
		};
		
		JDialog dialog = JColorChooser.createDialog(component, title, true, lColorChooser, okListener, null);
		dialog.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
				lColorChooser.colorChanged();
			}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
		dialog.setVisible(true);
		
		return lColorChooser.dialogCancelled ? null : lColorChooser.getColor();
	}
}
