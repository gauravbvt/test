package com.mindalliance.channels.scenario.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.ArtifactVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class ArtifactAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function ArtifactAdapter()
		{
			super("artifact", ArtifactVO);
		}
		
        override public function fromXML(obj:XML):ElementVO {
            return new ArtifactVO(obj.id, obj.name, obj.description,
                                 XMLHelper.xmlToCategorySet(obj),
                                 ElementHelper.findElementById(obj.product.taskId,
                                             ChannelsModelLocator.getInstance().getElementListModel('tasks').data));
        }
        
        override public function toXML(element:ElementVO) : XML {
            var obj : ArtifactVO = (element as ArtifactVO);
            var xml : XML =  <artifact schema="/channels/schema/artifact.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </artifact>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            xml.appendChild(<product><taskId>{obj.product.id}</taskId></product>);
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			return <artifact schema="/channels/schema/artifact.rng">
             <name>{params["name"]}</name>
             <categories atMostOne="false" taxonomy="artifact"/>
             <product>
                <taskId>{params["taskId"]}</taskId>
             </product>
           </artifact>;
		}
		
		override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('artifacts').data.addItem(element);
            ChannelsModelLocator.getInstance().getElementListModel('artifacts' + (element as ArtifactVO).product.id).data.addItem(element);
        } 
        override public function updateElement(element : ElementVO, values : Object) : void  {
            var data : ArtifactVO= element as ArtifactVO;
            data.name=values["name"];
            data.description = values["description"];
            data.categories = values["categories"]; 
            data.product = values["product"];
        }   		
	}
}