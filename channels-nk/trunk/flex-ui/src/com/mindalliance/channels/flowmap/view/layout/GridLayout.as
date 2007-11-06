package com.mindalliance.channels.flowmap.view.layout
{
	import com.mindalliance.channels.flowmap.view.FlowMapError;
	import com.mindalliance.channels.flowmap.view.GraphHelper;
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.mindalliance.channels.flowmap.view.data.NodeData;
	import com.mindalliance.channels.flowmap.view.data.PortType;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.Iterator;
	import com.yworks.ui.GraphCanvasComponent;
	
	import flash.utils.Dictionary;
	
	public class GridLayout
	{
		public var leftMargin:Number ;
		
		public var topMargin:Number ;
		
		public var nodeVerticalSpacing:Number ;
		
		public var nodeHorizontalSpacing:Number ;
		
		private var _mapper:GraphDataMapper ;
		
		private var _graph:IGraph ;
		
		private var _helper:GraphHelper ;
		
		private var _graphCanvas:GraphCanvasComponent ;
		
		public function GridLayout(mapper:GraphDataMapper, graph:IGraph, helper:GraphHelper, graphCanvas:GraphCanvasComponent) {
			_mapper = mapper ;
			_graph = graph ;
			_helper = helper ;
			_graphCanvas = graphCanvas ;
		}
		
		public function layout():void {
			var colIndexDict:Dictionary = new Dictionary() ;
			var cols:Array = new Array() ;
		
			var iter:Iterator = (_mapper.nodeDataMapper as DictionaryMapper).values() ;
			while (iter.hasNext()) {
				var nd:NodeData = iter.next() as NodeData ;
				var colIndex:Object = colIndexDict[nd.node] ;
				if (colIndex == null)
					layoutNode(nd.node, colIndexDict, cols) ;
			}
			
			layoutGrid(cols, leftMargin, topMargin) ;

			layoutCausationBends(_graph.edges.iterator()) ;
		}
		
		private function layoutGrid(nodeColumns:Array, left:Number, top:Number):void {
			var leftEdge:Number = left ;
			for each (var column:Array in nodeColumns) {
				Alignment.alignLeft(_graph, column, leftEdge) ;
				Spacing.spaceVertically(_graph, column, nodeVerticalSpacing, top) ; 
				leftEdge = nodeHorizontalSpacing + Alignment.minMax(column, BoundsHelper.getRight)[1] ;
			}			
		}
		
		private function layoutCausationBends(edgeIter:Iterator):void {
			var dev:Number = 0 ;
			
			var bendXDict:Dictionary = new Dictionary() ;
						
			while (edgeIter.hasNext()) {
				var edge:IEdge = edgeIter.next() as IEdge ;
				var sourceNode:INode = edge.sourcePort.owner as INode ;
				var targetNode:INode = edge.targetPort.owner as INode ;
				
				var sourcePort:IPort = _helper.getPortByType(sourceNode, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
				if (!sourcePort)
					continue ;
				
				var targetPort:IPort = _helper.getPortByType(targetNode, PortType.PORT_TYPE_CAUSE_INCOMING) ;
				if (!targetPort)
					continue ;
				
				for (var i:int = edge.bends.length() ; i < 2 ; i++)
					_graph.addBend(edge, i, 0, 0) ;
					
				if (bendXDict[sourceNode] == null) {
					var bx:Number = BoundsHelper.getX(targetNode) - nodeHorizontalSpacing ;
					dev ++ ;
					bendXDict[sourceNode] = bx + 2 * Math.random() * nodeHorizontalSpacing/3 ;
				}
				
				var bendX:Number = bendXDict[sourceNode] as Number ;
				_graph.setBendLocation(edge.bends.bendAt(0), bendX, BoundsHelper.getBottom(sourceNode)) ;
				_graph.setBendLocation(edge.bends.bendAt(1), bendX, BoundsHelper.getBottom(targetNode)) ;
			}
		}
		
		private function addNodeToColumn(column:Array, node:INode, preferredAt:int):void {
			if (_graph.edgesAtPortOwner(node).iterator().hasNext())
				column.splice(preferredAt, 0, node) ;
			else
				column.push(node) ;
		}
		
		private function layoutNode(node:INode, colIndexDict:Dictionary, cols:Object):uint {
				var sourceNodeCol:int = -1 ;

				var sourceNode:INode = findCausingNode(node) ;
				
				// If there is no source node, then this is a first column node 
				if (!sourceNode) {
					if (!cols[0])
						cols[0] = new Array() ;
					addNodeToColumn(cols[0] as Array, node, 0) ;
					colIndexDict[node] = 0 ;
					return 0 ;
				}
				else {
					var val:Object = colIndexDict[sourceNode] ;
					if (val == null)
						sourceNodeCol = layoutNode(sourceNode, colIndexDict, cols) ;
					else
						sourceNodeCol = colIndexDict[sourceNode] ;
				}

				var sourceNodeRow:uint = (cols[sourceNodeCol] as Array).indexOf(sourceNode) ;
				
				if (sourceNodeRow < 0)
					throw new FlowMapError('GridLayout: layoutNode: Unable to find row index of already-laid-out node.') ;
				
				// Column of caused node is column of causing node + 1
				var nodeCol:uint = sourceNodeCol + 1 ;
				if (cols[nodeCol] == null)
					cols[nodeCol] = new Array() ;
				
				// Row of cause node is preferably same or greater than causing node
				// So insert at same horizontal position and let the others
				// be shifted downward
				addNodeToColumn(cols[nodeCol] as Array, node, sourceNodeRow) ;
				colIndexDict[node] = nodeCol ;
				
				return nodeCol ;
		}
		
		private function findCausingNode(node:INode):INode {
			var targetPort:IPort = _helper.getPortByType(node, PortType.PORT_TYPE_CAUSE_INCOMING) ;
			
			if (!targetPort)
				return null ;
			
			var edgeIter:Iterator = _graph.edgesAtPort(targetPort).iterator() ;
			
			if (!edgeIter.hasNext())
				return null ;
			
			return getSourceNode(edgeIter.next() as IEdge) ; 
		}
		
		private function getSourceNode(edge:IEdge):INode {
			var pt:String = _mapper.portTypeMapper.lookupValue(edge.sourcePort) as String ;
			return edge.sourcePort.owner as INode ;
		}
		
	}
}