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
import com.mindalliance.userinterface.EmailNotification;

/**
 * The Home is an Swing GUI class which uses the swing components for creating the UI for Channels Test cases execution
 * @author Afour
 *
 */
public class Home extends JFrame implements ActionListener, ItemListener{
	private static final long serialVersionUID = 1L;
	private JList jListView;
	private JScrollPane jScrollPane0;
	private String arrayOfTestCaseId[] = new String[200];
	private static int noOfSelectedTestCases;
	private JButton jButtonAdd;
	private JTextField jTextField0;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JList jListPlan;
	private JScrollPane jScrollPane1;
	private JLabel jLabel2;
	private JList jListCommand;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JButton jButtonExecute;
	private JProgressBar jProgressBarStatus;
	private JLabel jLabelStatus;
	private static int cnt;
	private JLabel jLabelTestCaseId;
	private JButton jButtonLogLink;
	private JButton jButtonReportLink;
	private JCheckBox jCheckBoxView;
	private JCheckBox jCheckBoxPlan;
	private JCheckBox jCheckBoxCommand;
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
	private JButton jButtonSendMessage;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	
	/**
	 * Constructor of Home class 
	 * @author Afour
	 */
	public Home() {
		try {
			// Initialize Components
			initComponents();
		}
		catch (Exception e) {
			System.out.println("In InitComponents Function.");
			e.printStackTrace();
		}
	}

	/**
	 * Parameterized Constructor of Image
	 * @param image
	 * @author Afour
	 */
	Home(BufferedImage image) {
		this.image = image;
	}

	/**
	 * The initComponent() method provides and places all the swing component on the panel as per their positions
	 * @author Afour 
	 */
	private void initComponents() {
		setTitle("Mind Alliance Automation Framework");
		setLayout(new GroupLayout());		
		// Position for Textfield
		add(getJTextField0(), new Constraints(new Leading(256, -53, 10, 10), new Leading(63, 12, 12)));		
		// Position for the Scroll panes
		add(getJScrollPane0(), new Constraints(new Leading(41, 298, 10, 10), new Leading(34, 317, 12, 12)));
		add(getJScrollPane1(), new Constraints(new Leading(338, 298, 10, 10), new Leading(34, 317, 10, 10)));
		add(getJScrollPane2(), new Constraints(new Leading(634, 298, 10, 10), new Leading(34, 317, 10, 10)));
		add(getJScrollPane3(), new Constraints(new Leading(1027, 298, 10, 10), new Leading(34, 317, 10, 10)));		
		// Position the Buttons
		add(getJButton0(), new Constraints(new Leading(959, 10, 10), new Leading(174, 12, 12)));
		add(getJButton1(), new Constraints(new Leading(1153, 12, 12), new Leading(369, 12, 12)));
		add(getJButton2(), new Constraints(new Leading(1027, 12, 12), new Leading(525, 10, 10)));
		add(getJButton3(), new Constraints(new Leading(1027, 12, 12), new Leading(575, 10, 10)));
		add(getJButton4(), new Constraints(new Leading(1026, 12, 12), new Leading(622, 10, 10)));		
		add(getJButton5(), new Constraints(new Leading(1239, 10, 10), new Leading(369, 12, 12)));
		add(getJButton6(), new Constraints(new Leading(1137, 112, 12, 12), new Leading(575, 12, 12)));		
		// Position the Labels
		add(getJLabel0(), new Constraints(new Leading(41, 12, 12), new Leading(8, 10, 10)));
		add(getJLabel1(), new Constraints(new Leading(341, 10, 10), new Leading(8, 12, 12)));
		add(getJLabel2(), new Constraints(new Leading(637, 12, 12), new Leading(10, 12, 12, 12)));
		add(getJLabel3(), new Constraints(new Leading(1024, 12, 12), new Leading(453, 10, 10)));
		add(getJLabel4(), new Constraints(new Leading(1024, 12, 12), new Leading(489, 12, 12)));		
		// Position the CheckBoxes
		add(getJCheckBox0(), new Constraints(new Leading(166, 10, 10), new Leading(4, 8, 8)));
		add(getJCheckBox1(), new Constraints(new Leading(374, 8, 8), new Leading(4, 8, 8)));
		add(getJCheckBox2(), new Constraints(new Leading(798, 10, 10), new Leading(4, 8, 8)));		
		// Position the JPanel
		add(getJPanel0(), new Constraints(new Leading(43, 892, 10, 10), new Leading(363, 278, 10, 10)));		
		// Position the ProgressBar
		add(getJProgressBar0(), new Constraints(new Leading(1024, 298, 10, 10), new Leading(413, 12, 12)));		
		// Position the ComboBox
		add(getJComboBox0(), new Constraints(new Leading(1023, 122, 10, 10), new Leading(369, 12, 12)));		
		setSize(1356, 698);
	}

	/**
	 * This method creates the JTextField
	 * @return jTextField0
	 * @author Afour
	 */
	private JTextField getJTextField0() {
		if (jTextField0 == null) {
			jTextField0 = new JTextField();
		}
		return jTextField0;
	}
	
	/**
	 * This method creates the JScrollPane
	 * @return jScrollPane0
	 * @author Afour
	 */
	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.setViewportView(getJList0());
		}
		return jScrollPane0;
	}
	
	/**
	 * This method creates the JScrollPane
	 * @return jScrollPane1
	 * @author Afour
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJList1());
		}
		return jScrollPane1;
	}
	/**
	 * This method creates the JScrollPane
	 * @return jScrollPane2
	 * @author Afour
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJList2());
		}
		return jScrollPane2;
	}
	/**
	 * This method creates the JScrollPane
	 * @return jScrollPane3
	 * @author Afour
	 */
	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJList3());
		}
		return jScrollPane3;
	}

	/**
	 * This method adds button for ">"
	 * @return jButtonAdd
	 * @author Afour
	 */
	private JButton getJButton0() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText(">");
			jButtonAdd.setActionCommand("add");
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}
	/**
	 * This method adds execute button
	 * @return jButtonExecute
	 * @author Afour
	 */
	private JButton getJButton1() {
		if (jButtonExecute == null) {
			jButtonExecute = new JButton();
			jButtonExecute.setText("Execute");
			jButtonExecute.setActionCommand("execute");
			jButtonExecute.addActionListener(this);
		}
		return jButtonExecute;
	}
	/**
	 * This method adds Logs button
	 * @return jButtonLogLink
	 * @author Afour 
	 */
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
	/**
	 * This method adds Reports button
	 * @return jButtonReportLink
	 * @author Afour 
	 */ 
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
	/**
	 * This method adds Exit button
	 * @return jButtonExit
	 * @author Afour 
	 */
	private JButton getJButton4() {
		if (jButtonExit == null) {
			jButtonExit = new JButton();
			jButtonExit.setText("Exit");
			jButtonExit.setActionCommand("exit");
			jButtonExit.addActionListener(this);
		}
		return jButtonExit;
	}
	/**
	 * This method adds New Test button
	 * @return jButtonNewTest
	 * @author Afour 
	 */
	private JButton getJButton5() {
		if (jButtonNewTest == null) {
			jButtonNewTest = new JButton();
			jButtonNewTest.setText("New Test");
			jButtonNewTest.setActionCommand("newtest");
			jButtonNewTest.addActionListener(this);
		}
		return jButtonNewTest;
	}
	/**
	* This method adds Send Email button
	* @return jButtonSendMessage
	* @author Afour 
	*/
	private JButton getJButton6() {
		if (jButtonSendMessage == null) {
			jButtonSendMessage = new JButton();
			jButtonSendMessage.setText("Send Email");
			jButtonSendMessage.setEnabled(false);
			jButtonSendMessage.setActionCommand("message");
			jButtonSendMessage.addActionListener(this);
		}
		return jButtonSendMessage;
	}

	/**
	 * This methods adds View label
	 * @return jLabel0
	 * @author Afour
	 */
	private JLabel getJLabel0() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("Tree Navigation View");
		}
		return jLabel0;
	}
	/**
	 * This methods adds Plan label
	 * @return jLabel1
	 * @author Afour
	 */
	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Plan");
		}
		return jLabel1;
	}
	/**
	 * This methods adds Command label
	 * @return jLabel2
	 * @author Afour
	 */
	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Undo and Redo Commands");
		}
		return jLabel2;
	}
	/**
	 * This methods adds Status label
	 * @return jLabelStatus
	 * @author Afour
	 */
	private JLabel getJLabel3() {
		if (jLabelStatus == null) {
			jLabelStatus = new JLabel();
			jLabelStatus.setText("Status:");
		}
		return jLabelStatus;
	}
	/**
	 * This methods adds TestCaseId label
	 * @return jLabelTestCaseId
	 * @author Afour
	 */
	private JLabel getJLabel4() {
		if (jLabelTestCaseId == null) {
			jLabelTestCaseId = new JLabel();
			jLabelTestCaseId.setText("TestCaseId: ");
		}
		return jLabelTestCaseId;
	}
	/**
	 * This methods adds Start Date time label
	 * @return jLabelStartDateTime
	 * @author Afour
	 */
	private JLabel getJLabel5() {
		if (jLabelStartDateTime == null) {
			jLabelStartDateTime = new JLabel();
			jLabelStartDateTime.setText("Start DateTime: ");
		}
		return jLabelStartDateTime;
	}
	/**
	 * This methods adds End date time label
	 * @return jLabelEndDateTime
	 * @author Afour
	 */
	private JLabel getJLabel6() {
		if (jLabelEndDateTime == null) {
			jLabelEndDateTime = new JLabel();
			jLabelEndDateTime.setText("End DateTime: ");
		}
		return jLabelEndDateTime;
	}
	/**
	 * This methods adds Number of test cases executed label
	 * @return jLabelNumberOfTestCasesExecuted
	 * @author Afour
	 */
	private JLabel getJLabel7() {
		if (jLabelNumberOfTestCasesExecuted == null) {
			jLabelNumberOfTestCasesExecuted = new JLabel();
			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: ");
		}
		return jLabelNumberOfTestCasesExecuted;
	}
	/**
	 * This method adds Number of test cases passed label
	 * @return jLabelNumberOfTestCasesPassed
	 * @author Afour
	 */
	private JLabel getJLabel8() {
		if (jLabelNumberOfTestCasesPassed == null) {
			jLabelNumberOfTestCasesPassed = new JLabel();
			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: ");
		}
		return jLabelNumberOfTestCasesPassed;
	}
	/**
	 * This method adds the Number of test cases failed label  
	 * @return jLabelNumberOfTestCasesFailed
	 * @author Afour
	 */
	private JLabel getJLabel9() {
		if (jLabelNumberOfTestCasesFailed == null) {
			jLabelNumberOfTestCasesFailed = new JLabel();
			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: ");
		}
		return jLabelNumberOfTestCasesFailed;
	}

	/**
	 * This method creates the Select All checkbox
	 * @return jCheckBoxView
	 * @author Afour
	 */
	private JCheckBox getJCheckBox0() {
		if (jCheckBoxView == null) {
			jCheckBoxView = new JCheckBox();
			jCheckBoxView.setText("Select All");
			jCheckBoxView.addItemListener(this);
		}
		return jCheckBoxView;
	}

	/**
	 * This method creates the Select All checkbox
	 * @return jCheckBoxPlan
	 * @author Afour
	 */
	private JCheckBox getJCheckBox1() {
		if (jCheckBoxPlan == null) {
			jCheckBoxPlan = new JCheckBox();
			jCheckBoxPlan.setText("Select All");
			jCheckBoxPlan.addItemListener(this);
		}
		return jCheckBoxPlan;
	}

	/**
	 * This method creates the Select All checkbox
	 * @return jCheckCommand
	 * @author Afour
	 */
	private JCheckBox getJCheckBox2() {
		if (jCheckBoxCommand == null) {
			jCheckBoxCommand = new JCheckBox();
			jCheckBoxCommand.setText("Select All");
			jCheckBoxCommand.addItemListener(this);
		}
		return jCheckBoxCommand;
	}

	/**
	 * This method adds Panel on the swing component as desired position
	 * @author Afour
	 */
	private JPanel getJPanel0() {
		
		if (jPanelReport == null) {
			jPanelReport = new JPanel();
			jPanelReport.setLayout(new GroupLayout());
			jPanelReport.add(getJPanel1(), new Constraints(new Trailing(12, 100, 228, 228), new Leading(8, 100, 10, 10)));
			jPanelReport.add(getJLabel9(), new Constraints(new Leading(51, 12, 12), new Leading(226, 10, 10)));
			jPanelReport.add(getJLabel8(), new Constraints(new Leading(41, 12, 12), new Leading(183, 10, 10)));
			jPanelReport.add(getJLabel7(), new Constraints(new Leading(31, 12, 12), new Leading(139, 10, 10)));
			jPanelReport.add(getJLabel6(), new Constraints(new Leading(132, 30, 130), new Leading(95, 10, 10)));
			jPanelReport.add(getJLabel5(), new Constraints(new Leading(125, 30, 130), new Leading(53, 10, 10)));
		}
		return jPanelReport;
	}
	/**
	 * This method adds the Image/Logo to the JPanel
	 * @author Afour
	 */
	private JPanel getJPanel1() {
		if (jPanelLogo == null) {
			jPanelLogo = new JPanel();
			try	{ 
				image = ImageIO.read(new File(GlobalVariables.fCurrentDir + "//Images//Mind-Alliance_Logo.png"));
			} 
			catch (IOException ex) {
				System.out.println("Error Occured in getJPanel1 Function.");
				ex.printStackTrace();
			}
		    jPanelLogo.setLayout(new GroupLayout());
		}
		return jPanelLogo;
	}
	
	/**
	 * This method adds the status progress-bar
	 * @return jProgressBarStarus
	 * @author afour
	 */
	private JProgressBar getJProgressBar0() {
		if (jProgressBarStatus == null) {
			jProgressBarStatus = new JProgressBar();
		}
		return jProgressBarStatus;
	}
	/**
	 * This method is used to update the status Progress-bar
	 * @author Afour
	 */
	public void updateProgressBar(int percent) {
		jProgressBarStatus.setValue(percent);
		jProgressBarStatus.setString("Completed: " + Integer.toString(percent) + "/" + noOfSelectedTestCases);
		jProgressBarStatus.setStringPainted(true);
		Rectangle progressRect = jProgressBarStatus.getBounds();
		progressRect.x = 0;
		progressRect.y = 0;
		jProgressBarStatus.paintImmediately(progressRect);
	}
	
	/**
	 * This method is used to add the Browsers to the JCombobox
	 * @author Afour
	 * @return jComboBoxBrowser
	 */
	private JComboBox getJComboBox0() {
		if (jComboBoxBrowser == null) {
			jComboBoxBrowser = new JComboBox();
			jComboBoxBrowser.setModel(new DefaultComboBoxModel(new Object[] { "Mozilla Firefox", "Internet Explorer" }));
			jComboBoxBrowser.setDoubleBuffered(false);
			jComboBoxBrowser.setBorder(null);
			
		}
		return jComboBoxBrowser;
	}
	
	/**
	 * This method is used to add test cases to be executed to the JList
	 * @author Afour
	 * @return jListExecute 
	 */
	private JList getJList3() {
		if (GlobalVariables.jListExecute == null) {
			GlobalVariables.jListExecute = new JList();
			DefaultListModel listModel = new DefaultListModel();
			GlobalVariables.jListExecute.setModel(listModel);
		}
		return GlobalVariables.jListExecute;
	}
	/**
	 * This method is used to add the test cases to the JList of View
	 * @return jListView
	 * @author Afour
	 */
	private JList getJList0() {
		try {
			jListView = new JList();
			DefaultListModel listModel = new DefaultListModel();
			arrayOfTestCaseId = ReportFunctions.readTestCaseId(1);
			for (int i=0;i<GlobalVariables.iIndex;i++)
				listModel.addElement(arrayOfTestCaseId[i]);
			jListView.setModel(listModel);
			jListView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			return jListView;
		}
		catch (Exception e) {
			System.out.println("Error Occured in getJList0 Function.");
			e.printStackTrace();
		}
		return jListView;
	}
	/**
	 * This method is used to add the test cases to the JList of Plan
	 * @return jListPlan
	 * @author Afour
	 */
	private JList getJList1() {
		try {
			jListPlan = new JList();
			DefaultListModel listModel = new DefaultListModel();
			arrayOfTestCaseId = ReportFunctions.readTestCaseId(2);
			for (int i = 0; i <GlobalVariables.iIndex ; i++ ) 
				listModel.addElement(arrayOfTestCaseId[i]);
			jListPlan.setModel(listModel);
			jListPlan.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			return jListPlan;
		}
		catch (Exception e) {
			System.out.println("Error Occured in getJList1 Function.");
			e.printStackTrace();
		}
		return jListPlan;
	}
	/**
	 * This method is used to add the test cases to the JList of Command
	 * @return jListCommand
	 * @author Afour
	 */
	private JList getJList2() {
		try {
			jListCommand = new JList();
			DefaultListModel listModel = new DefaultListModel();
			arrayOfTestCaseId = null;
			arrayOfTestCaseId = ReportFunctions.readTestCaseId(3);
			for (int i = 0; i <GlobalVariables.iIndex; i++)
				listModel.addElement(arrayOfTestCaseId[i]);
			jListCommand.setModel(listModel);
			jListCommand.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			return jListCommand;
		}
		catch (Exception e) {
			System.out.println("Error Occured in getJList2 Function.");
			e.printStackTrace();
		}
		return jListCommand;
	}

	/**
	 * This method is used to execute the test cases. This method initialize testdata and loads object repository and also calls teardown method
	 * It calls the report generation methods and writes an start and end date time on the swing frame
	 * @author Afour
	 */
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
			GenericFunctionLibrary.loadObjectRepository();
			// Execution of selected TestCases
			for (Object testCaseId: arrayOfTestCaseId) {
				try {
					//Clear TestCaseId label
					jLabelTestCaseId.removeAll();
					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
					//Set TestCaseId label
					jLabelTestCaseId.setText("Executing TestCaseId: " + testCaseId.toString());
					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
					//Execute current TestCaseId
					cls = Class.forName("com.mindalliance.testscripts." + testCaseId);
					cls.newInstance();
					//Update progressBar
					cnt = cnt + 1;
					updateProgressBar(cnt);
				}
				catch(Exception e) {
					//Update progressBar
					cnt = cnt + 1;
					updateProgressBar(cnt);
					System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				}
			}
			// Call GenericFunctionLibrary.tearDownTestData()
			GenericFunctionLibrary.tearDownTestData();
			// Call ReportFunctions.generateAutomationReport()
 			ReportFunctions.generateAutomationReport();
			// Enable Logs and Reports button
			jButtonLogLink.setEnabled(true);
			jButtonReportLink.setEnabled(true);
			jButtonSendMessage.setEnabled(true);
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
			// Get total Executed TestCaseId
			totalExecute = ReportFunctions.totalNoOfTestCasesPassed + ReportFunctions.totalNoOfTestCasesFailed;
			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: " + Integer.toString(totalExecute));
			jLabelNumberOfTestCasesExecuted.setSize(jLabelNumberOfTestCasesExecuted.getPreferredSize());
			jLabelNumberOfTestCasesExecuted.paintImmediately(jLabelNumberOfTestCasesExecuted.getVisibleRect());
			// Get total Test Cases Passed
			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: " + Integer.toString(ReportFunctions.totalNoOfTestCasesPassed));
			jLabelNumberOfTestCasesPassed.setSize(jLabelNumberOfTestCasesPassed.getPreferredSize());
			jLabelNumberOfTestCasesPassed.paintImmediately(jLabelNumberOfTestCasesPassed.getVisibleRect());
			// Get total TestCases Failed
			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: " + Integer.toString(ReportFunctions.totalNoOfTestCasesFailed));
			jLabelNumberOfTestCasesFailed.setSize(jLabelNumberOfTestCasesFailed.getPreferredSize());
			jLabelNumberOfTestCasesFailed.paintImmediately(jLabelNumberOfTestCasesFailed.getVisibleRect());
		}
		catch (Exception e) {
			System.out.println("Error Occured in ExecuteTestCases Function.");
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to set the look and feel
	 * @author Afour
	 */
	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			UIManager.setLookAndFeel(lnfClassname);
		} 
		catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL + " on this platform:" + e.getMessage());
		}
	}
	
	/**
	 * This method is Called When Action is Performed
	 * @author Afour
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			if ("add".equals(e.getActionCommand())) {// when clicked on '>' button
				Object[] arrayOfListObject;
				DefaultListModel listModel = new DefaultListModel();
				arrayOfListObject = jListView.getSelectedValues();
				for (Object listObject : arrayOfListObject){ 
					listModel.addElement(listObject);
					GlobalVariables.noOfViewTestCasesExecuted++;
				}
				GlobalVariables.jListExecute.setModel(listModel);
				
				arrayOfListObject = jListPlan.getSelectedValues();
				for (Object listObject : arrayOfListObject){ 
					listModel.addElement(listObject);
					GlobalVariables.noOfPlanTestCasesExecuted++;
				}
				GlobalVariables.jListExecute.setModel(listModel);
				
				arrayOfListObject = jListCommand.getSelectedValues();
				for (Object listObject : arrayOfListObject){ 
					listModel.addElement(listObject);
					GlobalVariables.noOfCommandTestCasesExecuted++;
				}
				GlobalVariables.jListExecute.setModel(listModel);
				
				System.out.println("Total Count : " + (GlobalVariables.noOfViewTestCasesExecuted +
						GlobalVariables.noOfPlanTestCasesExecuted + GlobalVariables.noOfCommandTestCasesExecuted));
			}
			else if ("execute".equals(e.getActionCommand())) { // when clicked on 'Execute' button
				if (GlobalVariables.jListExecute.getModel().getSize() > 0) {
					jButtonExecute.setEnabled(false);
					Vector<Object> vc = new Vector<Object>();
				    //;Object o[] = new Object[200];
					noOfSelectedTestCases = 0;
					for (int i = 0; i < GlobalVariables.jListExecute.getModel().getSize(); i++) {
						noOfSelectedTestCases ++;
						vc.add(GlobalVariables.jListExecute.getModel().getElementAt(i));
					}
					executeTestCases(vc);
					jButtonExecute.setEnabled(true);
				}
				else
					JOptionPane.showMessageDialog(rootPane, "Please select the testcases.");
			}
			// when clicked on 'New Test' button
			else if ("newtest".equals(e.getActionCommand())) { 
				clearTestPlanResult();
			}
			// when clicked on 'Logs' button
			else if ("logs".equals(e.getActionCommand())) { 
				File file = new File(GlobalVariables.sLogDirectoryPath);
				Desktop desktop = Desktop.getDesktop();	
				desktop.open(file);
			}
			 // when clicked on 'Reports' button
			else if ("reports".equals(e.getActionCommand())) {
				File file = new File(GlobalVariables.sReportDstDirectoryPath+"//index.htm");
				Desktop desktop = Desktop.getDesktop();	
				desktop.open(file);
			}
			 // when clicked on 'Send message' button
			else if ("message".equals(e.getActionCommand())){
				@SuppressWarnings("unused")
				EmailNotification emi=new EmailNotification();
			}
			 // when clicked on 'Exit' button
			else if ("exit".equals(e.getActionCommand())) { // when clicked on 'Exit' button
				System.exit(0);
			}
			
		} 
		catch (IOException ex) {
			System.out.println("Error Occured in ActionPerformed Function.");
			ex.printStackTrace();
		}
	}

	/**
	 * This method is used to clear the Test Plan Result
	 * @author Afour 
	 */
	private void clearTestPlanResult() {
		try {
			DefaultListModel listModel = new DefaultListModel();
			listModel.removeAllElements();
			GlobalVariables.jListExecute.setModel(listModel);
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
			jButtonSendMessage.setEnabled(false);
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
		catch (Exception e) {
			System.out.println("Error Occured in ClearTestPlanResult Function.");
			e.printStackTrace();
		}
	}
	
	/**
	 *  This method is called when Item Status Changed
	 *  @author Afour 
	 */
	public void itemStateChanged(ItemEvent e) {
		try {
			if (e.getSource() == jCheckBoxView) {
				if (e.getStateChange() == 1)
					jListView.setSelectionInterval(0, jListView.getModel().getSize() - 1);
				else
					jListView.clearSelection();
			}
			else if (e.getSource() == jCheckBoxPlan) {
				if (e.getStateChange() == 1)
					jListPlan.setSelectionInterval(0, jListPlan.getModel().getSize() - 1);
				else
					jListPlan.clearSelection();
			}
			else if (e.getSource() == jCheckBoxCommand) {
				if (e.getStateChange() == 1)
					jListCommand.setSelectionInterval(0, jListCommand.getModel().getSize() - 1);
				else
					jListCommand.clearSelection();
			}
		}
		catch(Exception ex) {
			System.out.println("Error Occured in ItemStateChanged Function.");
			ex.printStackTrace();
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
				Home frame = new Home();
				frame.setDefaultCloseOperation(Home.EXIT_ON_CLOSE);
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