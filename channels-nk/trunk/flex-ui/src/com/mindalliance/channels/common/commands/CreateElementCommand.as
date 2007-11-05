package com.mindalliance.channels.common.commands
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.common.business.CreateElementDelegate;
	import com.mindalliance.channels.common.business.ElementAdapterFactory;
	import com.mindalliance.channels.common.events.CreateElementEvent;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class CreateElementCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateElementEvent = event as CreateElementEvent;
            var delegate:CreateElementDelegate = new CreateElementDelegate( this,
                                                        evt.elementType,
                                                        evt.parameters );
            delegate.createElement();
        }
        
        override public function result(data:Object):void
        {
            var result:ElementVO = data["data"] as ElementVO;
            if (result!=null) {
            	ElementAdapterFactory.getInstance().fromType(result).postCreate(result, data["parameters"]);
            }
        }
		
	}
}