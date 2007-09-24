package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.DurationVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.Knowable;
	import com.mindalliance.channels.vo.common.SourceOrSink;
    [Bindable]
	public class NeedToKnowVO extends KnowVO implements IValueObject
	{
		public function NeedToKnowVO(id : String,
                              who : SourceOrSink,
                              about : Knowable,
                              what : InformationVO,
                              criticality : String,
                              urgency : DurationVO,
                              deliveryMode : String,
                              updateOnChange : Boolean,
                              updateEvery : DurationVO,
                              format : CategorySetVO) {
            super(id,who,about,what);
            this.criticality = criticality;
            this.urgency = urgency;
            this.deliveryMode = deliveryMode;
            this.updateOnChange = updateOnChange;
            this.updateEvery = updateEvery;
            this.format = format;
                              	
        }
		
		private var _criticality : String;
		private var _urgency : DurationVO;
		private var _deliveryMode : String;
		private var _updateOnChange : Boolean;
		private var _updateEvery : DurationVO;
		private var _format : CategorySetVO;
		
		public function get criticality() : String {
			return _criticality;
		}

		public function set criticality(criticality : String) : void {
			_criticality=criticality;
		}
		
		public function get urgency() : DurationVO {
			return _urgency;
		}

		public function set urgency(urgency : DurationVO) : void {
			_urgency=urgency;
		}
		
		public function get deliveryMode() : String {
			
			return _deliveryMode;
		}

		public function set deliveryMode(deliveryMode : String) : void {
			_deliveryMode=deliveryMode;
		}
		
		public function get updateOnChange() : Boolean {
			return _updateOnChange;
		}

		public function set updateOnChange(updateOnChange : Boolean) : void {
			_updateOnChange=updateOnChange;
		}
		
		public function get updateEvery() : DurationVO {
			return _updateEvery;
		}

		public function set updateEvery(updateEvery : DurationVO) : void {
			_updateEvery=updateEvery;
		}
		
		
		public function get format() : CategorySetVO {
			return _format;
		}

		public function set format(format : CategorySetVO) : void {
			_format=format;
		}
		
		
	}
}