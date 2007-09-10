// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.scenario.ArtifactDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.people.*;
    import com.mindalliance.channels.events.scenario.UpdateArtifactEvent;
    import com.mindalliance.channels.model.*;
    import com.mindalliance.channels.util.ElementHelper;
    import com.mindalliance.channels.vo.ArtifactVO;
    import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateArtifactCommand extends BaseDelegateCommand
	{
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateArtifactEvent = event as UpdateArtifactEvent;
            
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : ArtifactVO = (element.data as ArtifactVO);   
            if (model.isChanged) {
                log.debug("Updating Artifact model");

                data.name=evt.name;
                data.description = evt.description;
                data.categories = evt.categories; 
                data.product = evt.product;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Artifact");
                var delegate:ArtifactDelegate = new ArtifactDelegate( this );
                delegate.updateElement(data);
            }      
            
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("Artifact successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
                                // Update the element name in the artifact list
                var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('artifacts').data);
                el.name = channelsModel.getElementModel(id).data.name;
            } else {
                log.error("Update of Artifact " + result + " failed");
            }
        }
	}
}