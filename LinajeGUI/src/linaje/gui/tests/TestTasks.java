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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import linaje.gui.LButton;
import linaje.gui.LCheckBox;
import linaje.gui.LPanel;
import linaje.gui.LTextField;
import linaje.gui.Task;
import linaje.gui.Tasks;
import linaje.gui.components.LabelTextField;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.logs.Console;

@SuppressWarnings("serial")
public class TestTasks extends LPanel {

	public TestTasks() {
		super();
		initialize();
	}

	private void initialize() {
		
		setName("Tasks");
		
		final LCheckBox checkWait = new LCheckBox("Wait for task completed");
		final LCheckBox checkCancel = new LCheckBox("Cancellable", true);
		LButton buttonReutilizable = new LButton("Start reutilizable task");
		LButton button = new LButton("Start task");
		final LabelTextField lblTxtTimeout = new LabelTextField();
		lblTxtTimeout.setAutoSizeLabel(true);
		lblTxtTimeout.setTextLabel("Timeout:");
		lblTxtTimeout.getTextField().setType(LTextField.TYPE_NUMBER);
		lblTxtTimeout.getTextField().setDecimals(0);
		lblTxtTimeout.getTextField().setValue(60);
		
		Dimension sizeTxt = lblTxtTimeout.getTextField().getPreferredSize();
		sizeTxt.width = 30;
		lblTxtTimeout.getTextField().setPreferredSize(sizeTxt);;
		
		add(checkWait);
		add(checkCancel);
		add(lblTxtTimeout);
		add(button);
		add(buttonReutilizable);
					
		final Task<Void, Void> taskReutilizable = new Task<Void, Void>("Tarea reutilizable", true) {
            @Override
            protected Void doInBackground() throws Exception {
            	try {
            		setMessage(null);
					for (int index = 0; index < 100; index++) {
	                    setProgress(index);
	                    /*if (index == 5)
	                    	setMessage("Starting reutilizable task...");
	                    else if (index == 15)
	                    	setMessage("Reutilizable task in progress");
	                    else if (index == 60)
	                    	setMessage("Ending reutilizable task...");*/
	                    //else if (index == 80)
	                    //	 throw new Exception("Error in reutilizable task");
	                
	                    //Consola.println("Progress global " + index);
	                    Thread.sleep(200);
	               }
                } catch (Exception ex) {
                	setMessage(ex.getMessage());
				}
            	return null;
            }
        };
        
        buttonReutilizable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				taskReutilizable.setCancellable(checkCancel.isSelected());
				taskReutilizable.setTimeoutInSeconds(lblTxtTimeout.getTextField().getValueNumber().intValue());
				if (checkWait.isSelected()) {
					Tasks.executeTaskAndWait(taskReutilizable, TestTasks.this);
					Console.println("Wait for task end");
				}
				else {
					Tasks.executeTask(taskReutilizable, TestTasks.this);
				}
			}
		});

        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				boolean cancellable = checkCancel.isSelected();
				int timeout = lblTxtTimeout.getTextField().getValueNumber().intValue();
				final Task<Void, Void> task = new Task<Void, Void>(null, timeout, cancellable) {
		            @Override
		            protected Void doInBackground() throws Exception {
		            	try {
							for (int index = 0; index < 100; index++) {
			                    if (index <= 100)
			                    //setProgress(index);
			                    if (index == 5)
			                    	setMessage("Starting task...");
			                    else if (index == 15)
			                    	setMessage("Task in progress...");
			                    else if (index == 60)
			                    	setMessage("Ending task...");
			                    
			                   // Consola.println("Progress " + index);
			                    //else if (index == 80)
			                    //	 throw new Exception("Error in task");
			                    Thread.sleep(100);
			                }
		                } catch (Exception ex) {
		                	setMessage(ex.getMessage());
						}
		            	return null;
		            }
		        };
		        
		        if (checkWait.isSelected()) {
					try {
			        	Tasks.executeTaskAndWait(task, TestTasks.this);
					}
			        catch (Exception ex) {
						Console.printException(ex);
					}
					Console.println("Wait for task end");
		        }
		        else {
			        Tasks.executeTask(task, TestTasks.this);
		        }
			}
		});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
}
