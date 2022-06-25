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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.UISupportUtils;
import linaje.utils.Strings;

public class LMenuItemUI extends BasicMenuItemUI {
	
	public LMenuItemUI() {
		super();
	}
	
	 public static ComponentUI createUI(JComponent c) {
        return new LMenuItemUI();
    }
		
	protected void installDefaults(){
		super.installDefaults();
        LookAndFeel.installBorder(menuItem, getPropertyPrefix() + ".border");
        LookAndFeel.installProperty(menuItem, "opaque", Boolean.FALSE);
        acceleratorForeground = ColorsGUI.getColorInfo();
        acceleratorSelectionForeground = ColorsGUI.getColorInfo();
    }
	
	protected String getPropertyPrefix() {
		//En BasicMenuItemUI esperan el prefijo sin punto
		return LinajeLookAndFeel.getUIName(this.getClass());
        //return UISupport.getPropertyPrefix(this.getClass());
    }
	
	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		
		AbstractButton b = (AbstractButton) c;
		String[] lineas = Strings.getLines(b.getText());
		int numLineas = lineas.length;
		//Si hay mas de una linea o el horizontalTextPosition es CENTER se pintará el icono relativo al texto en paintText(...)
		if (numLineas <= 1 || b.getHorizontalTextPosition() != SwingConstants.CENTER) {
			UISupportButtons.paintIcon(g, b, iconRect);
		}
	}

	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
		//UISupportButtons.paintText(g, menuItem, textRect, text);
		UISupportButtons.paintTextClassicAndIcon(g, menuItem, textRect, text);
	}
	
	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		
		ButtonModel buttonModel = menuItem.getModel();
		boolean rollover = buttonModel.isArmed();
		buttonModel.setRollover(rollover);
		if (rollover) {
			UISupportButtons.paintButtonBackground(g, menuItem, true);
			Rectangle rects = UISupportUtils.getBackgroundRects(menuItem);
			if (buttonModel.isEnabled()) {
				//g.setColor(Colors.darker(g.getColor(), 0.2));
				g.drawRect(rects.x, rects.y, rects.width, rects.height);
			}
		}
		else {
			super.paintBackground(g, menuItem, ColorsGUI.getFirstOpaqueParentBackground(menuItem.getParent()));
		}
	}
	
	/**
	 *  COPIA DE BasicMenuItemUI (Métodos privados que necesito sobreescribir y no puedo)
	 */
	
	protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon,
			Icon arrowIcon, Color background, Color foreground,
			int defaultTextIconGap) {
		// Save original graphics font and color
		Font holdf = g.getFont();
		Color holdc = g.getColor();

		JMenuItem mi = (JMenuItem) c;
		g.setFont(mi.getFont());

		Rectangle viewRect = new Rectangle(0, 0, mi.getWidth(), mi.getHeight());
		applyInsets(viewRect, mi.getInsets());

		MenuItemLayoutHelper lh = new LMenuItemLayoutHelper(mi, checkIcon,
				arrowIcon, viewRect, defaultTextIconGap, acceleratorDelimiter,
				mi.getComponentOrientation().isLeftToRight(), mi.getFont(),
				acceleratorFont,
				MenuItemLayoutHelper.useCheckAndArrow(menuItem),
				getPropertyPrefix());
		
		MenuItemLayoutHelper.LayoutResult lr = lh.layoutMenuItem();

		paintBackground(g, mi, background);
		paintCheckIcon(g, lh, lr, holdc, foreground);
		paintIcon(g, lh, lr, holdc);
		paintText(g, lh, lr);
		paintAccText(g, lh, lr);
		paintArrowIcon(g, lh, lr, foreground);

		// Restore original graphics font and color
		g.setColor(holdc);
		g.setFont(holdf);
	}
	
	protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon,
			Icon arrowIcon, int defaultTextIconGap) {

		// The method also determines the preferred width of the
		// parent popup menu (through DefaultMenuLayout class).
		// The menu width equals to the maximal width
		// among child menu items.

		// Menu item width will be a sum of the widest check icon, label,
		// arrow icon and accelerator text among neighbor menu items.
		// For the latest menu item we will know the maximal widths exactly.
		// It will be the widest menu item and it will determine
		// the width of the parent popup menu.

		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// There is a conceptual problem: if user sets preferred size manually
		// for a menu item, this method won't be called for it
		// (see JComponent.getPreferredSize()),
		// maximal widths won't be calculated, other menu items won't be able
		// to take them into account and will be layouted in such a way,
		// as there is no the item with manual preferred size.
		// But after the first paint() method call, all maximal widths
		// will be correctly calculated and layout of some menu items
		// can be changed. For example, it can cause a shift of
		// the icon and text when user points a menu item by mouse.

		JMenuItem mi = (JMenuItem) c;
		MenuItemLayoutHelper lh = new LMenuItemLayoutHelper(mi, checkIcon,
				arrowIcon, MenuItemLayoutHelper.createMaxRect(),
				defaultTextIconGap, acceleratorDelimiter,
				mi.getComponentOrientation().isLeftToRight(), mi.getFont(),
				acceleratorFont,
				MenuItemLayoutHelper.useCheckAndArrow(menuItem),
				getPropertyPrefix());

		Dimension result = new Dimension();

		// Calculate the result width
		result.width = lh.getLeadingGap();
		MenuItemLayoutHelper.addMaxWidth(lh.getCheckSize(),
				lh.getAfterCheckIconGap(), result);
		// Take into account mimimal text offset.
		if ((!lh.isTopLevelMenu()) && (lh.getMinTextOffset() > 0)
				&& (result.width < lh.getMinTextOffset())) {
			result.width = lh.getMinTextOffset();
		}
		MenuItemLayoutHelper
				.addMaxWidth(lh.getLabelSize(), lh.getGap(), result);
		MenuItemLayoutHelper.addMaxWidth(lh.getAccSize(), lh.getGap(), result);
		MenuItemLayoutHelper
				.addMaxWidth(lh.getArrowSize(), lh.getGap(), result);

		// Calculate the result height
		result.height = MenuItemLayoutHelper.max(lh.getCheckSize().getHeight(),
				lh.getLabelSize().getHeight(), lh.getAccSize().getHeight(), lh
						.getArrowSize().getHeight());

		// Take into account menu item insets
		Insets insets = lh.getMenuItem().getInsets();
		if (insets != null) {
			result.width += insets.left + insets.right;
			result.height += insets.top + insets.bottom;
		}

		// if the width is even, bump it up one. This is critical
		// for the focus dash line to draw properly
		if (result.width % 2 == 0) {
			result.width++;
		}

		// if the height is even, bump it up one. This is critical
		// for the text to center properly
		if (result.height % 2 == 0
				&& Boolean.TRUE != UIManager.get(getPropertyPrefix()
						+ ".evenHeight")) {
			result.height++;
		}

		return result;
	}
	/**
	 * Llamamamos internamente a nuestro paintIcon
	 */
	private void paintIcon(Graphics g, MenuItemLayoutHelper lh,
			MenuItemLayoutHelper.LayoutResult lr, Color holdc) {
		if (lh.getIcon() != null) {
			Icon icon;
			ButtonModel model = lh.getMenuItem().getModel();
			if (!model.isEnabled()) {
				icon = lh.getMenuItem().getDisabledIcon();
			} else if (model.isPressed() && model.isArmed()) {
				icon = lh.getMenuItem().getPressedIcon();
				if (icon == null) {
					// Use default icon
					icon = lh.getMenuItem().getIcon();
				}
			} else {
				icon = lh.getMenuItem().getIcon();
			}

			if (icon != null) {
				//icon.paintIcon(lh.getMenuItem(), g, lr.getIconRect().x,	lr.getIconRect().y);
				paintIcon(g, lh.getMenuItem(), lr.getIconRect());
				g.setColor(holdc);
			}
		}
	}

	private void paintCheckIcon(Graphics g, MenuItemLayoutHelper lh,
			MenuItemLayoutHelper.LayoutResult lr, Color holdc, Color foreground) {
		if (lh.getCheckIcon() != null) {
			ButtonModel model = lh.getMenuItem().getModel();
			if (model.isArmed()
					|| (lh.getMenuItem() instanceof JMenu && model.isSelected())) {
				g.setColor(foreground);
			} else {
				g.setColor(holdc);
			}
			if (lh.useCheckAndArrow()) {
				lh.getCheckIcon().paintIcon(lh.getMenuItem(), g,
						lr.getCheckRect().x, lr.getCheckRect().y);
			}
			g.setColor(holdc);
		}
	}

	private void paintAccText(Graphics g, MenuItemLayoutHelper lh,
			MenuItemLayoutHelper.LayoutResult lr) {
		if (!lh.getAccText().equals("")) {
			ButtonModel model = lh.getMenuItem().getModel();
			g.setFont(lh.getAccFontMetrics().getFont());
			if (!model.isEnabled()) {
				// *** paint the accText disabled
				if (disabledForeground != null) {
					g.setColor(disabledForeground);
					SwingUtilities2.drawString(lh.getMenuItem(), g,
							lh.getAccText(), lr.getAccRect().x,
							lr.getAccRect().y
									+ lh.getAccFontMetrics().getAscent());
				} else {
					g.setColor(lh.getMenuItem().getBackground().brighter());
					SwingUtilities2.drawString(lh.getMenuItem(), g,
							lh.getAccText(), lr.getAccRect().x,
							lr.getAccRect().y
									+ lh.getAccFontMetrics().getAscent());
					g.setColor(lh.getMenuItem().getBackground().darker());
					SwingUtilities2.drawString(lh.getMenuItem(), g,
							lh.getAccText(), lr.getAccRect().x - 1,
							lr.getAccRect().y + lh.getFontMetrics().getAscent()
									- 1);
				}
			} else {
				// *** paint the accText normally
				if (model.isArmed()
						|| (lh.getMenuItem() instanceof JMenu && model
								.isSelected())) {
					g.setColor(acceleratorSelectionForeground);
				} else {
					g.setColor(acceleratorForeground);
				}
				SwingUtilities2.drawString(lh.getMenuItem(), g,
						lh.getAccText(), lr.getAccRect().x, lr.getAccRect().y
								+ lh.getAccFontMetrics().getAscent());
			}
		}
	}
	
	private void paintArrowIcon(Graphics g, MenuItemLayoutHelper lh,
			MenuItemLayoutHelper.LayoutResult lr, Color foreground) {
		if (lh.getArrowIcon() != null) {
			ButtonModel model = lh.getMenuItem().getModel();
			if (model.isArmed()
					|| (lh.getMenuItem() instanceof JMenu && model.isSelected())) {
				g.setColor(foreground);
			}
			if (lh.useCheckAndArrow()) {
				lh.getArrowIcon().paintIcon(lh.getMenuItem(), g,
						lr.getArrowRect().x, lr.getArrowRect().y);
			}
		}
	}

	private void paintText(Graphics g, MenuItemLayoutHelper lh,
			MenuItemLayoutHelper.LayoutResult lr) {
		if (!lh.getText().equals("")) {
			if (lh.getHtmlView() != null) {
				// Text is HTML
				lh.getHtmlView().paint(g, lr.getTextRect());
			} else {
				// Text isn't HTML
				paintText(g, lh.getMenuItem(), lr.getTextRect(), lh.getText());
			}
		}
	}
	
	private void applyInsets(Rectangle rect, Insets insets) {
		if (insets != null) {
			rect.x += insets.left;
			rect.y += insets.top;
			rect.width -= (insets.right + rect.x);
			rect.height -= (insets.bottom + rect.y);
		}
	}
}
