package com.mindalliance.channels.commands
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.utils.ObjectUtil;
	
	public class BaseCommand implements ICommand
	{
				
		protected var model : ChannelsModelLocator = ChannelsModelLocator.getInstance();	
		protected var log : ILogger = Log.getLogger(ObjectUtil.getClassInfo(this).name.replace("::", "."));
		
		public function execute(event:CairngormEvent):void
		{
			// Should be overridden
		}
		
	}
}