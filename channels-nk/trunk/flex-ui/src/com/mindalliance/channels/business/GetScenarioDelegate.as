
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
			//this.service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_SERVICE );
		}
		
		public function getScenario(id : String) : void {
			switch (id) {
				case "1.1" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_1_1_SERVICE ); break;
				case "1.2" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_1_2_SERVICE ); break;
				case "1.3" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_1_3_SERVICE ); break;
				case "2.1" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_2_1_SERVICE ); break;
				case "2.2" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_2_2_SERVICE ); break;
				case "3.1" : service =  ServiceLocator.getInstance().getHTTPService( Services.GET_SCENARIO_3_1_SERVICE ); break;
			};
			
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}