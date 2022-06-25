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
package linaje.gui.components;

import java.util.Hashtable;

import linaje.LocalizedStrings;
import linaje.gui.LButton;
import linaje.gui.windows.ButtonsPanel;
import linaje.logs.Console;
import linaje.statics.Constants;

@SuppressWarnings("serial")
public class ButtonsPanelWizard extends ButtonsPanel {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.localization.linaje.properties
		
		public String next;
		public String previous;
		public String finish;
		public String step;
		public String of;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public static final int ASPECT_WIZARD = 9;
	
	private int steps = 1;
	private int currentStep = 1;
	private int previousStep = -1;
	
	private Hashtable<Integer, String> hashStepDescriptions = null;
	private Hashtable<Integer, String> hashStepTitles = null;
	
	public ButtonsPanelWizard() {
		super(ASPECT_WIZARD);
	}

	protected void processActionPerformed(LButton button) {
		
		super.processActionPerformed(button);
		
		String command = button.getActionCommand();
			
		if (command.equals(BUTTON_ACCEPT) || command.equals(BUTTON_YES) || command.equals(BUTTON_FINISH)) {
			setResponse(RESPONSE_ACCEPT_YES);
			if (isAutoCloseOnAccept())
				getDialogContent().dispose();
		}
		else if (command.equals(BUTTON_CANCEL)) {
			setResponse(RESPONSE_CANCEL);
			if (isAutoCloseOnCancel())
				getDialogContent().dispose();
		}
		else if (command.equals(BUTTON_NO)) {
			setResponse(RESPONSE_NO);
			if (isAutoCloseOnCancel())
				getDialogContent().dispose();
		}
		else if (command.equals(BUTTON_PREVIOUS)) {
			setPreviousStep(getCurrentStep());
			setCurrentStep(getCurrentStep() - 1);
			if (getDialogContent() != null)
				getDialogContent().executeStep(getCurrentStep());
		}
		else if (command.equals(BUTTON_NEXT)) {
			setPreviousStep(getCurrentStep());
			setCurrentStep(getCurrentStep() + 1);
			if (getDialogContent() != null)
				getDialogContent().executeStep(getCurrentStep());
		}
	}

	protected void setAspect(int aspect) {
		
		super.setAspect(aspect);;

		addButton(BUTTON_PREVIOUS, POSITION_RIGHT);
		addButton(BUTTON_NEXT, POSITION_RIGHT);
		addSeparator(POSITION_RIGHT);
		addButton(BUTTON_FINISH, POSITION_RIGHT);
		addButton(BUTTON_CANCEL, POSITION_RIGHT);
		
		getButton(BUTTON_PREVIOUS).addActionListener(actionListener);
		getButton(BUTTON_NEXT).addActionListener(actionListener);
		getButton(BUTTON_FINISH).addActionListener(actionListener);
		getButton(BUTTON_CANCEL).addActionListener(actionListener);
		setFocusButton(BUTTON_CANCEL);
		setCurrentStep(1);
	}

	public void updateWizardText() {
		
		if (getDialogContent() != null) {
			
			getDialogContent().getAuxDescriptionPanel().setTitle(getStepTitle(getCurrentStep()));
			getDialogContent().getAuxDescriptionPanel().setDescription(getDescripcionPaso(getCurrentStep()));
		}
	}
	
	public void setSteps(int newValue) {
		this.steps = newValue;
		if (getCurrentStep() <= getSteps()) {
			setCurrentStep(getCurrentStep());
		}
		else {
			setCurrentStep(getSteps());
		}
	}
	
	public void setCurrentStep(int currentStep) {
		
		this.currentStep = currentStep;
	
		LButton buttonPrevious = getButton(BUTTON_PREVIOUS);
		LButton buttonNext = getButton(BUTTON_NEXT);
		LButton buttonFinish = getButton(BUTTON_FINISH);
	
		if (getSteps() <= 1) {
			buttonPrevious.setEnabled(false);
			buttonNext.setEnabled(false);
			buttonFinish.setEnabled(true);
		}
		else {
			if (getCurrentStep() == 1) {
				buttonPrevious.setEnabled(false);
				buttonNext.setEnabled(true);
				buttonFinish.setEnabled(false);
				buttonNext.requestFocus();
			}
			else if (getCurrentStep() == getSteps()) {
				buttonPrevious.setEnabled(true);
				buttonNext.setEnabled(false);
				buttonFinish.setEnabled(true);
				buttonFinish.requestFocus();
			}
			else {
				buttonPrevious.setEnabled(true);
				buttonNext.setEnabled(true);
				buttonFinish.setEnabled(false);
			}
		}
		updateWizardText();
	}
	
	public void setPreviousStep(int previousStep) {
		this.previousStep = previousStep;
	}
	
	public int getSteps() {
		return steps;
	}
	
	public int getCurrentStep() {
		return currentStep;
	}
	public int getPreviousStep() {
		return previousStep;
	}
	
	public String getStepTitle(int stepNumber) {
	
		String stepTitle = getHashStepTitles().get(stepNumber);
	
		if (stepTitle != null)
			return stepTitle;
		else if (getSteps() > 1 && stepNumber <= getSteps())
			return TEXTS.step + stepNumber +TEXTS.of + getSteps();
		else
			return Constants.VOID;
	}
	
	public void setStepTitle(int stepNumber, String stepTitle) {
		
		getHashStepTitles().put(stepNumber, stepTitle);
	}
	
	public void setDescripcionPaso(int stepNumber, String stepDescription) {
		
		getHashStepDescriptions().put(stepNumber, stepDescription);
	}
	
	public String getDescripcionPaso(int stepNumber) {
		
		String stepDescription = getHashStepDescriptions().get(stepNumber);
		return stepDescription != null ? stepDescription.toString() : Constants.VOID;
	}
	
	private Hashtable<Integer, String> getHashStepDescriptions() {

		if (hashStepDescriptions == null)
			hashStepDescriptions = new Hashtable<Integer, String>();
		return hashStepDescriptions;
	}
	
	private Hashtable<Integer, String> getHashStepTitles() {
		
		if (hashStepTitles == null)
			hashStepTitles = new Hashtable<Integer, String>();
		return hashStepTitles;
	}
	
	public void destroy() {
		
		super.destroy();
		
		try {
			
			hashStepDescriptions = null;
			hashStepTitles = null;
	
			super.finalize();
		}
		catch (Throwable ex) {
			Console.printException(ex);	
		}
	}
}
