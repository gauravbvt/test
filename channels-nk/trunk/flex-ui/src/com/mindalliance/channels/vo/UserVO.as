package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;
	import com.mindalliance.channels.vo.common.ElementVO;
	
    [Bindable]
	public class UserVO extends ElementVO implements IValueObject
	{
		
		public function UserVO(id : String, username : String,password : String,admin : Boolean) {
			super (id, username);
			this.admin = admin;
			this.password = password;
		}
		private var _admin : Boolean;
		private var _password : String;
		
		
		public function get admin() : Boolean {
			return _admin;
		}

		public function set admin(admin : Boolean) : void {
			_admin=admin;
		}
		
		public function get password() : String {
			return _password;
		}

		public function set password(password : String) : void {
			_password=password;
		}
		
	}
}