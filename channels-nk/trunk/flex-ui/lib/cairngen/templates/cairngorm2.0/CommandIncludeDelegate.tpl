
package @namespace@.@commands@
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.business.Responder;
	import @namespace@.@business@.@sequence@Delegate;
	import @namespace@.@events@.@sequence@Event;
	
	public class @sequence@Command implements Command, Responder
	{
		public function execute(event:CairngormEvent) : void
		{
			var evt:@sequence@Event = event as @sequence@Event;
			var delegate:@sequence@Delegate = new @sequence@Delegate( this );
		}
		
		public function onResult( event:* = null ) : void {
		}
		
		public function onFault( event:* = null) : void {
		}
	}
}