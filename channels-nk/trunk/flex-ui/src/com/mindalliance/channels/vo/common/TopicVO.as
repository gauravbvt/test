package com.mindalliance.channels.vo.common
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
    [Bindable]
	public class TopicVO implements IValueObject
	{
		
		public function TopicVO(name : String,
								description : String,
								confidence : String,
								privacy : ArrayCollection,
								eoi : ArrayCollection) {
			this.name = name;
			this.description = description;
			this.confidence = confidence;
			this.privacy = privacy;
			this.eoi = eoi;							
		}
		private var _name : String;
		private var _description : String;
		private var _confidence : String;
		private var _privacy : ArrayCollection;
		private var _eoi : ArrayCollection;
		
		
		public function get name() : String {
			return _name;
		}

		public function set name(name : String) : void {
			_name=name;
		}
		
		public function get description() : String {
			return _description;
		}

		public function set description(description : String) : void {
			_description=description;
		}
		
		public function get confidence() : String {
			return _confidence;
		}

		public function set confidence(confidence : String) : void {
			_confidence=confidence;
		}
		
		public function get privacy() : ArrayCollection {
			return _privacy;
		}

		public function set privacy(privacy : ArrayCollection) : void {
			_privacy=privacy;
		}
		
		public function get eoi() : ArrayCollection {
			return _eoi;
		}

		public function set eoi(eoi : ArrayCollection) : void {
			_eoi=eoi;
		}
		
		
	}
}