package com.mindalliance.channels.flowmap.view.delegates
{
	import com.mindalliance.channels.flowmap.view.FlowMapError;
	import com.mindalliance.channels.flowmap.view.FlowMapEvent;
	import com.mindalliance.channels.flowmap.view.GraphHelper;
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.yworks.graph.model.IGraph;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	
	public class BaseDelegate extends EventDispatcher
	{
		
		protected var helper:GraphHelper ;
		
		protected var graph:IGraph ;
		
		protected var mapper:GraphDataMapper ;
		
		public function BaseDelegate(mapperObj:GraphDataMapper, helperObj:GraphHelper, graphObj:IGraph) {
			this.mapper = mapperObj ;	
			this.helper = helperObj ;
			this.graph = graphObj ;
		}
		
		protected function dispatchFlowMapChanged():void {
			dispatchEvent(new Event(FlowMapEvent.FLOWMAP_CHANGED.name)) ;
		}

	}
}