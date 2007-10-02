package com.mindalliance.channels.view.flowmap.delegates {
	import com.mindalliance.channels.view.flowmap.FlowMapLayoutHelper;
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.mindalliance.channels.view.flowmap.data.LabelData;
	import com.mindalliance.channels.view.flowmap.data.NodeData;
	import com.mindalliance.channels.view.flowmap.data.PortType;
	import com.mindalliance.channels.view.flowmap.data.TaskNodeData;
	import com.mindalliance.channels.view.flowmap.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.graph.model.IPortCandidateProvider;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.Iterator;
	
	import mx.collections.ArrayCollection;

	public class TaskDelegate extends BaseDelegate {
		
		public function TaskDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}

		public function removeTask(taskID:String):void {
			helper.removeNode(taskID) ;
		}
		
		public function removeAllTasks():void {
			var iter:Iterator = (mapper.nodeDataMapper as DictionaryMapper).values() ;
			var itemsToRemove:ArrayCollection = new ArrayCollection() ;
	
			while (iter.hasNext()) {
				var nd:NodeData = iter.next() as NodeData ;
				if (nd.type == NodeData.NODE_TYPE_TASK)
					itemsToRemove.addItem(nd.id) ;
			}
	
			for each (var id:String in itemsToRemove)
				removeTask(id) ;
		}
	
		public function renameTask(taskID:String, text:String):void {
			var tnd:TaskNodeData = helper.getNodeDataByID(taskID) as TaskNodeData ;
			var label:ILabel = helper.getLabelByType(tnd.node, LabelData.LABEL_TYPE_TASK) ;
			
			graph.setLabelText(label, text) ;
			FlowMapLayoutHelper.updateNodeBounds(graph, label.owner as INode) ;
		}
	
	
		private var _portCandidateProvider:IPortCandidateProvider ;
		public function set portCandidateProvider(pcp:IPortCandidateProvider):void {
			_portCandidateProvider = pcp ;
		}
		
		public function addTask(phaseID:String, taskID:String, taskLabel:String):void {
			var tnd:TaskNodeData = helper.getNodeDataByID(taskID) as TaskNodeData ;
	
			if (tnd) removeTask(taskID) ;
			
			// Find out where the node should be placed
			var node:INode = helper.addNewNode(FlowMapLayoutHelper.getLocationForNewNode2(), 
												FlowMapStyles.taskNodeStyle, 
												taskID) ;
	
			// Attach the custom port candidate provider
			(node as DefaultNode).registerLookup(IPortCandidateProvider, _portCandidateProvider) ;
			
			// Setup node data mappings
			tnd = new TaskNodeData(node, taskID, phaseID) ;
			mapper.nodeDataMapper.mapValue(taskID, tnd) ;
			mapper.idMapper.mapValue(node, taskID) ;
			
			// Add the task label
			var label:ILabel = helper.addNewNodeLabel(node, 
												taskLabel, 
												FlowMapStyles.taskLabelModelParameter, 
												FlowMapStyles.taskLabelStyle, 
												taskID, 
												LabelData.LABEL_TYPE_TASK) ;
	
			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(graph, node) ;
			
			// Add a port now
			var rect:IRectangle = node.layout ;
			var port:IPort = helper.addNewPort(tnd.node, 
										rect.x + rect.width + 5, 
										rect.y + rect.height/2, 
										PortType.PORT_TYPE_TASK_OUTGOING, 
										taskID) ;
	
			// _updatePhaseBounds(phaseID) ;
		}
	}
}