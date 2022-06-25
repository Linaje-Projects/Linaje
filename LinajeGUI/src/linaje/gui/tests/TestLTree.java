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
package linaje.gui.tests;

import java.util.Arrays;

import javax.swing.JScrollPane;

import linaje.gui.cells.DataCell;
import linaje.gui.tree.LTree;
import linaje.gui.tree.LTreeNode;
import linaje.gui.tree.TreeModelEvent;
import linaje.gui.tree.TreeModelListener;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;
import linaje.utils.Numbers;

public class TestLTree {
	
	public static void main(String[] args) {	
		try {
	
			LinajeLookAndFeel.init();
			
			LTree<DataCell> lTree = new LTree<DataCell>();
						
			DataCell root = new DataCell(0, "Root");
			LTreeNode<DataCell> rootNode = new LTreeNode<DataCell>(root);
			lTree.setRootNode(rootNode);
			
			lTree.getModel().addTreeModelListener(new TreeModelListener<DataCell>() {
				
				public void treeStructureChanged(TreeModelEvent<DataCell> e) {
					Console.println("treeStructureChanged -- " + Arrays.toString(e.getChildren()) + " Parent path: " + Arrays.toString(e.getPath()));
				}
				public void treeNodesRemoved(TreeModelEvent<DataCell> e) {
					Console.println("treeNodesRemoved -- " + Arrays.toString(e.getChildren()) + " removed from: " + Arrays.toString(e.getPath()));
				}
				public void treeNodesInserted(TreeModelEvent<DataCell> e) {
					Console.println("treeNodesInserted -- " + Arrays.toString(e.getChildren()) + " inserted in parent: " + Arrays.toString(e.getPath()));
				}
				public void treeNodesChanged(TreeModelEvent<DataCell> e) {
					Console.println("treeNodesChanged -- " + Arrays.toString(e.getChildren()) + " Parent path: " + Arrays.toString(e.getPath()));
				}
			});
			
			int elemsN1 = Numbers.getRandomNumberInt(2, 6);
			for (int i = 0; i < elemsN1; i++) {
				int n = i+1;
				DataCell elemN1 = new DataCell(n, "Elemento " + n);
				LTreeNode<DataCell> nodeN1 = lTree.addElement(elemN1);
				int elemsN2 = Numbers.getRandomNumberInt(2, 6);
				for (int j = 0; j < elemsN2; j++) {
					n = j+1;
					DataCell elemN2 = new DataCell(n, "Elemento " + n);
					LTreeNode<DataCell> nodeN2 = lTree.addElement(elemN2, nodeN1);
					int elemsN3 = Numbers.getRandomNumberInt(2, 6);
					for (int k = 0; k < elemsN3; k++) {
						n = k+1;
						DataCell elemN3 = new DataCell(n, "Elemento " + n);
						lTree.addElement(elemN3, nodeN2);
					}
				}
			}
			
			lTree.expandRoot();
			lTree.setRootVisible(false);
			lTree.setShowsRootHandles(true);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setSize(300, 400);
			scrollPane.setViewportView(lTree);
			
			//JTree jtree = new JTree();
			//scrollPane.setViewportView(jtree);
			//jtree.setUI(new BasicTreeUI());
			
			LDialogContent.showComponentInFrame(scrollPane);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
