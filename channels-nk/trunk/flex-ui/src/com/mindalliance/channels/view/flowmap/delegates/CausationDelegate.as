package com.mindalliance.channels.view.flowmap.delegates {
	import com.mindalliance.channels.view.flowmap.data.EventNodeData;
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.mindalliance.channels.view.flowmap.data.NodeData;
	import com.mindalliance.channels.view.flowmap.data.PortType;
	import com.mindalliance.channels.view.flowmap.data.TaskNodeData;
	import com.mindalliance.channels.view.flowmap.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.support.Iterator;
	
	
	public class CausationDelegate extends BaseDelegate {
		
		public function CausationDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}
		
		public function addCausePort(node:INode, type:String):IPort {
			var rect:IRectangle = node.layout ;
			var port:IPort ;
			switch (type) {
				case PortType.PORT_TYPE_CAUSE_INCOMING:
					port = graph.addPort(node, rect.x, rect.y + rect.height) ;
					mapper.portTypeMapper.mapValue(port, type) ;
				break ;
				case PortType.PORT_TYPE_CAUSE_OUTGOING:
					port = graph.addPort(node, rect.x + rect.width/2, rect.y + rect.height) ;
					mapper.portTypeMapper.mapValue(port, type) ;
				break ;	
			}
			return port ;
		}
		
		public function addCausation(sourceID:String, targetID:String):void {
			// Both nodes must exist before causation can be added 
			var snd:NodeData = mapper.nodeDataMapper.lookupValue(sourceID) as NodeData ;
			if (!snd)
				return ;
			var tnd:NodeData = mapper.nodeDataMapper.lookupValue(targetID) as NodeData ;
			if (!tnd)
				return ;
			
			// Both nodes must be either source or target type
			if (!(snd is TaskNodeData || snd is EventNodeData))
				return ;
			
			if (!(tnd is TaskNodeData || tnd is EventNodeData))
				return ;
			
			// Check if source and target ports already exists. If not, add them.
			var sourcePort:IPort = helper.getPortByType(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
			if (!sourcePort) {
				sourcePort = addCausePort(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
			}
			var targetPort:IPort = helper.getPortByType(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) ;
			if (!targetPort) {
				targetPort = addCausePort(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) ;
			}
			
			// Check if there is already a causal edge between them
			var edgeIter:Iterator = graph.edgesAtPort(sourcePort).iterator() ;
			while (edgeIter.hasNext()) {
				var e:IEdge = edgeIter.next() as IEdge ;
				if (e.targetPort == targetPort)
					return ;
			}
			
			// Add causal edge
			var edge:IEdge = graph.createEdge(sourcePort, targetPort) ;
			graph.addLabel(edge, 'causes') ;
	 	/* 	mapper.edgeTypeMapper.mapValue(edge, EdgeType.EDGE_TYPE_CAUSE) ; */
			graph.setEdgeStyle(edge, FlowMapStyles.causeEdgeStyle) ;
		}
		
		public function removeCausation(sourceID:String, targetID:String):void {
			// Both nodes must exist
			var snd:NodeData = mapper.nodeDataMapper.lookupValue(sourceID) as NodeData ;
			if (!snd)
				return ;
			var tnd:NodeData = mapper.nodeDataMapper.lookupValue(targetID) as NodeData ;
			if (!tnd)
				return ;
				
			// get the source and target ports
			var sourcePort:IPort = helper.getPortByType(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
			if (!sourcePort)
				return ;
			var targetPort:IPort = helper.getPortByType(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) ;
			if (!targetPort)
				return ;
				
			// Find the edge with the required source and target port
			var edgeIter:Iterator = graph.edgesAtPort(sourcePort).iterator() ;
			while (edgeIter.hasNext()) {
				var e:IEdge = edgeIter.next() as IEdge ;
				if (e.targetPort == targetPort) {
					mapper.edgeTypeMapper.unMapValue(e) ;
					graph.removeEdge(e) ;
					return ;
				}
			}
		}
	}
}