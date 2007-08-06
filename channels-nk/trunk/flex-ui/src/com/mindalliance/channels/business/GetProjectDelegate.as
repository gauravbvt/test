
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
			this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_SERVICE );
			this.responder = responder;
		}
		
		public function getProject(id : String) : void
		{		
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}