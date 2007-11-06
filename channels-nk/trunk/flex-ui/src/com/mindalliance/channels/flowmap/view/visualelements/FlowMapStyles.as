package com.mindalliance.channels.flowmap.view.visualelements
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
	import com.yworks.graph.model.ILabelModelParameter;
	import com.yworks.graph.model.InteriorLabelModel;
	
	import mx.core.UITextFormat;
	import mx.graphics.IFill;
	import mx.graphics.SolidColor;
	import mx.graphics.Stroke;
	import mx.managers.ISystemManager;
	
	public class FlowMapStyles
	{
		
		public static const TASK_NODE_FILL:IFill = new SolidColor(0xFAF1C3, 0.5) ;
		
		public static const TASK_NODE_STROKE:Stroke = new Stroke(0xE7E4D3, 1.0) ;
		
		public static const EVENT_NODE_STROKE:Stroke = new Stroke(0xBEDF5D, 1.0) ;
		
		public static const EVENT_NODE_FILL:IFill = new SolidColor(0xD6EB9A, 0.5) ;
		
		public static const SHARING_NEED_NODE_STROKE:Stroke = new Stroke(0xAAAAA, 1.0) ;
		
		public static const SHARING_NEED_NODE_FILL:IFill = new SolidColor(0xEBF4FA, 0.5) ;
		
		public static const SELECTED_NODE_STROKE:Stroke = new Stroke(0xBBD9EE, 2.0) ;
		
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
		
		public static var taskNodeStyle:INodeStyle ;
		public static var eventNodeStyle:INodeStyle ;
		public static var repositoryNodeStyle:INodeStyle ;
		public static var sharingNeedNodeStyle:INodeStyle ;
		public static var roleNodeStyle:INodeStyle ;
		public static var roleLabelStyle:ILabelStyle ;
		public static var roleLabelModelParameter:ILabelModelParameter = InteriorLabelModel.northWest ;
		public static var sharingNeedAboutLabelStyle:ILabelStyle ;
		public static var sharingNeedAboutLabelModelParameter:ILabelModelParameter = InteriorLabelModel.north ;
		public static var sharingNeedWhatLabelStyle:ILabelStyle ;
		public static var sharingNeedWhatLabelModelParameter:ILabelModelParameter = InteriorLabelModel.west ;
		public static var eventLabelStyle:ILabelStyle ;
		public static var eventLabelModelParameter:ILabelModelParameter = InteriorLabelModel.center ;
		public static var taskLabelStyle:ILabelStyle ;
		public static var taskLabelModelParameter:ILabelModelParameter = InteriorLabelModel.center ;
		public static var repositoryLabelStyle:ILabelStyle ;
		public static var repositoryLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.north
		public static var repositoryOwnerLabelStyle:ILabelStyle ;
		public static var repositoryOwnerLabelModelParameter:ILabelModelParameter = ExteriorLabelModel.south ;
		public static var portStyle:IPortStyle ;
		public static var hoverPortStyle:IPortStyle ;
		public static var edgeStyle:IEdgeStyle ;
		public static var selectedEdgeStyle:IEdgeStyle ;
		public static var causeEdgeStyle:IEdgeStyle ;

		private static function _initNodeStyles():void {
			_checkSystemManager() ;
			taskNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, TASK_NODE_STROKE, TASK_NODE_FILL) ;
			eventNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, EVENT_NODE_STROKE, EVENT_NODE_FILL) ;
			sharingNeedNodeStyle = new ShapeNodeStyle(new ShapeNodeStyleRenderer(), ShapeNodeShape.roundrectangle, SHARING_NEED_NODE_STROKE, SHARING_NEED_NODE_FILL) ;
 			 var imgNodeStyle:ImageNodeStyle = new ImageNodeStyle(null, new ImageNodeStyleRenderer()) ;
 			 imgNodeStyle.url = "assets/images/data.png" ;
 			repositoryNodeStyle = imgNodeStyle.clone() as ImageNodeStyle ;
 			imgNodeStyle.url = "assets/images/human.png" ;
 			roleNodeStyle = imgNodeStyle.clone() as ImageNodeStyle ;
		}
			
		private static function _initNodeLabelStyles():void {
			_checkSystemManager() ;
			var uitf:UITextFormat = new UITextFormat(_systemManager, 'Georgia', 10, '0x000000', true, null, null, null, null, 'center', 5, 5) ;
			roleLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(), uitf) ;
			taskLabelStyle = SimpleLabelStyle(roleLabelStyle.clone()) ;
			eventLabelStyle = SimpleLabelStyle(roleLabelStyle.clone()) ;
			repositoryLabelStyle = SimpleLabelStyle(roleLabelStyle.clone()) ;
			sharingNeedAboutLabelStyle = SimpleLabelStyle(roleLabelStyle.clone()) ;
			
			sharingNeedWhatLabelStyle = new SimpleLabelStyle(new SimpleLabelStyleRenderer(),
				new UITextFormat(_systemManager, 'Georgia', 10, '0x000000', true, null, null, null, null, 'left', 5)) ;
		}
				
		private static function _initPortStyle():void {
			var ps:SimplePortStyle = new SimplePortStyle(new SimplePortStyleRenderer()) ;
			/* ps.radius = 1.0 ;
			ps.stroke = new Stroke(0x004100, 1.0) ;
			portStyle = IPortStyle(ps.clone()) ;
			 ps.radius = 3.0 ;
			ps.stroke.weight = 3.0 ;
			hoverPortStyle = IPortStyle(ps.clone()) ; */
			portStyle = null ;
		}
				
		private static function _initEdgeStyles():void {
			var es:PolylineEdgeStyle = new PolylineEdgeStyle() ;
			es.stroke = new Stroke(0xAAAAAA, 4.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0xAAAAAA,1.0), new SolidColor(0xAAAAAA), 4.0, 2.0) ;
			edgeStyle = IEdgeStyle(es.clone()) ;
			
			es.stroke = new Stroke(0x000000, 5.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.DEFAULT, new Stroke(0x005C9F, 1.0), new SolidColor(0x005C9F), 4.0, 2.0) ;
			selectedEdgeStyle = IEdgeStyle(es.clone()) ;
			
			es.stroke = new Stroke(0xCCFF33, 2.0) ;
			es.targetArrow = DefaultArrow.create(ArrowType.SPEARHEAD, new Stroke(0xCCFF33, 1.0), new SolidColor(0x0), 4.0, 2.0) ;
			causeEdgeStyle = es.clone() as IEdgeStyle ;
		}
			
	}
}