package com.mindalliance.channels.resources.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.RepositoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	
	import mx.collections.ArrayCollection;

	public class RepositoryAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function RepositoryAdapter()
		{
			super("repository", RepositoryVO);
		}
		
        /**
         * parses /channels/schema/repository.rng
         */
        override public function fromXML(obj:XML):ElementVO {
            var contents : ArrayCollection = new ArrayCollection();
            for each (var el : XML in obj.contents.information) {
                 contents.addItem(XMLHelper.xmlToInformation(el));  
            }
            return new RepositoryVO(obj.id, 
                                     obj.name, 
                                     obj.description,
                                     XMLHelper.xmlToCategorySet(obj),
                                     new ElementVO(obj.organizationId, null),
                                     XMLHelper.xmlToIdList("roleId", obj.administrators),
                                     contents,
                                     XMLHelper.xmlToIdList("roleId", obj.access));
        }
        /**
         * generates /channels/schema/repository.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : RepositoryVO = (element as RepositoryVO);
            var xml : XML = <repository schema="/channels/schema/repository.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </repository>;
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            xml.appendChild(<organizationId>{obj.organization.id}</organizationId>);
            xml.appendChild(XMLHelper.idListToXML("administrators","roleId",obj.administrators));
            var contents : XML = <contents></contents>;
            for each (var info : InformationVO in obj.contents) {
                contents.appendChild(XMLHelper.informationToXML(info)); 
            }
            xml.appendChild(contents);
             
            xml.appendChild(XMLHelper.idListToXML("access", "roleId", obj.access));
            return xml;
        }
        		
		override public function create(params:Object):XML
		{
			return <repository schema="/channels/schema/repository.rng">
                    <name>{params["name"]}</name>
                    <organizationId>{params["organizationId"]}</organizationId>
                    <categories atMostOne="false" taxonomy="repository"/>
                    <administrators/>
                    <contents/>
                    <access/>
            </repository>;
		}
		
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('repositories').data.addItem(element);
        } 
        override public function updateElement(element : ElementVO, values : Object) : void {
            var data : RepositoryVO  = element as RepositoryVO;
            data.name = values["name"];
            data.description = values["description"];
            data.categories = values["categories"];
            data.organization = values["organization"];
            data.administrators = values["administrators"];
            data.contents = values["contents"];
            data.access = values["access"];
        }   
	}
}