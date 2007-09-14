package com.mindalliance.channels.view.flowmap
{
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	
	public class Phase extends AbstractPhaseRenderer
	{
				
		private var _nodes:ArrayCollection ;
		
		private var _phaseID:String ;
		
		public function Phase() {
			super() ;
			_nodes = new ArrayCollection() ;
		}
		
		public function get nodes():ArrayCollection {
			return _nodes ;
		}
		
		public function set phaseID(value:String):void {
			_phaseID = value ;
		}
		
		public function get phaseID():String {
			return _phaseID ;
		}
		
		public function set name(value:String):void {
			_name = value ;
 			_widenForText() ;
		}
		
		public function get name():String {
			return _name ;
		}
		
		private static var phases:Dictionary = new Dictionary() ;
		
		private static var _lastID:uint = 0 ;
		
		private static function getNewID():uint {
			_lastID ++ ;
			return _lastID ;
		}
		
		private var _id:uint ;
		
		public function get internalID():uint {
			return this._id ;
		}
		
		public static function createPhase(name:String):Phase {
			var phase:Phase = new Phase() ;
			phase.name = name ;
			var lastPhase:Phase = phases[_lastID] ;
			if (lastPhase != null)
				phase.x = lastPhase.x + lastPhase.width ;
			phase._id = getNewID() ;
			phases[phase._id] = phase ;
			return phase ;
		}
	}
}