package com.mindalliance.channels.view.flowmap.delegates {
	import com.mindalliance.channels.view.flowmap.FlowMapLayoutHelper;
	import com.mindalliance.channels.view.flowmap.GraphHelper;
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.mindalliance.channels.view.flowmap.data.LabelData;
	import com.mindalliance.channels.view.flowmap.data.NodeData;
	import com.mindalliance.channels.view.flowmap.data.PortType;
	import com.mindalliance.channels.view.flowmap.data.RepositoryNodeData;
	import com.mindalliance.channels.view.flowmap.data.RoleNodeData;
	import com.mindalliance.channels.view.flowmap.data.SharingNeedNodeData;
	import com.mindalliance.channels.view.flowmap.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.ExteriorLabelModel;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.support.Iterator;
	
	
	public class SharingNeedDelegate extends BaseDelegate {
		
		public function SharingNeedDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}

		private function ensureSharingNeedSourceExists(sourceID:String, sourceNodeType:String, sourceNodeLabel:String):IPort {
			var nd:NodeData = mapper.nodeDataMapper.lookupValue(sourceID) as NodeData;
			if (!nd && (sourceNodeType == NodeData.NODE_TYPE_ROLE)) {
				var node:INode = helper.addNewNode(FlowMapStyles.roleNodeStyle, sourceID) ;
				nd = new RoleNodeData(node, sourceID) ;
				mapper.nodeDataMapper.mapValue(sourceID, nd) ;
				helper.addNewNodeLabel(node, sourceNodeLabel, ExteriorLabelModel.south, FlowMapStyles.roleLabelStyle, sourceID, LabelData.LABEL_TYPE_ROLE) ;
			}
			
			if (nd) {
				var port:IPort = null ;
				var rect:IRectangle = nd.node.layout ;
				if (nd is RoleNodeData) {
					port = helper.getPortByType(nd.node, PortType.PORT_TYPE_ROLE_OUTGOING) ;
					if (!port)
						port = helper.addNewPort(nd.node, rect.x + rect.width + 5, rect.y, PortType.PORT_TYPE_ROLE_OUTGOING, sourceID) ;
				}
				else if (nd is RepositoryNodeData) {
					port = helper.getPortByType(nd.node, PortType.PORT_TYPE_REPOSITORY_OUTGOING) ;
					if (!port)
						port = helper.addNewPort(nd.node, rect.x + rect.width + 5, rect.y, PortType.PORT_TYPE_REPOSITORY_OUTGOING, sourceID) ;
				}
			}
			
			return port ;
		}
				
		private function ensureSharingNeedTargetExists(targetID:String, targetNodeType:String):IPort {
			var nd:NodeData = mapper.nodeDataMapper.lookupValue(targetID) as NodeData;
			if (!nd && (targetNodeType == NodeData.NODE_TYPE_ROLE)) {
				var node:INode = helper.addNewNode(FlowMapStyles.roleNodeStyle, targetID) ;
				nd = new RoleNodeData(node, targetID) ;
				mapper.nodeDataMapper.mapValue(targetID, nd) ;
			}
			
			if (nd) {
				var port:IPort = null ;
				var rect:IRectangle = nd.node.layout ;
				if (nd is RoleNodeData) {
					port = helper.getPortByType(nd.node, PortType.PORT_TYPE_ROLE_INCOMING) ;
					if (!port)
						port = helper.addNewPort(nd.node, rect.x - rect.width - 5, rect.y, PortType.PORT_TYPE_ROLE_INCOMING, targetID) ;
				}
				else if (nd is RepositoryNodeData) {
					port = helper.getPortByType(nd.node, PortType.PORT_TYPE_REPOSITORY_INCOMING) ;
					if (!port)
						port = helper.addNewPort(nd.node, rect.x - rect.width - 5, rect.y, PortType.PORT_TYPE_REPOSITORY_INCOMING, targetID) ;
				}
			}
			
			return port as IPort ;
		}
		
		public function addSharingNeed(elemID:String, aboutLabelText:String, what:Array, 
			sourceID:String, sourceNodeType:String, targetID:String, targetNodeType:String, sourceNodeLabel:String=null):void {
			var snnd:SharingNeedNodeData = mapper.nodeDataMapper.lookupValue(elemID) as SharingNeedNodeData ;
			if (snnd) {
				removeSharingNeed(elemID) ;
				addSharingNeed(elemID, aboutLabelText, what, sourceID, sourceNodeType, targetID, targetNodeType, sourceNodeLabel) ;
				return ;
			}
			
			var sourcePort:IPort = ensureSharingNeedSourceExists(sourceID, sourceNodeType, sourceNodeLabel) ;
			if (!sourcePort)
				return ;
			
			var targetPort:IPort = ensureSharingNeedTargetExists(targetID, targetNodeType) ;
			if (!targetPort)
				return ;
						
			// Find a place to add the event node
			var node:INode = helper.addNewNode(FlowMapStyles.sharingNeedNodeStyle,
												elemID) ;
			
			// Attach node data
			snnd = new SharingNeedNodeData(node, elemID) ;
			mapper.nodeDataMapper.mapValue(elemID, snnd) ;
	
			// Add About label
			var aboutLabel:ILabel = helper.addNewNodeLabel(node, aboutLabelText, 
													FlowMapStyles.sharingNeedAboutLabelModelParameter, 
													FlowMapStyles.sharingNeedAboutLabelStyle,
													elemID, LabelData.LABEL_TYPE_SHARING_NEED_ABOUT) ;
	
			// Add What label
			var whatLabel:ILabel = helper.addNewNodeLabel(node, what.join("\n"), 
													FlowMapStyles.sharingNeedWhatLabelModelParameter, 
													FlowMapStyles.sharingNeedWhatLabelStyle,
													elemID, LabelData.LABEL_TYPE_SHARING_NEED_WHAT) ;
			
			// Adjust node size to fit labels
			FlowMapLayoutHelper.updateNodeBounds(graph, node) ;
			
			// Add ports
			var rect:IRectangle = node.layout ;
			var sharingIncomingPort:IPort = helper.addNewPort(node, rect.x + rect.width + 5, rect.y + rect.height/2, PortType.PORT_TYPE_SHARING_NEED_OUTGOING, elemID) ;
			var sharingOutgoingPort:IPort = helper.addNewPort(node, rect.x - rect.width - 5 , rect.y + rect.height/2, PortType.PORT_TYPE_SHARING_NEED_INCOMING, elemID) ;
			
			// Create the edges
			var edge:IEdge ;
			edge = graph.createEdge(sourcePort, sharingIncomingPort, FlowMapStyles.edgeStyle) ;
			graph.addLabel(edge, 'knows') ;
			edge = graph.createEdge(sharingOutgoingPort, targetPort, FlowMapStyles.edgeStyle) ;
			graph.addLabel(edge, 'needs to know') ;
		}
		
		public function removeSharingNeed(elemID:String):void {
			// First make sure node exists and is the right type
			var snnd:SharingNeedNodeData = mapper.nodeDataMapper.lookupValue(elemID) as SharingNeedNodeData ;
			if (!snnd)
				return ;
				
			// Get source and target nodes. If they are role nodes, remove them.
			var snd:NodeData = mapper.nodeDataMapper.lookupValue(snnd.sourceID) as NodeData ;
			var edgeIter:Iterator ;
			if (snd is RoleNodeData) {
				edgeIter = graph.edgesAtPortOwner(snd.node).iterator() ;
				edgeIter.next() ;
				if (!edgeIter.hasNext())
					helper.removeNode(snd.id) ;
			}
			
			var tnd:NodeData = mapper.nodeDataMapper.lookupValue(snnd.targetID) as NodeData ;
			if (tnd is RoleNodeData) {
				edgeIter = graph.edgesAtPortOwner(tnd.node).iterator() ;
				edgeIter.next() ;
				if (!edgeIter.hasNext())
					helper.removeNode(tnd.id) ;
			}
			
			// Remove the sharing need node
			helper.removeNode(elemID) ;
		}
	}
}