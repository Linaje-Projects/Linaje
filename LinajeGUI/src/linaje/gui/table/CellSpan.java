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
package linaje.gui.table;

public class CellSpan {
	
	public static final int COMBINE_CELLS_NEVER = 0;
	public static final int COMBINE_CELLS_SAME_VALUE = 1;
	public static final int COMBINE_CELLS_BLANK = 2;
	public static final int COMBINE_CELLS_BOTH = 3;
	public static final int COMBINE_CELLS_ALWAYS = 4;
	
	private int type = COMBINE_CELLS_NEVER;
	private boolean combineIfGroupedHeader = false;
	private String actionCondition = null;

	public CellSpan() {
		super();
	}
	
	public String getActionCondition() {
		return actionCondition;
	}
	public boolean isCombineIfGroupedHeader() {
		return combineIfGroupedHeader;
	}
	public int getType() {
		return type;
	}
	
	public void setActionCondition(String actionCondition) {
		this.actionCondition = actionCondition;
	}
	public void setCombineIfGroupedHeader(boolean combineIfGroupedHeader) {
		this.combineIfGroupedHeader = combineIfGroupedHeader;
	}
	public void setType(int type) {
		this.type = type;
	}
}
