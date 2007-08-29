// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import mx.collections.ArrayCollection;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class RoleVO extends CategorizedElementVO implements IValueObject
	{
		public function RoleVO( id : String, 
								name : String, 
								description : String,
								categories : ArrayCollection, 
								organization : ElementVO,
								expertise : ArrayCollection) {
			super(id,name,description,categories);
			this.organization = organization;
			this.expertise = expertise;
		}
		
		private var _organization : ElementVO;
		private var _expertise : ArrayCollection;
		
		public function get organization() : ElementVO {
			return _organization;
		}
		
		public function set organization(organization: ElementVO) : void {
			this._organization = organization;	
		}
		
		public function get expertise() : ArrayCollection {
			return _expertise;
		}
		
		public function set expertise(expertise: ArrayCollection) : void {
			this._expertise = expertise;	
		}
	}
}