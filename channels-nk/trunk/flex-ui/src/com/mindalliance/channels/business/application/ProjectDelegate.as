
package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.ElementVO;
	
	public class ProjectDelegate extends BaseDelegate
	{
		
		public function ProjectDelegate(responder:IResponder)
		{
			super(responder);
			typeName = "project";
		}
		
		public function getProjectList() : void {
			performQuery("allProjects", null);
		}
		
		public function createProject(name:String) : void {
			var project : XML = <project></project>;
			project.appendChild(<name>{name}</name>);
			
			createElement(project);	
		}
		
		override public function fromXML(obj:Object):ElementVO {
			return new ProjectVO(obj.id, obj.name, obj.description);
		}
		
		override public function toXML(obj:ElementVO) : XML {
			return <project schema="/channels/schema/project.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</project>;
		}
		
	}
}