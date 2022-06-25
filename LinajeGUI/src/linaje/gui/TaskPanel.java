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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import linaje.gui.ui.LProgressBarUI;
import linaje.gui.utils.ColorsGUI;
import linaje.statics.Constants;
import linaje.utils.StateColor;

/**
 * Panel utilizado para pintar el progreso de una tarea "Task"
 * Se compone de:
 *	- Una etiqueta donde se mostrará el mensaje de espera o progreso
 *	- Un botón "x" para cancelar la tarea si es cancelable
 *	- Una barra de progreso (circular)
 *
 * Es utilizado por WaitPanel para pintar las tareas en ejecución
 * 
 * @see WaitPanel
 * @see Task
 * @see Tasks
 **/
@SuppressWarnings("serial")
public class TaskPanel extends JPanel {
	
	private Task<?, ?> task = null;
	private SwingWorker<?, ?> worker = null;
	private JLabel label = null;
	private LButton button = null;
	private JProgressBar progressBar = null;
	private JLabel emptyLabel = null;
		
	private boolean indeterminateProgressBarVisible = false;
	
	public TaskPanel(Task<?, ?> task) {
		
		super();
		
		this.task = task;
		setOpaque(false);
		setLayout(new BorderLayout());
		add(getLabel(), BorderLayout.CENTER);
		add(getButton(), BorderLayout.WEST);
		if (!task.isCancellable()) {
			getButton().setEnabled(false);
			Icon iconX = getButton().getIcon();
			getButton().setIcon(Icons.getEmptyIcon(iconX.getIconWidth(), iconX.getIconHeight()));
		}
		
		//add(getProgressBar(), BorderLayout.EAST);
		setForeground(new StateColor(Color.white, null, null, ColorsGUI.getColorNegative(), null, null, null, null));
		
		setProgress(task.getProgress());
		
		task.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(Task.PROPERTY_MESSAGE)) {
					getLabel().setText((String) evt.getNewValue());
				}
				else if (evt.getPropertyName().equals(Task.PROPERTY_PROGRESS)) {
					setProgress((int) evt.getNewValue());
				}
			}
		});
		setWorker(task.getWorker());
		if (task.getMessage() != null)
			getLabel().setText(task.getMessage());
	}
	
	private void setProgress(int progress) {
		
		boolean indeterminateProgress = progress <= 0 || progress > 100;
		boolean hasChanged = progressBar == null;
		if (!hasChanged) {
			if (indeterminateProgress)
				hasChanged = !getProgressBar().isIndeterminate();
			else {
				hasChanged = getProgressBar().isIndeterminate() || progress != (int) (getProgressBar().getPercentComplete()*100);
			}
		}
		
		if (hasChanged) {
			
			getProgressBar().setIndeterminate(indeterminateProgress);
			getProgressBar().setStringPainted(!indeterminateProgress);
			if (indeterminateProgress) {
				add(getEmptyLabel(), BorderLayout.EAST);
				if (isIndeterminateProgressBarVisible())
					add(getProgressBar(), BorderLayout.EAST);
			}
			else {
				getProgressBar().setValue(progress);
				add(getProgressBar(), BorderLayout.EAST);
			}
		}
	}
	
	private Task<?, ?> getTask() {
		return task;
	}
	private SwingWorker<?, ?> getWorker() {
		return worker;
	}
	
	private JLabel getLabel() {
		if (label == null)
			label = new JLabel(Task.TEXTS.defaultMessage);
		return label;
	}
	
	private JLabel getEmptyLabel() {
		if (emptyLabel == null) {
			emptyLabel = new JLabel(Constants.VOID);
			emptyLabel.setPreferredSize(getProgressBar().getPreferredSize());
		}
		return emptyLabel;
	}
	
	private LButton getButton() {
		if (button == null) {
			button = new LButton(Constants.SPACE);
			int iconSize = button.getFontSize()/2 + 1;
			Icon icon = Icons.getIconX(iconSize);//, 1, button.getForeground());
			button.getButtonProperties().setIgnoreIconHeight(false);
			button.getButtonProperties().setIconForegroundEnabled(false);
			button.getButtonProperties().setPaintBgEffectsWhenTransparent(false);
			button.getButtonProperties().setShadowTextMode(LButtonProperties.SHADOW_TEXT_MODE_NEVER);
			button.getButtonProperties().setShadowPosition(SwingConstants.SOUTH_EAST);
			button.setIcon(icon);
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setOpaque(false);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getTask().cancel();
					//getWorker().cancel(true);
				}
			});
		}
		return button;
	}
	
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setUI(new LProgressBarUI(true));
			progressBar.setPreferredSize(getButton().getPreferredSize());
			progressBar.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
			progressBar.setFont(progressBar.getFont().deriveFont(6));
			progressBar.setOpaque(false);
		}
		return progressBar;
	}
	
	private void setWorker(SwingWorker<?, ?> worker) {
		this.worker = worker;
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				/*if (evt.getPropertyName().equals(Task.PROPERTY_PROGRESS)) {
					getProgressBar().setValue((int) evt.getNewValue());
					Consola.println("progress: "+evt.getNewValue());
				}
				else */if (evt.getPropertyName().equals("state")) {
					SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
					if (state == StateValue.DONE) {
						getWorker().removePropertyChangeListener(this);
					}
				}
			}
		});
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		getLabel().setForeground(fg);
		getButton().setForeground(fg);
		getProgressBar().setForeground(fg);
	}

	public boolean isIndeterminateProgressBarVisible() {
		return indeterminateProgressBarVisible;
	}

	public void setIndeterminateProgressBarVisible(boolean showIndeterminateProgressBar) {
		this.indeterminateProgressBarVisible = showIndeterminateProgressBar;
	}
}