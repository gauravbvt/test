// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@commands@.@submodule@
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import @namespace@.@events@.@submodule@.@sequence@Event;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class @sequence@Command implements ICommand
	{
		private var log : ILogger = Log.getLogger("@namespace@.@commands@.@submodule@.@sequence@Command");
		
		public function execute(event:CairngormEvent):void
		{
			var evt:@sequence@Event = event as @sequence@Event;
		}
	}
}