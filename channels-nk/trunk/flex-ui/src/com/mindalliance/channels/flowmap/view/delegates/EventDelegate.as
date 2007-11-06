package com.mindalliance.channels.flowmap.view.delegates {
	import com.mindalliance.channels.flowmap.view.FlowMapLayoutHelper;
	import com.mindalliance.channels.flowmap.view.GraphHelper;
	import com.mindalliance.channels.flowmap.view.data.EventNodeData;
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.mindalliance.channels.flowmap.view.data.LabelData;
	import com.mindalliance.channels.flowmap.view.data.NodeData;
	import com.mindalliance.channels.flowmap.view.data.PortType;
	import com.mindalliance.channels.flowmap.view.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.Iterator;
	
	import mx.collections.ArrayCollection;
	
	
	public class EventDelegate extends BaseDelegate {

		public function EventDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}
		
		public function removeEvent(eventID:String):void {
			helper.removeNode(eventID) ;
		}
		
		public function renameEvent(eventID:String, newText:String):void {
			var end:EventNodeData = mapper.nodeDataMapper.lookupValue(eventID) as EventNodeData ;
			var labelIter:Iterator = end.node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type != LabelData.LABEL_TYPE_EVENT)
					continue ;
				graph.setLabelText(ld.label, newText) ;
				// Adjust node size to fit label
				FlowMapLayoutHelper.updateNodeBounds(graph, label.owner as INode) ;
				break ;
			}
		}
		
		public function removeAllEvents():void {
			var iter:Iterator = (mapper.nodeDataMapper as DictionaryMapper).values() ;
			var itemsToRemove:ArrayCollection = new ArrayCollection() ;
			while (iter.hasNext()) {
				var nd:NodeData = iter.next() as NodeData ;
				if (nd.type == NodeData.NODE_TYPE_EVENT)
					itemsToRemove.addItem(nd.id) ;
			}
			for each (var id:String in itemsToRemove)
				removeEvent(id) ;
		}
		
		public function addEvent(phaseID:String, eventID:String, eventLabel:String):void {
			// First check if event is already there
			var end:EventNodeData = mapper.nodeDataMapper.lookupValue(eventID) as EventNodeData ;
			if (end) {
				renameEvent(eventID, eventLabel) ;
				return ;
			}
			
			var node:INode = helper.addNewNode(FlowMapStyles.eventNodeStyle, eventID) ;
			
			//Setup mappings
			end = new EventNodeData(node, eventID) ;
			end.startPhaseID = phaseID ;
			end.endPhaseID = phaseID ;
			mapper.nodeDataMapper.mapValue(eventID, end) ;
			
			// Add the event label
			helper.addNewNodeLabel(node, eventLabel, 
							FlowMapStyles.eventLabelModelParameter, 
							FlowMapStyles.eventLabelStyle, 
							eventID, LabelData.LABEL_TYPE_EVENT) ;
		
			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(graph, node) ;
			
			// Add ports
			var rect:IRectangle = node.layout ;
			helper.addNewPort(node, rect.x + rect.width + 5, rect.y + rect.height/2, PortType.PORT_TYPE_EVENT_OUTGOING, eventID) ;
			
			/* _updateScenarioStageBounds(stageID) ; */
		}
	}
}