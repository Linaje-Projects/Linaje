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
package linaje.gui.windows;

/**
 * <b>Funcionalidad:</b><br>
 * Homogeneizar el aspecto y funcionamiento de los diálogos
 * <p>
 * <b>Uso:</b><br>
 * Iniciar la clase con una de las opciones por defecto BOTON_ACEPTAR_CANCELAR, BOTON_SI_NO, ...
 * o bien añadir botones despues de crear la instancia
 *
 * Los botones de una mísma posición (izqierda, derecha o centro) se dimensionarán con el
 * tamaño del botón de su misma posición que tenga el texto mas largo
 * <p>
 * 
 * @author Pablo Linaje
 * @version 1.0
 * 
 * @see clase relacionada 1 
 * @see clase relacionada n 
 * 
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import linaje.LocalizedStrings;
import linaje.gui.LButton;
import linaje.gui.LPanel;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;
import linaje.utils.Lists;

@SuppressWarnings("serial")
public class ButtonsPanel extends LPanel {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String accept;
		public String cancel;
		public String yes;
		public String no;
		public String next;
		public String previous;
		public String finish;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	//Constantes de inicialización del panel
	public static final int ASPECT_ACCEPT = 0;
	public static final int ASPECT_ACCEPT_CANCEL = 1;
	public static final int ASPECT_YES_NO = 2;
	public static final int ASPECT_YES_NO_CANCEL = 3;
	public static final int ASPECT_VOID = 4;
	
	//Constantes para inicializar los nombres de los botones predefinidos
	//Tambien servirán para referenciarlos con el método "getButton(String)"
	public static final String BUTTON_ACCEPT = TEXTS.accept;
	public static final String BUTTON_CANCEL = TEXTS.cancel;
	public static final String BUTTON_YES = TEXTS.yes;
	public static final String BUTTON_NO = TEXTS.no;
	public static final String BUTTON_PREVIOUS = TEXTS.previous;
	public static final String BUTTON_NEXT = TEXTS.next;
	public static final String BUTTON_FINISH = TEXTS.finish;
	//Constants de inicialización de botones
	public static final int POSITION_LEFT = SwingConstants.LEFT;
	public static final int POSITION_CENTER = SwingConstants.CENTER;
	public static final int POSITION_RIGHT = SwingConstants.RIGHT;
	//Constants de indicación de respuesta (Botón que hemos pulsado)
	public static final int RESPONSE_CANCEL = 0;
	public static final int RESPONSE_ACCEPT_YES = 1;
	public static final int RESPONSE_NO = 2;
	
	private boolean createButtonsWithLineBackground = false;
	
	private int minWidth = 100;
	
	private int response = 0;
	private int aspect = -1;
	private int margin = 0;
	
	private boolean autoCloseOnCancel = true;
	private boolean autoCloseOnAccept = false;
	private boolean autoSizeButtonsLeft = true;
	private boolean autoSizeButtonsRight = true;
	private boolean autoSizeButtonsCenter = true;
	
	private LDialogContent dialogContent = null;
	private List<LButton> buttons = null;
	
	private LPanel panelNorthSeparator = null;
	
	private JPanel panelMain = null;
	private JPanel panelNorth = null;
	private JPanel panelCenter = null;
	private JPanel panelEast = null;
	private JPanel panelWest = null;
	
	private JPanel panelMarginRight = null;
	private JPanel panelMarginLeft = null;
	private JPanel panelMarginBottom = null;
	
	private JPanel panelButtonsZone = null;
	private JPanel panelDatailZone = null;
	
	private JPanel panelMarginDetailRight = null;
	private JPanel panelMarginDetailLeft = null;
	
	private FlowLayout panelCenterFlowLayout = null;
	private FlowLayout panelEastFlowLayout = null;
	private FlowLayout panelWestFlowLayout = null;
	
	private String focusButton = null;
	
	private JComponent detailComponent = null;
	
	protected ActionListener actionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			processActionPerformed((LButton) e.getSource());
		}
	};

	public ButtonsPanel() {
		super();
		initialize();
	}
	
	public ButtonsPanel(int aspect) {
		super();
		initialize();
		setAspect(aspect);
	}
	
	protected void processActionPerformed(LButton button) {
			
		String command = button.getActionCommand();
			
		if (command.equals(BUTTON_ACCEPT) || command.equals(BUTTON_YES) || command.equals(BUTTON_FINISH)) {
			setResponse(RESPONSE_ACCEPT_YES);
			if (isAutoCloseOnAccept())
				getDialogContent().dispose();
		}
		else if (command.equals(BUTTON_CANCEL)) {
			setResponse(RESPONSE_CANCEL);
			if (isAutoCloseOnCancel())
				getDialogContent().dispose();
		}
		else if (command.equals(BUTTON_NO)) {
			setResponse(RESPONSE_NO);
			if (isAutoCloseOnCancel())
				getDialogContent().dispose();
		}
	}
	
	public LButton addButton(String buttonText, int position) {
		
		LButton button = new LButton(buttonText);
		
		if (isCreateButtonsWithLineBackground()) {
			button.getButtonProperties().setLineBackgroundColor(null);
		}
		
		int width = button.getPreferredSize().width;
	
		return addButton(buttonText, position, width);
	}
	
	public LButton addButton(String buttonText, int position, int buttonWidth) {
		
		//Obtenemos el panel al que añadiremos el botón
		JPanel panel = null;
		boolean autoSizeButtons;
		
		if (position == POSITION_LEFT) {
			panel = getPanelWest();
			autoSizeButtons = isAutoSizeButtonsLeft();
		}
		else if (position == POSITION_RIGHT) {
			panel = getPanelEast();
			autoSizeButtons = isAutoSizeButtonsRight();
		}
		else {
			panel = getPanelCenter();
			autoSizeButtons = isAutoSizeButtonsCenter();
		}
	
		if (autoSizeButtons) {
			//Calculamos el ancho mayor de los botones que haya en el panel
			int maxButtonWidth = 0;
			if (panel.getComponentCount() > 0) {
				int width = 0;
				for (int i = 0; i < panel.getComponentCount(); i++) {
					if (panel.getComponent(i) instanceof LButton) {
						LButton button = (LButton) panel.getComponent(i);
						width = button.getPreferredSize().width;
						if (width > maxButtonWidth)
							maxButtonWidth = width;
					}
				}	
			}
	
			//Asignamos a todos los botones el ancho mayor
			if (maxButtonWidth > buttonWidth) {
				//Asignamos al nuevo botón el ancho del boton mayor de la zona
				buttonWidth = maxButtonWidth;
			}
			else {
				//Asignamos a todos los botones de la mísma zona el ancho del nuevo botón
				if (panel.getComponentCount() > 0) {
					for (int i = 0; i < panel.getComponentCount(); i++) {
						if (panel.getComponent(i) instanceof LButton) {
							LButton button = (LButton) panel.getComponent(i);
							button.setPreferredSize(new Dimension(buttonWidth, button.getPreferredSize().height));
						}
					}	
				}
			}
		}
		//Añadimos el botón
		LButton button = new LButton(buttonText);
		if (isCreateButtonsWithLineBackground()) {
			
			Color lineBackground = null;
			if (buttonText.equalsIgnoreCase(BUTTON_ACCEPT)
			 || buttonText.equalsIgnoreCase(BUTTON_YES)) {
				lineBackground = ColorsGUI.getColorPositive();
			}
			else if (buttonText.equalsIgnoreCase(BUTTON_NO)) {
				lineBackground = ColorsGUI.getColorNegative();
			}
			else if (buttonText.equalsIgnoreCase(BUTTON_CANCEL)) {
				lineBackground = ColorsGUI.getColorBorder();
			}
			else {
				lineBackground = ColorsGUI.getColorBorderBright();
			}
			button.getButtonProperties().setLineBackgroundColor(lineBackground);
		}
		panel.add(button);
	
		getButtons().add(button);
	
		//Redimensionamos los paneles
		resizePanels();
		
		return button;
	}
	
	/**
	 * <b>Descripción:</b><br>
	 * Para agregar algún componente que no sea un Botón llamaremos a este método
	 * 
	 * @param component JComponent
	 * @param position int
	 */
	public void addJComponent(JComponent component, int position) {
	
		//Si el componente no tiene PreferredSize se lo asignamos a partir de su tamaño
		if (component.getPreferredSize().width == 0)
			component.setPreferredSize(new Dimension(component.getWidth(), component.getPreferredSize().height));
	
		if (component.getPreferredSize().height == 0)
			component.setPreferredSize(new Dimension(component.getPreferredSize().width, component.getHeight()));
	
		
		//Obtenemos el panel al que añadiremos el botón
		JPanel panel = null;
		if (position == POSITION_LEFT)
			panel = getPanelWest();
			
		else if (position == POSITION_RIGHT)
			panel = getPanelEast();
			
		else
			panel = getPanelCenter();
		
		//Añadimos el componente
		panel.add(component);
	
		//Redimensionamos los paneles
		resizePanels();
	}
	
	public void addSeparator(int posicion) {
		
		//Obtenemos el panel al que añadiremos el panelNorthSeparator
		JPanel panel = null;
		if (posicion == POSITION_LEFT) {
			panel = getPanelWest();
		}
		else if (posicion == POSITION_RIGHT) {
			panel = getPanelEast();
		}
		else {
			panel = getPanelCenter();
		}
	
		//Añadimos el botón
		JPanel separator = new JPanel();
		int height = panel.getPreferredSize().height - 10;
		if (height < 5)
			height = 5;
		separator.setPreferredSize(new Dimension(10, height));
		separator.setOpaque(false);
		panel.add(separator);
		
		//Redimensionamos los paneles
		resizePanels();
	}
	
	public void removeButton(String buttonText) {
		
		LButton button = getButton(buttonText);
	
		if (button != null) {
	
			button.getParent().remove(button);
			
			getButtons().remove(button);
	
			//Redimensionamos los paneles
			resizePanels();
		}
	}
	
	public void removeJComponent(JComponent componente) {
	
		//Obtenemos el panel al que añadiremos el botón
		Container padre = componente.getParent();
		if (padre == getPanelWest() || padre == getPanelEast() || padre == getPanelCenter()) {
			
			padre.remove(componente);	
			
			//Redimensionamos los paneles
			resizePanels();
		}
	}
	private void assingMargin() {
	
		int marginSides = getMarginSides();
			
		getPanelMarginLeft().setPreferredSize(new Dimension(marginSides, 0));
		getPanelMarginRight().setPreferredSize(new Dimension(marginSides, 0));
		//getPanelMarginTop().setPreferredSize(new Dimension(0, getMargin()));
		
		getPanelNorth().setBorder(BorderFactory.createEmptyBorder(getMargin(), getMargin(), 0, getMargin()));
		
		setPreferredSize(getAspect() == ASPECT_VOID ? new Dimension(getPreferredSize().width, getMargin()) : null);
	}
	
	public void destroy() {
	
		try {
	
			removeListeners();
			
			dialogContent = null;
			buttons = null;
			panelNorthSeparator = null;
			panelMain = null;
			panelMarginRight = null;
			panelMarginLeft = null;
			panelMarginBottom = null;
			panelNorth = null;
			detailComponent = null;
			panelCenter = null;
			panelCenterFlowLayout = null;
			panelEast = null;
			panelEastFlowLayout = null;
			panelWest = null;
			panelWestFlowLayout = null;
			panelButtonsZone = null;
			panelMarginDetailRight = null;
			panelMarginDetailLeft = null;
			panelDatailZone = null;
			focusButton = null;
			
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public int getMinWidth() {
		return minWidth;
	}
	public int getAspect() {
		return aspect;
	}
	
	public boolean isAutoCloseOnAccept() {
		return autoCloseOnAccept;
	}
	public boolean isAutoCloseOnCancel() {
		return autoCloseOnCancel;
	}
	public boolean isAutoSizeButtonsCenter() {
		return autoSizeButtonsCenter;
	}
	public boolean isAutoSizeButtonsRight() {
		return autoSizeButtonsRight;
	}
	public boolean isAutoSizeButtonsLeft() {
		return autoSizeButtonsLeft;
	}
	
	public LButton getButton(String buttonText) {
	
		LButton button = null;
	
		if (buttonText != null) {
			
			for (int i = 0; i < getButtons().size(); i++) {
				button = getButtons().get(i);
				if (button.getText().equalsIgnoreCase(buttonText)) {
					break;
				} else {
					button = null;
				}
			}
	
			if (button == null) {
	
				//Si no encontramos el botón es posible que se haya cambiado su descripción
				//por lo que buscamos en su actionCommand
				for (int i = 0; i < getButtons().size(); i++) {
					button = getButtons().get(i);
					if (button.getActionCommand().equalsIgnoreCase(buttonText)) {
						break;
					} else {
						button = null;
					}
				}
			}
		}
		
		return button;
	}
	
	public String getFocusButton() {
		return focusButton;
	}
	
	public JComponent getDetailComponent() {
		return detailComponent;
	}
	
	public int getMargin() {	
		return margin;
	}
	
	private int getMarginSides() {
		
		int margenLateral = getMargin() - getPanelEastFlowLayout().getHgap();
		if (margenLateral < 0)
			margenLateral = 0;
	
		return margenLateral;
	}
	
	public LDialogContent getDialogContent() {
		return dialogContent;
	}
	
	private FlowLayout getPanelCenterFlowLayout() {
		if (panelCenterFlowLayout == null)
			panelCenterFlowLayout = new FlowLayout(FlowLayout.CENTER);
		return panelCenterFlowLayout;
	}
	private FlowLayout getPanelEastFlowLayout() {
		if (panelEastFlowLayout == null)
			panelEastFlowLayout = new FlowLayout(FlowLayout.RIGHT);
		return panelEastFlowLayout;
	}
	private FlowLayout getPanelWestFlowLayout() {
		if (panelWestFlowLayout == null)
			panelWestFlowLayout = new FlowLayout(FlowLayout.LEFT);
		return panelWestFlowLayout;
	}
	
	private JPanel getPanelCenter() {
		if (panelCenter == null) {
			panelCenter = new JPanel(getPanelCenterFlowLayout());
			panelCenter.setOpaque(false);
		}
		return panelCenter;
	}
	
	private JPanel getPanelEast() {
		if (panelEast == null) {
			panelEast = new JPanel(getPanelEastFlowLayout());
			panelEast.setOpaque(false);
		}
		return panelEast;
	}
	
	private JPanel getPanelWest() {
		if (panelWest == null) {
			panelWest = new JPanel(getPanelWestFlowLayout());
			panelWest.setOpaque(false);
		}
		return panelWest;
	}
	
	private JPanel getPanelMarginRight() {
		if (panelMarginRight == null) {
			panelMarginRight = new JPanel();
			panelMarginRight.setPreferredSize(new Dimension(10, 0));
			panelMarginRight.setOpaque(false);
		}
		return panelMarginRight;
	}
	
	private JPanel getPanelMarginDetailRight() {
		if (panelMarginDetailRight == null) {
			panelMarginDetailRight = new JPanel();
			panelMarginDetailRight.setPreferredSize(new Dimension(5, 0));
			panelMarginDetailRight.setOpaque(false);
		}
		return panelMarginDetailRight;
	}
	
	private JPanel getPanelMarginDetailLeft() {
		if (panelMarginDetailLeft == null) {
			panelMarginDetailLeft = new JPanel();
			panelMarginDetailLeft.setPreferredSize(new Dimension(5, 0));
			panelMarginDetailLeft.setOpaque(false);
		}
		return panelMarginDetailLeft;
	}
	
	private JPanel getPanelMarginBottom() {
		if (panelMarginBottom == null) {
			panelMarginBottom = new JPanel();
			panelMarginBottom.setPreferredSize(new Dimension(0, 10));
			panelMarginBottom.setOpaque(false);
		}
		return panelMarginBottom;
	}
	
	private JPanel getPanelMarginLeft() {
		if (panelMarginLeft == null) {
			panelMarginLeft = new JPanel();
			panelMarginLeft.setPreferredSize(new Dimension(10, 0));
			panelMarginLeft.setOpaque(false);
		}
		return panelMarginLeft;
	}
	
	private JPanel getPanelNorth() {
		if (panelNorth == null) {
			panelNorth = new  JPanel(new BorderLayout());
			panelNorth.setOpaque(false);
			//panelNorth.add(getPanelMarginTop(), BorderLayout.NORTH);
			panelNorth.add(getPanelNorthSeparator(), BorderLayout.SOUTH);
			panelNorth.setBorder(BorderFactory.createEmptyBorder(getMargin(), getMargin(), 0, getMargin()));
		}
		return panelNorth;
	}
	
	private LPanel getPanelNorthSeparator() {
		if (panelNorthSeparator == null) {
			panelNorthSeparator = new LPanel();
			panelNorthSeparator.setBackground(GeneralUIProperties.getInstance().getColorBorderBright());
			panelNorthSeparator.setPreferredSize(new Dimension(0,1));
			panelNorthSeparator.setOpacity(0.1f);
		}
		return panelNorthSeparator;
	}
	
	private JPanel getPanelMain() {
		if (panelMain == null) {
			panelMain = new JPanel(new BorderLayout());
			panelMain.setOpaque(false);
			panelMain.add(getPanelButtonsZone(), BorderLayout.NORTH);
			panelMain.add(getPanelDatailZone(), BorderLayout.SOUTH);
		}
		return panelMain;
	}
	
	protected JPanel getPanelDatailZone() {
		if (panelDatailZone == null) {
			panelDatailZone = new JPanel(new BorderLayout());
			panelDatailZone.setOpaque(false);
			panelDatailZone.add(getPanelMarginDetailLeft(), BorderLayout.WEST);
			panelDatailZone.add(getPanelMarginDetailRight(), BorderLayout.EAST);
		}
		return panelDatailZone;
	}
	
	private JPanel getPanelButtonsZone() {
		if (panelButtonsZone == null) {
			panelButtonsZone = new JPanel(new BorderLayout());
			panelButtonsZone.setOpaque(false);
			//panelButtonsZone.setPreferredSize(new Dimension(0, 28));
			panelButtonsZone.add(getPanelWest(), BorderLayout.WEST);
			panelButtonsZone.add(getPanelEast(), BorderLayout.EAST);
			panelButtonsZone.add(getPanelCenter(), BorderLayout.CENTER);
		}
		return panelButtonsZone;
	}
	
	public int getResponse() {
		return response;
	}
	
	public List<LButton> getButtons() {
		if (buttons == null)
			buttons = Lists.newList();
		return buttons;
	}
	
	
	private void initialize() {
		
		setName("ButtonsPanel");
		setOpaque(false);
		setLayout(new BorderLayout());
		setSize(350, 50);
		setImageAlignment(SwingConstants.SOUTH_EAST);
		
		add(getPanelNorth(), BorderLayout.NORTH);
		add(getPanelMain(), BorderLayout.CENTER);
		
		add(getPanelMarginLeft(), BorderLayout.WEST);
		add(getPanelMarginBottom(), BorderLayout.SOUTH);
		add(getPanelMarginRight(), BorderLayout.EAST);
	}
	
	private void removeListeners() {
	
		try {
			for (int i = 0; i < getButtons().size(); i++) {
				LButton button = getButtons().get(i);
				button.removeActionListener(actionListener);
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	private void resizePanels() {
	
		assingMargin();
		
		int widthPanelEast = 0;
		int widthPanelWest = 0;
		int widthPanelCenter = 0;
		
		int heightPanelEast = 0;
		int heightPanelWest = 0;
		int heightPanelCenter = 0;
		
		int hGap = getButtonsHgap();
		int vGap = getButtonsVgap();
		
		if (getPanelEast().getComponentCount() > 0) {
			
			for (int i = 0; i < getPanelEast().getComponentCount(); i++) {
				Dimension cSize = getPanelEast().getComponent(i).getPreferredSize();
				widthPanelEast = widthPanelEast + cSize.width + hGap;
				heightPanelEast = Math.max(cSize.height, heightPanelEast);
			}
			widthPanelEast += hGap;
			heightPanelEast += vGap;
		}
		
		if (getPanelWest().getComponentCount() > 0) {
			
			for (int i = 0; i < getPanelWest().getComponentCount(); i++) {
				Dimension cSize = getPanelWest().getComponent(i).getPreferredSize();
				widthPanelWest = widthPanelWest + cSize.width + hGap;
				heightPanelWest = Math.max(cSize.height, heightPanelWest);
			}
			widthPanelWest += hGap;
			heightPanelWest += vGap;
		}
			
		if (getPanelCenter().getComponentCount() > 0) {
			//Si el panel central tiene botones habrá que redimensionar los paneles
			//izquierdo y derecho al mísmo tamaño (el mayor de ellos) para que realmente
			//se pongan los botones en el centro
			if (widthPanelEast > widthPanelWest)
				widthPanelWest = widthPanelEast;
			else
				widthPanelEast = widthPanelWest;
	
			//No afecta visualmete asignar dimensión al panel central ya que se redimensiona solo al 
			//espacio que le dejen los paneles izquierdo y derecho, pero lo asignamos para saber el 
			//tamaño mínimo que debe tener el diálogo que contenga el panel de botones
			for (int i = 0; i < getPanelCenter().getComponentCount(); i++) {
				Dimension cSize = getPanelCenter().getComponent(i).getPreferredSize();
				widthPanelCenter = widthPanelCenter + cSize.width + hGap;
				heightPanelCenter = Math.max(cSize.height, heightPanelCenter);
			}
			widthPanelCenter += hGap;
			heightPanelCenter += vGap;
		}
	
		getPanelEast().setPreferredSize(new Dimension(widthPanelEast, 0));
		getPanelWest().setPreferredSize(new Dimension(widthPanelWest, 0));
		getPanelCenter().setPreferredSize(new Dimension(widthPanelCenter, 0));
	
		int widthSideMargins = getMarginSides() * 2;
	
		int heightButtonsZone = Math.max(heightPanelEast, heightPanelWest);
		heightButtonsZone = Math.max(heightButtonsZone, heightPanelCenter);
		if (getAspect() == ASPECT_VOID)
			heightButtonsZone = 0;
		
		getPanelButtonsZone().setPreferredSize(new Dimension(0, heightButtonsZone));
			
		setMinWidth(widthPanelEast + widthPanelWest + widthPanelCenter + widthSideMargins + 10);
	}
	
	
	private void setMinWidth(int newValue) {
		this.minWidth = newValue;
	}
	
	protected void setAspect(int newAspecto) {
		
		this.aspect = newAspecto;
	
		setPreferredSize(null);
		
		switch (newAspecto) {
		
			case ASPECT_ACCEPT:
				addButton(BUTTON_ACCEPT, POSITION_RIGHT);
				
				getButton(BUTTON_ACCEPT).addActionListener(actionListener);
				setFocusButton(BUTTON_ACCEPT);
				break;
			
			case ASPECT_ACCEPT_CANCEL:
				addButton(BUTTON_ACCEPT, POSITION_RIGHT);
				addButton(BUTTON_CANCEL, POSITION_RIGHT);
				
				getButton(BUTTON_ACCEPT).addActionListener(actionListener);
				getButton(BUTTON_CANCEL).addActionListener(actionListener);
				setFocusButton(BUTTON_CANCEL);
				break;	
			
			case ASPECT_YES_NO:
				addButton(BUTTON_YES, POSITION_RIGHT);
				addButton(BUTTON_NO, POSITION_RIGHT);
				
				getButton(BUTTON_YES).addActionListener(actionListener);
				getButton(BUTTON_NO).addActionListener(actionListener);
				setFocusButton(BUTTON_NO);
				break;
	
			case ASPECT_YES_NO_CANCEL:
				setAutoSizeButtonsRight(false);
				addButton(BUTTON_YES, POSITION_RIGHT);
				addButton(BUTTON_NO, POSITION_RIGHT);
				addSeparator(POSITION_RIGHT); //Dejamos espacio entre los botones de YES NO y el de CANCELAR
				addButton(BUTTON_CANCEL, POSITION_RIGHT);
				
				getButton(BUTTON_YES).addActionListener(actionListener);
				getButton(BUTTON_NO).addActionListener(actionListener);
				getButton(BUTTON_CANCEL).addActionListener(actionListener);
				setFocusButton(BUTTON_CANCEL);
				break;
		
			case ASPECT_VOID:
				remove(getPanelMain());
				getPanelNorth().remove(getPanelNorthSeparator());
				//setPreferredSize(new Dimension(getPreferredSize().width, getMargen() > 10? getMargen():10));
				setPreferredSize(new Dimension(getPreferredSize().width, getMargin()));
				break;
		}
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		// TODO Auto-generated method stub
		super.setPreferredSize(preferredSize);
	}
	
	public void setAutoCloseOnAccept(boolean autoCloseOnAccept) {
		this.autoCloseOnAccept = autoCloseOnAccept;
	}
	public void setAutoCloseOnCancel(boolean autoCloseOnCancel) {
		this.autoCloseOnCancel = autoCloseOnCancel;
	}
	public void setAutoSizeButtonsCenter(boolean autoSizeButtonsCenter) {
		this.autoSizeButtonsCenter = autoSizeButtonsCenter;
	}
	public void setAutoSizeButtonsRight(boolean autoSizeButtonsRight) {
		this.autoSizeButtonsRight = autoSizeButtonsRight;
	}
	public void setAutoSizeButtonsLeft(boolean autoSizeButtonsLeft) {
		this.autoSizeButtonsLeft = autoSizeButtonsLeft;
	}
	
	public void setFocusButton(String focusButton) {
		this.focusButton = focusButton;
	}
	
	public void setDetailComponent(JComponent detailComponent) {
		setDetailComponent(detailComponent, true);
	}
	public void setDetailComponent(JComponent detailComponent, boolean placeUnderButtons) {
	
		this.detailComponent = detailComponent;
		
		try {
	
			getPanelMain().removeAll();
	
			if (placeUnderButtons) {
	
				getPanelMain().add(getPanelButtonsZone(), BorderLayout.NORTH);
				if (detailComponent != null)
					getPanelMain().add(getPanelDatailZone(), BorderLayout.SOUTH);
			}
			else {
	
				getPanelMain().add(getPanelButtonsZone(), BorderLayout.SOUTH);
				if (detailComponent != null)
					getPanelMain().add(getPanelDatailZone(), BorderLayout.SOUTH);
			}
			if (detailComponent != null) 
				getPanelDatailZone().add(detailComponent, BorderLayout.CENTER);
		} 
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public void setMargin(int newMargen) {
		this.margin = newMargen;
		resizePanels();
	}
	
	public void setDialogContent(LDialogContent newValue) {
		this.dialogContent = newValue;
	}
	
	public void setResponse(int newValue) {
		this.response = newValue;
	}
	
	public boolean isCreateButtonsWithLineBackground() {
		return createButtonsWithLineBackground;
	}
	public void setCreateButtonsWithLineBackground(boolean createButtonsWithLineBackground) {
		this.createButtonsWithLineBackground = createButtonsWithLineBackground;
	}
	
	public int getButtonsHgap() {
		return getPanelCenterFlowLayout().getHgap();
	}
	public int getButtonsVgap() {
		return getPanelCenterFlowLayout().getVgap();
	}
	public void setButtonsHgap(int hgap) {
		getPanelCenterFlowLayout().setHgap(hgap);
		getPanelEastFlowLayout().setHgap(hgap);
		getPanelWestFlowLayout().setHgap(hgap);
	}
	public void setButtonsVgap(int vgap) {
		getPanelCenterFlowLayout().setHgap(vgap);
		getPanelEastFlowLayout().setHgap(vgap);
		getPanelWestFlowLayout().setHgap(vgap);
	}
}
