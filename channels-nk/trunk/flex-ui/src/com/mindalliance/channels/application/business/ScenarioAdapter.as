package com.mindalliance.channels.application.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.ScenarioVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class ScenarioAdapter extends BaseElementAdapter
	{
		public function ScenarioAdapter() {
          super("scenario", ScenarioVO);
        }
		
		override public function create(params : Object) : XML {
			return super.create(params).appendChild(<projectId>{params["projectId"]}</projectId>);        }
        
        /**
         * Produces XML of the form:
         * 
         * <scenario>
         *   <id>{id}</id>
         *   <name>{name}</name>
         *   <description>{description}</description>
         * </scenario>
         */
        override public function toXML(element : ElementVO) : XML {
            var obj : ScenarioVO = (element as ScenarioVO)
            return <scenario schema="/channels/schema/scenario.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                        <projectId>{obj.project.id}</projectId>
                    </scenario>;
        
        }
        /**
         * Expects XML of the form:
         * <scenario>
         *   <id>{id}</id>
         *   <name>{name}</id>
         *   <description>{description}</description>
         *   <projectId>{projectId}</projectId>
         * </scenario>
         */
        override public function fromXML( obj : XML ) : ElementVO {
                return new ScenarioVO(obj.id, obj.name, obj.description, new ElementVO(obj.projectId, null));
        }
		
		override public function postCreate(element : ElementVO, parameters : Object) : void {
            
            ChannelsModelLocator.getInstance().getElementListModel('scenarios').data.addItem(element);
            
        }
        
        override public function updateElement(element : ElementVO, values : Object) : void {
            var obj : ScenarioVO = element as ScenarioVO;
            obj.name = values["name"];
            obj.description = values["description"];
        }
        
	}
}