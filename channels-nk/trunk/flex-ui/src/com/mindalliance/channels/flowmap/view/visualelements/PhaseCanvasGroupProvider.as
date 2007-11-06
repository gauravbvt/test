package com.mindalliance.channels.flowmap.view.visualelements
{
	import com.yworks.canvas.CanvasComponent;
	import com.yworks.canvas.model.ICanvasGroupProvider;
	import com.yworks.canvas.ICanvasObjectGroup;

	public class ScenarioStageCanvasGroupProvider implements ICanvasGroupProvider
	{
		private static var cog:ICanvasObjectGroup ;
		
		public static function setCanvasObjectGroup(cog:ICanvasObjectGroup):void {
			ScenarioStageCanvasGroupProvider.cog = cog ;
		}
		
		public function getCanvasObjectGroup(canvas:CanvasComponent, forItem:Object):ICanvasObjectGroup
		{
			return ScenarioStageCanvasGroupProvider.cog ;
		}
		
	}
}