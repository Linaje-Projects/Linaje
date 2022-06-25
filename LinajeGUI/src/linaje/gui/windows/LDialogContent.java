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

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import linaje.gui.AppGUI;
import linaje.gui.LPanel;
import linaje.gui.ToolTip;
import linaje.gui.components.ButtonsPanelWizard;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Utils;

@SuppressWarnings("serial")
public class LDialogContent extends LPanel implements WindowListener {

	private int margin = 5;
	
	private Frame frameOwner = null;
	private HeaderPanel headerPanel = null;
	private ButtonsPanel buttonsPanel = null;
	private AuxDescriptionPanel auxDescriptionPanel = null;
	private LPanel panelMarginRight = null;
	
	private boolean autoSize = true;
	private JDialog dialog = null;
	private Dimension originalSize = null;

	private String title = null;
	private JFrame frame = null;
	private Image iconImage = null;
	
	private LPanel contentPane = null;
	private JComponent firstFocusableComponent = null;
	
	private LDialog defaultDialog = null;

	public LDialogContent() {
		super();
		initialize();
	}
	public LDialogContent(Frame frame) {
		super();
		setFrameOwner(frame);
		initialize();
	}
	
	private void initialize() {
		setForeground(ColorsGUI.getColorText());
	}
	
	public void addComponents() {
	
		getContentPane().removeAll();
	
		assignMargins();
		
		//CUERPO PRINCIPAL
		getContentPane().add(this, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(getOriginalSize().width, getOriginalSize().height));
	
		//PANEL DE BOTONES
		if (getButtonsPanel() != null) {
			getContentPane().add(getButtonsPanel(), BorderLayout.SOUTH);
			getButtonsPanel().setDialogContent(this);
			if (getButtonsPanel() instanceof ButtonsPanelWizard) {
				//Si tenemos el aspecto Wizard añadimos el panel auxiliar para describir los pasos
				setAuxDescriptionPanel(new AuxDescriptionPanel());
				getAuxDescriptionPanel().setMarginTextArea(0, getMargin(), getMargin(), 0);
				((ButtonsPanelWizard) getButtonsPanel()).updateWizardText();	
			}
		}
	
		//CABECERA
		if (getHeaderPanel() != null)
			getContentPane().add(getHeaderPanel(), BorderLayout.NORTH);
	
		//PANEL AUXILIAR (IZQUIERDA)
		if (getAuxDescriptionPanel() != null)
			getContentPane().add(getAuxDescriptionPanel(), BorderLayout.WEST);
	
		//MARGEN DERECHA
		getContentPane().add(getPanelMarginRight(), BorderLayout.EAST);
	
		assignSize();
	}
	
	public void destroy() {
	
		try {
			
			//Dejamos los objetos globales a null
			if (headerPanel != null)
				headerPanel.destroy();
			if (buttonsPanel != null)
				buttonsPanel.destroy();
			if (auxDescriptionPanel != null)
				auxDescriptionPanel.destruir();
	
			if (dialog != null)
				dialog.removeWindowListener(this);
			
			dialog = null;
			headerPanel = null;
			buttonsPanel = null;
			auxDescriptionPanel = null;	
			frameOwner = null;
			panelMarginRight = null;
			originalSize = null;
			title = null;
		
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public void dispose() {
	
		if (confirmWindowClose()) {
			
			if (getFrame() != null) {
				getFrame().setVisible(false);
				if (getFrame().getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
					System.exit(0);
			}
			else
				getDialog().dispose();
		}
	}
	/**
	 * <b>Descripción:</b><br>
	 * Creado por: Pablo Linaje (20/04/2005 12:37:24)
	 *
	 * Cuando se quiera hacer algo en algún paso cuando el LDialogContent tiene 'Aspecto Wizard',
	 * hay que sobreescribir este método y realizar las acciones oportunas
	 *
	 * @param stepNumber int
	 */
	public void executeStep(int stepNumber) {}
	
	public boolean isAutoSize() {
		return autoSize;
	}
	
	private LPanel getContentPane() {
		if (contentPane == null) {
			contentPane = new LPanel(new BorderLayout());
		}
		return contentPane;
	}
	
	public LDialog getDefaultDialog() {
		if (defaultDialog == null) {
			defaultDialog = new LDialog(getFrameOwner(), true);
			defaultDialog.setBackgroundContent(getBackground());
			defaultDialog.setForeground(getForeground());
		}
		return defaultDialog;
	}
	
	public JDialog getDialog() {
		
		if (dialog == null) {
			
			dialog = getDefaultDialog();
			//((LDialog)dialog).setTransparency(0);
			dialog.removeWindowListener(this);
			dialog.addWindowListener(this);
		}
		return dialog;
	}
	
	/**
	 * Se usará como frame owner del diálogo siempre que no se pase un JDialog en setDialog
	 * No tendrá relevancia si mostramos el LDialogContent en un frame
	 */
	protected Frame getFrameOwner() {
		if (frameOwner == null)
			frameOwner = AppGUI.getCurrentAppGUI().getFrame();
		return frameOwner;
	}
	
	/**
	 * Se usará como icono del marco de la ventana en caso de que mostremos el LDialogContent en un frame
	 * No tendrá relevancia si mostramos el LDialogContent en un dialogo
	 */
	public Image getIconImage() {
		return iconImage;
	}
	
	public int getMargin() {
		return margin;
	}
	private Dimension getOriginalSize() {
		if (originalSize == null)
			originalSize = new Dimension(getWidth(), getHeight());
		return originalSize;
	}
	public AuxDescriptionPanel getAuxDescriptionPanel() {
		if (auxDescriptionPanel == null) {
			auxDescriptionPanel = new AuxDescriptionPanel(AuxDescriptionPanel.ASPECT_VOID);
		}
		return auxDescriptionPanel;
	}
	public ButtonsPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new ButtonsPanel(ButtonsPanel.ASPECT_VOID);
		}
		return buttonsPanel;
	}
	public HeaderPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new HeaderPanel(HeaderPanel.ASPECT_VOID);
		}
		return headerPanel;
	}
	
	private LPanel getPanelMarginRight() {
		if (panelMarginRight == null) {
			panelMarginRight = new LPanel();
			panelMarginRight.setOpaque(false);
		}
		return panelMarginRight;
	}
	
	public String getTitle() {
		return title != null ? title : getDialog().getTitle() == null || getDialog().getTitle().equals(Constants.VOID) ? AppGUI.getCurrentApp().getName() : getDialog().getTitle();
	}
	
	/**
	 * Es la ventana en la que se muestra el LDialogContent cuando usamos showInFrame
	 * Si lo mostramos en un díalogo no tiene sentido
	 **/
	public JFrame getFrame() {
		return frame;
	}
	
	public boolean isModal() {
		return getDialog().isModal();
	}
	public boolean isResizable() {
		return getDialog().isResizable();
	}
	
	public int showInDialog() {
		return showInDialog(null);
	}
	public int showInDialog(Component component, int hGap, int vGap) {
		Point location = null;
		if (component != null) {
			try {
				location = component.getLocationOnScreen();
				location.x = location.x + hGap;
				location.y = location.y + vGap;
			}
			catch (Exception e) {}
		}
		return showInDialog(location);
	}
	public int showInDialog(Point location) {
	
		int response = -1;
	
		setFrame(null);
		prepareLDialogContent(location);
	
		getDialog().setContentPane(getContentPane());
		
		getDialog().setVisible(true);
		//if (!(getDialog() instanceof LDialog))
			//UtilsGUI.showDialogDarkenOwner(getDialog());	
	
		if (ToolTip.getInstance().getLWindow().getWindow().isVisible())
			ToolTip.getInstance().hideTooltip();
			
		if (getButtonsPanel() != null)
			response = getButtonsPanel().getResponse();
		
		return response;
	}
	
	public JFrame showInFrame() {
	
		prepareLDialogContent(null, true);
	
		if (getFrame() == null) {
			setFrame(AppGUI.getCurrentAppGUI().getFrame());	
		}
		
		getFrame().pack();
		getFrame().setBounds(getDialog().getBounds());
		adjustWindowLocation();
		getFrame().setContentPane(getContentPane());
		getFrame().setResizable(getDialog().isResizable());
		getFrame().setTitle(getDialog().getTitle());
		if (getIconImage() != null)
			getFrame().setIconImage(getIconImage());
		
		getFrame().setVisible(true);
	
		return getFrame();
	}
	
	/**
	 * Ajustamos al posición de la ventana para que no se salga de la pantalla
	 * Tenemos en cuenta que puede haber varias pantallas fisicas (monitores)
	 **/
	private void adjustWindowLocation() {
		
		Rectangle windowBounds = getFrame().getBounds();
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = environment.getScreenDevices();
		
		Rectangle screenBounds = null;
		boolean screenFound = false;
		for (int i = screens.length - 1; !screenFound && i >= 0; i--) {
			GraphicsDevice currentScreen = screens[i];
			Rectangle currentScreenBounds = currentScreen.getDefaultConfiguration().getBounds();
			if (currentScreenBounds.intersects(windowBounds))
				screenBounds = currentScreenBounds;
		}
		
		if (screenBounds != null) {
			
			if (windowBounds.x < screenBounds.x)
				windowBounds.x = screenBounds.x;
			if (windowBounds.y < screenBounds.y)
				windowBounds.y = screenBounds.y;
			
			getFrame().setLocation(windowBounds.getLocation());
		}
	}
	
	private void assignMargins() {
	
		int margin = getMargin();
		
		getPanelMarginRight().setPreferredSize(new Dimension(margin, 0));
		getButtonsPanel().setMargin(margin);
		getAuxDescriptionPanel().setMargin(margin);
		getHeaderPanel().setMargin(margin);
	}
	
	protected void prepareLDialogContent() {
		prepareLDialogContent(null);
	}
	protected void prepareLDialogContent(Point location) {
		prepareLDialogContent(location, false);
	}
	protected void prepareLDialogContent(Point location, boolean isFrame) {
	
		if (getDialog() != null && !getDialog().isVisible()) {
	
			//Agregamos los componentes de nuestro dialog
			addComponents();
			setTitle(getTitle());
			
			//Esto es para que dentro de Visual Age se dimensione el diálogo igual que desde fuera
			boolean resizable = isResizable();
			setResizable(true);
			
			//Centramos la ventana
			//getFrame().pack();
			if (location == null)
				setLocationRelativeTo(getFrameOwner());
			else
				getDialog().setLocation(location);
	
			if (!isFrame) {
				getDialog().removeWindowListener(this);
				boolean modal = isModal();
				
				try {
					
					//Hacemos el diálogo NO modal para que después de hacerlo visible podamos recuperar el foco en el botón de cancelar
					setModal(false);
		
					getDialog().setVisible(true);
		
					//No dejamos que el dialog se salga de la pantalla, tiene que estar el dialog visible
					Point adjustedLocation = UtilsGUI.getWindowLocationAdjusted(getDialog(), getDialog().getLocation());
					getDialog().setLocation(adjustedLocation);
		
					setResizable(resizable);
				}
				catch (Throwable ex) {
					Console.printException(ex);
				}
		
				if (getDialog().isVisible())
					getDialog().setVisible(false);
						
				//Volvemos a dejar el diálogo modal en caso de que lo estubiese
				setModal(modal);
				getDialog().addWindowListener(this);
			}
		}
	}
	
	public void assignSize() {
		
		if (isAutoSize()) {
		
			int heightHeader = 0;
			int heightMainPanel = 0;
			int heightButtonsPanel = 0;
			
			int widthMainPanel = 0;
			int widthButtonsPanel = 0;
			int widthAuxPanel = 0;
			
			widthMainPanel = getPreferredSize().width;
			heightMainPanel = getPreferredSize().height;
			
			if (getHeaderPanel() != null) {
				heightHeader = getHeaderPanel().getPreferredSize().height;
			}
			if (getButtonsPanel() != null) {
				widthButtonsPanel = getButtonsPanel().getMinWidth();
				heightButtonsPanel = getButtonsPanel().getPreferredSize().height;
			}
			if (getAuxDescriptionPanel() != null) {
				widthAuxPanel = getAuxDescriptionPanel().getPreferredSize().width;
			}
	
			int width = widthAuxPanel + widthMainPanel + getMargin();
			if (widthButtonsPanel > width)
				width = widthButtonsPanel;
	
			int height = heightHeader + heightMainPanel + heightButtonsPanel;
			
			getDialog().pack();
			Insets dialogInsets;
			if (getDialog() instanceof LDialog) {
				LDialog lDialog = (LDialog) getDialog();
				dialogInsets = lDialog.getLDialogInsets();
			} 
			else {
				dialogInsets = getDialog().getInsets();
			}
			width += (dialogInsets.left + dialogInsets.right);
			height += (dialogInsets.top + dialogInsets.bottom);
			
			getDialog().setSize(width, height);
		}
	}
	
	public void setAutoSize(boolean autoSize) {
		this.autoSize = autoSize;
	}
		
	public void setDialog(JDialog dialog) {
	
		if (dialog != null)
			dialog.removeWindowListener(this);
		
		if (dialog != null) {
			dialog.removeWindowListener(this);
			dialog.addWindowListener(this);
		}
	
		this.dialog = dialog;
	}
	
	private void setFrameOwner(Frame frameOwner) {
		this.frameOwner = frameOwner;
	}
	public void setIconImage(Image iconImage) {
		this.iconImage = iconImage;
	}
	
	public void setLocationRelativeTo(Component component) {
		getDialog().setLocationRelativeTo(component);
		if (getFrame() != null)
			UtilsGUI.centerWindow(getFrame());
	}
	
	public void setMargin(int margin) {
		this.margin = margin;
	}
	public void setModal(boolean modal) {
		getDialog().setModal(modal);
	}
	private void setOriginalSize(Dimension originalSize) {
		this.originalSize = originalSize;
	}
	public void setAuxDescriptionPanel(AuxDescriptionPanel auxDescriptionPanel) {
		this.auxDescriptionPanel = auxDescriptionPanel;
	}
	
	public void setButtonsPanel(ButtonsPanel buttonsPanel) {
		
		ButtonsPanel oldButtonPanel = this.buttonsPanel;
		if (Utils.propertyChanged(oldButtonPanel, buttonsPanel)) {
			this.buttonsPanel = buttonsPanel;
		
			if (buttonsPanel != null) {
				
				this.buttonsPanel.setDialogContent(this);
		
				if (buttonsPanel instanceof ButtonsPanelWizard)
					executeStep(1);
			}
			
			if (oldButtonPanel != null) {
				getContentPane().remove(oldButtonPanel);
				getContentPane().add(getButtonsPanel(), BorderLayout.SOUTH);
				getButtonsPanel().setDialogContent(this);
				if (getButtonsPanel() instanceof ButtonsPanelWizard) {
					//Si tenemos el aspecto Wizard añadimos el panel auxiliar para describir los pasos
					setAuxDescriptionPanel(new AuxDescriptionPanel());
					getAuxDescriptionPanel().setMarginTextArea(0, getMargin(), getMargin(), 0);
					((ButtonsPanelWizard) getButtonsPanel()).updateWizardText();	
				}
				validate();
				repaint();
			}
		}
	}
	
	public void setHeaderPanel(HeaderPanel headerPanel) {
		this.headerPanel = headerPanel;
	}
	
	public void setResizable(boolean resizable) {
		
		if (getFrame() != null)
			getFrame().setResizable(resizable);
		getDialog().setResizable(resizable);
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		setOriginalSize(new Dimension(width, height));
	}
	
	public void setSize(Dimension dimension) {
		super.setSize(dimension);
		setOriginalSize(dimension);
	}
	
	public void setBackground(Color bg) {
		super.setBackground(bg);
		getContentPane().setBackground(bg);
		if (getDialog() instanceof LDialog)
			((LDialog) getDialog()).setBackgroundContent(bg);
	}
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (getDialog() instanceof LDialog)
			((LDialog) getDialog()).setForeground(fg);
	}
	
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(false);
		getContentPane().setOpaque(isOpaque);
	}
	@Override
	public void setOpacity(float opacity) {
		super.setOpacity(0);
		getContentPane().setOpacity(opacity);
	}
	public void setTitle(String title) {
	
		this.title = title;
		getDialog().setTitle(title);
		if (getFrame() != null)
			getFrame().setTitle(title);
	}
	
	public void setFrame(JFrame frame) {
		if (this.frame != null)
			this.frame.removeWindowListener(this);
		this.frame = frame;
		if (frame != null) {
			frame.addWindowListener(this);
			if (title == null)
				title = frame.getTitle();
		}
	}
	
	public void windowActivated(WindowEvent e) {
	
		JComponent firstFocusableComponent = getFirstFocusableComponent();
		if (firstFocusableComponent == null)
			firstFocusableComponent = getButtonsPanel().getButton(getButtonsPanel().getFocusButton());
		if (firstFocusableComponent != null)
			firstFocusableComponent.requestFocusInWindow();
	}
	public void windowClosing(WindowEvent e) {
	
		if (getFrame() != null && getFrame().getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
			confirmWindowClose();
			
		if (getButtonsPanel() != null)
			getButtonsPanel().setResponse(-1);
	}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	/**
	 * Sobreescribir este método si se quiere hacer algo antes de que se cierre la ventana o cancelar el cierre
	 */
	protected boolean confirmWindowClose() {
		return true;
	}
	
	public static LDialogContent showComponentInFrame(JComponent component) {
		
		try {
			
			final int MARGIN = 10;
			LDialogContent dialogContent = new LDialogContent();
			Dimension dimension = new Dimension(Math.max(component.getWidth(), component.getPreferredSize().width), Math.max(component.getHeight(), component.getPreferredSize().height));
			dimension.width+=MARGIN*2;
			dimension.height+=MARGIN*2;
			dialogContent.setSize(dimension);
			ButtonsPanel pb = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
			pb.setAutoCloseOnAccept(true);
			dialogContent.setButtonsPanel(pb);
			dialogContent.setMargin(MARGIN);
			dialogContent.setResizable(true);
			dialogContent.setLayout(new BorderLayout());
			dialogContent.add(component, BorderLayout.CENTER);
			dialogContent.showInFrame();
			
			return dialogContent;
			
		} catch (Throwable ex) {
			Console.printException(ex);
		}
		
		return null;
	}
	
	public JComponent getFirstFocusableComponent() {
		return firstFocusableComponent;
	}
	public void setFirstFocusableComponent(JComponent firstFocusableComponent) {
		this.firstFocusableComponent = firstFocusableComponent;
	}
}
