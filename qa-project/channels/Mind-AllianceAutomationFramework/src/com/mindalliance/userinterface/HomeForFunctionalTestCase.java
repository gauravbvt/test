package com.mindalliance.userinterface;

import java.awt.Component;
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
public class HomeForFunctionalTestCase extends JFrame implements ActionListener, ItemListener{
	boolean clFlag=true,hpFlag=true,caFlag=true,dcFlag=true,ceFlag=true,cpFlag=true,psFlag=true,tfFlag=true,teFlag=true,isgFlag=true,ifmFlag=true,acFlag=true,lfFlag=true,misgFlag=true,isrFlag=true;
	private static final long serialVersionUID = 1L;
	private static int jListCount=0;
	private JList jListFunctional;
	private JScrollPane jScrollPane0;
	private String arrayOfTestCaseIdOld[];
	private String arrayOfTestCaseIdNew[];
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
	private JCheckBox jCheckBoxFunctionalTestCase;
	private JCheckBox jCheckBoxChannelsLogin;
	private JCheckBox jCheckBoxHomePage;
	private JCheckBox jCheckBoxChannelsAdmin;
	private JCheckBox jCheckBoxDisplayControl;
	private JCheckBox jCheckBoxChannelsCommands;
	private JCheckBox jCheckBoxCollaborationPanel;
	private JCheckBox jCheckBoxPlanSegmentBar;
	private JCheckBox jCheckBoxTaskFlowPanel;
	private JCheckBox jCheckBoxEntities;
	private JCheckBox jCheckBoxInfoFlowMap;
	private JCheckBox jCheckBoxInfoSharing;
	private JCheckBox jCheckBoxAssignmentCommitments;
	private JCheckBox jCheckBoxLockFunctionality;
	private JCheckBox jCheckBoxInfoGuidelines;
	private JCheckBox jCheckBoxIssueReportSummary;
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
	private static DefaultListModel listModel;
	private JButton jButtonSendEmail;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public HomeForFunctionalTestCase() {
		initComponents();
	}

	private void initComponents() {
		setTitle("Mind Alliance Automation Framework");
		setLayout(new GroupLayout());
		add(getJTextField0(), new Constraints(new Leading(256, -53, 10, 10), new Leading(63, 12, 12)));
		add(getJScrollPane3(), new Constraints(new Leading(1027, 298, 10, 10), new Leading(34, 317, 10, 10)));
		add(getJScrollPane0(), new Constraints(new Leading(500, 350, 100, 50), new Leading(34, 317, 12, 12)));
		add(getJButton2(), new Constraints(new Leading(1027, 12, 12), new Leading(525, 10, 10)));
		add(getJLabel0(), new Constraints(new Leading(500, 200, 200), new Leading(16, 20, 20)));
		add(getJCheckBox0(), new Constraints(new Leading(620, 300, 600), new Leading(10, 10, 10)));
		add(getJPanel0(), new Constraints(new Leading(43, 892, 10, 10), new Leading(363, 278, 10, 10)));
		add(getJProgressBar0(), new Constraints(new Leading(1024, 298, 10, 10), new Leading(413, 12, 12)));
		add(getJLabel3(), new Constraints(new Leading(1024, 12, 12), new Leading(453, 10, 10)));
		add(getJLabel4(), new Constraints(new Leading(1024, 12, 12), new Leading(489, 12, 12)));
		add(getJButton3(), new Constraints(new Leading(1027, 12, 12), new Leading(575, 10, 10)));
		add(getJButton4(), new Constraints(new Leading(1026, 12, 12), new Leading(622, 10, 10)));
		add(getJButton0(), new Constraints(new Leading(900, 10, 10), new Leading(174, 12, 12)));
		add(getJButton5(), new Constraints(new Leading(1239, 10, 10), new Leading(369, 12, 12)));
		add(getJButton1(), new Constraints(new Leading(1153, 12, 12), new Leading(369, 12, 12)));
		add(getJComboBox0(), new Constraints(new Leading(1023, 122, 10, 10), new Leading(369, 12, 12)));
		add(getJCheckBox1(), new Constraints(new Leading(100, 300, 200), new Leading(30, 10, 10)));
		add(getJCheckBox2(), new Constraints(new Leading(100, 400, 300), new Leading(50, 10, 10)));
		add(getJCheckBox3(), new Constraints(new Leading(100, 500, 400), new Leading(70, 10, 10)));
		add(getJCheckBox4(), new Constraints(new Leading(100, 600, 500), new Leading(90, 10, 10)));
		add(getJCheckBox5(), new Constraints(new Leading(100, 700, 600), new Leading(110, 10, 10)));
		add(getJCheckBox6(), new Constraints(new Leading(100, 800, 700), new Leading(130, 10, 10)));
		add(getJCheckBox7(), new Constraints(new Leading(100, 900, 800), new Leading(150, 10, 10)));
		add(getJCheckBox8(), new Constraints(new Leading(100, 910, 850), new Leading(170, 10, 10)));
		add(getJCheckBox9(), new Constraints(new Leading(100, 920, 850), new Leading(190, 10, 10)));
		add(getJCheckBox10(), new Constraints(new Leading(100, 930, 850), new Leading(210, 10, 10)));
		add(getJCheckBox11(), new Constraints(new Leading(100, 940, 850), new Leading(230, 10, 10)));
		add(getJCheckBox12(), new Constraints(new Leading(100, 950, 850), new Leading(250, 10, 10)));
		add(getJCheckBox13(), new Constraints(new Leading(100, 960, 850), new Leading(270, 10, 10)));
		add(getJCheckBox14(), new Constraints(new Leading(100, 970, 850), new Leading(290, 10, 10)));
		add(getJCheckBox15(), new Constraints(new Leading(100, 990, 850), new Leading(310, 10, 10)));
		add(getjButtonSendEmail(), new Constraints(new Leading(1126, 110, 10, 10), new Leading(525, 12, 12)));
		setSize(1356, 698);
	}

	private JButton getjButtonSendEmail() {
		if (jButtonSendEmail == null) {
			jButtonSendEmail = new JButton();
			jButtonSendEmail.setText("Send Email");
			jButtonSendEmail.setEnabled(false);
			jButtonSendEmail.setActionCommand("send");
			jButtonSendEmail.addActionListener(this);
		}
		return jButtonSendEmail;
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

	HomeForFunctionalTestCase(BufferedImage image) {
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
			jPanelReport.add(getJLabel9(), new Constraints(new Leading(51, 12, 12), new Leading(226, 10, 10)));
			jPanelReport.add(getJLabel8(), new Constraints(new Leading(41, 12, 12), new Leading(183, 10, 10)));
			jPanelReport.add(getJLabel7(), new Constraints(new Leading(31, 12, 12), new Leading(139, 10, 10)));
			jPanelReport.add(getJLabel6(), new Constraints(new Leading(132, 30, 130), new Leading(95, 10, 10)));
			jPanelReport.add(getJLabel5(), new Constraints(new Leading(125, 30, 130), new Leading(53, 10, 10)));
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
		if (jCheckBoxFunctionalTestCase == null) {
			jCheckBoxFunctionalTestCase = new JCheckBox();
			jCheckBoxFunctionalTestCase.setText("Select All");
			jCheckBoxFunctionalTestCase.addItemListener(this);
		}
		return jCheckBoxFunctionalTestCase;
	}

	private JCheckBox getJCheckBox1(){
		if (jCheckBoxChannelsLogin == null) {
			jCheckBoxChannelsLogin = new JCheckBox();
			jCheckBoxChannelsLogin.setText("Channels Login");
			jCheckBoxChannelsLogin.addItemListener(this);
		}
		return jCheckBoxChannelsLogin;
	}

	private JCheckBox getJCheckBox2(){
		if (jCheckBoxHomePage == null) {
			jCheckBoxHomePage = new JCheckBox();
			jCheckBoxHomePage.setText("Home Page");
			jCheckBoxHomePage.addItemListener(this);
		}
		return jCheckBoxHomePage;
	}

	private JCheckBox getJCheckBox3(){
		if (jCheckBoxChannelsAdmin == null) {
			jCheckBoxChannelsAdmin = new JCheckBox();
			jCheckBoxChannelsAdmin.setText("Channels Administration");
			jCheckBoxChannelsAdmin.addItemListener(this);
		}
		return jCheckBoxChannelsAdmin;
	}

	private JCheckBox getJCheckBox4(){
		if (jCheckBoxDisplayControl == null) {
			jCheckBoxDisplayControl = new JCheckBox();
			jCheckBoxDisplayControl.setText("Display Controls");
			jCheckBoxDisplayControl.addItemListener(this);
		}
		return jCheckBoxDisplayControl;
	}

	private JCheckBox getJCheckBox5(){
		if (jCheckBoxChannelsCommands == null) {
			jCheckBoxChannelsCommands = new JCheckBox();
			jCheckBoxChannelsCommands.setText("Channels Commands");
			jCheckBoxChannelsCommands.addItemListener(this);
		}
		return jCheckBoxChannelsCommands;
	}

	private JCheckBox getJCheckBox6(){
		if (jCheckBoxPlanSegmentBar == null) {
			jCheckBoxPlanSegmentBar = new JCheckBox();
			jCheckBoxPlanSegmentBar.setText("Plan and Segment");
			jCheckBoxPlanSegmentBar.addItemListener(this);
		}
		return jCheckBoxPlanSegmentBar;
	}

	private JCheckBox getJCheckBox7(){
		if (jCheckBoxCollaborationPanel == null) {
			jCheckBoxCollaborationPanel = new JCheckBox();
			jCheckBoxCollaborationPanel.setText("Collaboration Panel");
			jCheckBoxCollaborationPanel.addItemListener(this);
		}
		return jCheckBoxCollaborationPanel;
	}

	private JCheckBox getJCheckBox8(){
		if (jCheckBoxTaskFlowPanel == null) {
			jCheckBoxTaskFlowPanel = new JCheckBox();
			jCheckBoxTaskFlowPanel.setText("Task and Flow Panel");
			jCheckBoxTaskFlowPanel.addItemListener(this);
		}
		return jCheckBoxTaskFlowPanel;
	}

	private JCheckBox getJCheckBox9(){
		if (jCheckBoxEntities == null) {
			jCheckBoxEntities = new JCheckBox();
			jCheckBoxEntities.setText("Entities");
			jCheckBoxEntities.addItemListener(this);
		}
		return jCheckBoxEntities;
	}

	private JCheckBox getJCheckBox10(){
		if (jCheckBoxInfoFlowMap == null) {
			jCheckBoxInfoFlowMap = new JCheckBox();
			jCheckBoxInfoFlowMap.setText("Information Flow Map");
			jCheckBoxInfoFlowMap.addItemListener(this);
		}
		return jCheckBoxInfoFlowMap;
	}

	private JCheckBox getJCheckBox11(){
		if (jCheckBoxInfoSharing == null) {
			jCheckBoxInfoSharing = new JCheckBox();
			jCheckBoxInfoSharing.setText("Information Sharing Guidelines");
			jCheckBoxInfoSharing.addItemListener(this);
		}
		return jCheckBoxInfoSharing;
	}

	private JCheckBox getJCheckBox12(){
		if (jCheckBoxAssignmentCommitments == null) {
			jCheckBoxAssignmentCommitments = new JCheckBox();
			jCheckBoxAssignmentCommitments.setText("Assignments and Commitments");
			jCheckBoxAssignmentCommitments.addItemListener(this);
		}
		return jCheckBoxAssignmentCommitments;
	}

	private JCheckBox getJCheckBox13(){
		if (jCheckBoxLockFunctionality == null) {
			jCheckBoxLockFunctionality = new JCheckBox();
			jCheckBoxLockFunctionality.setText("Lock Functionality");
			jCheckBoxLockFunctionality.addItemListener(this);
		}
		return jCheckBoxLockFunctionality;
	}

	private JCheckBox getJCheckBox14(){
		if (jCheckBoxInfoGuidelines == null) {
			jCheckBoxInfoGuidelines = new JCheckBox();
			jCheckBoxInfoGuidelines.setText("My Information Sharing Guidelines(User)");
			jCheckBoxInfoGuidelines.addItemListener(this);
		}
		return jCheckBoxInfoGuidelines;
	}

	private JCheckBox getJCheckBox15(){
		if (jCheckBoxIssueReportSummary == null) {
			jCheckBoxIssueReportSummary = new JCheckBox();
			jCheckBoxIssueReportSummary.setText("Issue Summary Report");
			jCheckBoxIssueReportSummary.addItemListener(this);
		}
		return jCheckBoxIssueReportSummary;
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
			jLabel0.setText("Functional Test Case");
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
		}
		return jScrollPane0;
	}

	private JList getJList0(int sheetNumber) {
		try {
			listModel = new DefaultListModel();
			if (jListFunctional == null) {
				jListFunctional = new JList();
				arrayOfTestCaseIdOld=new String[600];
				arrayOfTestCaseIdOld = ReportFunctions.readTestCaseIdForFunctional(sheetNumber);
				for (int i=0;i<GlobalVariables.iIndex;i++){
					jListCount=i;
					listModel.addElement(arrayOfTestCaseIdOld[i]);
				}
				jListCount++;
				jListFunctional.setModel(listModel);
				jListFunctional.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			else {
				jListFunctional = new JList();
				listModel=new DefaultListModel();
				arrayOfTestCaseIdNew=new String[600];
				arrayOfTestCaseIdNew = ReportFunctions.readTestCaseIdForFunctional(sheetNumber);
				for(int i=0;i<GlobalVariables.iIndex;i++) {
					arrayOfTestCaseIdOld[jListCount++]= arrayOfTestCaseIdNew[i];
				}
				for (int i=0;i<jListCount;i++) {
					listModel.addElement(arrayOfTestCaseIdOld[i]);
				}
				jListFunctional.setModel(listModel);
				jListFunctional.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			System.out.println("Total Count : "+jListCount);
			return jListFunctional;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return jListFunctional;
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
			GenericFunctionLibrary.loadObjectRepository();
			// Execution of selected TestCases
			for (Object testCaseId: arrayOfTestCaseId) {
				try
				{
					//Clear TestCaseId label
					jLabelTestCaseId.removeAll();
					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
					//Set TestCaseId label
					jLabelTestCaseId.setText("Executing TestCaseId: " + testCaseId.toString());
					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
					//Execute current TestCaseId
					cls = Class.forName("com.mindalliance.functionaltestsripts." + testCaseId);
					cls.newInstance();
					//Update progressBar
					cnt = cnt + 1;
					updateProgressBar(cnt);
				}
				catch(Exception e)
				{
					//Update progressBar
					cnt = cnt + 1;
					updateProgressBar(cnt);
					System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				}
			}
			// Call GenericFunctionLibrary.tearDownTestData()
			GenericFunctionLibrary.tearDownTestData();
			// Call ReportFunctions.generateAutomationReport()
			ReportFunctions.generateAutomationReportForFunctionalTestCases();
			// Enable Logs and Reports button
			jButtonLogLink.setEnabled(true);
			jButtonReportLink.setEnabled(true);
			jButtonSendEmail.setEnabled(true);
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


	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			if ("add".equals(e.getActionCommand())) {// when clicked on '>' button
				Object[] arrayOfListObject;
				DefaultListModel listModel = new DefaultListModel();
				arrayOfListObject = jListFunctional.getSelectedValues();
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
				File file = new File(GlobalVariables.sReportDstDirectoryPath+"//index.htm");
				Desktop desktop = Desktop.getDesktop();
				desktop.open(file);
			}
			else if("send".equals(e.getActionCommand())){
				EmailNotification emi=new EmailNotification();
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
		jButtonSendEmail.setEnabled(false);
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
		JCheckBox chk=(JCheckBox)e.getSource();
		if(chk.getText().equalsIgnoreCase("Channels Login")) {
			if(chk.isSelected() && clFlag==true) {
				jScrollPane0.setViewportView(getJList0(1));
				clFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("CL"));
				clFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Home Page")) {
			if(chk.isSelected() && hpFlag==true) {
				jScrollPane0.setViewportView(getJList0(2));
				hpFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("HP"));
				hpFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Channels Administration")) {
			if(chk.isSelected() && caFlag==true) {
				jScrollPane0.setViewportView(getJList0(3));
				caFlag=false;
			}
			else {
				jScrollPane0.setViewportView(getJListModified0("CA"));
				caFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Display Controls")) {
			if(chk.isSelected() && dcFlag==true) {
				jScrollPane0.setViewportView(getJList0(6));
				dcFlag=false;
			}
			else {
				jScrollPane0.setViewportView(getJListModified0("DC"));
				dcFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Channels Commands")) {
			if(chk.isSelected() && ceFlag==true) {
				jScrollPane0.setViewportView(getJList0(7));
				ceFlag=false;
			}
			else {
				jScrollPane0.setViewportView(getJListModified0("CC"));
				ceFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Collaboration Panel")) {
			if(chk.isSelected() && cpFlag==true) {
				jScrollPane0.setViewportView(getJList0(8));
				cpFlag=false;
			}
			else {
				jScrollPane0.setViewportView(getJListModified0("CP"));
				cpFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Plan and Segment")) {
			if(chk.isSelected() && psFlag==true){
				jScrollPane0.setViewportView(getJList0(9));
				psFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("PS"));
				psFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Task and Flow Panel")){
			if(chk.isSelected() && tfFlag==true){
				jScrollPane0.setViewportView(getJList0(10));
				tfFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("TF"));
				tfFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Entities")){
			if(chk.isSelected() && teFlag==true){
				jScrollPane0.setViewportView(getJList0(11));
				teFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("TE"));
				teFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Information Flow Map")){
			if(chk.isSelected() && ifmFlag==true){
				jScrollPane0.setViewportView(getJList0(12));
				ifmFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("IF"));
				ifmFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Information Sharing Guidelines")) {
			if(chk.isSelected() && isgFlag==true){
				jScrollPane0.setViewportView(getJList0(13));
				isgFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("PP"));
				isgFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Assignments and Commitments")){
			if(chk.isSelected() && acFlag==true){
				jScrollPane0.setViewportView(getJList0(14));
				acFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("PE"));
				acFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Lock Functionality")){
			if(chk.isSelected() && lfFlag==true){
				jScrollPane0.setViewportView(getJList0(15));
				lfFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("LF"));
				lfFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("My Information Sharing Guidelines(User)")){
			if(chk.isSelected() && misgFlag==true){
				jScrollPane0.setViewportView(getJList0(16));
				misgFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("SG"));
				misgFlag=true;
			}
		}
		else if(chk.getText().equalsIgnoreCase("Issue Summary Report")){
			if(chk.isSelected() && isrFlag==true){
				jScrollPane0.setViewportView(getJList0(17));
				isrFlag=false;
			}
			else{
				jScrollPane0.setViewportView(getJListModified0("IS"));
				isrFlag=true;
			}
		}
		// TODO Auto-generated method stub
		if (e.getSource() == jCheckBoxFunctionalTestCase) {
			if (e.getStateChange() == 1)
				jListFunctional.setSelectionInterval(0, jListFunctional.getModel().getSize() - 1);
			else
				jListFunctional.clearSelection();
		}
	}

	private Component getJListModified0(String textCaseName) {
		int j=0;
		arrayOfTestCaseIdNew=new String[500];
		for(int i=0;i<jListCount;i++){
			if(!(arrayOfTestCaseIdOld[i].substring(0,2).equals(textCaseName))){
				arrayOfTestCaseIdNew[j++]=arrayOfTestCaseIdOld[i];
			}
		}
		jListFunctional=new JList();
		listModel=new DefaultListModel();
		int cnt=0;
		for (int i=0;i<jListCount;i++){
			if(arrayOfTestCaseIdNew[i] != null) {
				cnt++;
				listModel.addElement(arrayOfTestCaseIdNew[i]);
			}
		}
		jListCount=cnt;
		for (int i=0;i<jListCount;i++)
			arrayOfTestCaseIdOld[i]=arrayOfTestCaseIdNew[i];
		jListFunctional.setModel(listModel);
		jListFunctional.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		return jListFunctional;
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
				HomeForFunctionalTestCase frame = new HomeForFunctionalTestCase();
				frame.setDefaultCloseOperation(HomeForFunctionalTestCase.EXIT_ON_CLOSE);
				frame.setTitle("Mind-Alliance Automation Framework For Functional Test Case");
				//frame.getContentPane().setPreferredSize(frame.getSize());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			}
		});
	}
}
