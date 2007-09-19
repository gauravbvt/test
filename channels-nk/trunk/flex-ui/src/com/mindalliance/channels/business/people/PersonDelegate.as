// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.PersonVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class PersonDelegate extends BaseDelegate
	{	
		public function PersonDelegate(responder:IResponder)
		{
			super(responder);
			typeName="person";
		}
		
		public function getPersonList() : void {
			performQuery("allPersons", null);
		}
		
		public function getPersonByUser(userId : String) : void {
			var request : Array = new Array();
			request["userId"] = userId;
			performQuery("personOfUser", request);
		}
		
		/**
         * parses /channels/schema/person.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			var user : ElementVO;
			if (xml.userId.length > 0) {
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
		
		override public function fromXMLElementList(list : XML) : ArrayCollection {
            var results : ArrayCollection = new ArrayCollection();
            for each (var el : XML in list.elements(typeName)) {
                results.addItem(new PersonVO(el.id,el.firstName,el.lastName,null,null,null,null,null,null,null)); 
            }
            return results; 
        }
        
        public function create(firstName : String, lastName : String) : void {
            var param : Array=new Array();
            param["firstName"] = firstName;
            param["lastName"] = lastName;
           createElement(<person schema="/channels/schema/person.rng">
             <firstName>{firstName}</firstName>
             <lastName>{lastName}</lastName>
             <email></email>
             <roles/>
           </person>, param);
        }
        
	}
}