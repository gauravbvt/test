
package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	
	public class ProjectDelegate extends BaseDelegate
	{
		
		public function ProjectDelegate(responder:IResponder)
		{
			super(responder);
		}
		
		public function getProjectList() : void {
			send("project", null, "GET", responder);
		}
		
		public function createProject(name:String) : void {
			var project : XML = <project></project>;
			project.appendChild(<name>{name}</name>);
			
			var request:Object = new Object();
			request["doc"] = project;

			send("project", request, "POST", responder);	
		}
		
		public function deleteProject(id : String) : void {			
			send(id, null, "DELETE", responder);	
		}
		
		
	}
}