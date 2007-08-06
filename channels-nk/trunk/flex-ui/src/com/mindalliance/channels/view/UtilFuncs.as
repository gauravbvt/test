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
    }
}