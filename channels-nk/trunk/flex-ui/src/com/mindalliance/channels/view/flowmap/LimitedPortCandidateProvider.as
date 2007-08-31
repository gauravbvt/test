package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.IPortCandidateProvider;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.IPortCandidate;
	import com.yworks.graph.model.IGraph;
	import com.yworks.support.Iterable;
	import com.yworks.support.IMapper;
	import com.yworks.support.Iterator;
	import com.yworks.support.ArrayList;
	import com.yworks.graph.model.IPort;
	import com.yworks.graph.model.DefaultPortCandidate;
	import mx.collections.ArrayCollection;
	import com.yworks.graph.model.IPortOwner;

	public class LimitedPortCandidateProvider implements IPortCandidateProvider
	{
		public function getEdgeTargetPortCandidates(graph:IGraph, edge:IEdge):Iterable
		{
			return getCandidateTargetPortCandidates(graph, DefaultPortCandidate.create(edge.sourcePort)) ;
		}
		
		public function getCandidateTargetPortCandidates(graph:IGraph, source:IPortCandidate):Iterable
		{
			var mapper:IMapper = graph.mapperRegistry.getMapper(GraphMapperHelper.KEY_PORT_TYPE) ;
 			var port:IPort = source.getInstance() ;
			var pt:Object = mapper.lookupValue(port) ;
			var portType:String = String(pt) ;
			switch (portType) {
				case GraphMapperHelper.VALUE_PORT_TYPE_EVENT_OUTGOING:
 					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[GraphMapperHelper.VALUE_PORT_TYPE_ROLE_INCOMING, 
	 				GraphMapperHelper.VALUE_PORT_TYPE_TASK_INCOMING], mapper, port.owner) ;
				break ;
			}
			return null ;
		}
		
		public function getEdgeSourcePortCandidates(graph:IGraph, edge:IEdge):Iterable
		{
			return getCandidateSourcePortCandidates(graph, DefaultPortCandidate.create(edge.targetPort)) ;
		}
		
		public function getGraphSourcePortCandidates(graph:IGraph):Iterable
		{
			var candidates:Iterable = _getPortCandidatesOfType(graph.ports.iterator(), 
			[GraphMapperHelper.VALUE_PORT_TYPE_EVENT_OUTGOING],
			graph.mapperRegistry.getMapper(GraphMapperHelper.KEY_PORT_TYPE), null) ;
			return candidates ;
		}
		
		public function getCandidateSourcePortCandidates(graph:IGraph, target:IPortCandidate):Iterable
		{
			var mapper:IMapper = graph.mapperRegistry.getMapper(GraphMapperHelper.KEY_PORT_TYPE) ;
			var portType:String = String(mapper.lookupValue(target.getInstance())) ;
			switch (portType) {
				case GraphMapperHelper.VALUE_PORT_TYPE_ROLE_INCOMING: 
				case GraphMapperHelper.VALUE_PORT_TYPE_TASK_INCOMING:
					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[GraphMapperHelper.VALUE_PORT_TYPE_EVENT_OUTGOING], mapper, target.owner) ;
				break ;
			}
			return null ;
		}
		
		public function getGraphTargetPortCandidates(graph:IGraph):Iterable
		{
			trace('Target port candidates requested') ;
			return _getPortCandidatesOfType(graph.ports.iterator(), 
			[GraphMapperHelper.VALUE_PORT_TYPE_ROLE_INCOMING,
			GraphMapperHelper.VALUE_PORT_TYPE_TASK_INCOMING],
			graph.mapperRegistry.getMapper(GraphMapperHelper.KEY_PORT_TYPE), null) ;
		}
		
		private function _getPortCandidatesOfType(ports:Iterator, types:Array, portTypeMapper:IMapper, portOwner:IPortOwner):Iterable {
			var portCandidates:ArrayList = new ArrayList() ;
			while (ports.hasNext()) {
				var p:IPort = IPort(ports.next()) ;
				if (p.owner == portOwner)
					continue ;
				var pt:String = String(portTypeMapper.lookupValue(p)) ;
				for (var i:int=0 ; i < types.length ; i++) {
					if (types[i] == pt) {
						portCandidates.addItem(DefaultPortCandidate.create(p)) ;
						break ;
					}
				}
			}
			return portCandidates ;
		}
		
	}
}