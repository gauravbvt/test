package com.mindalliance.channels.common.view
{
	import flash.display.DisplayObject;
	
	import mx.containers.Box;

	public class EditorWrapper extends Box
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