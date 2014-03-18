//package com.mindalliance.driverscripts;
//
//import java.awt.Desktop;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.util.Vector;
//
//import javax.imageio.ImageIO;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.DefaultListModel;
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JProgressBar;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.ListSelectionModel;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//
//import org.dyno.visual.swing.layouts.Constraints;
//import org.dyno.visual.swing.layouts.GroupLayout;
//import org.dyno.visual.swing.layouts.Leading;
//import org.dyno.visual.swing.layouts.Trailing;
//
//import com.mindalliance.configuration.BrowserController;
//import com.mindalliance.configuration.Configuration;
//import com.mindalliance.configuration.DataController;
//import com.mindalliance.configuration.ElementController;
//import com.mindalliance.configuration.GlobalVariables;
//import com.mindalliance.configuration.LogFunctions;
//import com.mindalliance.configuration.Reporting;
//
//
//public class UIHome extends JFrame implements ActionListener, ItemListener{
//	public int noOfViewTestCasesExecuted=0;
//	public int noOfPlanTestCasesExecuted=0;
//	public int noOfCommandTestCasesExecuted=0;
//	public JList jListExecute;
//	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
//	private static final long serialVersionUID = 1L;
//	private JList jListView;
//	private JScrollPane jScrollPane0;
//	private String arrayOfTestCaseId[] = new String[200];
//	private static int noOfSelectedTestCases;
//	private JButton jButtonAdd;
//	private JTextField jTextField0;
//	private JLabel jLabel0;
//	private JLabel jLabel1;
//	private JList jListPlan;
//	private JScrollPane jScrollPane1;
//	private JLabel jLabel2;
//	private JList jListCommand;
//	private JScrollPane jScrollPane2;
//	private JScrollPane jScrollPane3;
//	private JButton jButtonExecute;
//	private JProgressBar jProgressBarStatus;
//	private JLabel jLabelStatus;
//	private static int cnt;
//	private JLabel jLabelTestCaseId;
//	private JButton jButtonLogLink;
//	private JButton jButtonReportLink;
//	private JCheckBox jCheckBoxView;
//	private JCheckBox jCheckBoxPlan;
//	private JCheckBox jCheckBoxCommand;
//	private JLabel jLabelNumberOfTestCasesExecuted;
//	private JLabel jLabelStartDateTime;
//	private JLabel jLabelEndDateTime;
//	private JLabel jLabelNumberOfTestCasesPassed;
//	private JLabel jLabelNumberOfTestCasesFailed;
//	private JPanel jPanelReport;
//	private JPanel jPanelLogo;
//	BufferedImage image;
//	private JButton jButtonExit;
//	private JButton jButtonNewTest;
//	private JComboBox jComboBoxBrowser;
//	private JButton jButtonSendMessage;
//	
//	// Constructor 
//	public UIHome() {
//		try {
//			if (GlobalVariables.configuration == null){
//				GlobalVariables.configuration = Configuration.getConfigurationObject();
//		}
//		if(GlobalVariables.configuration.getAttrSearchList() == null){
//			new ElementController();
//		}
//	// Initialize Components
//			initComponents();
//		}
//		catch (Exception e) {
//			System.out.println("In InitComponents Function.");
//			e.printStackTrace();
//		}
//	}
//	
//	// Parameterized Constructor for Image
//	UIHome(BufferedImage image) {
//		this.image = image;
//	}
//	
//	private void initComponents() {
//		setTitle("Mind Alliance Automation Framework");
//		setLayout(new GroupLayout());		
//		// TextField
//		add(getJTextField0(), new Constraints(new Leading(256, -53, 10, 10), new Leading(63, 12, 12)));		
//		// Scroll Panes
//		add(getJScrollPane0(), new Constraints(new Leading(41, 298, 10, 10), new Leading(34, 317, 12, 12)));
//		add(getJScrollPane1(), new Constraints(new Leading(338, 298, 10, 10), new Leading(34, 317, 10, 10)));
//		add(getJScrollPane2(), new Constraints(new Leading(634, 298, 10, 10), new Leading(34, 317, 10, 10)));
//		add(getJScrollPane3(), new Constraints(new Leading(1027, 298, 10, 10), new Leading(34, 317, 10, 10)));		
//		// Buttons
//		add(getJButton0(), new Constraints(new Leading(959, 10, 10), new Leading(174, 12, 12)));
//		add(getJButton1(), new Constraints(new Leading(1153, 12, 12), new Leading(369, 12, 12)));
//		add(getJButton2(), new Constraints(new Leading(1027, 12, 12), new Leading(525, 10, 10)));
//		add(getJButton3(), new Constraints(new Leading(1027, 12, 12), new Leading(575, 10, 10)));
//		add(getJButton4(), new Constraints(new Leading(1026, 12, 12), new Leading(622, 10, 10)));		
//		add(getJButton5(), new Constraints(new Leading(1239, 10, 10), new Leading(369, 12, 12)));
//		add(getJButton6(), new Constraints(new Leading(1137, 112, 12, 12), new Leading(575, 12, 12)));		
//		// Labels
//		add(getJLabel0(), new Constraints(new Leading(41, 12, 12), new Leading(8, 10, 10)));
//		add(getJLabel1(), new Constraints(new Leading(341, 10, 10), new Leading(8, 12, 12)));
//		add(getJLabel2(), new Constraints(new Leading(637, 12, 12), new Leading(10, 12, 12, 12)));
//		add(getJLabel3(), new Constraints(new Leading(1024, 12, 12), new Leading(453, 10, 10)));
//		add(getJLabel4(), new Constraints(new Leading(1024, 12, 12), new Leading(489, 12, 12)));		
//		// CheckBoxes
//		add(getJCheckBox0(), new Constraints(new Leading(166, 10, 10), new Leading(4, 8, 8)));
//		add(getJCheckBox1(), new Constraints(new Leading(374, 8, 8), new Leading(4, 8, 8)));
//		add(getJCheckBox2(), new Constraints(new Leading(798, 10, 10), new Leading(4, 8, 8)));		
//		// Panel
//		add(getJPanel0(), new Constraints(new Leading(43, 892, 10, 10), new Leading(363, 278, 10, 10)));		
//		// ProgressBar
//		add(getJProgressBar0(), new Constraints(new Leading(1024, 298, 10, 10), new Leading(413, 12, 12)));		
//		// ComboBox
//		add(getJComboBox0(), new Constraints(new Leading(1023, 122, 10, 10), new Leading(369, 12, 12)));		
//		setSize(1356, 698);
//	}
//
//	// JTextField
//	private JTextField getJTextField0() {
//		if (jTextField0 == null) {
//			jTextField0 = new JTextField();
//		}
//		return jTextField0;
//	}
//	
//	// JScrollPane
//	private JScrollPane getJScrollPane0() {
//		if (jScrollPane0 == null) {
//			jScrollPane0 = new JScrollPane();
//			jScrollPane0.setViewportView(getJList0());
//		}
//		return jScrollPane0;
//	}
//	private JScrollPane getJScrollPane1() {
//		if (jScrollPane1 == null) {
//			jScrollPane1 = new JScrollPane();
//			jScrollPane1.setViewportView(getJList1());
//		}
//		return jScrollPane1;
//	}
//	private JScrollPane getJScrollPane2() {
//		if (jScrollPane2 == null) {
//			jScrollPane2 = new JScrollPane();
//			jScrollPane2.setViewportView(getJList2());
//		}
//		return jScrollPane2;
//	}
//	private JScrollPane getJScrollPane3() {
//		if (jScrollPane3 == null) {
//			jScrollPane3 = new JScrollPane();
//			jScrollPane3.setViewportView(getJList3());
//		}
//		return jScrollPane3;
//	}
//
//	// JButton
//	private JButton getJButton0() {
//		if (jButtonAdd == null) {
//			jButtonAdd = new JButton();
//			jButtonAdd.setText(">");
//			jButtonAdd.setActionCommand("add");
//			jButtonAdd.addActionListener(this);
//		}
//		return jButtonAdd;
//	}
//	private JButton getJButton1() {
//		if (jButtonExecute == null) {
//			jButtonExecute = new JButton();
//			jButtonExecute.setText("Execute");
//			jButtonExecute.setActionCommand("execute");
//			jButtonExecute.addActionListener(this);
//		}
//		return jButtonExecute;
//	}
//	private JButton getJButton2() {
//		if (jButtonLogLink == null) {
//			jButtonLogLink = new JButton();
//			jButtonLogLink.setText("Logs");
//			jButtonLogLink.setEnabled(false);
//			jButtonLogLink.setActionCommand("logs");
//			jButtonLogLink.addActionListener(this);
//		}
//		return jButtonLogLink;
//	}
//	private JButton getJButton3() {
//		if (jButtonReportLink == null) {
//			jButtonReportLink = new JButton();
//			jButtonReportLink.setText("Reports");
//			jButtonReportLink.setEnabled(false);
//			jButtonReportLink.setActionCommand("reports");
//			jButtonReportLink.addActionListener(this);
//		}
//		return jButtonReportLink;
//	}
//	private JButton getJButton4() {
//		if (jButtonExit == null) {
//			jButtonExit = new JButton();
//			jButtonExit.setText("Exit");
//			jButtonExit.setActionCommand("exit");
//			jButtonExit.addActionListener(this);
//		}
//		return jButtonExit;
//	}
//	private JButton getJButton5() {
//		if (jButtonNewTest == null) {
//			jButtonNewTest = new JButton();
//			jButtonNewTest.setText("New Test");
//			jButtonNewTest.setActionCommand("newtest");
//			jButtonNewTest.addActionListener(this);
//		}
//		return jButtonNewTest;
//	}
//	private JButton getJButton6() {
//		if (jButtonSendMessage == null) {
//			jButtonSendMessage = new JButton();
//			jButtonSendMessage.setText("Send Email");
//			jButtonSendMessage.setEnabled(false);
//			jButtonSendMessage.setActionCommand("message");
//			jButtonSendMessage.addActionListener(this);
//		}
//		return jButtonSendMessage;
//	}
//	
//	// JLabel
//	private JLabel getJLabel0() {
//		if (jLabel0 == null) {
//			jLabel0 = new JLabel();
//			jLabel0.setText("Tree Navigation View");
//		}
//		return jLabel0;
//	}
//	private JLabel getJLabel1() {
//		if (jLabel1 == null) {
//			jLabel1 = new JLabel();
//			jLabel1.setText("Plan");
//		}
//		return jLabel1;
//	}
//	private JLabel getJLabel2() {
//		if (jLabel2 == null) {
//			jLabel2 = new JLabel();
//			jLabel2.setText("Undo and Redo Commands");
//		}
//		return jLabel2;
//	}
//	private JLabel getJLabel3() {
//		if (jLabelStatus == null) {
//			jLabelStatus = new JLabel();
//			jLabelStatus.setText("Status:");
//		}
//		return jLabelStatus;
//	}
//	private JLabel getJLabel4() {
//		if (jLabelTestCaseId == null) {
//			jLabelTestCaseId = new JLabel();
//			jLabelTestCaseId.setText("TestCaseId: ");
//		}
//		return jLabelTestCaseId;
//	}
//	private JLabel getJLabel5() {
//		if (jLabelStartDateTime == null) {
//			jLabelStartDateTime = new JLabel();
//			jLabelStartDateTime.setText("Start DateTime: ");
//		}
//		return jLabelStartDateTime;
//	}
//	private JLabel getJLabel6() {
//		if (jLabelEndDateTime == null) {
//			jLabelEndDateTime = new JLabel();
//			jLabelEndDateTime.setText("End DateTime: ");
//		}
//		return jLabelEndDateTime;
//	}
//	private JLabel getJLabel7() {
//		if (jLabelNumberOfTestCasesExecuted == null) {
//			jLabelNumberOfTestCasesExecuted = new JLabel();
//			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: ");
//		}
//		return jLabelNumberOfTestCasesExecuted;
//	}
//	private JLabel getJLabel8() {
//		if (jLabelNumberOfTestCasesPassed == null) {
//			jLabelNumberOfTestCasesPassed = new JLabel();
//			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: ");
//		}
//		return jLabelNumberOfTestCasesPassed;
//	}
//	private JLabel getJLabel9() {
//		if (jLabelNumberOfTestCasesFailed == null) {
//			jLabelNumberOfTestCasesFailed = new JLabel();
//			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: ");
//		}
//		return jLabelNumberOfTestCasesFailed;
//	}
//
//	// JCheckBox
//	private JCheckBox getJCheckBox0() {
//		if (jCheckBoxView == null) {
//			jCheckBoxView = new JCheckBox();
//			jCheckBoxView.setText("Select All");
//			jCheckBoxView.addItemListener(this);
//		}
//		return jCheckBoxView;
//	}
//	private JCheckBox getJCheckBox1() {
//		if (jCheckBoxPlan == null) {
//			jCheckBoxPlan = new JCheckBox();
//			jCheckBoxPlan.setText("Select All");
//			jCheckBoxPlan.addItemListener(this);
//		}
//		return jCheckBoxPlan;
//	}
//	private JCheckBox getJCheckBox2() {
//		if (jCheckBoxCommand == null) {
//			jCheckBoxCommand = new JCheckBox();
//			jCheckBoxCommand.setText("Select All");
//			jCheckBoxCommand.addItemListener(this);
//		}
//		return jCheckBoxCommand;
//	}
//
//	// JPanel
//	private JPanel getJPanel0() {
//		if (jPanelReport == null) {
//			jPanelReport = new JPanel();
//			jPanelReport.setLayout(new GroupLayout());
//			jPanelReport.add(getJPanel1(), new Constraints(new Trailing(12, 100, 228, 228), new Leading(8, 100, 10, 10)));
//			jPanelReport.add(getJLabel9(), new Constraints(new Leading(51, 12, 12), new Leading(226, 10, 10)));
//			jPanelReport.add(getJLabel8(), new Constraints(new Leading(41, 12, 12), new Leading(183, 10, 10)));
//			jPanelReport.add(getJLabel7(), new Constraints(new Leading(31, 12, 12), new Leading(139, 10, 10)));
//			jPanelReport.add(getJLabel6(), new Constraints(new Leading(132, 30, 130), new Leading(95, 10, 10)));
//			jPanelReport.add(getJLabel5(), new Constraints(new Leading(125, 30, 130), new Leading(53, 10, 10)));
//		}
//		return jPanelReport;
//	}
//	private JPanel getJPanel1() {
//		if (jPanelLogo == null) {
//			jPanelLogo = new JPanel();
//			try	{ 
//				image = ImageIO.read(new File(GlobalVariables.configuration.getCurrentDir() + "//Images//Mind-Alliance_Logo.png"));
//			} 
//			catch (Exception ex) {
//				System.out.println("Error Occured in getJPanel1 Function.");
//				ex.printStackTrace();
//			}
//		    jPanelLogo.setLayout(new GroupLayout());
//		}
//		return jPanelLogo;
//	}
//	
//	// JProgressBar
//	private JProgressBar getJProgressBar0() {
//		if (jProgressBarStatus == null) {
//			jProgressBarStatus = new JProgressBar();
//		}
//		return jProgressBarStatus;
//	}
//	public void updateProgressBar(int percent) {
//		jProgressBarStatus.setValue(percent);
//		jProgressBarStatus.setString("Completed: " + Integer.toString(percent) + "/" + noOfSelectedTestCases);
//		jProgressBarStatus.setStringPainted(true);
//		Rectangle progressRect = jProgressBarStatus.getBounds();
//		progressRect.x = 0;
//		progressRect.y = 0;
//		jProgressBarStatus.paintImmediately(progressRect);
//	}
//	
//	// JComboBox
//	private JComboBox getJComboBox0() {
//		if (jComboBoxBrowser == null) {
//			jComboBoxBrowser = new JComboBox();
//			jComboBoxBrowser.setModel(new DefaultComboBoxModel(new Object[] { "Mozilla Firefox", "Internet Explorer" ,"Chrome"}));
//			jComboBoxBrowser.setDoubleBuffered(false);
//			jComboBoxBrowser.setBorder(null);
//			
//		}
//		return jComboBoxBrowser;
//	}
//	
//	// JList
//	private JList getJList3() {
//		if (jListExecute == null) {
//			jListExecute = new JList();
//			DefaultListModel listModel = new DefaultListModel();
//			jListExecute.setModel(listModel);
//		}
//		return jListExecute;
//	}
//	
//	Reporting reporting= new Reporting();
//	
//	
//	private JList getJList0() {
//		int actualLength=0;
//		
//		try {
//			jListView = new JList();
//			DefaultListModel listModel = new DefaultListModel();
//			arrayOfTestCaseId = reporting.readTestCaseId(1);
//			for(int j=0;j<arrayOfTestCaseId.length;j++){
//				if(arrayOfTestCaseId[j]!=null)
//					actualLength++;
//			}
//			for (int i=0;i<actualLength;i++)
//			{
//				listModel.addElement(arrayOfTestCaseId[i]);
//			}
//			jListView.setModel(listModel);
//			jListView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//			return jListView;
//		}
//		catch (Exception e) {
//			System.out.println("Error Occured in getJList0 Function.");
//			e.printStackTrace();
//		}
//		return jListView;
//	}
//	
//	private JList getJList1() {
//		int actualLength=0;
//		try {
//			jListPlan = new JList();
//			DefaultListModel listModel = new DefaultListModel();
//			arrayOfTestCaseId = reporting.readTestCaseId(2);
//			
//			for(int j=0;j<arrayOfTestCaseId.length;j++){
//				if(arrayOfTestCaseId[j]!=null)
//					actualLength++;
//			}
//			for (int i = 0; i <actualLength ; i++ ) 
//				listModel.addElement(arrayOfTestCaseId[i]);
//			
//			jListPlan.setModel(listModel);
//			jListPlan.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//			return jListPlan;
//		}
//		catch (Exception e) {
//			System.out.println("Error Occured in getJList1 Function.");
//			e.printStackTrace();
//		}
//		return jListPlan;
//	}
//	private JList getJList2() {
//		int actualLength=0;
//		try {
//			jListCommand = new JList();
//			DefaultListModel listModel = new DefaultListModel();
//			arrayOfTestCaseId = reporting.readTestCaseId(3);
//			for(int j=0;j<arrayOfTestCaseId.length;j++){
//				if(arrayOfTestCaseId[j]!=null)
//					actualLength++;
//			}
//			for (int i = 0; i <actualLength; i++)
//				listModel.addElement(arrayOfTestCaseId[i]);
//			jListCommand.setModel(listModel);
//			jListCommand.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//			return jListCommand;
//		}
//		catch (Exception e) {
//			System.out.println("Error Occured in getJList2 Function.");
//			e.printStackTrace();
//		}
//		return jListCommand;
//	}
//	
//	// Execute from package
//	public void executeTestCases(Vector<Object> arrayOfTestCaseId) {
//		int totalExecute;
//		try {
//			
//			// Select browser from Combo box
//			String browser=jComboBoxBrowser.getSelectedItem().toString();
//			BrowserController browserController=new BrowserController();
//			browserController.initializeDriver(browser);
//			
//			String log_folder=GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+"\\Logs\\UILogs";
//			
//			//			String log_folder="D:\\Channels\\Mind-AllianceFramework_V2\\Logs\\UILogs";
//			File log_file=new File(log_folder);
//			deleteFolder(log_file);
//			
//			Class<?> cls;
//			cnt = 0;
//					
//			DataController dataController = new DataController();
//			dataController.createResultFiles();
//			
//			// Set progressBar Values
//			jProgressBarStatus.setMinimum(0);
//			jProgressBarStatus.setMaximum(noOfSelectedTestCases);
//			// Set Status label
//			jLabelStatus.setText("Status: Automation TestPlan script started");
//            jLabelStatus.setSize(jLabelStatus.getPreferredSize());
//			jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
//			
//			// Set startDateTime label
//			jLabelStartDateTime.setText("Start DateTime: " + LogFunctions.getDateTime());
//			jLabelStartDateTime.setSize(jLabelStartDateTime.getPreferredSize());
//			
//			// Execution of selected TestCases
//			for (Object testCaseId: arrayOfTestCaseId) {
//				try {
//					//Clear TestCaseId label
//					jLabelTestCaseId.removeAll();
//					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
//					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
//					//Set TestCaseId label
//					jLabelTestCaseId.setText("Executing TestCaseId: " + testCaseId.toString());
//					jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
//					jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());							
//					
//					//Execute current TestCaseId
//					ClassLoader classLoader=ClassLoader.getSystemClassLoader();
//					String className="com.mindalliance.uitestscripts."+testCaseId;
//					cls = classLoader.loadClass(className);
//					cls.newInstance();
//							
//					//Update progressBar
//					cnt = cnt + 1;
//					updateProgressBar(cnt);
//				}
//				catch(Exception e) {
//					//Update progressBar
//					cnt = cnt + 1;
//					updateProgressBar(cnt);
//				}
//			}
//			
//			GlobalVariables.configuration.setEndtime(LogFunctions.getDateTime());
//			jLabelEndDateTime.setText("End DateTime: "+ LogFunctions.getDateTime());
//			jLabelEndDateTime.setSize(jLabelEndDateTime.getPreferredSize());
//			
//			// Convert vector to array of string and then to Jlist
//			String[] arrayOfTestCases=new String[arrayOfTestCaseId.size()];
//			int i=0;
//			for(Object str : arrayOfTestCaseId){
//				arrayOfTestCases[i++]=(String) str;
//			}					
//			
//			JList<String> jlist=new JList<String>(arrayOfTestCases);
//			GlobalVariables.configuration.setList(jlist);				
//			String folder=GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+File.separator+"Reports"+File.separator+"UIAutomationReport";
//		
////			String folder="D:\\Channels\\Mind-AllianceFramework_V2\\Reports\\UIAutomationReport";
//			File file1=new File(folder);
//			deleteFolder(file1);
//			
//			
//			
//			reporting.generateAutomationReport();
//			// Enable Logs and Reports button
//			jButtonLogLink.setEnabled(true);
//			jButtonReportLink.setEnabled(true);
//			jButtonSendMessage.setEnabled(true);
//			// Clear TestCaseId label
//			jLabelTestCaseId.setText("");
//			jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
//			jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
//			// Set Status label
//			jLabelStatus.setText("Status: Automation TestPlan script completed");
//			jLabelStatus.setSize(jLabelStatus.getPreferredSize());
//			jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
//		}
//		catch (Exception e) {
//			System.out.println("Error Occured in ExecuteTestCases Function.");
//			e.printStackTrace();
//		}
//	}
//	public void deleteFolder(File folder){
//		File[] files = folder.listFiles();
//		    if(files!=null) { //some JVMs return null for empty dirs
//		        for(File f: files) {
//		            if(f.isDirectory()) {
//		                deleteFolder(f);
//		            } else {
//		                f.delete();
//		            }
//		        }
//		    }
//	}
//	// Called When Action Performed
//	public void actionPerformed(ActionEvent e) {
//		try {
//			if ("add".equals(e.getActionCommand())) {// when clicked on '>' button
//				Object[] arrayOfListObject;
//				DefaultListModel listModel = new DefaultListModel();
//				arrayOfListObject = jListView.getSelectedValues();
//				for (Object listObject : arrayOfListObject){ 
//					listModel.addElement(listObject);
//						noOfViewTestCasesExecuted++;
//				}
//				jListExecute.setModel(listModel);
//				
//				arrayOfListObject = jListPlan.getSelectedValues();
//				for (Object listObject : arrayOfListObject){ 
//					listModel.addElement(listObject);
//						noOfPlanTestCasesExecuted++;
//				}
//				jListExecute.setModel(listModel);
//				
//				arrayOfListObject = jListCommand.getSelectedValues();
//				for (Object listObject : arrayOfListObject){ 
//					listModel.addElement(listObject);
//						noOfCommandTestCasesExecuted++;
//				}
//				jListExecute.setModel(listModel);
//				
//					System.out.println("Total Count : " + (noOfViewTestCasesExecuted +noOfPlanTestCasesExecuted + noOfCommandTestCasesExecuted));
//			}
//			else if ("execute".equals(e.getActionCommand())) { // when clicked on 'Execute' button
//				if (jListExecute.getModel().getSize() > 0) {
//					jButtonExecute.setEnabled(false);
//					Vector<Object> vc = new Vector<Object>();
//					noOfSelectedTestCases = 0;
//					for (int i = 0; i < jListExecute.getModel().getSize(); i++) {
//						noOfSelectedTestCases ++;
//						vc.add(jListExecute.getModel().getElementAt(i));
//					}
//					executeTestCases(vc);
//					jButtonExecute.setEnabled(true);
//				}
//				else
//					JOptionPane.showMessageDialog(rootPane, "Please select the testcases.");
//			}
//			else if ("newtest".equals(e.getActionCommand())) { 
//				// when clicked on 'New Test' button
//					clearTestPlanResult();
//			}
//			else if("logs".equals(e.getActionCommand())){
//				File file=new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+"\\Logs\\UILogs");
////				File file=new File("D:\\Channels\\Mind-AllianceFramework_V2\\Logs\\UILogs");
//				Desktop.getDesktop().open(file);
//			}
//			else if ("reports".equals(e.getActionCommand())) { // when clicked on 'Reports' button
//				File file = new File(GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+"\\Reports\\UIAutomationReport\\index.htm");
////				File file = new File("D:\\Channels\\Mind-AllianceFramework_V2\\Reports\\UIAutomationReport"+"\\index.htm");
//				Desktop.getDesktop().open(file);
//			}
//			else if("message".equals(e.getActionCommand())){
//				@SuppressWarnings("unused")
//			    EmailNotification emi=new EmailNotification();
//			}
//			else if ("exit".equals(e.getActionCommand())) { // when clicked on 'Exit' button
//				System.exit(0);
//			}
//			
//		} 
//		catch (Exception ex) {
//			System.out.println("Error Occured in ActionPerformed Function.");
//			ex.printStackTrace();
//		}
//	}
//
//	// Clear Test Plan Result
//	private void clearTestPlanResult() {
//		try {
//			DefaultListModel listModel = new DefaultListModel();
//			listModel.removeAllElements();
//			jListExecute.setModel(listModel);
//			// Clear progressBar Values
//			jProgressBarStatus.setMinimum(0);
//			jProgressBarStatus.setMaximum(0);
//			// Clear Status label
//			jLabelStatus.setText("Status: ");
//			jLabelStatus.setSize(jLabelStatus.getPreferredSize());
//			jLabelStatus.paintImmediately(jLabelStatus.getVisibleRect());
//			// Clear TestCaseId label
//			jLabelTestCaseId.setText("");
//			jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
//			jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
//			// Clear TestCaseId label
//			jLabelTestCaseId.setText("TestCaseId: ");
//			jLabelTestCaseId.setSize(jLabelTestCaseId.getPreferredSize());
//			jLabelTestCaseId.paintImmediately(jLabelTestCaseId.getVisibleRect());
//			jButtonLogLink.setEnabled(false);
//			jButtonReportLink.setEnabled(false);
//			jButtonSendMessage.setEnabled(false);
//			// Clear startDateTime label
//			jLabelStartDateTime.setText("Start DateTime: ");
//			jLabelStartDateTime.setSize(jLabelStartDateTime.getPreferredSize());
//			jLabelStartDateTime.paintImmediately(jLabelStartDateTime.getVisibleRect());
//			// Clear endDateTime label
//			jLabelEndDateTime.setText("End DateTime: ");
//			jLabelEndDateTime.setSize(jLabelEndDateTime.getPreferredSize());
//			jLabelEndDateTime.paintImmediately(jLabelEndDateTime.getVisibleRect());
//			// Clear totalExecuted TestCaseId
//			jLabelNumberOfTestCasesExecuted.setText("Number of TestCases Executed: ");
//			jLabelNumberOfTestCasesExecuted.setSize(jLabelNumberOfTestCasesExecuted.getPreferredSize());
//			jLabelNumberOfTestCasesExecuted.paintImmediately(jLabelNumberOfTestCasesExecuted.getVisibleRect());
//			// Clear totalTestCasesPassed
//			jLabelNumberOfTestCasesPassed.setText("Number of TestCases Passed: ");
//			jLabelNumberOfTestCasesPassed.setSize(jLabelNumberOfTestCasesPassed.getPreferredSize());
//			jLabelNumberOfTestCasesPassed.paintImmediately(jLabelNumberOfTestCasesPassed.getVisibleRect());
//			// Clear totalTestCasesFailed
//			jLabelNumberOfTestCasesFailed.setText("Number of TestCases Failed: ");
//			jLabelNumberOfTestCasesFailed.setSize(jLabelNumberOfTestCasesFailed.getPreferredSize());
//			jLabelNumberOfTestCasesFailed.paintImmediately(jLabelNumberOfTestCasesFailed.getVisibleRect());
//			// Clear progressBar String
//			jProgressBarStatus.setString("");
//			jProgressBarStatus.setStringPainted(true);
//			Rectangle progressRect = jProgressBarStatus.getBounds();
//			progressRect.x = 0;
//			progressRect.y = 0;
//			jProgressBarStatus.paintImmediately(progressRect);
//		}
//		catch (Exception e) {
//			System.out.println("Error Occured in ClearTestPlanResult Function.");
//			e.printStackTrace();
//		}
//	}
//	
//	// Called when Item Status Changed
//	public void itemStateChanged(ItemEvent e) {
//		try {
//			if (e.getSource() == jCheckBoxView) {
//				if (e.getStateChange() == 1)
//					jListView.setSelectionInterval(0, jListView.getModel().getSize() - 1);
//				else
//					jListView.clearSelection();
//			}
//			else if (e.getSource() == jCheckBoxPlan) {
//				if (e.getStateChange() == 1)
//					jListPlan.setSelectionInterval(0, jListPlan.getModel().getSize() - 1);
//				else
//					jListPlan.clearSelection();
//			}
//			else if (e.getSource() == jCheckBoxCommand) {
//				if (e.getStateChange() == 1)
//					jListCommand.setSelectionInterval(0, jListCommand.getModel().getSize() - 1);
//				else
//					jListCommand.clearSelection();
//			}
//		}
//		catch(Exception ex) {
//			System.out.println("Error Occured in ItemStateChanged Function.");
//			ex.printStackTrace();
//		}
//	}
//	// Look & Feel
//	private static void installLnF() {
//		try {
//			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
//			UIManager.setLookAndFeel(lnfClassname);
//		} 
//		catch (Exception e) {
//			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL + " on this platform:" + e.getMessage());
//		}
//	}
//	
//	public static void main(String[] args) {
//	installLnF();
//	SwingUtilities.invokeLater(new Runnable() {
//		@Override
//		public void run() {
//			UIHome frame = new UIHome();
//			frame.setDefaultCloseOperation(UIHome.EXIT_ON_CLOSE);
//			frame.setTitle("Mind-Alliance Automation Framework");
//			//frame.getContentPane().setPreferredSize(frame.getSize());
//			frame.pack();
//			frame.setLocationRelativeTo(null);
//			frame.setVisible(true);
//			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
//		}
//	});
//	}
//}
