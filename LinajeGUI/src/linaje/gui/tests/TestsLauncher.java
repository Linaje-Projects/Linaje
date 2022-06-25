package linaje.gui.tests;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;

import linaje.LocalizedStrings;
import linaje.gui.AppGUI;
import linaje.gui.LButton;
import linaje.gui.LList;
import linaje.gui.cells.LabelCell;
import linaje.gui.layouts.LFlowLayout;
import linaje.gui.renderers.LCellRenderer;
import linaje.gui.utils.UtilsGUI;
import linaje.gui.windows.LDialogContent;
import linaje.utils.Processes;
import linaje.utils.ReflectAccessSupport;

@SuppressWarnings("serial")
public class TestsLauncher extends LDialogContent {

	public static class Texts extends LocalizedStrings {

		//Texts in linaje.gui.tests.localization.linaje_gui_tests.properties
		
		public String launch;
		
		public Texts() {
			super(LocalizedStrings.DEFAULT_GUI_TESTS_RESOURCE_BUNDLE);
		}
		
		@Override
		protected void initValues() {
			initValuesFromFieldNames(getClass().getEnclosingClass());
		}
	}
	
	public static final Texts TEXTS = new Texts();
	
	public TestsLauncher() {
		initialize();
	}

	private void initialize() {
		
		setLayout(new LFlowLayout());
		
		final LList<Class<?>> list = new LList<>();
		final LButton btnExec = new LButton(TEXTS.launch);
		
		Package testsPackage = getClass().getPackage();
		List<Class<?>> classes = ReflectAccessSupport.getClassesFromPackage(testsPackage);
		for (Class<?> c : classes) {
			//labelCombo.addItem(c);
			list.addElement(c);
		}
		
		btnExec.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				//Class<?> testClass = labelCombo.getCombo().getSelectedItem();
				Class<?> testClass = list.getSelectedValue();
				Processes.executeJava(testClass, null, null);
			}
		});
		
		/*final LabelCombo<Class<?>> labelCombo = new LabelCombo<>("Tests:", LabelComponent.VERTICAL);
		labelCombo.getCombo().setRenderer(new LCellRenderer<Object>() {*/
		list.setCellRenderer(new LCellRenderer<Object>() {
			
			@Override
			public LabelCell getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (value instanceof Class<?>)
					value = ((Class<?>) value).getSimpleName();
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		
		list.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					btnExec.doClick();
			}
			public void keyPressed(KeyEvent e) {}
		});
				
		list.setSelectedIndex(0);
		list.requestFocusInWindow();
		
		AppGUI.getCurrentAppGUI().getFrame().addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	list.requestFocusInWindow();
		    }
		});
		
		//add(labelCombo);
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(list);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 500));
		add(scrollPane);
		add(btnExec);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		//Truco para obtener la clase actual de forma estática y copiar y pegar éste main en otras clases
		Class currentClass = new Object() { }.getClass().getEnclosingClass();
		UtilsGUI.quickMain(currentClass);
	}
}
