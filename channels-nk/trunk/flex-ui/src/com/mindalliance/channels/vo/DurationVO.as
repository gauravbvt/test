// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class DurationVO implements IValueObject
	{
		public function DurationVO(  ) {
			
		}

		private var _taskBased : Boolean;
		private var _length : int;
		private var _unit : String;
		private var _tasks : ArrayCollection;

		
		public function get taskBased() : Boolean {
			return _taskBased;
		};

		public function set taskBased(taskBased : Boolean) : void {
			_taskBased=taskBased;
		}
		
		public function get length() : int {
			return _length;
		};

		public function set length(length : int) : void {
			_length=length;
		}
		
		public function get unit() : String {
			return _unit;
		};

		public function set unit(unit : String) : void {
			_unit=unit;
		}
		
		public function get tasks() : ArrayCollection {
			return _tasks;
		};

		public function set tasks(tasks : ArrayCollection) : void {
			_tasks=tasks;
		}
		

		/**
		 * Produces XML of the form:
		 * 
		 * <duration>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </duration>
		 */
		public function toXML() : XML {
			var xml : XML =  <duration></duration>;
			if (_taskBased) {
				var taskXML : XML = <tasks></tasks>;
				for each (var task in _tasks) {
					categoriesXML.appendChild(<taskId>{task.id}</taskId>);
				}
				xml.appendChild(taskXML);
			} else {
				xml.appendChild(<length>{_length}</length>);
				xml.appendChild(<unit>{_unit}</unit>);
			}
			return xml;
		}

		/**
		 * Expects XML of the form:
		 * <duration>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </duration>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
			if (obj.taskBased == true) {
				return new DurationVO(true, 0, null, ElementVO.getXMLList("task", obj.tasks));
			} else {
				return new DurationVO(false, obj.length, obj.unit, new ArrayCollection());
			}
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <duration>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </duration>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("duration", obj);
		}
	}
}