package com.mindalliance.channels.view.flowmap
{
	import com.yworks.canvas.drawing.RectangularSelectionPaintable;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.DefaultNode;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.ISelectionPaintable;
	
	import flash.geom.Rectangle;
	
	import mx.graphics.SolidColor;
	import mx.graphics.Stroke;

	public class DefaultNodeSelectionPaintable extends RectangularSelectionPaintable
	{
		public function DefaultNodeSelectionPaintable(bounds:IRectangle)
		{
			super(bounds) ;
		}
		
		public static function createAndRegisterFor(node:DefaultNode):DefaultNodeSelectionPaintable {
			var sp:DefaultNodeSelectionPaintable = new DefaultNodeSelectionPaintable(node.layout) ;
			sp.stroke = FlowMapStyles.SELECTED_NODE_STROKE ;
			node.registerLookup(ISelectionPaintable, sp) ;
			return sp ;
		}
		
	}
}