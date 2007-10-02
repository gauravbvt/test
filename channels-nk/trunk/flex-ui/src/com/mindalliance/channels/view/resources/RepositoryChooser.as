package com.mindalliance.channels.view.resources
{
	import com.mindalliance.channels.events.resources.CreateRepositoryEvent;
	import com.mindalliance.channels.events.resources.DeleteRepositoryEvent;
	import com.mindalliance.channels.events.resources.GetRepositoryEvent;
	import com.mindalliance.channels.events.resources.GetRepositoryListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	
	import mx.managers.PopUpManager;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class RepositoryChooser extends Chooser
	{
		[Embed(source='../../../../../assets/images/data16x16.png')]
		[Bindable]
		private var dataImgCls:Class ;
		
		public function RepositoryChooser()
		{
			super();
			elementListKey="repositories";
			elementName="Repositories";
            editor = new RepositoryEditor();
            titleIcon = dataImgCls ;
		}
		
	    override protected function populateList() : void {
            CairngormHelper.fireEvent( new GetRepositoryListEvent() ); 
        }
        
        override protected function populateElement(id : String) : void {
            CairngormHelper.fireEvent( new GetRepositoryEvent(id, model.editorModel));
        }
        
        override protected function btnAddClicked():void {
        	var rc:RepositoryCreator = PopUpManager.createPopUp(this, RepositoryCreator, true) as RepositoryCreator ;
        	PopUpManager.centerPopUp(rc) ;
        	rc.resultHandler = function anon(reposName:String, orgID:String):void {
         		CairngormHelper.fireEvent(new CreateRepositoryEvent(reposName, orgID)) ;
         		//listElements.scrollToIndex(list.length - 1) ;
         	}
		}

        override protected function btnRemoveClicked():void {
         	for each (var item:Object in listElements.selectedItems) {
        		CairngormHelper.fireEvent(new DeleteRepositoryEvent((item as ElementVO).id)) ;
        	}
        	
        }		
	}
}