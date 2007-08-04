
package com.mindalliance.channels.business
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	
	public class GetProjectListDelegate
	{
		private var responder:IResponder;
		private var service : Object;
		public function GetProjectListDelegate(responder:IResponder)
		{
			this.service = ServiceLocator.getInstance().getHTTPService( Services.GET_PROJECT_LIST_SERVICE );
			this.responder = responder;
		}
		
		public function getProjectList() : void {
			var token:AsyncToken = service.send();
			token.addResponder( responder );	
		}
	}
}