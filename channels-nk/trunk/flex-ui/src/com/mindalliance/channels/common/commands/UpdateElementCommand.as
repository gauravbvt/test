package com.mindalliance.channels.common.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.ElementAdapterFactory;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.common.business.UpdateElementDelegate;
	import com.mindalliance.channels.common.events.UpdateElementEvent;
	import com.mindalliance.channels.model.*;
	import com.mindalliance.channels.vo.common.ElementVO;
    
	public class UpdateElementCommand extends BaseDelegateCommand
	{      
	    override public function execute(event:CairngormEvent):void
        { 
            var evt:UpdateElementEvent = event as UpdateElementEvent;
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            if (model.isChanged) {
                var adapter : IElementAdapter = ElementAdapterFactory.getInstance().fromType(element.data);
                adapter.updateElement(element.data, evt.parameters);
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                var delegate:UpdateElementDelegate = new UpdateElementDelegate( this, element.data );
                delegate.update();
            }        
            
        }
        
        override public function result(data:Object):void
        {
            if (data["data"]==true) {
            	var element : ElementVO = data["element"] as ElementVO;
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(element.id).dirty = false;
            
                channelsModel.replaceElementInLists(element);
            } else {
                log.error("Update of " + data["id"] + " failed");
            }
            
        }
	}
}