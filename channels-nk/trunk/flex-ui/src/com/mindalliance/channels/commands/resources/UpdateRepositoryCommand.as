// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.resources.RepositoryDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.resources.UpdateRepositoryEvent;
    import com.mindalliance.channels.model.*;
    import com.mindalliance.channels.util.ElementHelper;
    import com.mindalliance.channels.vo.RepositoryVO;
    import com.mindalliance.channels.vo.common.ElementVO;
    
    public class UpdateRepositoryCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateRepositoryEvent = event as UpdateRepositoryEvent;
            
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : RepositoryVO = (element.data as RepositoryVO);   
            if (model.isChanged) {
                log.debug("Updating Repository model");
 
                data.name = evt.name;
                data.description = evt.description;
                data.categories = evt.categories;
                 data.organization = evt.organization;
                data.administrators = evt.administrators;
                data.contents = evt.contents;
                data.access = evt.access
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Repository");
                var delegate:RepositoryDelegate = new RepositoryDelegate( this );
                delegate.updateElement(data);
            }      
            
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("Repository successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
                                // Update the element name in the repository list
                var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('repositories').data);
                el.name = channelsModel.getElementModel(id).data.name;
            } else {
                log.error("Update of Repository " + result + " failed");
            }
        }
    }
}