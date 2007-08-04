
package com.mindalliance.channels.business
{
	import com.mindalliance.channels.business.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class GetScenarioListDelegate
	{
		private var responder:IResponder;
		
		public function GetScenarioListDelegate(responder:IResponder)
		{
			this.responder = responder;
		}
	}
}