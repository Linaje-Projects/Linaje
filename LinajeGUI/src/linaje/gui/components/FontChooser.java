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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linaje.LocalizedStrings;
import linaje.gui.AppGUI;
import linaje.gui.LCheckBox;
import linaje.gui.LLabel;
import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.LToggleButton;
import linaje.gui.Task;
import linaje.gui.Tasks;
import linaje.gui.ToolTip;
import linaje.gui.renderers.CellRendererFonts;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.TypographyLabel;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.utils.LFont;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class FontChooser extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String style;
		public String size;
		public String layout;
		
		public String plain;
		public String bold;
		public String italic;
		public String boldItalic;
		
		public String heightMax;
		public String heightMost;
		public String heightCommon;
		public String heightExact;
		
		public String descHeightMax;
		public String descHeightMost;
		public String descHeightCommon;
		public String descHeightExact;
		
		public String sampleLabel;
		public String sampleButton;
		
		public String obtainingFonts;
		public String hideStyledFonts;
		
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private JScrollPane scrollPane = null;
	private LabelCombo<String> lblCmbStyle = null;
	private LabelCombo<String> lblCmbSize = null;
	private LabelCombo<String> lblCmbLayoutMode = null;
	private LPanel panelOptions = null;
	private LList<Font> listFonts = null;
	private TypographyLabel typographyLabel = null;
	private LLabel labelSample = null;
	private LToggleButton buttonSample = null;
	private JTextArea textAreaDescLayout = null;
	private LCheckBox checkBoxFilterFonts = null;
	
	public static final LFont FONT_DEFAULT = new LFont(AppGUI.getFont());
	
	private static Font[] fonts = null;

	public FontChooser() {
		super();
		initialize();
	}
	public FontChooser(Frame frame) {
		super(frame);
		initialize();
	}
	
	public static final Font[] getFonts() {
		
		if (fonts == null) {
			/*Task<Font[], Void> taskFonts = new Task<Font[], Void>() {
	
				@Override
				protected Font[] doInBackground() throws Exception {
					for (int i = 0; i < 50; i++) {
						Thread.sleep(50);
						setMessage(TEXTS.obtainingFonts+i);
					}
					setMessage(TEXTS.obtainingFonts);
					Thread.sleep(1000);
					Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
					return allFonts;
				}
			};
			
			fonts = Tasks.executeTaskAndWait(taskFonts, AplicacionGUI.getCurrentAppGUI().getFrame());*/
			return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		}
		return fonts;
	}
	
	private void initialize() {
		
		//setTitle("Select font");
		setLayout(new BorderLayout());
		setSize(800, 600);
		add(getScrollPane(), BorderLayout.CENTER);
		add(getCheckBoxFilterFonts(), BorderLayout.SOUTH);
		add(getPanelOptions(), BorderLayout.EAST);

		CellRendererFonts<String> render = new CellRendererFonts<String>();
		CellRendererFonts<Font> renderTip = new CellRendererFonts<Font>();
		
		fillCombos();
		addFonts();
		
		getlblCmbSize().getCombo().setEditable(true);
				
		getlblCmbStyle().getCombo().setRenderer(render);
		getlblCmbSize().getCombo().setRenderer(render);
		getLblCmbLayoutMode().getCombo().setRenderer(render);
		getListFonts().setCellRenderer(renderTip);
		
		ItemListener itemListener = new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateSampleLabels(getSelectedFont());
			}
		};
		
		getlblCmbSize().getCombo().addItemListener(itemListener);
		getlblCmbStyle().getCombo().addItemListener(itemListener);
		getLblCmbLayoutMode().getCombo().addItemListener(itemListener);
		
		getListFonts().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateSampleLabels(getSelectedFont());
			}
		});
		
		setSelectedFont(FONT_DEFAULT);
		
		ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT_CANCEL);
		buttonsPanel.setAutoCloseOnAccept(true);
		setButtonsPanel(buttonsPanel);
	}

	private List<Font> getFilteredFonts() {
		
		List<Font> filteredFonts = Lists.newList();
		Font[] fonts = getFonts();
		for (int i = 0; i < fonts.length; i++) {

			Font originalFont = fonts[i];
			if (!getCheckBoxFilterFonts().isSelected() || !filterFont(originalFont)) {
				String name = originalFont.getName();
				int style = getlblCmbStyle().getCombo().getSelectedIndex();
				int size = 12;
				try {
					size = Integer.parseInt(getlblCmbSize().getCombo().getSelectedItem().toString());
				} catch (Throwable ex) {
				}
				Font font = new Font(name, style, size);
				filteredFonts.add(font);
			}
		}
		
		return filteredFonts;
	}
	
	private void addFonts() {
	
		Task<List<Font>, Void> taskFilteredFonts = new Task<List<Font>, Void>() {

			@Override
			protected List<Font> doInBackground() throws Exception {
				setMessage(FontChooser.TEXTS.obtainingFonts);
				return getFilteredFonts();
			}
		};
		
		List<Font> filteredFonts = Tasks.executeTaskAndWait(taskFilteredFonts, this);
		//List<Font> filteredFonts = getFilteredFonts();
		getListFonts().setElements(filteredFonts);
		revalidate();
		repaint();
	}
	
	private boolean filterFont(Font font) {
	
		if (font == null || font.getName() == null)
			return true;
			
		String fontName = font.getName().toLowerCase();
		
		if (fontName.indexOf("bold") != -1 || 
			fontName.indexOf("italic") != -1 ||
			fontName.indexOf("negrita") != -1 ||
			fontName.indexOf("cursiva") != -1 ||
			fontName.indexOf("italic") != -1 ||
			fontName.indexOf("kursiv") != -1 ||
			fontName.indexOf("halvfed") != -1 ||
			fontName.equals("arial terminal one") ||
			fontName.equals("arial terminal two") ||
			fontName.indexOf("bookshelf symbol") != -1 ||
			fontName.equals("cambria math") ||
			fontName.indexOf("extra!") != -1 ||
			fontName.equals("estrangelo edessa") ||
			fontName.equals("gautami") ||
			fontName.equals("latha") ||
			fontName.equals("lucida attm special") ||
			fontName.indexOf("lucida console ") != -1 ||
			fontName.equals("ms outlook") ||
			fontName.equals("ms reference specialty") ||
			fontName.equals("mv boli") ||
			fontName.equals("mangal") ||
			fontName.equals("marlett") ||
			fontName.equals("raavi") ||
			fontName.equals("shruti") ||
			fontName.equals("symbol") ||
			fontName.equals("tunga") ||
			fontName.equals("webdings") ||
			fontName.indexOf("wingdings") != -1) {
				
				return true;
		}
		
		return false;
	}
	
	public LFont getSelectedFont() {
	
		try {
			if (getListFonts().getSelectedValue() != null) {
				
				String name = getListFonts().getSelectedValue().getName();
				int style = getlblCmbStyle().getCombo().getSelectedIndex();
				int size = 12;
				try {
					size = Integer.parseInt(getlblCmbSize().getCombo().getSelectedItem().toString());
				} catch (Throwable ex) {
				}
				int layoutMode = getLblCmbLayoutMode().getCombo().getSelectedIndex();
				
				return new LFont(name, style, size, layoutMode);
			}
			else
				return GeneralUIProperties.getInstance().getFontApp();
		}
		catch (Throwable e){
			return GeneralUIProperties.getInstance().getFontApp();
		}
	}
	
	private LabelCombo<String> getlblCmbStyle() {
		if (lblCmbStyle == null) {
			lblCmbStyle = new LabelCombo<String>();
			lblCmbStyle.setOrientation(LabelComponent.VERTICAL);
			lblCmbStyle.setTextLabel(TEXTS.style);
			lblCmbStyle.setAutoSizeLabel(true);
		}
		return lblCmbStyle;
	}
	
	private LabelCombo<String> getlblCmbSize() {
		if (lblCmbSize == null) {
			lblCmbSize = new LabelCombo<String>();
			lblCmbSize.setOrientation(LabelComponent.VERTICAL);
			lblCmbSize.setTextLabel(TEXTS.size);
			lblCmbSize.setAutoSizeLabel(true);
		}
		return lblCmbSize;
	}

	private LabelCombo<String> getLblCmbLayoutMode() {
		if (lblCmbLayoutMode == null) {
			lblCmbLayoutMode = new LabelCombo<String>();
			lblCmbLayoutMode.setOrientation(LabelComponent.VERTICAL);
			lblCmbLayoutMode.setTextLabel(TEXTS.layout);
			lblCmbLayoutMode.setAutoSizeLabel(true);
		}
		return lblCmbLayoutMode;
	}

	
	private LList<Font> getListFonts() {
		if (listFonts == null) {
			listFonts = new LList<Font>();	
		}
		return listFonts;
	}
	
	private LPanel getPanelOptions() {
		if (panelOptions == null) {
			panelOptions = new LPanel(new GridBagLayout());
			panelOptions.setPreferredSize(new Dimension(410, 600));
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.insets.left = 10;
			panelOptions.add(getlblCmbStyle(), gbc);
			
			gbc.gridx = 2;
			gbc.insets.left = 5;
			panelOptions.add(getlblCmbSize(), gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.insets.left = 10;
			panelOptions.add(getLblCmbLayoutMode(), gbc);
			
			gbc.gridy = 3;
			gbc.insets.top = 5;
			panelOptions.add(getTextAreaDescLayout(), gbc);
			
			gbc.gridy = 4;
			gbc.insets.top = 5;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.CENTER;
			panelOptions.add(getTypographyLabel(), gbc);
			
			gbc.gridy = 6;
			gbc.insets.top = 20;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			panelOptions.add(getLabelSample(), gbc);
			
			gbc.gridy = 7;
			panelOptions.add(getButtonSample(), gbc);
			
			gbc.gridy = 8;
			gbc.weighty = 1.0;
			gbc.insets.top = 0;
			gbc.fill = GridBagConstraints.BOTH;
			panelOptions.add(new JPanel(), gbc);
		}
		return panelOptions;
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getListFonts());
		}
		return scrollPane;
	}
	
	private TypographyLabel getTypographyLabel() {
		if (typographyLabel == null) {
			typographyLabel = new TypographyLabel() {
				@Override
				public Dimension getPreferredSize() {
					Dimension prefSize = super.getPreferredSize();
					prefSize.width = 400;
					return prefSize;
				}
			};
			typographyLabel.setFontSize(50);
		}
		return typographyLabel;
	}
	
	private LLabel getLabelSample() {
		if (labelSample == null) {
			labelSample = new LLabel(TEXTS.sampleLabel) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					boolean paintTextLinesBounds = true;
					boolean paintGlobalTextRect = false;
					boolean paintViewRect = false;
					GraphicsUtils.paintLabelLayouts(g, this, paintTextLinesBounds, paintGlobalTextRect, paintViewRect);
				}
			};
		}
		labelSample.setMargin(new Insets(0,0,0,0));
		labelSample.setHorizontalAlignment(SwingConstants.CENTER);
		labelSample.setOpaque(true);
		labelSample.setBackground(ColorsGUI.getColorPanelsBrightest());
		return labelSample;
	}
	
	private LToggleButton getButtonSample() {
		if (buttonSample == null) {
			buttonSample = new LToggleButton(TEXTS.sampleButton);
		}
		return buttonSample;
	}
	
	private JTextArea getTextAreaDescLayout() {
		if (textAreaDescLayout == null) {
			textAreaDescLayout = new JTextArea();
			textAreaDescLayout.setLineWrap(true);
			textAreaDescLayout.setWrapStyleWord(true);
			textAreaDescLayout.setOpaque(false);
			textAreaDescLayout.setFont(UtilsGUI.getFontWithSizeFactor(textAreaDescLayout.getFont(), 0.8f));
			textAreaDescLayout.setPreferredSize(new Dimension(400, 100));
			ToolTip.getInstance().registerComponent(textAreaDescLayout);
		}
		return textAreaDescLayout;
	}
	
	private LCheckBox getCheckBoxFilterFonts() {
		if (checkBoxFilterFonts == null) {
			checkBoxFilterFonts = new LCheckBox(TEXTS.hideStyledFonts);
			checkBoxFilterFonts.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					final Font selectedFont = getSelectedFont();
					getListFonts().setElements(getFilteredFonts());
					getListFonts().updateUI();
					setSelectedFont(selectedFont);
				}
			});
		}
		return checkBoxFilterFonts;
	}
	private void fillCombos() {
	
		for (int i = 9; i < 17; i++) {
	
			getlblCmbSize().addItem(String.valueOf(i));
		}
	
		getlblCmbStyle().addItem(TEXTS.plain);
		getlblCmbStyle().addItem(TEXTS.bold);
		getlblCmbStyle().addItem(TEXTS.italic);
		getlblCmbStyle().addItem(TEXTS.boldItalic);
		
		getLblCmbLayoutMode().addItem(TEXTS.heightMax);
		getLblCmbLayoutMode().addItem(TEXTS.heightMost);
		getLblCmbLayoutMode().addItem(TEXTS.heightCommon);
		getLblCmbLayoutMode().addItem(TEXTS.heightExact);
		
		getlblCmbSize().getCombo().setSelectedIndex(5);
		getlblCmbStyle().getCombo().setSelectedIndex(0);
		getLblCmbLayoutMode().getCombo().setSelectedIndex(0);
	}
	
	private void selectRow(int rowIndex) {
	
		Vector<Font> elements = getListFonts().getElements();
		if (rowIndex >= 0 && rowIndex < elements.size()) {
			
			getScrollPane().validate();
			Rectangle cellBounds = getListFonts().getCellBounds(rowIndex, rowIndex);
			int centeredMargin = getScrollPane().getHeight()/2;
			if (cellBounds.height > getListFonts().getHeight()) {
	
				if (getScrollPane().getHeight() > 0)
					centeredMargin = 0;
				else
					centeredMargin = 40;//La primera vez que entramos en el dialogo no estan bien definidos los tamaños
			}
			int y = cellBounds.y - centeredMargin - getSelectedFont().getSize() / 2;
			if (y < 0)
				y = 0;
			
			getScrollPane().getViewport().setViewPosition(new Point(0, y));
			getListFonts().setSelectedIndex(rowIndex);
		}
	}
	
	public void setSelectedFont(Font selectedFont) {
	
		if (selectedFont != null) {
			
			Vector<Font> elements = getListFonts().getElements();
			int rowIndex = -1;
			for (int i = 0; i < elements.size() && rowIndex == -1; i++) {
				Font font = elements.elementAt(i);
				if (font.getName().equalsIgnoreCase(selectedFont.getName())) {
					rowIndex = i;
				}
			}
			
			if (rowIndex != -1) {
				getlblCmbStyle().getCombo().setSelectedIndex(selectedFont.getStyle());
				getlblCmbSize().getCombo().setSelectedItem(Integer.toString(selectedFont.getSize()));
				if (selectedFont instanceof LFont) {
					LFont selectedLFont = (LFont) selectedFont;
					getLblCmbLayoutMode().getCombo().setSelectedIndex(selectedLFont.getLayoutMode());
				}
				
				selectRow(rowIndex);
				
				updateSampleLabels(selectedFont);
			}
			else if (selectedFont.getName().equals(FONT_DEFAULT.getName())) {
				Font auxSelectedFont = new Font(getListFonts().getElements().firstElement().getName(), selectedFont.getStyle(), selectedFont.getSize());
				setSelectedFont(auxSelectedFont);
			}
			else {
				Font auxSelectedFont = new Font(FONT_DEFAULT.getName(), selectedFont.getStyle(), selectedFont.getSize());
				setSelectedFont(auxSelectedFont);
			}
		}
	}
	
	private void updateSampleLabels(Font selectedFont) {
		getLabelSample().setFont(selectedFont);
		getButtonSample().setFont(selectedFont);
		getTypographyLabel().setFont(UtilsGUI.getFontWithSize(selectedFont, getTypographyLabel().getFontSize()));
		int layoutMode = LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
		if (selectedFont instanceof LFont) {
			layoutMode = ((LFont) selectedFont).getLayoutMode();
		}
		getTextAreaDescLayout().setText(getDescFontLayout(layoutMode));
		
		getDialog().setTitle(selectedFont.getName() + "  -  " + getlblCmbStyle().getCombo().getSelectedItem() + "  -  " + selectedFont.getSize());
	}
	
	public static String getDescFontLayout(int layoutMode) {
		String desc;
		if (layoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST)
			desc = TEXTS.descHeightMost;
		else if (layoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON)
			desc = TEXTS.descHeightCommon;
		else if (layoutMode == LFont.LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT)
			desc = TEXTS.descHeightExact;
		else
			desc = TEXTS.descHeightMax;
		
		return desc;		
	}
}
