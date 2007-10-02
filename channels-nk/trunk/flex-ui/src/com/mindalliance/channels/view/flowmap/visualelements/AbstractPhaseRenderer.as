package com.mindalliance.channels.view.flowmap.visualelements
{
	import com.yworks.canvas.ICanvasContext;
	import com.yworks.canvas.ICanvasObjectDescriptor;
	import com.yworks.canvas.drawing.IBoundsProvider;
	import com.yworks.canvas.drawing.IHitTestable;
	import com.yworks.canvas.drawing.IPaintContext;
	import com.yworks.canvas.drawing.IPaintable;
	import com.yworks.canvas.drawing.ShapePaintable;
	import com.yworks.canvas.drawing.SimpleTextPaintable;
	import com.yworks.canvas.drawing.YGraphics;
	import com.yworks.canvas.geom.IMutableRectangle;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.canvas.geom.IReshapeable;
	import com.yworks.canvas.geom.ImmutablePoint;
	import com.yworks.canvas.geom.ImmutableRectangle;
	import com.yworks.canvas.model.IModelItem;
	import com.yworks.ui.GraphCanvasComponent;
	
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	import mx.core.UITextFormat;
	import mx.graphics.IFill;
	import mx.graphics.IStroke;
	import mx.graphics.SolidColor;
	import mx.graphics.Stroke;
	import mx.managers.ISystemManager;
	import mx.utils.ObjectProxy;
	import com.yworks.canvas.geom.YPoint;

	public class AbstractPhaseRenderer implements IPaintable, IBoundsProvider, IHitTestable, IReshapeable, IModelItem
	{
		protected var _name:String ;
		public var x:Number = 0 ;
		public var y:Number = 0 ;
		public var width:Number ;
		public var height:Number ;
		public var textFormat:TextFormat ;
		public var selectedTextFormat:TextFormat ;
		public var fill:IFill ;
		public var selectedFill:IFill ;
		public var stroke:Stroke ;
		private var _selected:Boolean ;
		private static var _dottedLineGap:int = 5 ;
		private static var _headerHeight:int = 20 ;
		private static var _padding:int = 5 ;
		
		public function AbstractPhaseRenderer() {
			initializeStyle() ;
		}
		
		protected function _widenForText():void {
			var tf:TextField = new TextField() ;
			tf.text = _name ;
			tf.defaultTextFormat = selectedTextFormat ;
			tf.autoSize = TextFieldAutoSize.LEFT ;
			var minDesiredWidth:Number = tf.textWidth + 2*_padding ;
			if (width < minDesiredWidth)
				width = minDesiredWidth ;
		}
		
		public function set selected(value:Boolean):void {
			_selected = value ;
		}
		
		public function get selected():Boolean {
			return _selected ;
		}
		
		public function paint(g:YGraphics, ctx:IPaintContext):void {
			g.applyStroke(stroke) ;
			for (var curY:int = y; curY < height ; curY += (2*_dottedLineGap)) {
				g.moveTo(x+width, curY) ;
				g.lineTo(x+width, curY+_dottedLineGap) ;
			}
			
			g.drawLine(x, y+_headerHeight, x+width, y+_headerHeight) ;
			if (_selected)
				g.drawString(_name, selectedTextFormat, x, y) ;
			else
				g.drawString(_name, textFormat, x, y) ;
		}
		
		protected function initializeStyle():void {
			stroke = new Stroke(0xDDDDDD, 1.0) ;
			stroke.scaleMode = 'noScale' ;
			textFormat = new TextFormat() ;
			textFormat.bold = true ;
			textFormat.color = 0x110011 ;
			textFormat.italic = false ;
			textFormat.align = 'center' ;
			textFormat.font = 'Verdana' ;
			textFormat.size = 12 ;
			selectedTextFormat = new TextFormat() ;
			selectedTextFormat.bold = true ;
			selectedTextFormat.italic = true ;
			selectedTextFormat.align = 'center' ;
			selectedTextFormat.font = 'Verdana' ;
			selectedTextFormat.size = 12 ;
		}
		
		public function calculateBounds(scratch:IMutableRectangle, ctx:ICanvasContext):IRectangle {
			scratch.reshape(x, y, width, height) ;
			return scratch ;
		}
		
		public function isHit(x:Number, y:Number, ctx:ICanvasContext):Boolean {
			return (x >= this.x) 
			&& (x <= (this.x + this.width)) 
			&& (y >= this.y) 
			&& (y <= (this.y + _headerHeight)) ;
		}
		
		public function reshape(x:Number, y:Number, width:Number, height:Number):void {
			this.x = x ;
			this.y = y ;
			this.width = width ;
			this.height = height ;
		}
		
		public function lookup(type:Class):Object {
			return null ;
		}
	}
}