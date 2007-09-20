package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.IMapperRegistry;
	import com.yworks.graph.model.INode;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.IMapper;
	import com.yworks.support.Iterator;
	import com.yworks.graph.model.IPort;
	import mx.collections.ArrayCollection;
	import com.yworks.graph.model.DefaultNode;
	
	public class GraphMapperHelper
	{
				
		/*
		 	Keys used to lookup the appropriate mappers from the mapper registry.
		 */
		private static const NODE_DATA_MAPPER:String = "NodeDataMapper" ;
		
		private static const LABEL_DATA_MAPPER:String = "LabelDataMapper" ;
		
		private static const PORT_TYPE_MAPPER:String = "PortTypeMapper" ;
		
		private static const ID_MAPPER:String = "IDMapper" ;
		
		private static const EDGE_TYPE_MAPPER:String = "EdgeTypeMapper" ;
		
		private static const _INSTANCE:GraphMapperHelper = new GraphMapperHelper() ;
		
		public static function getInstance():GraphMapperHelper {
			return _INSTANCE ;
		}
		
		private var _nodeDataMapper:IMapper ;
		private var _labelDataMapper:IMapper ;
		private var _portTypeMapper:IMapper ;
		private var _phaseMapper:IMapper ;
		private var _idMapper:IMapper ;
		private var _edgeTypeMapper:IMapper ;
		
		public function initialize(mapReg:IMapperRegistry):void {
			mapReg.addMapper(GraphMapperHelper.NODE_DATA_MAPPER, new DictionaryMapper()) ;
			_nodeDataMapper = mapReg.getMapper(GraphMapperHelper.NODE_DATA_MAPPER) ;
			
			mapReg.addMapper(GraphMapperHelper.LABEL_DATA_MAPPER, new DictionaryMapper()) ;
			_labelDataMapper = mapReg.getMapper(GraphMapperHelper.LABEL_DATA_MAPPER) ;
			
			mapReg.addMapper(GraphMapperHelper.PORT_TYPE_MAPPER, new DictionaryMapper()) ;
			_portTypeMapper = mapReg.getMapper(GraphMapperHelper.PORT_TYPE_MAPPER) ;
			
			mapReg.addMapper(GraphMapperHelper.ID_MAPPER, new DictionaryMapper()) ;
			_idMapper = mapReg.getMapper(GraphMapperHelper.ID_MAPPER) ;
			
			mapReg.addMapper(GraphMapperHelper.EDGE_TYPE_MAPPER, new DictionaryMapper()) ;
			_edgeTypeMapper = mapReg.getMapper(GraphMapperHelper.EDGE_TYPE_MAPPER) ;
		}
		
		public function get edgeTypeMapper():IMapper {
			return _edgeTypeMapper ;
		}
		
		public function get idMapper():IMapper {
			return _idMapper ;
		}
		
		public function get nodeDataMapper():IMapper {
			return _nodeDataMapper ;
		}
		
		public function get labelDataMapper():IMapper {
			return _labelDataMapper ;
		}
		
		public function get portTypeMapper():IMapper {
			return _portTypeMapper ;
		}
	}
}