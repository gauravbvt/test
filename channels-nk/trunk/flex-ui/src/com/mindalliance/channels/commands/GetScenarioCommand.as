
package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.GetScenarioDelegate;
	import com.mindalliance.channels.events.GetScenarioEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	public class GetScenarioCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:GetScenarioEvent = event as GetScenarioEvent;
			var delegate:GetScenarioDelegate = new GetScenarioDelegate( this );
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