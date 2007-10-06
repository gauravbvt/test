package com.mindalliance.channels.view.flowmap.layout
{
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.INode;
	
	public class BoundsHelper
	{
		internal static function resizeLeftward(graph:IGraph, node:INode, dx:Number):void {
			//
		}
		
		internal static function resizeRightward(graph:IGraph, node:INode, dx:Number):void {
			//
		}

		internal static function resizeUpward(graph:IGraph, node:INode, dx:Number):void {
			//
		}
		
		internal static function resizeDownward(graph:IGraph, node:INode, dx:Number):void {
			//
		}
		
		internal static function setX(graph:IGraph, node:INode, x:Number):void {
			var rect:IRectangle = node.layout ;
			graph.setBounds(node, x, rect.y, rect.width, rect.height) ;
		}

		internal static function setY(graph:IGraph, node:INode, y:Number):void {
			var rect:IRectangle = node.layout ;
			graph.setBounds(node, rect.x, y, rect.width, rect.height) ;
		}

		internal static function setWidth(graph:IGraph, node:INode, width:Number):void {
			var rect:IRectangle = node.layout ;
			graph.setBounds(node, rect.x, rect.y, width, rect.height) ;
		}

		internal static function setHeight(graph:IGraph, node:INode, height:Number):void {
			var rect:IRectangle = node.layout ;
			graph.setBounds(node, rect.x, rect.y, rect.width, height) ;
		}
		
		internal static function moveHorizontalBy(graph:IGraph, node:INode, value:Number):void {
			BoundsHelper.setX(graph, node, node.layout.x + value) ;
		}
		
		internal static function moveVerticalBy(graph:IGraph, node:INode, value:Number):void {
			BoundsHelper.setY(graph, node, node.layout.y + value) ;
		}
		
		internal static function setRight(graph:IGraph, node:INode, x:Number):void {
			BoundsHelper.setX(graph, node, x - node.layout.width) ;
		}
		
		internal static function setBottom(graph:IGraph, node:INode, y:Number):void {
			BoundsHelper.setY(graph, node, y - node.layout.height) ;
		}
		
		internal static function setCenterX(graph:IGraph, node:INode, x:Number):void {
			BoundsHelper.moveHorizontalBy(graph, node, x - BoundsHelper.getCenterX(node)) ;
		}
		
		internal static function setCenterY(graph:IGraph, node:INode, y:Number):void {
			BoundsHelper.moveVerticalBy(graph, node, y - BoundsHelper.getCenterY(node)) ;
		}
		
		internal static function getX(node:INode):Number {
			return node.layout.x ;
		}
		
		internal static function getY(node:INode):Number {
			return node.layout.y ;
		}
		
		internal static function getWidth(node:INode):Number {
			return node.layout.width ;
		}
		
		internal static function getHeight(node:INode):Number {
			return node.layout.height ;
		}
		
		internal static function getRight(node:INode):Number {
			return node.layout.x + node.layout.width ;
		}
		
		internal static function getBottom(node:INode):Number {
			return node.layout.y + node.layout.height ;
		}
		
		internal static function getCenterX(node:INode):Number {
			return node.layout.x + node.layout.width / 2 ;
		}
		
		internal static function getCenterY(node:INode):Number {
			return node.layout.y + node.layout.height / 2 ;
		}
		
	}
}