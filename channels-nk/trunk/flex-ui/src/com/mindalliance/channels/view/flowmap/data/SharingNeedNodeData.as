package com.mindalliance.channels.view.flowmap.data
{
	import com.yworks.graph.model.INode;

	public class SharingNeedNodeData extends NodeData
	{
		public function SharingNeedNodeData(node:INode, id:String)
		{
			super(node, id, NODE_TYPE_SHARING_NEED);
		}
		
		private var _sourceID:String ;
		
		private var _targetID:String ;
		
		public function get sourceID():String {
			return _sourceID;
		}
		
		public function set sourceID(value:String):void {
			_sourceID = value ;
		}
		
		public function get targetID():String {
			return _targetID;
		}
		
		public function set (value:String):void {
			_targetID = value ;
		}
	}
}