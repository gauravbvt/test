////////////////////////////////////////////////////////////////////////////////////////////////
//
//  ResizableTitleWindow(6/11/06 Nisheet Jain)
//     
//  Ported from  -
//  Manish Jethani's ResizableTitleWindow
//  
//  This basic design for this class has been taken from Manish Jethani's ResizableTitleWindow
//  http://manish.revise.org/archives/2005/01/09/resizable-titlewindow-in-flex/
//
//  Deleted lots of unwanted/non-compiling code - Shashi
//  
////////////////////////////////////////////////////////////////////////////////////////////////
   
package com.mindalliance.channels.view.common
{
	import adobe.utils.CustomActions;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.FocusEvent;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import mx.containers.TitleWindow;
	import mx.controls.Button;
	import mx.controls.ToggleButtonBar;
	import mx.core.Application;
	import mx.core.Container;
	import mx.core.EdgeMetrics;
	import mx.core.IUIComponent;
	import mx.core.UIComponent;
	import mx.core.UITextField;
	import mx.core.mx_internal;
	import mx.effects.Move;
	import mx.effects.Parallel;
	import mx.effects.Pause;
	import mx.effects.Resize;
	import mx.effects.Sequence;
	import mx.effects.easing.Bounce;
	import mx.effects.easing.Circular;
	import mx.effects.easing.Linear;
	import mx.events.CloseEvent;
	import mx.managers.CursorManager;
	import mx.managers.CursorManagerPriority;
	import mx.managers.ISystemManager;
	import mx.managers.SystemManager;
	import mx.states.SetProperty;
	import mx.states.State;
	import mx.states.Transition;
	import mx.styles.CSSStyleDeclaration;
	import mx.styles.StyleManager;
	
	//--------------------------------------
	//  Styles
	//--------------------------------------
	
	/**
	*  Thickness in pixels of the area where the user can click to resize 
	*  the window.
	*  This area is centered at the edge of the window
	*  A resize cursor appears when the mouse is over this area.
	*
	*  @default 6
	*/
	
	[Style(name="resizeAffordance", type="Number", format="Length", inherit="no")]
	                                                                                                       
	public class ResizableTitleWindow extends TitleWindow
	{
	    //--------------------------------------------------------------------------
	    //
	    //  Class Constants
	    //
	    //--------------------------------------------------------------------------
	
	    // Constants for window edges (see `handleEdge`)                                                                                                                    
	    static private var EDGE_NONE : Number = 0;
	
	    static private var EDGE_BOTTOM : Number = 1;
	    static private var EDGE_RIGHT : Number = 2;
	
	    static private var EDGE_CORNER : Number = 5;
	
	    //--------------------------------------------------------------------------
	    //
	    //  Constructor
	    //
	    //--------------------------------------------------------------------------
	
	    /**
	     *  Constructor.
	     */
	    public function ResizableTitleWindow()
	    {
	        super();
	
	        setStyle("resizeAffordance",6);
	
	        addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler,true);
	        addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler,false);
	        addEventListener(MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler);
	        addEventListener(MouseEvent.ROLL_OUT, systemManager_mouseMoveHandler);
	    }
	
	    //--------------------------------------------------------------------------
	    //
	    //  Variables
	    //
	    //--------------------------------------------------------------------------
	
	    //--------------------------------------------------------------------------
	    //  Resize Cursor Classes
	    //--------------------------------------------------------------------------
	
	    /**
	     *  @private
	     *  CursorSymbol used to display Vertical Resize Cursor
	     */
	    [Embed(source="../../../../../assets/images/verticalresizecursor.png")]
	    private var vCursorSymbol:Class ;
	    /**
	     *  @private
	     *  CursorSymbol used to display Horizontal Resize Cursor
	     */
	    [Embed(source="../../../../../assets/images/horizontalresizecursor.png")]
	    private var hCursorSymbol:Class ;
	    /**
	     *  @private
	     *  CursorSymbol used to display Diagonal Resize Cursor
	     */
	    [Embed(source="../../../../../assets/images/diagonalresizecursor.png")]
	    private var dCursorSymbol:Class ;
	
	    /**
	     *  @private
	     */
	    private var isHandleDragging : Boolean = false;
	
	    /**
	     *  @private
	     */
	    private var handleEdge : Number = EDGE_NONE;
	
	    /**
	     *  @private
	     */
	    private var cursorId:Number=0;
	
	    /**
	     *  @private
	     */
	    private var lastEdge:Number=0;
	
	    /**
	     *  @private
	     */
	    private var origPosition:Point;
	
	    /**
	     *  @private
	     */
	    private var origDimensions:Point;
	
	    /**
	     *  @private
	     */
	    private var origPerDimensions:Point;
	
	    /**
	     *  @private
	     */
	    private var origDimensionsCached:Boolean = false;
	    
	    /**
	     *  @private
	     */
	    private function mouseDownHandler(event:MouseEvent):void
	    {
	        systemManager.addEventListener(
	            MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);
	
	        systemManager.addEventListener(
	            MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
	
	        systemManager_mouseDownHandler(event);
	    }
	
	    /**
	     *  @private
	     */
	    private function systemManager_mouseDownHandler(event:MouseEvent):void
	    {
	        if(handleEdge != EDGE_NONE)
	        {
	            isHandleDragging = true;
	            event.stopPropagation();
	        }
	    }
	
	    /**
	     *  @private
	     */
	    private function systemManager_mouseMoveHandler(event:MouseEvent):void
	    {
	        if(this.parent!=null)
	        {
	            if(isHandleDragging)
	                resize(event);
	            else
	                setResizeCursor(event);
	        }
	    }
	
	    /**
	     *  @private
	     */
	    private function systemManager_mouseUpHandler(event:MouseEvent):void
	    {
	        systemManager.removeEventListener(MouseEvent.MOUSE_MOVE, systemManager_mouseMoveHandler, true);
	        systemManager.removeEventListener(MouseEvent.MOUSE_UP, systemManager_mouseUpHandler, true);
	        systemManager.removeEventListener(MouseEvent.MOUSE_DOWN, systemManager_mouseDownHandler, true);
	        isHandleDragging = false;
	    }
	
	    /**
	     *  @private
	     */
	    private function isCursorOnEdge(event:MouseEvent):void
	    {
	        var point:Point, eventPoint:Point;
	        var x1:Number, x2:Number, y1:Number, y2:Number;
	
	        var tolerance:Number = getStyle("resizeAffordance") as Number;
	
	        point = new Point(x,y);
	        point = this.parent.localToGlobal(point);
	        eventPoint = new Point(event.stageX, event.stageY);
	
	        //Distance from right edge
	        x1 = eventPoint.x - this.width - point.x;
	        //Distance from left edge
	        x2 = eventPoint.x-point.x;
	        //Distance from bottom edge       
	        y1 = eventPoint.y - this.height-point.y;
	        //Distance from top edge
	        y2 = eventPoint.y - point.y;
	
	        if (event.type == "rollOut")
	        {
	            handleEdge = EDGE_NONE;
	        }
	        else
	        {
	            handleEdge = EDGE_NONE;
	            if(handleEdge == EDGE_NONE)
	            {
	                if(Math.abs(x1) < tolerance && Math.abs(y1) < tolerance)
	                    handleEdge = EDGE_CORNER;
	                else if(Math.abs(y1) < tolerance)
	                    handleEdge = EDGE_BOTTOM;
	                else if(Math.abs(x1) < tolerance)
	                    handleEdge = EDGE_RIGHT;
	            }
	        }
	    }
	
	    /**
	     *  @private
	     */
	    private function resize(event:MouseEvent):void
	    {
	        var newWidth:Number, newHeight:Number;
	        var point:Point = new Point(x,y);
	        point = this.parent.localToGlobal(point);
	
	        var eventPoint:Point = new Point(event.stageX, event.stageY);
	
			if(handleEdge == EDGE_CORNER)
	            setSize(point.x,point.y,eventPoint.x-point.x, eventPoint.y-point.y);
	        else if (handleEdge == EDGE_BOTTOM)
	            setSize(point.x,point.y,width, eventPoint.y-point.y);
	        else if(handleEdge == EDGE_RIGHT)
	            setSize(point.x,point.y,eventPoint.x-point.x, height);
	    }
	
	    /**
	     *  @private
	     */
	    private function setResizeCursor(event:MouseEvent):void
	    {
	        isCursorOnEdge(event);
	        if (handleEdge != EDGE_NONE)
	        {
	            if(cursorId==0 || lastEdge != handleEdge)
	            {
	                if(lastEdge != handleEdge)
	                {
	                    CursorManager.removeCursor(cursorId);
	                    cursorId=0;
	                }
	                if(handleEdge == EDGE_CORNER)
	                    cursorId=CursorManager.setCursor(dCursorSymbol, CursorManagerPriority.HIGH);
	                else if(handleEdge == EDGE_RIGHT)
	                    cursorId=CursorManager.setCursor(hCursorSymbol, CursorManagerPriority.HIGH);
	                else if(handleEdge == EDGE_BOTTOM)
	                    cursorId=CursorManager.setCursor(vCursorSymbol, CursorManagerPriority.HIGH);
	
	                lastEdge = handleEdge;
	            }
	        }
	        else
	        {
	            if(cursorId!=0)
	            {
	                CursorManager.removeCursor(cursorId);
	                cursorId=0;
	                lastEdge = EDGE_NONE;
	            }
	        }
	    }
	
	
	
	    /**
	     *  @private
	     */
	    private function setSize(newX:Number,newY:Number,newWidth:Number, newHeight:Number):void
	    {
	        if(newWidth > 100 && newHeight >35)
	        {
	            var point:Point = new Point(newX, newY);
	            point = this.parent.globalToLocal(point);
	            x = point.x;
	            y = point.y;
	
	            width = newWidth;
	            height = newHeight;
	
	        }
	    }
	
	    private function storeOrigDimensions():void
	
	    {
	
	        origPosition = new Point();
	        origDimensions = new Point();
	        origPerDimensions = new Point();
	
	        //In case this is a Pop up, special care is needed
	
	        if (parent is ISystemManager)
	        {
	            origPosition.x = parent.x;
	            origPosition.y = parent.y;
	            origPosition = this.localToGlobal(origPosition);
	        }
	        else
	        {
	            origPosition.x = x;
	            origPosition.y = y;
	        }
	        if(percentWidth)
	            origPerDimensions.x = percentWidth;
	        else
	            origDimensions.x = getExplicitOrMeasuredWidth() ;
	        if(percentHeight)
	            origPerDimensions.y = percentHeight;
	        else
	            origDimensions.y = getExplicitOrMeasuredHeight();
	    }
	
	}
}
