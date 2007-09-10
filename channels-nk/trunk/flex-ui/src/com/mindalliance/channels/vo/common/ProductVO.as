package com.mindalliance.channels.vo.common
{
	import mx.collections.ArrayCollection;
	[Bindable]
	public class ProductVO extends CategorizedElementVO
	{
		public function ProductVO( id : String, 
                                    name : String, 
                                    description : String,
                                    categories : CategorySetVO,
                                    product : ArrayCollection
                                 ) {

            super(id, name, description, categories);
            this.product = product;
        }
        
        private var _product : ArrayCollection;

		
		public function get product() : ArrayCollection {
			return _product;
		}

		public function set product(product : ArrayCollection) : void {
			_product=product;
		}
		
	}
}