// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.categories
{
	import com.mindalliance.channels.business.common.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.CategoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class DisciplineDelegate extends BaseDelegate
	{	
		public function DisciplineDelegate(responder:IResponder)
		{
			super(responder);
			typeName="discipline";
		}

        
        public function getDisciplineList(taxonomy : String) : void {
            var request:Array = new Array();
            request["taxonomy"] = taxonomy;
            performQuery("disciplinesInTaxonomy", request);
        }
	}
}