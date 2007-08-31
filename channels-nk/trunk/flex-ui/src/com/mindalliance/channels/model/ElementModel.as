package com.mindalliance.channels.model
{
    import com.mindalliance.channels.vo.common.ElementVO;
	[Bindable]
	public class ElementModel
	{
		private var _dirty : Boolean = false;
        private var _data : ElementVO;
        
        public function get dirty() : Boolean {
            return _dirty;
        }

        public function set dirty(dirty : Boolean) : void {
            _dirty=dirty;
        }
        
        
        public function get data() : ElementVO {
        	return _data;
        }

        public function set data(data : ElementVO) : void {
        	_data=data;
        }
        
        
	}
}