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

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ScrollablePanel extends JPanel implements Scrollable {
	
	private int incrementoScrollHorizontal = 10;
	private int incrementoScrollVertical = 10;
	
	public ScrollablePanel() {
		super();
		setLayout(null);
	}
	
	/**
	 * Returns the preferred size of the viewport for a view component.
	 * For example the preferredSize of a JList component is the size
	 * required to acommodate all of the cells in its list however the
	 * value of preferredScrollableViewportSize is the size required for
	 * JList.getVisibleRowCount() rows.   A component without any properties
	 * that would effect the viewport size should just return 
	 * getPreferredSize() here.
	 * 
	 * @return The preferredSize of a JViewport whose view is this Scrollable.
	 * @see JViewport#getPreferredSize
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	/**
	 * Components that display logical rows or columns should compute
	 * the scroll increment that will completely expose one block
	 * of rows or columns, depending on the value of orientation. 
	 * <p>
	 * Scrolling containers, like JScrollPane, will use this method
	 * each time the user requests a block scroll.
	 * 
	 * @param visibleRect The view area visible within the viewport
	 * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The "block" increment for scrolling in the specified direction.
	 * @see JScrollBar#setBlockIncrement
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width - 1;
		else
			return visibleRect.height - 1;
	}
	
	/**
	 * Return true if a viewport should always force the height of this 
	 * Scrollable to match the height of the viewport.  For example a 
	 * columnar text view that flowed text in left to right columns 
	 * could effectively disable vertical scrolling by returning
	 * true here.
	 * <p>
	 * Scrolling containers, like JViewport, will use this method each 
	 * time they are validated.  
	 * 
	 * @return True if a viewport should force the Scrollables height to match its own.
	 */
	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport) {
		    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}
	
	/**
	 * Return true if a viewport should always force the width of this 
	 * Scrollable to match the width of the viewport.  For example a noraml 
	 * text view that supported line wrapping would return true here, since it
	 * would be undesirable for wrapped lines to disappear beyond the right
	 * edge of the viewport.  Note that returning true for a Scrollable
	 * whose ancestor is a JScrollPane effectively disables horizontal
	 * scrolling.
	 * <p>
	 * Scrolling containers, like JViewport, will use this method each 
	 * time they are validated.  
	 * 
	 * @return True if a viewport should force the Scrollables width to match its own.
	 */
	public boolean getScrollableTracksViewportWidth() {
		if (getParent() instanceof JViewport) {
		    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
		}
		return false;
	}
	/**
	 * Devuelve los pixels que se desplazará la tabla al pulsar sobre los botones del scroll
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return getIncrementoScrollHorizontal();
		else
			return getIncrementoScrollVertical();
	}
	
	public void setIncremento(int newIncremento) {	
		setIncrementoScrollHorizontal(newIncremento);
		setIncrementoScrollVertical(newIncremento);
	}
	public void setIncrementoScrollHorizontal(int newIncrementoScrollHorizontal) {
		incrementoScrollHorizontal = newIncrementoScrollHorizontal;
	}
	public void setIncrementoScrollVertical(int newIncrementoScrollVertical) {
		incrementoScrollVertical = newIncrementoScrollVertical;
	}
	public int getIncrementoScrollHorizontal() {
		return incrementoScrollHorizontal;
	}
	public int getIncrementoScrollVertical() {
		return incrementoScrollVertical;
	}
}
