package com.mindalliance.channels.model.flowmap
{
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import mx.events.PropertyChangeEvent;
	import mx.events.PropertyChangeEventKind;
	
	public class BaseModel
	{
		protected var model:ChannelsModelLocator ;
		
		public function BaseModel():void {
			model = ChannelsModelLocator.getInstance() ;
		}
		
		protected function extractElementVO(item:Object, callback:Function):void {
			var elemVO:ElementVO = item as ElementVO ;
			if (!elemVO)
				return ;
			callback(elemVO) ;
		}
		
		protected function examinePropertyChange(event:Object, 
												callback:Function, 
												property:String='name', 
												propertyChangeEventKind:String=PropertyChangeEventKind.UPDATE):void {
			var propChangeEvent:PropertyChangeEvent = event as PropertyChangeEvent ;
			if (!propChangeEvent)
				return ;
			if (propChangeEvent.kind != propertyChangeEventKind)
				return ;
			if (propChangeEvent.property == property) {
				var elemVO:ElementVO = propChangeEvent.source as ElementVO ;
				callback(elemVO, propChangeEvent.newValue) ;
			}
		}		
	}
}