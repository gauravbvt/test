
package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.application.ProjectDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.application.DeleteProjectEvent;
	import com.mindalliance.channels.events.application.GetProjectListEvent;
	public class DeleteProjectCommand extends BaseDelegateCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteProjectEvent = event as DeleteProjectEvent;
			var id : String = evt.id;
			
			log.debug("Deleting Project...");
			
			var delegate:ProjectDelegate = new ProjectDelegate( this );
			delegate.deleteElement(id);
		}
		
		override public function result(data:Object):void
		{
			var result:Boolean = data["data"] as Boolean;
			if (result == true) {
 	        	CairngormEventDispatcher.getInstance().dispatchEvent( new GetProjectListEvent() );
 	        	log.info("Project successfully deleted");
			} else {
				log.warn("Project Deletion failed");	
			}
		}
	}
}