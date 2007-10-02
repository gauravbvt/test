package com.mindalliance.channels.view.flowmap
{
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.yworks.canvas.geom.IPoint;
	import com.yworks.canvas.geom.ImmutablePoint;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.support.Iterator;
	import com.yworks.ui.GraphCanvasComponent;
	
	import flash.geom.Point;
	
	public class FlowMapLayoutHelper
	{
		
		public static var graphCanvas:GraphCanvasComponent ;
		
		public static function updateNodeBounds(graph:IGraph, node:INode):void {
			var iter:Iterator = node.labels.iterator() ;

			var minRequiredWidth:Number = 0 ;
			var minRequiredHeight:Number = 0 ;

			while (iter.hasNext()) {
				var label:ILabel = iter.next() as ILabel ;

				var labelWidth:Number = label.layout.width + 20 ;
				if (minRequiredWidth < labelWidth)
					minRequiredWidth = labelWidth ;

				var labelHeight:Number = label.layout.height + 20 ;
				if (minRequiredHeight < labelHeight)
					minRequiredHeight = labelHeight; 
			}

			if (node.layout.width < minRequiredWidth)
				graph.setBounds(node, node.layout.x, node.layout.y, minRequiredWidth, node.layout.height) ;

			if (node.layout.height < minRequiredHeight)
				graph.setBounds(node, node.layout.x, node.layout.y, node.layout.width, minRequiredHeight) ;
				
			graphCanvas.forceRepaint() ;
		}
		
		public static function getLocationForNewNode2():IPoint {
			var gc:GraphCanvasComponent = graphCanvas ;
		
			var dx:Number = (Math.random() > 0.5 ? 1 : -1) * Math.random() * gc.width/3 ;

			var dy:Number = (Math.random() > 0.5 ? 1 : -1) * Math.random() * gc.height/3 ;

			return new ImmutablePoint(gc.center.x + dx, gc.center.y + dy) ;
		}
		
		public static function getLocationForNewNode(mapper:GraphDataMapper, phaseID:String):IPoint {
				return null ;
/* 			var phase:Phase = _mapperHelper.phaseMapper.lookupValue(phaseID) as Phase ;
			var maxY:Number = 0 ;
			for (var i:int=0; i < phase.nodes.length ; i++) {
				var node:INode = INode(nodes.getItemAt(i)) ;
				if (node.layout.y > maxY)
					maxY = node.layout.y ;
			}
			var nodeX:Number = ss.x + FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_X ;
			var nodeY:Number = (maxY == 0 ? FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_Y : maxY + FlowMapStyles.VERTICAL_INTERNODE_GAP) ;
			return new ImmutablePoint(nodeX, nodeY) ; */
		}
		
		public static function updatePhaseBounds(mapper:GraphDataMapper, phaseID:String):void {
			return ;
/* 			var desiredWidth:Number = 0 ;
			var maxY:Number = 0 ;
			for (var i:int=0 ; i < nodes.length ; i++) {
				var rect:IRectangle = INode(nodes.getItemAt(i)).layout ;
				if (rect.width >= desiredWidth)
					desiredWidth = rect.width ;
				var desiredY:Number = rect.y + rect.height + FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_Y ;
				if (desiredY >= maxY)
					maxY = desiredY ;
			}
			desiredWidth = desiredWidth + FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_X * 2 ;
			if (ss.width < desiredWidth)
				ss.width = desiredWidth ;
			var desiredHeight:Number = maxY - ss.y + FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_Y * 2;
			if (ss.height < desiredHeight) {
				var stages:Iterator = DictionaryMapper(_mapperHelper.scenarioStageByIDMapper).values() ;
				while (stages.hasNext()) {
					var stage:ScenarioStage = ScenarioStage(stages.next()) ;
					stage.height = desiredHeight ;
				}
			} */
		}

	}
}