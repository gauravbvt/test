package com.mindalliance.channels.model
{
	[Bindable]
	public class BaseChannelsModel 
	{
		public function BaseChannelsModel(elements : Object, elementLists : Object) {
		  this._elements = elements;
		  this._elementLists = elementLists;	
		}
		
		private var _elements : Object;
		private var _elementLists : Object;
		      
        public function getElementModel(id : String) : ElementModel {
            if (_elements[id] == null) {
                _elements[id] = new ElementModel();
            }
            return _elements[id];
        }
        public function getElementListModel(key : String) : ElementListModel {
            if (_elementLists[key] == null) {
                _elementLists[key] = new ElementListModel();
            }
            return _elementLists[key];
        }   
		
		public function get elements() : Object {
			return _elements;
		}

		public function set elements(elements : Object) : void {
			_elements=elements;
		}
		
		public function get elementLists() : Object {
			return _elementLists;
		}

		public function set elementLists(elementLists : Object) : void {
			_elementLists=elementLists;
		}
		
	}
}