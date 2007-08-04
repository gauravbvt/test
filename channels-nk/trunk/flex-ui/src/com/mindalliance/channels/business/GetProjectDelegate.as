
package com.mindalliance.channels.business
{
	import com.mindalliance.channels.business.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import mx.rpc.IResponder;
	import mx.rpc.AsyncToken;
	
	public class GetProjectDelegate
	{
		private var responder:IResponder;
		private var service : Object;
		
		public function GetProjectDelegate(responder:IResponder)
		{
			//this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_SERVICE );
			this.responder = responder;
		}
		
		public function getProject(id : String) : void
		{
			if (id == "1") {
				this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_1_SERVICE );
			} else if (id == "2") {
				this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_2_SERVICE );
			} else if (id == "3") {
				this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_3_SERVICE );
			}
			
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}