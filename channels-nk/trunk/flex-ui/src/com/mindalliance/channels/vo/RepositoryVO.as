// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorizedElementVO;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	
    [Bindable]
	public class RepositoryVO extends CategorizedElementVO implements IValueObject
	{
		public function RepositoryVO( id : String, 
								name : String, 
								description : String,
								categories : CategorySetVO,
								organization : ElementVO,
								administrators : ArrayCollection,
								contents : ArrayCollection,
								access : ArrayCollection ) {
			super(id,name,description,categories);
			this.organization = organization;
			this.administrators = administrators;
			this.contents = contents;
			this.access = access;
		}

        private var _organization : ElementVO;
        private var _administrators : ArrayCollection;
        private var _contents : ArrayCollection;
        private var _access : ArrayCollection;
	
		
		public function get organization() : ElementVO {
			return _organization;
		}

		public function set organization(organization : ElementVO) : void {
			_organization=organization;
		}
		
		public function get administrators() : ArrayCollection {
			return _administrators;
		}

		public function set administrators(administrators : ArrayCollection) : void {
			_administrators=administrators;
		}
		
		public function get contents() : ArrayCollection {
			return _contents;
		}

		public function set contents(contents : ArrayCollection) : void {
			_contents=contents;
		}
		
		public function get access() : ArrayCollection {
			return _access;
		}

		public function set access(access : ArrayCollection) : void {
			_access=access;
		}
		
	}
}