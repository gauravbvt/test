package com.mindalliance.channels.scenario.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.AgentVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class AgentAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function AgentAdapter()
		{
			super("agent", AgentVO);
		}
		
		override public function fromXML(obj : XML) : ElementVO {
            return new AgentVO(obj.id,
                                 obj.name,
                                 obj.description,
                                 ElementHelper.findElementById(obj.taskId, ChannelsModelLocator.getInstance().getElementListModel("tasks").data), 
                                 ElementHelper.findElementById(obj.roleId, ChannelsModelLocator.getInstance().getElementListModel("roles").data));
        }
        
        override public function toXML(element : ElementVO) : XML {
          var obj : AgentVO = (element as AgentVO);
          return <agent schema="/channels/schema/agent.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                        <taskId>{obj.task.id}</taskId>
                        <roleId>{obj.role.id}</roleId>
                          </agent>; 
        }
		
		override public function create(params:Object):XML
		{
			return <agent>
                        <name>{params["name"]}</name>
                        <taskId>{params["taskId"]}</taskId>
                        <roleId>{params["roleId"]}</roleId>
                          </agent>;
		}
		
		override public function fromXMLListElement(element:XML):ElementVO
		{
            return fromXML(element);
		}
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('agents').data.addItem(element);
            ChannelsModelLocator.getInstance().getElementListModel('agents' + (element as AgentVO).task).data.addItem(element);
        } 		
        
        override public function updateElement(element : ElementVO, values : Object) : void  {
            
        }   
	}
}