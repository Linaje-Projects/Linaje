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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;

import linaje.gui.LLabel;
import linaje.gui.StateIcon;
import linaje.gui.utils.ColorsGUI;

public class Link extends LLabel {

	private static final long serialVersionUID = 1L;

	private boolean selected = false;
	
	private Font fontDefault = null; 
	private Font fontSelected = null; 
	
	private Color foregroundDefault = null;
	private Color foregroundSelected = null;
	private Color foregroundRollover = null;
	
	private Icon iconDefault = null;
	
	private EventListenerList listenerList = null;
	private String actionCommand = null;
	
	public Link() {
		super();
		initialize();
	}
	public Link(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		initialize();
	}
	public Link(Icon image) {
		super(image);
		initialize();
	}
	public Link(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		initialize();
	}
	public Link(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		initialize();
	}
	public Link(String text) {
		super(text);
		initialize();
	}

	private void initialize() {
		
		setFont(new Font("Verdana", Font.PLAIN, 11));
		setForeground(ColorsGUI.BLUE);
		setUnderlined(true);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		initConnections();
	}
	
	private void initConnections() {
		MouseListener ml = new MouseListener() {
			
			public void mouseEntered(MouseEvent e) {
				rollover(true);
			}
			public void mouseExited(MouseEvent e) {
				rollover(false);
			}	
			public void mouseReleased(MouseEvent e) {
				fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand(), e.getWhen(), e.getModifiers()));
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		};
		this.addMouseListener(ml);
	}
	
	public void rollover(boolean activar) {
		
		if (isEnabled()) {	
			if (activar) {
				super.setForeground(getForegroundRollover());
				setUnderlined(false);
				if (getIcon() != null && getIcon() instanceof StateIcon)
					super.setIcon(((StateIcon) getIcon()).getRolloverIcon());
			}
			else {
				setSelected(isSelected());
			}
		}
	}
	public boolean isSelected() {
		return selected;
	}
	private Font getFontDefault() {
		return fontDefault;
	}
	private Color getForegroundDefault() {	
		return foregroundDefault;
	}
	private Icon getIconDefault() {
		return iconDefault;
	}

	public Font getFontSelected() {
		if (fontSelected == null)
			fontSelected = new Font(getFontDefault().getName(), Font.BOLD, getFontDefault().getSize());
		return fontSelected;
	}
	public Color getForegroundSelected() {	
		if (foregroundSelected == null)
			foregroundSelected = new Color(getForegroundDefault().getRGB());
		return foregroundSelected;
	}

	public Color getForegroundRollover() {	
		if (foregroundRollover == null)
			foregroundRollover = ColorsGUI.getColorRolloverDark();
		return foregroundRollover;
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setUnderlined(enabled && !selected);
		setCursor(!enabled || isSelected() ? new Cursor(Cursor.DEFAULT_CURSOR) : new Cursor(Cursor.HAND_CURSOR));
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		setUnderlined(!selected && isEnabled());
		super.setForeground(isSelected() ? getForegroundSelected() : getForegroundDefault());
		super.setFont(selected ? getFontSelected() : getFontDefault());
		if (selected && getIcon() != null && getIcon() instanceof StateIcon && ((StateIcon) getIcon()).getSelectedIcon() != null)
			super.setIcon(((StateIcon) getIcon()).getSelectedIcon());
		else
			super.setIcon(getIconDefault());
		setCursor(selected || !isEnabled() ? new Cursor(Cursor.DEFAULT_CURSOR) : new Cursor(Cursor.HAND_CURSOR));
	}

	public void setForegroundSelected(Color foregroundSelected) {
		this.foregroundSelected = foregroundSelected;
		if (isSelected())
			super.setForeground(foregroundSelected);
	}
	
	public void setFontSelected(Font fontSelected) {
		this.fontSelected = fontSelected;
		if (isSelected())
			super.setFont(fontSelected);
	}
	
	public void setForegroundRollover(Color foregroundRollover) {	
		this.foregroundRollover = foregroundRollover;
	}
	
	public void setIcon(Icon icon) {
		iconDefault = icon;
		if (isSelected() && icon != null && icon instanceof StateIcon && ((StateIcon) icon).getSelectedIcon() != null) {
			Icon selectedIcon = ((StateIcon) icon).getSelectedIcon();
			super.setIcon(selectedIcon);
		}
		else
			super.setIcon(icon);
	}
	public void setFont(Font font) {
		fontDefault = font;
		super.setFont(font);
	}
	public void setForeground(Color foreground) {
		foregroundDefault = foreground;
		super.setForeground(foreground);
	}
	
	/**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>event</code> 
     * parameter.
     *
     * @param event  the <code>ActionEvent</code> object
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = getListenerList().getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                      String actionCommand = event.getActionCommand();
                      if(actionCommand == null) {
                         actionCommand = getActionCommand();
                      }
                      e = new ActionEvent(Link.this,
                                          ActionEvent.ACTION_PERFORMED,
                                          actionCommand,
                                          event.getWhen(),
                                          event.getModifiers());
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }
    protected EventListenerList getListenerList() {
    	if (listenerList == null)
    		listenerList = new EventListenerList();
    	return listenerList;
    }
    public void addActionListener(ActionListener l) {
    	getListenerList().add(ActionListener.class, l);
    }
    public void removeActionListener(ActionListener l) {
    	getListenerList().remove(ActionListener.class, l);
    }
	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}
	public String getActionCommand() {
		if (actionCommand == null)
			actionCommand = getText();
		return actionCommand;
	}
}
