// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class OccurrenceVO extends ArtifactVO implements IValueObject
	{
		public function OccurrenceVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection,
								information : ArrayCollection,
								cause : CauseVO,
								duration : Integer,
								durationUnit : String) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.categories =categories;
			this.information = information;
			_cause = cause;
			_duration =duration;
			_durationUnit =durationUnit;
		}

		private var _cause : CauseVO;
		private var _duration : Integer;
		private var _durationUnit : String;

		public function get cause() : CauseVO {
			return _cause;
		};

		public function set cause(cause : CauseVO) : void {
			_cause=cause;
		}

		public function get duration() : Integer {
			return _duration;
		}

		public function set duration(duration : Integer) : void {
			_duration=duration;
		}
		
		public function get durationUnit() : String{
			return _durationUnit;
		}

		public function set durationUnit(durationUnit : String) : void {
			_durationUnit=durationUnit;
		}
		/**
		 * Produces XML of the form:
		 * 
		 * <Occurrence>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </Occurrence>
		 */
		public function toXML() : XML {
			return <occurrence>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</ccurrence>;
		}

		/**
		 * Expects XML of the form:
		 * <Occurrence>
		 *   <id>{id}</id>
		 *   <name>{name}</name>
		 *   <description>{description}</description>
		 * </Occurrence>
		 */
		public static function fromXML( obj : Object ) : ProjectVO {
				return new OccurrenceVO(obj.id, obj.name, obj.description);
		}
		
		/**
		 * Produces a list from XML of the form:
		 * 
		 * <list>
		 *   <Occurrence>
		 *     <id>{id}</id>
		 *     <name>{name}</id>
		 *   </Occurrence>
		 *   ...
		 * </list>
		 * 
		 */
		public static function fromXMLList( obj : Object ) : ArrayCollection {
			return ElementVO.fromXMLList("Occurrence", obj);
		}
	}
}