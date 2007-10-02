package com.mindalliance.channels.view.flowmap.data
{
	public class FlowMapElementType
	{
		public static const TASK:FlowMapElementType = new FlowMapElementType("task") ;

		public static const EVENT:FlowMapElementType = new FlowMapElementType("event") ;

		public static const ROLE:FlowMapElementType = new FlowMapElementType("role") ;

		public static const SHARING_NEED:FlowMapElementType = new FlowMapElementType("sharingneed") ;
		
		public static const PHASE:FlowMapElementType = new FlowMapElementType("scenariostage") ;
		
		private var _type:String ;
		public function FlowMapElementType(type:String) {
			this._type = type ;
		}
		
		public function get type():String {
			return this._type ;
		}
		
	}
}