package com.mindalliance.channels.model
{
    import com.mindalliance.channels.vo.common.ElementVO;
    
    import mx.collections.ArrayCollection;
	[Bindable]
	public class ElementListModel
	{
		private var _dirty : Boolean = false;
        private var _data : ArrayCollection;
        
        public function get dirty() : Boolean {
            return _dirty;
        }

        public function set dirty(dirty : Boolean) : void {
            _dirty=dirty;
        }
        
        
        public function get data() : ArrayCollection {
        	return _data;
        }

        public function set data(data : ArrayCollection) : void {
        	_data=data;
        }
        
        
	}
}