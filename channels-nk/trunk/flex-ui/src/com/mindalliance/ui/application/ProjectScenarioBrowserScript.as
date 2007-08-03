/* 
	Example of the kinds of permissions we need.
	Assuming this stuff will be set by the backend.
 */

import mx.managers.PopUpManager;
import mx.collections.ArrayCollection;
import mx.controls.Alert;
import com.mindalliance.ui.common.InputTextDialog;

private function updateScenarioList():void {
		var projectID:String = Projects.getItemAt(listProjects.selectedIndex).ProjectID ;
		Scenarios.removeAll();
		for (var i:int = 0 ; i < AllScenarios.length ; ++i) {
			var scenario:Object = AllScenarios.getItemAt(i) ;
			if (scenario.ProjectID == projectID)
				Scenarios.addItem(scenario) ;
		}
}

private function addProject():void {
/* 	Popup text box.
	Create project object.
	Add to data provider. */
}

[Bindable]
private var Scenarios:ArrayCollection = new ArrayCollection();

/* Here we put variables relating to permissions to be set by cairngorm. */

[Bindable]
private var canEditProject:Boolean = false ;

[Bindable]
private var canAddProject:Boolean = false ;

[Bindable]
private var canRemoveProject:Boolean = false ;

[Bindable]
private var canEditScenario:Boolean = false ;

[Bindable]
private var canAddScenario:Boolean = false ;

[Bindable]
private var canRemoveScenario:Boolean = false ;
