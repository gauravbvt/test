package com.mindalliance.testscripts;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.ReportFunctions;

public class ExecuteTestcases {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			GenericFunctionLibrary.initializeTestData();		
//			ConcurrentUserTest cut=new ConcurrentUserTest();
//			MACU0002_allEvents.lockAllEvents();
//			MACU0003_lockSecreacyClassifications.lockSecreacyClassifications();
//			MACU0007_lockDetailsAboutPlanSegment.lockDetailsAboutPlanSegment();
//			MACU0008_lockGoalsOfPlanSegment.lockGoalsOfPlanSegment();
//			MACU0009_lockTaskMover.lockTaskMover();
//			MACU0010_lockRemoveSegment.lockRemoveThisSegment();
//			MACU0011_lockUndoPlanSegment.lockUndoPlanSegment();
//		    MACU0012_lockRedoPlanSegment.lockRedoPlanSegment();
//			MACU0013_lockUpdateTaskDetails.lockUpdateTaskDetails();
//			MACU0014_addNewTask.addNewTask();
//			MACU0015_lockSameTaskDetails.lockSameTaskDetails();
//			MACU0016_addReceiveInfo.addReceiveInfo();
//			MACU0017_addSentInfo.addSentInfo();
//			MACU0018_sendMessage.sendMessage();
//			MACU0019_addNewTask.addNewTask();
//			MACU0020_addNewSegment.addNewSegment();
//			MACU0021_lockMoveTaskPlanSegment.lockMoveTaskPlanSegment();
			
		    // Call tearAutomationScripts()
		    GenericFunctionLibrary.tearDownTestData();
		    // Call generateAutomationReportInOds()
		    ReportFunctions.generateAutomationReport();
		}
		catch(Exception e)
		{
			System.console().writer();
		}
	}
}

