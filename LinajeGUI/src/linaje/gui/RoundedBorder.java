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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.border.AbstractBorder;

import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.UISupportButtons;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.logs.Console;
import linaje.utils.Colors;

/**
 * Es un roundedBorder que permite:
 * 	- Cambiar la curvatura de las esquinas
 * 	- Colorear cada uno de los cuatro lados de un color distinto
 *  - Sombra semitransparente pintada dinámicamente
 *  - Asignar el grosor, color y transparencia de la sombra (si asignamos thicknessShadow=0 no se pintará la sombra)
 *  - Permite elegir si queremos una sombra degradada o plana
 *  - Permite asignar una imagen de sombra por si se prefiere a la pintada dinámicamente
 *  - También permite pintar sombra interna
 **/
@SuppressWarnings("serial")
public class RoundedBorder extends AbstractBorder {

	public static int MIN_TRANSPARENCY = 0;
	public static int MAX_TRANSPARENCY = 95;
	
	private Dimension cornersCurveSize = new Dimension(4, 4);
    private int shadowTransparency = 80;//Porcentaje de transparencia
    private int thicknessInsetsExtra = 0;
    private int thicknessLineBorder = 1;
    private int thicknessShadow = 5;
    private int thicknessInnerShadow = 0;
    private Color lineBorderColor = null;
    private Color shadowColor = null;
    
    private boolean paintInsideAlways = true;
    private boolean gradientShadow = true;
    private File shadowImageFile = null;
    
    private Color topColor = null;
    private Color leftColor = null;
    private Color bottomColor = null;
    private Color rightColor = null;

	public RoundedBorder() {
		super();
	}
	public RoundedBorder(boolean shadowPainted) {
		super();
		if (!shadowPainted)
			setThicknessShadow(0);
	}
	public RoundedBorder(Color lineBorderColor) {
		super();
		setLineBorderColor(lineBorderColor);
	}
	public RoundedBorder(boolean shadowPainted, Color lineBorderColor) {
		super();
		if (!shadowPainted)
			setThicknessShadow(0);
		setLineBorderColor(lineBorderColor);
	}
	public RoundedBorder(boolean shadowPainted, Color lineBorderColor, int thicknessLineBorder) {
		super();
		if (!shadowPainted)
			setThicknessShadow(0);
		setLineBorderColor(lineBorderColor);
		setThicknessLineBorder(thicknessLineBorder);
	}
	
	@Override
	public Insets getBorderInsets(Component c) {
		
		return getBorderInsets(c, new Insets(0,0,0,0));
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		
		int minWidth = getCornersCurveSize().width/6;
		int minHeight = getCornersCurveSize().height/6;
		if (minWidth < 1)
			minWidth = 1;
		if (minHeight < 1)
			minHeight = 1;
		
		int thicknessLineBorder = getThicknessLineBorder();
    	int thicknessShadow = getThicknessShadow();
    	int thicknessInsetsExtra = getThicknessInsetsExtra();
		insets.left = minWidth + thicknessInsetsExtra + thicknessLineBorder;
		insets.right = minWidth + thicknessInsetsExtra + thicknessLineBorder + thicknessShadow;
		insets.top = minHeight + thicknessInsetsExtra + thicknessLineBorder;
		insets.bottom = minHeight + thicknessInsetsExtra + thicknessLineBorder + thicknessShadow;
		return insets;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		
		paintBorderMain(c, g, x, y, width, height);
	}

	private void paintBorderMain(Component c, Graphics g, int x, int y, int width, int height) {

		Dimension arcs = getCornersCurveSize();
		double arcW = arcs.getWidth();
		double arcH = arcs.getHeight();

		int thicknessLineBorder = getThicknessLineBorder();
		int thicknessShadow = getThicknessShadow();

		Rectangle boundsBorder = new Rectangle(x, y, width - thicknessLineBorder - thicknessShadow, height - thicknessLineBorder - thicknessShadow);
		//Rectangle boundsInterior = new Rectangle(boundsBorder.x + thicknessLineBorder, boundsBorder.y + thicknessLineBorder, boundsBorder.width - thicknessLineBorder, boundsBorder.height - thicknessLineBorder);

		int posOffset = 0;
		int dimOffset = 0;
		
		if (thicknessLineBorder > 0) {
			posOffset = thicknessLineBorder - Math.max(thicknessLineBorder-2, 1);
			dimOffset = -posOffset + Math.max(thicknessLineBorder-2, 0);
		}
		
		Rectangle boundsInterior = new Rectangle(boundsBorder.x + posOffset, boundsBorder.y + posOffset, boundsBorder.width + dimOffset, boundsBorder.height + dimOffset);
		
		Insets borderInsets = getBorderInsets(c);
		Area areaInterior = new Area(new RoundRectangle2D.Double(boundsInterior.x, boundsInterior.y, boundsInterior.width, boundsInterior.height, arcW, arcH));
		Area areaComponent = new Area(new Rectangle2D.Double(x + borderInsets.left, y + borderInsets.top, width - borderInsets.left - borderInsets.right, height - borderInsets.top - borderInsets.bottom));
		if (width > borderInsets.left + borderInsets.right && height > borderInsets.top + borderInsets.bottom) {
			// Al areaInterior le restamos el area del componente
			areaInterior.subtract(areaComponent);
		}

		Graphics2D g2d = (Graphics2D) g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
			if (c.isOpaque() && c.getParent() != null) {
				//Si el componente no es transparente, se pintarían artefactos en el borde transparente semitransparente de la sombra, por lo que antes rellenamos el fondo de la sombra con el color del primer padre que sea opaco
				Color parentBgColor = ColorsGUI.getFirstOpaqueParentBackground(c.getParent());
				if (parentBgColor != null) {
					Area areaNoComponent = new Area(new Rectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width + thicknessShadow + thicknessLineBorder, boundsBorder.height + thicknessShadow + thicknessLineBorder));
					areaNoComponent.subtract(areaComponent);
					g2d.setColor(parentBgColor);
					g2d.fill(areaNoComponent);
				}
			}
			
			// Pintamos la sombra
			if (thicknessShadow > 0) {
	
				Area areaBorder = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
				int thicknessExtra = thicknessShadow/2;//setShadowImageFile() != null ? thicknessShadow : thicknessShadow/2;//Ancho extra para que empiece antes la sombra
				double curveIncrement = 1.25;
				
				Area areaShadow = new Area(new RoundRectangle2D.Double(boundsBorder.x + thicknessShadow + thicknessLineBorder - thicknessExtra, boundsBorder.y + thicknessShadow + thicknessLineBorder - thicknessExtra, boundsBorder.width + thicknessExtra, boundsBorder.height + thicknessExtra, arcW * curveIncrement , arcH * curveIncrement));
				areaShadow.subtract(areaBorder);
				
				if (getShadowImageFile() != null && getShadowImageFile().exists()) {
					
					try {
						Rectangle boundsShadow = areaShadow.getBounds();
						BufferedImage shadowImage = ImageIO.read(getShadowImageFile()); 
						g2d.setPaint(new TexturePaint(shadowImage, boundsShadow));
						g2d.fill(areaShadow);
					}
					catch (IOException ex) {
						Console.printException(ex);
					}
				}
				else if (isGradientShadow()) {
					
					Composite originalComposite = g2d.getComposite();
					float alpha = (float) (100 - getShadowTransparency()) / (float) 100;
					int type = AlphaComposite.SRC_OVER;
					AlphaComposite alphaComposite = AlphaComposite.getInstance(type, alpha);
					g2d.setComposite(alphaComposite);
	
					Rectangle boundsShadow = areaShadow.getBounds();
					Color brightColor = Colors.brighter(getShadowColor(), 0.7);
					BufferedImage shadowImage = Icons.createImageShadow(getShadowColor(), brightColor, boundsShadow.width, boundsShadow.height, thicknessShadow);
					g2d.setPaint(new TexturePaint(shadowImage, boundsShadow));
					g2d.fill(areaShadow);
					g2d.setComposite(originalComposite);
					
					/*
					//Cuando intentamos pintar la sombra semitransparente se hacen mal los degradados, por lo que al final generamos la imagen opaca y luego la hacemos semitransparente
					paintGradientShadow(g2d, areaShadow, x, y, width, height)
					*/
				}
				else {
					
					Color shadowColorAlpha = Colors.getColorAlpha(getShadowColor(), getShadowTransparency());
					g2d.setColor(shadowColorAlpha);
					g2d.fill(areaShadow);
				}
			}
	
			// Pintamos el area interior
			float componentOpacity = c instanceof LPanel ? ((LPanel) c).getOpacity() : c.isOpaque() ? 1 : 0;
			if (componentOpacity > 0 || isPaintInsideAlways()) {
				AbstractButton b = c instanceof AbstractButton ? (AbstractButton) c : null;
				Paint paint;
				if (b != null) {
					Color bgColor = UISupportButtons.getBackground(b);
					LButtonProperties buttonProperties = UISupportButtons.getButtonProperties(b);
					boolean isGradientBackgroundEnabled = buttonProperties.isGradientBackgroundEnabled();
					paint = isGradientBackgroundEnabled ? GraphicsUtils.getGradientPaint(areaInterior.getBounds(), bgColor, GraphicsUtils.GRADIENT_TYPE_VERTICAL) : bgColor;
					if (componentOpacity == 0 && UISupportButtons.isBackgroundPainted(b))
						componentOpacity = 1;//Esto se da cuando pintamos un botón transparente, pero estamos haciendo rollover
				}
				else {
					paint = c.getBackground();
				}
				
				Composite oldComposite = g2d.getComposite();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, componentOpacity));
				
				g2d.setPaint(paint);
				g2d.fill(areaInterior);
				g2d.setComposite(oldComposite);
			}
					
			// Pintamos el borde de color
			paintLineBorder(g2d, boundsBorder, arcW, arcH);
	
			paintInnerShadow(g2d, boundsBorder, arcW, arcH);
		}
		finally {
			g2d.dispose();
		}
	}
	
	private void paintLineBorder(Graphics2D g2d, Rectangle boundsBorder, double arcW, double arcH) {
		
		if (getThicknessLineBorder() > 0) {
				
			if (getTopColor() != null || getLeftColor() != null || getBottomColor() != null || getRightColor() != null) {
			
				Area areaBorderAux = null;
				if (getBottomColor() != null) {
					Area areaBorderBottom = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderAux = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y-getThicknessLineBorder(), boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderBottom.subtract(areaBorderAux);
					g2d.setColor(getBottomColor());
					g2d.fill(areaBorderBottom);
				}
				if (getRightColor() != null) {
					Area areaBorderRight = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderAux = new Area(new RoundRectangle2D.Double(boundsBorder.x-getThicknessLineBorder(), boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderRight.subtract(areaBorderAux);
					g2d.setColor(getRightColor());
					g2d.fill(areaBorderRight);
				}
				if (getTopColor() != null) {
					Area areaBorderTop = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderAux = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y+getThicknessLineBorder(), boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderTop.subtract(areaBorderAux);
					g2d.setColor(getTopColor());
					g2d.fill(areaBorderTop);
				}
				if (getLeftColor() != null) {
					Area areaBorderLeft = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderAux = new Area(new RoundRectangle2D.Double(boundsBorder.x+getThicknessLineBorder(), boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
					areaBorderLeft.subtract(areaBorderAux);
					g2d.setColor(getLeftColor());
					g2d.fill(areaBorderLeft);
				}
				
			}
			else if (getLineBorderColor() != null) {
				
				g2d.setStroke(new BasicStroke((float) thicknessLineBorder));
				g2d.setColor(getLineBorderColor());
				g2d.drawRoundRect(boundsBorder.x+thicknessLineBorder/2, boundsBorder.y+thicknessLineBorder/2, boundsBorder.width, boundsBorder.height, (int) arcW, (int) arcH);
			}
		}
	}

	private void paintInnerShadow(Graphics2D g2d, Rectangle boundsBorder, double arcW, double arcH) {
		
		if (getThicknessInnerShadow() > 0) {
			
			Rectangle boundsShadow = new Rectangle(boundsBorder.x + getThicknessLineBorder(), boundsBorder.y + getThicknessLineBorder(), boundsBorder.width - getThicknessLineBorder()*2, boundsBorder.height - getThicknessLineBorder()*2);
			
			Area areaShadow = new Area(new RoundRectangle2D.Double(boundsShadow.x, boundsShadow.y, boundsShadow.width, boundsShadow.height, arcW, arcH));
			Area areaShadowAux = new Area(new RoundRectangle2D.Double(boundsShadow.x+getThicknessInnerShadow(), boundsShadow.y+getThicknessInnerShadow(), boundsShadow.width, boundsShadow.height, arcW, arcH));
			areaShadow.subtract(areaShadowAux);
			
			g2d.setColor(getShadowColor());
			Composite originalComposite = g2d.getComposite();
			float alpha = (float) (100 - getShadowTransparency()) / (float) 100;
			AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			g2d.setComposite(alphaComposite);
			g2d.fill(areaShadow);
			g2d.setComposite(originalComposite);
		}
	}
	
	private void paintGradientShadow(Graphics2D g2d, Area areaShadow, int x, int y, int width, int height) {
		
		Area areaShadowDown = new Area(areaShadow);
		Area areaRestShadowDown = new Area(new Rectangle(x, y, width, height - thicknessShadow - thicknessLineBorder - getCornersCurveSize().height/2));
		areaShadowDown.subtract(areaRestShadowDown);
		Area areaShadowRight = new Area(areaShadow);
		Area areaRestShadowRight = new Area(new Rectangle(x, y, width - thicknessShadow - thicknessLineBorder - getCornersCurveSize().width/2, height));
		areaShadowRight.subtract(areaRestShadowRight);
		Area areaShadowCorner = new Area(areaShadow);
		areaShadowCorner.subtract(areaRestShadowRight);
		areaShadowCorner.subtract(areaRestShadowDown);
		//areaShadowRight.subtract(areaShadowCorner);
		//areaShadowDown.subtract(areaShadowCorner);
		
		Rectangle boundsShadowRight = areaShadowRight.getBounds();
		Rectangle boundsShadowDown = areaShadowDown.getBounds();
		Rectangle boundsShadowCorner = areaShadowCorner.getBounds();
		//Rectangle boundsComponent = areaComponent.getBounds();
						
		final Color BRIGHT_COLOR = Colors.brighter(getShadowColor(), 0.5);
		
		Point2D leftBorder = new Point2D.Float((float) boundsShadowRight.x, boundsShadowRight.y + (float) boundsShadowRight.height / 2.0f);
		//Point2D leftBorder = new Point2D.Float((float) borderBounds.x + borderBounds.width, boundsShadowRight.y + (float) boundsShadowRight.height / 2.0f);
		Point2D rightBorder = new Point2D.Float((float) boundsShadowRight.x + boundsShadowRight.width, (float) leftBorder.getY());
		
		Point2D upBorder = new Point2D.Float(boundsShadowDown.x + (float) boundsShadowDown.width / 2.0f, (float) boundsShadowDown.y);
		//Point2D upBorder = new Point2D.Float(boundsShadowDown.x + (float) boundsShadowDown.width / 2.0f, (float) borderBounds.y + borderBounds.height);
		Point2D downBorder = new Point2D.Float((float) upBorder.getX(), (float) boundsShadowDown.y + boundsShadowDown.height);
		
		if (boundsShadowRight.width > 0 && boundsShadowRight.height > 0) {
			
			GradientPaint gradientShadowRight = new GradientPaint(rightBorder, BRIGHT_COLOR, leftBorder, getShadowColor());
			g2d.setPaint(gradientShadowRight);
			g2d.fill(areaShadowRight);
		}
		if (boundsShadowDown.width > 0 && boundsShadowDown.height > 0) {
			
			GradientPaint gradientShadowDown = new GradientPaint(downBorder, BRIGHT_COLOR, upBorder, getShadowColor());
			g2d.setPaint(gradientShadowDown);
			g2d.fill(areaShadowDown);
		}
		if (boundsShadowCorner.width > 0 && boundsShadowCorner.height > 0) {
			float[] dist = { 0.0f, 1.0f };
			Color[] colors = { getShadowColor(), BRIGHT_COLOR };

			float shadowSize = boundsShadowRight.width/2 + boundsShadowDown.height/2;
			Point2D cornerDownRight = new Point2D.Float(boundsShadowCorner.x, boundsShadowCorner.y);
			//Point2D esquinaAbajoDer = new Point2D.Float((float) boundsComponente.x + boundsComponente.width, (float) boundsComponente.y + boundsComponente.height);
			RadialGradientPaint gradientCorner = new RadialGradientPaint(cornerDownRight, shadowSize, dist, colors);
			
			g2d.setPaint(gradientCorner);
			g2d.fill(areaShadowCorner);
			
			//Pruebas
			int sizePoint = 4;
			int deviationPoint = sizePoint/2;
			
			g2d.setColor(Color.red);
			g2d.fillOval((int)cornerDownRight.getX()-deviationPoint, (int)cornerDownRight.getY()-deviationPoint, sizePoint, sizePoint);
			g2d.fillOval((int)leftBorder.getX()-deviationPoint, (int)leftBorder.getY()-deviationPoint, sizePoint, sizePoint);
			g2d.fillOval((int)upBorder.getX()-deviationPoint, (int)upBorder.getY()-deviationPoint, sizePoint, sizePoint);
			g2d.drawLine((int)cornerDownRight.getX(), (int)cornerDownRight.getY(), (int)upBorder.getX(), (int)upBorder.getY());
			g2d.drawLine((int)cornerDownRight.getX(), (int)cornerDownRight.getY(), (int)leftBorder.getX(), (int)leftBorder.getY());
			
			g2d.setColor(Color.blue);
			g2d.fillOval((int)rightBorder.getX()-deviationPoint, (int)rightBorder.getY()-deviationPoint, sizePoint, sizePoint);
			g2d.fillOval((int)downBorder.getX()-deviationPoint, (int)downBorder.getY()-deviationPoint, sizePoint, sizePoint);
			g2d.drawLine((int)cornerDownRight.getX(), (int)cornerDownRight.getY(), (int)downBorder.getX(), (int)downBorder.getY());
			g2d.drawLine((int)cornerDownRight.getX(), (int)cornerDownRight.getY(), (int)rightBorder.getX(), (int)rightBorder.getY());
			
			//
		}
	}
	private void paintBorderAlt(Component c, Graphics g, int x, int y, int width, int height) {

		Dimension arcs = getCornersCurveSize();
		double arcW = arcs.getWidth();
		double arcH = arcs.getHeight();

		int thicknessLineBorder = getThicknessLineBorder();
		int thicknessShadow = getThicknessShadow();

		Rectangle boundsBorder = new Rectangle(x, y, width - thicknessLineBorder - thicknessShadow, height - thicknessLineBorder - thicknessShadow);
		Rectangle boundsInterior = new Rectangle(boundsBorder.x + thicknessLineBorder, boundsBorder.y + thicknessLineBorder, boundsBorder.width - thicknessLineBorder, boundsBorder.height - thicknessLineBorder);
		
		Area areaInterior = new Area(new RoundRectangle2D.Double(boundsInterior.x, boundsInterior.y, boundsInterior.width, boundsInterior.height, arcW, arcH));
		Insets borderInsets = getBorderInsets(c);
		if (width > borderInsets.left + borderInsets.right && height > borderInsets.top + borderInsets.bottom) {
			// Al areaInterior le restamos el area del componente
			Area areaComponent = new Area(new Rectangle2D.Double(x + borderInsets.left, y + borderInsets.top, width - borderInsets.left - borderInsets.right, height - borderInsets.top - borderInsets.bottom));
			areaInterior.subtract(areaComponent);
		}

		Graphics2D g2d = (Graphics2D) g.create();
		try {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
			// Pintamos el area interior
			if (c.isOpaque() || isPaintInsideAlways()) {
				g2d.setColor(c.getBackground());
				g2d.fill(areaInterior);
			}
			// Pintamos la sombra
			if (thicknessShadow > 0) {
	
				Rectangle boundsShadow = new Rectangle(boundsBorder.x + thicknessShadow, boundsBorder.y + thicknessShadow, boundsBorder.width, boundsBorder.height);
	
				Area areaBorder = new Area(new RoundRectangle2D.Double(boundsBorder.x, boundsBorder.y, boundsBorder.width, boundsBorder.height, arcW, arcH));
				Area areaShadow = new Area(new RoundRectangle2D.Double(boundsShadow.x, boundsShadow.y, boundsShadow.width, boundsShadow.height, arcW, arcH));
				areaShadow.subtract(areaBorder);
				
				boolean degradado = true;
				if (degradado) {
					float alpha = (float) (100 - getShadowTransparency()) / (float) 100;
					Composite originalComposite = g2d.getComposite();
					int type = AlphaComposite.SRC_OVER;
					AlphaComposite alphaComposite = AlphaComposite.getInstance(type, alpha);
					g2d.setComposite(alphaComposite);
	
					Area areaShadowDown = new Area(areaShadow);
					Area areaRestShadowDown = new Area(new Rectangle(x, y, width, height - thicknessShadow - thicknessLineBorder - getCornersCurveSize().height/2));
					areaShadowDown.subtract(areaRestShadowDown);
					Area areaShadowRight = new Area(areaShadow);
					Area areaRestShadowRight = new Area(new Rectangle(x, y, width - thicknessShadow - thicknessLineBorder - getCornersCurveSize().width/2, height));
					areaShadowRight.subtract(areaRestShadowRight);
					Area areaShadowCorner = new Area(areaShadow);
					areaShadowCorner.subtract(areaRestShadowRight);
					areaShadowCorner.subtract(areaRestShadowDown);
					//areaShadowRight.subtract(areaShadowCorner);
					//areaShadowDown.subtract(areaShadowCorner);
					
					/*Rectangle boundsShadowRight = areaShadowRight.getBounds();
					Rectangle boundsShadowDown = areaShadowDown.getBounds();
					Rectangle boundsShadowCornerDownRight = areaShadowCorner.getBounds();
					*/
									
					final Color BRIGHT_COLOR = GeneralUIProperties.getInstance().getColorPanelsBright();
					
					Point2D downBorder = new Point2D.Float(boundsShadow.x + (float) boundsShadow.getWidth() / 2.0f, (float) boundsShadow.y + boundsShadow.height);
					Point2D upBorder = new Point2D.Float((float) downBorder.getX(), (float) downBorder.getY() - thicknessShadow);
					
					Point2D rightBorder = new Point2D.Float(boundsShadow.x + (float) boundsShadow.width, boundsShadow.y + (float) boundsShadow.getHeight() / 2.0f);
					Point2D leftBorder = new Point2D.Float((float) downBorder.getX() - thicknessShadow, (float) downBorder.getY());
					
					Point2D rgPointUpRight = new Point2D.Float(boundsShadow.x + (float) boundsShadow.getWidth() - thicknessShadow, boundsShadow.y + (float) boundsShadow.getHeight() - thicknessShadow);
					
					//if (leftBorder.getX() < rightBorder.getX()) {
						
						GradientPaint gradientShadowRight = new GradientPaint(rightBorder, BRIGHT_COLOR, leftBorder, getShadowColor());
						g2d.setPaint(gradientShadowRight);
						g2d.fill(areaShadowRight);
					//}
					//if (upBorder.getY() < downBorder.getY()) {
						
						GradientPaint gradientShadowDown = new GradientPaint(downBorder, BRIGHT_COLOR, upBorder, getShadowColor());
						g2d.setPaint(gradientShadowDown);
						g2d.fill(areaShadowDown);
					//}
					
					float[] dist = { 0.0f, 1.0f };
					Color[] colors = { getShadowColor(), BRIGHT_COLOR };
					RadialGradientPaint gradientCorner = new RadialGradientPaint(rgPointUpRight, thicknessShadow, dist, colors);
					
					g2d.setPaint(gradientCorner);
					g2d.fill(areaShadowCorner);
	
					g2d.setComposite(originalComposite);
					
					//Pruebas
					int sizePoint = 4;
					int deviationPoint = sizePoint/2;
					
					g2d.setColor(Color.red);
					g2d.fillOval((int)rgPointUpRight.getX()-deviationPoint, (int)rgPointUpRight.getY()-deviationPoint, sizePoint, sizePoint);
					g2d.fillOval((int)leftBorder.getX()-deviationPoint, (int)leftBorder.getY()-deviationPoint, sizePoint, sizePoint);
					g2d.fillOval((int)upBorder.getX()-deviationPoint, (int)upBorder.getY()-deviationPoint, sizePoint, sizePoint);
					g2d.drawLine((int)rgPointUpRight.getX(), (int)rgPointUpRight.getY(), (int)upBorder.getX(), (int)upBorder.getY());
					g2d.drawLine((int)rgPointUpRight.getX(), (int)rgPointUpRight.getY(), (int)leftBorder.getX(), (int)leftBorder.getY());
					
					g2d.setColor(Color.blue);
					g2d.fillOval((int)rightBorder.getX()-deviationPoint, (int)rightBorder.getY()-deviationPoint, sizePoint, sizePoint);
					g2d.fillOval((int)downBorder.getX()-deviationPoint, (int)downBorder.getY()-deviationPoint, sizePoint, sizePoint);
					g2d.drawLine((int)rgPointUpRight.getX(), (int)rgPointUpRight.getY(), (int)downBorder.getX(), (int)downBorder.getY());
					g2d.drawLine((int)rgPointUpRight.getX(), (int)rgPointUpRight.getY(), (int)rightBorder.getX(), (int)rightBorder.getY());
					
					//
				}
				else {
					int alpha = 255 - getShadowTransparency() * 255 / 100;
					Color shadowColorAlpha = new Color(getShadowColor().getRed(), getShadowColor().getGreen(), getShadowColor().getBlue(), alpha);
					g2d.setColor(shadowColorAlpha);
					g2d.fill(areaShadow);
				}
			}
	
			// Pintamos el borde
			if (thicknessLineBorder > 0 && getLineBorderColor() != null) {
	
				g2d.setStroke(new BasicStroke((float) thicknessLineBorder));
				g2d.setColor(getLineBorderColor());
				g2d.drawRoundRect(boundsBorder.x+thicknessLineBorder/2, boundsBorder.y+thicknessLineBorder/2, boundsBorder.width, boundsBorder.height, (int) arcW, (int) arcH);
			}
		}
		finally {
			g2d.dispose();
		}
	}

	
	public Dimension getCornersCurveSize() {
		return cornersCurveSize;
	}
	public int getShadowTransparency() {
		return shadowTransparency;
	}
	public int getThicknessInsetsExtra() {
		return thicknessInsetsExtra;
	}
	public int getThicknessLineBorder() {
		return thicknessLineBorder;
	}
	public int getThicknessShadow() {
		return thicknessShadow;
	}
	public int getThicknessInnerShadow() {
		return thicknessInnerShadow;
	}
	public Color getLineBorderColor() {
		if (lineBorderColor == null)
			lineBorderColor = ColorsGUI.getColorBorder();
		return lineBorderColor;
	}
	public Color getShadowColor() {
		if (shadowColor == null)
			shadowColor = ColorsGUI.getColorShadow();
		return shadowColor;
	}
	public boolean isPaintInsideAlways() {
		return paintInsideAlways;
	}
	public boolean isGradientShadow() {
		return gradientShadow;
	}
	public File getShadowImageFile() {
		return shadowImageFile;
	}

	public void setCornersCurveSize(Dimension cornersCurveSize) {
		this.cornersCurveSize = cornersCurveSize;
	}
	/*
	 * El nivel de transparencia va de 0 a 95 (0=Opaco 100=Transparente)
	 */
	public void setShadowTransparency(int shadowTransparency) {
		
		if (shadowTransparency < MIN_TRANSPARENCY)
			shadowTransparency = MIN_TRANSPARENCY;
    	else if (shadowTransparency > MAX_TRANSPARENCY)
    		shadowTransparency = MAX_TRANSPARENCY;
		
		this.shadowTransparency = shadowTransparency;
	}
	public void setThicknessInsetsExtra(int thicknessInsetsExtra) {
		this.thicknessInsetsExtra = thicknessInsetsExtra;
	}
	public void setThicknessLineBorder(int thicknessLineBorder) {
		this.thicknessLineBorder = thicknessLineBorder;
	}
	public void setThicknessShadow(int thicknessShadow) {
		this.thicknessShadow = thicknessShadow;
	}
	public void setThicknessInnerShadow(int thicknessInnerShadow) {
		this.thicknessInnerShadow = thicknessInnerShadow;
	}
	public void setLineBorderColor(Color lineBorderColor) {
		this.lineBorderColor = lineBorderColor;
	}
	public void setLineBorderColor(Color topColor, Color leftColor, Color bottomColor, Color rightColor) {
		this.topColor = topColor;
		this.leftColor = leftColor;
		this.bottomColor = bottomColor;
		this.rightColor = rightColor;
	}
	public void setShadowColor(Color colorSombra) {
		this.shadowColor = colorSombra;
	}
	/*
	 * Si se pone a true forzará el pintado del hueco formado por el arco de las esquinas y el componente sobre el que se aplica el borde aunque sea transparente
	 */
	public void setPaintInsideAlways(boolean pintarInteriorSiempre) {
		this.paintInsideAlways = pintarInteriorSiempre;
	}
	public void setGradientShadow(boolean degradarSombra) {
		this.gradientShadow = degradarSombra;
	}
	/*
	 * Si se especifica una imagen para la sombra se ignorará el color y la transparencia de la sombra
	 */
	public void setShadowImageFile(File imagenSombra) {
		this.shadowImageFile = imagenSombra;
	}
	
	public Color getTopColor() {
		return topColor;
	}
	public Color getLeftColor() {
		return leftColor;
	}
	public Color getBottomColor() {
		return bottomColor;
	}
	public Color getRightColor() {
		return rightColor;
	}
}
