
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.GetScenarioListDelegate;
	import com.mindalliance.channels.events.GetScenarioListEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	public class GetScenarioListCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioListEvent = event as GetScenarioListEvent;
			var delegate:GetScenarioListDelegate = new GetScenarioListDelegate( this );
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