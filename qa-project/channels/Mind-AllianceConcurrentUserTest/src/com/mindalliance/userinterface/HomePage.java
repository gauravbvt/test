package com.mindalliance.userinterface;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.ReportFunctions;

//VS4E -- DO NOT REMOVE THIS LINE!
public class HomePage extends JFrame implements ActionListener, ItemListener{
	private static final long serialVersionUID = 1L;
	private JList jListView;
	private JScrollPane jScrollPane0;
	private String arrayOfTestCaseId[] = new String[150];
	private static int noOfSelectedTestCases;
	private JButton jButtonAdd;
	private JTextField jTextField0;
	private JLabel jLabel0;
	private JList jListExecute;
	private JScrollPane jScrollPane3;
	private JButton jButtonExecute;
	private JProgressBar jProgressBarStatus;
	private JLabel jLabelStatus;
	private static int cnt;
	private JLabel jLabelTestCaseId;
	private JButton jButtonLogLink;
	private JButton jButtonReportLink;
	private JCheckBox jCheckBoxView;
	private JLabel jLabelNumberOfTestCasesExecuted;
	private JLabel jLabelStartDateTime;
	private JLabel jLabelEndDateTime;
	private JLabel jLabelNumberOfTestCasesPassed;
	private JLabel jLabelNumberOfTestCasesFailed;
	private JPanel jPanelReport;
	private JPanel jPanelLogo;
	BufferedImage image;
	private JButton jButtonExit;
	private JButton jButtonNewTest;
	private JComboBox jComboBoxBrowser;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public HomePage() {
		initComponents();
	}

	private void initComponents() {
		setTitle("Mind Alliance Automation Framework");
		setLayout(new GroupLayout());
		add(getJTextField0(), new Constraints(new Leading(256, -53, 10, 10), new Leading(63, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Leading(41, 298, 10, 10), new Leading(34, 317, 12, 12)));
		add(getJLabel0(), new Constraints(new Leading(41, 12, 12), new Leading(8, 10, 10)));
		add(getJCheckBox0(), new Constraints(new Leading(166, 10, 10), new Leading(4, 8, 8)));
		add(getJPanel0(), new Constraints(new Leading(43, 892, 10, 10), new Leading(363, 278, 10, 10)));
		add(getJButton0(), new Constraints(new Leading(395, 10, 10), new Leading(177, 10, 10)));
		add(getJScrollPane3(), new Constraints(new Leading(497, 298, 10, 10), new Leading(34, 317, 10, 10)));
		setSize(1356, 698);
	}

	private JComboBox getJComboBox0() {
		if (jComboBoxBrowser == null) {
			jComboBoxBrowser = new JComboBox();
			jComboBoxBrowser.setModel(new DefaultComboBoxModel(new Object[] { "Mozilla Firefox", "Internet Explorer" }));
			jComboBoxBrowser.setDoubleBuffered(false);
			jComboBoxBrowser.setBorder(null);
			
		}
		return jComboBoxBrowser;
	}

	private JButton getJButton5() {
		if (jButtonNewTest == null) {
			jButtonNewTest = new JButton();
			jButtonNewTest.setText("New Test");
			jButtonNewTest.setActionCommand("newtest");
			jButtonNewTest.addActionListener(this);
		}
		return jButtonNewTest;
	}

	private JButton getJButton4() {
		if (jButtonExit == null) {
			jButtonExit = new JButton();
			jButtonExit.setText("Exit");
			jButtonExit.setActionCommand("exit");
			jButtonExit.addActionListener(this);
		}
		return jButtonExit;
	}

	HomePage(BufferedImage image) {
	        this.image = image;
	    }

	
	private JPanel getJPanel1() {
		if (jPanelLogo == null) {
			jPanelLogo = new JPanel();
			try { 
		          image = ImageIO.read(new File(GlobalVariables.fCurrentDir + "//Images//Mind-Alliance_Logo.png"));
		       } catch (IOException ex) {
		            // handle exception...
		       }
		       jPanelLogo.setLayout(new GroupLayout());
		}
		return jPanelLogo;
	}

	private JPanel getJPanel0() {
		if (jPanelReport == null) {
			jPanelReport = new JPanel();
			jPanelReport.setLayout(new GroupLayout());
			jPanelReport.add(getJPanel1(), new Constraints(new Trailing(12, 100, 228, 228), new Leading(8, 100, 10, 10)));
			jPanelReport.add(getJComboBox0(), new Constraints(new Leading(457, 122, 10, 10), new Leading(10, 12, 12)));
			jPanelReport.add(getJButton1(), new Constraints(new Leading(591, 24, 124), new Leading(10, 12, 12)));
			jPanelReport.add(getJButton5(), new Constraints(new Leading(677, 24, 124), new Leading(8, 12, 12)));
			jPanelReport.add(getJProgressBar0(), new Constraints(new Leading(457, 298, 24, 124), new Leading(48, 12, 12)));
			jPanelReport.add(getJLabel3(), new Constraints(new Leading(457, 30, 130), new Leading(78, 10, 10)));
			jPanelReport.add(getJLabel4(), new Constraints(new Leading(457, 30, 130), new Leading(105, 12, 12)));
			jPanelReport.add(getJButton2(), new Constraints(new Leading(457, 12, 12), new Leading(133, 12, 12)));
			jPanelReport.add(getJButton3(), new Constraints(new Leading(457, 12, 12), new Leading(165, 12, 12)));
			jPanelReport.add(getJButton4(), new Constraints(new Leading(457, 12, 12), new Leading(203, 12, 12)));
			jPanelReport.add(getJLabel5(), new Constraints(new Leading(22, 10, 10), new Leading(33, 10, 10)));
			jPanelReport.add(getJLabel6(), new Constraints(new Leading(22, 30, 130), new Leading(74, 12, 12)));
			jPanelReport.add(getJLabel7(), new Constraints(new Leading(24, 10, 10), new Leading(112, 12, 12)));
			jPanelReport.add(getJLabel8(), new Constraints(new Leading(24, 12, 12), new Leading(156, 10, 10)));
			jPanelReport.add(getJLabel9(), new Constraints(new Leading(24, 12, 12), new Leading(199, 10, 10)));
		}
		return jPanelReport;
	}

	private JLabel getJLabel9() {
		if (jLabelNumberOfTestCasesFailed == null) {
			jLabelNumberOfTestCasesFailed = new JLabel();
			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: ");
		}
		return jLabelNumberOfTestCasesFailed;
	}

	private JLabel getJLabel8() {
		if (jLabelNumberOfTestCasesPassed == null) {
			jLabelNumberOfTestCasesPassed = new JLabel();
			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: ");
		}
		return jLabelNumberOfTestCasesPassed;
	}

	private JLabel getJLabel6() {
		if (jLabelEndDateTime == null) {
			jLabelEndDateTime = new JLabel();
			jLabelEndDateTime.setText("End DateTime: ");
		}
		return jLabelEndDateTime;
	}

	private JLabel getJLabel5() {
		if (jLabelStartDateTime == null) {
			jLabelStartDateTime = new JLabel();
			jLabelStartDateTime.setText("Start DateTime: ");
		}
		return jLabelStartDateTime;
	}

	private JLabel getJLabel7() {
		if (jLabelNumberOfTestCasesExecuted == null) {
			jLabelNumberOfTestCasesExecuted = new JLabel();
			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: ");
		}
		return jLabelNumberOfTestCasesExecuted;
	}

	private JCheckBox getJCheckBox0() {
		if (jCheckBoxView == null) {
			jCheckBoxView = new JCheckBox();
			jCheckBoxView.setText("Select All");
			jCheckBoxView.addItemListener(this);
		}
		return jCheckBoxView;
	}

	private JButton getJButton3() {
		if (jButtonReportLink == null) {
			jButtonReportLink = new JButton();
			jButtonReportLink.setText("Reports");
			jButtonReportLink.setEnabled(false);
			jButtonReportLink.setActionCommand("reports");
			jButtonReportLink.addActionListener(this);
		}
		return jButtonReportLink;
	}

	private JButton getJButton2() {
		if (jButtonLogLink == null) {
			jButtonLogLink = new JButton();
			jButtonLogLink.setText("Logs");
			jButtonLogLink.setEnabled(false);
			jButtonLogLink.setActionCommand("logs");
			jButtonLogLink.addActionListener(this);
		}
		return jButtonLogLink;
	}

	private JLabel getJLabel4() {
		if (jLabelTestCaseId == null) {
			jLabelTestCaseId = new JLabel();
			jLabelTestCaseId.setText("TestCaseId: ");
		}
		return jLabelTestCaseId;
	}

	private JLabel getJLabel3() {
		if (jLabelStatus == null) {
			jLabelStatus = new JLabel();
			jLabelStatus.setText("Status:");
		}
		return jLabelStatus;
	}

	private JProgressBar getJProgressBar0() {
		if (jProgressBarStatus == null) {
			jProgressBarStatus = new JProgressBar();
		}
		return jProgressBarStatus;
	}

	private JButton getJButton1() {
		if (jButtonExecute == null) {
			jButtonExecute = new JButton();
			jButtonExecute.setText("Execute");
			jButtonExecute.setActionCommand("execute");
			jButtonExecute.addActionListener(this);
		}
		return jButtonExecute;
	}

	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJList3());
		}
		return jScrollPane3;
	}

	private JList getJList3() {
		if (jListExecute == null) {
			jListExecute = new JList();
			DefaultListModel listModel = new DefaultListModel();
			jListExecute.setModel(listModel);
		}
		return jListExecute;
	}

	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Concurrent Users");
		}
		return jLabel0;
	}

	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		}
		return jTextField0;
	}

	private JButton getJButton0() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText(">");
			jButtonAdd.setActionCommand("add");
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJList0());
		}
		return jScrollPane0;
	}

	private JList getJList0() {
		try {
		if (jListView == null) {
			jListView = new JList();
			DefaultListModel listModel = new DefaultListModel();
			arrayOfTestCaseId = ReportFunctions.readTestCaseId(1);
			for (int i=0;i<127;i++)
				if(arrayOfTestCaseId[i] != null) {
					listModel.addElement(arrayOfTestCaseId[i]);
			}
			jListView.setModel(listModel);
			jListView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return jListView;
		}
		catch (Exception e) {
			
		}
		return jListView;
	}
	
	public void updateProgressBar(int percent) {
		jProgressBarStatus.setValue(percent);
		jProgressBarStatus.setString("Completed: " + Integer.toString(percent) + "/" + noOfSelectedTestCases);
		jProgressBarStatus.setStringPainted(true);
		Rectangle progressRect = jProgressBarStatus.getBounds();
		progressRect.x = 0;
		progressRect.y = 0;
		jProgressBarStatus.paintImmediately(progressRect);
	}
	
	public void executeTestCases(Vector<Object> arrayOfTestCaseId) {
		int totalExecute;
		try {
			Class<?> cls;
			cnt = 0;
			// Call GenericFunctionLibrary.initializeTestData()
			GenericFunctionLibrary.initializeTestData();
			// Get Browser Name
			GlobalVariables.sBrowser = jComboBoxBrowser.getSelectedItem().toString();
			// Set progressBar Values
			jProgressBarStatus.setMinimum(0);
			jProgressBarStatus.setMaximum(noOfSelectedTestCases);
			// Set Status label
			jLabelStatus.setText("Status: Automation TestPlan script started");
            jLabelStatus.setSize(jLabelStatus.getPreferredSize());
			jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
			// Set startDateTime label
			jLabelStartDateTime.setText("Start DateTime: " + GlobalVariables.sStartDateTime);
			jLabelStartDateTime.setSize(jLabelStartDateTime.getPreferredSize());
			jLabelStartDateTime.paintImmediately(jLabelStartDateTime.getVisibleRect());
			// Call GenericFunctionLibrary.loadTestData()
			GenericFunctionLibrary.tearDownTestData();
			// Execution of selected TestCases
			for (Object testCaseId: arrayOfTestCaseId) {
				// Clear TestCaseId label
				jLabelTestCaseId.removeAll();
				jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
				jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
				// Set TestCaseId label
				jLabelTestCaseId.setText("Executing TestCaseId: " + testCaseId.toString());
				jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
				jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
				// Execute current TestCaseId
				cls = Class.forName("com.mindalliance.testscripts." + testCaseId);
				cls.newInstance();
				// Update progressBar
				cnt = cnt + 1;
				updateProgressBar(cnt);
			}
			// Call GenericFunctionLibrary.tearDownTestData()
			GenericFunctionLibrary.tearDownTestData();
			// Call ReportFunctions.generateAutomationReport()
			ReportFunctions.generateAutomationReport();
			// Enable Logs and Reports button
			jButtonLogLink.setEnabled(true);
			jButtonReportLink.setEnabled(true);
			// Clear TestCaseId label
			jLabelTestCaseId.setText("");
			jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
			jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
			// Set TestCaseId label
			jLabelTestCaseId.setText("TestPlan: " + GlobalVariables.sReportDirectoryName);
			jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
			jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
			// Set Status label
			jLabelStatus.setText("Status: Automation TestPlan script completed");
			jLabelStatus.setSize(jLabelStatus.getPreferredSize());
			jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
			// Set endDateTime label
			jLabelEndDateTime.setText("End DateTime: " + GlobalVariables.sEndDateTime);
			jLabelEndDateTime.setSize(jLabelEndDateTime.getPreferredSize());
			jLabelEndDateTime.paintImmediately(jLabelEndDateTime.getVisibleRect());
			// Get totalExecuted TestCaseId
			totalExecute = ReportFunctions.totalNoOfTestCasesPassed + ReportFunctions.totalNoOfTestCasesFailed;
			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: " + Integer.toString(totalExecute));
			jLabelNumberOfTestCasesExecuted.setSize(jLabelNumberOfTestCasesExecuted.getPreferredSize());
			jLabelNumberOfTestCasesExecuted.paintImmediately(jLabelNumberOfTestCasesExecuted.getVisibleRect());
			// Get totalTestCasesPassed
			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: " + Integer.toString(ReportFunctions.totalNoOfTestCasesPassed));
			jLabelNumberOfTestCasesPassed.setSize(jLabelNumberOfTestCasesPassed.getPreferredSize());
			jLabelNumberOfTestCasesPassed.paintImmediately(jLabelNumberOfTestCasesPassed.getVisibleRect());
			// Get totalTestCasesFailed
			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: " + Integer.toString(ReportFunctions.totalNoOfTestCasesFailed));
			jLabelNumberOfTestCasesFailed.setSize(jLabelNumberOfTestCasesFailed.getPreferredSize());
			jLabelNumberOfTestCasesFailed.paintImmediately(jLabelNumberOfTestCasesFailed.getVisibleRect());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}

	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			if ("add".equals(e.getActionCommand())) {// when clicked on '>' button
				Object[] arrayOfListObject;
				DefaultListModel listModel = new DefaultListModel();
				arrayOfListObject = jListView.getSelectedValues();
				for (Object listObject : arrayOfListObject) 
					listModel.addElement(listObject);
				jListExecute.setModel(listModel);
				}

			else if ("execute".equals(e.getActionCommand())) { // when clicked on 'Execute' button
				if (jListExecute.getModel().getSize() > 0) {
					jButtonExecute.setEnabled(false);
					Vector<Object> vc = new Vector<Object>();
				    //;Object o[] = new Object[200];
					noOfSelectedTestCases = 0;
					for (int i = 0; i < jListExecute.getModel().getSize(); i++) {
						noOfSelectedTestCases ++;
						vc.add(jListExecute.getModel().getElementAt(i));
					}
					executeTestCases(vc);
					jButtonExecute.setEnabled(true);
				}
				else
					JOptionPane.showMessageDialog(rootPane, "Please select the testcases.");
			}
			else if ("newtest".equals(e.getActionCommand())) { // when clicked on 'New Test' button
				clearTestPlanResult();
			}
			else if ("logs".equals(e.getActionCommand())) { // when clicked on 'Logs' button
				File file = new File(GlobalVariables.sLogDirectoryPath);
				Desktop desktop = Desktop.getDesktop();	
				desktop.open(file);
			}
			else if ("reports".equals(e.getActionCommand())) { // when clicked on 'Reports' button
				File file = new File(GlobalVariables.sReportDstDirectoryPath);
				Desktop desktop = Desktop.getDesktop();	
				desktop.open(file);
			}
			else if ("exit".equals(e.getActionCommand())) { // when clicked on 'Exit' button
				System.exit(0);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void clearTestPlanResult() {
		// TODO Auto-generated method stub
		DefaultListModel listModel = new DefaultListModel();
		listModel.removeAllElements();
		jListExecute.setModel(listModel);
		// Clear progressBar Values
		jProgressBarStatus.setMinimum(0);
		jProgressBarStatus.setMaximum(0);
		// Clear Status label
		jLabelStatus.setText("Status: ");
        jLabelStatus.setSize(jLabelStatus.getPreferredSize());
		jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
		// Clear TestCaseId label
		jLabelTestCaseId.setText("");
		jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
		jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
		// Clear TestCaseId label
		jLabelTestCaseId.setText("TestCaseId: ");
		jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
		jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
		jButtonLogLink.setEnabled(false);
		jButtonReportLink.setEnabled(false);
		// Clear startDateTime label
		jLabelStartDateTime.setText("Start DateTime: ");
		jLabelStartDateTime.setSize(jLabelStartDateTime.getPreferredSize());
		jLabelStartDateTime.paintImmediately(jLabelStartDateTime.getVisibleRect());
		// Clear endDateTime label
		jLabelEndDateTime.setText("End DateTime: ");
		jLabelEndDateTime.setSize(jLabelEndDateTime.getPreferredSize());
		jLabelEndDateTime.paintImmediately(jLabelEndDateTime.getVisibleRect());
		// Clear totalExecuted TestCaseId
		jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: ");
		jLabelNumberOfTestCasesExecuted.setSize(jLabelNumberOfTestCasesExecuted.getPreferredSize());
		jLabelNumberOfTestCasesExecuted.paintImmediately(jLabelNumberOfTestCasesExecuted.getVisibleRect());
		// Clear totalTestCasesPassed
		jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: ");
		jLabelNumberOfTestCasesPassed.setSize(jLabelNumberOfTestCasesPassed.getPreferredSize());
		jLabelNumberOfTestCasesPassed.paintImmediately(jLabelNumberOfTestCasesPassed.getVisibleRect());
		// Clear totalTestCasesFailed
		jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: ");
		jLabelNumberOfTestCasesFailed.setSize(jLabelNumberOfTestCasesFailed.getPreferredSize());
		jLabelNumberOfTestCasesFailed.paintImmediately(jLabelNumberOfTestCasesFailed.getVisibleRect());
		// Clear progressBar String
		jProgressBarStatus.setString("");
		jProgressBarStatus.setStringPainted(true);
		Rectangle progressRect = jProgressBarStatus.getBounds();
		progressRect.x = 0;
		progressRect.y = 0;
		jProgressBarStatus.paintImmediately(progressRect);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == jCheckBoxView) {
			if (e.getStateChange() == 1)
				jListView.setSelectionInterval(0, jListView.getModel().getSize() - 1);
			else
				jListView.clearSelection();
		}
	}

	/**
	 * Main entry of the class.
	 * Note: This class is only created so that you can easily preview the result at runtime.
	 * It is not expected to be managed by the designer.
	 * You can modify it as you like.
	 */
	public static void main(String[] args) {
		installLnF();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HomePage frame = new HomePage();
				frame.setDefaultCloseOperation(HomePage.EXIT_ON_CLOSE);
				frame.setTitle("Mind-Alliance Automation Framework");
				//frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			}
		});
	}
}
