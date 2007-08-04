
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.GetProjectDelegate;
	import com.mindalliance.channels.events.GetProjectEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	public class GetProjectCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:GetProjectEvent = event as GetProjectEvent;
			var delegate:GetProjectDelegate = new GetProjectDelegate( this );
		}
		
		public function result(data:Object):void
		{
			var result:ResultEvent = data as ResultEvent;
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}