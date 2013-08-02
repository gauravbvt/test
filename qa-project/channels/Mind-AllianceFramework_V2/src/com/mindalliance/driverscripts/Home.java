package com.mindalliance.driverscripts;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.uitestscripts.*;

/**
 * Class Home page creates a test suite  
 * @author afour
 *
 */
public class Home {
	public static Test suite() throws UIAutomationException{	  
		GlobalVariables.configuration= Configuration.getConfigurationObject();
		new ElementController();
		TestSuite suite = new TestSuite("Mind-Alliance Automation Framework");	

		suite.addTestSuite(CL0001_LoginPage.class);
		suite.addTestSuite(CL0007_ClickSignInButton.class);
		suite.addTestSuite(CL0018_ClickCantAccessYourAccountLink.class);
		//		View
		suite.addTestSuite(MAV0001_viewLoginPage.class);		
		suite.addTestSuite(MAV0002_viewHomePage.class);
		suite.addTestSuite(MAV0003_SignoutOnHomePage.class);
		suite.addTestSuite(MAV0004_viewAdminHome.class);
		suite.addTestSuite(MAV0005_logoutOnAdminPage.class);	
		suite.addTestSuite(MAV0006_redirectToPlanPage.class);
		suite.addTestSuite(MAV0007_logoutOnPlanPage.class);
		suite.addTestSuite(MAV0008_hidePlannerInfo.class);
		suite.addTestSuite(MAV0009_viewAboutPlanWindow.class);
		suite.addTestSuite(MAV0010_viewAllEventForm.class);
		suite.addTestSuite(MAV0011_viewAllSecrecyclassificationsForm.class);
		suite.addTestSuite(MAV0012_viewAllOrganizationForm.class);
		suite.addTestSuite(MAV0016_viewAllWhoForm.class);
		suite.addTestSuite(MAV0017_viewAllIssueForm.class);
		suite.addTestSuite(MAV0019_viewAllIndexForm.class);
		suite.addTestSuite(MAV0020_viewAllTagForm.class);
		suite.addTestSuite(MAV0021_viewAllEvaluationForm.class);
//		suite.addTestSuite(MAV0022_viewAllParticipationsForm.class);
		suite.addTestSuite(MAV0023_viewAllVersionsForm.class);
		suite.addTestSuite(MAV0025_closeAboutPlanWindow.class);
		suite.addTestSuite(MAV0026_viewAboutPlanSegmentWindow.class);
		suite.addTestSuite(MAV0028_viewGoalsForm.class);
		suite.addTestSuite(MAV0029_viewOrganizationsForm.class);
		suite.addTestSuite(MAV0005_logoutOnAdminPage.class);
		suite.addTestSuite(MAV0007_logoutOnPlanPage.class);
//		suite.addTestSuite(MAV0030_viewMapWindow.class);
		suite.addTestSuite(MAV0031_viewTaskMoversForm.class);
		suite.addTestSuite(MAV0033_closeAboutPlanSegmentWindow.class);
		suite.addTestSuite(MAV0034_viewSurveryWindow.class);
		suite.addTestSuite(MAV0035_closeSurveysWindow.class);
		suite.addTestSuite(MAV0066_viewAllSegmentsActionList.class);
		suite.addTestSuite(MAV0067_closeAllSegmentWindow.class);
		suite.addTestSuite(MAV0075_undoAddNewIssueUnderAction.class);
		suite.addTestSuite(MAV0076_redoAddNewIssueUnderAction.class);
		suite.addTestSuite(MAV0080_addNewTask.class);
		suite.addTestSuite(MAV0081_hideDetails.class);
		suite.addTestSuite(MAV0082_TaskDetails.class);
		suite.addTestSuite(MAV0083_addAssignment.class);
		suite.addTestSuite(MAV0084_closeAssignment.class);
		suite.addTestSuite(MAV0085_FailureImpact.class);
		suite.addTestSuite(MAV0086_closeFailureImpact.class);		
		suite.addTestSuite(MAV0087_Dissimination.class);
		suite.addTestSuite(MAV0088_closeDissemination.class);
		suite.addTestSuite(MAV0089_viewActionsPopup.class);
		suite.addTestSuite(MAV0090_addNewIssue.class);
		suite.addTestSuite(MAV0091_closeNewIssue.class);
		suite.addTestSuite(MAV0092_addNewSegment.class);
		suite.addTestSuite(MAV0093_closeNewSegment.class);
		suite.addTestSuite(MAV0098_removeSegmentUnderAction.class);
		suite.addTestSuite(MAV0100_moveTaskUnderAction.class);
		suite.addTestSuite(MAV0101_closeTaskMover.class);
//		suite.addTestSuite(MAV0102_viewHelpForm.class);
//		suite.addTestSuite(MAV0103_viewHelpFormWithFeedback.class);
		suite.addTestSuite(MAV0107_viewPresence.class);
		suite.addTestSuite(MAV0109_hideInactiveUsers.class);
		suite.addTestSuite(MAV0110_viewActivitiesAllUsers.class);
		suite.addTestSuite(MAV0111_hideActivities.class);
		suite.addTestSuite(MAV0112_viewAllActivities.class);
		suite.addTestSuite(MAV0113_viewMessages.class);
		suite.addTestSuite(MAV0114_hideBroadcasts.class);
		suite.addTestSuite(MAV0115_showAllMessages.class);
		suite.addTestSuite(MAV0116_showSent.class);
		suite.addTestSuite(MAV0117_showReceived.class);
		suite.addTestSuite(MAV0124_sendMessage.class);
//		suite.addTestSuite(MAV0125_copyFlow.class);
		suite.addTestSuite(MAV0126_copyNeed.class);
		suite.addTestSuite(MAV0127_addNewIssue.class);
		suite.addTestSuite(MAV0128_removeInfoNeed.class);
		suite.addTestSuite(MAV0150_viewSocialPanelMessage.class);
		suite.addTestSuite(MAV0151_viewSocialPanelShowSentMessage.class);
		suite.addTestSuite(MAV0152_viewSocialPanelShowReceivedMessage.class);
		suite.addTestSuite(MAV0153_viewSocialPanelHideBroadCast.class);
		suite.addTestSuite(MAV0154_viewSocialPanelShowAllMessages.class);
//		suite.addTestSuite(MAV0157_viewSocialPanelCalender.class);
		suite.addTestSuite(MAV0161_viewSocialPanelAboutMe.class);
		suite.addTestSuite(MAV0162_viewIssueSummaryReport.class);
		suite.addTestSuite(MAV0164_viewHomeFromIssueReport.class);
		suite.addTestSuite(MAV0165_SimpleFormOfTask.class);
		suite.addTestSuite(MAV0166_AdvanceFormOfTask.class);
		suite.addTestSuite(MAV0167_SimpleFormOfReceiveInfo.class);		
		suite.addTestSuite(MAV0168_AdvanceFormOfReceivenfo.class);
		suite.addTestSuite(MAV0169_SimpleFormOfSendInfo.class);
		suite.addTestSuite(MAV0170_AdvanceFormOfSendInfo.class);
		suite.addTestSuite(MAV0171_ReceivePanel.class);
		suite.addTestSuite(MAV0172_SendPanel.class);
		suite.addTestSuite(MAV0173_AddInfoReceive.class);
		suite.addTestSuite(MAV0174_AddInfoSend.class);
		suite.addTestSuite(MAV0175_ExpandTaskSummary.class);
		suite.addTestSuite(MAV0176_ShowOrHideTaskDetails.class);
		suite.addTestSuite(MAV0177_TaskTagsDetails.class);
		suite.addTestSuite(MAV0178_TaskTagsLink.class);
		suite.addTestSuite(MAV0179_TaskIsOption.class);
		suite.addTestSuite(MAV0182_CausesEventOption.class);
		suite.addTestSuite(MAV0183_UsuallyCompletesAfterOption.class);
		suite.addTestSuite(MAV0184_RepeatsEvery.class);
		suite.addTestSuite(MAV0186_EventUnnamed.class);
		suite.addTestSuite(MAV0187_OngoingOrStartsWith.class);
		suite.addTestSuite(MAV0188_EndEventUnnamed.class);
		suite.addTestSuite(MAV0189_GoalsLink.class);
		suite.addTestSuite(MAV0190_GoalsOption.class);
		suite.addTestSuite(MAV0191_GoalsOptionUnchecked.class);
		suite.addTestSuite(MAV0193_TaskAttachOptions.class);
////		suite.addTestSuite(MAV0194_TaskNewIssue.class);
		suite.addTestSuite(MAV0195_TaskIssueDetails.class);
		suite.addTestSuite(MAV0196_TaskIssueTypeOption.class);
		suite.addTestSuite(MAV0197_TaskIssueSeverityOption.class);
		suite.addTestSuite(MAV0198_TaskIssueAttachOptions.class);
		suite.addTestSuite(MAV0199_AddInfoReceiveSendInformation.class);
		suite.addTestSuite(MAV0200_AddInfoReceiveSendTagLink.class);
////		suite.addTestSuite(MAV0201_AddInfoReceiveSendIntent.class);
		suite.addTestSuite(MAV0202_AddInfoReceiveSendElementsLink.class);
		suite.addTestSuite(MAV0203_NotificationOption.class);
		suite.addTestSuite(MAV0204_ReplyThatOption.class);
        suite.addTestSuite(MAV0205_AddInfoReceiveSendButOnlyIfOption.class);
		suite.addTestSuite(MAV0206_AddInfoReceiveSendChannels.class);
		suite.addTestSuite(MAV0207_AddInfoReceiveSendChannelsOptions.class);
		suite.addTestSuite(MAV0208_AddInfoReceiveSendWithin.class);
		suite.addTestSuite(MAV0209_AddInfoReceiveSendAttachOptions.class);
 		suite.addTestSuite(MAV0210_AddInfoReceiveSendNewIssue.class);
		suite.addTestSuite(MAV0211_AddInfoReceiveSendIssueTypeOption.class);
		suite.addTestSuite(MAV0212_AddInfoReceiveSendIssueSeverity.class);
////		suite.addTestSuite(MAV0213_AttachReceiveSentAttachOption.class);
		suite.addTestSuite(MAV0215_AddInfoReceiveShowPopUpMenu.class);
		suite.addTestSuite(MAV0216_AddInfoReceiveActionPopUpMenu.class);
		suite.addTestSuite(MAV0217_AddInfoSentShowPopUpMenu.class);
		suite.addTestSuite(MAV0218_AddInfoSentActionPopUpMenu.class);
		suite.addTestSuite(MAV0219_AddInfoReceiveShowHideDetails.class);
		suite.addTestSuite(MAV0220_AddInfoSentHideShowDetails.class);
		suite.addTestSuite(MAV0221_AddInfoReceiveSentSendMessage.class);
		suite.addTestSuite(MAV0222_AddInfoReceiveShowElements.class);
		suite.addTestSuite(MAV0223_AddInfoSentCopyCapability.class);
		suite.addTestSuite(MAV0224_AddInfoReceiveCopyNeed.class);
		suite.addTestSuite(MAV0225_AddInfoReceiveRemoveInfoNeeds.class);
//		suite.addTestSuite(MAV0226_AddInfoSentRemoveSharingCapability.class);
//		suite.addTestSuite(MAV0243_ShowRequirements.class);
//		suite.addTestSuite(MAV0245_ShowRequirementDefinition.class);
//		suite.addTestSuite(MAV0246_ShowRequirementNetwork.class);
//		suite.addTestSuite(MAV0247_NewRequirement.class);
//		suite.addTestSuite(MAV0248_RemoveRequirement.class);
////	suite.addTestSuite(MAV0249_EditRequirement.class);
//		suite.addTestSuite(MAV0250_ViewAllSituationRequirement.class);
//		suite.addTestSuite(MAV0251_ViewRequirementBefore.class);
//		suite.addTestSuite(MAV0252_ViewRequirementAfter.class);
//		suite.addTestSuite(MAV0253_ViewRequirementDuring.class);
//		suite.addTestSuite(MAV0254_ViewRequirementAtAnyEvent.class);
//		suite.addTestSuite(MAV0269_ViewOrganizationsNetworkTab.class);
//		suite.addTestSuite(MAV0270_ViewOrganizationStructureTab.class);
//		suite.addTestSuite(MAV0271_ViewOrganizationAgreementTab.class);
//		suite.addTestSuite(MAV0272_ViewOrganizationAnalyticsTab.class);
//		suite.addTestSuite(MAV0273_ViewOrganizationIssueTab.class);
//		
////		Plan
//		
//		suite.addTestSuite(MAP0001_AddPlan.class);
//		suite.addTestSuite(MAP0002_AddNameAndLocalizePlan.class);
//		suite.addTestSuite(MAP0003_AddNewUserToPlan.class);
//		suite.addTestSuite(MAP0004_DeleteUser.class);
//		suite.addTestSuite(MAP0005_DeletePlan.class);
//		suite.addTestSuite(MAP0006_addOrganizations.class);
//		suite.addTestSuite(MAP0007_addOrganizationsDetails.class);
//		suite.addTestSuite(MAP0008_removeExpectation.class);
//		suite.addTestSuite(MAP0009_addTask.class);
		suite.addTestSuite(MAP0010_addRoleJuridiction.class);
		suite.addTestSuite(MAP0011_addEventToPlan.class);
    	suite.addTestSuite(MAP0012_addPhase.class);
		suite.addTestSuite(MAP0013_addPhaseDetails.class);
		suite.addTestSuite(MAP0014_addSegment.class);
		suite.addTestSuite(MAP0015_removeSegment.class);
//		suite.addTestSuite(MAP0016_addEvent.class);
//		suite.addTestSuite(MAP0017_deleteEvent.class);
////		suite.addTestSuite(MAP0018_attachFile.class);
////		suite.addTestSuite(MAP0019_deleteAttachFile.class);
////		suite.addTestSuite(MAP0020_addFileToEvent.class);
////		suite.addTestSuite(MAP0021_deleteFileOfEvent.class);
		suite.addTestSuite(MAP0022_addGoal.class);
		suite.addTestSuite(MAP0023_removeGoal.class);	
////		suite.addTestSuite(MAP0024_moveTask.class);
////		suite.addTestSuite(MAP0025_receiveTask.class);
////		suite.addTestSuite(MAP0026_sendTask.class);
////		suite.addTestSuite(MAP0027_AttachFileToRequirement.class);
//		suite.addTestSuite(MAP0028_AddQuestionnaire.class);
//		suite.addTestSuite(MAP0029_AddQuestionnaireName.class);
////		suite.addTestSuite(MAP0030_AddQuestionnaireRFIAsThisPlan.class);
//		
////		Command
//		
		suite.addTestSuite(MAC0001_UndoAddSegment.class);
		suite.addTestSuite(MAC0002_UndoRemoveThisSegment.class);
		suite.addTestSuite(MAC0003_UndoAddGoal.class);
		suite.addTestSuite(MAC0004_UndoRemoveGoal.class);
		suite.addTestSuite(MAC0005_UndoAddNewTask.class);	
		suite.addTestSuite(MAC0006_UndoCutTask.class);
		suite.addTestSuite(MAC0007_UndoPasteTaskUsingCut.class);
		suite.addTestSuite(MAC0008_UndoPasteTaskUsingCopy.class);
		suite.addTestSuite(MAC0009_UndoDuplicateTask.class);
		suite.addTestSuite(MAC0010_UndoIntermediateTask.class);
////		suite.addTestSuite(MAC0011_UndoDisintermediateTask.class);
////		suite.addTestSuite(MAC0012_UndoMoveTask.class);
////		suite.addTestSuite(MAC0013_UndoConnectFlow.class);
//		suite.addTestSuite(MAC0021_CopyTask.class);
//		suite.addTestSuite(MAC0023_UndoAddInfoNeed.class);
//		suite.addTestSuite(MAC0024_UndoAddInfoCapability.class);
////		suite.addTestSuite(MAC0025_UndoRemoveFlow.class);
////		suite.addTestSuite(MAC0026_UndoDuplicateFlow.class);
//		suite.addTestSuite(MAC0031_RedoAddSegment.class);
//		suite.addTestSuite(MAC0033_RedoAddGoal.class);
//		suite.addTestSuite(MAC0034_RedoRemoveGoal.class);
//		suite.addTestSuite(MAC0035_RedoAddNewTask.class);
//		suite.addTestSuite(MAC0036_RedoCutTask.class);
//		suite.addTestSuite(MAC0037_RedoPasteTaskUsingCut.class);
//		suite.addTestSuite(MAC0038_RedoPasteTaskUsingCopy.class);
//		suite.addTestSuite(MAC0039_RedoDuplicateTask.class);
////		suite.addTestSuite(MAC0040_RedoIntermediateTask.class);
////		suite.addTestSuite(MAC0041_RedoDisintermediateTask.class);
////		suite.addTestSuite(MAC0042_RedoMoveTask.class);
////		suite.addTestSuite(MAC0043_RedoConnectFlow.class);
////		suite.addTestSuite(MAC0045_RedoAddIssue.class);
////		suite.addTestSuite(MAC0046_RedoRemoveIssue.class);
////		suite.addTestSuite(MAC0047_RedoAttachDocument.class);
////		suite.addTestSuite(MAC0050_RedoPasteAttachment.class);
////		suite.addTestSuite(MAC0052_RedoSetTaskFromCopy.class);
////		suite.addTestSuite(MAC0053_RedoAddInfoNeed.class);
////		suite.addTestSuite(MAC0054_RedoAddInfoCapability.class);
////		suite.addTestSuite(MAC0055_RedoRemoveFlow.class);
////		suite.addTestSuite(MAC0056_RedoDuplicateFlow.class);
////		suite.addTestSuite(MAC0057_RedoBreakUpFlow.class);
////		suite.addTestSuite(MAC0059_RedoTransferJobs.class);
//		suite.addTestSuite(MAC0061_UndoAddNewRequirement.class);
////		suite.addTestSuite(MAC0062_RedoAddNewRequirement.class);
//		suite.addTestSuite(MAC0063_UndoRemoveRequirement.class);
//		suite.addTestSuite(MAC0064_RedoRemoveRequirement.class);
//		
		
		
		
				
		return suite;
	}
}

