package com.mindalliance.channels.view.flowmap
{
	import com.yworks.canvas.input.MainInputMode;
	import com.yworks.graph.model.DefaultPortCandidate;
	import com.yworks.graph.model.IEdge;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.IPort;
	import com.yworks.graph.model.IPortCandidate;
	import com.yworks.graph.model.IPortCandidateProvider;
	import com.yworks.graph.model.IPortOwner;
	import com.yworks.support.ArrayList;
	import com.yworks.support.IMapper;
	import com.yworks.support.Iterable;
	import com.yworks.support.Iterator;
	
	import mx.collections.ArrayCollection;

	public class LimitedPortCandidateProvider implements IPortCandidateProvider
	{
		private var _mapperHelper:GraphMapperHelper ;
		public function LimitedPortCandidateProvider(mapperHelper:GraphMapperHelper) {
			_mapperHelper = mapperHelper ;
		}
		
		public function getEdgeTargetPortCandidates(graph:IGraph, edge:IEdge):Iterable
		{
			return getCandidateTargetPortCandidates(graph, DefaultPortCandidate.create(edge.sourcePort)) ;
		}
		
		public function getCandidateTargetPortCandidates(graph:IGraph, source:IPortCandidate):Iterable
		{
 			var port:IPort = source.getInstance() ;
			var pt:Object = _mapperHelper.portTypeMapper.lookupValue(port) ;
			var portType:String = String(pt) ;
			switch (portType) {
				case PortType.PORT_TYPE_EVENT_OUTGOING:
				case PortType.PORT_TYPE_TASK_OUTGOING:
 					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[PortType.PORT_TYPE_ROLE_INCOMING, PortType.PORT_TYPE_REPOSITORY_INCOMING], _mapperHelper.portTypeMapper, port.owner) ;
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
			[PortType.PORT_TYPE_EVENT_OUTGOING, PortType.PORT_TYPE_TASK_OUTGOING], _mapperHelper.portTypeMapper, null) ;
			return candidates ;
		}
		
		public function getCandidateSourcePortCandidates(graph:IGraph, target:IPortCandidate):Iterable
		{
			var portType:String = String(_mapperHelper.portTypeMapper.lookupValue(target.getInstance())) ;
			switch (portType) {
				case PortType.PORT_TYPE_ROLE_INCOMING:
				case PortType.PORT_TYPE_REPOSITORY_INCOMING:
					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[PortType.PORT_TYPE_EVENT_OUTGOING, PortType.PORT_TYPE_TASK_OUTGOING], _mapperHelper.portTypeMapper, target.owner) ;
				break ;
			}
			return null ;
		}
		
		public function getGraphTargetPortCandidates(graph:IGraph):Iterable
		{
			return _getPortCandidatesOfType(graph.ports.iterator(), 
			[PortType.PORT_TYPE_ROLE_INCOMING, PortType.PORT_TYPE_REPOSITORY_INCOMING],
			_mapperHelper.portTypeMapper, null) ;
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