// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.ProductVO;

    [Bindable]
	public class AcquirementVO extends ProductVO implements IValueObject
	{
		
		public function AcquirementVO( id : String, 
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ElementVO,
                                    information : InformationVO) {
            super(id,name,description,categories,product);
            this.information = information;   
		}
		
		private var _information : InformationVO;
		
		public function get information() : InformationVO {
            return _information;
        }

        public function set information(information : InformationVO) : void {
            _information=information;
        }
        
	}
}