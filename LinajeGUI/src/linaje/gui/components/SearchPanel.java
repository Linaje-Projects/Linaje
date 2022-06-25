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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import linaje.LocalizedStrings;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LButtonProperties;
import linaje.gui.LComponentBorder;
import linaje.gui.LList;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.cells.LabelCell;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.windows.LWindow;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class SearchPanel<E> extends LPanel {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String tipSearch;
		public String tipClean;
		public String textBackground;
		public String textNotFound;
				
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	private LTextField textField = null;
	private LButton button = null;
	
	//Opcional: Solo se usa en caso de que especifiquemos elementos de búsqueda
	private LList<E> listSearchResults = null;
	private JScrollPane scrollPane = null;
	private LWindow windowList = null;
	private List<E> searchElements = null;
	//
	
	private Timer timerSearchChange = null;
	private int showDelay = 500;
	
	protected transient ChangeEvent changeEvent = null;
		
	private static Icon ICON_SEARCH = Icons.SEARCH;
	private static Icon ICON_CLEAN = Icons.getIconX(10, 2, ColorsGUI.getColorText());
	
	public static final String TIP_SEARCH = TEXTS.tipSearch;
	public static final String TIP_CLEAN = TEXTS.tipClean;
	
	public SearchPanel() {
		initialize();
	}

	public static boolean meetsFilter(String searchText, Object searchElement) {
		
		//Comprobamos si el nodo cumple el filtro de búsqueda
		if (searchElement != null && searchText != null && !searchText.trim().equals(Constants.VOID)) {
			
			String[] searchTexts = searchText.split(Constants.SPACE);
			for (int i = 0; i < searchTexts.length; i++) {
				String text = searchTexts[i];
				String searchElementText = searchElement.toString();
				int indexText = searchElementText.toLowerCase().indexOf(text.toLowerCase());
				//En cuanto no cumpla uno de los textos de búsqueda devolvemos false
				if (indexText == -1) {
					return false;
				}
			}
		}
		
		return true;
	}

	public LTextField getTextField() {
		
		if (textField == null) {
			
			textField = new LTextField();
			textField.setOpaque(false);
			textField.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
			textField.setTextBackgroundVoid(TEXTS.textBackground);
			textField.setFontStyle(Font.BOLD);
			textField.setFontSize(textField.getFontSize()+2);
			textField.setFontTextBackgroundVoid(GeneralUIProperties.getInstance().getFontApp());
			Dimension size =  new Dimension(250, textField.getPreferredSize().height);
			textField.setPreferredSize(size);
			textField.setSelectTextWithFocus(false);
			textField.addKeyListener(new KeyListener() {
				
				public void keyReleased(KeyEvent e) {
					getTimerSearchChange().restart();
				}
				public void keyTyped(KeyEvent e) {}
				public void keyPressed(KeyEvent e) {}
			});
			textField.addMouseListener(new MouseListener() {
				
				public void mouseReleased(MouseEvent e) {
					if (getWindowList().isVisibleWindow()) {
						getTimerSearchChange().stop();
						getWindowList().closeWindow();
					}
					else {
						changeSearch();
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
			});
		}
		return textField;
	}
	
	private LButton getButton() {
		if (button == null) {
			button = new LButton();
			button.setText(Constants.VOID);
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setOpaque(false);
			button.getButtonProperties().setShadowTextMode(LButtonProperties.SHADOW_TEXT_MODE_NEVER);
			Dimension size =  new Dimension(getTextField().getPreferredSize().height+6, getTextField().getPreferredSize().height+4);
			button.setPreferredSize(size);
			button.setMinimumSize(size);
			button.setMaximumSize(size);
			
			button.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					clean();
				}
			});
		}
		return button;
	}

	private void initialize() {
		
		setName("SearchPanel");
		setOpaque(true);
		setBackground(ColorsGUI.getColorPanelsBrightest());
		setBorder(new LComponentBorder());
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(getButton(), gbc);
		
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(getTextField(), gbc);
		
		updateButton();
	}
	
	public void changeSearch() {
		
		if (getListSearchResults() != null && getListSearchResults().getCellRenderer() instanceof LabelCell) {
			LabelCell render = (LabelCell) getListSearchResults().getCellRenderer();
			render.setTextSearch(getSearchText());
		}
		
		updateButton();
		fireStateChanged();
		fillListSearchResults();
	}
	
	private void updateButton() {
		
		if (getTextField().getText().equals(Constants.VOID)) {
			getButton().setIcon(ICON_SEARCH);
			getButton().setToolTipText(TIP_SEARCH);
		}
		else {
			getButton().setIcon(ICON_CLEAN);
			getButton().setToolTipText(TIP_CLEAN);
		}
		
		//fireStateChanged();
	}
	
	public String getSearchText() {
		return getTextField().getText();
	}

	public void setTextBackgroundVoid(String textBackgroundVoid) {
		getTextField().setTextBackgroundVoid(textBackgroundVoid);
	}
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireStateChanged() {

		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}
	
	public void clean() {
		getTextField().setText(Constants.VOID);
		changeSearch();
	}
	

	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setOpaque(false);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			scrollPane.getViewport().setOpaque(false);
			scrollPane.setBackground(Color.white);
		}
		return scrollPane;
	}

	private LWindow getWindowList() {
		if (windowList == null) {
			windowList = new LWindow(this, (Frame) null);
			windowList.setForeground(ColorsGUI.getColorText());
			windowList.setBackground(ColorsGUI.getColorPanelsBrightest());
			windowList.setLayout(new BorderLayout());
			windowList.add(getScrollPane(), BorderLayout.CENTER);
			windowList.setBorderColor(ColorsGUI.getColorBorder());
		}
		return windowList;
	}

	public LList<E> getListSearchResults() {
		return listSearchResults;
	}
	public List<E> getSearchElements() {
		return searchElements;
	}
	public void setSearchElements(List<E> searchElements) {
		this.searchElements = searchElements;
		
		if (searchElements != null && !searchElements.isEmpty() && getListSearchResults() == null) {
			setSearchBaseList(new LList<E>());
		}
	}
	
	public void setSearchBaseList(JList<E> baseList) {
		
		setListSearchResults(baseList != null ? new LList<E>(baseList) : null);
	}
	
	public void setListSearchResults(LList<E> listSearchResults) {
		
		this.listSearchResults = listSearchResults;
		
		getScrollPane().setViewportView(listSearchResults);
		
		if (listSearchResults != null) {
			listSearchResults.setOpaque(false);
			listSearchResults.setBorder(BorderFactory.createEmptyBorder());
			int numColumnas = listSearchResults.getNumberOfColumns();
			int horizScrollPolicy = numColumnas > 1 ? JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
			int vertScrollPolicy = numColumnas > 1 ? JScrollPane.VERTICAL_SCROLLBAR_NEVER : JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
			getScrollPane().setHorizontalScrollBarPolicy(horizScrollPolicy);
			getScrollPane().setVerticalScrollBarPolicy(vertScrollPolicy);
		}
	}
	
	private void fillListSearchResults() {
		
		if (getSearchElements() != null && getListSearchResults() != null) {
			
			String searchText = getTextField().getText();
			
			if (!searchText.equals(Constants.VOID)) {
				
				List<E> elementsFound = Lists.newList();
				for (int i = 0; i < getSearchElements().size(); i++) {
					try {
						E element = getSearchElements().get(i);
						if (meetsFilter(searchText, element))
							elementsFound.add(element);
					}
					catch (Throwable ex) {
					}				
				}
				
				getListSearchResults().setElements(elementsFound);
				String textNotFound = elementsFound.isEmpty() ? TEXTS.textNotFound : null;
				getWindowList().setTextBackground(textNotFound);
				getWindowList().setSize(new Dimension(getWidth(), 100));
				
				Point location = getLocationOnScreen();
				location.y = location.y + getHeight();
				
				getWindowList().showWindow(location);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							//Thread.sleep(100);
						} catch (Exception e) {}
						getTextField().requestFocus();
					}
				});
				
			}
			else {
				
				getWindowList().closeWindow();
			}
		}
	}
	
	
	public int getShowDelay() {
		return showDelay;
	}
	public void setShowDelay(int retrasoMostrar) {
		this.showDelay = retrasoMostrar;
	}

	private Timer getTimerSearchChange() {

		if (timerSearchChange == null) {
			
			ActionListener actionListener = new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					try {
						changeSearch();
					}
					catch (Throwable ex) {
						Console.printException(ex);
					}
					getTimerSearchChange().stop();
				}
			};
			timerSearchChange = new Timer(showDelay, actionListener);
		}
		return timerSearchChange;
	}
}
