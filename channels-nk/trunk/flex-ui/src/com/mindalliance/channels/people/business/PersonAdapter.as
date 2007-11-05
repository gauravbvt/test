package com.mindalliance.channels.people.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.PersonVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class PersonAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function PersonAdapter()
		{
			super("person", PersonVO);
		}
		
/**
         * parses /channels/schema/person.rng
         */
        override public function fromXML(xml:XML):ElementVO {
            var user : ElementVO;
            if (xml.userId.length() > 0) {
                user = new ElementVO(xml.userId, null); 
            }
            return new PersonVO(xml.id, 
                                 xml.firstName,
                                 xml.lastName,
                                 xml.photo,
                                 xml.email,
                                 xml.officePhone,
                                 xml.cellPhone,
                                 XMLHelper.xmlToAddress(xml.address),
                                 XMLHelper.xmlToIdList("roleId", xml.roles),
                                 user);
        }
        /**
         * generates /channels/schema/person.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : PersonVO = (element as PersonVO);
            var xml : XML = <person schema="/channels/schema/person.rng">
                        <id>{obj.id}</id>
                        <firstName>{obj.firstName}</firstName>
                        <lastName>{obj.lastName}</lastName>
                    </person>;
            if (obj.photo != null) {
                xml.appendChild(<photo>{obj.photo}</photo>);    
            }
            xml.appendChild(<email>{obj.email}</email>);
            if (obj.officePhone != null) {
                xml.appendChild(<officePhone>{obj.officePhone}</officePhone>);  
            }
            if (obj.cellPhone != null) {
                xml.appendChild(<cellPhone>{obj.cellPhone}</cellPhone>);  
            }
            if (obj.address != null) {
                xml.appendChild(XMLHelper.addressToXML(obj.address));   
            }
            xml.appendChild(XMLHelper.idListToXML("roles","roleId", obj.roles));
            if (obj.user != null) {
                xml.appendChild(<userId>{obj.user.id}</userId>);
            }
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			return <person schema="/channels/schema/person.rng">
             <firstName>{params["firstName"]}</firstName>
             <lastName>{params["lastName"]}</lastName>
             <email></email>
             <roles/>
           </person>
		}
		
		override public function fromXMLListElement(element:XML):ElementVO
		{
			return new PersonVO(element.id,element.firstName,element.lastName,null,null,null,null,null,null,null);
		}
        override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('people').data.addItem(element);
        } 		

        override public function updateElement(element : ElementVO, values : Object) : void {
        	var data: PersonVO = element as PersonVO;
            data.firstName = values["firstName"];
            data.lastName = values["lastName"];
            data.photo = values["photo"];
            data.email = values["email"];
            data.officePhone = values["officePhone"];
            data.cellPhone = values["cellPhone"];
            data.address = values["address"];
            data.roles = values["roles"];
            data.user = values["user"];
        }   
    }
}