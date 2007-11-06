package com.mindalliance.channels.flowmap.view
{
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.mindalliance.channels.flowmap.view.data.PortType;
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
		private var _mapper:GraphDataMapper ;
		public function LimitedPortCandidateProvider(mapper:GraphDataMapper) {
			_mapper = mapper ;
		}
		
		public function getEdgeTargetPortCandidates(graph:IGraph, edge:IEdge):Iterable
		{
			return getCandidateTargetPortCandidates(graph, DefaultPortCandidate.create(edge.sourcePort)) ;
		}
		
		public function getCandidateTargetPortCandidates(graph:IGraph, source:IPortCandidate):Iterable
		{
 			var port:IPort = source.getInstance() ;
			var pt:Object = _mapper.portTypeMapper.lookupValue(port) ;
			var portType:String = String(pt) ;
			switch (portType) {
				case PortType.PORT_TYPE_EVENT_OUTGOING:
				case PortType.PORT_TYPE_TASK_OUTGOING:
 					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[PortType.PORT_TYPE_ROLE_INCOMING, PortType.PORT_TYPE_REPOSITORY_INCOMING], _mapper.portTypeMapper, port.owner) ;
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
			[PortType.PORT_TYPE_EVENT_OUTGOING, PortType.PORT_TYPE_TASK_OUTGOING], _mapper.portTypeMapper, null) ;
			return candidates ;
		}
		
		public function getCandidateSourcePortCandidates(graph:IGraph, target:IPortCandidate):Iterable
		{
			var portType:String = String(_mapper.portTypeMapper.lookupValue(target.getInstance())) ;
			switch (portType) {
				case PortType.PORT_TYPE_ROLE_INCOMING:
				case PortType.PORT_TYPE_REPOSITORY_INCOMING:
					return _getPortCandidatesOfType(graph.ports.iterator(), 
					[PortType.PORT_TYPE_EVENT_OUTGOING, PortType.PORT_TYPE_TASK_OUTGOING], _mapper.portTypeMapper, target.owner) ;
				break ;
			}
			return null ;
		}
		
		public function getGraphTargetPortCandidates(graph:IGraph):Iterable
		{
			return _getPortCandidatesOfType(graph.ports.iterator(), 
			[PortType.PORT_TYPE_ROLE_INCOMING, PortType.PORT_TYPE_REPOSITORY_INCOMING],
			_mapper.portTypeMapper, null) ;
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