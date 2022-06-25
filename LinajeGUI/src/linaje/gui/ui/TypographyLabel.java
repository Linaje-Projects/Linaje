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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.SwingConstants;

import linaje.gui.AppGUI;
import linaje.gui.LLabel;
import linaje.gui.LTextField;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.windows.LDialogContent;
import linaje.statics.Constants;
import linaje.utils.LFont;
import linaje.utils.Lists;


@SuppressWarnings("serial")
public class TypographyLabel extends LLabel {
	
	private LDialogContent dialogContentChangeText = null;
	private LTextField textFieldChangeText = null;
	
	public TypographyLabel() {
		super(Constants.VOID);
		initialize();
	}
	public TypographyLabel(String text) {
		super(text);
		initialize();
	}
	
	private void initialize() {
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.BOTTOM);
		LFont font = new LFont(AppGUI.getFont().getName(), Font.PLAIN, 60, LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST);
		setFont(font);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
				getDialogContentChangeText().showInDialog();
				setText(getTextFieldChangeText().getText());
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
	}
	
	private LTextField getTextFieldChangeText() {
		if (textFieldChangeText == null) {
			textFieldChangeText = new LTextField() {
				public void keyPressed(KeyEvent e) {
					super.keyPressed(e);
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						getDialogContentChangeText().dispose();
					}
				}
			};
			textFieldChangeText.setTextBackgroundVoid(LLabelUI.TYPOGRAPHY_TEXT_MOST);
			textFieldChangeText.setPreferredSize(new Dimension(200, textFieldChangeText.getPreferredSize().height));
			textFieldChangeText.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return textFieldChangeText;
	}
	
	public LDialogContent getDialogContentChangeText() {
		if (dialogContentChangeText == null) {
			dialogContentChangeText = new LDialogContent();
			dialogContentChangeText.setTitle("Sample text");
			dialogContentChangeText.setLayout(new LFlowLayout());
			dialogContentChangeText.add(getTextFieldChangeText());
			dialogContentChangeText.setSize(dialogContentChangeText.getPreferredSize());
		}
		return dialogContentChangeText;
	}
	
	private static Rectangle paintTextTypography(Graphics g, LLabel label, float alpha) {
		
		Graphics2D g2d = (Graphics2D) g.create();
		try {	
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			
			Rectangle textViewRect = new Rectangle();
			Rectangle textRect = new Rectangle();
			Rectangle iconRect = new Rectangle();
			List<Rectangle> textLinesBounds = Lists.newList();
			List<Point> offsets = Lists.newList();
			
			UISupport.layoutTextIcon(label, textViewRect, textRect, textLinesBounds, iconRect, null, offsets, label.getText(), label.getIcon(), label.getIconTextGap(), label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label.getHorizontalTextPosition());
			
			Color foreground = label.getForeground();
			
			g2d.setFont(label.getFont());
			Color underLineTextColor = null;
			Point globalOffsets = null;
			UISupport.drawText(g2d, label.getText(), textViewRect, textLinesBounds, foreground, offsets, globalOffsets, underLineTextColor);
			
			if (label.getIcon() != null) {
				label.getIcon().paintIcon(label, g2d, iconRect.x, iconRect.y);
			}
			
			FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
			
			int ascent = fm.getAscent();
			int descent = fm.getDescent();
			
			int yAscent = textViewRect.y - 1;// - label.getMargin().top;
			int yDescent = yAscent + ascent + descent + 1;
			int yBaseLine = yAscent + ascent + 2;
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			
			g2d.setPaint(Color.cyan); // Base line
			int x1 = textViewRect.x;
			int x2 = textViewRect.x + textViewRect.width;
			int y1 = yBaseLine;
			int y2 = y1;
			g2d.drawLine(x1, y1, x2, y2);
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
			
			g2d.setPaint(Color.green); // Ascent line
			y1 = yAscent;
			y2 = y1;
			g2d.drawLine(x1, y1, x2, y2);
			
			g2d.setPaint(Color.red); // Descent line
			y1 = yDescent;
			y2 = y1;
			g2d.drawLine(x1, y1, x2, y2);
			
			return textRect;
		
		} finally {
			g2d.dispose();
		}
	}

	private static void paintText(Graphics g, LLabel label, Rectangle textRectTypography) {
		
		Graphics2D g2d = (Graphics2D) g.create();
		
		try {
				
			Rectangle textViewRect = textRectTypography;
			Rectangle textRect = new Rectangle();
			Rectangle iconRect = new Rectangle();
			List<Rectangle> textLinesBounds = Lists.newList();
			List<Point> offsets = Lists.newList();
			
			UISupport.layoutTextIcon(label, textViewRect, textRect, textLinesBounds, iconRect, null, offsets, label.getText(), label.getIcon(), label.getIconTextGap(), label.getVerticalAlignment(), label.getHorizontalAlignment(), label.getVerticalTextPosition(), label.getHorizontalTextPosition());
			
			Color foreground = label.getForeground();
			
			g2d.setFont(label.getFont());
			Color underLineTextColor = null;//new Color(255, 0, 0, 128);
			Point globalOffsets = null;
			UISupport.drawText(g2d, label.getText(), textViewRect, textLinesBounds, foreground, offsets, globalOffsets, underLineTextColor);
			
			if (label.getIcon() != null) {
				label.getIcon().paintIcon(label, g2d, iconRect.x, iconRect.y);
			}
			
			g2d.setColor(Color.orange);
			for (int i = 0; i < textLinesBounds.size(); i++) {
				Rectangle textBounds = textLinesBounds.get(i);
				g2d.drawRect(textBounds.x, textBounds.y, textBounds.width-1, textBounds.height-1);
			}
		}
		finally {
			g2d.dispose();
		}
		
	}
	

	protected void paintComponent(Graphics g) {
		//super.paintComponent(g);
		LLabel labelTypography = getLabelTypography();
		Rectangle textRectTypography = paintTextTypography(g, labelTypography, 0.1f);
		paintText(g, this, textRectTypography);
	}
	
	@Override
	public void setText(String text) {
		if (text.equals(Constants.VOID))
			text = LLabelUI.TYPOGRAPHY_TEXT_MOST;
		super.setText(text);
	}
	
	private LLabel getLabelTypography() {
		LLabel labelTypography = new LLabel(LLabelUI.TYPOGRAPHY_TEXT_MOST, getIcon(), getHorizontalAlignment());
		labelTypography.setVerticalAlignment(getVerticalAlignment());
		labelTypography.setVerticalTextPosition(getVerticalTextPosition());
		labelTypography.setHorizontalTextPosition(getHorizontalTextPosition());
		labelTypography.setIconTextGap(getIconTextGap());
		labelTypography.setFont(new LFont(getFont(), LFont.LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX));
		labelTypography.setBounds(getBounds());
		return labelTypography;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getLabelTypography().getPreferredSize();
	}
}
