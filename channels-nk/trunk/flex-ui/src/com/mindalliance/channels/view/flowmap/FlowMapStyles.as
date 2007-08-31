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
	import com.yworks.graph.drawing.IPortStyle;
	import com.yworks.graph.drawing.SimplePortStyle;
	import com.yworks.graph.drawing.SimplePortStyleRenderer;
	
	public class FlowMapStyles
	{
		
		public static const SCENARIO_STAGE_NODE_PADDING_X:Number = 30 ;
		public static const SCENARIO_STAGE_NODE_PADDING_Y:Number = 100 ;
		public static const VERTICAL_INTERNODE_GAP:Number = 100 ;
		
		private static var _systemManager:ISystemManager ;
		public static function set systemManager(systemManager:ISystemManager):void {
			_systemManager = systemManager ;
			_initNodeStyles() ;
			_initNodeLabelStyles() ;
			_initPortStyle() ;
			_initEdgeStyles() ;
		}
		
		private static function _checkSystemManager():void {
			if (_systemManager == null)
				throw new Error('SystemManager not initialized. ' + 
						'Set FlowMapStyles.systemManager property. ' + 
						'See UIComponent.') ;
		}
		
		private static var _nodeStyle:INodeStyle ;
		private static var _selectedNodeStyle:INodeStyle ;
		
		private static function _initNodeStyles():void {
			_checkSystemManager() ;
			_nodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, new Stroke(0xAAAAAA, 1.0), new SolidColor(0xFFFEDF)) ;
			_selectedNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, new Stroke(0x888888, 2.0), new SolidColor(0xFFFEDF)) ;
		}
		
		public static function get nodeStyle():INodeStyle {
			return _nodeStyle ;
		}
		
		public static function get selectedNodeStyle():INodeStyle {
			return _selectedNodeStyle ;
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
		private static var _eventLabelModelParameter:ILabelModelParameter = InteriorLabelModel.east ;
		
		public static function get eventLabelStyle():ILabelStyle {
			return _eventLabelStyle ;
		}
		
		public static function get eventLabelModelParameter():ILabelModelParameter {
			return _eventLabelModelParameter ;
		}
		
		private static function _initPortStyle():void {
			var ps:SimplePortStyle = new SimplePortStyle(new SimplePortStyleRenderer()) ;
			ps.radius = 2.0 ;
			ps.stroke = new Stroke(0x004100, 2.0) ;
			_portStyle = IPortStyle(ps.clone()) ;
			ps.radius = 3.0 ;
			ps.stroke.weight = 3.0 ;
			_hoverPortStyle = IPortStyle(ps.clone()) ;
		}
		
		private static var _portStyle:IPortStyle ;
		private static var _hoverPortStyle:IPortStyle ;
		
		public static function get portStyle():IPortStyle {
			return _portStyle ;
		}
		
		private static var _edgeStyle:IEdgeStyle ;
		private static var _selectedEdgeStyle:IEdgeStyle ;
		
		private static function _initEdgeStyles():void {
			var es:PolylineEdgeStyle = new PolylineEdgeStyle() ;
			es.stroke = new Stroke(0xAAAAAA, 2.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0xAAAAAA,1.0), new SolidColor(0xAAAAAA), 2.0) ;
			_edgeStyle = IEdgeStyle(es.clone()) ;
			
			es.stroke = new Stroke(0x000000, 3.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0x005C9F, 1.0), new SolidColor(0x005C9F), 2.0) ;
			_selectedEdgeStyle = IEdgeStyle(es.clone()) ;
		}
		
		public static function get edgeStyle():IEdgeStyle {
			return _edgeStyle ;
		}
		
		public static function get selectedEdgeStyle():IEdgeStyle {
			return _selectedEdgeStyle ;
		}
	}
}