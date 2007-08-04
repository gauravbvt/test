
package @namespace@.@commands@
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import @namespace@.@events@.@sequence@Event;

	public class @sequence@Command implements ICommand
	{
		public function execute(event:CairngormEvent):void
		{
			var evt:@sequence@Event = event as @sequence@Event;
		}
	}
}