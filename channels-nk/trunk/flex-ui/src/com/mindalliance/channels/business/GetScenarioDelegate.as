
package com.mindalliance.channels.business
{
	import com.mindalliance.channels.business.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class GetScenarioDelegate
	{
		private var responder:IResponder;
		
		public function GetScenarioDelegate(responder:IResponder)
		{
			this.responder = responder;
		}
	}
}