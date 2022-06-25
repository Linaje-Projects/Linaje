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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import linaje.LocalizedStrings;
import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LPanel;
import linaje.gui.utils.ColorsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Strings;

@SuppressWarnings("serial")
public class MessageDialog extends LDialogContent {
	
	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String detail;
		public String show;
		public String hide;
		public String warning;
		public String error;
		public String info;
		public String question;
		public String testMessage;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final ImageIcon ICON_WARNING = Icons.MESSAGE_WARNING;
	public static final ImageIcon ICON_ERROR = Icons.MESSAGE_ERROR;
	public static final ImageIcon ICON_INFO = Icons.MESSAGE_INFO;
	public static final ImageIcon ICON_QUESTION = Icons.MESSAGE_QUESTION;
	
	private boolean adjustDialogToDetail = true;
	private boolean detailVisible = false;
	private boolean addShowHideToDetailButtonText = true;
	private boolean desplegarDetalleAlMostrar = false;
	
	private String detailButtonText = TEXTS.detail;
	
	private LPanel panelIcon = null;
	private LPanel panelMessage = null;
	private JScrollPane scrollPane = null;
	private JTextArea textArea = null;
	private LPanel panelIcon2 = null;
	private LPanel panelMarginSouth = null;
	private LPanel panelMarginNorth = null;
	private LPanel panelMarginEast = null;
	private LPanel panelMarginWest = null;
	private JComponent detail = null;
	
	private LButton buttonDetail = null;
	
	private Dimension sizeDetail = null;
	private Dimension sizeDialogWithoutDetail = null;
	
	private Dimension sizeFirstTime = null;

	public MessageDialog() {
		super();
		initialize();
	}
	
	public MessageDialog(Frame frame) {
		super(frame);
		initialize();
	}
	
	private void updateDetail() {
		
		String detailButtonText = getDetailButtonTextComplete();
		getButtonDetail().setText(detailButtonText);
	
		if (getDetail() != null) {
	
			Dimension detailSize = new Dimension(0, 0);
			if (detailVisible)
				detailSize = getSizeDetail();
			
			getButtonsPanel().getPanelDatailZone().setPreferredSize(detailSize);
		}
	}
	
	private int adjustDialogToMessage() {
	
		int minWidth = getMinWidth();
		int maxWidth = getMaxWidth();
	
		setDialogWidth(maxWidth);
	
		int linesNumberInitial = getLinesNumber();
			
		//Vamos haciendo el dialogo mas pequeño hasta que queden todas las lineas de la misma longitud
		while (maxWidth > minWidth) {
	
			int currentWidth = getWidth();
			maxWidth = maxWidth - 5;
			if (maxWidth < minWidth)
				maxWidth = minWidth;
				
			setDialogWidth(maxWidth);
			
			int linesNumber = getLinesNumber();
			if (linesNumber > linesNumberInitial) {
				maxWidth = currentWidth + 10;
				break;
			}
		}
	
		if (maxWidth > getMaxWidth())
			maxWidth = getMaxWidth();
		
		setDialogWidth(maxWidth);
		
		return maxWidth;
	}
	
	private void adjustMarginsToMessage() {
	
		if (!isResizable()) {
			
			int linesNumberInitial = getLinesNumber();
			int marginsWidth = 0;
			
			if (linesNumberInitial == 1) {
	
				int textWidth = getFontMetrics(getMessageFont()).stringWidth(getMensaje()) + 5;
				marginsWidth = (getRealWidthTextArea() - textWidth) / 2;
			}
			else {
				
				//Vamos haciendo los margenes mas grandes hasta que queden todas las lineas de la misma longitud
				int linesNumber = linesNumberInitial;
				marginsWidth = getPanelMarginWest().getPreferredSize().width;
				while (linesNumber == linesNumberInitial && !(marginsWidth > getWidth()/4)) {
	
					marginsWidth = getPanelMarginWest().getPreferredSize().width;
					getPanelMarginWest().setPreferredSize(new Dimension(marginsWidth + 2, 0));
					getPanelMarginEast().setPreferredSize(new Dimension(marginsWidth + 2, 0));
						
					linesNumber = getLinesNumber();
				}
			}
	
			marginsWidth = marginsWidth - 4;
			if (marginsWidth < 0)
				marginsWidth = 0;
			getPanelMarginWest().setPreferredSize(new Dimension(marginsWidth, 0));
			getPanelMarginEast().setPreferredSize(new Dimension(marginsWidth, 0));
		}
	}
	
	public static int showMessage(String message) {
		return showMessage(message, null, null, ButtonsPanel.ASPECT_ACCEPT);
	}
	public static int showMessage(String message, Frame frame) {
		return showMessage(message, null, null, ButtonsPanel.ASPECT_ACCEPT, frame);
		
	}
	public static int showMessage(String message, String title, ImageIcon icon, int aspectButtons) {
		return showMessage(message, title, icon, aspectButtons, null);	
	}
	
	public static int showMessage(String message, String title, ImageIcon icon, int aspectButtons, Frame frame) {
	
		int response = ButtonsPanel.RESPONSE_CANCEL;
		try {
	
			if (message == null)
				message = Constants.VOID;
			if (icon != null && title == null)
				title = getTitle(icon);
			if (title == null)
				title = Constants.VOID;
	
			ButtonsPanel buttonsPanel = new ButtonsPanel(aspectButtons);
			buttonsPanel.setAutoCloseOnAccept(true);
			
			MessageDialog dlg = frame == null ? new MessageDialog() : new MessageDialog(frame);
			dlg.setMessage(message);
			dlg.setTitle(title);
			dlg.setIcon(icon);
			dlg.setButtonsPanel(buttonsPanel);
			
			response = dlg.showInDialog();
		} 
		catch (Throwable exception) {
			Console.printException(exception);
		}
	
		return response;
	}
	public static int showMessage(String message, ImageIcon icon) {
	
		int aspectButtons = ButtonsPanel.ASPECT_ACCEPT;
		if (icon != null && icon.equals(ICON_QUESTION))
			aspectButtons = ButtonsPanel.ASPECT_YES_NO;
			
		return showMessage(message, null, icon, aspectButtons);
	}
	public static int showMessage(String message, ImageIcon icon, int aspectButtons) {
		return showMessage(message, null, icon, aspectButtons);
	}
	public static int showMessage(String message, ImageIcon icon, Frame frame) {
	
		int aspectButtons = ButtonsPanel.ASPECT_ACCEPT;
		if (icon != null && icon.equals(ICON_QUESTION))
			aspectButtons = ButtonsPanel.ASPECT_YES_NO;
			
		return showMessage(message, null, icon, aspectButtons, frame);
	}
	
	public boolean getAdjustDialogToDetail() {
		return adjustDialogToDetail && getDetail() != null;
	}
	
	private float getLineWidth() {
	
		FontMetrics fontMetrics = getFontMetrics(getMessageFont());
		return fontMetrics.getHeight();
	}
	
	private int getMaxWidth() {
	
		int maxWidth = getTextArea().getFont().getSize()*50;
		if (getButtonsPanel().getMinWidth() > maxWidth)
			maxWidth = getButtonsPanel().getMinWidth();
		
		return maxWidth;
	}
	
	private int getMinWidth() {
	
		int minWidth = 300;
		if (getButtonsPanel().getMinWidth() > minWidth)
			minWidth = getButtonsPanel().getMinWidth();
	
		if (getAdjustDialogToDetail() && getDetail().getWidth() > minWidth)
			minWidth = getDetail().getWidth();
		
		return minWidth;
	}
	
	private int getRealWidthTextArea() {
		return getWidth()
			   - getPanelIcon().getPreferredSize().width
			   - getPanelIcon2().getPreferredSize().width
			   - getPanelMarginEast().getPreferredSize().width
			   - getPanelMarginWest().getPreferredSize().width
			   - getTextArea().getMargin().left
			   - getTextArea().getMargin().right;
	}
	
	private LButton getButtonDetail() {
		if (buttonDetail == null) {
			buttonDetail = new LButton();
			buttonDetail.addActionListener(new ActionListener() {	
				public void actionPerformed(ActionEvent e) {
					setDetailVisible(!isDetailVisible());
				}
			});
		}
		return buttonDetail;
	}
	
	public boolean getDesplegarDetalleAlMostrar() {
		return desplegarDetalleAlMostrar;
	}
	
	public JComponent getDetail() {
		return detail;
	}
	public Font getMessageFont() {
		return getTextArea().getFont();
	}
	public Icon getIcono() {
		return getPanelIcon().getImageBackground();
	}
	public String getMensaje() {
		return getTextArea().getText();
	}
	
	/**
	 * <b>Descripción:</b><br>
	 * Creado por: Pablo Linaje (01/12/2004 9:07:30)
	 * 
	 */
	private int getLinesNumber() {
	
		int lineWidth = (int) getLineWidth();
		//Actualizamos el ancho del textArea
		int realWidthTextArea = getRealWidthTextArea();				
		getTextArea().setSize(realWidthTextArea, getTextArea().getHeight());
		int realHeightTextArea = getScrollPane().getViewport().getViewSize().height;
		int numLineas = realHeightTextArea / lineWidth;
	
		return numLineas;
	}
	
	private LPanel getPanelIcon() {
		if (panelIcon == null) {
			panelIcon = new LPanel();
			panelIcon.setPreferredSize(new Dimension(0, 0));
			panelIcon.setOpaque(false);
			panelIcon.setLayout(null);
		}
		return panelIcon;
	}
	
	private LPanel getPanelIcon2() {
		if (panelIcon2 == null) {
			panelIcon2 = new LPanel();
			panelIcon2.setPreferredSize(new Dimension(0, 0));
			panelIcon2.setOpaque(false);
			panelIcon2.setLayout(null);
		}
		return panelIcon2;
	}
	
	private LPanel getPanelMarginEast() {
		if (panelMarginEast == null) {
			panelMarginEast = new LPanel();
			panelMarginEast.setPreferredSize(new Dimension(0, 0));
			panelMarginEast.setOpaque(false);
			panelMarginEast.setLayout(null);
		}
		return panelMarginEast;
	}
	
	private LPanel getPanelMarginSouth() {
		if (panelMarginSouth == null) {
			panelMarginSouth = new LPanel();
			panelMarginSouth.setPreferredSize(new Dimension(0, 0));
			panelMarginSouth.setOpaque(false);
			panelMarginSouth.setLayout(null);
		}
		return panelMarginSouth;
	}
	
	private LPanel getPanelMarginWest() {
		if (panelMarginWest == null) {
			panelMarginWest = new LPanel();
			panelMarginWest.setPreferredSize(new Dimension(0, 0));
			panelMarginWest.setOpaque(false);
			panelMarginWest.setLayout(null);
		}
		return panelMarginWest;
	}
	
	private LPanel getPanelMarginNorth() {
		if (panelMarginNorth == null) {
			panelMarginNorth = new LPanel();
			panelMarginNorth.setPreferredSize(new Dimension(0, 0));
			panelMarginNorth.setOpaque(false);
			panelMarginNorth.setLayout(null);
		}
		return panelMarginNorth;
	}
	
	private LPanel getPanelMessage() {
		if (panelMessage == null) {
			panelMessage = new LPanel(new BorderLayout());
			panelMessage.setOpaque(false);
			panelMessage.add(getScrollPane(), BorderLayout.CENTER);
			panelMessage.add(getPanelMarginNorth(), BorderLayout.NORTH);
			panelMessage.add(getPanelMarginSouth(), BorderLayout.SOUTH);
			panelMessage.add(getPanelMarginEast(), BorderLayout.EAST);
			panelMessage.add(getPanelMarginWest(), BorderLayout.WEST);
		}
		return panelMessage;
	}
	
	public boolean isAddShowHideToDetailButtonText() {
		return addShowHideToDetailButtonText;
	}
	
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setOpaque(false);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.setViewportView(getTextArea());
			scrollPane.getViewport().setOpaque(false);
		}
		return scrollPane;
	}
	
	private Dimension getSizeDetail() {
	
		if (sizeDetail == null)
			sizeDetail = new Dimension(0, 0);
		
		return sizeDetail;
	}
	
	private Dimension getSizeDialogWithoutDetail() {
		return sizeDialogWithoutDetail;
	}
	
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setOpaque(false);
			textArea.setWrapStyleWord(true);
			textArea.setText(Constants.VOID);
			textArea.setBounds(0, 0, 160, 120);
			textArea.setEditable(false);
		}
		return textArea;
	}
	
	public String getDetailButtonText() {
		return detailButtonText;
	}
	
	private String getDetailButtonTextComplete() {
		
		String detailButtonText = getDetailButtonText();
		
		try {
			
			if (isDetailVisible()) {
	
				if (isAddShowHideToDetailButtonText())
					detailButtonText = TEXTS.hide + detailButtonText;
				detailButtonText = Strings.capitalize(detailButtonText);
				detailButtonText = "<< " + detailButtonText;
			}
			else {
	
				if (isAddShowHideToDetailButtonText())
					detailButtonText = TEXTS.show + detailButtonText;
				detailButtonText = Strings.capitalize(detailButtonText);
				detailButtonText = detailButtonText + " >>";
			}
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	
		return detailButtonText;
	}
	
	private static String getTitle(ImageIcon icon) {
	
		String title = Constants.VOID;
		if (icon != null) {
	
			if (icon.equals(ICON_WARNING))
				title = TEXTS.warning;
			else if (icon.equals(ICON_ERROR))
				title = TEXTS.error;
			else if (icon.equals(ICON_INFO))
				title = TEXTS.info;
			else if (icon.equals(ICON_QUESTION))
				title = TEXTS.question;			
		}
		
		return title;
	}
	
	private boolean isDetailVisible() {
		return detailVisible;
	}
	
	private void initialize() {
		
		setName("MessageDialog");
		setLayout(new BorderLayout());
		setSize(500, 60);
		add(getPanelIcon(), BorderLayout.WEST);
		add(getPanelMessage(), BorderLayout.CENTER);
		add(getPanelIcon2(), BorderLayout.EAST);
	
		getScrollPane().setBorder(null);
		getPanelIcon().setImageAlignment(SwingConstants.CENTER);
		getPanelIcon().setImageInsets(new Insets(0, -5, 0, 0));
	
		setBackground(ColorsGUI.getColorPanelsBrightest());
		getDefaultDialog().setBackgroundContent(ColorsGUI.getColorPanelsBrightest());
		
		setDetailButtonText(getDetailButtonText());
	
		setTitle(TEXTS.warning);
		setMessage(TEXTS.testMessage);
		setIcon(ICON_WARNING);
	
		setResizable(false);
		setMargin(5);
		ButtonsPanel buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
		buttonsPanel.setAutoCloseOnAccept(true);
		setButtonsPanel(buttonsPanel);
	}
	
	private void insertDetail() {
	
		setSizeDialogWithoutDetail(getDialog().getSize());
		getButtonsPanel().setDetailComponent(getDetail());
		setDetailVisible(getDesplegarDetalleAlMostrar());
	}
	
	public int showInDialog() {
	
		if (sizeFirstTime == null)
			sizeFirstTime = getSize();
		else {
			//Las siguientes veces reseteamos antes de mostrar
			setSize(sizeFirstTime);
			setDetailVisible(false);
			getTextArea().setSize(new Dimension(160, 120));
			getScrollPane().setViewportView(null);
			getScrollPane().setViewportView(getTextArea());
			getPanelMarginWest().setPreferredSize(new Dimension(0, 0));
			getPanelMarginEast().setPreferredSize(new Dimension(0, 0));
		}
		
		int dialogWidth;
	
		if (getAdjustDialogToDetail()) {
			dialogWidth = getMinWidth();
		}
		else {
			dialogWidth = adjustDialogToMessage();
		}
	
		setDialogWidth(dialogWidth);
		adjustMarginsToMessage();
			
		//Asignamos el alto del dialogo
		int altoTexto = (int) (getLineWidth() * getLinesNumber());
		int altoIcono = Math.max(getPanelIcon().getPreferredSize().height, getPanelIcon2().getPreferredSize().height);
		altoTexto +=14;
		altoIcono +=14;
		int altoDialogo = Math.max(altoTexto, altoIcono);
		int margenSup = 0;
		if (altoIcono > altoTexto)
			margenSup = (altoIcono - altoTexto)/2;
		getPanelMarginNorth().setPreferredSize(new Dimension(0, margenSup));
		
		setSize(dialogWidth, altoDialogo);
	
		return super.showInDialog();
	}
	
	public int show(String message) {
	
		setMessage(message);
		return showInDialog();
	}
	
	public int show(String message, ImageIcon icon) {
	
		setMessage(message);
		setIcon(icon);
		
		return showInDialog();
	}
	
	protected void prepareLDialogContent(Point location) {
	
		super.prepareLDialogContent(location);
		
		insertDetail();
	}
	
	private void relocateDialog() {
		
		try {
			
			if (getDialog() != null && getDialog().isVisible() && getFrameOwner() != null && getFrameOwner().isShowing()) {
	
				int positionBotomEdgeDialog = getDialog().getLocationOnScreen().y + getDialog().getHeight();
				int positionBotomEdgeFrame = getFrameOwner().getLocationOnScreen().y + getFrameOwner().getHeight();
				if (positionBotomEdgeDialog > positionBotomEdgeFrame)
					setLocationRelativeTo(getFrameOwner());
				if (getDialog().getLocationOnScreen().y < 0)
					setLocation(getLocation().x, 0);
			}
		} catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public void setAdjustDialogToDetail(boolean newAjustarDialogoADetalle) {
		adjustDialogToDetail = newAjustarDialogoADetalle;
	}
	
	private void setDialogWidth(int ancho) {
	
		setSize(ancho, getSize().height);
	}
	
	public void setDesplegarDetalleAlMostrar(boolean newDesplegarDetalleAlMostrar) {
		desplegarDetalleAlMostrar = newDesplegarDetalleAlMostrar;
	}
	
	public void setDetail(JComponent newDetalle) {
		
		detail = newDetalle;
		if (newDetalle == null) {
	
			setSizeDetail(null);
			getButtonsPanel().removeJComponent(getButtonDetail());
		}
		else {
	
			setSizeDetail(newDetalle.getSize());
			newDetalle.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));
			getButtonsPanel().addJComponent(getButtonDetail(), ButtonsPanel.POSITION_LEFT);
		}
	}
	
	public void setMessageFont(Font font) {
		
		getTextArea().setFont(font);
	}
	
	public void setIcon(Icon icon) {
		
		getPanelIcon().setImageBackground(icon);
	
		int iconWidth = 0;
		int iconHeight = 0;
		if (getIcono() != null) {
			iconWidth = getIcono().getIconWidth();
			iconHeight = getIcono().getIconHeight();
		}
		getPanelIcon().setPreferredSize(new Dimension(iconWidth + 25, iconHeight));
		getPanelIcon2().setPreferredSize(new Dimension(iconWidth + 15, iconHeight));
		
		setBorderColor(getColor(getIcono()));
	}
	
	private Color getColor(Icon icon) {
		Color color = null;
		if (icon == ICON_WARNING)
			color = ColorsGUI.getColorWarning();
		else if (icon == ICON_ERROR)
			color = ColorsGUI.getColorNegative();
		else if (icon == ICON_INFO)
			color = ColorsGUI.getColorInfo();
		
		return color;
	}
	
	public void setBorderColor(Color color) {
		getDefaultDialog().setBorderColor(color);
	}
	
	public void setMessage(String message) {
		
		getTextArea().setText(message.trim());
	}
	
	public void setButtonsPanel(ButtonsPanel buttonsPanel) {
		
		super.setButtonsPanel(buttonsPanel);
	
		if (buttonsPanel != null && getDetail() != null) {
			getButtonsPanel().addJComponent(getButtonDetail(), ButtonsPanel.POSITION_LEFT);
		}
	}
	
	public void setAddShowHideToDetailButtonText(boolean addShowHideToDetailButtonText) {
		this.addShowHideToDetailButtonText = addShowHideToDetailButtonText;
	}
	
	private void setSizeDetail(Dimension sizeDetail) {
		this.sizeDetail = sizeDetail;
	}
	
	protected void setSizeDialogWithoutDetail(Dimension sizeDialogWithoutDetail) {
		this.sizeDialogWithoutDetail = sizeDialogWithoutDetail;
	}
	
	public void setDetailButtonText(String detailButtonText) {
		
		this.detailButtonText = detailButtonText;
		
		updateDetail();
	}
	
	private void setDetailVisible(boolean detailVisible) {
		
		this.detailVisible = detailVisible;
	
		updateDetail();
	
		if (detailVisible) {
	
			getDialog().setSize(getSizeDialogWithoutDetail().width, getSizeDialogWithoutDetail().height + getSizeDetail().height);
			if (detail != null && detail instanceof JScrollPane) {
				JScrollPane scrollPaneDetail = (JScrollPane) detail;
				scrollPaneDetail.getViewport().setViewPosition(new Point(0,0));
			}
			
			//Recolocamos el dialogo si es necesario
			if (getDialog() != null) {
	
				if (!getDialog().isVisible()) {
					
					boolean modal = isModal();
					setModal(false);
					getDialog().setVisible(true);
					
					relocateDialog();
					
					getDialog().setVisible(false);
					setModal(modal);
				}
				else {
	
					relocateDialog();
				}
			}
		}
		else {
	
			getDialog().setSize(getSizeDialogWithoutDetail());
		}
		getDialog().validate();
		getDialog().repaint();
	}
}
