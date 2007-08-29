package com.mindalliance.channels.vo
{
	import com.adobe.cairngorm.vo.IValueObject;

	public class UserVO implements IValueObject
	{
		
		public function UserVO(id : String, username : String,password : String,admin : Boolean) {
			
		}
		private var _id : String;
		private var _admin : Boolean;
		private var _username : String;
		private var _password : String;
		
		
		public function get id() : String {
			return _id;
		}

		public function set id(id : String) : void {
			_id=id;
		}
		
		public function get admin() : Boolean {
			return _admin;
		}

		public function set admin(admin : Boolean) : void {
			_admin=admin;
		}
		
		public function get username() : String {
			return _username;
		}

		public function set username(username : String) : void {
			_username=username;
		}
		
		public function get password() : String {
			return _password;
		}

		public function set password(password : String) : void {
			_password=password;
		}
		
	}
}