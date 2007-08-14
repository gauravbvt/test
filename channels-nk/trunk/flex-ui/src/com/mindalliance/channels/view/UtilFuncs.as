package com.mindalliance.channels.view
{
    import com.mindalliance.channels.view.common.InputTextDialog ;
    
    import flash.display.DisplayObject;
    
    import mx.managers.PopUpManager;
    
    public class UtilFuncs
    {
        
        public static function GetUserTextInput(parent:DisplayObject, callback:Function, titleText:String, modal:Boolean=false):void {
	                var inputDialog:InputTextDialog = InputTextDialog(PopUpManager.createPopUp(parent, InputTextDialog, modal)) ;
    	            inputDialog.title = titleText ;
    	            inputDialog.okEventHandler = callback ;
    	            PopUpManager.centerPopUp(inputDialog);
    	            inputDialog.textInput.setFocus();
	    }
	    
	    public static function Capitalize(text:String):String {
	    	var arr:Array = text.split(' ') ;
	    	for (var i:int ; i < arr.length ; ++i) {
	    		var full:String = arr[i] ;
		    	var rest:String = '' ;
	    		if (full.length > 1)
	    			rest = full.substring(1) ;
	    		arr[i] = full.substr(0, 1).toUpperCase() + rest ;
	    	}
	    	return arr.join(' ') ;
	    }
    }
}