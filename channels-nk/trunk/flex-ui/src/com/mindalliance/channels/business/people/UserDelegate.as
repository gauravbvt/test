// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.business.people
{
	import com.mindalliance.channels.business.BaseDelegate;
	
	import mx.rpc.IResponder;
	import com.mindalliance.channels.vo.ElementVO;
	import com.mindalliance.channels.vo.UserVO;
	
	public class UserDelegate extends BaseDelegate
	{	
		public function UserDelegate(responder:IResponder)
		{
			super(responder);
		}
		/**
         * parses /channels/schema/user.rng
         */
		override public function fromXML(xml:XML):ElementVO {
			return new UserVO(obj.id, obj.userName, obj.password, obj.(@isAdmin));
		}
		/**
         * generates /channels/schema/user.rng
         */
		override public function toXML(element:ElementVO) : XML {
			var obj : UserVO = (element as UserVO);
			var xml : XML = <user schema="/channels/schema/user.rng" isAdmin="{obj.admin}">
						<id>{obj.id}</id>
						<userName>{obj.username}</userName>
						<password>{obj.password}</password>
					</user>;
			
			return xml;
		}
	}
}