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
package linaje.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.UISupport;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.statics.Constants;
import linaje.utils.Files;
import linaje.utils.Lists;
import linaje.utils.Resources;
import sun.swing.SwingUtilities2;

/**
 * Centralizar los iconos del framework Linaje
 * 
 * Iconos dinámicos dónde podemos especificar tamaños, grosor, color, etc.
 * 	- Vacío
 *  - Flechas en todas direcciones
 *  - Barras horizontales y verticales - |
 *  - Signo +
 *  - Checkbox
 *  - Marca de checkbox en estilo normal y largo
 *  - Rectángulos
 *  
 * Utilidades:
 * 	- Obtener icono de recurso
 *  - Obtener iconos reescalados a cualquier tamaño
 *  - Obtener iconos e imágenes colorizados
 *  - Obtener imagen de un Icon (Si no tiene imagen devolverá una imagen vacía del tamaño del icono en lugar de fallar como imageIcon.getImage())
 * 
 **/
@SuppressWarnings("serial")
public final class Icons {

	private static final Color OBSCURE_BASE_COLOR = new Color(5, 5, 5);
	
	public static final String RESOURCE_DIR_IMAGES = "/images/";
	
	public static final int SIZE_ICONS = GeneralUIProperties.getInstance().getFontApp().getSize(); 
	public static final int SIZE_ICONS_CHECK_RADIO = SIZE_ICONS;
	
	public static final ImageIcon FOLDER_256x256 = getIcon("folder.png");
	public static final ImageIcon DOCUMENT_256x256 = getIcon("document.png");
	public static final ImageIcon DOCUMENT_WHITE_256x256 = getIcon("document_white.png");
	public static final ImageIcon NETWORK_256x256 = getIcon("network.png");
	public static final ImageIcon COMPUTER_256x256 = getIcon("computer.png");
	public static final ImageIcon DESKTOP_256x256 = getIcon("desktop.png");
	public static final ImageIcon FOLDER_NEW_256x256 = getIcon("folder_new.png");
	public static final ImageIcon FOLDER_UP_256x256 = getIcon("folder_up.png");
	
	public static final ImageIcon CALENDAR_48x48 = getIcon("calendar.png");
	public static final ImageIcon WINDOW_48x48 = getIcon("window.png");
	public static final ImageIcon FILTER_48x48 = getIcon("filter.png");
	
	public static final ImageIcon CONSOLE_48x48 = getIcon("console.png");
	
	public static final ImageIcon SAVE_48x48 = getIcon("save.png");
	public static final ImageIcon SEARCH_48x48 = getIcon("search.png");
	public static final ImageIcon UNDO_48x48 = getIcon("undo.png");
	public static final ImageIcon REDO_48x48 = getIcon("redo.png");
	
	public static final ImageIcon BOLD_48x48 = getIcon("bold.png");
	public static final ImageIcon ITALIC_48x48 = getIcon("italic.png");
	public static final ImageIcon UNDERLINE_48x48 = getIcon("underline.png");
	public static final ImageIcon CUT_48x48 = getIcon("cut.png");
	public static final ImageIcon COPY_48x48 = getIcon("copy.png");
	public static final ImageIcon PASTE_48x48 = getIcon("paste.png");
	
	public static final ImageIcon ARROW_UP_48x48 = getIcon("arrowUp.png");
	public static final ImageIcon ARROW_DOWN_48x48 = getIcon("arrowDown.png");
	public static final ImageIcon ARROW_LEFT_48x48 = getIcon("arrowLeft.png");
	public static final ImageIcon ARROW_RIGHT_48x48 = getIcon("arrowRight.png");
	
	public static final ImageIcon MESSAGE_WARNING_48x48 = getIcon("warning.png");
	public static final ImageIcon MESSAGE_ERROR_48x48 = getIcon("error.png");
	public static final ImageIcon MESSAGE_INFO_48x48 = getIcon("info.png");
	public static final ImageIcon MESSAGE_QUESTION_48x48 = getIcon("question.png");
	
	public static final ImageIcon CALENDAR = getScaledIcon(CALENDAR_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon WINDOW = getScaledIcon(WINDOW_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon FILTER = getScaledIcon(FILTER_48x48, SIZE_ICONS, SIZE_ICONS);
		
	public static final ImageIcon CONSOLE = getScaledIcon(Icons.CONSOLE_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon CONSOLE_ON = getColorizedIcon(CONSOLE, ColorsGUI.getColorPositive());
	public static final ImageIcon CONSOLE_OFF = getColorizedIcon(CONSOLE, ColorsGUI.getColorNegative());
	
	public static final ImageIcon FOLDER = getScaledIcon(FOLDER_256x256, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon FOLDER_NEW = getScaledIcon(FOLDER_NEW_256x256, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon FOLDER_UP = getScaledIcon(FOLDER_UP_256x256, SIZE_ICONS, SIZE_ICONS);

	public static final ImageIcon SAVE = getScaledIcon(SAVE_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon DOCUMENT = getScaledIcon(DOCUMENT_256x256, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon DOCUMENT_WHITE = getScaledIcon(DOCUMENT_WHITE_256x256, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon SEARCH = getScaledIcon(SEARCH_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon UNDO = getScaledIcon(UNDO_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon REDO = getScaledIcon(REDO_48x48, SIZE_ICONS, SIZE_ICONS);
	
	public static final ImageIcon NETWORK = getScaledIcon(NETWORK_256x256, SIZE_ICONS, SIZE_ICONS);
	
	public static final ImageIcon CONNECT_ON = getColorizedIcon(NETWORK, ColorsGUI.getColorPositive());
	public static final ImageIcon CONNECT_OFF = getColorizedIcon(NETWORK, ColorsGUI.getColorNegative());
	public static final ImageIcon CONNECTING = getColorizedIcon(NETWORK, ColorsGUI.getColorImportant());
	
	public static final ImageIcon BOLD = getScaledIcon(BOLD_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon ITALIC = getScaledIcon(ITALIC_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon UNDERLINE = getScaledIcon(UNDERLINE_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon CUT = getScaledIcon(CUT_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon COPY = getScaledIcon(COPY_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon PASTE = getScaledIcon(PASTE_48x48, SIZE_ICONS, SIZE_ICONS);
	
	public static final ImageIcon MESSAGE_WARNING = getColorizedIcon(MESSAGE_WARNING_48x48, ColorsGUI.getColorWarning());
	public static final ImageIcon MESSAGE_ERROR = getColorizedIcon(MESSAGE_ERROR_48x48, ColorsGUI.getColorNegative());
	public static final ImageIcon MESSAGE_INFO = getColorizedIcon(MESSAGE_INFO_48x48, ColorsGUI.getColorInfo());
	public static final ImageIcon MESSAGE_QUESTION = getColorizedIcon(MESSAGE_QUESTION_48x48, ColorsGUI.getColorInfo());
	
	public static final ImageIcon ARROW_UP = getScaledIcon(ARROW_UP_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon ARROW_DOWN = getScaledIcon(ARROW_DOWN_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon ARROW_LEFT = getScaledIcon(ARROW_LEFT_48x48, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon ARROW_RIGHT = getScaledIcon(ARROW_RIGHT_48x48, SIZE_ICONS, SIZE_ICONS);
	
	public static final ImageIcon DESKTOP = getScaledIcon(DESKTOP_256x256, SIZE_ICONS, SIZE_ICONS);
	public static final ImageIcon COMPUTER = getScaledIcon(COMPUTER_256x256, SIZE_ICONS, SIZE_ICONS);
	
	public static final ImageIcon SORT_ASCEN_TRASLUCENT = getIconArrow(SwingConstants.NORTH, true, true, false, Math.round(SIZE_ICONS*0.7f), Color.gray, 0.6f);
	public static final ImageIcon SORT_DESCEN_TRASLUCENT = getIconArrow(SwingConstants.SOUTH, true, true, false, Math.round(SIZE_ICONS*0.7f), Color.gray, 0.7f);//Lo hacemos un poco mas transparente que el ascendente
	public static final ImageIcon SORT_ASCEN = getIconArrow(SwingConstants.NORTH, true, true, false, SIZE_ICONS, Color.black, 0f);
	public static final ImageIcon SORT_DESCEN = getIconArrow(SwingConstants.SOUTH, true, true, false, SIZE_ICONS, Color.black, 0f);
		
	public static final ImageIcon ACCEPT = Icons.getIconCheckMark(SIZE_ICONS, false, null);
	public static final ImageIcon CANCEL = Icons.getIconX(SIZE_ICONS, 2, null);
	public static final ImageIcon EXPAND = Icons.getIconPlus(8, 2, null);
	public static final ImageIcon COLLAPSE = Icons.getIconBarHoriz(8, 2, null);
	
	public static final StateIcon STATEICON_CHECK_ON
	= new StateIcon(getIconCheckBox(true, true, false),
					getIconCheckBox(true, false, false),
					null,
					getIconCheckBox(true, true, true),
					null);
	
	public static final StateIcon STATEICON_CHECK_OFF
	= new StateIcon(getIconCheckBox(false, true, false),
					getIconCheckBox(false, false, false),
					null,
					getIconCheckBox(false, true, true),
					null);
	
	public static final StateIcon STATEICON_ARROW_UP
	= new StateIcon(getIconArrow(SwingConstants.NORTH, true, true, false),
					getIconArrow(SwingConstants.NORTH, true, false, false),
					null,
					getIconArrow(SwingConstants.NORTH, true, true, true),
					null);
	
	public static final StateIcon STATEICON_ARROW_DOWN
	= new StateIcon(getIconArrow(SwingConstants.SOUTH, true, true, false),
					getIconArrow(SwingConstants.SOUTH, true, false, false),
					null,
					getIconArrow(SwingConstants.SOUTH, true, true, true),
					null);
	
	public static final StateIcon STATEICON_ARROW_LEFT
	= new StateIcon(getIconArrow(SwingConstants.WEST, true, true, false),
					getIconArrow(SwingConstants.WEST, true, false, false),
					null,
					getIconArrow(SwingConstants.WEST, true, true, true),
					null);
	
	public static final StateIcon STATEICON_ARROW_RIGHT
	= new StateIcon(getIconArrow(SwingConstants.EAST, true, true, false),
					getIconArrow(SwingConstants.EAST, true, false, false),
					null,
					getIconArrow(SwingConstants.EAST, true, true, true),
					null);
	
	public static final StateIcon STATEICON_VOID
	= new StateIcon(getIconVoid(),
			getIconVoid(),
			getIconVoid(),
			null,
			getIconVoid());
	
	public static ImageIcon getIconCheckBox(final boolean selected, final boolean enabled, final boolean over) {
		
		ImageIcon icon = new ImageIcon() {
				
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Color bgColor = enabled ? (over ? ColorsGUI.getColorRollover() : null) : UIManager.getColor("CheckBox.background");
				Color markColor = enabled ? null : UIManager.getColor("CheckBox.shadow");
				GraphicsUtils.paintCheckBox(g, x, y, bgColor, selected, markColor, true);
			}
			
			@Override
			public int getIconWidth() {
				return SIZE_ICONS_CHECK_RADIO;
			}
			
			@Override
			public int getIconHeight() {
				return SIZE_ICONS_CHECK_RADIO;
			}
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(SIZE_ICONS_CHECK_RADIO, SIZE_ICONS_CHECK_RADIO, null);
				GraphicsUtils.paintCheckBox(image.getGraphics(), 0, 0, UIManager.getColor("CheckBox.background"), selected, UIManager.getColor("CheckBox.shadow"), true);
				return image;
			}
		};
		
		return icon;
	}
	
	public static ImageIcon getIconArrow(final int direction, final boolean selected, final boolean enabled, final boolean over) {
		Color arrowColor = enabled ? null : UIManager.getColor("CheckBox.shadow");
		return getIconArrow(direction, selected, enabled, over, Math.round(SIZE_ICONS*0.7f), arrowColor, 0);
	}
	public static ImageIcon getIconArrow(final int direction, final boolean selected, final boolean enabled, final boolean over, final int size, final Color color, final float transparency) {
		
		ImageIcon icon = new ImageIcon() {
				
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				
				Graphics2D g2d = (Graphics2D) g.create();
				try {
					
					if (transparency > 0) {
						float alpha = 1 - transparency;
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
						AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
						g2d.setComposite(alphaComposite);
					}
				
					Color arrowColor = color != null ? color : c != null ? c.getForeground() : null;
					GraphicsUtils.paintTriangle(g2d, x, y, size, enabled, arrowColor, direction, over);
				}
				finally {
					g2d.dispose();
				}
				
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(size, size, null);
				paintIcon(null, image.getGraphics(), 0, 0);
				//GraphicsUtils.paintTriangle(image.getGraphics(), 0, 0, size, enabled, color, direction, over);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconX(final int size) {
		return getIconX(size, 1, null);
	}
	public static PaintedImageIcon getIconX(final int size, final int thickness, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
				
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintX(g, x, y, size, thickness);
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintX(g, 0, 0, size, thickness);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconPlus(final int size) {
		return getIconPlus(size, 1, null);
	}
	public static PaintedImageIcon getIconPlus(final int size, final int thickness, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintPlus(g, x, y, size, thickness);
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintPlus(g, 0, 0, size, thickness);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconBarHoriz(final int size) {
		return getIconBarHoriz(size, 1, null);
	}
	public static PaintedImageIcon getIconBarHoriz(final int size, final int thickness, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintBarHoriz(g, x, y, size, thickness);
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintBarHoriz(g, 0, 0, size, thickness);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconBarVert(final int size) {
		return getIconBarVert(size, 1, null);
	}
	public static PaintedImageIcon getIconBarVert(final int size, final int thickness, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintBarVert(g, x, y, size, thickness);
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintBarVert(g, 0, 0, size, thickness);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconCheckMark(final int size, final boolean extended, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
				
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintCheckMark(g, x, y, getCurrentColor(), size, extended);
			}
			
			@Override
			public int getIconWidth() {
				return size;
			}
			
			@Override
			public int getIconHeight() {
				return size;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintCheckMark(g, 0, 0, g.getColor(), size, extended);
				return image;
			}
		};
		
		return icon;
	}
	
	public static PaintedImageIcon getIconRectangle(final int width, final int height) {
		return getIconRectangle(width, height, 1, null);
	}
	public static PaintedImageIcon getIconRectangle(final int width, final int height, final int thickness, final Color color) {
		
		PaintedImageIcon icon = new PaintedImageIcon(color) {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.paintIcon(c, g, x, y);
				GraphicsUtils.paintRectangle(g, x, y, width, height, thickness);
			}
			
			@Override
			public int getIconWidth() {
				return width;
			}
			
			@Override
			public int getIconHeight() {
				return height;
			}
			
			@Override
			public Image getImage() {
				BufferedImage image = Icons.createImage(getIconWidth(), getIconHeight(), null);
				Graphics g = image.getGraphics();
				g.setColor(getCurrentColor());
				GraphicsUtils.paintRectangle(g, 0, 0, getIconWidth(), getIconHeight(), thickness);
				return image;
			}
		};
		
		return icon;
	}
	
	public static ImageIcon getIconVoid() {
		return getIconVoid(SIZE_ICONS, SIZE_ICONS);
	}
	
	public static ImageIcon getIconVoid(final int width, final int height) {
		
		ImageIcon iconVoid = new ImageIcon() {
						
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
			}
			
			@Override
			public int getIconWidth() {
				return width;
			}
			
			@Override
			public int getIconHeight() {
				return height;
			}
			
			public Image getImage() {
				BufferedImage image = Icons.createImage(width, height, null);
				return image;
			}
		};
		
		return iconVoid;
	}
	
	public static ImageIcon getScaledIcon(ImageIcon imageIcon, float widthScale, float heightScale) {
		
		int width = Math.round(imageIcon.getIconWidth()*widthScale);
		int height = Math.round(imageIcon.getIconHeight()*heightScale);
	
		return getScaledIcon(imageIcon, width, height);
	}
	public static ImageIcon getScaledIcon(ImageIcon imageIcon, int width, int height) {
		
		Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		
		return new ImageIcon(scaledImage);
	}
	
	public static Image getImage(Icon icon) {
		return getImage(icon, null);
	}
	public static Image getImage(Icon icon, Component c) {
		Image image = null;
		if (icon instanceof PaintedImageIcon)
			image = ((PaintedImageIcon) icon).getImage(c);
		if (icon instanceof ImageIcon)
			image = ((ImageIcon) icon).getImage();
			
		if (image == null) {
			image = Icons.createImage(Math.max(1, icon.getIconWidth()), Math.max(1, icon.getIconHeight()), null);
			icon.paintIcon(c, image.getGraphics(), 0, 0);
		}
		return image;
	} 
	
	/**
	 * Este icono se asigna al calcular el preferredSize 
	 * y se usa para que el botón no se haga mas alto cuando el icono es mas alto que la fuente
	 */
	public static Icon getFixedHeithIcon(AbstractButton b) {
		return getFixedHeithIcon(b.getIcon(), b.getFont().getSize());
	}
	public static Icon getFixedHeithIcon(Icon icon, int fixedHeight) {
		return icon != null ? getEmptyIcon(icon.getIconWidth(), fixedHeight) : null;
	}
	
	public static Icon getEmptyIcon(final int width, final int height) {
		
		Icon emptyIcon = new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
			}
			@Override
			public int getIconWidth() {
				return width;
			}
			@Override
			public int getIconHeight() {
				return height;
			}
		};
		
		return emptyIcon;
	}
	
	public static ImageIcon getResourceIcon(String resourceName) {
		URL urlIcon = Resources.getResourceURL(resourceName);
		return urlIcon != null ? new ImageIcon(urlIcon) : new ImageIcon(Constants.VOID);
	}
	
	public static ImageIcon getIcon(String imageName) {
		return getResourceIcon(RESOURCE_DIR_IMAGES + imageName);
	}
	
	public static ImageIcon getIcon(File dirImages, String imageName) {
		return imageName != null ? new ImageIcon(dirImages.getAbsolutePath() + Files.FILE_SEPARATOR +  imageName) : null;
	}
	
	public static ImageIcon getColorizedIcon(ImageIcon baseIcon, Color color) {
		
		ImageIcon colorizedIcon = baseIcon;
		if (baseIcon != null && baseIcon.getImage() != null && color != null) {
			Image colorizedImage = createColorizedImage(baseIcon.getImage(), color);
			colorizedIcon = new ImageIcon(colorizedImage);
		}
		return colorizedIcon;
	}
	
	public static Image createColorizedImage(Image image, Color color) {
		return createColorizedImage(image, color, false);
	}
	public static Image createColorizedImage(Image image, Color color, boolean obscureImageFirst) {
		Image baseImage = obscureImageFirst ? createColorizedImage(image, OBSCURE_BASE_COLOR) : image;
    	ColorizedImageFilter filter = new ColorizedImageFilter(color, AppGUI.getCurrentAppGUI().isOptimizeNonPlainImagesColorize());
        ImageProducer prod = new FilteredImageSource(baseImage.getSource(), filter);
        Image colorizedImage = Toolkit.getDefaultToolkit().createImage(prod);
        return colorizedImage;
    }

	public static BufferedImage createImage(int width, int height, Color background) {
	
		BufferedImage bufferedImage = new BufferedImage(width, height, background == null ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		if (background != null) {
		    for (int x = 0; x < bufferedImage.getWidth(); x++) {
		        for (int y = 0; y < bufferedImage.getHeight(); y++) {
		            bufferedImage.setRGB(x, y, background.getRGB());
		        }
		    }
		}
	    return bufferedImage;
	}

	/**
	 * Crea una imagen de un texto con sombra
	 **/
	public static BufferedImage createImageText(String text, FontMetrics fm, Color foreground, Color background, Insets insets, boolean squareImage) {
		
		if (insets == null)
			insets = new Insets(0, 0, 0, 0);
		if (foreground == null)
			foreground = Color.black;
		if (text == null)
			text = Constants.VOID;
		
		Rectangle textViewRect = new Rectangle();
		List<Rectangle> textLinesBounds = Lists.newList();
		List<Point> offsets = Lists.newList();
		
		int wText = fm.stringWidth(text) + 1;//+1 de la posible sombra
		int hText = fm.getHeight();
		int width = wText + insets.left + insets.right;
		int height = hText + insets.top + insets.bottom;
		if (squareImage) {
			width = Math.max(width, height);
			height = width;
		}
		
		Rectangle viewRect = new Rectangle(0, 0, width, height);
		UISupport.layoutText(viewRect, textViewRect, null, textLinesBounds, null, offsets, text, fm, SwingConstants.CENTER, SwingConstants.CENTER);
		
		//Creamos la imagen con el tamaño del texto mas el margen del descent
		BufferedImage imageText = createImage(width, height, background);
	
		//Escribimos el texto en la imagen
	    Graphics2D g2d = imageText.createGraphics();
	    g2d.setFont(fm.getFont());
	    g2d.setColor(foreground);
	    
	    boolean isShadowTextEnabled = true;
	    int shadowPosition = SwingConstants.SOUTH_EAST;
	    boolean underline = false;
	    Color underlineColor = null;
	    UISupport.paintText(g2d, foreground, background, textViewRect, textLinesBounds, offsets, text, true, 0, isShadowTextEnabled, shadowPosition, underline, underlineColor);
	    
	    g2d.dispose();
	    
	    return imageText;
	}

	/**
	 * Crea una imagen de un texto normal
	 **/
	public static BufferedImage createImageTextClassic(String text, Font font, Color foreground, Color background) {
		return createImageTextClassic(text, font, foreground, background, null, false);
	}

	public static BufferedImage createImageTextClassic(String text, Font font, Color foreground, Color background, Insets insets, boolean squareImage) {
		
		if (insets == null)
			insets = new Insets(0, 0, 0, 0);
		if (foreground == null)
			foreground = Color.black;
		if (text == null)
			text = Constants.VOID;
		
		FontMetrics fm = SwingUtilities2.getFontMetrics(null, font);
		int wText = fm.stringWidth(text);
		int hText = fm.getHeight();
		if (wText == 0)
			wText = 1;
		if (hText == 0)
			hText = 1;
		
		int width = wText + insets.left + insets.right;
		int height = hText + insets.top + insets.bottom;
		
		int x = insets.left;
		int y = insets.top + fm.getAscent();
		
		if (squareImage) {
			width = Math.max(width, height);
			height = width;
			x = (width - wText) / 2;
			x += insets.left - insets.right;
			y = ((height - hText) / 2) + fm.getAscent();
			y += insets.top - insets.bottom;
		}
		
		//Creamos la imagen con el tamaño del texto mas el margen del descent
		BufferedImage imageText = createImage(width, height, background);
	
		//Escribimos el texto en la imagen
	    Graphics2D g2D = imageText.createGraphics();
	    g2D.setFont(font);
	    g2D.setColor(foreground);
	    GraphicsUtils.drawString(g2D, text, x, y);
	    
	    g2D.dispose();
	    
	    return imageText;
	}

	public static BufferedImage createImageShadow(Color shadowColor, Color background, int width, int height, int gradientSize) {
		return createImageShadow(shadowColor, background, width, height, gradientSize, 0);
	}

	public static BufferedImage createImageShadow(Color shadowColor, Color background, int width, int height, int gradientSize, int innerArcSize) {
		
		BufferedImage imageShadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		if (width == 0)
			width = 4;
		if (height == 0)
			height = 4;
		
		Graphics2D g2d = imageShadow.createGraphics();
	    
	    Color darkColor = shadowColor;
	    //No podemos usar colores transparentes porque en ese caso hace el degradado de otra forma que no nos vale
		Color lightColor = background;//new Color(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue(), 0);
		
		Rectangle boundsImage = new Rectangle(0, 0, width, height);
		g2d.setColor(darkColor);
		g2d.fillRoundRect(boundsImage.x, boundsImage.y, boundsImage.width, boundsImage.height, innerArcSize, innerArcSize);
		
		final int MAX_GRADIENT_SIZE = Math.min(boundsImage.width/2, boundsImage.height/2);
		if (gradientSize > MAX_GRADIENT_SIZE)
			gradientSize = MAX_GRADIENT_SIZE;
		
		Rectangle boundsShadowBottom = new Rectangle(boundsImage.x, boundsImage.y + boundsImage.height - gradientSize, boundsImage.width, gradientSize);
		Rectangle boundsShadowTop = new Rectangle(boundsImage.x, boundsImage.y, boundsImage.width, gradientSize);
		Rectangle boundsShadowRight = new Rectangle(boundsImage.x + boundsImage.width - gradientSize, boundsImage.y, gradientSize, boundsImage.height);
		Rectangle boundsShadowLeft = new Rectangle(boundsImage.x, boundsImage.y, gradientSize, boundsImage.height);
				
		Point2D topOfBorderBottom = new Point2D.Float(boundsShadowBottom.x + (float) boundsShadowBottom.getWidth() / 2.0f, (float) boundsShadowBottom.y);
		Point2D bottomOfBorderBottom = new Point2D.Float(boundsShadowBottom.x + (float) boundsShadowBottom.getWidth() / 2.0f, (float) boundsShadowBottom.y + boundsShadowBottom.height);
		
		Point2D topOfBorderTop = new Point2D.Float(boundsShadowTop.x + (float) boundsShadowTop.getWidth() / 2.0f, (float) boundsShadowTop.y);
		Point2D bottomOfBorderTop = new Point2D.Float(boundsShadowTop.x + (float) boundsShadowTop.getWidth() / 2.0f, (float) boundsShadowTop.y + boundsShadowTop.height);
		
		Point2D leftOfBorderRight = new Point2D.Float(boundsShadowRight.x , (float) boundsShadowRight.getHeight() / 2.0f);
		Point2D rightOfBorderRight = new Point2D.Float(boundsShadowRight.x + (float) boundsShadowRight.width, (float) boundsShadowRight.getHeight() / 2.0f);
		
		Point2D leftOfBorderLeft = new Point2D.Float(boundsShadowLeft.x , (float) boundsShadowLeft.getHeight() / 2.0f);
		Point2D rightOfBorderLeft = new Point2D.Float(boundsShadowLeft.x + (float) boundsShadowLeft.width, (float) boundsShadowLeft.getHeight() / 2.0f);
		
		GradientPaint gradientPaint = new GradientPaint(topOfBorderBottom, darkColor, bottomOfBorderBottom, lightColor);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(boundsShadowBottom.x, boundsShadowBottom.y, boundsShadowBottom.width, boundsShadowBottom.height);
		
		gradientPaint = new GradientPaint(topOfBorderTop, lightColor, bottomOfBorderTop, darkColor);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(boundsShadowTop.x, boundsShadowTop.y, boundsShadowTop.width, boundsShadowTop.height);
		
		gradientPaint = new GradientPaint(leftOfBorderRight, darkColor, rightOfBorderRight, lightColor);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(boundsShadowRight.x, boundsShadowRight.y, boundsShadowRight.width, boundsShadowRight.height);
		
		gradientPaint = new GradientPaint(leftOfBorderLeft, lightColor, rightOfBorderLeft, darkColor);
		g2d.setPaint(gradientPaint);
		g2d.fillRect(boundsShadowLeft.x, boundsShadowLeft.y, boundsShadowLeft.width, boundsShadowLeft.height);
		
		//lado²+lado²=diagonal²
		//diagonal=v¨¨(lado²+lado²)
		//lado=v¨¨(diagonal²/2)
		//double diagonal = tamanoDegradado;
		//double lado = Math.sqrt((Math.pow(diagonal, 2)/2));
		//Point2D rgPoint = new Point2D.Float((float)getWidth() - (float)lado, (float) getHeight() - (float)lado);			
				
		Rectangle boundsCornerTopLeft = new Rectangle(boundsImage.x, boundsImage.y, gradientSize, gradientSize);
		Rectangle boundsCornerTopRight = new Rectangle(boundsImage.x + boundsImage.width-gradientSize, boundsImage.y, gradientSize, gradientSize);
		Rectangle boundsCornerBottomLeft = new Rectangle(boundsImage.x, boundsImage.y + boundsImage.height-gradientSize, gradientSize, gradientSize);
		Rectangle boundsCornerBottomRight = new Rectangle(boundsImage.x + boundsImage.width-gradientSize, boundsImage.y + boundsImage.height-gradientSize, gradientSize, gradientSize);
		
		float[] dist = { 0.0f, 1.0f };
		Color[] colors = { darkColor, lightColor };
		
		Point2D rgPointTopLeft = new Point2D.Float((float)boundsCornerTopLeft.x + boundsCornerTopLeft.width, (float) boundsCornerTopLeft.y + boundsCornerTopLeft.height);
		RadialGradientPaint radialPaint = new RadialGradientPaint(rgPointTopLeft, (float) gradientSize, dist, colors);
		g2d.setPaint(radialPaint);
		g2d.fillRect(boundsCornerTopLeft.x, boundsCornerTopLeft.y, boundsCornerTopLeft.width, boundsCornerTopLeft.height);
		
		Point2D rgPointTopRight = new Point2D.Float((float)boundsCornerTopRight.x, (float) boundsCornerTopRight.y + boundsCornerTopRight.height);
		radialPaint = new RadialGradientPaint(rgPointTopRight, (float) gradientSize, dist, colors);
		g2d.setPaint(radialPaint);
		g2d.fillRect(boundsCornerTopRight.x, boundsCornerTopRight.y, boundsCornerTopRight.width, boundsCornerTopRight.height);
		
		Point2D rgPointBottomLeft = new Point2D.Float((float)boundsCornerBottomLeft.x + boundsCornerBottomLeft.width, (float) boundsCornerBottomLeft.y);
		radialPaint = new RadialGradientPaint(rgPointBottomLeft, (float) gradientSize, dist, colors);
		g2d.setPaint(radialPaint);
		g2d.fillRect(boundsCornerBottomLeft.x, boundsCornerBottomLeft.y, boundsCornerBottomLeft.width, boundsCornerBottomLeft.height);
		
		Point2D rgPointBottoRight = new Point2D.Float((float)boundsCornerBottomRight.x, (float) boundsCornerBottomRight.y);
		radialPaint = new RadialGradientPaint(rgPointBottoRight, (float) gradientSize, dist, colors);
		g2d.setPaint(radialPaint);
		g2d.fillRect(boundsCornerBottomRight.x, boundsCornerBottomRight.y, boundsCornerBottomRight.width, boundsCornerBottomRight.height);
		
		g2d.dispose();
			
	    return imageShadow;
	}
}
