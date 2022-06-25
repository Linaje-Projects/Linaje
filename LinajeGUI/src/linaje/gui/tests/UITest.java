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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import linaje.comunications.Connection;
import linaje.comunications.ConnectionEvent;
import linaje.comunications.ConnectionListener;
import linaje.gui.AppGUI;
import linaje.gui.Icons;
import linaje.gui.LPanel;
import linaje.gui.LTabbedPane;
import linaje.gui.console.ConsoleWindow;
import linaje.gui.ui.GeneralUIProperties;
import linaje.gui.ui.LinajeLookAndFeel;
import linaje.gui.ui.UIConfig;
import linaje.gui.windows.ButtonsPanel;
import linaje.gui.windows.LDialogContent;
import linaje.gui.windows.MessageDialog;
import linaje.logs.Console;
import linaje.utils.ReflectAccessSupport;

@SuppressWarnings("serial")
public class UITest extends LPanel {

	private LTabbedPane tabbedPane = null;
	private JMenuBar menuBar = null;
	
	private int connectionPort = Connection.PORT_DEFAULT;
	
	public UITest() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		
		addComponents();
		
		Dimension size = new Dimension(800, 600);
		setSize(size);
		setPreferredSize(size);
		
		try {
			final Connection conexionCliente = new Connection(connectionPort);
			conexionCliente.addConnectionListener(new ConnectionListener() {
				
				public void connectionDone(ConnectionEvent evt) {
					System.out.println("Conexión establecida con el servidor en " + conexionCliente.getName() + ":" + conexionCliente.getPort());
				}
				
				public void connectionEnd(ConnectionEvent evt) {
					System.out.println("Conexión finalizada con el servidor en " + conexionCliente.getName() + ":" + conexionCliente.getPort());
				}
				
				public void comunicationReceived(ConnectionEvent evt) {
					System.out.println("Comunicación recibida desde el servidor en " + evt.getConnection().getName() + ":");
					System.out.println(" - " + evt.getComunicationReceived());
					restartUI();
				}
				public void connectionFailed(ConnectionEvent evt) {
					System.out.println(evt.getComunicationReceived());
				}
			});
			conexionCliente.initConnection(null);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	private void addComponents() {
		
		add(getTabbedPane(), BorderLayout.CENTER);
		
		addTab(new TestButtons());
		addTab(new TestTasks());
		addTab(new TestTables());
		addTab(new TestColorize());
		addTab(TestExpresions.getTestComponent());
		
		AppGUI.getCurrentAppGUI().getFrame().setJMenuBar(getMenuBar());
				
		validate();
		repaint();
	}
	private void destroy() {		
		tabbedPane = null;
		menuBar = null;
	}
	
	private void restartUI() {
		
		try {
			int selectedTabIndex = getTabbedPane().getSelectedIndex();
			removeAll();
			destroy();
			UIManager.setLookAndFeel(new LinajeLookAndFeel(UIConfig.CONFIG_FILE_TEST));
			addComponents();
			if (getParent() instanceof LDialogContent) {
				LDialogContent lDialogContent = (LDialogContent) getParent();
				lDialogContent.setBackground(GeneralUIProperties.getInstance().getColorPanels());
				ButtonsPanel pb = new ButtonsPanel(ButtonsPanel.ASPECT_ACCEPT);
				pb.setAutoCloseOnAccept(true);
				lDialogContent.setButtonsPanel(pb);
				lDialogContent.addComponents();
			}
			this.setBackground(GeneralUIProperties.getInstance().getColorPanels());
			getTabbedPane().setSelectedIndex(selectedTabIndex);
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Throwable ex) {
			Console.printException(ex);
		}
	}
	
	public LTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new LTabbedPane();
			
		}
		return tabbedPane;
	}
	
	private void addTab(Component tabComponent) {
		LPanel tab = new LPanel(new BorderLayout());
		tab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tab.add(tabComponent, BorderLayout.CENTER);
		String tabTitle = tabComponent.getName();
		getTabbedPane().addTab(tabTitle, tab);
	}
	
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();
			
			JMenu menuComponents = new JMenu("Componentes");
			ButtonGroup bgComponents = new ButtonGroup();
			for (int i = 0; i < getTabbedPane().getTabCount(); i++) {
				final int tabIndex = i;
				boolean selected = i == 0;
				JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(getTabbedPane().getTitleAt(i), selected);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getTabbedPane().setSelectedIndex(tabIndex);
					}
				});
				menuComponents.add(menuItem);
				bgComponents.add(menuItem);
			}
			
			JMenu menuOpciones = new JMenu("Opciones");
			int opciones = 5;
			for (int i = 0; i < opciones; i++) {
				boolean selected = i==1 || i==2;
				JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Opción "+(i+1), selected); 
				menuOpciones.add(menuItem);
			}
			
			JMenu menuOtros = new JMenu("Otros");
			JMenuItem menuItemSwingSet = new JMenuItem("Abrir Swingset");
			JMenuItem menuItemConsola = new JMenuItem("Abrir consola");
			//JMenuItem menuItem1 = new JMenuItem("Otra línea");
			JMenuItem menuItem1 = new JMenuItem("item 1\nOtra línea");
			JMenuItem menuItem2 = new JMenuItem("Item 2");
			JMenuItem menuItem3 = new JMenuItem("item 3");
			
			menuItem2.setMnemonic(KeyEvent.VK_I);
			
			menuItem1.setAccelerator(KeyStroke.getKeyStroke("control Y"));
			menuItem2.setAccelerator(KeyStroke.getKeyStroke("control X"));
			menuItemSwingSet.setAccelerator(KeyStroke.getKeyStroke("control S"));
			menuItem2.setHorizontalTextPosition(SwingConstants.CENTER);
			menuItem2.setVerticalTextPosition(SwingConstants.BOTTOM);
			menuItem1.setHorizontalTextPosition(SwingConstants.CENTER);
			menuItem1.setVerticalTextPosition(SwingConstants.BOTTOM);
			
			JMenu menuSubmenu = new JMenu("Submenú");
			JMenuItem menuItem4 = new JMenuItem("item 4");
			JMenuItem menuItem5 = new JMenuItem("item 5");
			JMenuItem menuItem6 = new JMenuItem("item 6");
			
			menuItem1.setIcon(Icons.ARROW_UP);
			menuItem2.setIcon(Icons.ARROW_DOWN);
			menuItem3.setEnabled(false);
			
			menuOtros.add(menuItemSwingSet);
			menuOtros.add(menuItemConsola);
			menuOtros.add(menuItem1);
			menuOtros.add(menuItem2);
			menuOtros.add(menuItem3);
			menuOtros.add(new JSeparator());
			menuOtros.add(menuSubmenu);
			
			menuSubmenu.add(menuItem4);
			menuSubmenu.add(menuItem5);
			menuSubmenu.add(menuItem6);
			
			menuBar.add(menuComponents);
			menuBar.add(menuOpciones);
			menuBar.add(menuOtros);
			
			menuItemSwingSet.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					try {
						//SwingSet2 no tiene paquete con nombre, por lo que sólo lo podemos llamar por introspección
						Class<?>[] parameterTypes = {Class.forName("SwingSet2Applet")};
						Object[] parameterValues = {null};
						ReflectAccessSupport.newInstance(Class.forName("SwingSet2"), parameterTypes, parameterValues);
					}
					catch (ClassNotFoundException cnfe) {
						String message = "Parece que no se encuentra la clase SwingSet2. Por favor asegurate de que SwingSet2.jar está correctamente añadido al classpath.\nhttps://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html#swingset2";
						MessageDialog.showMessage(message, MessageDialog.ICON_ERROR);
					}
					catch (Exception ex) {
						Console.printException(ex);
					}
				}
			});
			
			menuItemConsola.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ConsoleWindow.executeConsoleWindow();
				}
			});
		}
		return menuBar;
	}
	
	public static void main(String[] args) {
		
		try {
	
			LinajeLookAndFeel.init(UIConfig.CONFIG_FILE_TEST);
			//UIManager.setLookAndFeel(new LinajeLookAndFeel(UIConfig.TEST_CONFIG_FILE));
			Console.setConsoleWindowEnabled(true);
			Console.println("Iniciando UIConfig...");
			UITest uiTest = new UITest();
			
			for (int i = 0; args != null && i < args.length; i++) {
				try {
					String param = args[0];
					String paramPrefix = UIConfig.PREFIX_PARAM_PORT;
					if (param.toLowerCase().startsWith(paramPrefix))
						uiTest.connectionPort = Integer.parseInt(param.substring(paramPrefix.length()));
				}
				catch (Exception ex) {
					Console.printException(ex);
				}
			}
			
			LDialogContent.showComponentInFrame(uiTest);
		}
		catch (Throwable exception) {
			Console.printException(exception);
		}
	}
}
