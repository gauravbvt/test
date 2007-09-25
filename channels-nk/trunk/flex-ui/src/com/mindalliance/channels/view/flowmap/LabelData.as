package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.model.DefaultLabel;
	import com.yworks.graph.model.ILabel;
	
	public class LabelData
	{
		public static const LABEL_TYPE_TASK:String = "TaskLabel" ;
		
		public static const LABEL_TYPE_EVENT:String = "EventLabel" ;
		
		public static const LABEL_TYPE_ROLE:String = "RoleLabel" ;
		
		public static const LABEL_TYPE_REPOSITORY:String = "RepositoryLabel" ;
		
		public static const LABEL_TYPE_REPOSITORY_OWNER:String = "RepositoryOwnerLabel" ;
		
		public static const LABEL_TYPE_SHARING_NEED_ABOUT:String = "SharingNeedAboutLabel" ;
		
		public static const LABEL_TYPE_SHARING_NEED_WHAT:String = "SharingNeedWhatLabel" ;

		private var _label:ILabel ;
		
		private var _id:String ;
		
		private var _type:String ;
		
		public function LabelData(label:ILabel, id:String, type:String) {
			_id = id ;
			_type = type ;
		}
		
		public function get label():ILabel {
			return _label ;
		}
		
		public function set label(value:ILabel):void {
			_label = value ;
		}
		
		public function get id():String {
			return _id ;
		}
		
		public function set id(value:String):void {
			_id = value ;
		}
		
		public function get type():String {
			return _type ;
		}
		
		public function set type(value:String):void {
			_type = value ;
		}
	}
}