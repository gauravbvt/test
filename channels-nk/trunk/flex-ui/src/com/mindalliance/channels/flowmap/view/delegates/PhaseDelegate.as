package com.mindalliance.channels.flowmap.view.delegates {
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.yworks.graph.model.IGraph;
	
	
	public class PhaseDelegate extends BaseDelegate {
		
		public function PhaseDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}
	
		public function addPhase(phaseID:String, phaseName:String):void {
			var phase:Phase = Phase.createPhase(phaseName) ;
			phase.width = 200 ;
			phase.height = _graphCanvas.height ;
			/* _mapperHelper.phaseMapper.mapValue(phaseID, phase) ; */
			_graphCanvas.addCanvasObject(phase, _phaseCanvasObjectDescriptor, _phaseCanvasObjectGroup) ;
			_mapperHelper.idMapper.mapValue(phase, phaseID) ;
		}
	
		public function renamePhase(phaseID:String, newText:String):void {
			var phase:Phase = null ;
			if (phase == null)
				return ;
			phase.name = newText ;
			FlowMapLayoutHelper.updatePhaseBounds(_mapperHelper, phaseID) ;
			_graphCanvas.forceRepaint() ;
		}
	}
}