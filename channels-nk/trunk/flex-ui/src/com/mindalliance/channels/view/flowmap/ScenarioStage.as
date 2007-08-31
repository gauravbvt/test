package com.mindalliance.channels.view.flowmap
{
	import com.yworks.canvas.ICanvasObjectDescriptor;
	import com.yworks.canvas.drawing.IBoundsProvider;
	import com.yworks.canvas.drawing.IHitTestable;
	import com.yworks.canvas.drawing.IPaintContext;
	import com.yworks.canvas.drawing.IPaintable;
	import com.yworks.canvas.drawing.ShapePaintable;
	import com.yworks.canvas.drawing.SimpleTextPaintable;
	import com.yworks.canvas.drawing.YGraphics;
	import com.yworks.canvas.geom.IRectangle;
	import com.yworks.canvas.geom.ImmutablePoint;
	import com.yworks.canvas.geom.ImmutableRectangle;
	
	import flash.geom.Matrix;
	import flash.text.TextFormat;
	
	import mx.core.UITextFormat;
	import mx.graphics.IFill;
	import mx.graphics.IStroke;
	import mx.graphics.SolidColor;
	import mx.graphics.Stroke;
	import mx.managers.ISystemManager;
	import mx.utils.ObjectProxy;
	import com.yworks.canvas.geom.IMutableRectangle;
	import com.yworks.canvas.ICanvasContext;
	import com.yworks.canvas.geom.IReshapeable;
	import mx.collections.ArrayCollection;
	import flash.utils.Dictionary;
	import com.yworks.canvas.model.IModelItem;

	public class ScenarioStage implements IPaintable, IBoundsProvider, IHitTestable, IReshapeable, IModelItem
	{
		public var name:String ;
		public var x:Number = 0 ;
		public var y:Number = 0 ;
		public var width:Number ;
		public var height:Number ;
		public var textFormat:TextFormat ;
		public var selectedTextFormat:TextFormat ;
		public var fill:IFill ;
		public var selectedFill:IFill ;
		public var stroke:Stroke ;
		public var selectedStroke:Stroke ;
		private var _selected:Boolean ;
		
		public function paint(g:YGraphics, ctx:IPaintContext):void {
			if (_selected)
				g.applyStroke(selectedStroke) ;
			else
				g.applyStroke(stroke) ;
			g.drawRoundRect(x, y, width, height, width/100, height/100) ;
			if (_selected)
				g.applyFill(selectedFill) ;
			else
				g.applyFill(fill) ;
			g.drawRoundRect(x, y, width, 20, width/100, height/100) ;
			if (_selected)
				g.drawString(name, selectedTextFormat, x, y, width) ;
			else
				g.drawString(name, textFormat, x, y, width) ;
		}
		
		private function initialize(name:String):void {
			this.name = name ;
			fill = new SolidColor(0x4A0D24) ;
			selectedFill = new SolidColor(0x440000) ;
			stroke = new Stroke(0, 1.0) ;
			stroke.scaleMode = 'noScale' ;
			selectedStroke = new Stroke(0x440000,1.5) ;
			selectedStroke.scaleMode = 'noScale' ;
			textFormat = new TextFormat() ;
			textFormat.bold = true ;
			textFormat.color = 0xFFFFFF ;
			textFormat.italic = false ;
			textFormat.align = 'center' ;
			textFormat.font = 'Verdana' ;
			textFormat.size = 12 ;
			selectedTextFormat = new TextFormat() ;
			selectedTextFormat.bold = true ;
			selectedTextFormat.italic = true ;
			selectedTextFormat.color = 0xFF6633 ;
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
			&& (y <= (this.y + this.height)) ;
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
		
		public function set selected(value:Boolean):void {
			_selected = value ;
		}
		
		public function get selected():Boolean {
			return _selected ;
		}
		
		private static var scenarioStages:Dictionary = new Dictionary() ;
		
		private static var _lastID:uint = 0 ;
		
		private static function getNewID():uint {
			_lastID ++ ;
			return _lastID ;
		}
		
		private var _id:uint ;
		
		public function get internalID():uint {
			return this._id ;
		}
		
		public static function createScenarioStage(name:String):ScenarioStage {
			var ss:ScenarioStage = new ScenarioStage() ;
			ss.initialize(name) ;
			var lss:ScenarioStage = scenarioStages[_lastID] ;
			if (lss != null)
				ss.x = lss.x + lss.width ;
			ss._id = getNewID() ;
			scenarioStages[ss._id] = ss ;
			return ss ;
		}
		
		public static function deleteScenarioStage(id:uint):void {
			delete scenarioStages[id] ;
		}
	}
}