package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
    [Bindable]
	public class InformationVO implements IValueObject
	{
		
		public function InformationVO(topics : ArrayCollection) {
			this.topics = topics;	
		}
		private var _topics : ArrayCollection;
		
		public function get topics() : ArrayCollection {
			return _topics;
		}

		public function set topics(topics : ArrayCollection) : void {
			_topics=topics;
		}
		
	}
}