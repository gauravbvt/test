package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.ISelectionModel;
	import com.yworks.graph.input.GraphEditorInputMode;
	import com.yworks.graph.model.IGraph;
	import com.yworks.canvas.ICanvasObject;
	import com.yworks.canvas.input.ClickEvent;
	import flash.events.Event;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.SelectionEvent;
	import com.yworks.ui.GraphCanvasComponent;

	public class CustomGraphEditorInputMode extends GraphEditorInputMode
	{
		public function CustomGraphEditorInputMode(graph:IGraph, selectionModel:ISelectionModel)
		{
			super(graph, selectionModel);
		}
		
		public function configure(gc:GraphCanvasComponent):void {
			this.install(gc) ;
			contextMenuInputMode.uninstall(gc) ;
			moveInputMode.uninstall(gc) ;
			handleInputMode.uninstall(gc) ;
			deleteElementsAllowed = false ;
			labelEditingAllowed = false ;
			selectElementsAllowed = true ;
			createBendInputMode.enabled = true ;
			nodeCreator = null ;
			autoRemoveEmptyLabels = false ;
		}
		
		[Bindable(event="scenarioStageSelectionChanged")]
		public function get selectedScenarioStage():ScenarioStage {
			return _selectedScenarioStage ;
		}
		
		private var _selectedScenarioStage:ScenarioStage ;
		protected override function onClickInputModeClicked(evt:ClickEvent):void {
			var ico:ICanvasObject = this.graphCanvas.getCanvasObject(evt.clickPoint.x, evt.clickPoint.y) ;
			
			if (ico == null) {
				_clearScenarioStageSelection() ;
				dispatchEvent(new Event(FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED.name)) ;
			}
			else if (ico.userObject is ScenarioStage) {
				var ss:ScenarioStage = ScenarioStage(ico.userObject) ;
				if (ss == _selectedScenarioStage)
					return ;
				_clearScenarioStageSelection() ;
				_selectedScenarioStage = ss ;
				_selectedScenarioStage.selected = true ;
				dispatchEvent(new Event(FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED.name)) ;
			}

			super.onClickInputModeClicked(evt) ;
		}
		
		
		private function _clearScenarioStageSelection():void {
			if (_selectedScenarioStage != null) {
				_selectedScenarioStage.selected = false ;
				_selectedScenarioStage = null ;
			}			
		}
	}
}