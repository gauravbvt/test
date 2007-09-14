package com.mindalliance.channels.view.flowmap
{
	import com.yworks.canvas.drawing.IHitTestable;
	import com.yworks.canvas.drawing.IPaintable;
	import com.yworks.canvas.drawing.IBoundsProvider;
	import com.yworks.canvas.ICanvasObjectDescriptor;
	import com.yworks.canvas.ICanvasObject;

	public class PhaseCanvasObjectDescriptor implements ICanvasObjectDescriptor
	{
		public function getPaintable(forUserObject:Object):IPaintable
		{
			return Phase(forUserObject) ;
		}
		
		public function getHitTestable(forUserObject:Object):IHitTestable
		{
			return Phase(forUserObject) ;
		}
		
		public function getBoundsProvider(forUserObject:Object):IBoundsProvider
		{
			return Phase(forUserObject) ;
		}
		
		public function isDirty(obj:ICanvasObject):Boolean
		{
			return true ;
		}
		
	}
}