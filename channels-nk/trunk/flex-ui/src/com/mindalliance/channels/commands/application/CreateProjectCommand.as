
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.CreateProjectEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class CreateProjectCommand implements ICommand
	{
		
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:CreateProjectEvent = event as CreateProjectEvent;
			var name : String = evt.name;
			var project : XML = <project></project>;
			project.appendChild(<id>{name}</id>);
		}
		
		public function result(data:Object):void
		{
			var result:Object = (data as ResultEvent).result.project;
			
		}
		
		public function fault(info:Object):void
		{
			var fault:FaultEvent = info as FaultEvent;
		}
	}
}