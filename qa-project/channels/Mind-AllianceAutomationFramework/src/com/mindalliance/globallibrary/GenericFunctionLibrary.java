package com.mindalliance.globallibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenericFunctionLibrary {

	/**
	 * Create Log and Result Structures
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
//	public static BufferedReader br=null;
	public static StringTokenizer st=null;
	public static void createResultFiles() {
		try{
			// Get Current Date
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			GlobalVariables.dCurrentDate = new Date();
			// Create Report Directory
			GlobalVariables.sReportDirectoryName = dateFormat.format(GlobalVariables.dCurrentDate);
			GlobalVariables.sReportSrcDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods";
			GlobalVariables.sReportDstDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Reports\\" + GlobalVariables.sReportDirectoryName;
			File Dir = new File(GlobalVariables.sReportDstDirectoryPath);
			if (!Dir.exists())
				Dir.mkdir();
			// Create Log Directory
			GlobalVariables.sLogDirectoryName = dateFormat.format(GlobalVariables.dCurrentDate);
			GlobalVariables.sLogDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Logs\\" + GlobalVariables.sLogDirectoryName;
			Dir = new File(GlobalVariables.sLogDirectoryPath); 
			if (!Dir.exists())
				Dir.mkdir();
			// Create Errors sub-directory
			GlobalVariables.sErrorLogSubDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Logs\\" + GlobalVariables.sLogDirectoryName + "\\Errors";
			Dir = new File(GlobalVariables.sErrorLogSubDirectoryPath);
			if (!Dir.exists())
				Dir.mkdir();
			// Logs Files
			GlobalVariables.sResultCsvFile = GlobalVariables.sLogDirectoryPath + "\\Results.csv";
			GlobalVariables.sLogFile = GlobalVariables.sLogDirectoryPath + "\\Logs.logs";
			FileWriter fileWriter = new FileWriter(GlobalVariables.sResultCsvFile, true);
			BufferedWriter oBWriter = new BufferedWriter(fileWriter);
			oBWriter.write("TestCaseId,VerificationStepNo,Description,Result,ScriptException,ErrorReport");
			oBWriter.newLine();
			oBWriter.flush();
			oBWriter.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	/**
	 * Load Object Repository
	 */
	public static void loadObjectRepository() {
		try {
			GlobalVariables.sObjectRepositoryDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\ObjectRepository\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

			File ChannelsAdmin=new File(GlobalVariables.sObjectRepositoryDirectoryPath + "ChannelsAdministartion.xml");
			File HomePage=new File(GlobalVariables.sObjectRepositoryDirectoryPath+"HomePage.xml");
			File LoginPage=new File(GlobalVariables.sObjectRepositoryDirectoryPath+"LoginPage.xml");
			File PlanPage=new File(GlobalVariables.sObjectRepositoryDirectoryPath+"PlanPage.xml");

			GlobalVariables.sObjectRepositoryDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestData\\";
			File viewElements=new File(GlobalVariables.sObjectRepositoryDirectoryPath+"ViewElements.xml");

			Document docChannelsAdmin=db.parse(ChannelsAdmin);
			Document docHomePage=db.parse(HomePage);
			Document docLoginPage=db.parse(LoginPage);
			Document docPlanPage=db.parse(PlanPage);
			Document docViewElements=db.parse(viewElements);

			/*NodeList elementsList = docEle.getElementsByTagName("elements");
            Node node = elementsList.item(0);*/
            Element eleChannelsAdmin=docChannelsAdmin.getDocumentElement();
            Element eleHomePage=docHomePage.getDocumentElement();
            Element eleLoginPage=docLoginPage.getDocumentElement();
            Element elePlanPage=docPlanPage.getDocumentElement();
            Element eleViewElement=docViewElements.getDocumentElement();

            Element oXmlEleChannelsAdmin = (Element) eleChannelsAdmin;
            Element oXmlEleHomePage = (Element) eleHomePage;
            Element oXmlEleLoginPage = (Element) eleLoginPage;
            Element oXmlElePlanPage = (Element) elePlanPage;
            Element oXmlEleViewElements = (Element) eleViewElement;

            // Login Page
            GlobalVariables.login.put("sChannelURL",oXmlEleLoginPage.getElementsByTagName("channelURL").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sUsername",oXmlEleLoginPage.getElementsByTagName("username").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sPassword",oXmlEleLoginPage.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sLogin",oXmlEleLoginPage.getElementsByTagName("login").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathBody",oXmlEleLoginPage.getElementsByTagName("xPathBody").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sSignoutOnParticipantsPage",oXmlEleLoginPage.getElementsByTagName("xPathSignOutOnParticipantsPage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathStackTrace",oXmlEleLoginPage.getElementsByTagName("xPathStackTrace").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathSignoutOnAssignmentsCommitments",oXmlEleLoginPage.getElementsByTagName("xPathSignoutOnAssignmentsCommitments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathForgotUsernamePassword",oXmlEleLoginPage.getElementsByTagName("xPathForgotUsernamePassword").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathRequestNewPassword",oXmlEleLoginPage.getElementsByTagName("xPathRequestNewPassword").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathRequestPasswordMessage",oXmlEleLoginPage.getElementsByTagName("xPathRequestPasswordMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.login.put("sXpathBackToLogin",oXmlEleLoginPage.getElementsByTagName("xPathBackToLogin").item(0).getChildNodes().item(0).getNodeValue());
			// Channels Admin
			GlobalVariables.channelsAdmin.put("sXpathLogoutAdminPage",oXmlEleChannelsAdmin.getElementsByTagName("xPathLogoutAdminPage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathSamePlanErrorMessage",oXmlEleChannelsAdmin.getElementsByTagName("xPathSamePlanErrorMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathInputUserID",oXmlEleChannelsAdmin.getElementsByTagName("xPathInputUserID").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathUserID",oXmlEleChannelsAdmin.getElementsByTagName("xPathUserID").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathDeleteUser",oXmlEleChannelsAdmin.getElementsByTagName("xPathDeleteUser").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathAccessPrivilegeAdmin",oXmlEleChannelsAdmin.getElementsByTagName("xPathAccessPrivilegeAdmin").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathUserPassword",oXmlEleChannelsAdmin.getElementsByTagName("xPathUserPassword").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathDisableUser",oXmlEleChannelsAdmin.getElementsByTagName("xPathDisableUser").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathAccessPrivilegePlanner",oXmlEleChannelsAdmin.getElementsByTagName("xPathAccessPrivilegePlanner").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathThisPlanAccessPrivilegePlanner",oXmlEleChannelsAdmin.getElementsByTagName("xPathThisPlanAccessPrivilegePlanner").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathProductize",oXmlEleChannelsAdmin.getElementsByTagName("xPathProductize").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.channelsAdmin.put("sXpathDeletePlan",oXmlEleChannelsAdmin.getElementsByTagName("xPathDeletePlan").item(0).getChildNodes().item(0).getNodeValue());
			// Channels Home Page
			GlobalVariables.home.put("sXpathLogoutHomePage",oXmlEleHomePage.getElementsByTagName("xPathLogoutHomePage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSocialAboutMe",oXmlEleHomePage.getElementsByTagName("xPathSocialAboutMe").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSocialCalendar",oXmlEleHomePage.getElementsByTagName("xPathSocialCalendar").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSocialMessages",oXmlEleHomePage.getElementsByTagName("xPathSocialMessages").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSocialSurvey",oXmlEleHomePage.getElementsByTagName("xPathSocialSurvey").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathHomePageIcon",oXmlEleHomePage.getElementsByTagName("xPathHomePageIcon").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSentMessages",oXmlEleHomePage.getElementsByTagName("xPathSentMessages").item(0).getChildNodes().item(0).getNodeValue());	
			GlobalVariables.home.put("sXpathHideBroadcast",oXmlEleHomePage.getElementsByTagName("xPathHideBroadcast").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSendFeedBack",oXmlEleHomePage.getElementsByTagName("xPathSendFeedBack").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathSendFeedbackWindow",oXmlEleHomePage.getElementsByTagName("xPathSendFeedbackWindow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathHelpIcon",oXmlEleHomePage.getElementsByTagName("xPathHelpIcon").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.home.put("sXpathShowSentReceiveMessages",oXmlEleHomePage.getElementsByTagName("xPathShowSentReceiveMessages").item(0).getChildNodes().item(0).getNodeValue());
			//Channels Plan Page
			GlobalVariables.plan.put("sXpathShowPopUpMenu",oXmlElePlanPage.getElementsByTagName("xPathShowPopUpMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAllSegmentShowMenu",oXmlElePlanPage.getElementsByTagName("xPathAllSegmentShowMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionsPopUpMenu",oXmlElePlanPage.getElementsByTagName("xPathActionsPopUpMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowMenu",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegShowMenu",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegShowMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathTaskMoverDetail",oXmlElePlanPage.getElementsByTagName("xPathTaskMoverDetail").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDefaultTask",oXmlElePlanPage.getElementsByTagName("xPathDefaultTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathTaskShowMenu",oXmlElePlanPage.getElementsByTagName("xPathTaskShowMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathTaskActionsMenu",oXmlElePlanPage.getElementsByTagName("xPathTaskActionsMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAddInfoReceive",oXmlElePlanPage.getElementsByTagName("xPathAddInfoReceive").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAddInfoSend",oXmlElePlanPage.getElementsByTagName("xPathAddInfoSend").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathReceiveFlowMoreMenu",oXmlElePlanPage.getElementsByTagName("xPathReceiveFlowMoreMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathSendFlowMoreMenu",oXmlElePlanPage.getElementsByTagName("xPathSendFlowMoreMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathIssueMoreMenu",oXmlElePlanPage.getElementsByTagName("xPathIssueMoreMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathPresenceTabs",oXmlElePlanPage.getElementsByTagName("xPathPresenceTabs").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathMessages",oXmlElePlanPage.getElementsByTagName("xPathMessages").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActivities",oXmlElePlanPage.getElementsByTagName("xPathActivities").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDoingSomeThingLink",oXmlElePlanPage.getElementsByTagName("xPathDoingSomeThingLink").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathLegend",oXmlElePlanPage.getElementsByTagName("xPathLegend").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathRemoveIssue",oXmlElePlanPage.getElementsByTagName("xPathRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathRespondingPhaseLink",oXmlElePlanPage.getElementsByTagName("xPathRespondingPhaseLink").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteEvent",oXmlElePlanPage.getElementsByTagName("xPathDeleteEvent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathMoveTaskButton",oXmlElePlanPage.getElementsByTagName("xPathMoveTaskButton").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteGoals",oXmlElePlanPage.getElementsByTagName("xPathDeleteGoals").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteOrgs",oXmlElePlanPage.getElementsByTagName("xPathDeleteOrgs").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeletePhase",oXmlElePlanPage.getElementsByTagName("xPathDeletePhase").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeletePlanSegAttachment",oXmlElePlanPage.getElementsByTagName("xPathDeletePlanSegAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteTaskAttachment",oXmlElePlanPage.getElementsByTagName("xPathDeleteTaskAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanAction",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanAction").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathCloseSurveyWindow",oXmlElePlanPage.getElementsByTagName("xPathCloseSurveyWindow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathCutAttachment",oXmlElePlanPage.getElementsByTagName("xPathCutAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAllUsers",oXmlElePlanPage.getElementsByTagName("xPathShowAllUsers").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathPhaseShowMenu",oXmlElePlanPage.getElementsByTagName("xPathPhaseShowMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathIndexAgent",oXmlElePlanPage.getElementsByTagName("xPathIndexAgent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathEvent",oXmlElePlanPage.getElementsByTagName("xPathEvent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathOrganizationJob",oXmlElePlanPage.getElementsByTagName("xPathOrganizationJob").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathOrganizationJobDetails",oXmlElePlanPage.getElementsByTagName("xPathOrganizationJobDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathOrganizationChart",oXmlElePlanPage.getElementsByTagName("xPathOrganizationChart").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathOrganizationAgreement",oXmlElePlanPage.getElementsByTagName("xPathOrganizationAgreement").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanLocale",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanLocale").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanLocaleLink",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanLocaleLink").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathStretchUpShrinkBack",oXmlElePlanPage.getElementsByTagName("xPathStretchUpShrinkBack").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDoingSomeThing",oXmlElePlanPage.getElementsByTagName("xPathDoingSomeThing").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathPlanURIName",oXmlElePlanPage.getElementsByTagName("xPathPlanURIName").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathCollebrationPanelResstMessage",oXmlElePlanPage.getElementsByTagName("xPathCollebrationPanelResstMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowHidePlanners",oXmlElePlanPage.getElementsByTagName("xPathShowHidePlanners").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAboutPlan",oXmlElePlanPage.getElementsByTagName("xPathShowAboutPlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAboutPlanSegment",oXmlElePlanPage.getElementsByTagName("xPathShowAboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowSurveys",oXmlElePlanPage.getElementsByTagName("xPathShowSurveys").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAllSegments",oXmlElePlanPage.getElementsByTagName("xPathShowAllSegments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAllIssues",oXmlElePlanPage.getElementsByTagName("xPathShowAllIssues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowAllTypes",oXmlElePlanPage.getElementsByTagName("xPathShowAllTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowIndex",oXmlElePlanPage.getElementsByTagName("xPathShowIndex").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathShowHelp",oXmlElePlanPage.getElementsByTagName("xPathShowHelp").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionSendMessage",oXmlElePlanPage.getElementsByTagName("xPathActionSendMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionAddNewTask",oXmlElePlanPage.getElementsByTagName("xPathActionAddNewTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionAddNewIssue",oXmlElePlanPage.getElementsByTagName("xPathActionAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionAddNewSegment",oXmlElePlanPage.getElementsByTagName("xPathActionAddNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionRemoveSegment",oXmlElePlanPage.getElementsByTagName("xPathActionRemoveSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionMoveTaskToSegment",oXmlElePlanPage.getElementsByTagName("xPathActionMoveTaskToSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActionSignOut",oXmlElePlanPage.getElementsByTagName("xPathActionSignOut").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathHelpIcon",oXmlElePlanPage.getElementsByTagName("xPathHelpIcon").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathTagsLinkOnTask",oXmlElePlanPage.getElementsByTagName("xPathTagsLinkOnTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanSegmentLink",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanSegmentLink").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowDetails",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllEvents",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllEvents").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowSecrecyClassification",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowSecrecyClassification").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllOrganizations",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllSegments",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllSegments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowProcedureMap",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowProcedureMap").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowWhosWho",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowWhosWho").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllIssues",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllIssues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowBibliography",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowBibliography").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowIndex",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowIndex").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllTypes",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowAllTags",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowAllTags").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowEvaluation",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowEvaluation").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowParticipations",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowParticipations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanShowVersions",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanShowVersions").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanActionMenu",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanActionMenu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanActionSendMessage",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanActionSendMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAboutPlanActionAddNewIssue",oXmlElePlanPage.getElementsByTagName("xPathAboutPlanActionAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegDetails",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegGoals",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegGoals").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegOrganizations",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegMap",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegMap").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegTaskMover",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegTaskMover").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegActionSendMessage",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegActionSendMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAbtPlanSegActionAddNewIssue",oXmlElePlanPage.getElementsByTagName("xPathAbtPlanSegActionAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathNewOrganization",oXmlElePlanPage.getElementsByTagName("xPathNewOrganization").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathSurvey",oXmlElePlanPage.getElementsByTagName("xPathSurvey").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentContacts",oXmlElePlanPage.getElementsByTagName("xPathAgentContacts").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgent",oXmlElePlanPage.getElementsByTagName("xPathAgent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentCategory",oXmlElePlanPage.getElementsByTagName("xPathAgentCategory").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteAgentCategory",oXmlElePlanPage.getElementsByTagName("xPathDeleteAgentCategory").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentViewAllTypes",oXmlElePlanPage.getElementsByTagName("xPathAgentViewAllTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathURL",oXmlElePlanPage.getElementsByTagName("xPathURL").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteAgentURL",oXmlElePlanPage.getElementsByTagName("xPathDeleteAgentURL").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentMore",oXmlElePlanPage.getElementsByTagName("xPathAgentMore").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathLocale",oXmlElePlanPage.getElementsByTagName("xPathLocale").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathPlanHomeIcon",oXmlElePlanPage.getElementsByTagName("xPathPlanHomeIcon").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAddInfoSendNewMedium",oXmlElePlanPage.getElementsByTagName("xPathAddInfoSendNewMedium").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathParticipationNewMedium",oXmlElePlanPage.getElementsByTagName("xPathParticipationNewMedium").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentDetails",oXmlElePlanPage.getElementsByTagName("xPathAgentDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathAgentName",oXmlElePlanPage.getElementsByTagName("xPathAgentName").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathIssueSummaryReportToHome",oXmlElePlanPage.getElementsByTagName("xPathIssueSummaryReportToHome").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathViewAllTypesOfAddInfoSendNewMedium",oXmlElePlanPage.getElementsByTagName("xPathViewAllTypesOfAddInfoSendNewMedium").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathEventTagsLink",oXmlElePlanPage.getElementsByTagName("xPathEventTagsLink").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteCategoriesOfEvent",oXmlElePlanPage.getElementsByTagName("xPathDeleteCategoriesOfEvent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathInformationSharingGuidelinesForParticipantsToHome",oXmlElePlanPage.getElementsByTagName("xPathInformationSharingGuidelinesForParticipantsToHome").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathActiveUsers",oXmlElePlanPage.getElementsByTagName("xPathActiveUsers").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathParticipantsPageSubTitle",oXmlElePlanPage.getElementsByTagName("xPathParticipantsPageSubTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathOperationalNonOperational",oXmlElePlanPage.getElementsByTagName("xPathOperationalNonOperational").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeleteEventAttachment",oXmlElePlanPage.getElementsByTagName("xPathDeleteEventAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathCopyEventAttachment",oXmlElePlanPage.getElementsByTagName("xPathCopyEventAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathCrossOnAttachmentCopiedMessage",oXmlElePlanPage.getElementsByTagName("xPathCrossOnAttachmentCopiedMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeletePhaseCategories",oXmlElePlanPage.getElementsByTagName("xPathDeletePhaseCategories").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.plan.put("sXpathDeletePhaseAttachment",oXmlElePlanPage.getElementsByTagName("xPathDeletePhaseAttachment").item(0).getChildNodes().item(0).getNodeValue());
			// Assertions
			GlobalVariables.assertion.put("sXpathPhaseAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathPhaseAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathPhaseCategoriesAssertion", oXmlElePlanPage.getElementsByTagName("xPathPhaseCategoriesAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathDeletePhaseAssertion", oXmlElePlanPage.getElementsByTagName("xPathDeletePhaseAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathPhaseCreatedAssertion", oXmlElePlanPage.getElementsByTagName("xPathPhaseCreatedAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathMediumDeleteAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathMediumDeleteAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathDeleteEventAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathDeleteEventAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXathCopyEventAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathCopyEventAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathEventUrlAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathEventUrlAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionPhaseDetails", oXmlElePlanPage.getElementsByTagName("xPathAssertionPhaseDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionDetails",oXmlElePlanPage.getElementsByTagName("xPathAssertionDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathPhaseAssertionDetails",oXmlElePlanPage.getElementsByTagName("xPathPhaseAssertionDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionEventWindow",oXmlElePlanPage.getElementsByTagName("xPathAssertionEventWindow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionOrg",oXmlElePlanPage.getElementsByTagName("xPathAssertionAboutOrg").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionBibliography",oXmlElePlanPage.getElementsByTagName("xPathAssertionBibliography").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionTags",oXmlElePlanPage.getElementsByTagName("xPathAssertionTags").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionTypes",oXmlElePlanPage.getElementsByTagName("xPathAssertionTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionAllSegments",oXmlElePlanPage.getElementsByTagName("xPathAssertionAllSegments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionIndex",oXmlElePlanPage.getElementsByTagName("xPathAssertionIndex").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionIssues",oXmlElePlanPage.getElementsByTagName("xPathAssertionIssues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionParticipations",oXmlElePlanPage.getElementsByTagName("xPathAssertionParticipations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionProcedureMap",oXmlElePlanPage.getElementsByTagName("xPathAssertionProcedureMap").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionWhowho",oXmlElePlanPage.getElementsByTagName("xPathAssertionWhowho").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionVersions",oXmlElePlanPage.getElementsByTagName("xPathAssertionVersions").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionSecrecyclassifications",oXmlElePlanPage.getElementsByTagName("xPathAssertionSecrecyclassifications").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionEvaluation",oXmlElePlanPage.getElementsByTagName("xPathAssertionEvaluation").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionAboutPlanSegmentDetails",oXmlElePlanPage.getElementsByTagName("xPathAssertionAboutPlanSegmentDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionGoals",oXmlElePlanPage.getElementsByTagName("xPathAssertionGoals").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionSegmentOrg",oXmlElePlanPage.getElementsByTagName("xPathAssertionSegmentOrg").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionSegmentMap",oXmlElePlanPage.getElementsByTagName("xPathAssertionSegmentMap").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionMessageTab",oXmlElePlanPage.getElementsByTagName("xPathAssertionMessageTab").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionAssignment",oXmlElePlanPage.getElementsByTagName("xPathAssertionAssignment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionCloseAssignment",oXmlElePlanPage.getElementsByTagName("xPathAssertionCloseAssignment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionCloseFailureImpact",oXmlElePlanPage.getElementsByTagName("xPathAssertionCloseFailureImpact").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionCloseDissemination",oXmlElePlanPage.getElementsByTagName("xPathAssertionCloseDissemination").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionDissemination",oXmlElePlanPage.getElementsByTagName("xPathAssertionDissemination").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAssertionCloseAboutPlanSegment",oXmlElePlanPage.getElementsByTagName("xPathAssertionCloseAboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathGoBack",oXmlElePlanPage.getElementsByTagName("xPathGoBack").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathGoForward",oXmlElePlanPage.getElementsByTagName("xPathGoForward").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathElementsOfInformation",oXmlElePlanPage.getElementsByTagName("xPathElementsOfInformation").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathIssueReport",oXmlElePlanPage.getElementsByTagName("xPathIssueReport").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathIssueReportHome",oXmlElePlanPage.getElementsByTagName("xPathIssueReportHome").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathLegendLabel",oXmlElePlanPage.getElementsByTagName("xPathLegendLabel").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathReceivePanel",oXmlElePlanPage.getElementsByTagName("xPathReceivePanel").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathSendPanel",oXmlElePlanPage.getElementsByTagName("xPathSendPanel").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAddInfoReceiveAssertion",oXmlElePlanPage.getElementsByTagName("xPathAddInfoReceiveAssertion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.assertion.put("sXpathAddInfoSendAssertion",oXmlElePlanPage.getElementsByTagName("xPathAddInfoSendAssertion").item(0).getChildNodes().item(0).getNodeValue());	
			GlobalVariables.assertion.put("sXpathEventAttachmentAssertion", oXmlElePlanPage.getElementsByTagName("xPathEventAttachmentAssertion").item(0).getChildNodes().item(0).getNodeValue());
			// View Elements
			GlobalVariables.viewElements.put("notOperational",oXmlEleViewElements.getElementsByTagName("notOperational").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("adminPageTitle",oXmlEleViewElements.getElementsByTagName("adminPageTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("homePageTitle",oXmlEleViewElements.getElementsByTagName("homePageTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("loginPageTitle",oXmlEleViewElements.getElementsByTagName("loginPageTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("planPageSubTitle",oXmlEleViewElements.getElementsByTagName("planPageSubTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("aboutPlanPageSubTitle",oXmlEleViewElements.getElementsByTagName("aboutPlanPageSubTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("channelsAdministration",oXmlEleViewElements.getElementsByTagName("channelsAdministration").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("informationSharingModel",oXmlEleViewElements.getElementsByTagName("informationSharingModel").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("details",oXmlEleViewElements.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("Details",oXmlEleViewElements.getElementsByTagName("Details").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("untitled",oXmlEleViewElements.getElementsByTagName("untitled").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allEventsInPlan",oXmlEleViewElements.getElementsByTagName("allEventsInPlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allInterSegmentFlows",oXmlEleViewElements.getElementsByTagName("allInterSegmentFlows").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("documentsAttached",oXmlEleViewElements.getElementsByTagName("documentsAttached").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allKnownTags",oXmlEleViewElements.getElementsByTagName("allKnownTags").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("classificationSystems",oXmlEleViewElements.getElementsByTagName("classificationSystems").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("organizationsInPlan",oXmlEleViewElements.getElementsByTagName("organizationsInPlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hidePlanners",oXmlEleViewElements.getElementsByTagName("hidePlanners").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("aboutPlan",oXmlEleViewElements.getElementsByTagName("aboutPlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allEvents",oXmlEleViewElements.getElementsByTagName("allEvents").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("secrecyClassification",oXmlEleViewElements.getElementsByTagName("secrecyClassification").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allOrganizations",oXmlEleViewElements.getElementsByTagName("allOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allTypes",oXmlEleViewElements.getElementsByTagName("allTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allSegments",oXmlEleViewElements.getElementsByTagName("allSegments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("procedureMap",oXmlEleViewElements.getElementsByTagName("procedureMap").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("whosWho",oXmlEleViewElements.getElementsByTagName("whosWho").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allIssues",oXmlEleViewElements.getElementsByTagName("allIssues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("bibliography",oXmlEleViewElements.getElementsByTagName("bibliography").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("index",oXmlEleViewElements.getElementsByTagName("index").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allTags",oXmlEleViewElements.getElementsByTagName("allTags").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("evaluation",oXmlEleViewElements.getElementsByTagName("evaluation").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("participation",oXmlEleViewElements.getElementsByTagName("participation").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("versions",oXmlEleViewElements.getElementsByTagName("versions").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("actions",oXmlEleViewElements.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("aboutPlanSegment",oXmlEleViewElements.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("goals",oXmlEleViewElements.getElementsByTagName("goals").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("goalsForThisSegment",oXmlEleViewElements.getElementsByTagName("goalsForThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("organizations",oXmlEleViewElements.getElementsByTagName("organizations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("organizationsInThisSegment",oXmlEleViewElements.getElementsByTagName("organizationsInThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("map",oXmlEleViewElements.getElementsByTagName("map").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("taskMover",oXmlEleViewElements.getElementsByTagName("taskMover").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("moveTaskToAnotherSegment",oXmlEleViewElements.getElementsByTagName("moveTaskToAnotherSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("surveys",oXmlEleViewElements.getElementsByTagName("surveys").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allEventsInPlan",oXmlEleViewElements.getElementsByTagName("allEventsInPlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("help",oXmlEleViewElements.getElementsByTagName("help").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addNewIssue",oXmlEleViewElements.getElementsByTagName("addNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddNewIssue",oXmlEleViewElements.getElementsByTagName("undoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddNewIssue",oXmlEleViewElements.getElementsByTagName("redoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("sendMessage",oXmlEleViewElements.getElementsByTagName("sendMessage").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("to",oXmlEleViewElements.getElementsByTagName("to").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addNewTask",oXmlEleViewElements.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hideDetails",oXmlEleViewElements.getElementsByTagName("hideDetails").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("cutTask",oXmlEleViewElements.getElementsByTagName("cutTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("pasteTask",oXmlEleViewElements.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("moveTasksToSegment",oXmlEleViewElements.getElementsByTagName("moveTasksToSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("task1234",oXmlEleViewElements.getElementsByTagName("task1234").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("pasteAttachment",oXmlEleViewElements.getElementsByTagName("pasteAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("file1",oXmlEleViewElements.getElementsByTagName("file1").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("task",oXmlEleViewElements.getElementsByTagName("task").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("assignments",oXmlEleViewElements.getElementsByTagName("assignments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("taskAssignments",oXmlEleViewElements.getElementsByTagName("taskAssignments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("failureImpacts",oXmlEleViewElements.getElementsByTagName("failureImpacts").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("taskFailureImpacts",oXmlEleViewElements.getElementsByTagName("taskFailureImpacts").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("dissemination",oXmlEleViewElements.getElementsByTagName("dissemination").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("infoDisseminationForTask",oXmlEleViewElements.getElementsByTagName("infoDisseminationForTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addNewSegment",oXmlEleViewElements.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("removeThisSegment",oXmlEleViewElements.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showAllUsers",oXmlEleViewElements.getElementsByTagName("showAllUsers").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hideInactiveUsers",oXmlEleViewElements.getElementsByTagName("hideInactiveUsers").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hideMyActivities",oXmlEleViewElements.getElementsByTagName("hideMyActivities").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showAllActivities",oXmlEleViewElements.getElementsByTagName("showAllActivities").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showAllMessages",oXmlEleViewElements.getElementsByTagName("showAllMessages").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showElements",oXmlEleViewElements.getElementsByTagName("showElements").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("copyNeed",oXmlEleViewElements.getElementsByTagName("copyNeed").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("agents",oXmlEleViewElements.getElementsByTagName("agents").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("network",oXmlEleViewElements.getElementsByTagName("network").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addNewTask",oXmlEleViewElements.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("audit",oXmlEleViewElements.getElementsByTagName("audit").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("type",oXmlEleViewElements.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("high",oXmlEleViewElements.getElementsByTagName("high").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("low",oXmlEleViewElements.getElementsByTagName("low").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("after",oXmlEleViewElements.getElementsByTagName("after").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("moveTaskToSegment",oXmlEleViewElements.getElementsByTagName("moveTaskToSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("other",oXmlEleViewElements.getElementsByTagName("other").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("mitigate",oXmlEleViewElements.getElementsByTagName("mitigate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("financial",oXmlEleViewElements.getElementsByTagName("financial").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("minor",oXmlEleViewElements.getElementsByTagName("minor").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("actual",oXmlEleViewElements.getElementsByTagName("actual").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoUpdateSegment",oXmlEleViewElements.getElementsByTagName("undoUpdateSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoUpdateSegment",oXmlEleViewElements.getElementsByTagName("redoUpdateSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoRemoveThisSegment",oXmlEleViewElements.getElementsByTagName("undoRemoveThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoRemoveThisSegment",oXmlEleViewElements.getElementsByTagName("redoRemoveThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoUpdateTask",oXmlEleViewElements.getElementsByTagName("undoUpdateTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddNewTask",oXmlEleViewElements.getElementsByTagName("undoAddNewTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("cutTask",oXmlEleViewElements.getElementsByTagName("cutTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoCutTask",oXmlEleViewElements.getElementsByTagName("undoCutTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoPasteTask",oXmlEleViewElements.getElementsByTagName("undoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("pasteTask",oXmlEleViewElements.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("copyTask",oXmlEleViewElements.getElementsByTagName("copyTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("duplicateTask",oXmlEleViewElements.getElementsByTagName("duplicateTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("UndoDuplicateTask",oXmlEleViewElements.getElementsByTagName("UndoDuplicateTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addIntermediate",oXmlEleViewElements.getElementsByTagName("addIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddIntermediate",oXmlEleViewElements.getElementsByTagName("undoAddIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("addNewIssue",oXmlEleViewElements.getElementsByTagName("addNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddNewIssue",oXmlEleViewElements.getElementsByTagName("undoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddNewIssue",oXmlEleViewElements.getElementsByTagName("redoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoRemoveIssue",oXmlEleViewElements.getElementsByTagName("undoRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("removeIssue",oXmlEleViewElements.getElementsByTagName("removeIssue").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoRedirectFlow",oXmlEleViewElements.getElementsByTagName("undoRedirectFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoRedirectFlow",oXmlEleViewElements.getElementsByTagName("redoRedirectFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoMoveTasks",oXmlEleViewElements.getElementsByTagName("undoMoveTasks").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("disintermediate",oXmlEleViewElements.getElementsByTagName("disintermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoDisintermediate",oXmlEleViewElements.getElementsByTagName("undoDisintermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("reference",oXmlEleViewElements.getElementsByTagName("reference").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("pasteAttachment",oXmlEleViewElements.getElementsByTagName("pasteAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoPasteAttachment",oXmlEleViewElements.getElementsByTagName("undoPasteAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoPasteAttachment",oXmlEleViewElements.getElementsByTagName("redoPasteAttachment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAttachDocument",oXmlEleViewElements.getElementsByTagName("undoAttachDocument").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAttachDocument",oXmlEleViewElements.getElementsByTagName("redoAttachDocument").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("removeFlow",oXmlEleViewElements.getElementsByTagName("removeFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoRemoveFlow",oXmlEleViewElements.getElementsByTagName("undoRemoveFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoRemoveFlow",oXmlEleViewElements.getElementsByTagName("redoRemoveFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddSharingCapability",oXmlEleViewElements.getElementsByTagName("undoAddSharingCapability").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddSharingCapability",oXmlEleViewElements.getElementsByTagName("redoAddSharingCapability").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("setTaskFromCopy",oXmlEleViewElements.getElementsByTagName("setTaskFromCopy").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoSetTaskFromCopy",oXmlEleViewElements.getElementsByTagName("undoSetTaskFromCopy").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoSetTaskFromCopy",oXmlEleViewElements.getElementsByTagName("redoSetTaskFromCopy").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoAddInformationNeed",oXmlEleViewElements.getElementsByTagName("undoAddInformationNeed").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddInformationNeed",oXmlEleViewElements.getElementsByTagName("redoAddInformationNeed").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoDuplicateFlow",oXmlEleViewElements.getElementsByTagName("undoDuplicateFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("duplicateFlow",oXmlEleViewElements.getElementsByTagName("duplicateFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoBreakUpFlow",oXmlEleViewElements.getElementsByTagName("undoBreakUpFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoBreakUpFlow",oXmlEleViewElements.getElementsByTagName("redoBreakUpFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("breakUpFlow",oXmlEleViewElements.getElementsByTagName("breakUpFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("copyFlow",oXmlEleViewElements.getElementsByTagName("copyFlow").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoRemoveGoal",oXmlEleViewElements.getElementsByTagName("redoRemoveGoal").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddIntermediate",oXmlEleViewElements.getElementsByTagName("redoAddIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoAddNewTask",oXmlEleViewElements.getElementsByTagName("redoAddNewTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoPasteTask",oXmlEleViewElements.getElementsByTagName("redoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoDisintermediate",oXmlEleViewElements.getElementsByTagName("redoDisintermediate").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("confirmed",oXmlEleViewElements.getElementsByTagName("confirmed").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("messages",oXmlEleViewElements.getElementsByTagName("messages").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showSent",oXmlEleViewElements.getElementsByTagName("showSent").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showReceived",oXmlEleViewElements.getElementsByTagName("showReceived").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("calendar",oXmlEleViewElements.getElementsByTagName("calendar").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showAll",oXmlEleViewElements.getElementsByTagName("showAll").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("aboutMe",oXmlEleViewElements.getElementsByTagName("aboutMe").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("issueSummaryReport",oXmlEleViewElements.getElementsByTagName("issueSummaryReport").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("channelsIssueSummaryReport",oXmlEleViewElements.getElementsByTagName("channelsIssueSummaryReport").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("assignmentsAndCommitments",oXmlEleViewElements.getElementsByTagName("assignmentsAndCommitments").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("assignmentsAndCommitmentsPageTitle",oXmlEleViewElements.getElementsByTagName("assignmentsAndCommitmentsPageTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("informationSharingGuidelinesForAllParticipants",oXmlEleViewElements.getElementsByTagName("informationSharingGuidelinesForAllParticipants").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("participantPagesTitle",oXmlEleViewElements.getElementsByTagName("participantPagesTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("signOut",oXmlEleViewElements.getElementsByTagName("signOut").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("places",oXmlEleViewElements.getElementsByTagName("places").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("issues",oXmlEleViewElements.getElementsByTagName("issues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("Issues",oXmlEleViewElements.getElementsByTagName("Issues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hideBroadcast",oXmlEleViewElements.getElementsByTagName("hideBroadcast").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("samePlan",oXmlEleViewElements.getElementsByTagName("samePlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoMoveTasks",oXmlEleViewElements.getElementsByTagName("redoMoveTasks").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoUpdateOrganization",oXmlEleViewElements.getElementsByTagName("redoUpdateOrganization").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("undoUpdateOrganization",oXmlEleViewElements.getElementsByTagName("undoUpdateOrganization").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("afourtech",oXmlEleViewElements.getElementsByTagName("afourtech").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("planners",oXmlEleViewElements.getElementsByTagName("planners").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("menu",oXmlEleViewElements.getElementsByTagName("menu").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("displayProceduresFrom",oXmlEleViewElements.getElementsByTagName("displayProceduresFrom").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("indexOn",oXmlEleViewElements.getElementsByTagName("indexOn").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("issuesInSegment",oXmlEleViewElements.getElementsByTagName("issuesInSegment").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("showTypes",oXmlEleViewElements.getElementsByTagName("showTypes").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allParticipants",oXmlEleViewElements.getElementsByTagName("allParticipants").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("thisIsVersion",oXmlEleViewElements.getElementsByTagName("thisIsVersion").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("agent1",oXmlEleViewElements.getElementsByTagName("agent1").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("agent2",oXmlEleViewElements.getElementsByTagName("agent2").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("description",oXmlEleViewElements.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("tagDescription",oXmlEleViewElements.getElementsByTagName("tagDescription").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("category1",oXmlEleViewElements.getElementsByTagName("category1").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("email",oXmlEleViewElements.getElementsByTagName("email").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("rolesPerformed",oXmlEleViewElements.getElementsByTagName("rolesPerformed").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("allFlowsInvolving",oXmlEleViewElements.getElementsByTagName("allFlowsInvolving").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("includeWaivedIssues",oXmlEleViewElements.getElementsByTagName("includeWaivedIssues").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("name",oXmlEleViewElements.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("helpPageTitle",oXmlEleViewElements.getElementsByTagName("helpPageTitle").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("hideCompleted",oXmlEleViewElements.getElementsByTagName("hideCompleted").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("doingSomething",oXmlEleViewElements.getElementsByTagName("doingSomething").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("deletePlan",oXmlEleViewElements.getElementsByTagName("deletePlan").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("newMedium",oXmlEleViewElements.getElementsByTagName("newMedium").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("anyMedium",oXmlEleViewElements.getElementsByTagName("anyMedium").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("participations",oXmlEleViewElements.getElementsByTagName("participations").item(0).getChildNodes().item(0).getNodeValue());
			GlobalVariables.viewElements.put("redoRemoveIssue",oXmlEleViewElements.getElementsByTagName("redoRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initialize Automation Scripts
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void initializeTestData() throws InterruptedException, IOException {
		System.out.println("Initializing  TestData...");
		GlobalVariables.sStartDateTime = LogFunctions.getDateTime();
		createResultFiles();
		try{
			//csv file containing data
			GlobalVariables.sTestDataDirectoryPath= GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestData\\";
			File testDataFile=new File(GlobalVariables.sTestDataDirectoryPath+"TestData.csv");
			BufferedReader testData = new BufferedReader( new FileReader(testDataFile));
			String strLine = "",key="",value="";
			while((strLine=testData.readLine())!=null)
			{
				st = new StringTokenizer(strLine, ",");
				while(st.hasMoreTokens())
				{
					key=st.nextToken();
					value=st.nextToken();
					GlobalVariables.testData.put(key,value);
				}
			}
		}
		catch(Exception e){
			System.out.println("Exception while reading csv file: " + e.getMessage());
		}
		/*GlobalStatic.oDriver = new FirefoxDriver();
		// URL
		GlobalStatic.oDriver.get(GlobalStatic.sLoginURL);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(5000);
		GlobalStatic.oDriver.manage().deleteAllCookies();
		// Usernames
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sUsername);
		// Password
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sPassword);
		GlobalStatic.oDriver.findElement(By.name("_spring_security_remember_me")).click();
		// Sign in
		GlobalStatic.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(10000);
		GlobalStatic.oDriver.findElement(By.linkText("Channels administration")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// newPlanUri
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("newPlanUri"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		// newPlanClient
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("newPlanClient"));
		GlobalStatic.oElement.sendKeys("Afourtech");
		//Submit
		GlobalStatic.oDriver.findElement(By.name("Submit")).submit();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(10000);
		// Go back
		GlobalStatic.oDriver.navigate().back();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.navigate().back();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.findElement(By.linkText("Information sharing model")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Select Plan
		GlobalStatic.oDriver.findElement(By.name("switch-plan:plan-sel")).click();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("switch-plan:plan-sel"));
		GlobalStatic.oElement.sendKeys("New Plan v.1 (dev)");
		GlobalStatic.oElement.sendKeys(Keys.ENTER);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(5000);
		// Click About plan under Show pop up menu
		ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[2]/span/span", "About plan");
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Enter Plan Name
		GlobalStatic.oDriver.findElement(By.name("plan:mo:aspect:name")).clear();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("plan:mo:aspect:name"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		// Click done button
		GlobalStatic.oDriver.findElement(By.className("close")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(2000);
		// Update Default Segment
		ApplicationFunctionLibrary.addSegment("Segment 1", "Default");
		// Add New Segment
		ApplicationFunctionLibrary.addSegment("Segment 2", "New");
		// Call logout
		ApplicationFunctionLibrary.logout();*/
		System.out.println("TestData initialization completed");
	}

	/**
	 * Initialize Automation Scripts
	 * @throws InterruptedException
	 */
	public static void tearDownTestData() throws InterruptedException {
		System.out.println("Performing cleanup TestData...");
		/*GlobalStatic.oDriver = new FirefoxDriver();
		// URL
		GlobalStatic.oDriver.get(GlobalStatic.sLoginURL);
		// Username
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sUsername);
		// Password
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sPassword);
		// Sign in
		GlobalStatic.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.findElement(By.linkText("Channels administration")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Select Plan
		GlobalStatic.oDriver.findElement(By.name("plan-sel")).click();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("plan-sel"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		GlobalStatic.oElement.sendKeys(Keys.ENTER);
		// Delete Plan
		GlobalStatic.oDriver.findElement(By.linkText("Delete plan")).click();
		Alert alert = GlobalStatic.oDriver.switchTo().alert();
		Thread.currentThread();
		Thread.sleep(2000);
		// And acknowledge the alert (equivalent to clicking "OK")
		alert.accept();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Logout
		GlobalStatic.oDriver.findElement(By.partialLinkText("Logout ")).click();
		// Webdriver close
		GlobalStatic.oDriver.close();*/
		GlobalVariables.sEndDateTime = LogFunctions.getDateTime();
		System.out.println("TestData cleanup completed");
	}
}
