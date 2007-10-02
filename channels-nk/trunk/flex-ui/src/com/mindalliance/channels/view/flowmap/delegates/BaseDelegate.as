package com.mindalliance.channels.view.flowmap.delegates
{
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.yworks.graph.model.IGraph;
	
	public class BaseDelegate
	{
		protected var helper:GraphHelper ;
		
		protected var graph:IGraph ;
		
		protected var mapper:GraphDataMapper ;
		
		public function BaseDelegate(mapperObj:GraphDataMapper, helperObj:GraphHelper, graphObj:IGraph) {
			this.mapper = mapperObj ;	
			this.helper = helperObj ;
			this.graph = graphObj ;
		}
	}
}