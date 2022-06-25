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
package linaje.gui.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingConstants;

/**
 * Permite asignar alineación vertical (entre componentes)
 * y alienación vertical global respecto al contenedor
 * Tambien nos permite elegir si queremos que el preferredSize sea en horizontal o en vertical
 **/
@SuppressWarnings("serial")
public class LFlowLayout extends FlowLayout {

	private int verticalAlignmentGlobal = SwingConstants.CENTER;
	private int verticalAlignment = SwingConstants.CENTER;
	
	private boolean preferredSizeVertical = true;
	
	public LFlowLayout() {
		super();
	}

	public LFlowLayout(boolean preferredSizeVertical) {
		super();
		setPreferredSizeVertical(preferredSizeVertical);
	}
	
	public LFlowLayout(int align) {
		super(align);
	}
	
	public LFlowLayout(int alignH, boolean preferredSizeVertical) {
		super(alignH);
		setPreferredSizeVertical(preferredSizeVertical);
	}

	public LFlowLayout(int alignH, int hgap, int vgap) {
		super(alignH, hgap, vgap);
	}
	
	public LFlowLayout(int alignH, int hgap, int vgap, boolean preferredSizeVertical) {
		super(alignH, hgap, vgap);
		setPreferredSizeVertical(preferredSizeVertical);
	}
	
	public LFlowLayout(int alignH, int alignV, int hgap, int vgap) {
		super(alignH, hgap, vgap);
		setVerticalAlignmentGlobal(alignV);
	}
	
	public LFlowLayout(int alignH, int alignV, int hgap, int vgap, boolean preferredSizeVertical) {
		super(alignH, hgap, vgap);
		setVerticalAlignmentGlobal(alignV);
		setPreferredSizeVertical(preferredSizeVertical);
	}

	public void layoutContainer(Container target) {
		
		synchronized (target.getTreeLock()) {
		
			layoutContainerIndividual(target);
			layoutContainerGlobal(target);
		}
	}
	
	private void layoutContainerIndividual(Container target) {
		
		if (getVerticalAlignment() == SwingConstants.CENTER) {
			super.layoutContainer(target);
		}
		else {
			//Copiado de super.layoutContainer con funcionamiento similar a useBaseline=true y baseline inicializado segun la alineación vertical
			Insets insets = target.getInsets();
			int hgap = getHgap();
			int vgap = getVgap();
			int maxwidth = target.getWidth() - (insets.left + insets.right + hgap * 2);
			int nmembers = target.getComponentCount();
			int x = 0, y = insets.top + vgap;
			int rowh = 0, start = 0;

			boolean ltr = target.getComponentOrientation().isLeftToRight();

			int[] ascent = new int[nmembers];
			int[] descent = new int[nmembers];

			for (int i = 0; i < nmembers; i++) {
				
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					int baseline = getVerticalAlignment() == SwingConstants.TOP ? 0 : getVerticalAlignment() == SwingConstants.BOTTOM ? d.height : -1;
					if (baseline >= 0) {
						ascent[i] = baseline;
						descent[i] = d.height - baseline;
					}
					else {
						ascent[i] = -1;
					}

					if ((x == 0) || ((x + d.width) <= maxwidth)) {
						if (x > 0) {
							x += hgap;
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					}
					else {
						rowh = moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, i, ltr, ascent, descent);
						x = d.width;
						y += vgap + rowh;
						rowh = d.height;
						start = i;
					}
				}
			}
			moveComponents(target, insets.left + hgap, y, maxwidth - x, rowh, start, nmembers, ltr, ascent, descent);
		}
	}
	
	/**
     * Centers the elements in the specified row, if there is any slack.
     * @param target the component which needs to be moved
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width dimensions
     * @param height the height dimensions
     * @param rowStart the beginning of the row
     * @param rowEnd the the ending of the row
     * @param useBaseline Whether or not to align on baseline.
     * @param ascent Ascent for the components. This is only valid if
     *               useBaseline is true.
     * @param descent Ascent for the components. This is only valid if
     *               useBaseline is true.
     * @return actual row height
     */
	private int moveComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr, int[] ascent, int[] descent) {
		//Copiado de super.moveComponents con funcionamiento similar a useBaseline=true
		switch (getAlignment()) {
			case LEFT:
				x += ltr ? 0 : width;
				break;
			case CENTER:
				x += width / 2;
				break;
			case RIGHT:
				x += ltr ? width : 0;
				break;
			case LEADING:
				break;
			case TRAILING:
				x += width;
				break;
		}
		
		int maxAscent = 0;
		int nonbaselineHeight = 0;
		int baselineOffset = 0;
		
		int maxDescent = 0;
		for (int i = rowStart; i < rowEnd; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				if (ascent[i] >= 0) {
					maxAscent = Math.max(maxAscent, ascent[i]);
					maxDescent = Math.max(maxDescent, descent[i]);
				}
				else {
					nonbaselineHeight = Math.max(m.getHeight(), nonbaselineHeight);
				}
			}
		}
		height = Math.max(maxAscent + maxDescent, nonbaselineHeight);
		baselineOffset = (height - maxAscent - maxDescent) / 2;
		
		for (int i = rowStart; i < rowEnd; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				int cy;
				if (ascent[i] >= 0) {
					cy = y + baselineOffset + maxAscent - ascent[i];
				}
				else {
					cy = y + (height - m.getHeight()) / 2;
				}
				if (ltr) {
					m.setLocation(x, cy);
				}
				else {
					m.setLocation(target.getWidth() - x - m.getWidth(), cy);
				}
				x += m.getWidth() + getHgap();
			}
		}
		return height;
	}
	
	private void layoutContainerGlobal(Container target) {
		//Codigo copiado de http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4295966
		if (getVerticalAlignmentGlobal() != SwingConstants.TOP) {
			// first, find the highest and lowest points
			int high = Integer.MAX_VALUE;
			int low = 0;

			int nmembers = target.getComponentCount();
			for (int ii = 0; ii < nmembers; ++ii) {
				Component cmp = target.getComponent(ii);
				Point loc = cmp.getLocation();
				int top = loc.y;
				int btm = top + cmp.getHeight();
				low = Math.max(btm, low);
				high = Math.min(top, high);
			}
			// Now, calculate how far to drop each component.
			Insets insets = target.getInsets();
			int maxht = target.getHeight() - (insets.bottom + insets.top + getVgap() * 2);

			// delta is the amount to move each component
			int delta = maxht - low + high; // bottom alignment
			if (getVerticalAlignmentGlobal() == SwingConstants.CENTER)
				delta /= 2; // center alignment

			// Now, move each component down.
			for (int ii = 0; ii < nmembers; ++ii) {
				Component cmp = target.getComponent(ii);
				Point newLoc = cmp.getLocation();
				newLoc.y += delta;
				cmp.setLocation(newLoc);
			}
		}
	}
	
	public int getVerticalAlignment() {
		return verticalAlignment;
	}
	public void setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
	
	public void setVerticalAlignmentGlobal(int verticalAlignmentGlobal) {
		this.verticalAlignmentGlobal = verticalAlignmentGlobal;
	}
	public int getVerticalAlignmentGlobal() {
		return verticalAlignmentGlobal;
	}
	
	/**
	 * La alineación horizontal es la alineación normal del FlowLayout
	 * Se aplica tanto a nivel Global como particular
	 */
	public void setHorizontalAlignment(int horizontalAlignment) {
		
		int align = FlowLayout.CENTER;
		if (horizontalAlignment == SwingConstants.LEFT)
			align = FlowLayout.LEFT;
		else if (horizontalAlignment == SwingConstants.RIGHT)
			align = FlowLayout.RIGHT;
		
		setAlignment(align);
	}
	/**
	 * La alineación horizontal es la alineación normal del FlowLayout
	 * Se aplica tanto a nivel Global como particular
	 */
	public int getHorizontalAlignment() {
		
		int align = getAlignment();
		int alignH = SwingConstants.CENTER;
		if (align == FlowLayout.LEFT)
			alignH = SwingConstants.LEFT;
		else if (align == FlowLayout.RIGHT)
			alignH = SwingConstants.RIGHT;
		
		return alignH;
	}	
	
	public boolean isPreferredSizeVertical() {
		return preferredSizeVertical;
	}
	public void setPreferredSizeVertical(boolean preferredSizeVertical) {
		this.preferredSizeVertical = preferredSizeVertical;
	}
	
	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			if (isPreferredSizeVertical()) {
				//boolean layoutFirst = Security.getSystemProperty(Security.KEY_JAVA_SPECIFICATION_VERSION).compareTo("1.8") < 0;
				boolean layoutFirst = true;
				return preferredLayoutSize(target, layoutFirst);
			}
			else return super.preferredLayoutSize(target);
		}
	}
	private Dimension preferredLayoutSize(Container target, boolean layoutFirst) {
		
		if (layoutFirst)
			layoutContainer(target);
		
		Dimension dim = new Dimension(0, 0);
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxW = 0;
		int maxH = 0;
		boolean visibleComponentsInside = false;
		for (int i = 0; i < target.getComponentCount(); i++) {
			Component c = target.getComponent(i);
			if (c.isVisible()) {
				visibleComponentsInside = true;
				Rectangle cBounds = c.getBounds();
				int x = cBounds.x;
				int y = cBounds.y;
				int w = x + cBounds.width;
				int h = y + cBounds.height;
				if (x < minX)
					minX = x;
				if (y < minY)
					minY = y;
				if (w > maxW)
					maxW = w;
				if (h > maxH)
					maxH = h;
			}
		}
		
		if (visibleComponentsInside && maxW == 0 && maxH == 0) {
			if (layoutFirst)
				return super.preferredLayoutSize(target);
			else
				return preferredLayoutSize(target, true);
		}
		
		dim = new Dimension(maxW-minX, maxH-minY);
		/*Component firstComponent = target.getComponents();
		int nmembers = target.getComponentCount();
		boolean firstVisibleComponent = true;
		boolean useBaseline = getAlignOnBaseline();
		int maxAscent = 0;
		int maxDescent = 0;

		for (int i = 0; i < nmembers; i++) {
			Component m = target.getComponent(i);
			if (m.isVisible()) {
				Dimension d = m.getPreferredSize();
				dim.height = Math.max(dim.height, d.height);
				if (firstVisibleComponent) {
					firstVisibleComponent = false;
				} else {
					dim.width += hgap;
				}
				dim.width += d.width;
				if (useBaseline) {
					int baseline = m.getBaseline(d.width, d.height);
					if (baseline >= 0) {
						maxAscent = Math.max(maxAscent, baseline);
						maxDescent = Math.max(maxDescent, d.height
								- baseline);
					}
				}
			}
		}
		if (useBaseline) {
			dim.height = Math.max(maxAscent + maxDescent, dim.height);
		}
		*/
		Insets insets = target.getInsets();
		dim.width += insets.left + insets.right + getHgap() * 2;
		dim.height += insets.top + insets.bottom + getVgap() * 2;
		return dim;
	}
}
