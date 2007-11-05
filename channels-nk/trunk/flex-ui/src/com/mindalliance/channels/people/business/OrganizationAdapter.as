package com.mindalliance.channels.people.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.OrganizationVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class OrganizationAdapter extends BaseElementAdapter
	{
		public function OrganizationAdapter() {
		  super("organization", OrganizationVO);	
		}
		
        /**
         * generates /channels/schema/organization.rng
         */
        override public function toXML(element : ElementVO) : XML {
            var obj : OrganizationVO = (element as OrganizationVO);
            var xml : XML =  <organization  schema="/channels/schema/organization.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                        

                    </organization>;
            
            xml.appendChild(XMLHelper.categorySetToXML(obj.categories));
            if (obj.abbreviation != null) {
                xml.appendChild(<abbreviation>{obj.abbreviation}</abbreviation>);
            }
            if (obj.parent != null) {
                xml.appendChild(<parentOrganizationId>{obj.parent.id}</parentOrganizationId>);  
            }
            if (obj.address != null) {
                xml.appendChild(XMLHelper.addressToXML(obj.address));
            }
            if (obj.logo != null) {
                xml.appendChild(<logo>{obj.logo}</logo>);
            }
            return xml;
        }

        /**
         * parses /channels/schema/organization.rng
         */
        override public function fromXML( obj : XML ) : ElementVO {
                return new OrganizationVO(obj.id, 
                                        obj.name, 
                                        obj.description, 
                                        XMLHelper.xmlToCategorySet(obj),
                                        obj.abbreviation,
                                        new ElementVO(obj.parentOrganizationId, null),
                                        XMLHelper.xmlToAddress(obj.address),
                                        obj.logo);
        }
        override public function create(params : Object) : XML {
          return super.create(params).appendChild(<categories atMostOne="false" taxonomy="organization"/>);
        }
        
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('organizations').data.addItem(element);
        }    
        
        override public function updateElement(element : ElementVO, values : Object) : void {
            var data : OrganizationVO = element as OrganizationVO;
            data.name=values["name"];
            data.description = values["description"];
            data.abbreviation = values["abbreviation"];
            data.address = values["address"];
            data.parent = values["parent"];
            
        }    
	}
}