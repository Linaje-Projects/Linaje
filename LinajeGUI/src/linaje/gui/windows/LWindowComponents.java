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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import linaje.gui.Icons;
import linaje.gui.LButton;
import linaje.gui.LLabel;
import linaje.gui.LPanel;
import linaje.gui.RoundedBorder;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;
import linaje.statics.Constants;
import linaje.utils.Colors;
import linaje.utils.StateColor;

public class LWindowComponents {

	private Window window = null;
	private JDialog dialog = null;
	private JFrame frame = null;
	
	public static final float DEFAULT_TRANSPARENCY = 0f;//0.1f;
	private float transparency = DEFAULT_TRANSPARENCY;
	private LPanel windowBorderPanel = null;
	private LPanel windowElementsPanel = null;
	private LPanel windowEmptyPanel = null;
	
	private Container defaultContentPane = null;
	private Color backgroundContent = null;
	private LPanel panelNorth = null;
	private LPanel panelButtons = null;
	private LLabel labelTitle = null;
	private LButton buttonClose = null;
	private LButton buttonMaximize = null;
	private LButton buttonMinimize = null;
	
	private RoundedBorder defaultRoundedBorder = null;
	
	private Point mousePressedLocation = null;
	private int mousePressedCursor = Cursor.DEFAULT_CURSOR;
	
	private boolean maximized = false;
	private boolean minimized = false;
	
	private Rectangle restoreBounds = null;
	private boolean resizing = false;
	//Usar una ventana opaca como contentPane para que se vean las transparencias del borde
	//y se pinte correctamente el antialiasing de los componentes de la ventana
	private boolean useContentWindowWhenOpaque = true;
	private LContentWindow contentWindow = null;
	
	private boolean closeable = true;
	private boolean maximizable = true;
	private boolean minimizable = true;
	private boolean minimizeCompact = true;
	
	private final Icon ICON_MAXIMIZE = Icons.getIconRectangle(7, 7);
	private final Icon ICON_MINIMIZE = Icons.getIconBarHoriz(7);
	private final Icon ICON_RESTORE = Icons.getIconRectangle(7, 5);
	
	private boolean buttonCloseAllwaysRed = false;//if false buttonClose is red only on rollover
		
	public LWindowComponents(Window window) {
		setWindow(window);
		initialize();
	}

	public Window getWindow() {
		return window;
	}
	private void setWindow(Window window) {
		this.window = window;
		if (window != null) {
			if (window instanceof JDialog)
				dialog = (JDialog) window;
			else if (window instanceof JFrame)
				frame = (JFrame) window;
		}
	}
	
	public void setTransparency(float transparency) {
		if (transparency < 0 || transparency > 1)
			transparency = DEFAULT_TRANSPARENCY;
		this.transparency = transparency;
		applyBGTransparency();
	}
	public float getTransparency() {
		return transparency;
	}
	
	public Color getForeground() {
		return getLabelTitle().getForeground();
	}
	
	public void setForeground(Color fg) {
		getLabelTitle().setForeground(fg);
		getPanelButtons().setForeground(fg);
		if (!buttonCloseAllwaysRed) {
			if (getButtonClose().getForeground() instanceof StateColor) {
				StateColor sc = (StateColor) getButtonClose().getForeground();
				getButtonClose().setForeground(new StateColor(sc, fg));
			}
			else
				getButtonClose().setForeground(fg);
		}
		if (!(fg instanceof StateColor) && getButtonMaximize().getForeground() instanceof StateColor) {
			StateColor currentSC = (StateColor) getButtonMaximize().getForeground();
			StateColor sc = new StateColor(currentSC, fg);
			getButtonMaximize().setForeground(sc);
			getButtonMinimize().setForeground(sc);
		}
		else {
			getButtonMaximize().setForeground(fg);
			getButtonMinimize().setForeground(fg);
		}
	}
	
	public Border getBorder() {
		return getWindowBorderPanel().getBorder();
	}
	public void setBorder(Border border) {
		getWindowBorderPanel().setBorder(border);
	}
	
	private void initialize() {
		
		setContentPane(getDefaultContentPane());
		
		getButtonMaximize().setVisible(isMaximizable());
		getButtonMinimize().setVisible(isMinimizable());
		getButtonClose().setVisible(isCloseable());
		
		getWindowBorderPanel().setBorder( getDefaultRoundedBorder()); 
		applyBGTransparency();
		
		MouseListener mouseListener = new MouseListener() {
			public void mousePressed(MouseEvent e) {
				mousePressedLocation = e.getPoint();
				mousePressedCursor = getResizeCursor(mousePressedLocation);
			}
			
			public void mouseReleased(MouseEvent e) {
				if (isResizable()) {
					resizing = false;
					getWindow().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
			public void mouseExited(MouseEvent e) {
				if (isResizable() && !resizing)
					getWindow().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			public void mouseEntered(MouseEvent e) {
				if (isResizable() && !resizing) {
					getWindow().setCursor(new Cursor(getResizeCursor(e.getPoint())));
				}
			}
			public void mouseClicked(MouseEvent e) {
				if (isResizable() && e.getClickCount() == 2 && e.getY() < 20) {
					getButtonMaximize().doClick();
				}
			}
		};
		
		MouseMotionListener mouseMotionListener = new MouseMotionListener() {
			
			public void mouseDragged(MouseEvent e) {
				
				if (mousePressedLocation == null)
					return;
				
				Point dragLocation = e.getPoint();
				int xDiference = dragLocation.x - mousePressedLocation.x;
				int yDiference = dragLocation.y - mousePressedLocation.y;
				int wDiference = 0;
				int hDiference = 0;
					
				int cursor = mousePressedCursor;
				
				Rectangle bounds = getWindow().getBounds();
				
				if (cursor != Cursor.DEFAULT_CURSOR) {
					
					resizing = true;
					/*//Si no limitamos los repintados, en diálogos con mucho contenido visual se hacen muy pesados tantos repintados
					final int MIN_TIME_BETWEEN_RESIZES = 100;
					Timer timer = new Timer(MIN_TIME_BETWEEN_RESIZES, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							resizing = false;
						}
					});
					timer.setRepeats(false);
					timer.start();*/
					
					if (cursor == Cursor.NW_RESIZE_CURSOR) {
						wDiference = -xDiference;
						hDiference = -yDiference;
					}
					else if (cursor == Cursor.N_RESIZE_CURSOR) {
						xDiference = 0;
						hDiference = -yDiference;
					}
					else if (cursor == Cursor.NE_RESIZE_CURSOR) {
						wDiference = xDiference;
						hDiference = -yDiference;
						xDiference = 0;
					}
					else if (cursor == Cursor.E_RESIZE_CURSOR) {
						wDiference = xDiference;
						xDiference = 0;
						yDiference = 0;
					}
					else if (cursor == Cursor.SE_RESIZE_CURSOR) {
						wDiference = xDiference;
						hDiference = yDiference;
						xDiference = 0;
						yDiference = 0;
					}
					else if (cursor == Cursor.S_RESIZE_CURSOR) {
						hDiference = yDiference;
						yDiference = 0;
						xDiference = 0;
					}
					else if (cursor == Cursor.SW_RESIZE_CURSOR) {
						wDiference = -xDiference;
						hDiference = yDiference;
						yDiference = 0;
					}
					else if (cursor == Cursor.W_RESIZE_CURSOR) {
						wDiference = -xDiference;
						hDiference = 0;
						yDiference = 0;
					}
					
					Dimension minSize = getMinsize(false);
					
					int minResizeW = minSize.width - bounds.width;
					int minResizeH = minSize.height - bounds.height;
					
					if (wDiference < 0 && wDiference < minResizeW) {
						wDiference = minResizeW;
						if (xDiference > 0)
							xDiference = -minResizeW;
						else if (xDiference < 0)
							xDiference = minResizeW;
					}
					if (hDiference < 0 && hDiference < minResizeH) {
						hDiference = minResizeH;
						if (yDiference > 0)
							yDiference = -minResizeH;
						else if (yDiference < 0)
							yDiference = minResizeH;
					}
				}
				
				bounds.x += xDiference;
				bounds.y += yDiference;
				bounds.width += wDiference;
				bounds.height += hDiference;
				
				if (xDiference == 0 && wDiference != 0)
					mousePressedLocation.x += wDiference;
				if (yDiference == 0 && hDiference != 0)
					mousePressedLocation.y += hDiference;
				
				getWindow().setBounds(bounds);
				
				if (resizing) {
					if (maximized) {
						maximized = false;
						getButtonMaximize().setIcon(ICON_MAXIMIZE);
					}
					if (minimized) {
						minimized = false;
						//getButtonMinimize().setIcon(ICON_MINIMIZE);
					}
				}
			}
			
			public void mouseMoved(MouseEvent e) {
				if (isResizable() && !resizing) {
					getWindow().setCursor(new Cursor(getResizeCursor(e.getPoint())));
				}
			}
		};
		
		getLabelTitle().addMouseListener(mouseListener);
		getWindowElementsPanel().addMouseListener(mouseListener);
		getWindow().addMouseListener(mouseListener);
		
		getLabelTitle().addMouseMotionListener(mouseMotionListener);
		getWindowElementsPanel().addMouseMotionListener(mouseMotionListener);
		getWindow().addMouseMotionListener(mouseMotionListener);
	}
	
	private void comprobarContentWindow() {
		if (getTransparency() == 0 && useContentWindowWhenOpaque) {
			getWindowBorderPanel().setOpaque(false);
			getWindowElementsPanel().setOpaque(true);
			getWindowBorderPanel().remove(getWindowElementsPanel());
			getWindowBorderPanel().add(getWindowEmptyPanel(), BorderLayout.CENTER);
			if (contentWindow == null)
				contentWindow = new LContentWindow(this);
			//contentWindow.setContentPane(getWindowElementsPanel());
		}
		else if (contentWindow != null) {
			try {
				contentWindow.finalize();
			} catch (Throwable ex) {
				Console.printException(ex);
			}
			contentWindow = null;
			getWindowBorderPanel().setOpaque(false);
			getWindowElementsPanel().setOpaque(false);
			getWindowBorderPanel().remove(getWindowEmptyPanel());
			getWindowBorderPanel().add(getWindowElementsPanel(), BorderLayout.CENTER);
		}
	}
	
	public boolean isUseContentWindowWhenOpaque() {
		return useContentWindowWhenOpaque;
	}
	/**
	 * Se usará una ventana opaca como contentPane para que se vean las transparencias del borde
	 * y se pinte correctamente el antialiasing de los componentes de la ventana
	 * 
	 * @param useContentWindowWhenOpaque
	 */
	public void setUseContentWindowWhenOpaque(boolean useContentWindowWhenOpaque) {
		this.useContentWindowWhenOpaque = useContentWindowWhenOpaque;
		applyBGTransparency();
	}
	
	public Dimension getMinsize(boolean includeTitle) {
		Insets borderInsets = getWindowBorderPanel().getInsets();//getBorder().getBorderInsets(LDialog.this);
		int minW = getPanelButtons().getPreferredSize().width + borderInsets.left + borderInsets.right + 5;
		int minH = getPanelNorth().getPreferredSize().height + borderInsets.top + borderInsets.bottom;
		
		if (includeTitle)
			minW += getLabelTitle().getPreferredSize().width;
		
		return new Dimension(minW, minH);
	}
	private int getResizeCursor(Point mouseLocation) {
		
		if (isResizable()) {
			final int RESIZE_MARGIN = 2;
			
			int x = mouseLocation.x;
			int y = mouseLocation.y;
			int w = getWindow().getWidth();
			int h = getWindow().getHeight();
			
			Insets insets = getBorder().getBorderInsets(getWindow());
			
			final int TOP_MARGIN_POSITION = insets.top + RESIZE_MARGIN;
			final int WEST_MARGIN_POSITION = insets.left + RESIZE_MARGIN;
			final int EAST_MARGIN_POSITION = w - RESIZE_MARGIN - insets.right;
			final int SOUTH_MARGIN_POSITION = h - RESIZE_MARGIN - insets.bottom;
			
			if (y < TOP_MARGIN_POSITION) {
				if (x < WEST_MARGIN_POSITION)
					return Cursor.NW_RESIZE_CURSOR;
				else if (x > EAST_MARGIN_POSITION)
					return Cursor.NE_RESIZE_CURSOR;
				else
					return Cursor.N_RESIZE_CURSOR;
			}
			else if (y > SOUTH_MARGIN_POSITION) {
				if (x < WEST_MARGIN_POSITION)
					return Cursor.SW_RESIZE_CURSOR;
				else if (x > EAST_MARGIN_POSITION)
					return Cursor.SE_RESIZE_CURSOR;
				else
					return Cursor.S_RESIZE_CURSOR;
			}
			else {
				if (x < WEST_MARGIN_POSITION)
					return Cursor.W_RESIZE_CURSOR;
				else if (x > EAST_MARGIN_POSITION)
					return Cursor.E_RESIZE_CURSOR;
			}
		}
		return Cursor.DEFAULT_CURSOR;
	}
	
	protected LPanel getWindowBorderPanel() {
		if (windowBorderPanel == null) {
			windowBorderPanel = new LPanel(new BorderLayout());
			windowBorderPanel.setOpaque(false);
			windowBorderPanel.add(getWindowElementsPanel(), BorderLayout.CENTER);
		}
		return windowBorderPanel;
	}
	
	protected LPanel getWindowElementsPanel() {
		if (windowElementsPanel == null) {
			windowElementsPanel = new LPanel(new BorderLayout());
			windowElementsPanel.setOpaque(false);
			windowElementsPanel.add(getPanelNorth(), BorderLayout.NORTH);
		}
		return windowElementsPanel;
	}
	
	@SuppressWarnings("serial")
	private LPanel getWindowEmptyPanel() {
		if (windowEmptyPanel == null) {
			windowEmptyPanel = new LPanel() {
				@Override
				public Color getBackground() {
					return getWindowElementsPanel().getBackground();
				}
			};
		}
		return windowEmptyPanel;
	}
	
	private LPanel getPanelNorth() {
		if (panelNorth == null) {
			panelNorth = new LPanel(new BorderLayout());
			panelNorth.setOpaque(false);
			panelNorth.add(getLabelTitle(), BorderLayout.CENTER);
			panelNorth.add(getPanelButtons(), BorderLayout.EAST);
		}
		return panelNorth;
	}
	public LLabel getLabelTitle() {
		if (labelTitle == null) {
			labelTitle = new LLabel(dialog != null ? dialog.getTitle() : frame!= null ? frame.getTitle() : Constants.VOID);
			labelTitle.setVerticalAlignment(SwingConstants.TOP);
			labelTitle.setFontSize(labelTitle.getFontSize()+9);
			labelTitle.setFontStyle(Font.BOLD);
			labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
			labelTitle.setMargin(new Insets(1, 1, 1, 1));
		}
		return labelTitle;
	}
	private LPanel getPanelButtons() {
		if (panelButtons == null) {
			LFlowLayout flowLayout = new LFlowLayout(LFlowLayout.RIGHT, SwingConstants.TOP, 0, 0, false);
			flowLayout.setPreferredSizeVertical(false);
			panelButtons = new LPanel();
			panelButtons.setOpaque(false);
			panelButtons.add(getButtonMinimize());
			panelButtons.add(getButtonMaximize());
			panelButtons.add(getButtonClose());
		}
		return panelButtons;
	}
	private LButton getButtonClose() {
		if (buttonClose == null) {
			buttonClose = new LButton(Constants.VOID);
			StateColor foreground;
			if (buttonCloseAllwaysRed) {
				foreground = new StateColor(ColorsGUI.getColorNegative(), null, Colors.brighter(ColorsGUI.getColorNegative(), 0.1));
			}
			else {
				foreground = new StateColor(buttonClose.getForeground());
				foreground.setRolloverColor(ColorsGUI.getColorNegative());
			}
			buttonClose.setForeground(foreground);
			buttonClose.setOpaque(false);
			buttonClose.setIcon(Icons.getIconX(9));
			buttonClose.getButtonProperties().setIconForegroundEnabled(false);
			buttonClose.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
			buttonClose.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
			buttonClose.setMargin(new Insets(0, 0, 0, 0));
			buttonClose.setPreferredSize(new Dimension(9,9));
			buttonClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
							new WindowEvent(getWindow(), WindowEvent.WINDOW_CLOSING));
				}
			});
		}
		return buttonClose;
	}
	
	private LButton getButtonMaximize() {
		if (buttonMaximize == null) {
			buttonMaximize = new LButton(Constants.VOID);
			buttonMaximize.setOpaque(false);
			buttonMaximize.setIcon(ICON_MAXIMIZE);
			buttonMaximize.getButtonProperties().setIconForegroundEnabled(false);
			buttonMaximize.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
			buttonMaximize.setBorder(BorderFactory.createEmptyBorder());
			buttonMaximize.setMargin(new Insets(0, 0, 0, 0));
			buttonMaximize.setPreferredSize(getButtonClose().getPreferredSize());
			buttonMaximize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchMaximize();
				}
			});
		}
		return buttonMaximize;
	}
	
	private LButton getButtonMinimize() {
		if (buttonMinimize == null) {
			buttonMinimize = new LButton(Constants.VOID);
			buttonMinimize.setOpaque(false);
			buttonMinimize.setPreferredSize(getButtonMaximize().getPreferredSize());
			buttonMinimize.setIcon(ICON_MINIMIZE);
			buttonMinimize.getButtonProperties().setIconForegroundEnabled(false);
			buttonMinimize.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
			buttonMinimize.setBorder(BorderFactory.createEmptyBorder());
			buttonMinimize.setMargin(new Insets(0, 0, 0, 0));
			buttonMinimize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isMinimizeCompact()) {
						switchMinimize();
					}
					else {
						frame.setState(Frame.ICONIFIED);
					}
				}
			});
		}
		return buttonMinimize;
	}
	
	private void switchMinimize() {
		if (minimized) {
			Dimension restoreSize = maximized ? GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize() : restoreBounds.getSize();
			Point restoreLocation = getWindow().getLocation();
			restoreLocation.x -= (restoreSize.width - getWindow().getWidth());
			getWindow().setBounds(new Rectangle(restoreLocation, restoreSize));
		}
		else {
			
			if (!maximized || restoreBounds == null)
				restoreBounds = getWindow().getBounds();
			
			Dimension minimizedSize = getMinsize(true);
			Point minimizedLocation = getWindow().getLocation();
			minimizedLocation.x += (getWindow().getWidth() - minimizedSize.width);
			getWindow().setBounds(new Rectangle(minimizedLocation, minimizedSize));
			
			//Rectangle minimizedBounds = new Rectangle(restoreBounds.getLocation(), minimizedSize);
			//minimizedBounds.x += (restoreBounds.width - minimizedSize.width);
			//getWindow().setBounds(minimizedBounds);
		}
		minimized = !minimized;
		//buttonMinimize.setIcon(minimized ? ICON_RESTORE : ICON_MINIMIZE);
	}
	
	private void switchMaximize() {
		if (maximized) {
			getWindow().setBounds(restoreBounds);
		}
		else {
			if (!minimized || restoreBounds == null)
				restoreBounds = getWindow().getBounds();
			Rectangle maximizedBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			getWindow().setBounds(maximizedBounds);
		}
		minimized = false;
		maximized = !maximized;
		buttonMaximize.setIcon(maximized ? ICON_RESTORE : ICON_MAXIMIZE);
	}

	public RoundedBorder getDefaultRoundedBorder() {
		if (defaultRoundedBorder == null) {
			defaultRoundedBorder = new RoundedBorder();
			defaultRoundedBorder.setCornersCurveSize(new Dimension(20, 20));
		}
		return defaultRoundedBorder;
	}
	
	private Container getDefaultContentPane() {
		if (defaultContentPane == null) {
			LPanel panel = new LPanel();
			panel.setOpaque(false);
			defaultContentPane = panel;
		}
		return defaultContentPane;
	}
	private void setDefaultContentPane(Container defaultContentPane) {
		this.defaultContentPane = defaultContentPane;
	}
	
	public void setContentPane(Container contentPane) {
		if (defaultContentPane != null)
			getWindowElementsPanel().remove(defaultContentPane);
		setDefaultContentPane(contentPane);
		if (contentPane != null)
			getWindowElementsPanel().add(getDefaultContentPane(), BorderLayout.CENTER);
	}
	
	public Container getContentPane() {
		return getDefaultContentPane();
	}
	
	private void applyBGTransparency() {
		float opacity = 1 - getTransparency();
		boolean windowOpaque = opacity == 1 && !useContentWindowWhenOpaque;
		getWindowBorderPanel().setOpacity(opacity);
		//getWindowElementsPanel().setOpacity(opacity);
		UtilsGUI.setWindowOpaque(getWindow(), windowOpaque);
		comprobarContentWindow();
	}
	
	public void setTitle(String title) {
		getLabelTitle().setText(title);
		getLabelTitle().repaint();
	}
	
	public void setOpacity(float opacity) {
		setTransparency((int)((1-opacity)*100));
	}
	
	public Color getBackgroundContent() {
		if (backgroundContent == null)
			backgroundContent = getWindow().getBackground();
		return backgroundContent;
	}
	public void setBackgroundContent(Color backgroundContent) {
		this.backgroundContent = backgroundContent;
		getWindowBorderPanel().setBackground(backgroundContent);
		getWindowElementsPanel().setBackground(backgroundContent);
	}
	
	public boolean isResizable() {
		return frame != null && frame.isResizable() || dialog != null && dialog.isResizable();
	}
	
	public void updateResizable() {
		getButtonMaximize().setVisible(isMaximizable());
	}
	
	public Insets getLWindowInsets() {
		
		Insets insets = (Insets) getWindowBorderPanel().getInsets().clone();
		insets.top += getPanelNorth().getPreferredSize().height;
		
		return insets;
	}
	
	public boolean isMaximizable() {
		return maximizable && isResizable();
	}
	public boolean isMinimizable() {
		return minimizable;
	}
	public boolean isCloseable() {
		return closeable;
	}
	public boolean isMinimizeCompact() {
		return minimizeCompact || dialog != null;
	}
	
	public void setMaximizable(boolean maximizable) {
		this.maximizable = maximizable;
		getButtonMaximize().setVisible(isMaximizable());
	}
	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;
		getButtonMinimize().setVisible(isMinimizable());
	}
	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		getButtonClose().setVisible(isCloseable());
	}
	public void setMinimizeCompact(boolean minimizeCompact) {
		boolean oldValue = this.minimizeCompact;
		boolean newValue = minimizeCompact;
		if (oldValue != newValue) {
			//Restauramos la ventana antes de cambiar el tipo de minimize
			if (oldValue == true && minimized && restoreBounds != null)
				getWindow().setBounds(restoreBounds);
			this.minimizeCompact = minimizeCompact;
		}
	}
	
	public void setVisible(boolean b) {
		
		if (contentWindow != null)
			contentWindow.setVisible(b);
		//UtilsGUI.showDialogDarkenOwner(this);		
	}
}
