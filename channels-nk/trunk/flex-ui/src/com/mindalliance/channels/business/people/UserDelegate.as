// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.common.BaseDelegate;
	import com.mindalliance.channels.vo.UserVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.rpc.IResponder;
	
	public class UserDelegate extends BaseDelegate
	{	
		public function UserDelegate(responder:IResponder)
		{
			super(responder);
			typeName="user"
		}
		/**
         * parses /channels/schema/user.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			return new UserVO(xml.id, xml.userName, xml.password, xml.(@isAdmin));
		}
		/**
         * generates /channels/schema/user.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : UserVO = (element as UserVO);
			var xml : XML = <user schema="/channels/schema/user.rng" isAdmin={obj.admin}>
						<id>{obj.id}</id>
						<userName>{obj.name}</userName>
						<password>{obj.password}</password>
					</user>;
			return xml;
		}
		
		public function create(name : String, password : String, isAdmin : Boolean) : void {
          var param : Array=new Array();
          param["name"] = name;
          param["password"] = password;
          param["isAdmin"] = isAdmin;
			createElement( <user schema="/channels/schema/user.rng" isAdmin={isAdmin}>
			 <userName>{name}</userName>
			 <password>{password}</password>
			</user>, param);
		}
	}
}