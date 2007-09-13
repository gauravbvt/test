// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.RoleVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	import com.mindalliance.channels.vo.common.InformationVO;
	import com.mindalliance.channels.vo.common.TopicVO;
	
	import mx.collections.ArrayCollection;
	import mx.rpc.IResponder;
	
	public class RoleDelegate extends BaseDelegate
	{	
		public function RoleDelegate(responder:IResponder)
		{
			super(responder);
			typeName="role";
		}
		
		public function getRoleList() : void {
            performQuery("allRoles", null);
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
		public function create(name : String, organizationId : String) : void {
            var param : Array=new Array();
           param["name"] = name;
           param["organizationId"] = organizationId;
           createElement(<role schema="/channels/schema/organization.rng">
             <name>{name}</name>
             <categories atMostOne="false" taxonomy="role"/>
             <organizationId>{organizationId}</organizationId>
             <expertise/>
           </role>, param);
        }
		
	}
}