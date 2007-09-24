package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ElementVO;
    [Bindable]
	public class SharingNeedVO extends ElementVO implements IValueObject
	{
		public function SharingNeedVO(id : String, knowId : String, needToKnowId : String) {
            super(id, null, null);
            this.knowId = knowId;
            this.needToKnowId = needToKnowId;
		}
		
		public var _knowId : String;
		public var _needToKnowId : String;
		
		
		public function get knowId() : String {
			return _knowId;
		}

		public function set knowId(knowId : String) : void {
			_knowId=knowId;
		}
		
		public function get needToKnowId() : String {
			return _needToKnowId;
		}

		public function set needToKnowId(needToKnowId : String) : void {
			_needToKnowId=needToKnowId;
		}
		
	}
}