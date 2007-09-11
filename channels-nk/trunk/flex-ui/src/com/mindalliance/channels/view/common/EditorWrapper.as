package com.mindalliance.channels.view.common
{
	import flash.display.DisplayObject;
	
	import mx.containers.Form;

	public class EditorWrapper extends Form
	{
	   private var _editor : IEditor;
		public function set editor(editor : IEditor) : void {
			this._editor = editor;
		
            this.addChild((editor as DisplayObject));	
		}
		
		public function get editor() : IEditor {	
			return _editor;
	    }
	}
}