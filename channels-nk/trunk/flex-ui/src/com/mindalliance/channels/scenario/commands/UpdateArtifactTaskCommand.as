// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.commands
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.common.commands.BaseCommand;
    import com.mindalliance.channels.scenario.events.*;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.util.ElementHelper;
    import com.mindalliance.channels.vo.ArtifactVO;
    
    public class UpdateArtifactTaskCommand extends BaseCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateArtifactTaskEvent = event as UpdateArtifactTaskEvent;
            var el : ArtifactVO = channelsModel.getElementModel(evt.id).data as ArtifactVO;
            var model : EditorModel  =channelsModel.getEditorModel();
            model.id=evt.id;
            model.isChanged = true;
            
            CairngormHelper.fireEvent(new UpdateArtifactEvent(model, el.name,el.description,el.categories,ElementHelper.findElementById(evt.taskId, channelsModel.getElementListModel('tasks').data)));
        }
        
    }
}