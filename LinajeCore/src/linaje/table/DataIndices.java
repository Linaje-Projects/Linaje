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
package linaje.table;

public class DataIndices { 
	
	private int codeIndex = 0;
	private int descriptionIndex = 0;
	private int typeIndex = -1;
	private int colorIndex = -1;
	
	public int getCodeIndex() {
		return codeIndex;
	}
	public int getDescriptionIndex() {
		return descriptionIndex;
	}
	public int getTypeIndex() {
		return typeIndex;
	}
	public int getColorIndex() {
		return colorIndex;
	}
	
	public void setCodeIndex(int codeIndex) {
		this.codeIndex = codeIndex;
	}
	public void setDescriptionIndex(int descriptionIndex) {
		this.descriptionIndex = descriptionIndex;
	}
	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}
	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}
}
