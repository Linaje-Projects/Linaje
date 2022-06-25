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
package linaje.utils;

import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

/**
 * Esta versión de Font incorpora layoutMode que usa LinajeLookAndFeel para dimensionar el alto de los textos y es útil para ahorrar espacio en vertical
 * 
 * 		LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX ->		Dimensionará con el alto máximo de la fuente y cabrán todos los caracterés posibles.
 * 												Es el tamaño por defecto de las fuentes y puede dejar algo de espacio arriba o abajo del carácter, aunque tenga el mayor alto posible.
 *  											
 * 		LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST ->	Dimensionará con un alto en el que cabrán la amplia mayoría de caractéres y simbolos (incluídas mayúsculas con acento).
 * 												Usar si queremos ahorrar algo de espacio vertical y no queremos tener problemas de espacio con ningún carácter.
 * 
 * 		LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON ->	Dimensionará con un alto en el que cabrán los caractéres mas comunes con mayúsculas y minúculas (Sin mayúsculas con acentos).
 * 												Usar si sabemos que no vamos a usar mayúsculas con acentos y queremos usar el mínimo espacio vertical uniforme para todos los componentes.
 * 
 * 		LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT ->	Dimensionará con el alto exacto del texto visualizado.
 * 												Hay que tener cuidado con éste último, ya que cada componente tendrá un 'preferredHigh' 
 * 												y no quedan bien por ejemplo botones con distinto alto cada uno. 
 * 												Es útil cuando queremos mostrar un texto en el mínimo espacio posible.
 * 
 * @see UISupport.layoutTextIcon(...)
 **/
@SuppressWarnings("serial")
public class LFont extends Font {

	public static final int LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX = 0;
	public static final int LAYOUT_MODE_TEXT_HEIGHT_FONT_MOST = 1;
	public static final int LAYOUT_MODE_TEXT_HEIGHT_FONT_COMMON = 2;
	public static final int LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT = 3;
	
	int layoutMode = LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
	
	public LFont(Map<? extends Attribute, ?> attributes) {
		super(attributes);
	}

	public LFont(Font font) {
		super(font);
	}

	public LFont(String name, int style, int size) {
		super(name, style, size);
	}
	
	public LFont(LFont lfont) {
		super(lfont);
		setLayoutMode(lfont.getLayoutMode());
	}
	
	public LFont(Map<? extends Attribute, ?> attributes, int textHeightLayoutMode) {
		super(attributes);
		setLayoutMode(textHeightLayoutMode);
	}

	public LFont(Font font, int textHeightLayoutMode) {
		super(font);
		setLayoutMode(textHeightLayoutMode);
	}

	public LFont(String name, int style, int size, int textHeightLayoutMode) {
		super(name, style, size);
		setLayoutMode(textHeightLayoutMode);
	}
	

	public int getLayoutMode() {
		return layoutMode;
	}

	private void setLayoutMode(int layoutMode) {
		if (layoutMode < LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX || layoutMode > LAYOUT_MODE_TEXT_HEIGHT_CURRENT_TEXT) {
			//throw new IllegalArgumentException("Invalid LFont textHeightLayoutMode");
			layoutMode = LAYOUT_MODE_TEXT_HEIGHT_FONT_MAX;
		}
		this.layoutMode = layoutMode;
	}
	
	public LFont deriveFontLayoutMode(int textHeightLayoutMode) {
		return new LFont(this, textHeightLayoutMode);
	}
	
	@Override
	public LFont deriveFont(float size) {
		Font font = super.deriveFont(size);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public LFont deriveFont(AffineTransform trans) {
		Font font = super.deriveFont(trans);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public LFont deriveFont(int style) {
		Font font = super.deriveFont(style);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public LFont deriveFont(int style, AffineTransform trans) {
		Font font = super.deriveFont(style, trans);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public LFont deriveFont(int style, float size) {
		Font font = super.deriveFont(style, size);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public LFont deriveFont(Map<? extends Attribute, ?> attributes) {
		Font font = super.deriveFont(attributes);
		return new LFont(font, getLayoutMode());
	}
	
	@Override
	public boolean equals(Object obj) {
		LFont lfont = obj != null && obj instanceof LFont ? (LFont) obj : null;
		return lfont != null && lfont.getLayoutMode() == layoutMode && super.equals(obj);
	}
	
	@Override
	public String toString() {
        
		String  strStyle;
        if (isBold()) {
            strStyle = isItalic() ? "bolditalic" : "bold";
        } else {
            strStyle = isItalic() ? "italic" : "plain";
        }

        return getClass().getName() + "[family=" + getFamily() + ",name=" + name + ",style=" +
            strStyle + ",size=" + size + ",layoutMode=" + layoutMode + "]";
    }
}
