package com.mindalliance.channels.application.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.ProjectVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class ProjectAdapter extends BaseElementAdapter
	{
		
		public function ProjectAdapter() {
		  super("project", ProjectVO);
		}
		
		override public function fromXML(obj:XML):ElementVO
		{
			var manager : ElementVO = null;
            if (obj.managedByPersonId != null) {
                 manager=new ElementVO(obj.managedByPersonId, null);
            }
            return new ProjectVO(obj.id, obj.name, obj.description, manager);
		}
		
		override public function toXML(element:ElementVO):XML
		{
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
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('projects').data.addItem(element);
        } 
        
        override public function updateElement(element : ElementVO, values : Object) : void {
        	var obj : ProjectVO = element as ProjectVO;
        	obj.name = values["name"];
        	obj.description = values["description"];
        	obj.manager = values["manager"];
        }
	}
}