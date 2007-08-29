
package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
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
		
		override public function fromXML(obj:XML):ElementVO {
			var manager : ElementVO = null;
			if (obj.managedByPersonId != null) {
			     manager=new ElementVO(obj.managedByPersonId, null);
			}
			return new ProjectVO(obj.id, obj.name, obj.description, manager);
		}
		
		override public function toXML(element:ElementVO) : XML {
			var obj : ProjectVO = (element as ProjectVO);
			var xml : XML =  <project schema="/channels/schema/project.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
						
					</project>;
			if (obj.manager != null) {
                xml.appendChild(<managedByPersonId>{obj.manager.id}</managedByPersonId>);
            }
            return xml;
		}
		
	}
}