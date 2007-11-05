package com.mindalliance.channels.view.resources
{
	import com.mindalliance.channels.common.events.DeleteElementEvent;
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.resources.events.CreateRepositoryEvent;
	import com.mindalliance.channels.resources.events.GetRepositoryListEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.view.common.Chooser;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.managers.PopUpManager;

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
            CairngormHelper.fireEvent( new GetElementEvent(id, model.editorModel));
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
        		CairngormHelper.fireEvent(new DeleteElementEvent((item as ElementVO).id)) ;
        	}
        	
        }		
	}
}