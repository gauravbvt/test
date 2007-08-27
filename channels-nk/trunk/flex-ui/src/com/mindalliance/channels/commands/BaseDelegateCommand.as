package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.rpc.IResponder;
	import mx.utils.ObjectUtil;

	public class BaseDelegateCommand extends BaseCommand implements IResponder
	{
		
		public function result(data:Object):void
		{
			// Should be overridden
		}
		
		public function fault(info:Object):void
		{
			log.error(fault as String);
		}
		
	}
}