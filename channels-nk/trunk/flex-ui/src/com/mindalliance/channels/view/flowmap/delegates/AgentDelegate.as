package com.mindalliance.channels.view.flowmap.delegates {
	import com.mindalliance.channels.view.flowmap.FlowMapLayoutHelper;
	import com.mindalliance.channels.view.flowmap.GraphHelper;
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.mindalliance.channels.view.flowmap.data.LabelData;
	import com.mindalliance.channels.view.flowmap.data.PortType;
	import com.mindalliance.channels.view.flowmap.data.TaskNodeData;
	import com.mindalliance.channels.view.flowmap.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IOrientedRectangle;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	
	
	public class AgentDelegate extends BaseDelegate {
		
		public function AgentDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}

		public function removeAgent(taskID:String, roleID:String):void {
			var tnd:TaskNodeData = mapper.nodeDataMapper.lookupValue(taskID) as TaskNodeData ;
			if (!tnd)
				return ;
			var label:ILabel = helper.getLabelByType(tnd.node, LabelData.LABEL_TYPE_ROLE) ;
			if (!label)
				return ;
			mapper.idMapper.unMapValue(label) ;
			mapper.labelDataMapper.unMapValue(label) ;
			graph.removeLabel(label) ;
			var port:IPort = helper.getPortByType(tnd.node, PortType.PORT_TYPE_ROLE_INCOMING) ;
			if (!port)
				return ;
			mapper.portTypeMapper.unMapValue(port) ;
			mapper.idMapper.unMapValue(port) ;
			graph.removePort(port) ;
		}
		
		public function renameAgent(taskID:String, roleID:String, newText:String):void {
/* 			renameRole(roleID, newText) ; */
// !!!!!!!!!!!!!!!!!!!!!!!!						FIX ME !!!!!!!!!!!!
		}
		
		private function addAgent(node:INode, roleID:String, roleLabel:String):void {
			// UPDATE TO ADD SUPPORT FOR MULTIPLE AGENTS
			var label:ILabel = helper.addNewNodeLabel(node, roleLabel, FlowMapStyles.roleLabelModelParameter, FlowMapStyles.roleLabelStyle, roleID, LabelData.LABEL_TYPE_ROLE) ;
			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(graph, node) ;
			var rect:IOrientedRectangle = label.layout ;
			var port:IPort = helper.addNewPort(node, rect.anchorX, rect.anchorY - rect.height/2, PortType.PORT_TYPE_ROLE_INCOMING, roleID) ;
		}
		
		public function setAgent(taskID:String, roleID:String, roleLabel:String):void {
			// Get the node in which this role exists
			var tnd:TaskNodeData = TaskNodeData(mapper.nodeDataMapper.lookupValue(taskID)) ;
			var label:ILabel = helper.getLabelByType(tnd.node, LabelData.LABEL_TYPE_ROLE) ;
			if (label == null)
				addAgent(tnd.node, roleID, roleLabel) ;
			else {
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id != roleID) {
					// get old agent id
					removeAgent(taskID, roleID) ;
					addAgent(tnd.node, roleID, roleLabel) ;
				}
				else
					renameAgent(taskID, roleID, roleLabel) ;
			}
		}
	}
}