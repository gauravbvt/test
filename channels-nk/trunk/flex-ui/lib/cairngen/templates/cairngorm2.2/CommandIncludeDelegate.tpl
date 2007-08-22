// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@commands@.@submodule@
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.util.XMLHelper;
	import @namespace@.@business@.@submodule@.@delegate@Delegate;
	import @namespace@.@events@.@submodule@.@sequence@Event;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class @sequence@Command implements ICommand, IResponder
	{
		
		private var log : ILogger = Log.getLogger("@namespace@.@commands@.@submodule@.@sequence@Command");
	
		public function execute(event:CairngormEvent):void
		{
			var evt:@sequence@Event = event as @sequence@Event;
			var delegate:@delegate@Delegate = new @delegate@Delegate( this );
		}
		
		public function result(data:Object):void
		{
			var result:ResultEvent = data as ResultEvent;
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
			log.error(fault.toString());
		}
	}
}