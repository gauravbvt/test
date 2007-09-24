package com.mindalliance.channels.vo.common
{
	
	
	public class SourceOrSink
	{
	   
	   public static const TYPES : Array = [
	               {name: "agent", display: "Agent"},
	               {name: "repository", display: "Repository"},
	               {name: "role", display: "Role"}];

	   
		public static function getAgentType(id : String): SourceOrSink {
		  return new SourceOrSink("agent", id);	
		}
		
		public static function getRepositoryType(id : String) : SourceOrSink {
		  return new SourceOrSink("repository", id);	
		}
		
		public static function getRoleType(id : String) : SourceOrSink {
			return new SourceOrSink("role", id);
			
		}
		
		public function SourceOrSink(type : String, id : String) {
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