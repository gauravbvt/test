package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.drawing.INodeStyle;
	import com.yworks.graph.drawing.ShapeNodeStyle;
	import com.yworks.graph.drawing.ShapeNodeStyleRenderer;
	import com.yworks.graph.drawing.ShapeNodeShape;
	import mx.graphics.IStroke;
	import mx.graphics.Stroke;
	import mx.graphics.IFill;
	import mx.graphics.SolidColor;
	import com.yworks.graph.drawing.ILabelStyle;
	import com.yworks.graph.drawing.SimpleLabelStyle;
	import com.yworks.graph.drawing.SimpleLabelStyleRenderer;
	import mx.core.UITextFormat;
	import com.yworks.graph.model.ExteriorLabelModel;
	import com.yworks.graph.model.InteriorLabelModel;
	import com.yworks.graph.model.ILabelModel;
	import com.yworks.graph.model.ILabelModelParameter;
	import com.yworks.graph.drawing.IEdgeStyle;
	import com.yworks.graph.model.RotatingEdgeLabelModel;
	import com.yworks.graph.drawing.PolylineEdgeStyle;
	import mx.managers.ISystemManager;
	import com.yworks.graph.drawing.ArrowType;
	import com.yworks.graph.model.DefaultArrow;
	
	public class FlowMapStyles
	{
		
		private static var _systemManager:ISystemManager ;
		public static function set systemManager(systemManager:ISystemManager):void {
			_systemManager = systemManager ;
			_initNodeStyles() ;
			_initNodeLabelStyles() ;
			_initEdgeStyles() ;
		}
		
		private static function _checkSystemManager():void {
			if (_systemManager == null)
				throw new Error('SystemManager not initialized. ' + 
						'Set FlowMapStyles.systemManager property. ' + 
						'See UIComponent.') ;
		}
		
		private static var _nodeStyle:INodeStyle ;
		private static var _nodeStroke:IStroke ;
		private static var _nodeFill:IFill ;
		
		private static function _initNodeStyles():void {
			_checkSystemManager() ;
			_nodeStroke = new Stroke(0xAAAAAA, 1.0) ;
			_nodeFill = new SolidColor(0xFFFEDF) ;
			_nodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, _nodeStroke, _nodeFill) ;
		}
		
		public static function get nodeStyle():INodeStyle {
			return _nodeStyle ;
		}
		
		private static function _initNodeLabelStyles():void {
			_checkSystemManager() ;
			_roleLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(), 
					new UITextFormat(_systemManager, 'Verdana', 12, '0x000000', true, null, null, null, null, 'center', 5)) ;
			_taskLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(), 
					new UITextFormat(_systemManager, 'Verdana', 11, '0x2A1192', true, null, null, null, null, null, 5)) ;
			_infoLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(),
			new UITextFormat(_systemManager, 'Verdana', 11, '0x000000', false, null, null, null, null, 'right', null, 5)) ;
			_eventLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(),
			new UITextFormat(_systemManager, 'Verdana', 11, '0x000000', false, null, null, null, null, 'right', null, 5)) ;
		}

		private static var _roleLabelStyle:ILabelStyle ;
		private static var _roleLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.north ;
		
		public static function get roleLabelStyle():ILabelStyle {
			return _roleLabelStyle ;
		}
		
		public static function get roleLabelModelParameter():ILabelModelParameter {
			return _roleLabelModelParameter ;
		}
		
		private static var _taskLabelStyle:ILabelStyle ;
		private static var _taskLabelModelParameter:ILabelModelParameter = InteriorLabelModel.west ;
		
		public static function get taskLabelStyle():ILabelStyle {
			return _taskLabelStyle ;
		}
		
		public static function get taskLabelModelParameter():ILabelModelParameter {
			return _taskLabelModelParameter ;
		}
		
		private static var _infoLabelStyle:ILabelStyle ;
		private static var _infoLabelModelParameter:ILabelModelParameter = InteriorLabelModel.east ;
		
		public static function get infoLabelStyle():ILabelStyle {
			return _infoLabelStyle ;
		}
		
		public static function get infoLabelModelParameter():ILabelModelParameter {
			return _infoLabelModelParameter ;
		}
		
		private static var _eventLabelStyle:ILabelStyle ;
		private static var _eventLabelModelParameter:ILabelModelParameter  ;
		
		public static function get eventLabelStyle():ILabelStyle {
			return _eventLabelStyle ;
		}
		
		public static function get eventLabelModelParameter():ILabelModelParameter {
			return _eventLabelModelParameter ;
		}
		
		private static var _edgeStyle:IEdgeStyle ;
		
		private static function _initEdgeStyles():void {
			var es:PolylineEdgeStyle = new PolylineEdgeStyle() ;
			es.stroke = new Stroke(0xAAAAAA, 2.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0xAAAAAA,2.0), new SolidColor(0xAAAAAA)) ;
			_edgeStyle = IEdgeStyle(es.clone()) ;
		}
		
		public static function get edgeStyle():IEdgeStyle {
			return _edgeStyle ;
		}
	}
}