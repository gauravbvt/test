package com.mindalliance.channels.scenario.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.AcquirementVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class AcquirementAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function AcquirementAdapter()
		{
			super("acquirement", AcquirementVO);
		}
		
        override public function fromXML(obj:XML):ElementVO {

            return new AcquirementVO(obj.id, obj.name, obj.description,
                                 XMLHelper.xmlToCategorySet(obj),
                                 ElementHelper.findElementById(obj.product.taskId,
                                             ChannelsModelLocator.getInstance().getElementListModel('tasks').data),
                                     XMLHelper.xmlToInformation(obj.information[0]));
        }
        
        override public function toXML(element:ElementVO) : XML {
            var obj : AcquirementVO = (element as AcquirementVO);
            var xml : XML =  <acquirement schema="/channels/schema/acquirement.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </acquirement>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            xml.appendChild(<product><taskId>{obj.product.id}</taskId></product>);
            xml.appendChild(XMLHelper.informationToXML(obj.information));
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			return  <acquirement schema="/channels/schema/acquirement.rng">
             <name>{params["name"]}</name>
             <categories atMostOne="false" taxonomy="acquirement"/>
             <product>
                <taskId>{params["taskId"]}</taskId>
             </product>
             <information/>
             </acquirement>;
		}
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('acquirements').data.addItem(element);
            ChannelsModelLocator.getInstance().getElementListModel('acquirements' + (element as AcquirementVO).product.id).data.addItem(element);
        } 
        
        override public function updateElement(element : ElementVO, values : Object) : void  {
            var data : AcquirementVO = element as AcquirementVO;
            data.name=values["name"];
            data.description = values["description"];
            data.categories = values["categories"]; 
            data.product = values["product"]; 
        }   
	}
}