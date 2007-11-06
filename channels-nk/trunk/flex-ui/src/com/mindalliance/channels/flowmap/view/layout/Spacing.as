package com.mindalliance.channels.flowmap.view.layout
{
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.INode;
	
	public class Spacing
	{
		internal static function spaceHorizontally(graph:IGraph, nodes:Array, width:Number):void {
			var prev:INode = nodes[0] as INode ;
			for each (var node:INode in nodes.slice(1))
				BoundsHelper.setX(graph, node, BoundsHelper.getX(prev) 
												+ BoundsHelper.getWidth(prev) 
												+ width) ;
		}
		
		internal static function spaceVertically(graph:IGraph, nodes:Array, height:Number, startY:Number):void {
			var prev:INode = nodes[0] as INode ;
			if (startY)
				BoundsHelper.setY(graph, prev, startY) ;
			for each (var node:INode in nodes.slice(1)) {
				var prevY:Number = BoundsHelper.getY(prev) ;
				var prevHeight:Number = BoundsHelper.getHeight(prev) ; 
				var newY:Number = prevY + prevHeight + height ;				
				BoundsHelper.setY(graph, node, newY) ;
				prev = node ;
			}
		}
	}
}