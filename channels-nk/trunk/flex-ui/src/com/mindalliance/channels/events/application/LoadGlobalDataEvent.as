// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class LoadGlobalDataEvent extends CairngormEvent
    {
        public static const LoadGlobalData_Event:String = "<LoadGlobalDataEvent>";
        
        public function LoadGlobalDataEvent() 
        {
            super( LoadGlobalData_Event );
        }
    }
}