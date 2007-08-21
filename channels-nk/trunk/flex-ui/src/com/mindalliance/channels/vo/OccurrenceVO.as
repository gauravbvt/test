// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	
	import mx.collections.ArrayCollection;

	public class OccurrenceVO extends ElementVO implements IValueObject
	{
		public function OccurrenceVO( id : String, 
								name : String, 
								description : String,
								independent : Boolean,
								causeType : String,
								causeId : String,
								duration : Integer,
								durationUnit : String,
								categories : ArrayCollection,
								information : ArrayCollection ) {
			this.id = id;
			this.name = name;
			this.description = description;
			_independent = independent;
			_causeType = causeType;
			_causeId = causeId;
			_duration =duration;
			_durationUnit =durationUnit;
			_categories =categories;
			_information = information
		}

		private var _independent : Boolean;
		private var _causeType : String ;
		private var _causeId : String;
		private var _duration : Integer;
		private var _durationUnit : String;
		private var _categories : ArrayCollection;
		private var _information : ArrayCollection;

		public function get categories() : ArrayCollection {
			return _categories;
		}

		public function set categories(categories : ArrayCollection) : void {
			_categories=categories;
		}
		
		
		public function get information() : ArrayCollection {
			return _information;
		};

		public function set information(information : ArrayCollection) : void {
			_information=information;
		}
		public function get independent() : Boolean {
			return _independent;
		}

		public function set independent(independent : Boolean) : void {
			_independent=independent;
		}
		
		
		public function get causeType() : String {
			return _causeType;
		}

		public function set causeType(causeType : String) : void {
			_causeType=causeType;
		}
		
		
		public function get causeId() : String {
			return _causeId;
		}

		public function set causeId(causeId : String) : void {
			_causeId=causeId;
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
			return <Occurrence>
						<id>{id}</id>
						<name>{name}</name>
						<description>{description}</description>
					</Occurrence>;
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