package com.mindalliance.channels.model.categories
{
	import com.mindalliance.channels.model.*;
	
	import mx.collections.ArrayCollection;
	[Bindable]
	public class CategoryChooserModel extends  ChooserModel
    {
        public function CategoryChooserModel(elements : Object, choosers : Object, taxonomy : String) {
            super(elements, choosers);
            this.taxonomy = taxonomy;
        }
        public var taxonomy : String;
       
        
	}
}