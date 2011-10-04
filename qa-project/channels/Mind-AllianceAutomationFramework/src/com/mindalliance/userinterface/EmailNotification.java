package com.mindalliance.userinterface;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import com.mindalliance.globallibrary.GlobalVariables;

//VS4E -- DO NOT REMOVE THIS LINE!
public class EmailNotification extends JFrame implements ActionListener, ItemListener{
	private static final long serialVersionUID = 1L;
//	private String arrayOfTestCaseId[] = new String[150];
	private static int noOfSelectedTestCases;
	private JPasswordField jTextFieldPassword;
	private static JTextField jTextFieldUserName;
	private static JTextField jTextFiledSMTPServer;
	private static JTextField jTextSMTPPort;
	private static JTextField jTextReceipentEmailId;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
//	private String s;
	private JButton jButtonExecute;
	private JProgressBar jProgressBarStatus;
	private JCheckBox jCheckBoxEnableServer;
	private JLabel jLabelMultipleEmail;
	private JLabel jLabelReceipentEmail;
	private JLabel jLabelSMTPPort;
	private JPanel jPanelReport;
	private JPanel jPanelLogo;
	BufferedImage image;
	private JButton jButtonExit;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public EmailNotification() {
		initComponents();
	}

	private void initComponents() {
		setTitle("Mind Alliance Automation Framework");
		setLayout(new GroupLayout());
		add(getJPanel1(), new Constraints(new Leading(201, 371, 10, 10), new Leading(12, 100, 12, 12)));
		add(getJProgressBar0(), new Constraints(new Leading(161, 298, 10, 10), new Leading(399, 10, 10)));
		add(getJButtonSave(), new Constraints(new Leading(524, 10, 10), new Leading(393, 10, 10)));
		add(getJButton4(), new Constraints(new Leading(615, 57, 10, 10), new Leading(393, 12, 12)));
		add(getJPanel0(), new Constraints(new Leading(108, 626, 10, 10), new Leading(121, 10, 10)));
		setSize(1356, 698);
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
	
	EmailNotification(BufferedImage image) {
	        this.image = image;
	    }
	
	private JPanel getJPanel1() {
		if (jPanelLogo == null) {
			jPanelLogo = new JPanel();
			jPanelLogo.setLayout(new GroupLayout());
		}
		return jPanelLogo;
	}

	private JPanel getJPanel0() {
		if (jPanelReport == null) {
			jPanelReport = new JPanel();
			jPanelReport.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelReport.setLayout(new GroupLayout());
			jPanelReport.add(getJLabelPassword(), new Constraints(new Leading(28, 30, 130), new Leading(99, 29, 12, 12)));
			jPanelReport.add(getJCheckBoxEnableServer(), new Constraints(new Leading(163, 10, 10), new Leading(170, 8, 8)));
			jPanelReport.add(getJLabelReceiptmentEmail(), new Constraints(new Leading(26, 10, 10), new Leading(144, 12, 12)));
			jPanelReport.add(getJLabelMultipleEmail(), new Constraints(new Leading(125, 10, 10), new Leading(202, 12, 12)));
			jPanelReport.add(getJLabelUsername(), new Constraints(new Leading(30, 30, 130), new Leading(73, 12, 12)));
			jPanelReport.add(getJLabelSMTPServer(), new Constraints(new Leading(26, 30, 130), new Leading(39, 12, 12)));
			jPanelReport.add(getTextSMTPServerName(), new Constraints(new Leading(147, 149, 10, 10), new Leading(41, 12, 12)));
			jPanelReport.add(getTextUserName(), new Constraints(new Leading(147, 148, 12, 12), new Leading(75, 10, 10)));
			jPanelReport.add(getTextPassword(), new Constraints(new Leading(147, 148, 12, 12), new Leading(105, 12, 12)));
			jPanelReport.add(getTextReceipentEmail(), new Constraints(new Leading(149, 245, 12, 12), new Leading(142, 12, 12)));
			jPanelReport.add(getJLabelSMTPPort(), new Constraints(new Leading(421, 10, 10), new Leading(30, 12, 12)));
			jPanelReport.add(getTextSMTPPort(), new Constraints(new Leading(506, 52, 10, 10), new Leading(28, 12, 12)));
		}
		return jPanelReport;
	}

	private JLabel getJLabelSMTPPort() {
		if (jLabelSMTPPort == null) {
			jLabelSMTPPort = new JLabel();
			jLabelSMTPPort.setText("SMTP Port");
		}
		return jLabelSMTPPort;
	}

	private JLabel getJLabelReceiptmentEmail() {
		if (jLabelReceipentEmail == null) {
			jLabelReceipentEmail = new JLabel();
			jLabelReceipentEmail.setText("Receipent Email Id ");
		}
		return jLabelReceipentEmail;
	}

	private JLabel getJLabelMultipleEmail() {
		if (jLabelMultipleEmail == null) {
			jLabelMultipleEmail = new JLabel();
			jLabelMultipleEmail.setText("Multiple Receiptnt should be comma seperated ");
			jLabelMultipleEmail.setForeground(Color.RED);
		}
		return jLabelMultipleEmail;
	}

	private JCheckBox getJCheckBoxEnableServer() {
		if (jCheckBoxEnableServer == null) {
			jCheckBoxEnableServer = new JCheckBox();
			jCheckBoxEnableServer.setText("Enable SMTP Server");
			jCheckBoxEnableServer.addItemListener(this);
		}
		return jCheckBoxEnableServer;
	}

	private JTextField getTextSMTPServerName(){
		if (jTextFiledSMTPServer == null) {
			jTextFiledSMTPServer = new JTextField();
			jTextFiledSMTPServer.setPreferredSize(jTextFiledSMTPServer.getPreferredSize());
			jTextFiledSMTPServer.setText("smtp.gmail.com");
			jTextFiledSMTPServer.addActionListener(this);
		}
		return jTextFiledSMTPServer;
	}

	private JTextField getTextUserName(){
		if (jTextFieldUserName == null) {
			jTextFieldUserName = new JTextField();
			jTextFieldUserName.setPreferredSize(jTextFieldUserName.getPreferredSize());
			jTextFieldUserName.setText("mypriyancagurav");
			jTextFieldUserName.addActionListener(this);
		}
		return jTextFieldUserName;
	}
	
	private JTextField getTextPassword(){
		if (jTextFieldPassword == null) {
			jTextFieldPassword = new JPasswordField();
			jTextFieldPassword.setPreferredSize(jTextFieldPassword.getPreferredSize());
			jTextFieldPassword.setText("waterlemon121");
			jTextFieldPassword.addActionListener(this);
		}
		return jTextFieldPassword;
	}
	
	private JTextField getTextReceipentEmail(){
		if (jTextReceipentEmailId == null) {
			jTextReceipentEmailId = new JTextField();
			jTextReceipentEmailId.setPreferredSize(jTextReceipentEmailId.getPreferredSize());
			jTextReceipentEmailId.setText("mypriyancagurav@gmail.com");
		}
		return jTextReceipentEmailId;
	}
	
	private JTextField getTextSMTPPort(){
		if (jTextSMTPPort == null) {
			jTextSMTPPort = new JTextField();
			jTextSMTPPort.setPreferredSize(jTextSMTPPort.getPreferredSize());
			jTextSMTPPort.setText("465");
		}
		return jTextSMTPPort;
	}
	
	private JProgressBar getJProgressBar0() {
		if (jProgressBarStatus == null) {
			jProgressBarStatus = new JProgressBar();
		}
		return jProgressBarStatus;
	}

	private JButton getJButtonSave() {
		if (jButtonExecute == null) {
			jButtonExecute = new JButton();
			jButtonExecute.setText("Send");
			jButtonExecute.setActionCommand("save");
			jButtonExecute.addActionListener(this);
		}
		return jButtonExecute;
	}

	private JLabel getJLabelPassword() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Password");
		}
		return jLabel2;
	}

	private JLabel getJLabelUsername() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Username");
		}
		return jLabel1;
	}

	private JLabel getJLabelSMTPServer() {
		if (jLabel0 == null) {
			jLabel0 = new JLabel();
			jLabel0.setText("SMTP Server");
		}
		return jLabel0;
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
	
	private static void installLnF() {
		try {
			String lnfClassname = PREFERRED_LOOK_AND_FEEL;
			UIManager.setLookAndFeel(lnfClassname);
		} catch (Exception e) {
			System.err.println("Cannot install " + PREFERRED_LOOK_AND_FEEL
					+ " on this platform:" + e.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub		
		if ("exit".equals(e.getActionCommand())) { // when clicked on 'Exit' button
			System.exit(0);
		}
		if("save".equals(e.getActionCommand())){
			String[] to={"mypriyancagurav@gmail.com"};
			String[] cc={"priyanka.gurav@afourtech.com"};
			//This is for google
			EmailNotification.sendMail(jTextFieldUserName.getText(),jTextFieldPassword.getText(),jTextFiledSMTPServer.getText(),jTextSMTPPort.getText(),"true","true",true,"javax.net.ssl.SSLSocketFactory","false",to,cc,"Mind-Alliance UI Automation Report");
		}
	}
		
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public synchronized static boolean sendMail(String userName,String passWord,String host,String port,String starttls,String auth,boolean debug,String socketFactoryClass,String fallback,String[] to,String[] cc,String subject){
        Properties props = new Properties();
        props.put("mail.smtp.user", jTextFieldUserName.getText());
        props.put("mail.smtp.host", jTextFiledSMTPServer.getText());
        if(!"".equals(port))
        	props.put("mail.smtp.port", jTextSMTPPort.getText());
        if(!"".equals(starttls))
        	props.put("mail.smtp.starttls.enable",starttls);
        props.put("mail.smtp.auth", auth);
        if(debug){
        	props.put("mail.smtp.debug", "true");
        }else{
        	props.put("mail.smtp.debug", "false");         
        }
        if(!"".equals(jTextSMTPPort.getText()))
        	props.put("mail.smtp.socketFactory.port", jTextSMTPPort.getText());
        if(!"".equals(socketFactoryClass))
        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
        if(!"".equals(fallback))
        	props.put("mail.smtp.socketFactory.fallback", fallback);

        try
        {
        	Session session = Session.getDefaultInstance(props, null);
        	session.setDebug(debug);
        	MimeMessage msg = new MimeMessage(session);
            msg.setText("Mind Alliance UIAutomation Report");
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress("mypriyancagurav@gmail.com"));
            for(int i=0;i<to.length;i++){
            	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }
            for(int i=0;i<cc.length;i++){
            	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(cc[i]));
            }
            msg.saveChanges();
            Transport transport = session.getTransport("smtp");
            transport.connect(host, userName, passWord);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            return true;
        }
        catch (Exception mex)
        {
        	mex.printStackTrace();
        	return false;
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
				EmailNotification frame = new EmailNotification();
				frame.setDefaultCloseOperation(EmailNotification.EXIT_ON_CLOSE);
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

