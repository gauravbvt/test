
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
			performQuery("project", null);
		}
		
		public function createProject(name:String) : void {
			var project : XML = <project></project>;
			project.appendChild(<name>{name}</name>);
			
			createElement("project", project);	
		}
	}
}