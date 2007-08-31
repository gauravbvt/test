package com.mindalliance.channels.view.flowmap
{
	public class FlowMapEvent
	{
		
		public static const ITEM_SELECTED:FlowMapEvent = new FlowMapEvent("itemSelected") ;

		public static const ITEM_DESELECTED:FlowMapEvent = new FlowMapEvent("itemDeselected") ;
		
		public static const SCENARIO_STAGE_SELECTION_CHANGED:FlowMapEvent = new FlowMapEvent("scenarioStageSelectionChanged") ;
		
		private var _name:String ;
		
		public function FlowMapEvent(name:String) {
			this._name = name ;
		}
		
		public function get name():String {
			return this._name ;
		}
	}
}