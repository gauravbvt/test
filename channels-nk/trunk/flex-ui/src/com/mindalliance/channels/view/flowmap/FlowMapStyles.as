package com.mindalliance.channels.view.flowmap
{
	import com.yworks.graph.drawing.ArrowType;
	import com.yworks.graph.drawing.IEdgeStyle;
	import com.yworks.graph.drawing.ILabelStyle;
	import com.yworks.graph.drawing.INodeStyle;
	import com.yworks.graph.drawing.IPortStyle;
	import com.yworks.graph.drawing.ImageNodeStyle;
	import com.yworks.graph.drawing.ImageNodeStyleRenderer;
	import com.yworks.graph.drawing.PolylineEdgeStyle;
	import com.yworks.graph.drawing.ShapeNodeShape;
	import com.yworks.graph.drawing.ShapeNodeStyle;
	import com.yworks.graph.drawing.ShapeNodeStyleRenderer;
	import com.yworks.graph.drawing.SimpleLabelStyle;
	import com.yworks.graph.drawing.SimpleLabelStyleRenderer;
	import com.yworks.graph.drawing.SimplePortStyle;
	import com.yworks.graph.drawing.SimplePortStyleRenderer;
	import com.yworks.graph.model.DefaultArrow;
	import com.yworks.graph.model.ExteriorLabelModel;
	import com.yworks.graph.model.ILabelModel;
	import com.yworks.graph.model.ILabelModelParameter;
	import com.yworks.graph.model.InteriorLabelModel;
	import com.yworks.graph.model.RotatingEdgeLabelModel;
	
	import flash.display.Bitmap;
	import flash.display.Loader;
	import flash.display.Shape;
	import flash.net.URLRequest;
	
	import mx.controls.Alert;
	import mx.core.UITextFormat;
	import mx.graphics.IFill;
	import mx.graphics.IStroke;
	import mx.graphics.SolidColor;
	import mx.graphics.Stroke;
	import mx.managers.ISystemManager;
	import mx.utils.URLUtil;
	import com.yworks.util.CloneableBitmap;
	
	public class FlowMapStyles
	{
		
		public static const SCENARIO_STAGE_NODE_PADDING_X:Number = 30 ;
		
		public static const SCENARIO_STAGE_NODE_PADDING_Y:Number = 100 ;
		
		public static const VERTICAL_INTERNODE_GAP:Number = 100 ;
		
		public static const TASK_NODE_FILL:IFill = new SolidColor(0xFAF1C3, 0.5) ;
		
		public static const TASK_NODE_STROKE:Stroke = new Stroke(0xE7E4D3, 1.0) ;
		
		public static const EVENT_NODE_STROKE:Stroke = new Stroke(0xBEDF5D, 1.0) ;
		
		public static const EVENT_NODE_FILL:IFill = new SolidColor(0xD6EB9A, 0.5) ;
		
		public static const SELECTED_NODE_STROKE:Stroke = new Stroke(0xBBD9EE, 1.0) ;
		
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
						'See mx.core.UIComponent.') ;
		}
		
		private static var _taskNodeStyle:INodeStyle ;
		private static var _eventNodeStyle:INodeStyle ;
		private static var _repositoryNodeStyle:INodeStyle ;
		private static var _roleLabelStyle:ILabelStyle ;
		private static var _roleLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.north ;
		private static var _infoLabelStyle:ILabelStyle ;
		private static var _infoLabelModelParameter:ILabelModelParameter = InteriorLabelModel.south ;
		private static var _eventLabelStyle:ILabelStyle ;
		private static var _eventLabelModelParameter:ILabelModelParameter = InteriorLabelModel.center ;
		private static var _taskLabelStyle:ILabelStyle ;
		private static var _taskLabelModelParameter:ILabelModelParameter = InteriorLabelModel.center ;
		private static var _repositoryLabelStyle:ILabelStyle ;
		private static var _repositoryLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.south ;
		private static var _repositoryOwnerLabelStyle:ILabelStyle ;
		private static var _repositoryOwnerLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.north ;
		private static var _portStyle:IPortStyle ;
		private static var _hoverPortStyle:IPortStyle ;
		private static var _edgeStyle:IEdgeStyle ;
		private static var _selectedEdgeStyle:IEdgeStyle ;
		private static var _causeEdgeStyle:IEdgeStyle ;

		private static function _initNodeStyles():void {
			_checkSystemManager() ;
			_taskNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, TASK_NODE_STROKE, TASK_NODE_FILL) ;
			_eventNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.ELLIPSE, EVENT_NODE_STROKE, EVENT_NODE_FILL) ;
 			 var imgNodeStyle:ImageNodeStyle = new ImageNodeStyle(null, new ImageNodeStyleRenderer()) ;
 			 imgNodeStyle.url = "assets/images/data.png" ;
 			_repositoryNodeStyle = imgNodeStyle ;
		}
		
		public static function get taskNodeStyle():INodeStyle {
			return _taskNodeStyle ;
		}
		
		public static function get eventNodeStyle():INodeStyle {
			return _eventNodeStyle ;
		}
		
		public static function get repositoryNodeStyle():INodeStyle {
			return _repositoryNodeStyle ;
		}
		
		private static function _initNodeLabelStyles():void {
			_checkSystemManager() ;
			var uitf:UITextFormat = new UITextFormat(_systemManager, 'Verdana', 11, '0x000000', true, null, null, null, null, 'center', 5) ;
			_roleLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(), uitf) ;
			_taskLabelStyle = SimpleLabelStyle(_roleLabelStyle.clone()) ;
			_infoLabelStyle = SimpleLabelStyle(_roleLabelStyle.clone()) ;
			_eventLabelStyle = SimpleLabelStyle(_roleLabelStyle.clone()) ;
			_repositoryLabelStyle = SimpleLabelStyle(_roleLabelStyle.clone()) ;
		}
		
		public static function get repositoryLabelStyle():ILabelStyle {
			return _repositoryLabelStyle ;
		}
		
		public static function get repositoryLabelModelParameter():ILabelModelParameter {
			return _repositoryLabelModelParameter ;
		}
		
		public static function get roleLabelStyle():ILabelStyle {
			return _roleLabelStyle ;
		}
		
		public static function get roleLabelModelParameter():ILabelModelParameter {
			return _roleLabelModelParameter ;
		}
				
		public static function get taskLabelStyle():ILabelStyle {
			return _taskLabelStyle ;
		}
		
		public static function get taskLabelModelParameter():ILabelModelParameter {
			return _taskLabelModelParameter ;
		}
				
		public static function get infoLabelStyle():ILabelStyle {
			return _infoLabelStyle ;
		}
		
		public static function get infoLabelModelParameter():ILabelModelParameter {
			return _infoLabelModelParameter ;
		}
				
		public static function get eventLabelStyle():ILabelStyle {
			return _eventLabelStyle ;
		}
		
		public static function get eventLabelModelParameter():ILabelModelParameter {
			return _eventLabelModelParameter ;
		}
		
		public static function get repositoryOwnerLabelStyle():ILabelStyle {
			return _repositoryOwnerLabelStyle ;
		}
		
		public static function get repositoryOwnerLabelModelParameter():ILabelModelParameter {
			return _repositoryOwnerLabelModelParameter ;
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
				
		public static function get portStyle():IPortStyle {
			return _portStyle ;
		}
				
		private static function _initEdgeStyles():void {
			var es:PolylineEdgeStyle = new PolylineEdgeStyle() ;
			es.stroke = new Stroke(0xAAAAAA, 4.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0xAAAAAA,1.0), new SolidColor(0xAAAAAA), 4.0) ;
			_edgeStyle = IEdgeStyle(es.clone()) ;
			
			es.stroke = new Stroke(0x000000, 5.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0x005C9F, 1.0), new SolidColor(0x005C9F), 4.0) ;
			_selectedEdgeStyle = IEdgeStyle(es.clone()) ;
			
			es.stroke = new Stroke(0xCCFF33, 2.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DIAMOND, new Stroke(0xCCFF33, 1.0), new SolidColor(0xCCFF33), 4.0) ;
			_causeEdgeStyle = es.clone() as IEdgeStyle ;
		}
		
		public static function get edgeStyle():IEdgeStyle {
			return _edgeStyle ;
		}
		
		public static function get selectedEdgeStyle():IEdgeStyle {
			return _selectedEdgeStyle ;
		}
		
		public static function get causeEdgeStyle():IEdgeStyle {
			return _causeEdgeStyle ;
		}
	}
}