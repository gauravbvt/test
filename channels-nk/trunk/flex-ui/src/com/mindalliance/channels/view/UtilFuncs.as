package com.mindalliance.channels.view
{
    import com.mindalliance.channels.view.common.Chooser;
    import com.mindalliance.channels.view.common.InputTextDialog;
    import com.mindalliance.channels.vo.common.ElementVO;
    
    import flash.display.DisplayObject;
    import flash.geom.Point;
    
    import mx.collections.ArrayCollection;
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
	    
	    public static function copyArrayCollection(array : ArrayCollection) : ArrayCollection {
                var result : ArrayCollection;
                if (array != null) {    
                    result = new ArrayCollection;
                    for (var i:uint = 0 ; i< array.length ; i++) {
                       result.addItem(array.getItemAt(i));  
                    }
                }
                return result;
        }
        public static function popupChooser(type : Class, parent : DisplayObject, filter : ArrayCollection, updateFunction : Function = null) : void {

            var rc:Chooser = (PopUpManager.createPopUp(parent,type,false) as Chooser);
            rc.filtered=filter;
            rc.processSelected=function(selected : ArrayCollection) : void {
                for each(var el : ElementVO in selected) {
                    filter.addItem(el); 
                    if (updateFunction != null)  
                        updateFunction();
                }
            }       
            var p:Point = new Point(parent.x, parent.y) ;
            p = parent.localToGlobal(p) ;
            rc.x = p.x + parent.width + 5 ;
            rc.y = p.y ;
        }
        
    }
    
    
}