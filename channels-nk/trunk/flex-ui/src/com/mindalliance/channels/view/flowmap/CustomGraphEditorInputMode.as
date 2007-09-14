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
			moveInputMode.enabled = true ;
			handleInputMode.enabled = false ;
			deleteElementsAllowed = false ;
			labelEditingAllowed = false ;
			selectElementsAllowed = true ;
			createBendInputMode.enabled = true ;
			nodeCreator = null ;
			autoRemoveEmptyLabels = false ;
		}
		
		[Bindable(event="phaseSelectionChanged")]
		public function get selectedPhase():Phase {
			return _selectedPhase ;
		}
		
		private var _selectedPhase:Phase ;
		protected override function onClickInputModeClicked(evt:ClickEvent):void {
			var ico:ICanvasObject = this.graphCanvas.getCanvasObject(evt.clickPoint.x, evt.clickPoint.y) ;
			
			if (ico == null) {
				_clearPhaseSelection() ;
				dispatchEvent(new Event(FlowMapEvent.PHASE_SELECTION_CHANGED.name)) ;
			}
			else if (ico.userObject is Phase) {
				var ss:Phase = Phase(ico.userObject) ;
				if (ss == _selectedPhase)
					return ;
				_clearPhaseSelection() ;
				_selectedPhase = ss ;
				_selectedPhase.selected = true ;
				dispatchEvent(new Event(FlowMapEvent.PHASE_SELECTION_CHANGED.name)) ;
			}

			super.onClickInputModeClicked(evt) ;
		}
		
		
		private function _clearPhaseSelection():void {
			if (_selectedPhase != null) {
				_selectedPhase.selected = false ;
				_selectedPhase = null ;
			}			
		}
	}
}