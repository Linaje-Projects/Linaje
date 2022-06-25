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

/**
 *
 * Además de las propiedades de un JPanel tiene las siguientes propiedades:
 *
 *  - Asignar la opacidad/Transparencia del panel
 *  - Posibilidad de fondo degradado
 *  - Permite asignar  una imagen de fondo y su alineación
 *  - Permite asignar un texto de fondo y su alineación
 * 
 * Creation date: (13/08/2004 12:12:27)
 * @author: Pablo Linaje
 */
import java.awt.*;

import javax.swing.*;

import linaje.gui.utils.GraphicsUtils;
import linaje.gui.utils.UISupportUtils;
import linaje.gui.utils.UtilsGUI;
import linaje.logs.Console;

@SuppressWarnings("serial")
public class LPanel extends JPanel {
	
	private Icon imageBackground = null;
	private String textBackground = null;
	
	private int imageAlignment = SwingConstants.CENTER;
	private int textAlignment = SwingConstants.CENTER;
	
	private Insets imageInsets = null;
	private Insets textInsets = null;
	
	private boolean adjustImageBackground = false;
	private boolean gradientBackground = false;
	private boolean adjustFontToPanelHeight = false;
	private Color secondaryGradientColor = null;
	
	private float opacity = 1f;

	public LPanel() {
		super();
	}
	public LPanel(LayoutManager layout) {
		super(layout);
	}
	public LPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}
	public LPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}
	
	public LPanel(String text) {
		super();
		setTextBackground(text);
	}
	public LPanel(String text, int textAlignment) {
		super();
		setTextBackground(text);
		setTextAlignment(textAlignment);
	}
	public LPanel(String text, int textAlignment, Icon imageBackground, int imageAlignment) {
		super();
		setTextBackground(text);
		setTextAlignment(textAlignment);
		setImageBackground(imageBackground);
		setImageAlignment(imageAlignment);
	}
	public LPanel(String text, Icon imageBackground) {
		super();
		setTextBackground(text);
		setImageBackground(imageBackground);
	}
	public LPanel(Icon imageBackground) {
		super();
		setImageBackground(imageBackground);
	}
	public LPanel(Icon imageBackground, int imageAlignment) {
		super();
		setImageBackground(imageBackground);
		setImageAlignment(imageAlignment);
	}
	
	public int getTextWidth(String text) {  
		return UtilsGUI.getStringWidth(text, getAdjustedFontToPanel(), this);
	}
	
	public Color getSecondaryGradientColor() {
		return secondaryGradientColor;
	}
	public Insets getBorderInsets() {
	
		Insets borderInsets = new Insets(0, 0, 0, 0);
		if (getBorder() != null)
			borderInsets = getBorder().getBorderInsets(this);
	
		return borderInsets;
	}
	
	protected Font getAdjustedFontToPanel() {
	
		if (isAdjustFontToPanelHeight() && getHeight() > 0) {
			
			Insets insets = getInsets();
			int fontSize = getHeight() - insets.top - insets.bottom;
			
			if (fontSize < 4)
				fontSize = 4;
	
			return new Font(getFont().getName(), getFont().getStyle(), fontSize);
		}
		else {
			
			return getFont();
		}
	}
	
	public int getImageAlignment() {
		return imageAlignment;
	}
	public Insets getImageInsets() {
		return imageInsets;
	}
	public Icon getImageBackground() {
		return imageBackground;
	}
	public int getTextAlignment() {
		return textAlignment;
	}
	public Insets getTextInsets() {
		return textInsets;
	}
	public String getTextBackground() {
		return textBackground;
	}
	public boolean isAdjustFontToPanelHeight() {
		return adjustFontToPanelHeight;
	}
	public boolean isAdjustImageBackground() {
		return adjustImageBackground;
	}
	public boolean isGradientBackground() {
		return gradientBackground;
	}
	
	public void paintComponent(Graphics g) {
	
		super.paintComponent(g);
		
		paintGradientBackground(g);
		paintImage(g);
		paintText(g);
	}
	
	private void paintGradientBackground(Graphics g) {
	
		if ((isGradientBackground() && isOpaque()) || (getOpacity() < 1 && getOpacity() > 0)) {
			
			//Oscurecemos
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
				if (isGradientBackground())
					GraphicsUtils.paintGradientBackground(g, this, getBackground(), getSecondaryGradientColor());
				else
					GraphicsUtils.paintBackground(g2d, this, getBackground());
			}
			finally {
				g2d.dispose();
			}
		}
	}
	
	private void paintImage(Graphics g) {
	
		if (getImageBackground() != null) {
			//Pintamos la imagen de fondo
			try {
	
				int imageWidht = getImageBackground().getIconWidth();
				int imageHeight = getImageBackground().getIconHeight();
				
				Rectangle imageRects = new Rectangle(0, 0, imageWidht, imageHeight);
				
				if (isAdjustImageBackground()) {
	
					//Expandimos la imagen a todo el panel
					
					boolean keepImageRatio = true;
					if (keepImageRatio) {
						
						boolean fillAll = true;
						
						float widthPercentChanged = getWidth()/(float)imageWidht;
						float heightPercentChanged = getHeight()/(float)imageHeight;
						
						boolean adjustHeight = fillAll ? heightPercentChanged > widthPercentChanged : heightPercentChanged < widthPercentChanged;
						if (adjustHeight) {
							//Ajustamos a lo alto y centramos por los lados
							imageRects.height = getHeight();
							imageRects.width = (int) (imageWidht*heightPercentChanged);
							imageRects.x = (getWidth() - imageRects.width)/2;
						}
						else {
							//Ajustamos a lo ancho y centramos por arriba y por abajo
							imageRects.width = getWidth();
							imageRects.height = (int) (imageHeight*widthPercentChanged);
							imageRects.y = (getHeight() - imageRects.height)/2;
						}
					}	
				}
				else {
					//Obtenemos las coordenadas de la imagen según la alineación
					Point location = UISupportUtils.getLocation(this, getImageBackground(), getImageAlignment(), getImageInsets());
					imageRects.setLocation(location);	
				}
				
				g.drawImage(Icons.getImage(getImageBackground()), imageRects.x, imageRects.y, imageRects.width, imageRects.height, this);
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
		}
	}
	
	private void paintText(Graphics g) {
	
		if (getTextBackground() != null) {
			//Pintamos el texto de fondo
			try {
	
				//Obtenemos las coordenadas de la imagen según la alineación
				Font font = getAdjustedFontToPanel();
				FontMetrics fm = getFontMetrics(font);
				Point location = UISupportUtils.getLocation(this, getTextBackground(), fm, getTextAlignment(), getTextInsets());
				g.setFont(font);
				g.setColor(getForeground());
				GraphicsUtils.drawString(g, getTextBackground(), location.x, location.y);
			}
			catch (Throwable ex) {
				Console.printException(ex);
			}
		}
	}
	
	public void setAdjustFontToPanelHeight(boolean newAjustarFuenteAltoPanel) {
		adjustFontToPanelHeight = newAjustarFuenteAltoPanel;
	}
	public void setAdjustImageBackground(boolean newAjustarImagenBackground) {
		adjustImageBackground = newAjustarImagenBackground;
	}
	public void setSecondaryGradientColor(Color newBackground2) {
		secondaryGradientColor = newBackground2;
	}
	public void setGradientBackground(boolean newFondoDegradado) {
		gradientBackground = newFondoDegradado;
	}
	public void setImageBackground(Icon newImageBackground) {
		imageBackground = newImageBackground;
	}
	public void setTextBackground(String newTextBackground) {
		textBackground = newTextBackground;
	}
	
	public void setImageAlignment(int imageAlignment) {
		this.imageAlignment = imageAlignment;
	}
	public void setTextAlignment(int textAlignment) {
		this.textAlignment = textAlignment;
	}
	public void setImageInsets(Insets imageInsets) {
		this.imageInsets = imageInsets;
	}
	public void setTextInsets(Insets textInsets) {
		this.textInsets = textInsets;
	}
	
	public float getOpacity() {
		return isOpaque() ? 1f : opacity;
	}
	public void setOpacity(float opacity) {
		if (opacity < 0 || opacity > 1)
			opacity = 1f;
		
		this.opacity = opacity;
		setOpaque(opacity == 1);
	}
}
