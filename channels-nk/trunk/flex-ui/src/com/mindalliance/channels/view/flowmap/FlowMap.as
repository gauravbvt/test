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
    import com.yworks.canvas.drawing.RectangularSelectionPaintable;
    import com.yworks.graph.model.SelectionPaintManager;
    import flash.geom.Rectangle;
    import com.yworks.graph.model.ISelectionPaintable;
    import mx.states.SetStyle;
    import com.yworks.graph.model.DefaultLabel;
    import com.yworks.graph.model.DefaultPort;
    import flash.events.EventDispatcher;

	[Bindable]
    public class FlowMap extends EventDispatcher
    {
    	
    	public static const EVENT_ITEM_SELECT:String = "itemSelect" ;
    	public static const EVENT_ITEM_DESELECT:String = "itemDeselect" ;
    	
    	private static var _graphCanvas:GraphCanvasComponent ;
    	private static var _graph:DefaultGraph ;
    	private static var _graphSelection:GraphSelection ;
    	private static var _selectionPaintManager:SelectionPaintManager ;
    	private static var _geim:CustomGraphEditorInputMode ;
    	private static var _portCandidateProvider:IPortCandidateProvider ;
    	
    	private static var _phaseCanvasObjectGroup:ICanvasObjectGroup ;
    	private static var _phaseCanvasObjectDescriptor:ICanvasObjectDescriptor ;
    	
    	private static var _mapperHelper:GraphMapperHelper ;
    	private static var _defaultPhase:Phase ;
    	
    	public static function get defaultPhaseID():String {
    		return _defaultPhase.phaseID ;
    	}
    	
    	public static function setEnabled(value:Boolean):void {
    		_graphCanvas.enabled = value ;
    	}
    	
    	public static function getEnabled():Boolean {
    		return _graphCanvas.enabled ;
    	}
    	
		public static function initialize(graphCanvas:GraphCanvasComponent):void {
			_graphCanvas = graphCanvas ;
			_graphCanvas.treeDirty = true ;

			_phaseCanvasObjectGroup = _graphCanvas.addGroup() ;
			_phaseCanvasObjectDescriptor = new PhaseCanvasObjectDescriptor() ;
			_phaseCanvasObjectGroup.descriptor = _phaseCanvasObjectDescriptor ;
						
			_graph = new DefaultGraph() ;
			_graphCanvas.graph = _graph ;
			_graph.autoCleanupPorts = false ;
			_graph.usePortCandidateProviders = true ;
			_graph.shareDefaultEdgeStyleInstance = true ;
			_graph.shareDefaultNodeLabelStyleInstance = true ;
			_graph.shareDefaultPortStyleInstance = true ;
			_graph.shareDefaultNodeStyleInstance = true ;
			_graph.shareDefaultEdgeLabelStyleInstance = true ;
			
			_graphSelection = new GraphSelection(_graph) ;
			_graphSelection.addEventListener(SelectionEvent.SELECT, _itemSelected) ;
			_graphSelection.addEventListener(SelectionEvent.DESELECT, _itemDeselected) ;
			_selectionPaintManager = new SelectionPaintManager(_graphCanvas, _graph.collectionModel, _graphSelection) ;
			
			_geim = new CustomGraphEditorInputMode(_graph, _graphSelection) ;
			_geim.configure(_graphCanvas) ;
			
			FlowMapStyles.systemManager = _graphCanvas.systemManager ;
  			_graph.defaultEdgeStyle = FlowMapStyles.edgeStyle ;
			_graph.defaultPortStyle = FlowMapStyles.portStyle ;
			
			_mapperHelper = GraphMapperHelper.getInstance() ;
			_mapperHelper.initialize(_graph.mapperRegistry) ;
			
			_portCandidateProvider = new LimitedPortCandidateProvider(_mapperHelper) ;
			_defaultPhase = Phase.createPhase("Default Phase") ;
			_defaultPhase.phaseID = FlowMap.getNewID() ;
		}
		
		public static function getIDForItem(item:Object):String {
			return _mapperHelper.idMapper.lookupValue(item) as String ;
		}
		
		public static function get selectedPhaseID():String {
			var selectedPhase:Phase = _geim.selectedPhase ;
			if (selectedPhase == null)
				return null ;
			return selectedPhase.phaseID ;
		}
		
		public static function getPhaseNameByID(phaseID:String):String {
			return null ;
		}
		
		public static function addPhase(phaseID:String, phaseName:String):void {
			var phase:Phase = Phase.createPhase(phaseName) ;
			phase.width = 200 ;
			phase.height = _graphCanvas.height ;
/* 			_mapperHelper.phaseMapper.mapValue(phaseID, phase) ; */
			_graphCanvas.addCanvasObject(phase, _phaseCanvasObjectDescriptor, _phaseCanvasObjectGroup) ;
			_mapperHelper.idMapper.mapValue(phase, phaseID) ;
		}
		
		protected static function _itemSelected(event:SelectionEvent):void {
			if (event.item is IEdge) {
 				var de:DefaultEdge = DefaultEdge(event.item) ;
 				de.style = FlowMapStyles.selectedEdgeStyle ;
			}
			_graphCanvas.forceRepaint();
		}
		
		protected static function _itemDeselected(event:SelectionEvent):void {
			if (event.item is IEdge) {
				var de:DefaultEdge = DefaultEdge(event.item) ;
				de.style = _graph.defaultEdgeStyle ;
			}
			_graphCanvas.forceRepaint();
		}
		
		public static function get selectedItems():Iterable {
			return _graphSelection.selectedObjects ;
		}
				
		public static function get numSelected():uint {
			return _graphSelection.count ;
		}
		
		public static function renamePhase(phaseID:String, newText:String):void {
			var phase:Phase = null ;
			if (phase == null)
				return ;
			phase.name = newText ;
			FlowMapLayoutHelper.updatePhaseBounds(_mapperHelper, phaseID) ;
			_graphCanvas.forceRepaint() ;
		}
		
		public static function renameEvent(eventID:String, newText:String):void {
			var end:EventNodeData = EventNodeData(_mapperHelper.nodeDataMapper.lookupValue(eventID)) ;
			var labelIter:Iterator = end.node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(labelIter.hasNext()) ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type != LabelData.LABEL_TYPE_EVENT)
					continue ;
				_graph.setLabelText(ld.label, newText) ;
				// Adjust node size to fit label
				FlowMapLayoutHelper.updateNodeBounds(_graph, label.owner as DefaultNode) ;
				break ;
			}
		}
		
		public static function renameTask(taskID:String, newText:String):void {
			var tnd:TaskNodeData = _mapperHelper.nodeDataMapper.lookupValue(taskID) as TaskNodeData ;
			if (!tnd)
				return ;
			var labelIter:Iterator = tnd.node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = labelIter.next() as DefaultLabel ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (!ld)
					return ;
				if (ld.type != LabelData.LABEL_TYPE_TASK)
					continue ;
				_graph.setLabelText(label, newText) ;
				_mapperHelper.labelDataMapper.mapValue(label, ld) ;
				// Adjust node size to fit label
				FlowMapLayoutHelper.updateNodeBounds(_graph, label.owner as DefaultNode) ;
				_graphCanvas.forceRepaint() ;
				break ;
			}
		}
		
		public static function renameRole(roleID:String, newText:String):void {
			var labelIter:Iterator = _graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(labelIter.hasNext()) ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == LabelData.LABEL_TYPE_ROLE && ld.id == roleID) {
					_graph.setLabelText(ld.label, newText) ;
				// Adjust node size to fit label
					FlowMapLayoutHelper.updateNodeBounds(_graph, DefaultNode(label.owner)) ;
				}
			}
		}
		
		public static function renameAgent(taskID:String, roleID:String, newText:String):void {
			renameRole(roleID, newText) ;
		}

		public static function renameRepository(reposID:String, newText:String):void {
			var nd:RepositoryNodeData = RepositoryNodeData(_mapperHelper.nodeDataMapper.lookupValue(reposID)) ;
			var labelIter:Iterator = nd.node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(labelIter.hasNext()) ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type != LabelData.LABEL_TYPE_REPOSITORY)
					continue ;
				_graph.setLabelText(ld.label, newText) ;
/* 				// Adjust node size to fit label
				FlowMapLayoutHelper.updateNodeBounds(_graph, label.owner as DefaultNode) ;
 */				break ;
			}
		}
		
		public static function renameRepositoryOwner(reposOwnerID:String, newText:String):void {
			var labelIter:Iterator = _graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(labelIter.hasNext()) ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == LabelData.LABEL_TYPE_REPOSITORY_OWNER && ld.id == reposOwnerID) {
					_graph.setLabelText(ld.label, newText) ;
				}
			}
		}
		
		public static function addTask(phaseID:String, taskID:String, taskLabel:String):void {
			// Rename task if already present
			var tnd:TaskNodeData = _mapperHelper.nodeDataMapper.lookupValue(taskID) as TaskNodeData ;
			if (tnd) {
				FlowMap.renameTask(taskID, taskLabel) ;
				return ;
			} 
			// Find out where the node should be placed
			var nodePoint:IPoint = FlowMapLayoutHelper.getLocationForNewNode2(_graphCanvas) ;
			var node:DefaultNode = DefaultNode(_graph.createNodeAt(nodePoint.x, nodePoint.y)) ;

			// Set styles
			_graph.setNodeStyle(node, FlowMapStyles.taskNodeStyle) ;
			DefaultNodeSelectionPaintable.createAndRegisterFor(node) ;
			
			// Attach the custom port candidate provider
			node.registerLookup(IPortCandidateProvider, _portCandidateProvider) ;
			
			// Setup node data mappings
			tnd = new TaskNodeData(node, taskID, phaseID) ;
			_mapperHelper.nodeDataMapper.mapValue(taskID, tnd) ;
			_mapperHelper.idMapper.mapValue(node, taskID) ;
			
			// Add the task label
			var label:ILabel = _graph.addLabel(node, taskLabel, FlowMapStyles.taskLabelModelParameter, FlowMapStyles.taskLabelStyle) ;

			// Setup task mappings
			var ld:LabelData = new LabelData(label, taskID, LabelData.LABEL_TYPE_TASK) as LabelData ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
			_mapperHelper.idMapper.mapValue(label, taskID) ;

			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(_graph, node) ;
			
			// Add a port now
			var rect:IRectangle = node.layout ;
			var port:IPort = _graph.addPort(tnd.node, rect.x + rect.width + 5, rect.y + rect.height/2);
			_mapperHelper.portTypeMapper.mapValue(port, PortType.PORT_TYPE_TASK_OUTGOING) ;
			_mapperHelper.idMapper.mapValue(port, taskID) ;

			_graphSelection.setNodeSelected(node, true) ;
/* 			_updatePhaseBounds(phaseID) ; */
		}
		
		public static function addRepository(phaseID:String, reposID:String, reposLabel:String):void {
			// Rename if already present
			var rnd:RepositoryNodeData = _mapperHelper.nodeDataMapper.lookupValue(reposID) as RepositoryNodeData ;
			if (rnd) {
				renameRepository(reposID, reposLabel) ;
			}
			
			// Find a place to add the event node
			var nodePoint:IPoint = FlowMapLayoutHelper.getLocationForNewNode2(_graphCanvas) ;
			var node:DefaultNode = DefaultNode(_graph.createNodeAt(nodePoint.x, nodePoint.y)) ;
			
			// Setup styles
			_graph.setNodeStyle(node, FlowMapStyles.repositoryNodeStyle) ;
			DefaultNodeSelectionPaintable.createAndRegisterFor(node) ;
			
			//Setup mappings
			rnd = new RepositoryNodeData(node, reposID) ;
			_mapperHelper.nodeDataMapper.mapValue(reposID, rnd) ;
			_mapperHelper.idMapper.mapValue(node, reposID) ;
			
			// Add repository name label
			var label:DefaultLabel = _graph.addLabel(node, reposLabel, FlowMapStyles.repositoryLabelModelParameter, FlowMapStyles.repositoryLabelStyle) as DefaultLabel ;
			var ld:LabelData = new LabelData(label, reposID, LabelData.LABEL_TYPE_REPOSITORY) as LabelData ;
			_mapperHelper.labelDataMapper.mapValue(reposID, ld) ;
			_mapperHelper.idMapper.mapValue(label, reposID) ;
			
/* 			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(_graph, node) ; */
			
			var rect:IOrientedRectangle = label.layout ;
			var port:IPort = _graph.addPort(node, rect.anchorX - 5, rect.anchorY - rect.height/2) ;
			_mapperHelper.portTypeMapper.mapValue(port, PortType.PORT_TYPE_REPOSITORY_INCOMING) ;
			_mapperHelper.idMapper.mapValue(port, reposID) ;
		}
		
		public static function addEvent(phaseID:String, eventID:String, eventLabel:String):void {
			// First check if event is already there
			var end:EventNodeData = _mapperHelper.nodeDataMapper.lookupValue(eventID) as EventNodeData ;
			if (end) {
				FlowMap.renameEvent(eventID, eventLabel) ;
				return ;
			}
			
			// Find a place to add the event node
			var nodePoint:IPoint = FlowMapLayoutHelper.getLocationForNewNode2(_graphCanvas) ;
			var node:DefaultNode = DefaultNode(_graph.createNodeAt(nodePoint.x, nodePoint.y)) ;
			_mapperHelper.idMapper.mapValue(node, eventID) ;
			
			// Setup styles
			_graph.setNodeStyle(node, FlowMapStyles.eventNodeStyle) ;
			DefaultNodeSelectionPaintable.createAndRegisterFor(node) ;
			
			//Setup mappings
			end = new EventNodeData(node, eventID) ;
			end.startPhaseID = phaseID ;
			end.endPhaseID = phaseID ;
			_mapperHelper.nodeDataMapper.mapValue(eventID, end) ;
			
			// Add the event label
			var label:ILabel = _graph.addLabel(node, eventLabel, FlowMapStyles.eventLabelModelParameter, FlowMapStyles.eventLabelStyle) ;
			var ld:LabelData = new LabelData(label, eventID, LabelData.LABEL_TYPE_EVENT) as LabelData ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
			_mapperHelper.idMapper.mapValue(label, eventID) ;

			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(_graph, node) ;
			
			// Add ports
			var rect:IRectangle = node.layout ;
			var port:IPort = _graph.addPort(node, rect.x + rect.width + 5, rect.y + rect.height/2) ;
			_mapperHelper.portTypeMapper.mapValue(port, PortType.PORT_TYPE_EVENT_OUTGOING) ;
			_mapperHelper.idMapper.mapValue(port, eventID) ;
			
/* 			_updateScenarioStageBounds(stageID) ; */
		}
		
		private static function _getLabel(node:DefaultNode, labelType:String):DefaultLabel {
			var iter:Iterator = node.labels.iterator() ;
			while (iter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(iter.next()) ;
				var ld:LabelData = LabelData(_mapperHelper.labelDataMapper.lookupValue(label)) ;
				if (ld.type == labelType)
					return label ;
			}
			return null ;
		}
		
		private static function addAgent(node:DefaultNode, roleID:String, roleLabel:String):void {
			var label:DefaultLabel = _graph.addLabel(node, roleLabel, FlowMapStyles.roleLabelModelParameter, FlowMapStyles.roleLabelStyle) as DefaultLabel ;
			var ld:LabelData = new LabelData(label, roleID, LabelData.LABEL_TYPE_ROLE) ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
			_mapperHelper.idMapper.mapValue(label, roleID) ;
			
			// Adjust node size to fit label
			FlowMapLayoutHelper.updateNodeBounds(_graph, node) ;
			var rect:IOrientedRectangle = label.layout ;
			var port:IPort = _graph.addPort(node, rect.anchorX, rect.anchorY - rect.height/2) ;
			_mapperHelper.portTypeMapper.mapValue(port, PortType.PORT_TYPE_ROLE_INCOMING) ;
			_mapperHelper.idMapper.mapValue(port, roleID) ;
		}
		
		private static function replaceAgent(node:DefaultNode, newRoleID:String, newRoleLabel:String):void {
			var label:DefaultLabel = _getLabel(node, LabelData.LABEL_TYPE_ROLE) as DefaultLabel ;
			var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
			_graph.setLabelText(label, newRoleLabel) ;
			ld.id = newRoleID ;
			ld.label = label ;
			_mapperHelper.idMapper.mapValue(label, newRoleID) ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
		}
		
		public static function setAgent(taskID:String, roleID:String, roleLabel:String):void {
			// Get the node in which this role exists
			var tnd:TaskNodeData = TaskNodeData(_mapperHelper.nodeDataMapper.lookupValue(taskID)) ;
			var label:DefaultLabel = _getLabel(tnd.node, LabelData.LABEL_TYPE_ROLE) ;
			if (label == null)
				addAgent(tnd.node, roleID, roleLabel) ;
			else {
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id != roleID)
					replaceAgent(tnd.node, roleID, roleLabel) ;
				else
					renameAgent(taskID, roleID, roleLabel) ;
			}
		}
		
		public static function setRepositoryOwner(reposID:String, reposOwnerID:String, reposOwnerLabel:String):void {
			// Get the node in which this reposOwner is to be added
			var nd:RepositoryNodeData = _mapperHelper.nodeDataMapper.lookupValue(reposID) as RepositoryNodeData ;
			var label:DefaultLabel = _getLabel(nd.node, LabelData.LABEL_TYPE_REPOSITORY_OWNER) ;
			if (label == null)
				addRepositoryOwner(nd.node, reposOwnerID, reposOwnerLabel) ;
			else {
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id != reposOwnerID)
					replaceRepositoryOwner(nd.node, reposOwnerID, reposOwnerLabel) ;
				else
					renameRepositoryOwner(reposOwnerID, reposOwnerLabel) ;
			}
		}
		
		private static function addRepositoryOwner(node:DefaultNode, reposOwnerID:String, reposOwnerLabel:String):void {
			var label:DefaultLabel = _graph.addLabel(node, reposOwnerLabel, FlowMapStyles.repositoryOwnerLabelModelParameter, FlowMapStyles.repositoryOwnerLabelStyle) as DefaultLabel ;
			var ld:LabelData = new LabelData(label, reposOwnerID, LabelData.LABEL_TYPE_REPOSITORY_OWNER) ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
			_mapperHelper.idMapper.mapValue(label, reposOwnerID) ;
		}
		
		private static function replaceRepositoryOwner(node:DefaultNode, newReposOwnerID:String, newReposOwnerLabel:String):void {
			var label:DefaultLabel = _getLabel(node, LabelData.LABEL_TYPE_REPOSITORY_OWNER) as DefaultLabel ;
			var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
			_graph.setLabelText(label, newReposOwnerLabel) ;
			ld.id = newReposOwnerID ;
			ld.label = label ;
			_mapperHelper.idMapper.mapValue(label, newReposOwnerID) ;
			_mapperHelper.labelDataMapper.mapValue(label, ld) ;
		}
		
		public static function addSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
					_graphSelection.addEventListener(SelectionEvent.SELECT, listener) ;
			else if (type == FlowMapEvent.ITEM_DESELECTED)
					_graphSelection.addEventListener(SelectionEvent.DESELECT, listener) ;
			else if (type == FlowMapEvent.PHASE_SELECTION_CHANGED)
				_geim.addEventListener(FlowMapEvent.PHASE_SELECTION_CHANGED.name, listener) ;
		}
		
		public static function removeSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
				_graphSelection.removeEventListener(SelectionEvent.SELECT, listener) ;
			else if (type == FlowMapEvent.ITEM_DESELECTED)
				_graphSelection.removeEventListener(SelectionEvent.DESELECT, listener) ;
			else if (type == FlowMapEvent.PHASE_SELECTION_CHANGED)
				_geim.removeEventListener(FlowMapEvent.PHASE_SELECTION_CHANGED.name, listener) ;
		}
		
		public static function removeTask(taskID:String):void {
			_removeNode(taskID) ;
		}
		
		public static function removeEvent(eventID:String):void {
			_removeNode(eventID) ;
		}
		
		public static function removeRepository(reposID:String):void {
			_removeNode(reposID) ;
		}
		
		private static function _removeNode(nodeID:String):void {
			var nd:NodeData = NodeData(_mapperHelper.nodeDataMapper.lookupValue(nodeID)) ;
			_unmapAllLabels(nd.node) ;
			_unmapAllPorts(nd.node) ;
			_mapperHelper.nodeDataMapper.unMapValue(nodeID) ;
			_mapperHelper.idMapper.unMapValue(nd.node) ;
			_graph.removeNode(nd.node) ;
		}
		
		private static function _unmapAllLabels(node:DefaultNode):void {
			var labelIter:Iterator = node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				_mapperHelper.labelDataMapper.unMapValue(label) ;
				_mapperHelper.idMapper.unMapValue(label) ;
			}
		}
		
		private static function _unmapAllPorts(node:DefaultNode):void {
			var portsIter:Iterator = node.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:IPort = portsIter.next() as IPort ;
				_mapperHelper.portTypeMapper.unMapValue(port) ;
				_mapperHelper.idMapper.unMapValue(port) ;
			}
		}
		
		public static function removeRole(roleID:String):void {
			removeLabelsByID(roleID) ;
			removePorts(roleID, PortType.PORT_TYPE_ROLE_INCOMING) ;
			// Deal with edges.
		}
		
		public static function removeAgent(taskID:String, roleID:String):void {
			var tnd:TaskNodeData = _mapperHelper.nodeDataMapper.lookupValue(taskID) as TaskNodeData ;
			if (!tnd)
				return ;
			var label:DefaultLabel = _getLabel(tnd.node, LabelData.LABEL_TYPE_ROLE) ;
			if (!label)
				return ;
			_mapperHelper.idMapper.unMapValue(label) ;
			_mapperHelper.labelDataMapper.unMapValue(label) ;
			_graph.removeLabel(label) ;
			var port:DefaultPort = _getPort(tnd.node, PortType.PORT_TYPE_ROLE_INCOMING) ;
			if (!port)
				return ;
			_mapperHelper.portTypeMapper.unMapValue(port) ;
			_mapperHelper.idMapper.unMapValue(port) ;
			_graph.removePort(port) ;
		}
		
		private static function _getPort(node:DefaultNode, portType:String):DefaultPort {
			var portsIter:Iterator = node.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:DefaultPort = portsIter.next() as DefaultPort ;
				if (!port)
					continue ;
				var pType:String = _mapperHelper.portTypeMapper.lookupValue(port) as String ;
				if (!pType)
					continue ;
				if (pType == portType)
					return port ;
			}
			return null ;
			
		}
		
		private static function removeLabelsByID(labelID:String):void {
			var labelIter:Iterator = _graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:DefaultLabel = DefaultLabel(labelIter.next()) ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id == labelID) {
					_graph.removeLabel(label) ;
					_mapperHelper.labelDataMapper.unMapValue(label) ;
					_mapperHelper.idMapper.unMapValue(label) ;
				}
			}
		}
		
		private static function removePorts(portID:String, portType:String):void {
			var portsIter:Iterator = _graph.ports.iterator() ;
			while (portsIter.hasNext()) {
				var port:DefaultPort = DefaultPort(portsIter.next()) ;
				var pType:String = _mapperHelper.portTypeMapper.lookupValue(port) as String ;
				var pID:String  = _mapperHelper.idMapper.lookupValue(port) as String ;
				if (pID == portID && pType == portType) {
					_graph.removePort(port) ;
					_mapperHelper.portTypeMapper.unMapValue(port) ;
					_mapperHelper.idMapper.unMapValue(port) ;
				}
			}			
		}
		
		
		public static function removeRepositoryOwner(reposOwnerID:String):void {
			removeLabelsByID(reposOwnerID) ;
		}
		
		private static function addCausePort(node:DefaultNode, type:String):DefaultPort {
			var rect:IRectangle = node.layout ;
			var port:DefaultPort ;
			switch (type) {
				case PortType.PORT_TYPE_CAUSE_INCOMING:
					port = _graph.addPort(node, rect.x, rect.y + rect.height) as DefaultPort ;
					_mapperHelper.portTypeMapper.mapValue(port, type) ;
				break ;
				case PortType.PORT_TYPE_EVENT_OUTGOING:
					port = _graph.addPort(node, rect.x + rect.width, rect.y + rect.height) as DefaultPort ;
					_mapperHelper.portTypeMapper.mapValue(port, type) ;
				break ;	
			}
			return port ;
		}
		
		public static function addCausation(sourceID:String, targetID:String):void {
			// Both nodes must exist before causation can be added 
			var snd:NodeData = _mapperHelper.nodeDataMapper.lookupValue(sourceID) as NodeData ;
			if (!snd)
				return ;
			var tnd:NodeData = _mapperHelper.nodeDataMapper.lookupValue(targetID) as NodeData ;
			if (!tnd)
				return ;
			
			// Both nodes must be either source or target type
			if (!(snd is TaskNodeData || snd is EventNodeData))
				return ;
			
			if (!(tnd is TaskNodeData || tnd is EventNodeData))
				return ;
			
			// Check if source and target ports already exists. If not, add them.
			var sourcePort:DefaultPort = _getPort(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) as DefaultPort ;
			if (!sourcePort) {
				sourcePort = addCausePort(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
			}
			var targetPort:DefaultPort = _getPort(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) as DefaultPort ;
			if (!targetPort) {
				targetPort = addCausePort(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) as DefaultPort ;
			}
			
			// Check if there is already a causal edge between them
			var edgeIter:Iterator = _graph.edgesAtPort(sourcePort).iterator() ;
			while (edgeIter) {
				var e:IEdge = edgeIter.next() as IEdge ;
				if (e.targetPort == targetPort)
					return ;
			}
			
			// Add causal edge
			var edge:DefaultEdge = _graph.createEdge(sourcePort, targetPort) as DefaultEdge ;
			_mapperHelper.edgeTypeMapper.mapValue(edge, EdgeType.EDGE_TYPE_CAUSE) ;
			_graph.setEdgeStyle(edge, FlowMapStyles.causeEdgeStyle) ;
		}
		
		public static function removeCausation(sourceID:String, targetID:String):void {
			// Both nodes must exist
			var snd:NodeData = _mapperHelper.nodeDataMapper.lookupValue(sourceID) as NodeData ;
			if (!snd)
				return ;
			var tnd:NodeData = _mapperHelper.nodeDataMapper.lookupValue(targetID) as NodeData ;
			if (!tnd)
				return ;
				
			// get the source and target ports
			var sourcePort:DefaultPort = _getPort(snd.node, PortType.PORT_TYPE_CAUSE_OUTGOING) ;
			if (!sourcePort)
				return ;
			var targetPort:DefaultPort = _getPort(tnd.node, PortType.PORT_TYPE_CAUSE_INCOMING) ;
			if (!targetPort)
				return ;
				
			// Find the edge with the required source and target port
			var edgeIter:Iterator = _graph.edgesAtPort(sourcePort).iterator() ;
			while (edgeIter.hasNext()) {
				var e:IEdge = edgeIter.next() as IEdge ;
				if (e.targetPort == targetPort) {
					_mapperHelper.edgeTypeMapper.unMapValue(e) ;
					_graph.removeEdge(e) ;
					return ;
				}
			}
		}
		
		public static function addSharingNeed(sourceID:String, targetID:String):void {
			
		}
		
		public static function removeSharingNeed(sourceID:String, targetID:String):void {
			
		}
			
		private static var id:int = -1 ;
		public static function getNewID():String {
			id ++ ;
			return id.toString() ;
		}
    }
}






