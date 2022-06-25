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
package linaje.gui.tests;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import linaje.gui.FieldsPanel;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.RoundedBorder;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

@SuppressWarnings("serial")
public class TestPanel extends LPanel {

	private FieldsPanel fieldsPanel = null;
	private JScrollPane scrollPaneFields = null;
	private Component testComponent = null;
	private boolean autoCreateFieldsFromTestComponent = true;
	
	public TestPanel(Component testComponent) {
		this(testComponent, true);
	}
	public TestPanel(Component testComponent, boolean autoCreateFields) {
		super(new BorderLayout());
		this.autoCreateFieldsFromTestComponent = autoCreateFields;
		setTestComponent(testComponent);
		add(getScrollPaneFields(), BorderLayout.EAST);
	}
	
	public JScrollPane getScrollPaneFields() {
		if (scrollPaneFields == null) {
			scrollPaneFields = new JScrollPane() {
				@Override
				public Dimension getPreferredSize() {
					Dimension prefSize = super.getPreferredSize();
					if (getVerticalScrollBar().isVisible())
						prefSize.width += getVerticalScrollBar().getWidth();
					return prefSize;
				}
			};
			scrollPaneFields.setViewportView(getFieldsPanel());
			Border outBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
			Border inBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, ColorsGUI.getColorBorder());
			scrollPaneFields.setBorder(new CompoundBorder(new CompoundBorder(outBorder, inBorder), outBorder));
		}
		return scrollPaneFields;
	}
	
	public FieldsPanel getFieldsPanel() {
		if (fieldsPanel == null) {
			fieldsPanel = new FieldsPanel(getTestComponent());
			fieldsPanel.setSize(new Dimension(150, 150));
		}
		return fieldsPanel;
	}
	
	public Component getTestComponent() {
		return testComponent;
	}

	public void setTestComponent(Component testComponent) {
		Component oldComponent = this.testComponent;
		if (UtilsGUI.propertyChanged(oldComponent, testComponent)) {
			this.testComponent = testComponent;
			if (oldComponent != null) {
				getFieldsPanel().removeAll();
				remove(oldComponent);
			}
			if (testComponent != null) {
				add(testComponent, BorderLayout.CENTER);
				Dimension size = testComponent.getSize();
				if (autoCreateFieldsFromTestComponent) {
					getFieldsPanel().addAccessComponentsFromFields(testComponent);
					size.width = size.width + getFieldsPanel().getPreferredSize().width;
				}
				else {
					size.width = size.width + getFieldsPanel().getWidth();
				}
				setSize(size);
			}
		}
	}
	
	public static void main(String[] args) {

		try {
			
			LinajeLookAndFeel.init();
			
			LTabbedPane testObject = TestLTabbedPane.getTestComponent();
			TestPanel testPanel = new TestPanel(testObject);
			
			testPanel.getFieldsPanel().addAccessComponentsFromFields(testObject.getUI());
			
			LDialogContent.showComponentInFrame(testPanel);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
