
package com.mindalliance.channels.business
{
	import com.mindalliance.channels.business.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class GetScenarioDelegate
	{
		private var responder:IResponder;
		private var service:Object;
		
		public function GetScenarioDelegate(responder:IResponder)
		{
			this.responder = responder;
			this.service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_SERVICE );
		}
		
		public function getScenario(id : String) : void {
			
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}