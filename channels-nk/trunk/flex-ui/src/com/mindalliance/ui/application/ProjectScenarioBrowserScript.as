/* 
	Example of the kinds of permissions we need.
	Assuming this stuff will be set by the backend.
 */

import mx.managers.PopUpManager;

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
