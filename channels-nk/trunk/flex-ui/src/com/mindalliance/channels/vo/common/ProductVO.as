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
                                    product : ElementVO
                                 ) {

            super(id, name, description, categories);
            this.product = product;
        }
        
        private var _product : ElementVO;

		
		public function get product() : ElementVO {
			return _product;
		}

		public function set product(product : ElementVO) : void {
			_product=product;
		}
		
	}
}