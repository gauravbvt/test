package com.mindalliance.channels.view.flowmap
{
    import com.mindalliance.channels.business.application.ScenarioDelegate;
    import com.mindalliance.channels.view.UtilFuncs;
    import com.yworks.canvas.ICanvasObject;
    import com.yworks.canvas.ICanvasObjectDescriptor;
    import com.yworks.canvas.ICanvasObjectGroup;
    import com.yworks.canvas.geom.IOrientedRectangle;
    import com.yworks.canvas.geom.IPoint;
    import com.yworks.canvas.geom.IRectangle;
    import com.yworks.canvas.input.ClickEvent;
    import com.yworks.canvas.input.MainInputMode;
    import com.yworks.canvas.input.MouseHoverInputMode;
    import com.yworks.canvas.model.DefaultCollectionModel;
    import com.yworks.canvas.model.IModelItem;
    import com.yworks.graph.drawing.ILabelStyle;
    import com.yworks.graph.drawing.INodeStyle;
    import com.yworks.graph.input.GraphEditorInputMode;
    import com.yworks.graph.model.DefaultEdge;
    import com.yworks.graph.model.DefaultGraph;
    import com.yworks.graph.model.DefaultNode;
    import com.yworks.graph.model.DefaultSelectionModel;
    import com.yworks.graph.model.GraphSelection;
    import com.yworks.graph.model.IEdge;
    import com.yworks.graph.model.IGraph;
    import com.yworks.graph.model.ILabel;
    import com.yworks.graph.model.ILabelCollection;
    import com.yworks.graph.model.IMapperRegistry;
    import com.yworks.graph.model.INode;
    import com.yworks.graph.model.IPort;
    import com.yworks.graph.model.IPortCandidateProvider;
    import com.yworks.graph.model.IPortCollection;
    import com.yworks.graph.model.SelectionEvent;
    import com.yworks.support.DictionaryMapper;
    import com.yworks.support.IMapper;
    import com.yworks.support.Iterable;
    import com.yworks.support.Iterator;
    import com.yworks.ui.GraphCanvasComponent;
    import com.yworks.util.Util;
    
    import mx.collections.ArrayCollection;
    import mx.controls.Alert;
    import mx.graphics.Stroke;
    import com.yworks.canvas.geom.ImmutablePoint;

    public class FlowMap
    {
    	
    	public static const EVENT_ITEM_SELECT:String = "itemSelect" ;
    	public static const EVENT_ITEM_DESELECT:String = "itemDeselect" ;
    	
    	private static var _graphCanvas:GraphCanvasComponent ;
    	private static var _graph:DefaultGraph ;
    	private static var _graphSelection:GraphSelection ;
    	private static var _geim:CustomGraphEditorInputMode ;
    	private static var _portCandidateProvider:IPortCandidateProvider ;
    	
    	private static var _scenarioStageCanvasObjectGroup:ICanvasObjectGroup ;
    	private static var _scenarioStageCanvasObjectDescriptor:ICanvasObjectDescriptor ;
    	
    	private static var _mapperHelper:GraphMapperHelper ;
    	
		public static function initialize(graphCanvas:GraphCanvasComponent):void {
			_graphCanvas = graphCanvas ;
			_graphCanvas.treeDirty = true ;

			_scenarioStageCanvasObjectGroup = _graphCanvas.addGroup() ;
			_scenarioStageCanvasObjectDescriptor = new ScenarioStageCanvasObjectDescriptor() ;
			_scenarioStageCanvasObjectGroup.descriptor = _scenarioStageCanvasObjectDescriptor ;
						
			_graph = new DefaultGraph() ;
			_graphCanvas.graph = _graph ;
			_graph.autoCleanupPorts = false ;
			_graph.usePortCandidateProviders = true ;
			_graph.shareDefaultEdgeStyleInstance = true ;
			_graph.shareDefaultNodeLabelStyleInstance = true ;
			_graph.shareDefaultPortStyleInstance = true ;
			_graph.shareDefaultNodeStyleInstance = true ;
			_graph.shareDefaultEdgeLabelStyleInstance = true ;
			
			_portCandidateProvider = new LimitedPortCandidateProvider() ;
			
			_graphSelection = new GraphSelection(_graph) ;
			_graphSelection.addEventListener(SelectionEvent.SELECT, _itemSelected) ;
			_graphSelection.addEventListener(SelectionEvent.DESELECT, _itemDeselected) ;
			
			_geim = new CustomGraphEditorInputMode(_graph, _graphSelection) ;
			_geim.configure(_graphCanvas) ;
			
			FlowMapStyles.systemManager = _graphCanvas.systemManager ;
			_graph.defaultNodeStyle = FlowMapStyles.nodeStyle ;
  			_graph.defaultEdgeStyle = FlowMapStyles.edgeStyle ;
			_graph.defaultPortStyle = FlowMapStyles.portStyle ;
			
			_mapperHelper = GraphMapperHelper.getInstance() ;
			_mapperHelper.initialize(_graph.mapperRegistry) ;
		}
		
		public static function get selectedScenarioStageID():String {
			var ss:ScenarioStage = _geim.selectedScenarioStage ;
			if (ss == null)
				return null ;
			return String(_mapperHelper.itemIDByInstanceMapper.lookupValue(ss)) ;
		}
		
		public static function addStage(stageID:String, stageName:String):void {
			var ss:ScenarioStage = ScenarioStage.createScenarioStage(stageName) ;
			ss.width = 200 ;
			ss.height = _graphCanvas.height ;
			_mapperHelper.scenarioStageByIDMapper.mapValue(stageID, ss) ;
			_mapperHelper.nodesByScenarioStageIDMapper.mapValue(stageID, new ArrayCollection()) ;
			_mapperHelper.itemIDByInstanceMapper.mapValue(ss, stageID) ;
			_graphCanvas.addCanvasObject(ss, _scenarioStageCanvasObjectDescriptor, _scenarioStageCanvasObjectGroup) ;
		}
		
		protected static function _itemSelected(event:SelectionEvent):void {
			if (event.item is IEdge) {
 				var de:DefaultEdge = DefaultEdge(event.item) ;
 				de.style = FlowMapStyles.selectedEdgeStyle ;
			}
			else if (event.item is INode) {
				var dn:DefaultNode = DefaultNode(event.item) ;
				dn.style = FlowMapStyles.selectedNodeStyle ;
			}
			_graphCanvas.forceRepaint();
		}
		
		protected static function _itemDeselected(event:SelectionEvent):void {
			if (event.item is IEdge) {
				var de:DefaultEdge = DefaultEdge(event.item) ;
				de.style = _graph.defaultEdgeStyle ;
			}
			else if (event.item is INode) {
				var dn:DefaultNode = DefaultNode(event.item) ;
				dn.style = _graph.defaultNodeStyle ;
			}
			_graphCanvas.forceRepaint();
		}
		
		public static function get selectedItems():Iterable {
			return _graphSelection.selectedObjects ;
		}
		
		public static function get numSelected():uint {
			return _graphSelection.count ;
		}
		
		public static function getIDForItem(item:IModelItem):String {
			var itemID:Object = _mapperHelper.itemIDByInstanceMapper.lookupValue(item) ;
			return (itemID == null ? null : String(itemID)) ;
		}
		
		public static function addTask(stageID:String, taskID:String, taskLabel:String):void {
			// Find out where the node should be placed
			var nodePoint:IPoint = _getLocationForNewNode(stageID) ;
			var node:DefaultNode = DefaultNode(_graph.createNodeAt(nodePoint.x, nodePoint.y)) ;
			
			// Attach the custom port candidate provider
			node.registerLookup(IPortCandidateProvider, _portCandidateProvider) ;
			
			// Add the task label
			var label:ILabel = _graph.addLabel(node, taskLabel, FlowMapStyles.taskLabelModelParameter, FlowMapStyles.taskLabelStyle) ;
			var port:IPort = _graph.addPort(node, node.layout.x, label.layout.anchorY - label.layout.height/2) ;
			
			// Update node bounds so that the task label will fit
			_graph.setBounds(node, node.layout.x, node.layout.y, label.layout.width * 1.2, node.layout.height) ;
			
			// Add this node to the new stage's collection
			var nodesAC:ArrayCollection = ArrayCollection(_mapperHelper.nodesByScenarioStageIDMapper.lookupValue(stageID)) ;
			nodesAC.addItem(node) ;
			_mapperHelper.nodesByScenarioStageIDMapper.mapValue(stageID, nodesAC) ;
			
			// Update scenario bounds in case widening of the node resulted in changes
			_updateScenarioStageBounds(stageID) ;
			
			_mapperHelper.nodeByIDMapper.mapValue(taskID, node) ;
			_mapperHelper.scenarioStageIDByItemIDMapper.mapValue(taskID, stageID) ;
			_mapperHelper.taskByIDMapper.mapValue(taskID, label) ;
			_mapperHelper.labelTypeByLabelMapper.mapValue(label, GraphMapperHelper.VALUE_LABEL_TYPE_TASK) ;
			_mapperHelper.itemIDByInstanceMapper.mapValue(label, taskID) ;
			_mapperHelper.itemIDByInstanceMapper.mapValue(node, taskID) ;
			_mapperHelper.portTypeByPortMapper.mapValue(port, GraphMapperHelper.VALUE_PORT_TYPE_TASK_INCOMING) ;
			
			_graphSelection.setNodeSelected(node, true) ;
		}
		
		public static function renameTask(taskID:String, newName:String):void {
			var label:ILabel = ILabel(_mapperHelper.taskByIDMapper.lookupValue(taskID)) ;
			_graph.setLabelText(label, newName) ;
			var ssid:String = String(_mapperHelper.scenarioStageIDByItemIDMapper.lookupValue(taskID)) ;
			_updateScenarioStageBounds(ssid) ;
		}
		
		private static function _updateScenarioStageBounds(stageID:String):void {
			var ss:ScenarioStage = ScenarioStage(_mapperHelper.scenarioStageByIDMapper.lookupValue(stageID)) ;
			var nodes:ArrayCollection = ArrayCollection(_mapperHelper.nodesByScenarioStageIDMapper.lookupValue(stageID)) ;
			var desiredWidth:Number = 0 ;
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
			}
		}
		
		private static function _getLocationForNewNode(stageID:String):IPoint {
			var ss:ScenarioStage = ScenarioStage(_mapperHelper.scenarioStageByIDMapper.lookupValue(stageID)) ;
			var nodes:ArrayCollection = ArrayCollection(_mapperHelper.nodesByScenarioStageIDMapper.lookupValue(stageID)) ;
			var maxY:Number = 0 ;
			for (var i:int=0; i < nodes.length ; i++) {
				var node:INode = INode(nodes.getItemAt(i)) ;
				if (node.layout.y > maxY)
					maxY = node.layout.y ;
			}
			var nodeX:Number = ss.x + FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_X ;
			var nodeY:Number = (maxY == 0 ? FlowMapStyles.SCENARIO_STAGE_NODE_PADDING_Y : maxY + FlowMapStyles.VERTICAL_INTERNODE_GAP) ;
			return new ImmutablePoint(nodeX, nodeY) ;
		}
		
		public static function addEvent(taskID:String, eventID:String, eventLabel:String):void {
			var node:INode = INode(_mapperHelper.nodeByIDMapper.lookupValue(taskID)) ;
			var stageID:String = String(_mapperHelper.scenarioStageIDByItemIDMapper.lookupValue(taskID)) ;
			var label:ILabel = _graph.addLabel(node, eventLabel, FlowMapStyles.eventLabelModelParameter, FlowMapStyles.eventLabelStyle) ;
			var rect:IOrientedRectangle = label.layout;
			var taskRect:IOrientedRectangle = ILabel(_mapperHelper.taskByIDMapper.lookupValue(taskID)).layout ;
			var nodeRect:IRectangle = node.layout ;
			var minRequiredWidth:Number = rect.width + taskRect.width + 20 ;
			if (node.layout.width < minRequiredWidth) {
				_graph.setBounds(node, nodeRect.x, nodeRect.y, minRequiredWidth, nodeRect.height) ;
			}
			rect = label.layout ;
			var port:IPort = _graph.addPort(node, rect.anchorX + rect.width + 2, rect.anchorY - rect.height/2) ;
			
			_updateScenarioStageBounds(stageID) ;
			_mapperHelper.scenarioStageIDByItemIDMapper.mapValue(eventID, stageID) ;
			_mapperHelper.eventByIDMapper.mapValue(eventID, label) ;
			_mapperHelper.labelTypeByLabelMapper.mapValue(label, GraphMapperHelper.VALUE_LABEL_TYPE_EVENT) ;
			_mapperHelper.itemIDByInstanceMapper.mapValue(label, eventID) ;
			_mapperHelper.portTypeByPortMapper.mapValue(port, GraphMapperHelper.VALUE_PORT_TYPE_EVENT_OUTGOING) ;
		}
		
		public static function setRole(taskID:String, roleID:String, roleLabel:String):void {
			var stageID:String = String(_mapperHelper.scenarioStageIDByItemIDMapper.lookupValue(taskID)) ;
			var node:INode = INode(_mapperHelper.nodeByIDMapper.lookupValue(taskID)) ;
			var label:Object = _mapperHelper.roleByIDMapper.lookupValue(roleID) ;

			if (label != null) {
				_graph.setLabelText(ILabel(label), roleLabel) ;
				_mapperHelper.itemIDByInstanceMapper.mapValue(label, roleID) ;
				// TODO: Update port location
			} 
			else {
				label = _graph.addLabel(node, roleLabel, FlowMapStyles.roleLabelModelParameter, FlowMapStyles.roleLabelStyle) ;
				_mapperHelper.roleByIDMapper.mapValue(roleID, label) ;	
				_mapperHelper.labelTypeByLabelMapper.mapValue(label, GraphMapperHelper.VALUE_LABEL_TYPE_ROLE) ;
				_mapperHelper.itemIDByInstanceMapper.mapValue(label, roleID) ;
			}
			var rect:IOrientedRectangle = ILabel(label).layout ;
			var nodeRect:IRectangle = node.layout ;
			if (nodeRect.width < rect.width)
				_graph.setBounds(node, nodeRect.x, nodeRect.y, rect.width*1.15, nodeRect.height) ;
			_updateScenarioStageBounds(stageID) ;
			rect = ILabel(label).layout ;
			var port:IPort = _graph.addPort(node, rect.anchorX, rect.anchorY - rect.height/2) ;
			_mapperHelper.portTypeByPortMapper.mapValue(port, GraphMapperHelper.VALUE_PORT_TYPE_ROLE_INCOMING) ;
		}
		
		public static function addSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
					_graphSelection.addEventListener(SelectionEvent.SELECT, listener) ;
			else if (type == FlowMapEvent.ITEM_DESELECTED)
					_graphSelection.addEventListener(SelectionEvent.DESELECT, listener) ;
			else if (type == FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED)
				_geim.addEventListener(FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED.name, listener) ;
		}
		
		public static function removeSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
				_graphSelection.removeEventListener(SelectionEvent.SELECT, listener) ;
			else if (type == FlowMapEvent.ITEM_DESELECTED)
				_graphSelection.removeEventListener(SelectionEvent.DESELECT, listener) ;
			else if (type == FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED)
				_geim.removeEventListener(FlowMapEvent.SCENARIO_STAGE_SELECTION_CHANGED.name, listener) ;
		}
		
		public static function removeTask(taskID:String):void {
			var tn:Object = _mapperHelper.nodeByIDMapper.lookupValue(taskID) ;
			if (tn == null)
				return ;
			_mapperHelper.removePortMappings(INode(tn)) ;
		}
			
		public static function reComputeLayout(taskID:String):void {
			var temp:Object = _mapperHelper.nodeByIDMapper.lookupValue(taskID) ;
			if (temp == null)
				return ;
			var node:INode = INode(temp) ;
		}
		
		public static function moveTask(taskID:String, fromStageID:String, toStageID:String):void {
			// TODO
		}
		
		private static var id:int = -1 ;
		public static function getNewID():String {
			id ++ ;
			return id.toString() ;
		}
    }
}