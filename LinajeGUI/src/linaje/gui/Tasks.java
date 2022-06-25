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

import java.awt.Component;
import java.awt.Window;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 * Ejecutaremos las tareas 'Task' en segundo plano a través de Tasks.
 * El glassPane de la ventana actual será reemplazado por WaitPanel, dónde se mostrará el progreso de las tareas,
 * por lo que si ya tenemos un glassPane y no queremos que esto pase tendremos que poner replaceGlassPaneAuto a false (No se mostrará el progreso de las tareas ni se oscurecerá el fondo).
 * 
 * executeTask: Ejecuta la tarea en segundo plano mostrando el progreso en el WaitPanel
 * executeTaskWaiting: Además bloqueará la interfaz de usuario oscureciendola y mostrando un circulo de progreso grande
 * executeTaskAndWait: Además de lo anterior, también bloqueará el hilo de ejecución principal
 **/
public class Tasks {

	private static WaitPanel waitPanel = null;
	private static boolean replaceGlassPaneAuto = true;
	
	public static WaitPanel getWaitPanel() {
		if (waitPanel == null)
			waitPanel = new WaitPanel();
		return waitPanel;
	}
	
	public static <T> T executeTaskAndWait(final Task<T, ?> task, final Component parent) {
		
		executeTask(task, parent, true);
				
		try {
			//Si pasa por el else es que hemos vuelto a ejecutar la misma tarea antes de que termine (llamamos a task.get() antes de que termine por lo que se bloqueará el UI hasta que termine)
			if (task.getLoop().enter())
				return task.get();
			else 
				return task.get();
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	public static void executeTaskWaiting(final Task<?, ?> task, final Component parent) {
		executeTask(task, parent, true);
	}
	
	public static void executeTask(final Task<?, ?> task, final Component parent) {
		
		executeTask(task, parent, false);
	}
	
	private static void executeTask(final Task<?, ?> task, final Component parent, boolean wait) {
		
		if (isReplaceGlassPaneAuto()) {
			Window ownerWindow = parent == null ? null : parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
			if (ownerWindow == null)
				ownerWindow = AppGUI.getCurrentAppGUI().getFrame();
			
			if (ownerWindow instanceof RootPaneContainer) {
				((RootPaneContainer) ownerWindow).setGlassPane(getWaitPanel());
			}
		}
		
		getWaitPanel().executeTask(task, wait);
	}
	
    public static boolean isReplaceGlassPaneAuto() {
		return replaceGlassPaneAuto;
	}

	public static void setReplaceGlassPaneAuto(boolean replaceGlassPaneAuto) {
		Tasks.replaceGlassPaneAuto = replaceGlassPaneAuto;
	}
	
	public static void main(String[] args) {
    	linaje.gui.tests.TestTasks.main(args);
	}
}
