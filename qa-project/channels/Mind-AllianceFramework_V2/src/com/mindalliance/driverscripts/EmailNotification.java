package com.mindalliance.driverscripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import com.mindalliance.configuration.GlobalVariables;
//import java.awt.Rectangle;

//VS4E -- DO NOT REMOVE THIS LINE!
public class EmailNotification extends JFrame implements ActionListener, ItemListener{
	private static final long serialVersionUID = 1L;
//	private static int noOfSelectedTestCases;
	private JPasswordField jTextFieldPassword;
	private static JTextField jTextFieldUserName;
	private static JTextField jTextFiledSMTPServer;
	private static JTextField jTextSMTPPort;
	private static JFormattedTextField jTextReceipentEmailId;
	private JLabel jLabel0;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JButton jButtonExecute;
	//	private JCheckBox jCheckBoxEnableServer;
	private JLabel jLabelMultipleEmail;
	private JLabel jLabelReceipentEmail;
	private JLabel jLabelSMTPPort;
	private JPanel jPanelReport;
	private JPanel jPanelLogo;
	BufferedImage image;
	private JButton jButtonExit;
	public static String reportDstDirectoryPath;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public EmailNotification() {
		installLnF();
		initComponents();
		setDefaultCloseOperation(EmailNotification.EXIT_ON_CLOSE);
		setTitle("Mind-Alliance Automation Framework");
		//frame.getContentPane().setPreferredSize(frame.getSize());
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private void initComponents() {
		setTitle("Mind-Alliance Automation Framework");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setForeground(Color.black);
		setLayout(new GroupLayout());
		add(getJPanel1(), new Constraints(new Leading(201, 371, 10, 10), new Leading(12, 100, 12, 12)));
		add(getJButtonSave(), new Constraints(new Leading(524, 10, 10), new Leading(393, 10, 10)));
		add(getJButton4(), new Constraints(new Leading(615, 57, 10, 10), new Leading(393, 12, 12)));
		add(getJPanel0(), new Constraints(new Leading(108, 626, 10, 10), new Leading(121, 233, 10, 10)));
		setSize(1366, 720);
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
			jPanelReport.add(getJLabelSMTPServer(), new Constraints(new Leading(26, 30, 130), new Leading(39, 12, 12)));
			jPanelReport.add(getTextSMTPServerName(), new Constraints(new Leading(147, 149, 10, 10), new Leading(41, 12, 12)));
			jPanelReport.add(getTextUserName(), new Constraints(new Leading(147, 148, 12, 12), new Leading(75, 10, 10)));
			jPanelReport.add(getJLabelMultipleEmail(), new Constraints(new Leading(130, 10, 10), new Leading(184, 10, 10)));
			jPanelReport.add(getTextReceipentEmail(), new Constraints(new Leading(147, 245, 12, 12), new Leading(143, 12, 12)));
			jPanelReport.add(getJLabelReceiptmentEmail(), new Constraints(new Leading(28, 12, 12), new Leading(143, 12, 12)));
			jPanelReport.add(getJLabelUsername(), new Constraints(new Leading(26, 12, 12), new Leading(77, 12, 12)));
			jPanelReport.add(getTextPassword(), new Constraints(new Leading(148, 148, 12, 12), new Leading(111, 12, 12)));
			jPanelReport.add(getJLabelPassword(), new Constraints(new Leading(28, 12, 12), new Leading(105, 29, 12, 12)));
			jPanelReport.add(getJLabelSMTPPort(), new Constraints(new Leading(419, 10, 10), new Leading(41, 12, 12)));
			jPanelReport.add(getTextSMTPPort(), new Constraints(new Leading(507, 52, 10, 10), new Leading(39, 12, 12)));
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

//	private JCheckBox getJCheckBoxEnableServer() {
//		if (jCheckBoxEnableServer == null) {
//			jCheckBoxEnableServer = new JCheckBox();
//			jCheckBoxEnableServer.setText("Enable SMTP Server");
//			jCheckBoxEnableServer.addItemListener(this);
//		}
//		return jCheckBoxEnableServer;
//	}

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
			jTextFieldUserName.setText("piugurav0069@gmail.com");
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
			jTextReceipentEmailId = new JFormattedTextField();
			jTextReceipentEmailId.setPreferredSize(jTextReceipentEmailId.getPreferredSize());
			jTextReceipentEmailId.setText("priyanka.gurav@afourtech.com");
		}
		return jTextReceipentEmailId;
	}
	
	private JTextField getTextSMTPPort(){
		if (jTextSMTPPort == null) {
			jTextSMTPPort = new JTextField();
			jTextSMTPPort.setPreferredSize(jTextSMTPPort.getPreferredSize());
			jTextSMTPPort.setText("587");
			jTextSMTPPort.addActionListener(this);
		}
		return jTextSMTPPort;
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

//	public void updateProgressBar(int percent) {
//		jProgressBarStatus.setValue(percent);
//		jProgressBarStatus.setString("Completed: " + Integer.toString(percent) + "/" + noOfSelectedTestCases);
//		jProgressBarStatus.setStringPainted(true);
//		Rectangle progressRect = jProgressBarStatus.getBounds();
//		progressRect.x = 0;
//		progressRect.y = 0;
//		jProgressBarStatus.paintImmediately(progressRect);
//	}
	
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
		if ("exit".equals(e.getActionCommand())) { 
			// when clicked on 'Exit' button
			System.exit(0);
		}
		if("save".equals(e.getActionCommand())){
			String[] to={"piugurav0069@gmail.com"};
			String[] cc={};
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
//        if(!"".equals(socketFactoryClass))
//        	props.put("mail.smtp.socketFactory.class",socketFactoryClass);
        if(!"".equals(fallback))
        	props.put("mail.smtp.socketFactory.fallback", fallback);

        try
        {
        	Session session = Session.getDefaultInstance(props, null);
        	session.setDebug(debug);
        	
        	MimeMessage msg = new MimeMessage(session);
            msg.setSubject(subject);
            msg.setFrom(new InternetAddress("priyanka.gurav@afourtech.com"));
            for(int i=0;i<to.length;i++){
            	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }
            for(int i=0;i<jTextReceipentEmailId.getText().length();){
            	msg.addRecipient(Message.RecipientType.CC, new InternetAddress(jTextReceipentEmailId.getText()));
            	break;
            }
            
            MimeBodyPart messageBodyPart = new MimeBodyPart();

    	    //fill message
    	    messageBodyPart.setText("Hi,");
    	    messageBodyPart.setText("This email is autogenerated. Please find the attached file of Report herewith");
    	    messageBodyPart.setText("Thanks & Regards,");
    	    messageBodyPart.setText("Automation Team");

    	    Multipart multipart = new MimeMultipart();
    	    multipart.addBodyPart(messageBodyPart);

    	    // Part two is attachment
    	    messageBodyPart = new MimeBodyPart();
    	    DataSource source = new FileDataSource(GlobalVariables.configuration.getCurrentDir().getCanonicalPath()+ "\\Reports\\UIAutomationReport.zip");
    	    messageBodyPart.setDataHandler(new DataHandler(source));
    	    messageBodyPart.setFileName("Mind-Alliance Report"+GlobalVariables.configuration.getCurrentDate().toString());
    	    multipart.addBodyPart(messageBodyPart);

    	    // Put parts in message
    	    msg.setContent(multipart);
            	    
            msg.saveChanges();
            
//            // create the second message part
//            MimeBodyPart mbp2 = new MimeBodyPart();
//            // attach the file to the message
//            FileDataSource fds = new FileDataSource("D:\\Channels\\Mind-AllianceFramework_V2\\Reports\\UIAutomationReport" + "\\index.htm");  
//            mbp2.setDataHandler(new DataHandler(fds));
//            // create the Multipart and add its parts to it
//            Multipart mp = new MimeMultipart();
//            mp.addBodyPart(mbp2);
//            // set the Date: header
//            msg.setSentDate(new Date());            
//            // add the Multipart to the message
//            msg.setContent(mp,"text/html; charset=utf-8");
            
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
	}
}

