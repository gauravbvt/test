// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class CauseVO implements IValueObject
	{
		private var type : String;
		private var id : String;
		private var name : String;

		public function CauseVO( type : String, id : String, name : String ) {
			this.type = type;
			this.id = id; 
			this.name = name;
		}
		
		public function get type() : String {
			return _type;
		};

		public function set type(type : String) : void {
			_type=type;
		}
		
		public function get id() : String {
			return _id;
		};

		public function set id(id : String) : void {
			_id=id;
		}
		
		public function get name() : String {
			return _name;
		};

		public function set name(name : String) : void {
			_name=name;
		}
		
		/**
		 * Produces XML of the form:
		 * 
		 * <cause>
		 *   <type>{independent | event | task}</type>
		 *   <id>{causeId}</id> <!-- if type == event or task -->
		 * </cause>
		 */
		public function toXML() : XML {
			var causeXML : XML =  <cause>
						<type>{type}</type>
					</cause>;
			if (type != "independent") {
				causeXML.appendChild(<id>{id}</id>
				<name>{name}</name>);
			}
			return causeXML;
			
		}

		/**
		 * Expects XML of the form:
		 * <cause>
		 *   <type>{independent | event | task}</type>
		 *   <id>{causeId}</id><!-- if type == event or task -->
		 *   <name>{cause name}</name><!-- if type == event or task -->
		 * </cause>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new CauseVO(obj.type, obj.id, obj.name);
		}
	}
}