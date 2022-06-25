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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.Border;

import linaje.gui.layouts.LFlowLayout;
import linaje.gui.layouts.VerticalBagLayout;
import linaje.gui.ui.LProgressBarUI;
import linaje.gui.utils.ColorsGUI;
import linaje.gui.utils.GraphicsUtils;
import linaje.utils.Lists;

/**
 * Está  pensado para ser utilizado como GlassPane y mostrar en él las tareas "Task" ejecutadas mediante Tasks
 * 
 * Mostrará un TaskPanel en la esquina inferior izquierda,  por cada tarea que se esté ejecutando en segundo plano
 * Si se ejecuta una tarea en el modo de espera (Tasks.executeTaskWaiting(...) o Tasks.executeTaskAndWait(...))
 * se oscurecerá el panel y se mostrará un circulo de progreso grande en el centro
 * 
 * @see TaskPanel
 * @see Task
 * @see Tasks
 **/
@SuppressWarnings("serial")
public class WaitPanel extends JPanel implements MouseListener, MouseMotionListener, FocusListener {

	private boolean mIsRunning;
	private boolean mIsFadingOut;
	private Timer mTimer;

	private int mAngle;
	private int mFadeCount;
	private int mFadeLimit = 5;
	
	private float maxFade = DEFAULT_MAX_FADE;
	
	private boolean eventsBlocked;
	
	private JPanel panelTasks = null;
	private List<SwingWorker<?, ?>> currentWorkers = null;
	private List<SwingWorker<?, ?>> currentWaitWorkers = null;
	
	public static float DEFAULT_MAX_FADE = 0.5f;
	
	private Border tasksBorder = null;
	private Border tasksWaitingBorder = null;
	
	public WaitPanel() {
		super();
		setOpaque(false);
		LFlowLayout lFlowLayout = new LFlowLayout(FlowLayout.LEFT, SwingConstants.SOUTH, 5, 5, true);
		setLayout(lFlowLayout);
		add(getPanelTasks());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (!mIsRunning || !eventsBlocked) {
			return;
		}

		float fade = (float) mFadeCount / (float) mFadeLimit;
		if (getMaxFade() > 0) {
			//Oscurecemos gradualmente
			float alpha = getMaxFade() * fade;
			GraphicsUtils.obscureRect(g, getVisibleRect(), alpha);
		}
		
		// Paint the wait indicator.
		boolean indeterminate = true;
		double progressFactor = mAngle/360.0;
		Color color = ColorsGUI.getColorApp();
		Rectangle viewRect = new Rectangle();
		int circleSize = 100;
		viewRect.width = Math.min(circleSize, (int)(getWidth()*0.75));
		viewRect.height = Math.min(circleSize, (int)(getHeight()*0.75));
		viewRect.x = (getWidth() - viewRect.width) / 2;
		viewRect.y = (getHeight() - viewRect.height) / 2;
		
		//LProgressBarUI.paintCircleBar(g, viewRect, indeterminate, progressFactor, color, -1, fade);
		LProgressBarUI.paintCircleBarStroke(g, viewRect, indeterminate, progressFactor, color, -1, fade, SwingConstants.NORTH);
	}
	
	private void start() {
		super.setVisible(true);
		if (mIsRunning && !mIsFadingOut) {
			return;
		}

		// Run a thread for animation.
		mIsFadingOut = false;
		mFadeCount = 0;
		int fps = 60;
		int tick = 1000 / fps;
		if (!mIsRunning) {
			mIsRunning = true;
			mTimer = new Timer(tick, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (mIsRunning) {
						getPanelTasks().revalidate();
						repaint();
						mAngle += 6;
						if (mAngle >= 360) {
							mAngle = 0;
						}
						if (mIsFadingOut) {
							if (--mFadeCount <= 0) {
								mIsRunning = false;
								if (getCurrentWaitWorkers().isEmpty())
									setEventsBlocked(false);
								mTimer.stop();
							}
						} else if (mFadeCount < mFadeLimit) {
							mFadeCount++;
						}
					}
				}
			});
			mTimer.start();
		}
	}

	private void stop() {
		if (mIsRunning) {
			mIsFadingOut = true;
		}
		else {
			super.setVisible(false);
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		if (visible) {
			add(getPanelTasks());
			if (!getCurrentWaitWorkers().isEmpty()) {
				setEventsBlocked(true);
			}
			requestFocus();
			start();
		}
		else {
			remove(getPanelTasks());
			stop();
		}
	}
	
	private void setEventsBlocked(boolean blocked) {
		
		this.eventsBlocked = blocked;
		
		removeMouseListener(this);
		removeMouseMotionListener(this);
		removeFocusListener(this);
		
		Border border = getTasksBorder();
		if (blocked) {
			addMouseListener(this);
			addMouseMotionListener(this);
			addFocusListener(this);
			
			border = getTasksWaitingBorder();
		}
		
		getPanelTasks().setBorder(border);
	}
	
	private JPanel getPanelTasks() {
		if (panelTasks == null) {
			panelTasks = new JPanel(new VerticalBagLayout()) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Rectangle rect = new Rectangle(getInsets().left, getInsets().top, getWidth() - getInsets().left - getInsets().right, getHeight() - getInsets().top - getInsets().bottom);
					GraphicsUtils.obscureRect(g, rect, 0.5f);
				}
			};			
			panelTasks.setOpaque(false);
			panelTasks.setBorder(getTasksBorder());
		}
		return panelTasks;
	}
	
	private Border getTasksBorder() {
		if (tasksBorder == null) {
			tasksBorder = new RoundedBorder();
		}
		return tasksBorder;
	}
	private Border getTasksWaitingBorder() {
		if (tasksWaitingBorder == null) {
			tasksWaitingBorder = BorderFactory.createLineBorder(ColorsGUI.getColorBorder(), 1, true);
		}
		return tasksWaitingBorder;
	}

	private List<SwingWorker<?, ?>> getCurrentWorkers() {
		if (currentWorkers == null)
			currentWorkers = Lists.newList();
		return currentWorkers;
	}
	
	private List<SwingWorker<?, ?>> getCurrentWaitWorkers() {
		if (currentWaitWorkers == null)
			currentWaitWorkers = Lists.newList();
		return currentWaitWorkers;
	}
	
	public void executeTask(final Task<?, ?> task, final boolean wait) {
			
		StateValue stateValue = task.getWorker().getState();
		//!getCurrentWorkers().contains(task.getWorker(), este control extra es porque si se ejecuta una tarea reutilizable varias veces seguidas
		//y hay mas de 10 tareas ejecutandose en el pool de tareas se añadiría la misma tarea reutilizable varias veces al estar en PENDING y no en STARTED
		//y no queremos eso, sino que una tarea reutilizable solo se puede ejecutar una a la vez
		if ((stateValue == StateValue.PENDING && !getCurrentWorkers().contains(task.getWorker())) || stateValue == StateValue.DONE) {
		
			task.execute();
			//Cogemos el worker después de ejecutar, ya que si su stateValue era DONE se creará uno nuevo al ejecutar
			final SwingWorker<?, ?> worker = task.getWorker();
			
			final TaskPanel taskPanel = new TaskPanel(task);
			getCurrentWorkers().add(worker);
			if (wait)
				getCurrentWaitWorkers().add(worker);
			getPanelTasks().add(taskPanel);
			setVisible(true);
			
			worker.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("state")) {
						SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
						if (state == StateValue.DONE) {
							getCurrentWorkers().remove(worker);
							if (wait)
								getCurrentWaitWorkers().remove(worker);
							if (getCurrentWorkers().isEmpty())
								setVisible(false);
							getPanelTasks().remove(taskPanel);
							getPanelTasks().revalidate();
							getPanelTasks().repaint();
							worker.removePropertyChangeListener(this);
							task.setMessage(null);
						}
					}
				}
			});
		}
	}

	public float getMaxFade() {
		return maxFade;
	}
	public void setMaxFade(float maxFade) {
		if (maxFade < 0 || maxFade > 1)
			this.maxFade = DEFAULT_MAX_FADE;
		else
			this.maxFade = maxFade;
	}
	
	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {
		//Impedimos que los componentes puedan obtener el foco
		//Si permitimos que otra ventana recupere el foco
		if (isVisible() && !(e.getOppositeComponent() instanceof Window))
			requestFocus();
	}
	
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
