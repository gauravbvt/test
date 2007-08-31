package com.mindalliance.channels.model
{
	[Bindable]
	public class EditorModel extends BaseChannelsModel
	{      
	   public function EditorModel(elements : Object, choosers : Object) {
          super(elements,choosers);

        }
        
        private var _isChanged : Boolean;
        private var _id : String;
        
        public function get isChanged() : Boolean {
            return _isChanged;
        }

        public function set isChanged(isChanged : Boolean) : void {
            _isChanged=isChanged;
        }
        
        public function get id() : String {
            return _id;
        }

        public function set id(id : String) : void {
            _id=id;
        }
        
        
	}
}