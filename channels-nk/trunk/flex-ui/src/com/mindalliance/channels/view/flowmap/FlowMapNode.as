package com.mindalliance.channels.view.flowmap
{
    import com.yworks.canvas.geom.IRectangle;
    import com.yworks.graph.model.DefaultNode;
    import com.yworks.graph.model.ILabelCollection;
    import com.yworks.graph.model.IPortCollection;
    import com.yworks.graph.model.IGraph;
    import mx.collections.ArrayCollection;
    import com.yworks.graph.drawing.INodeStyle;
    import com.yworks.graph.drawing.ILabelStyle;
    import com.mindalliance.channels.view.flowmap.FlowMapStyles;
    import com.yworks.graph.model.INode;

    public class FlowMapNode
    {
    	private var _graph:IGraph ;
    	private var _node:DefaultNode ;
    	
    	private var _tasks:ArrayCollection;
    	private var _roles:ArrayCollection ;
    	private var _infos:ArrayCollection ;
    	private var _events:ArrayCollection ;
    	
    	public function FlowMapNode(graph:IGraph, x:Number=0, y:Number=0) {
    		super() ;
    		this._graph = graph ;
    		this._node = DefaultNode(this._graph.createNodeAt(x, y)) ;
    		this._node.style = FlowMapStyles.nodeStyle ;
    	}
    	
    	public function get node():INode {
    		return _node ;
    	}
    	
    	public function addTask(taskName:String):void {
    		this._tasks.addItem(taskName) ;
    		this._graph.addLabel(this._node, taskName, FlowMapStyles.taskLabelModelParameter, FlowMapStyles.taskLabelStyle) ;
    	}
        
        public function addRole(roleName:String):void {
        	this._roles.addItem(roleName) ;
        	this._graph.addLabel(this._node, roleName, FlowMapStyles.roleLabelModelParameter, FlowMapStyles.roleLabelStyle) ;
        }
        
        public function addInfo(infoName:String):void {
        	this._infos.addItem(infoName) ;
        	this._graph.addLabel(this._node, infoName, FlowMapStyles.infoLabelModelParameter, FlowMapStyles.infoLabelStyle) ;
        }
        
        public function addEvent(eventName:String):void {
        	this._events.addItem(eventName) ;
        	this._graph.addLabel(this._node, eventName, FlowMapStyles.eventLabelModelParameter, FlowMapStyles.eventLabelStyle) ;
        }
        
    }
}