package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.Knowable;
	import com.mindalliance.channels.vo.common.SourceOrSink;
    [Bindable]
	public class KnowVO extends ElementVO implements IValueObject
	{
		public var _who : SourceOrSink;
		public var _about : Knowable;
		public var _what : InformationVO;
		
		public function KnowVO(id : String,
		                      who : SourceOrSink,
		                      about : Knowable,
		                      what : InformationVO) {
            super(id,null);
            this.who = who;
            this.about = about;
            this.what = what;
		                      	
		}
		
		
		public function get who() : SourceOrSink {
			return _who;
		}

		public function set who(who : SourceOrSink) : void {
			_who=who;
		}
				
		public function get about() : Knowable {
			return _about;
		}

		public function set about(about : Knowable) : void {
			_about=about;
		}

		
		public function get what() : InformationVO {
			return _what;
		}

		public function set what(what : InformationVO) : void {
			_what=what;
		}
		
	}
}