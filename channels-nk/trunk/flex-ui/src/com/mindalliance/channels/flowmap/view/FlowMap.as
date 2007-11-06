package com.mindalliance.channels.flowmap.view
{
    import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
    import com.mindalliance.channels.flowmap.view.delegates.AgentDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.CausationDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.EventDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.RepositoryDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.RoleDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.SharingNeedDelegate;
    import com.mindalliance.channels.flowmap.view.delegates.TaskDelegate;
    import com.mindalliance.channels.flowmap.view.interaction.CustomGraphEditorInputMode;
    import com.mindalliance.channels.flowmap.view.layout.GridLayout;
    import com.mindalliance.channels.flowmap.view.visualelements.FlowMapStyles;
    import com.mindalliance.channels.flowmap.view.visualelements.Phase;
    import com.mindalliance.channels.flowmap.view.visualelements.PhaseCanvasObjectDescriptor;
    import com.yworks.canvas.ICanvasObjectDescriptor;
    import com.yworks.canvas.ICanvasObjectGroup;
    import com.yworks.graph.model.DefaultGraph;
    import com.yworks.graph.model.GraphSelection;
    import com.yworks.graph.model.IPortCandidateProvider;
    import com.yworks.graph.model.SelectionEvent;
    import com.yworks.graph.model.SelectionPaintManager;
    import com.yworks.support.Iterable;
    import com.yworks.ui.GraphCanvasComponent;
    
    import flash.events.Event;
    import flash.geom.Point;

	[Bindable]
    public class FlowMap
    {
    	
    	public static const EVENT_ITEM_SELECT:String = "itemSelect" ;

    	public static const EVENT_ITEM_DESELECT:String = "itemDeselect" ;
    	
    	private var _graphCanvas:GraphCanvasComponent ;

    	private var _graph:DefaultGraph ;

    	private var _graphSelection:GraphSelection ;

    	private var _selectionPaintManager:SelectionPaintManager ;

    	private var _geim:CustomGraphEditorInputMode ;

    	private var _portCandidateProvider:IPortCandidateProvider ;
    	
    	private var _phaseCanvasObjectGroup:ICanvasObjectGroup ;

    	private var _phaseCanvasObjectDescriptor:ICanvasObjectDescriptor ;
    	
    	private var _mapper:GraphDataMapper ;
    	
    	private var _helper:GraphHelper ;

    	private var _layout:GridLayout ;
    	
    	public function get dataMapper():GraphDataMapper {
    		return _mapper ;
    	}

    	private var _defaultPhase:Phase ;

    	private var _tasks:TaskDelegate ;

    	public function get tasks():TaskDelegate {
    		return _tasks ;
    	}
    	

    	private var _events:EventDelegate ;
    	
    	public function get events():EventDelegate {
    		return _events ;
    	}
    	

    	private var _repositories:RepositoryDelegate ;
    	
    	public function get repositories():RepositoryDelegate {
    		return _repositories ;
    	}
    	
    	
    	
    	private var _agents:AgentDelegate ;
    	
    	public function get agents():AgentDelegate {
    		return _agents ;
    	}
    	
    	
    	private var _roles:RoleDelegate ;
    	
    	public function get roles():RoleDelegate {
    		return _roles ;
    	}
    	
    	
    	private var _sharingNeeds:SharingNeedDelegate ;
    	
    	public function get sharingNeeds():SharingNeedDelegate {
    		return _sharingNeeds ;
    	}
    	
    	
    	private var _causations:CausationDelegate ;
    	
    	public function get causations():CausationDelegate {
    		return _causations ;
    	}
    	
    	public function get defaultPhaseID():String {
    		return _defaultPhase.phaseID ;
    	}
    	
    	public function setEnabled(value:Boolean):void {
    		_graphCanvas.enabled = value ;
    	}
    	
    	public function getEnabled():Boolean {
    		return _graphCanvas.enabled ;
    	}
    	
    	private var _autoLayout:Boolean = true ;
    	
    	public function set autoLayout(value:Boolean):void {
    		_autoLayout = value ;	
    	}
    	
    	public function get autoLayout():Boolean {
    		return _autoLayout ;
    	}
    	
    	private function flowmapChanged(event:Event):void {
    		redraw(true) ;
    	}
    	
    	private function initDelegates():void {
    		var eventName:String = FlowMapEvent.FLOWMAP_CHANGED.name ;
    		
    		_tasks = new TaskDelegate(_mapper, _helper, _graph) ;
    		_tasks.addEventListener(eventName, flowmapChanged) ;
    		
    		_events = new EventDelegate(_mapper, _helper, _graph) ;
    		_events.addEventListener(eventName, flowmapChanged) ;
    		
    		_causations = new CausationDelegate(_mapper, _helper, _graph) ;
    		_causations.addEventListener(eventName, flowmapChanged) ;
    		
    		_repositories = new RepositoryDelegate(_mapper, _helper, _graph) ;
    		_repositories.addEventListener(eventName, flowmapChanged) ;
    		
    		_roles = new RoleDelegate(_mapper, _helper, _graph) ;
    		_roles.addEventListener(eventName, flowmapChanged) ;
    		
    		_agents = new AgentDelegate(_mapper, _helper, _graph) ;
    		_agents.addEventListener(eventName, flowmapChanged) ;
    		
    		_sharingNeeds = new SharingNeedDelegate(_mapper, _helper, _graph) ;
    		_sharingNeeds.addEventListener(eventName, flowmapChanged) ;
    	} 
    	 
    	private function configurePhaseCanvasGroup():void {
			_phaseCanvasObjectGroup = _graphCanvas.addGroup() ;
			_phaseCanvasObjectDescriptor = new PhaseCanvasObjectDescriptor() ;
			_phaseCanvasObjectGroup.descriptor = _phaseCanvasObjectDescriptor ;
    	}
    	
    	private function initLayout():void {
    		_layout = new GridLayout(_mapper, _graph, _helper, _graphCanvas) ;
    		_layout.leftMargin = 50 ;
    		_layout.topMargin = 50 ;
    		_layout.nodeHorizontalSpacing = 50 ;
    		_layout.nodeVerticalSpacing = 75 ;
    	}
    	
    	public function get graphCanvas():GraphCanvasComponent {
    		return _graphCanvas ;
    	}
    	    	
		public function set graphCanvas(graphCanvas:GraphCanvasComponent):void {
			_graphCanvas = graphCanvas ;

			configurePhaseCanvasGroup() ;
						
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
			_selectionPaintManager = new SelectionPaintManager(_graphCanvas, _graph.collectionModel, _graphSelection) ;
			
			_geim = new CustomGraphEditorInputMode(_graph, _graphSelection) ;
			_geim.configure(_graphCanvas) ;
			
			FlowMapStyles.systemManager = _graphCanvas.systemManager ;
  			_graph.defaultEdgeStyle = FlowMapStyles.edgeStyle ;
			_graph.defaultPortStyle = FlowMapStyles.portStyle ;
			
			_mapper = new GraphDataMapper(_graph.mapperRegistry) ;
    		_helper = new GraphHelper(_mapper, _graph) ;
			
			_portCandidateProvider = new LimitedPortCandidateProvider(_mapper) ;
			
			_defaultPhase = Phase.createPhase("Default Phase") ;
			_defaultPhase.phaseID = "phase0" ;
			
			FlowMapLayoutHelper.graphCanvas = _graphCanvas ;
			/* _graph.addEventListener(GraphEvent.GRAPH_CHANGED, handleGraphChanged) ; */
			
			initLayout() ;
			
			initDelegates() ;
		}
		
		public function redraw(useAutoLayout:Boolean=false):void {
			if (useAutoLayout && !_autoLayout)
				return ;
			_graphCanvas.viewPoint = new Point(0, 0) ;
			_layout.layout() ;
			_graphCanvas.forceRepaint() ;	
		}
				
		public function getIDForItem(item:Object):String {
			return _mapper.idMapper.lookupValue(item) as String ;
		}
		
		public function get selectedItems():Iterable {
			return _graphSelection.selectedObjects ;
		}
				
		public function get numSelected():uint {
			return _graphSelection.count ;
		}
		
		public function addSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
					_graphSelection.addEventListener(SelectionEvent.SELECT, listener) ;
					
			else if (type == FlowMapEvent.ITEM_DESELECTED)
					_graphSelection.addEventListener(SelectionEvent.DESELECT, listener) ;
					
			else if (type == FlowMapEvent.PHASE_SELECTION_CHANGED)
				_geim.addEventListener(FlowMapEvent.PHASE_SELECTION_CHANGED.name, listener) ;
		}
		
		public function removeSelectionListener(type:FlowMapEvent, listener:Function):void {
			if (type == FlowMapEvent.ITEM_SELECTED)
				_graphSelection.removeEventListener(SelectionEvent.SELECT, listener) ;
				
			else if (type == FlowMapEvent.ITEM_DESELECTED)
				_graphSelection.removeEventListener(SelectionEvent.DESELECT, listener) ;
				
			else if (type == FlowMapEvent.PHASE_SELECTION_CHANGED)
				_geim.removeEventListener(FlowMapEvent.PHASE_SELECTION_CHANGED.name, listener) ;
		}

    }
}






