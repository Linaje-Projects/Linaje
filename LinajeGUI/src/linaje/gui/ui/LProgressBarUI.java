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
package linaje.gui.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.utils.Colors;
import linaje.utils.Strings;

public class LProgressBarUI extends BasicProgressBarUI {

	private String MAX_PERCENT = "100%";
	private boolean paintCircleBar;
	
	public LProgressBarUI() {
		this(false);
	}
	public LProgressBarUI(boolean paintCircleBar) {
		super();
		this.paintCircleBar = paintCircleBar;
	}

	public static ComponentUI createUI(JComponent x) {
        return new LProgressBarUI();
    }

	public Dimension getPreferredSize(JComponent c) {
		Dimension prefSize = super.getPreferredSize(c);
		if (paintCircleBar) {
			int size = Strings.getStringWidth(MAX_PERCENT, c.getFontMetrics(c.getFont()));
			size += 2 + Math.round(size*0.1);
			prefSize.width = size;
			prefSize.height = size;
		}
		
		return prefSize;
	}

	protected void setAnimationIndex(int newValue) {
       progressBar.repaint();
    }
		
	public void paint(Graphics g, JComponent c) {
		
		Insets insets = progressBar.getInsets();
		
		int pbWidth = progressBar.getWidth() - insets.right - insets.left;
		int pbHeight = progressBar.getHeight() - insets.top - insets.bottom;
		
		Rectangle viewRect = new Rectangle(insets.left, insets.top, pbWidth, pbHeight);
		
		boolean indeterminate = progressBar.isIndeterminate();
		double progressFactor = indeterminate ? Calendar.getInstance().get(Calendar.MILLISECOND)/1000.0 : progressBar.getPercentComplete();
		Color color = progressBar.getForeground();
		color = ColorsGUI.getColorApp();
		
		if (paintCircleBar) {
			if (indeterminate)
				paintCircleBarStroke(g, viewRect, indeterminate, progressFactor, color, -1, 1, SwingConstants.NORTH);
			else
				paintCircleBar(g, viewRect, indeterminate, progressFactor, color);
		}
		else
			paintLineBar(g, viewRect, indeterminate, progressFactor, color);
		
		//Paint text
		if (progressBar.isStringPainted()) {
			g.setColor(progressBar.getForeground());
			paintString(g, viewRect.x, viewRect.y, viewRect.width, viewRect.height, 0, insets);
		}
	}
	
	@Override
	protected void installDefaults() {
		super.installDefaults();
		progressBar.setStringPainted(true);
	}
	@Override
	protected Color getSelectionBackground() {
		return progressBar.getForeground();
	}
	public static void paintLineBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color) {
		paintLineBar(g, viewRect, indeterminate, progressFactor, color, -1);
	}
	public static void paintLineBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness) {
		paintLineBar(g, viewRect, indeterminate, progressFactor, color, thickness, 1);
	}
	public static void paintLineBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness, float alpha) {
		paintLineBar(g, viewRect, indeterminate, progressFactor, color, thickness, alpha, -1);
	}
	public static void paintLineBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness, float alpha, int startPosition) {
		
		if (viewRect.width > 0 && viewRect.height > 0) {
			
			int size = Math.min(viewRect.width, viewRect.height);
			int xCenter = viewRect.x + viewRect.width / 2;
			int yCenter = viewRect.y + viewRect.height / 2;
			
			if (thickness <= 0 || thickness > size) {
				thickness = Math.min(viewRect.width, viewRect.height);
			}
						
			if (startPosition == -1) {
				startPosition = viewRect.width > viewRect.height ? SwingConstants.WEST : SwingConstants.SOUTH;
			}
				
			Point start = new Point();
			Point end = new Point();
			Point endProgress = new Point();
			Rectangle barRects = new Rectangle(viewRect);
			
			//int gradientType;
			switch (startPosition) {
				case SwingConstants.SOUTH:
					start.x = xCenter;
					start.y = viewRect.y + viewRect.height;
					end.x = xCenter;
					end.y = viewRect.y;
					endProgress.x = xCenter;
					endProgress.y = viewRect.y + viewRect.height - (int)(viewRect.height*progressFactor);
					barRects.width = (int) thickness;
					barRects.height = viewRect.height;
					barRects.x = xCenter - (int) (thickness/2);
					//gradientType = UtilsGUI.GRADIENT_TYPE_VERTICAL;
					break;
				case SwingConstants.NORTH:
					start.x = xCenter;
					start.y = viewRect.y;
					end.x = xCenter;
					end.y = viewRect.y + viewRect.height;
					endProgress.x = xCenter;
					endProgress.y = viewRect.y + (int)(viewRect.height*progressFactor);
					barRects.width = (int) thickness;
					barRects.height = viewRect.height;
					barRects.x = xCenter - (int) (thickness/2);
					//gradientType = UtilsGUI.GRADIENT_TYPE_VERTICAL;
					break;
				case SwingConstants.EAST:
					start.x = viewRect.x + viewRect.width;
					start.y = yCenter;
					end.x = viewRect.x;
					end.y = yCenter;
					endProgress.x = viewRect.x + viewRect.width - (int)(viewRect.width*progressFactor);
					endProgress.y = yCenter;
					barRects.width = viewRect.width;
					barRects.height = (int) thickness;
					barRects.y = yCenter - (int) (thickness/2);
					//gradientType = UtilsGUI.GRADIENT_TYPE_HORIZONTAL;
					break;	
				default:
					start.x = viewRect.x;
					start.y = yCenter;
					end.x = viewRect.x + viewRect.width;
					end.y = yCenter;
					endProgress.x = viewRect.x + (int)(viewRect.width*progressFactor);
					endProgress.y = yCenter;
					barRects.width = viewRect.width;
					barRects.height = (int) thickness;
					barRects.y = yCenter - (int) (thickness/2);
					//gradientType = UtilsGUI.GRADIENT_TYPE_HORIZONTAL;
					break;
			}
			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				//g2d.setStroke(new BasicStroke((float)thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.setStroke(new BasicStroke((float)thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				
				boolean isBrightColor = Colors.getLuminance(color) > 0.7;
				
				int darkTransparency = 30;
				double brightFactor = indeterminate ? 0.4 : 0.2;
				Color darkColor, brightColor;
				if (isBrightColor) {
					brightColor = color;
					darkColor = Colors.getColorAlpha(Colors.darker(color, brightFactor), darkTransparency);
				}
				else {
					brightColor = Colors.brighter(color, brightFactor);
					darkColor = Colors.getColorAlpha(color, darkTransparency);
				}
				
				float[] dist = {0.0f, 1.0f};
				Color[] colors = {darkColor, brightColor};
			    CycleMethod cycleMethod = CycleMethod.NO_CYCLE;//CycleMethod.REFLECT
				
			    if (indeterminate) {
			    	
			    	//darkColor bar background
			    	g2d.setPaint(darkColor);
			    	g2d.drawLine(start.x, start.y, end.x, end.y);
			    	
			    	//darkColor = Color.black;
			    	Rectangle gradientRects = new Rectangle(barRects);
			    	if (startPosition == SwingConstants.NORTH || startPosition == SwingConstants.SOUTH) {
			    		gradientRects.y = endProgress.y*2 - barRects.height;
						gradientRects.width = barRects.width*4;
						gradientRects.x = barRects.x - (gradientRects.width-barRects.width)/2;
			    	}
			    	else {
			    		gradientRects.x = endProgress.x*2 - barRects.width;
						gradientRects.height = barRects.height*4;
						gradientRects.y = barRects.y - (gradientRects.height-barRects.height)/2;
			    	}
					//gradientRects.width = viewRect.width*2;
					//gradientRects.y = endProgress.y;
					
					Paint paint = GraphicsUtils.getGradientPaint(gradientRects, brightColor, darkColor, GraphicsUtils.GRADIENT_TYPE_ELIPSE, false, false);
					//g2d.setPaint(Color.black);
					//g2d.fillRect(viewRect.x, viewRect.y, viewRect.width, viewRect.height);
			    	g2d.setPaint(paint);
					g2d.drawLine(start.x, start.y, end.x, end.y);
					g2d.setColor(Color.blue);
					g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
					//g2d.drawRect(barRects.x, barRects.y, barRects.width, barRects.height);
					//g2d.drawRect(gradientRects.x, gradientRects.y, gradientRects.width, gradientRects.height);
					g2d.setPaint(Color.red);
					//g2d.fillRect(gradientRects.x + gradientRects.width/2 - 5, gradientRects.y + gradientRects.height/2 - 5, 10, 10);
			    }
			    else {
			    	//Translucent bar background
			    	g2d.setPaint(Colors.getColorAlpha(Color.black, 92));
			    	g2d.drawLine(start.x, start.y, end.x, end.y);
			    	//Progress
			    	if (progressFactor > 0) {
				    	if (progressFactor > 0.1) {
					    	Paint paint = new LinearGradientPaint(start, endProgress, dist, colors, cycleMethod, ColorSpaceType.LINEAR_RGB, new AffineTransform());
							g2d.setPaint(paint);
				    	}
				    	else {
				    		g2d.setPaint(color);
				    	}
				    	
						g2d.drawLine(start.x, start.y, endProgress.x, endProgress.y);
			    	}
			    }	
			}
			finally {
				g2d.dispose();
			}
		}
	}
	
	public static void paintCircleBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color) {
		paintCircleBar(g, viewRect, indeterminate, progressFactor, color, -1);
	}
	public static void paintCircleBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness) {
		paintCircleBar(g, viewRect, indeterminate, progressFactor, color, thickness, 1);
	}
	public static void paintCircleBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness, float alpha) {
		paintCircleBar(g, viewRect, indeterminate, progressFactor, color, thickness, alpha, SwingConstants.NORTH);
	}
	public static void paintCircleBarStroke(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness, float alpha, int startPosition) {
		
		if (viewRect.width > 0 && viewRect.height > 0) {
			
			double degrees = 360 * progressFactor;
			double size = Math.min(viewRect.width, viewRect.height);
			double xCenter = viewRect.x + viewRect.width / 2;
			double yCenter = viewRect.y + viewRect.height / 2;
			double radious = size / 2;
			if (thickness <= 0 || thickness > size)
				thickness = radious*0.1;
			
			if (size > thickness) {
				radious -= thickness/2;
				size -= thickness;
			}
					
			int degreesStart;
			switch (startPosition) {
				case SwingConstants.SOUTH:
					degreesStart = 270;
					break;
				case SwingConstants.WEST:
					degreesStart = 180;
					break;
				case SwingConstants.EAST:
					degreesStart = 0;
					break;	
				default:
					degreesStart = 90;
					break;
			}
			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				
				double angle = Math.toRadians(degrees - degreesStart);
				double outRadiousMiddleBar = radious - thickness/2;
				double xCircleBorder = xCenter + outRadiousMiddleBar * Math.cos(angle);
				double yCircleBorder = yCenter + outRadiousMiddleBar * Math.sin(angle);
				Rectangle gradientRects = new Rectangle();
				gradientRects.width = (int) radious*2;
				gradientRects.height = (int) radious*2;
				gradientRects.x = (int) (xCircleBorder - gradientRects.width/2);
				gradientRects.y = (int) (yCircleBorder - gradientRects.height/2);
				
				boolean isBrightColor = Colors.getLuminance(color) > 0.7;
				//Color brightColor = isBrightColor ? Colors.darker(color, 0.3) : Colors.brighter(color, 0.3);
				//Paint paint = UtilsGUI.getGradientPaint(gradientRects, brightColor, color, UtilsGUI.GRADIENT_TYPE_ELIPSE, false, isBrightColor);
				
				int darkTransparency = 30;
				Color darkColor, brightColor;
				if (isBrightColor) {
					brightColor = color;
					darkColor = Colors.getColorAlpha(Colors.darker(color, 0.3), darkTransparency);
				}
				else {
					brightColor = Colors.brighter(color, 0.3);
					darkColor = Colors.getColorAlpha(color, darkTransparency);
				}
				
				Paint paint = GraphicsUtils.getGradientPaint(gradientRects, brightColor, darkColor, GraphicsUtils.GRADIENT_TYPE_ELIPSE, false, false);
				//g2d.setPaint(color);
				g2d.setPaint(paint);
				
				//g2d.fill(area);
				
				if (indeterminate) {
					g2d.setStroke(new BasicStroke((float)thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					int arcSize = 350;//Math.round(360*Math.max((float)progressFactor,0.5f)); 
					int paints = 40;//Cuantos mas paints, menos diferenca entre tonos
					Color colorArc = brightColor;
					double arcAngle = degrees;
					for (int i = paints; i >= 0; i--) {
						int transparency = (i*100)/(paints+1);
						float darkFactor = Math.min(transparency/200f, 0.3f);
						colorArc = Colors.darker(brightColor, darkFactor);
						colorArc = Colors.getColorAlpha(colorArc, transparency);
						arcSize -= arcSize/paints;
						g2d.setPaint(colorArc);
						g2d.drawArc((int) (xCenter - radious), (int) (yCenter - radious), (int) size, (int) size, (int) (degreesStart - arcAngle), (int) arcSize);
					}
					//g2d.setPaint(Colors.brighter(brightColor, 0.1));
					//g2d.drawArc((int) (xCenter - radious), (int) (yCenter - radious), (int) size, (int) size, (int) (degreesStart - arcAngle), (int) 1);
				}
				else {
					g2d.setStroke(new BasicStroke((float)thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
					int arcSize = (int) degrees;
					int paints = 40;//Cuantos mas paints, menos diferenca entre tonos
					Color colorArc = brightColor;
					double arcAngle = degrees;
					for (int i = paints; i >= 0; i--) {
						int transparency = (i*100)/(paints+1);
						float darkFactor = Math.min(transparency/200f, 0.3f);
						colorArc = Colors.darker(brightColor, darkFactor);
						//colorArc = Colors.getColorAlpha(colorArc, transparency);
						arcSize -= arcSize/paints;
						g2d.setPaint(colorArc);
						g2d.drawArc((int) (xCenter - radious), (int) (yCenter - radious), (int) size, (int) size, (int) (degreesStart - arcAngle), (int) arcSize);
					}
				}
			}
			finally {
				g2d.dispose();
			}
		}
	}
	
	public static void paintCircleBar(Graphics g, Rectangle viewRect, boolean indeterminate, double progressFactor, Color color, double thickness, float alpha, int startPosition) {
		
		if (viewRect.width > 0 && viewRect.height > 0) {
			
			double degrees = 360 * progressFactor;
			double size = Math.min(viewRect.width, viewRect.height);
			double xCenter = viewRect.x + viewRect.width / 2;
			double yCenter = viewRect.y + viewRect.height / 2;
			double outRadious = size / 2;
			double innerRadious;
			if (thickness <= 0 || thickness > size) {
				//El ancho de la barra está fuera de rango y lo ponemos automático
				innerRadious = outRadious * 0.8;
				thickness = outRadious - innerRadious;
			}
			else {
				innerRadious = outRadious - thickness;
			}
			
			int degreesStart;
			switch (startPosition) {
				case SwingConstants.SOUTH:
					degreesStart = 270;
					break;
				case SwingConstants.WEST:
					degreesStart = 180;
					break;
				case SwingConstants.EAST:
					degreesStart = 0;
					break;	
				default:
					degreesStart = 90;
					break;
			}
			
			Shape innerCircle = new Ellipse2D.Double(xCenter - innerRadious, yCenter - innerRadious, innerRadious * 2, innerRadious * 2);
			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				
				Shape outerCircle;
				if (indeterminate) {
					outerCircle = new Ellipse2D.Double(xCenter - outRadious, yCenter - outRadious, outRadious * 2, outRadious * 2);	
				}
				else {
					outerCircle = new Arc2D.Double(xCenter - outRadious, yCenter - outRadious, size, size, degreesStart - degrees, degrees, Arc2D.PIE);
				}
				
				Area area = new Area(outerCircle);
				area.subtract(new Area(innerCircle));
				
				double angle = Math.toRadians(degrees - degreesStart);
				double outRadiousMiddleBar = outRadious - thickness/2;
				double xCircleBorder = xCenter + outRadiousMiddleBar * Math.cos(angle);
				double yCircleBorder = yCenter + outRadiousMiddleBar * Math.sin(angle);
				Rectangle gradientRects = new Rectangle();
				gradientRects.width = (int) (outerCircle.getBounds().width);
				gradientRects.height = (int) (outerCircle.getBounds().height);
				gradientRects.x = (int) (xCircleBorder - gradientRects.width/2);
				gradientRects.y = (int) (yCircleBorder - gradientRects.height/2);
				
				boolean isBrightColor = Colors.getLuminance(color) > 0.7;
				//Color brightColor = isBrightColor ? Colors.darker(color, 0.3) : Colors.brighter(color, 0.3);
				//Paint paint = UtilsGUI.getGradientPaint(gradientRects, brightColor, color, UtilsGUI.GRADIENT_TYPE_ELIPSE, false, isBrightColor);
				
				int darkTransparency = 30;
				Color darkColor, brightColor;
				if (isBrightColor) {
					brightColor = color;
					darkColor = Colors.getColorAlpha(Colors.darker(color, 0.3), darkTransparency);
				}
				else {
					brightColor = Colors.brighter(color, 0.3);
					darkColor = Colors.getColorAlpha(color, darkTransparency);
				}
				
				Paint paint = GraphicsUtils.getGradientPaint(gradientRects, brightColor, darkColor, GraphicsUtils.GRADIENT_TYPE_ELIPSE, false, false);
				//g2d.setPaint(color);
				g2d.setPaint(paint);
				
				g2d.fill(area);
				
				/*if (indeterminate) {
					//Pintamos un pequeño arco en la zona de progreso justo en el centro del gradientPaint
					int ligthSizeAngle = 30;
					Shape lightArc = new Arc2D.Double(xCenter - outRadious, yCenter - outRadious, size, size, degreesStart - degreesLight-ligthSizeAngle/2, ligthSizeAngle, Arc2D.PIE);
					Area areaLightArc = new Area(lightArc);
					areaLightArc.subtract(new Area(innerCircle));
					g2d.setPaint(isBrightColor ? color : brightColor);
					g2d.fill(areaLightArc);
				}*/
				
			}
			finally {
				g2d.dispose();
			}
		}
	}
}
