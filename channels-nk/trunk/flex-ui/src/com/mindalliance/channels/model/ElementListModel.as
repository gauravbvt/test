package com.mindalliance.channels.model
{
    import mx.collections.ArrayCollection;
	[Bindable]
	public class ElementListModel
	{
		private var _dirty : Boolean = false;
        private var _data : ArrayCollection = new ArrayCollection();
        
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
        	if (data != null) {
        	   _data.source = data.source;
        	} else {
        	   _data.source = new Array();	
        	}
        }
        
        
	}
}