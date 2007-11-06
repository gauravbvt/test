package com.mindalliance.channels.util
{
    import com.mindalliance.channels.common.view.Chooser;
    import com.mindalliance.channels.common.view.InputTextDialog;
    import com.mindalliance.channels.vo.common.ElementVO;
    
    import flash.display.DisplayObject;
    import flash.geom.Point;
    
    import mx.collections.ArrayCollection;
    import mx.managers.PopUpManager;
    
    public class ViewUtils
    {
        
        public static function tracef(message:String='', callerClass:Object=null, callerFunction:Object=null):void {
        	trace((callerClass ? callerClass.toString() : '') + ': ' + (callerFunction ? callerFunction.toString() : '') + ': ' + message) ;
        }
        
        public static function getUserTextInput(parent:DisplayObject, callback:Function, 
        										titleText:String, modal:Boolean=false, 
        										textInputText:String='', ignoreNullOrEmpty:Boolean=true):void {
            var inputDialog:InputTextDialog = InputTextDialog(PopUpManager.createPopUp(parent, InputTextDialog, modal)) ;

            inputDialog.title = titleText ;
            inputDialog.okEventHandler = callback ;

            PopUpManager.centerPopUp(inputDialog);

            inputDialog.ignoreNullOrEmpty = ignoreNullOrEmpty ;
            inputDialog.textInput.text = textInputText ;

            if (textInputText != null && textInputText.length > 0) {
            	inputDialog.textInput.selectionBeginIndex = 0 ;
            	inputDialog.textInput.selectionEndIndex = textInputText.length ;
            }

            inputDialog.textInput.setFocus();
	    }
	    
	    public static function capitalize(text:String):String {
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
	    
	    public static function copyArrayCollection(array : ArrayCollection, defaultElement : Object = null) : ArrayCollection {
                var result : ArrayCollection;
                if (defaultElement!=null || array != null) {
                    result = new ArrayCollection();	
                }
                if (defaultElement != null) {
                    result.addItem(defaultElement); 
                }
                if (array != null) {    
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