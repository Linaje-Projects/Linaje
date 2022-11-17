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


import javax.swing.JFrame;

import linaje.LocalizedStrings;
import linaje.User;
import linaje.gui.AppGUI;
import linaje.gui.LPasswordField;
import linaje.gui.LSplashScreen;
import linaje.gui.Task;
import linaje.gui.Tasks;
import linaje.gui.console.ConsoleWindow;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.windows.DialogUserPassword;
import linaje.gui.windows.LDialogContent;

public class TestApp {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.tests.localization.linaje_gui_tests.properties
		
		public String passTip;
		public String verifying;
		public String authOK;
		public String authFail;
		public String bgTaskStart;
		public String doingSomethingInBg;
		public String ending;
		public String bgTaskEnd;
		public String initializing;
		public String doingSomething;
		public String doingAnother;
		public String almostDone;
		public String initialized;		
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_TESTS_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			/*try {
				AppGUI.getCurrentAppGUI().initFromConfig("linaje/gui/tests/resources/defaultAppConfig.cfg");
			} catch (Exception ex) {
				Console.printException(ex);
			}*/
			
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public TestApp() {
	}

	public static void main(String[] args) {
				
		LinajeLookAndFeel.init();
		
		ConsoleWindow.executeConsoleWindow();
				
		final LSplashScreen splash = AppGUI.getCurrentAppGUI().getSplashScreen();
		splash.setUserAuthenticated(false);
		JFrame frame = splash.showInFrame();
		
		@SuppressWarnings("serial")
		DialogUserPassword dlgUsuPass = new DialogUserPassword(frame) {
			
			@Override
			protected LPasswordField getTxtPassword() {
				LPasswordField txtPassword = super.getTxtPassword();
				txtPassword.setToolTipText(TestApp.TEXTS.passTip);
				return txtPassword;
			}
			
			@Override
			protected boolean verifyUserPassword() {
				Task<Boolean, Void> verifyTask = new Task<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						setMessage(TestApp.TEXTS.verifying);
						for (int i = 0; i < 30; i++) {
							//Do verifying job
							Thread.sleep(100);
							setProgress(100*i/30);
						}
						boolean authenticathed = getUser().equalsIgnoreCase(String.valueOf(getPassword()));
						setMessage(authenticathed ? TestApp.TEXTS.authOK : TestApp.TEXTS.authFail);
						
						User user = AppGUI.getCurrentApp().getCurrentUser();
						user.setFirstName("Pablo");
						user.setLastName("Linaje");
						user.setName("Pablo Linaje");
						user.setGender(User.GENDER_MALE);
						user.setId("PL1234");
						user.setLanguage("Spanish");
						user.setPhotoImage(null);
						
						return authenticathed;
					}
				};
				
				return Tasks.executeTaskAndWait(verifyTask, splash);
			}
		};
		
		dlgUsuPass.showInDialog();
		boolean userAuthenticated = dlgUsuPass.isVerified();
		if (!userAuthenticated) {
			System.exit(0);
		}
		
		//To see welcome message, user must be authenticated
		splash.setUserAuthenticated(userAuthenticated);
		
		Task<Void, Void> taskBackground = new Task<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				setMessage("1. "+TestApp.TEXTS.bgTaskStart);
				for (int i = 0; i < 100; i++) {
					//Do verifying job
					Thread.sleep(50);
					if (i > 20)
						setMessage("1. "+TestApp.TEXTS.doingSomethingInBg);
					if (i > 50)
						setMessage("1. "+TestApp.TEXTS.doingSomethingInBg+" 50%");
					if (i > 70)
						setMessage("1. "+TestApp.TEXTS.doingSomethingInBg+" "+TestApp.TEXTS.ending);
					
					setProgress(100*i/100);
				}
				setMessage("1. "+TestApp.TEXTS.bgTaskEnd);
				
				return null;
			}
		};
		
		Task<Void, Void> taskBackground2 = new Task<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				setMessage("2. "+TestApp.TEXTS.bgTaskStart);
				for (int i = 0; i < 100; i++) {
					//Do verifying job
					Thread.sleep(75);
					if (i > 20)
						setMessage("2. "+TestApp.TEXTS.doingSomethingInBg);
					if (i > 50)
						setMessage("2. "+TestApp.TEXTS.doingSomethingInBg+" 50%");
					if (i > 70)
						setMessage("2. "+TestApp.TEXTS.doingSomethingInBg+" "+TestApp.TEXTS.ending);
					
					setProgress(100*i/100);
				}
				setMessage("2. "+TestApp.TEXTS.bgTaskEnd);
				
				return null;
			}
		};
		
		Tasks.executeTask(taskBackground, splash);
		Tasks.executeTaskWaiting(taskBackground2, splash);
		
		Task<Void, Void> taskInitApp = new Task<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				setMessage(TestApp.TEXTS.initializing);
				for (int i = 0; i < 100; i++) {
					//Do verifying job
					Thread.sleep(100);
					if (i > 20)
						setMessage(TestApp.TEXTS.doingSomething);
					if (i > 50)
						setMessage(TestApp.TEXTS.doingAnother);
					if (i > 70)
						setMessage(TestApp.TEXTS.almostDone);
					
					setProgress(100*i/100);
				}
				setMessage(TestApp.TEXTS.initialized);
				
				return null;
			}
		};
		
		Tasks.executeTaskAndWait(taskInitApp, splash);
	
		splash.dispose();
		
		LDialogContent.showComponentInFrame(new UITest());
	}
}
