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

import java.awt.*;

import javax.swing.*;

import linaje.gui.utils.ColorsGUI;

/**
 * Añadidos respecto a un JPopupMenu:
 *  - Se pinta sobre componentes pesados
 *  - BETA: Mostrar una label de cabecera del popupMenu con forma de pestaña (No está pintando bien la transparencia junto a la label)
 **/
@SuppressWarnings("serial")
public class LPopupMenu extends JPopupMenu {
	
	private JLabel labelHeader = null;
	private Insets insetsLabel = new Insets(0, 5, 2, 5);
	
	public LPopupMenu() {
		super();
	}
	
	private void setLabelHeader(JLabel labelHeader, int alignment) {

		if (this.labelHeader != null)
			remove(0);
		
		JLabel clonedLabel = null;
		if (labelHeader != null) {
			clonedLabel = new JLabel(labelHeader.getText(), labelHeader.getIcon(), alignment);
			clonedLabel.setHorizontalTextPosition(labelHeader.getHorizontalTextPosition());
			clonedLabel.setForeground(labelHeader.getForeground());
			clonedLabel.setFont(labelHeader.getFont());
			// Metemos la label en un panel porque sino no se expande a lo ancho del popup
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = insetsLabel;
			gbc.weightx = 1.0D;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JPanel panelContenedor = new JPanel(new GridBagLayout());
			panelContenedor.setOpaque(false);
			panelContenedor.add(clonedLabel, gbc);

			insert(panelContenedor, 0);
		}
		this.labelHeader = clonedLabel;
	}

	public JLabel getLabelHeader() {
		return labelHeader;
	}

	public void show(Component invoker, int x, int y) {
		show(invoker, x, y, null, 0);
	}

	public void show(Component invoker, int x, int y, JLabel labelHeader, int alineacionCabecera) {
		
		toFront(invoker);
		
		setLabelHeader(labelHeader, alineacionCabecera);
		if (labelHeader != null) {
			if (alineacionCabecera == SwingConstants.LEFT) {
				Point locationPopup = new Point(x, y);
				SwingUtilities.convertPointToScreen(locationPopup, invoker);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				if (locationPopup.x + getPreferredSize().width > screenSize.width) {
					remove(0);
					alineacionCabecera = SwingConstants.RIGHT;
					setLabelHeader(labelHeader, alineacionCabecera);
				}
			}
			validate();
			if (alineacionCabecera == SwingConstants.RIGHT) {
				x = x - getPreferredSize().width + labelHeader.getPreferredSize().width;
				//x = x - 2;
				//y = y - 2;
			}
			else {
				x = x - 5;
				y = y - 5;
			}
		}
				 
		super.show(invoker, x, y);
	}

	public void paint(Graphics g) {

		super.paint(g);
		
		if (getLabelHeader() != null) {
			Dimension sizeLabel = getLabelHeader().getPreferredSize();
			boolean alinearDerecha = getLabelHeader().getHorizontalAlignment() == SwingConstants.RIGHT;
			Rectangle areaTransparente = new Rectangle();
			Rectangle areaLabel = new Rectangle();
	
			if (getWidth() - sizeLabel.width > (insetsLabel.left+insetsLabel.right)) {
				areaLabel.width = sizeLabel.width + insetsLabel.left+insetsLabel.right;
			}
			else {
				areaLabel.width = getWidth() - 1;
			}
			areaLabel.height = sizeLabel.height + insetsLabel.top+insetsLabel.bottom;
			areaTransparente.width = getWidth() - areaLabel.width;
			areaTransparente.height = areaLabel.height;
			
			areaLabel.x = alinearDerecha ? areaTransparente.width : 0;
			areaLabel.y = 0;
			areaTransparente.x = alinearDerecha ? 0 : areaLabel.width;
			areaTransparente.y = 0;
			
			if (getBorder() != null) {
				//Copiamos el borde superior y lateral del area transparente
				//Bajamos el borde superior
				Insets borderInsets = getBorder().getBorderInsets(this);
				g.copyArea(areaTransparente.x, areaTransparente.y, areaTransparente.width, borderInsets.top, 0, areaTransparente.height-borderInsets.top);
				if (alinearDerecha) {
					//Desplazamos el borde izquierdo a la izquierda de la label
					g.copyArea(areaTransparente.x, areaTransparente.y, borderInsets.left, areaTransparente.height, areaTransparente.width-borderInsets.left, 0);
					g.clearRect(areaTransparente.x, areaTransparente.y, areaTransparente.width-borderInsets.left, areaTransparente.height-borderInsets.top);
				}
				else {
					//Desplazamos el borde derecho a la derecha de la label
					g.copyArea(getWidth()-borderInsets.right, areaTransparente.y, borderInsets.right*2, areaTransparente.height, borderInsets.right-areaTransparente.width, 0);
					g.clearRect(areaTransparente.x + borderInsets.right, areaTransparente.y, areaTransparente.width + borderInsets.right + 50, areaTransparente.height-borderInsets.top);
					//g.setColor(Color.red);
					//g.fillRect(areaTransparente.x + borderInsets.right, areaTransparente.y, areaTransparente.width + borderInsets.right + 1000, areaTransparente.height-borderInsets.top);
				}
			}
			
			//Pintamos el hueco transparente
			Graphics2D g2d = (Graphics2D) g;
			Composite originalComposite = g2d.getComposite();
			float alpha = 0.20f;
			int type = AlphaComposite.SRC_OVER;
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2d.setComposite(composite);
					
			g.setColor(ColorsGUI.getColorBorder());
			GradientPaint gp = new GradientPaint(areaTransparente.x, areaTransparente.y, Color.white, areaTransparente.x, areaTransparente.height, ColorsGUI.getColorText(), true);
					
			g2d.setPaint(gp);
			g.fillRect(areaTransparente.x, areaTransparente.y, areaTransparente.width, areaTransparente.height);
			
			//Pintamos el resto opaco
			g2d.setComposite(originalComposite);
		}
	}
	
	private void toFront(Component invoker) {
		
		//Forzamos que el popup, que es ligero, se pinte sobre componentes pesados AWT
		// haciendo un revalidate del JComponent de mayor nivel que encontremos
		Component padre = invoker;
		JComponent lastParentJComponent = null;
		
		while (padre != null) {
			if (padre instanceof JComponent)
				lastParentJComponent = (JComponent) padre;
			padre = padre.getParent();
		}
		
		if (lastParentJComponent != null)
			lastParentJComponent.revalidate();
	}

	public boolean isLightWeightPopupEnabled() {
		//Lo forzamos porque si cambia no se pintan las transparencias
        return true;
        //return super.isLightWeightPopupEnabled();
    }
}
