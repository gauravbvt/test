package com.mindalliance.channels.vo.common
{
	[Bindable]
	public class Knowable
	{
		public static const TYPES : Array = [
                   {name: "acquirement", display: "Acquirement"},
                   {name: "artifact", display: "Artifact"},
                   {name: "event", display: "Event"},
                   {name: "task", display: "Task"}];
                   
                   
        public static function getAcquirementType(id : String) : Knowable {
            return new Knowable("acquirement", id);
            
        }
        public static function getArtifactType(id : String) : Knowable {
            return new Knowable("artifact", id);
            
        }
        public static function getEventType(id : String) : Knowable {
            return new Knowable("event", id);
            
        }
        public static function getTaskType(id : String) : Knowable {
            return new Knowable("task", id);
            
        }                   
		public function Knowable(type : String, id : String) {
          this.id = id; 
          this.type = type; 
        }
        
        private var _id : String;
        private var _type : String;
        
        public function get id() : String {
          return _id;
        }

        public function set id(id : String) : void {
          _id=id;
        }
        
        public function get type() : String {
          return _type;
        }

        public function set type(type : String) : void {
          _type=type;
        }
	}
}