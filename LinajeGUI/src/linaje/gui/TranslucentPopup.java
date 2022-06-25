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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.border.Border;

import linaje.gui.utils.UtilsGUI;
import linaje.utils.Security;


public class TranslucentPopup extends Popup {
	
	/**
     * Max number of items to store in the cache.
     */
    private static final int MAX_CACHE_SIZE = 5;

    /**
     * The cache to use for TranslucentPopups.
     */
    private static List<TranslucentPopup> cache;

    /**
     * The singleton instance used to draw all borders.
     */
    //private static final Border SHADOW_BORDER = DropShadowBorder.getInstance();
    //private static final Border SHADOW_BORDER = BorderFactory.createMatteBorder(0,0,3,3, SystemColor.controlShadow);
    private static final Border SHADOW_BORDER = getShadowBorder();
    	
    /**
     * The contents of the popup.
     */
    private Component contents;

    /**
     * The real popup. The #show() and #hide() methods will delegate
     * all calls to these popup.
     */
    private Popup popup;

    /**
     * The border of the contents' parent replaced by SHADOW_BORDER.
     */
    private Border oldBorder;

    /**
     * The old value of the opaque property of the contents' parent.
     */
    private boolean oldOpaque;

    /**
     * The heavy weight container of the popup contents, may be null.
     */
    private Container heavyWeightContainer;
    
    private Component owner;

    /**
     * Returns a previously used <code>TranslucentPopup</code>, or a new one
     * if none of the popups have been recycled.
     */
    static Popup getInstance(Component owner, Component contents, int x, int y, Popup delegate) {
    	
    	TranslucentPopup result;
        synchronized (TranslucentPopup.class) {
            if (cache == null) {
                cache = new ArrayList<TranslucentPopup>(MAX_CACHE_SIZE);
            }
            if (cache.size() > 0) {
                result = cache.remove(0);
            } else {
                result = new TranslucentPopup();
            }
        }
        result.reset(owner, contents, x, y, delegate);
        return result;
    }

    /**
     * Recycles the TranslucentPopup.
     */
    private static void recycle(TranslucentPopup popup) {
        synchronized (TranslucentPopup.class) {
            if (cache.size() < MAX_CACHE_SIZE) {
                cache.add(popup);
            }
        }
    }

    /**
     * Hides and disposes of the <code>Popup</code>. Once a <code>Popup</code>
     * has been disposed you should no longer invoke methods on it. A
     * <code>dispose</code>d <code>Popup</code> may be reclaimed and later used
     * based on the <code>PopupFactory</code>. As such, if you invoke methods
     * on a <code>disposed</code> <code>Popup</code>, indeterminate
     * behavior will result.<p>
     *
     * In addition to the superclass behavior, we reset the stored
     * horizontal and vertical drop shadows - if any.
     */
    public void hide() {
        
    	if (contents == null)
            return;

        JComponent parent = (JComponent) contents.getParent();
        popup.hide();
        if ((parent != null) && parent.getBorder() == SHADOW_BORDER) {
            parent.setBorder(oldBorder);
            parent.setOpaque(oldOpaque);
            oldBorder = null;
            if (heavyWeightContainer != null) {
                heavyWeightContainer = null;
            }
        }
        //owner = null;
        contents = null;
        popup = null;
        recycle(this);
    }

    /**
     * Makes the <code>Popup</code> visible. If the popup has a
     * heavy-weight container, we try to snapshot the background.
     * If the <code>Popup</code> is currently visible, it remains visible.
     */
    public void show() {
    	
    	toFront(owner);
    	
        popup.show();
    }
    
    /**
	 * Forzamos que el popup, que es ligero, se pinte sobre componentes pesados AWT
	 * haciendo un revalidate del JComponent de mayor nivel que encontremos
	 */
	private void toFront(Component invoker) {
		
		Component padre = invoker;
		JComponent jComponentMayor = null;
		
		while (padre != null) {
			if (padre instanceof JComponent) {
				jComponentMayor = (JComponent) padre;
				jComponentMayor.revalidate();
				jComponentMayor.validate();
				jComponentMayor.repaint();
			}
			padre = padre.getParent();
		}
		
		if (jComponentMayor != null) {
			jComponentMayor.revalidate();
			jComponentMayor.validate();
			jComponentMayor.repaint();
		}
	}

    /**
     * Reinitializes this TranslucentPopup using the given parameters.
     *
     * @param owner component mouse coordinates are relative to, may be null
     * @param contents the contents of the popup
     * @param x the desired x location of the popup
     * @param y the desired y location of the popup
     * @param popup the popup to wrap
     */
    private void reset(Component owner, Component contents, int x, int y, Popup popup) {
        this.owner = owner;
        this.contents = contents;
        this.popup = popup;
        //this.x = x;
        //this.y = y;
        if (owner instanceof JComboBox) {
            return;
        }
        // Do not install the shadow border when the contents
        // has a preferred size less than or equal to 0.
        // We can't use the size, because it is(0, 0) for new popups.
        Dimension contentsPrefSize = contents.getPreferredSize();
        if ((contentsPrefSize.width <= 0) || (contentsPrefSize.height <= 0)) {
            return;
        }
        for(Container p = contents.getParent(); p != null; p = p.getParent()) {
            if ((p instanceof JWindow) || (p instanceof Panel)) {
				// Workaround for the gray rect problem.
				p.setBackground(contents.getBackground());
				heavyWeightContainer = p;
				if (p instanceof JWindow) {
					JWindow w = (JWindow) p;
					//Consola.println("Heavy weight");
					if (Security.getSystemProperty(Security.KEY_JAVA_SPECIFICATION_VERSION).compareTo("1.8") < 0) {
						w.dispose();
						if (UtilsGUI.isWindowOpaque(w)) {
							UtilsGUI.setWindowOpaque(w, false);
						}
						w.setVisible(true);
					} else {
						if (UtilsGUI.isWindowOpaque(w)) {
							UtilsGUI.setWindowOpaque(w, false);
						}
					}
				}
                break;
            }
        }
        JComponent parent = (JComponent) contents.getParent();
        oldOpaque = parent.isOpaque();
        oldBorder = parent.getBorder();
        parent.setOpaque(false);
        parent.setBorder(SHADOW_BORDER);
        // Pack it because we have changed the border.
        if (heavyWeightContainer != null) {
            heavyWeightContainer.setSize(heavyWeightContainer.getPreferredSize());
        } else {
            parent.setSize(parent.getPreferredSize());
        }
    }
    
    private static Border getShadowBorder() {
    	RoundedBorder roundedBorder = new RoundedBorder();
    	//Pintamos solo la sombra sin borde
    	roundedBorder.setThicknessLineBorder(0);
    	//Ponemos ancho interior -1 para eliminar el hueco que provoca la curva del borde
    	roundedBorder.setThicknessInsetsExtra(-1);
    	return roundedBorder;
    }
}
