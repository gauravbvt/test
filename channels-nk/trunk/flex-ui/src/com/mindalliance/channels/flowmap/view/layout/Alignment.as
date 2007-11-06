package com.mindalliance.channels.flowmap.view.layout
{
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.INode;
	
	public class Alignment
	{	
		
		public static function alignLeft(graph:IGraph, nodes:Array, refX:Number):void {
			Alignment.align(graph, nodes, refX, BoundsHelper.setX, true, BoundsHelper.getX) ;
		}
		
		public static function alignTop(graph:IGraph, nodes:Array, refY:Number):void {
			Alignment.align(graph, nodes, refY, BoundsHelper.setY, true, BoundsHelper.getY) ;
		}
		
		public static function alignRight(graph:IGraph, nodes:Array, refX:Number):void {
			Alignment.align(graph, nodes, refX, BoundsHelper.setRight, false, BoundsHelper.getRight) ; 
		}
		
		public static function alignBottom(graph:IGraph, nodes:Array, refY:Number):void {
			Alignment.align(graph, nodes, refY, BoundsHelper.setBottom, false, BoundsHelper.getBottom) ;
		}
		
		public static function alignHorizontalCenter(graph:IGraph, nodes:Array, refX:Number):void {
			Alignment.align(graph, nodes, refX, BoundsHelper.setCenterX, true, BoundsHelper.getCenterX) ;
		}
		
		public static function alignVerticalCenter(graph:IGraph, nodes:Array, refY:Number):void {
			Alignment.align(graph, nodes, refY, BoundsHelper.setCenterY, true, BoundsHelper.getCenterX) ;
		}
		
		private static function align(graph:IGraph, nodes:Array, ref:Number, setAccessor:Function, 
							alignToMin:Boolean=true, getAccessor:Function=null):void {
			var v:Number = ref ;
			if (!v)
				v = alignToMin ? minMax(nodes, getAccessor)[0] : minMax(nodes, getAccessor)[1] ;
			
			for each (var node:INode in nodes)
				setAccessor(graph, node, v) ;
		}
		
		
		public static function minMax(nodes:Array, accessor:Function):Array {
			var min:Number = Infinity ;
			var max:Number = -Infinity ;
			
			for each (var node:INode in nodes) {
				min = Math.min(accessor(node), min) ;
				max = Math.max(accessor(node), max) ;
			}
			return [min, max] ;
		}
		
	}
}
	

