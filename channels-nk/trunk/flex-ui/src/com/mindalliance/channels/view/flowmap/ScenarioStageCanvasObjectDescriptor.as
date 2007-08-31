package com.mindalliance.channels.view.flowmap
{
	import com.yworks.canvas.drawing.IHitTestable;
	import com.yworks.canvas.drawing.IPaintable;
	import com.yworks.canvas.drawing.IBoundsProvider;
	import com.yworks.canvas.ICanvasObjectDescriptor;
	import com.yworks.canvas.ICanvasObject;

	public class ScenarioStageCanvasObjectDescriptor implements ICanvasObjectDescriptor
	{
		public function getPaintable(forUserObject:Object):IPaintable
		{
			return ScenarioStage(forUserObject) ;
		}
		
		public function getHitTestable(forUserObject:Object):IHitTestable
		{
			return ScenarioStage(forUserObject) ;
		}
		
		public function getBoundsProvider(forUserObject:Object):IBoundsProvider
		{
			return ScenarioStage(forUserObject) ;
		}
		
		public function isDirty(obj:ICanvasObject):Boolean
		{
			return true ;
		}
		
	}
}