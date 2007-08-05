package com.mindalliance
{
    import com.mindalliance.ui.common.InputTextDialog;
    
    import flash.display.DisplayObject;
    
    import mx.collections.ArrayCollection;
    import mx.managers.PopUpManager;
    import mx.utils.ObjectProxy;
    
    public class UtilFuncs
    {
        
        public static function GetUserTextInput(parent:DisplayObject, callback:Function, titleText:String, modal:Boolean=false):void {
	                var inputDialog:InputTextDialog = InputTextDialog(PopUpManager.createPopUp(parent, InputTextDialog, modal)) ;
    	            inputDialog.title = titleText ;
    	            inputDialog.okEventHandler = callback ;
    	            PopUpManager.centerPopUp(inputDialog);
    	            inputDialog.textInput.setFocus();
	    }
	        
		public static function convertServiceResults(results : Object) : ArrayCollection {
			if (results == null) {
				return new ArrayCollection();
			} else if (results is ObjectProxy) {
				return new ArrayCollection([results]);
			} else { 
				return results as ArrayCollection;
			}
			
		}
    }
}