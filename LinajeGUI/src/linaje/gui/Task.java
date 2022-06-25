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

import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.Timer;

import linaje.LocalizedStrings;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;
import linaje.utils.Strings;

/**
 * La clase SwingWorker no se puede ejecutar mas de una vez, 
 * con Task podemos ejecutar tareas mas de una vez con una sola instancia
 * sin tener que reescribir el código
 *
 *	- Podemos definir un timeout:
 *		- TIME_OUT_MODE_ASK_FOR_CANCEL: Preguntará si queremos cancelar la tarea al cumplirse el timeout
 *		- TIME_OUT_MODE_ADVERT_CANCEL: Advertirá que se ha cumplido el timeout y que se cancelará la tarea
 *		- TIME_OUT_MODE_CANCEL_WITHOUT_ADVERT: Se cancelará autmáticamente la tarea al cumplirse el timeout sin mostrar mensaje de aviso
 *	- Nos permite asignar progreso de 0 a 100 y un mensaje de progreso
 *	- Si usamos un WaitPanel o TaskPanel
 *		- Se pintará el mensaje de progreso
 *		- Se pintará el porcentaje de progreso de la tarea
 *		- cancellable=true: se pintará una x junto al mensaje de progreso para poder cancelar la tarea al hacer click en ella
 * 
 * @see WaitPanel
 * @see TaskPanel
 * @see Tasks
 */
public abstract class Task<T, V> {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String aProcess;
		public String theProcess;
		public String itSeems;
		public String isTakingTooLong;
		public String notRespond;
		public String defaultMessage;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final String PROPERTY_PROGRESS = "progress";
	public static final String PROPERTY_MESSAGE = "message";
	
	public static final int DEFAULT_TIME_OUT = 60;
	
	public static final int TIME_OUT_MODE_ASK_FOR_CANCEL = 0;
	public static final int TIME_OUT_MODE_ADVERT_CANCEL = 1;
	public static final int TIME_OUT_MODE_CANCEL_WITHOUT_ADVERT = 2;
		
	private SwingWorker<T, V> worker = null;
	private volatile int progress = 0;
	private String message = null;
	private SecondaryLoop loop = null;
	
	private String name = null;
	private boolean cancellable = false;
	
	private Timer timer = null;
	private String timeoutMessage = null;
	private int timeoutEndMode = TIME_OUT_MODE_ASK_FOR_CANCEL;
	
	private PropertyChangeSupport propertyChangeSupport = null;
	
	public Task() {
		this(null);
	}
	public Task(String name) {
		this(name, DEFAULT_TIME_OUT);
	}
	public Task(String name, int timeOutInSeconds) {
		this(name, timeOutInSeconds, false);
	}
	public Task(String name, boolean cancellable) {
		this(name, DEFAULT_TIME_OUT, cancellable);
	}
	public Task(String name, int timeOutInSeconds, boolean cancellable) {
		this(name, timeOutInSeconds, cancellable, null);
	}
	public Task(String name, int timeOutInSeconds, boolean cancellable, String timeoutMessage) {
		this.name = name;
		this.cancellable = cancellable;	
		setTimeoutInSeconds(timeOutInSeconds);
		setTimeoutMessage(timeoutMessage);
	}

	public SwingWorker<T, V> getWorker() {
		if (worker == null) {
			worker = newWorker();
		}
		return worker;
	}
	
	private SwingWorker<T, V> newWorker() {
		return new SwingWorker<T, V>() {
			
			PropertyChangeListener swPropertyChangeListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(PROPERTY_PROGRESS))
						setProgress((int) evt.getNewValue());
				}
			};
			protected T doInBackground() throws Exception {
				Task.this.addPropertyChangeListener(swPropertyChangeListener);
				String taskName = getName() == null ? "Task" : "Task \"" + getName()+"\"";
				Console.println(taskName+" executed");
				if (timer != null)
					timer.restart();
				return Task.this.doInBackground();
			}
			@Override
			protected void done() {
				Task.this.removePropertyChangeListener(swPropertyChangeListener);
				if (Task.this.loop != null)
					Task.this.loop.exit();
				if (timer != null)
					timer.stop();
				Task.this.done();
				String taskName = getName() == null ? "Task" : "Task \"" + getName()+"\"";
				String doneResult = isCancelled() ? " cancelled" : " done";
				Console.println(taskName+doneResult);
			}
		};
	}

	protected abstract T doInBackground() throws Exception;
	
	protected void done() {
	}
	protected void cancel() {
		getWorker().cancel(true);
	}
	
	public int getProgress() {
		return progress;
	}
	protected void setProgress(int progress) {
		int oldValue = this.progress;
		int newValue = progress;
		if (oldValue != newValue) {
			this.progress = progress;
			firePropertyChange(PROPERTY_PROGRESS, oldValue, newValue);
		}
	}
	
	public String getMessage() {
		return message;
	}
	protected void setMessage(String message) {
		String oldValue = this.message;
		String newValue = message;
		if (oldValue != newValue) {
			this.message = message;
			firePropertyChange(PROPERTY_MESSAGE, oldValue, newValue);
		}
	}
	
	public String getName() {
		return name;
	}
	public boolean isCancellable() {
		return cancellable;
	}
	
	public SecondaryLoop getLoop() {
		if (loop == null) {
			Toolkit tk = Toolkit.getDefaultToolkit();
	        EventQueue eq = tk.getSystemEventQueue();
	        loop = eq.createSecondaryLoop();
		}
		return loop;
	}
		
	public SwingWorker<T, V> execute() {
		if (getWorker().getState() == StateValue.DONE) {
			worker = newWorker();
		}
		
		getWorker().execute();
		
		return getWorker();
    }
	
	public boolean isDone() {
		return getWorker().isDone();
	}
	public boolean isCancelled() {
		return getWorker().isCancelled();
	}
	public final T get() throws InterruptedException, ExecutionException {
        return getWorker().get();
    }
	
	public void setCancellable(boolean cancellable) {
		this.cancellable = cancellable;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTimeoutInSeconds() {
		return timer == null ? 0 : timer.getInitialDelay()/1000;
	}
	public void setTimeoutInSeconds(int timeOutInSeconds) {
		
		if (timeOutInSeconds > 0) {
			
			int delay = timeOutInSeconds*1000;
			if (timer == null) {
				timer = new Timer(delay, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (!isDone()) {
							int response = ButtonsPanel.RESPONSE_NO;
							if (getTimeoutEndMode() != TIME_OUT_MODE_CANCEL_WITHOUT_ADVERT) {
								if (getTimeoutEndMode() == TIME_OUT_MODE_ADVERT_CANCEL) {
									MessageDialog.showMessage(getTimeoutMessage(), MessageDialog.ICON_ERROR, ButtonsPanel.ASPECT_ACCEPT);
								}
								else
									response = MessageDialog.showMessage(getTimeoutMessage(), MessageDialog.ICON_WARNING, ButtonsPanel.ASPECT_YES_NO);
							}
							if (response == ButtonsPanel.RESPONSE_NO) {
								getWorker().cancel(true);
							}
							else {
								if (!isDone())
									timer.restart();
							}
						}
					}
				});
				timer.setRepeats(false);
			}
			else {
				if (timer.getInitialDelay() != delay) {
					//Reiniciamos el timer con el nuevo timeout
					timer.setInitialDelay(delay);
					timer.setDelay(delay);
					timer.restart();
				}
			}				
		}
		else {
			if (timer != null && timer.isRunning())
				timer.stop();
			timer = null;
		}
	}
	
	public void setTimeoutMessage(String timeoutMessage) {
		this.timeoutMessage = timeoutMessage;
	}
	public String getTimeoutMessage() {
		if (timeoutMessage == null) {
			String taskName = getName() == null ? TEXTS.aProcess : TEXTS.theProcess + "\""+getName()+"\"";
			if (getTimeoutEndMode() == TIME_OUT_MODE_ASK_FOR_CANCEL)
				timeoutMessage = TEXTS.itSeems + taskName + TEXTS.isTakingTooLong;
			else if (getTimeoutEndMode() == TIME_OUT_MODE_ADVERT_CANCEL)
				timeoutMessage = Strings.capitalize(taskName, false) + TEXTS.notRespond;
		}
		return timeoutMessage;
	}
	public void setTimeoutEndMode(int timeoutEndMode) {
		this.timeoutEndMode = timeoutEndMode;
	}
	public int getTimeoutEndMode() {
		return timeoutEndMode;
	}
	//
	// PropertyChange methods
	//
	private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
	
	private PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			propertyChangeSupport = new PropertyChangeSupport(this);
		return propertyChangeSupport;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
	}
}
