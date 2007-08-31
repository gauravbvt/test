package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.IMapperRegistry;
	import com.yworks.graph.model.INode;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.IMapper;
	import com.yworks.support.Iterator;
	
	public class GraphMapperHelper
	{
		
		/*
		 	Keys used to lookup the appropriate mappers from the mapper registry.
		 */
		public static const KEY_NODE_ID:String = "NodeByID" ;
		
		public static const KEY_TASK_ID:String = "TaskByID" ;
	
		public static const KEY_EVENT_ID:String = "EventByID" ;
		
		public static const KEY_ROLE_ID:String = "RoleByID" ;
		
		public static const KEY_ID_INSTANCE:String = "ItemIDByInstance" ;
		
		public static const KEY_LABEL_TYPE:String = "LabelType" ;
		
		public static const KEY_PORT_TYPE:String = "PortType" ;
		
		public static const KEY_SCENARIO_INSTANCE:String = "ScenarioIDByItem" ;
		
		public static const KEY_SCENARIO_ID:String  = "ScenarioByID" ;
		
		public static const KEY_NODES_SCENARIO_ID:String = "NodesByScenarioID" ;
		
		/*
			Values used to classify the type of each label added to a node. 
		*/
		
		public static const VALUE_LABEL_TYPE_TASK:String = "LabelTypeTask" ;
		
		public static const VALUE_LABEL_TYPE_EVENT:String = "LabelTypeEvent" ;
		
		public static const VALUE_LABEL_TYPE_ROLE:String = "LabelTypeRole" ;
		
		/*
			Values used to classify the type of each port added to a node.
		*/
		
		public static const VALUE_PORT_TYPE_ROLE_INCOMING:String = "PortRoleIncoming" ;
		
		public static const VALUE_PORT_TYPE_TASK_INCOMING:String = "PortTaskIncoming" ;
		
		public static const VALUE_PORT_TYPE_EVENT_OUTGOING:String = "PortEventOutgoing" ;
		
		private static const _INSTANCE:GraphMapperHelper = new GraphMapperHelper() ;
		
		public static function getInstance():GraphMapperHelper {
			return _INSTANCE ;
		}
		
		private var _nodeByIDMapper:IMapper ;
		private var _taskByIDMapper:IMapper ;
		private var _eventByIDMapper:IMapper ;
		private var _roleByIDMapper:IMapper ;
		private var _labelTypeByLabelMapper:IMapper ;
		private var _itemIDByInstanceMapper:IMapper ;
		private var _portTypeByPortMapper:IMapper ;
		private var _scenarioStageByIDMapper:IMapper ;
		private var _scenarioStageIDByItemIDMapper:IMapper ;
		private var _nodesByScenarioStageIDMapper:IMapper ;
		
		public function initialize(mapReg:IMapperRegistry):void {
			mapReg.addMapper(GraphMapperHelper.KEY_NODE_ID, new DictionaryMapper()) ;
			_nodeByIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_NODE_ID) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_TASK_ID, new DictionaryMapper()) ;
			_taskByIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_TASK_ID) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_EVENT_ID, new DictionaryMapper()) ;
			_eventByIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_EVENT_ID) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_ROLE_ID, new DictionaryMapper()) ;
			_roleByIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_ROLE_ID) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_LABEL_TYPE, new DictionaryMapper()) ;
			_labelTypeByLabelMapper = mapReg.getMapper(GraphMapperHelper.KEY_LABEL_TYPE) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_ID_INSTANCE, new DictionaryMapper()) ;
			_itemIDByInstanceMapper = mapReg.getMapper(GraphMapperHelper.KEY_ID_INSTANCE) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_PORT_TYPE, new DictionaryMapper()) ;
			_portTypeByPortMapper = mapReg.getMapper(GraphMapperHelper.KEY_PORT_TYPE) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_SCENARIO_ID, new DictionaryMapper()) ;
			_scenarioStageByIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_SCENARIO_ID) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_SCENARIO_INSTANCE, new DictionaryMapper()) ;
			_scenarioStageIDByItemIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_SCENARIO_INSTANCE) ;
			
			mapReg.addMapper(GraphMapperHelper.KEY_NODES_SCENARIO_ID, new DictionaryMapper()) ;
			_nodesByScenarioStageIDMapper = mapReg.getMapper(GraphMapperHelper.KEY_NODES_SCENARIO_ID) ;
		}
		
		public function removePortMappings(node:INode):void {
			var iter:Iterator = node.ports.iterator() ;
			while (iter.hasNext())
				_portTypeByPortMapper.unMapValue(iter.next()) ;
		}
		
		public function removeLabelMappings(node:INode):void {
			var iter:Iterator = node.labels.iterator() ;
			while (iter.hasNext()) {
				var label:ILabel = ILabel(iter.next()) ;
				var id:Object = _itemIDByInstanceMapper.lookupValue(label) ;
				_itemIDByInstanceMapper.unMapValue(label) ;
				switch (_labelTypeByLabelMapper.lookupValue(label)) {
					case GraphMapperHelper.VALUE_LABEL_TYPE_ROLE:
						_roleByIDMapper.unMapValue(id) ;
					break ;
					case GraphMapperHelper.VALUE_LABEL_TYPE_TASK:
						_taskByIDMapper.unMapValue(id) ;
					break ;
					case GraphMapperHelper.VALUE_LABEL_TYPE_EVENT:
						_eventByIDMapper.unMapValue(id) ;
					break ;
				}
			}
		}
		
		public function get nodeByIDMapper():IMapper {
			return _nodeByIDMapper ;
		}
		
		public function get taskByIDMapper():IMapper {
			return _taskByIDMapper ;
		}
		
		public function get eventByIDMapper():IMapper {
			return _eventByIDMapper ; 
		}
		
		public function get roleByIDMapper():IMapper {
			return _roleByIDMapper ;
		}
		
		public function get labelTypeByLabelMapper():IMapper {
			return _labelTypeByLabelMapper ;
		}
		
		public function get itemIDByInstanceMapper():IMapper {
			return _itemIDByInstanceMapper ;
		}
		
		public function get portTypeByPortMapper():IMapper {
			return _portTypeByPortMapper ;
		}
		
		public function get scenarioStageByIDMapper():IMapper {
			return _scenarioStageByIDMapper ;
		}
		
		public function get scenarioStageIDByItemIDMapper():IMapper {
			return _scenarioStageIDByItemIDMapper ;
		}
		
		public function get nodesByScenarioStageIDMapper():IMapper {
			return _nodesByScenarioStageIDMapper ;
		}
	}
}