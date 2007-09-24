// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@events@.@submodule@
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class @sequence@Event extends CairngormEvent
    {
        public static const @sequence@_Event:String = "<@sequence@Event>";
        
        public function @sequence@Event() 
        {
            super( @sequence@_Event );
        }
    }
}