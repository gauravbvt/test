package com.mindalliance.channels.people.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.TopicVO;
	
	import mx.collections.ArrayCollection;

	public class RoleAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function RoleAdapter()
		{
			super("role", RoleVO);
		}
		
        override public function fromXML(xml:XML):ElementVO {
            var expertise : ArrayCollection = new ArrayCollection();
            for each (var info : XML in xml.expertise.information) {
                expertise.addItem(XMLHelper.xmlToInformation(info));
            }
            // TODO information needs to be parsed properly.  The current editor won't allow it.
            var topics : ArrayCollection  = new ArrayCollection();
            for each (var information : InformationVO in expertise) {
                for each (var topic : TopicVO in  information.topics) { 
                    topics.addItem(topic);
                }
            }

            return new RoleVO(xml.id, 
                                xml.name, 
                                xml.description,
                                XMLHelper.xmlToCategorySet(xml),
                                new ElementVO(xml.organizationId, null),
                                topics);
        }
        
        override public function toXML(obj:ElementVO) : XML {
            var role : RoleVO = (obj as RoleVO);
            var xml : XML = <role schema="/channels/schema/role.rng">
                        <id>{role.id}</id>
                        <name>{role.name}</name>
                        <description>{role.description}</description>
                    </role>;
            
            xml.appendChild(XMLHelper.categorySetToXML(role.categories));
            xml.appendChild(<organizationId>{role.organization.id}</organizationId>);
            var expertise : XML = <expertise></expertise>;
            
            
            // TODO information needs to be generated properly.  The current role editor won't allow it.
            var info : InformationVO = new InformationVO(role.expertise);
            expertise.appendChild(XMLHelper.informationToXML(info));    
            xml.appendChild(expertise);
            return xml;
        }		
		override public function create(params:Object):XML
		{
		  return <role schema="/channels/schema/organization.rng">
             <name>{params["name"]}</name>
             <categories atMostOne="false" taxonomy="role"/>
             <organizationId>{params["organizationId"]}</organizationId>
             <expertise/>
           </role>;
		}
		
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('roles').data.addItem(element);
        } 
                
        override public function updateElement(element : ElementVO, values : Object): void {
            var data : RoleVO = element as RoleVO;
                            data.name=values["name"];
                data.description = values["description"];
                data.categories = values["categories"]; 
                data.organization = values["organization"];
                data.expertise = values["expertise"];
        }   

	}
}