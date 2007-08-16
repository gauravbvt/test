// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	
	public class ScenarioDelegate extends BaseDelegate
	{	
		public function ScenarioDelegate(responder:IResponder)
		{
			super(responder);
		}
		
		public function getScenarioList(projectId : String) : void {
			
			var request:Object = new Object();
			request["project"] = projectId;

			send("scenario", request, "GET", responder);
		}
		
	}
}