
package com.mindalliance.channels.business
{
	import com.mindalliance.channels.business.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class GetScenarioListDelegate
	{
		private var responder:IResponder;
		private var service : Object;
		
		public function GetScenarioListDelegate(responder:IResponder)
		{
			this.responder = responder;
			this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_LIST_SERVICE );
		}
		
		public function getScenarioList( projectId : String) : void {
			
			var token:AsyncToken = service.send();
			token.addResponder( responder );		
		}
	}
}