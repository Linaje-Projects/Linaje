/*
 * Copyright (c) 1995, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package linaje.gui.layouts;

import java.awt.*;

/**
 * A vertical 'bag' of Components.  Allocates space for each Component from
 * top to bottom.
 *
 * @author      Herb Jellinek
 * 
 * Esto es un Backup del antiguo layout de sun.awt que no se encuentra en JDKs superiores a 1.6
 */
public class VerticalBagLayout implements LayoutManager {

    int vgap;

    /**
     * Constructs a new VerticalBagLayout.
     */
    public VerticalBagLayout() {
        this(0);
    }

    /**
     * Constructs a VerticalBagLayout with the specified gaps.
     * @param vgap the vertical gap
     */
    public VerticalBagLayout(int vgap) {
        this.vgap = vgap;
    }

    /**
     * Adds the specified named component to the layout.
     * @param name the String name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Returns the minimum dimensions needed to lay out the components
     * contained in the specified target container.
     * @param target the Container on which to do the layout
     * @see Container
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
        Dimension dim = new Dimension();
        int nmembers = target.getComponentCount();

        for (int i = 0; i < nmembers; i++) {
            Component comp = target.getComponent(i);
            if (comp.isVisible()) {
                Dimension d = comp.getMinimumSize();
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            }
        }

        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }

    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
        Dimension dim = new Dimension();
        int nmembers = target.getComponentCount();

        for (int i = 0; i < nmembers; i++) {
            Component comp = target.getComponent(i);
            //if (true || comp.isVisible()) {
                Dimension d = comp.getPreferredSize();
                /*if (comp instanceof JComponent) {
                	LayoutManager compLayout = ((JComponent) comp).getLayout();
                	d = compLayout.preferredLayoutSize(target);
                }*/
                dim.width = Math.max(d.width, dim.width);
                dim.height += d.height + vgap;
            //}
        }

        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }

    /**
     * Lays out the specified container. This method will actually reshape the
     * components in the specified target container in order to satisfy the
     * constraints of the VerticalBagLayout object.
     * @param target the component being laid out
     * @see Container
     */
    public void layoutContainer(Container target) {
        Insets insets = target.getInsets();
        int top = insets.top;
        //int bottom = target.getSize().height - insets.bottom;
        int left = insets.left;
        int right = target.getSize().width - insets.right;
        int nmembers = target.getComponentCount();

        for (int i = 0; i < nmembers; i++) {
            Component comp = target.getComponent(i);
            if (comp.isVisible()) {
                int compHeight = comp.getSize().height;
                comp.setSize(right - left, compHeight);
                Dimension d = comp.getPreferredSize();
                comp.setBounds(left, top, right - left, d.height);
                top += d.height + vgap;
            }
        }
    }

    /**
     * Returns the String representation of this VerticalBagLayout's values.
     */
    public String toString() {
        return getClass().getName() + "[vgap=" + vgap + "]";
    }
}