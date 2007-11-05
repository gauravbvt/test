package com.mindalliance.channels.common.business
{
	import mx.rpc.IResponder;

	public class GetElementDelegate extends BaseQueryDelegate
	{
		public function GetElementDelegate(responder : IResponder)
		{
			super(responder);
		}
		
		public function getElement(id : String) : void {
            invokeService("/element?id=" + id + "&nameReferenced=true");
        }
        
        public function getPersonByUser(userId : String) : void {
        	invokeService("/model?query=personOfUser&userId=" + userId);
        }
        
	}
}