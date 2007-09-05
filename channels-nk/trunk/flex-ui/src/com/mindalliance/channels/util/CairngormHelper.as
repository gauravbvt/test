package com.mindalliance.channels.util
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.mindalliance.channels.events.common.*;
    import com.mindalliance.channels.model.EditorModel;
    
	public class CairngormHelper
	{

	
	    public static function queueUpdate(model : EditorModel) : void {
            fireEvent( new QueueUpdateEvent(model) );
        }
        
        public static function fireEvent(event : CairngormEvent) : void {
        	
            CairngormEventDispatcher.getInstance().dispatchEvent(event);
        }
	
	}
}