package com.mindalliance.channels.model.categories
{
	import com.mindalliance.channels.model.*;
	import com.mindalliance.channels.vo.common.CategorySetVO;
	
	import mx.collections.ArrayCollection;
	[Bindable]
	public class CategoryViewerModel extends  EditorModel
    {
    	
    	public var categories : CategorySetVO;
    	public var eois : ArrayCollection;
    	
        public function CategoryViewerModel(elements : Object, choosers : Object) {
            super(elements, choosers);
        }
	}
}