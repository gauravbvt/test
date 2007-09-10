// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	
	import mx.collections.ArrayCollection;

	public class CategoryVO extends ElementVO implements IValueObject
	{
		public function CategoryVO( id : String, 
								name : String, 
								description : String,
								disciplines : ArrayCollection,
								implies : ArrayCollection,
								information : InformationVO ) {
			super(id,name,description);
			this.disciplines = disciplines;
			this.implies=implies;
			this.information = information;
		}
        
        private var _disciplines : ArrayCollection;
        private var _implies : ArrayCollection;
        private var _information : InformationVO;

		
		public function get disciplines() : ArrayCollection {
			return _disciplines;
		}

		public function set disciplines(disciplines : ArrayCollection) : void {
			_disciplines=disciplines;
		}
		
		public function get implies() : ArrayCollection {
			return _implies;
		}

		public function set implies(implies : ArrayCollection) : void {
			_implies=implies;
		}
		
		public function get information() : InformationVO {
			return _information;
		}

		public function set information(information : InformationVO) : void {
			_information=information;
		}
		
	}
}