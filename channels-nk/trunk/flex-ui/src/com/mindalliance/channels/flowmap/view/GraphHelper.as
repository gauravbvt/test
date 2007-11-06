package com.mindalliance.channels.flowmap.view
{
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.mindalliance.channels.flowmap.view.data.LabelData;
	import com.mindalliance.channels.flowmap.view.data.NodeData;
	import com.mindalliance.channels.flowmap.view.visualelements.DefaultNodeSelectionPaintable;
	import com.yworks.canvas.geom.IPoint;
	import com.yworks.canvas.geom.ImmutablePoint;
	import com.yworks.graph.drawing.ILabelStyle;
	import com.yworks.graph.drawing.INodeStyle;
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.ILabelModelParameter;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.support.Iterator;
	
	public class GraphHelper
	{
		
		private var _mapper:GraphDataMapper ;
		
		private var _graph:IGraph ;
		
		public function GraphHelper(mapper:GraphDataMapper, graph:IGraph) {
			_mapper = mapper ;
			_graph = graph ;
		}
		
		//////////////////////// LABEL FUNCTIONS /////////////////////////////
		
		public function addNewNodeLabel(node:INode, labelText:String, labelModelParameter:ILabelModelParameter, 
										labelStyle:ILabelStyle, elemID:String, labelType:String):ILabel {
			var label:ILabel = _graph.addLabel(node, labelText, labelModelParameter, labelStyle) ;
			var ld:LabelData = new LabelData(label, elemID, labelType) as LabelData ;
			_mapper.labelDataMapper.mapValue(label, ld) ;
			_mapper.idMapper.mapValue(label, elemID) ;
			return label ;
		}
				
		public function getLabelByType(node:INode, labelType:String):ILabel {
			var iter:Iterator = node.labels.iterator() ;
			while (iter.hasNext()) {
				var label:ILabel = iter.next() as ILabel ;
				var ld:LabelData = _mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == labelType)
					return label ;
			}
			return null ;
		}

		public function unmapAllLabels(node:INode):void {
			var labelIter:Iterator = node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				_mapper.labelDataMapper.unMapValue(label) ;
				_mapper.idMapper.unMapValue(label) ;
			}
		}

		public function removeLabelsByID(labelID:String):void {
			var labelIter:Iterator = _graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				var ld:LabelData = _mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id == labelID) {
					_graph.removeLabel(label) ;
					_mapper.labelDataMapper.unMapValue(label) ;
					_mapper.idMapper.unMapValue(label) ;
				}
			}
		}
		
		////////////////////////////// PORT FUNCTIONS ///////////////////////////////
		
		public function getPortByType(node:INode, portType:String):IPort {
			var portsIter:Iterator = node.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:IPort = portsIter.next() as IPort ;
				if (!port)
					continue ;
				var pType:String = _mapper.portTypeMapper.lookupValue(port) as String ;
				if (!pType)
					continue ;
				if (pType == portType)
					return port ;
			}
			return null ;
		}
		
		public function addNewPort(node:INode, x:Number, y:Number, portType:String, elemID:String):IPort {
			var port:IPort = _graph.addPort(node, x, y) ;
			_mapper.portTypeMapper.mapValue(port, portType) ;
			_mapper.idMapper.mapValue(port, elemID) ;
			return port ;
		}
		
		public function unmapAllPorts(node:INode):void {
			var portsIter:Iterator = node.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:IPort = portsIter.next() as IPort ;
				_mapper.portTypeMapper.unMapValue(port) ;
				_mapper.idMapper.unMapValue(port) ;
			}
		}
		
		public function removePorts(portID:String, portType:String):void {
			var portsIter:Iterator = _graph.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:IPort = portsIter.next() as IPort ;
				var pType:String = _mapper.portTypeMapper.lookupValue(port) as String ;
				var pID:String  = _mapper.idMapper.lookupValue(port) as String ;
				if (pID == portID && pType == portType) {
					_graph.removePort(port) ;
					_mapper.portTypeMapper.unMapValue(port) ;
					_mapper.idMapper.unMapValue(port) ;
				}
			}			
		}
		
		///////////////////////////// NODE FUNCTIONS ///////////////////////////////
		
		public function getNodeDataByID(nodeID:String):NodeData {
			return _mapper.nodeDataMapper.lookupValue(nodeID) as NodeData ;
		}
		
		public function removeNode(nodeID:String):void {
			var nd:NodeData = _mapper.nodeDataMapper.lookupValue(nodeID) as NodeData ;
			if (!nd)
				return ;
			unmapAllLabels(nd.node) ;
			unmapAllPorts(nd.node) ;
			_mapper.nodeDataMapper.unMapValue(nodeID) ;
			_mapper.idMapper.unMapValue(nd.node) ;
			_graph.removeNode(nd.node) ;
		}
		
		public function addNewNode(style:INodeStyle, elemID:String, location:IPoint=null):INode {
			var p:IPoint = location ;
			if (!p)
				p = new ImmutablePoint(0, 0) ;
				
			var node:INode = _graph.createNodeAt(p.x, p.y) as INode ;

			_mapper.idMapper.mapValue(node, elemID) ;

			_graph.setNodeStyle(node, style) ;

			DefaultNodeSelectionPaintable.createAndRegisterFor(node as DefaultNode) ;

			return node ;
		}
		
		
	}
}