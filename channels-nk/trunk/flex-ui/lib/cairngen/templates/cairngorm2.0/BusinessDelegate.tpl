
package @namespace@.@business@
{
	import @namespace@.@business@.Services;
	import com.adobe.cairngorm.business.ServiceLocator;
	import com.adobe.cairngorm.business.Responder;
	
	public class @sequence@Delegate
	{
		private var responder:Responder;
		
		public function @sequence@Delegate(responder:Responder)
		{
			this.responder = responder;
		}
	}
}