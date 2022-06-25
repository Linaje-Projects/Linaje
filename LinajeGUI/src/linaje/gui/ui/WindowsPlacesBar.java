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

/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import linaje.gui.LToggleButton;
import linaje.gui.ToolTip;
import sun.awt.shell.*;

/**
 * <b>WARNING:</b> This class is an implementation detailT and is only
 * public so that it can be used by two packages. You should NOT consider
 * this public API.
 * <p>
 *
 * @author Leif Samuelsson
 * 
 * Esto es una adaptación de sun.swing.WindowsPlacesBar para LFileChooserUI
 * 
 * @see LFileChooserUI
 */
@SuppressWarnings("serial")
public class WindowsPlacesBar extends JToolBar implements ActionListener, PropertyChangeListener {
   
	JFileChooser fc;
    LToggleButton[] buttons;
    ButtonGroup buttonGroup;
    File[] files;
    final Dimension buttonSize;

    public WindowsPlacesBar(JFileChooser fc) {
        super(JToolBar.VERTICAL);
        this.fc = fc;
        setFloatable(false);
        buttonSize = new Dimension(83, 69);

        FileSystemView fsv = fc.getFileSystemView();

        files = (File[]) ShellFolder.get("fileChooserShortcutPanelFolders");

        buttons = new LToggleButton[files.length];
        buttonGroup = new ButtonGroup();
        for (int i = 0; i < files.length; i++) {
            if (fsv.isFileSystemRoot(files[i])) {
                // Create special File wrapper for drive path
                files[i] = fsv.createFileObject(files[i].getAbsolutePath());
            }

            String folderName = fsv.getSystemDisplayName(files[i]);
            int index = folderName.lastIndexOf(File.separatorChar);
            if (index >= 0 && index < folderName.length() - 1) {
                folderName = folderName.substring(index + 1);
            }
			Icon icon;
			if (files[i] instanceof ShellFolder) {
				// We want a large icon, fsv only gives us a small.
				ShellFolder sf = (ShellFolder) files[i];
				Image image = sf.getIcon(true);

				if (image == null) {
					// Get default image
					image = (Image) ShellFolder.get("shell32LargeIcon 1");
				}

				icon = image == null ? null : new ImageIcon(image, sf.getFolderType());
			} else {
				icon = fsv.getSystemIcon(files[i]);
			}
			
			buttons[i] = new LToggleButton(folderName, icon);
			ToolTip.getInstance().registerComponent(buttons[i]);
			buttons[i].getButtonProperties().setIconForegroundEnabled(false);
			buttons[i].getButtonProperties().setRespectMaxMinSize(false);//Para que salga bien el tooltip
			buttons[i].setMargin(new Insets(3, 2, 1, 2));
			// buttons[i].setFocusPainted(false);
			// buttons[i].setIconTextGap(0);
			buttons[i].setHorizontalTextPosition(JToggleButton.CENTER);
			buttons[i].setVerticalTextPosition(JToggleButton.BOTTOM);
			buttons[i].setAlignmentX(JComponent.CENTER_ALIGNMENT);
			buttons[i].setPreferredSize(buttonSize);
			buttons[i].setMaximumSize(buttonSize);
			buttons[i].addActionListener(this);
			add(buttons[i]);

			buttonGroup.add(buttons[i]);
        }
        doDirectoryChanged(fc.getCurrentDirectory());
    }

    protected void doDirectoryChanged(File f) {
        for (int i=0; i<buttons.length; i++) {
            JToggleButton b = buttons[i];
            if (files[i].equals(f)) {
                b.setSelected(true);
                break;
            } else if (b.isSelected()) {
                // Remove temporarily from group because it doesn't
                // allow for no button to be selected.
                buttonGroup.remove(b);
                b.setSelected(false);
                buttonGroup.add(b);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop == JFileChooser.DIRECTORY_CHANGED_PROPERTY) {
            doDirectoryChanged(fc.getCurrentDirectory());
        }
    }

    public void actionPerformed(ActionEvent e) {
        JToggleButton b = (JToggleButton)e.getSource();
        for (int i=0; i<buttons.length; i++) {
            if (b == buttons[i]) {
                fc.setCurrentDirectory(files[i]);
                break;
            }
        }
    }

    public Dimension getPreferredSize() {
        Dimension min  = super.getMinimumSize();
        Dimension pref = super.getPreferredSize();
        int h = min.height;
        if (buttons != null && buttons.length > 0 && buttons.length < 5) {
            JToggleButton b = buttons[0];
            if (b != null) {
                int bh = 5 * (b.getPreferredSize().height + 1);
                if (bh > h) {
                    h = bh;
                }
            }
        }
        if (h > pref.height) {
            pref = new Dimension(pref.width, h);
        }
        return pref;
    }
}
