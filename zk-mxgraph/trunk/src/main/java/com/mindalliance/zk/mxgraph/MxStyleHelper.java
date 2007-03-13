/**
 * 
 */
package com.mindalliance.zk.mxgraph;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dfeeney
 *
 */
public class MxStyleHelper {
	public static Map getActorStyle() {
		Map style = new HashMap();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_ACTOR);
		style.put(MxConstants.STYLE_FILLCOLOR, "#EEEEEE");
		style.put(MxConstants.STYLE_STROKECOLOR, "#6482B9");
		style.put(MxConstants.STYLE_STROKEWIDTH, "2");
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_RECTANGLE_PERIMETER);
		return style;
	}
	
	public static Map getSwimlaneStyle() {
		Map style  = new HashMap();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_SWIMLANE);
		style.put(MxConstants.STYLE_FILLCOLOR, "#EEEEEE");
		style.put(MxConstants.STYLE_STROKECOLOR, "#6482B9");
		style.put(MxConstants.STYLE_STROKEWIDTH, "2");
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_RECTANGLE_PERIMETER);
		return style;
	}
	
	public static Map getArrowStyle() {
		Map style = new HashMap();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_ARROW);
		style.put(MxConstants.STYLE_FILLCOLOR, "#EEEEEE");
		style.put(MxConstants.STYLE_STROKECOLOR, "#6482B9");
		style.put(MxConstants.STYLE_STROKEWIDTH, "2");
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_RECTANGLE_PERIMETER);
		style.put(MxConstants.STYLE_EDGE, MxConstants.STYLE_EDGE_SIDE_TO_SIDE);
		style.put(MxConstants.STYLE_ALIGN, MxConstants.ALIGN_CENTER);
		style.put(MxConstants.STYLE_VERTICAL_ALIGN,MxConstants.ALIGN_MIDDLE);
		style.put(MxConstants.STYLE_ENDARROW,MxConstants.ARROW_CLASSIC);
		style.put(MxConstants.STYLE_FONTSIZE,"10");
		return style;
	}
	
	public static Map getCylinderStyle() {
		Map style = new HashMap();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_CYLINDER);
		style.put(MxConstants.STYLE_FILLCOLOR, "#EEEEEE");
		style.put(MxConstants.STYLE_STROKECOLOR, "#6482B9");
		style.put(MxConstants.STYLE_STROKEWIDTH, "2");
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_RECTANGLE_PERIMETER);
		return style;
	}
	
	public static Map getEllipseStyle() {
		Map style = new HashMap();
		style.put(MxConstants.STYLE_SHAPE, MxConstants.SHAPE_ELLIPSE);
		style.put(MxConstants.STYLE_FILLCOLOR, "#EEEEEE");
		style.put(MxConstants.STYLE_STROKECOLOR, "#6482B9");
		style.put(MxConstants.STYLE_STROKEWIDTH, "2");
		style.put(MxConstants.STYLE_PERIMETER, MxConstants.STYLE_VERTEX_ELLIPSE_PERIMETER);
		return style;
	}
	
	
}
