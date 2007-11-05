package com.mindalliance.channels.people.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.vo.UserVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class UserAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function UserAdapter()
		{
			//TODO: implement function
			super("user", UserVO);
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
		override public function create(params:Object):XML
		{
			return <user schema="/channels/schema/user.rng" isAdmin={params["isAdmin"]}>
             <userName>{params["name"]}</userName>
             <password>{params["password"]}</password>
            </user>;
		}
        override public function updateElement(element : ElementVO, values : Object) : void{
            element["password"] = values["password"];
        }   		
	}
}