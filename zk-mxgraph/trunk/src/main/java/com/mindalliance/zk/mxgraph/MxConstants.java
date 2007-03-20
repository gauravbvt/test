/*
 * Created on Feb 3; 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxConstants {
	
	public static final double RAD_PER_DEG = 0.0174532;
	public static final double DEG_PER_RAD = 57.2957795;
	public static final double ACTIVE_REGION = 0.3;
	public static final int MIN_ACTIVE_REGION = 8;
	public static final String DIALECT_SVG = "svg";
	public static final String DIALECT_VML = "vml";
	public static final String DIALECT_MIXEDHTML = "mixedHtml";
	public static final String DIALECT_PREFERHTML = "preferHtml";
	public static final String DIALECT_STRICTHTML = "strictHtml";
	public static final String NS_SVG = "http = //www.w3.org/2000/svg";
	public static final String NS_XLINK = "http = //www.w3.org/1999/xlink";
	public static final String SVG_SHADOWCOLOR = "gray";
	public static final String SVG_SHADOWTRANSFORM = "translate(2 3)";
	/**
	 * Specified as a  STYLE_VERTEX_*_PERIMETER value
	 */
	public static final String STYLE_PERIMETER = "perimeter";
	/**
	 * As a percent
	 */
	public static final String STYLE_OPACITY = "opacity";
	/**
	 * Specified from #000000 to #FFFFFF
	 */
	public static final String STYLE_FILLCOLOR = "fillColor";
	/**
	 * Specified from #000000 to #FFFFFF
	 */
	public static final String STYLE_GRADIENTCOLOR = "gradientColor";
	/**
	 * Specified from #000000 to #FFFFFF
	 */
	public static final String STYLE_STROKECOLOR = "strokeColor";
	/**
	 * Specified from #000000 to #FFFFFF
	 */
	public static final String STYLE_SEPARATORCOLOR = "separatorColor";
	/**
	 * In pixels
	 */
	public static final String STYLE_STROKEWIDTH = "strokeWidth";
	/**
	 * Specified as an ALIGN_* value
	 */
	public static final String STYLE_ALIGN = "align";
	/**
	 * Specified as an ALIGN_* value
	 */
	public static final String STYLE_VERTICAL_ALIGN = "verticalAlign";
	/**
	 * Specified as an ALIGN_* value
	 */
	public static final String STYLE_IMAGE_ALIGN = "imageAlign";
	/**
	 * Specified as an ALIGN_* value
	 */
	public static final String STYLE_IMAGE_VERTICAL_ALIGN = "imageVerticalAlign";
	/**
	 * Specified as a URL
	 */
	public static final String STYLE_IMAGE = "image";
	/**
	 * In pixels
	 */
	public static final String STYLE_IMAGE_WIDTH = "imageWidth";
	/**
	 * In pixels
	 */
	public static final String STYLE_IMAGE_HEIGHT = "imageHeight";
	public static final String STYLE_INDICATOR_SHAPE = "indicatorShape";
	public static final String STYLE_INDICATOR_IMAGE = "indicatorImage";
	public static final String STYLE_INDICATOR_COLOR = "indicatorColor";
	public static final String STYLE_INDICATOR_STROKECOLOR = "indicatorStrokeColor";
	public static final String STYLE_INDICATOR_GRADIENTCOLOR = "indicatorGradientColor";
	public static final String STYLE_INDICATOR_SPACING = "indicatorSpacing";
	public static final String STYLE_INDICATOR_WIDTH = "indicatorWidth";
	public static final String STYLE_INDICATOR_HEIGHT = "indicatorHeight";
	/**
	 * true/false
	 */
	public static final String STYLE_SHADOW = "shadow";
	/**
	 * As a shape derived from arrow, or ARROW_*
	 */
	public static final String STYLE_ENDARROW = "endArrow";
	/**
	 * As a shape derived from arrow, or ARROW_*
	 */
	public static final String STYLE_STARTARROW = "startArrow";
	/**
	 * In pixels
	 */
	public static final String STYLE_ENDSIZE = "endSize";
	/**
	 * In pixels
	 */
	public static final String STYLE_STARTSIZE = "startSize";
	public static final String STYLE_DASHED = "dashed";
	public static final String STYLE_ROUNDED = "rounded";
	/**
	 * In pixels
	 */
	public static final String STYLE_PERIMETER_SPACING = "perimeterSpacing";
	/**
	 * In pixels
	 */
	public static final String STYLE_SPACING = "spacing";
	/**
	 * In pixels
	 */
	public static final String STYLE_SPACING_TOP = "spacingTop";
	/**
	 * In pixels
	 */
	public static final String STYLE_SPACING_LEFT = "spacingLeft";
	/**
	 * In pixels
	 */
	public static final String STYLE_SPACING_BOTTOM = "spacingBottom";
	/**
	 * In pixels
	 */
	public static final String STYLE_SPACING_RIGHT = "spacingRight";
	/**
	 * In pixels
	 */
	public static final String STYLE_HORIZONTAL = "horizontal";
	public static final String STYLE_FONTCOLOR = "fontColor";
	public static final String STYLE_FONTFAMILY = "fontFamily";
	public static final String STYLE_FONTSIZE = "fontSize";
	public static final String STYLE_FONTSTYLE = "fontStyle";
	/**
	 * From SHAPE_*
	 */
	public static final String STYLE_SHAPE = "shape";
	/**
	 * Specified as a value from STYLE_EDGE_*
	 */
	public static final String STYLE_EDGE = "edgeStyle";
	public static final int FONT_BOLD = 1;
	public static final int FONT_ITALIC = 2;
	public static final int FONT_UNDERLINE = 4;
	public static final int FONT_SHADOW = 8;
	public static final String SHAPE_RECTANGLE = "rectangle";
	public static final String SHAPE_ELLIPSE = "ellipse";
	public static final String SHAPE_RHOMBUS = "rhombus";
	public static final String SHAPE_LINE = "line";
	public static final String SHAPE_IMAGE = "image";
	public static final String SHAPE_ARROW = "arrow";
	public static final String SHAPE_LABEL = "label";
	public static final String SHAPE_CYLINDER = "cylinder";
	public static final String SHAPE_SWIMLANE = "swimlane";
	public static final String SHAPE_CONNECTOR = "connector";
	public static final String SHAPE_ACTOR = "actor";
	public static final String ARROW_CLASSIC = "classic";
	public static final String ALIGN_LEFT = "left";
	public static final String ALIGN_CENTER = "center";
	public static final String ALIGN_RIGHT = "right";
	public static final String ALIGN_TOP = "top";
	public static final String ALIGN_MIDDLE = "middle";
	public static final String ALIGN_BOTTOM = "bottom";
	// Extras (not defined in mxClient.js :: mxConstants)
	/**
	 * Value for STYLE_PERIMETER that indicates a rectangular perimeter should be used for connecting
	 * edges to the vertex
	 */
	public static final String STYLE_VERTEX_RECTANGLE_PERIMETER = "RectanglePerimeter";
	/**
	 * Value for STYLE_PERIMETER that indicates a elliptical perimeter should be used for connecting
	 * edges to the vertex
	 */
	public static final String STYLE_VERTEX_ELLIPSE_PERIMETER = "EllipsePerimeter";
	/**
	 * Value for STYLE_PERIMETER that indicates a rhomboid perimeter should be used for connecting
	 * edges to the vertex
	 */
	public static final String STYLE_VERTEX_RHOMBUS_PERIMETER = "RhombusPerimeter";
	/**
	 * Value for STYLE_PERIMETER that indicates a rectangular perimeter should be used for connecting
	 * edges to the vertex
	 */
	public static final String STYLE_VERTEX_RIGHT_ANGLE_RECTANGLE_PERIMETER = "RightAngleRectanglePerimeter";
	/**
	 * Value for STYLE_EDGE that indicates the edge should layed out from left to right
	 */
	public static final String STYLE_EDGE_SIDE_TO_SIDE = "SideToSide";
	/**
	 * Value for STYLE_EDGE that indicates the edge should layed out from top to bottom
	 */
	public static final String STYLE_EDGE_TOP_TO_BOTTOM = "TopToBottom";
	
	static public final String COMMAND_SELECT = "onSelectCells";
	static public final String COMMAND_DELETE = "onDeleteCells";
	public static final String COMMAND_ADD_VERTEX = "onAddVertex";
	public static final String COMMAND_ADD_EDGE = "onAddEdge";
	public static final String COMMAND_ADD_OVERLAY = "onAddOverlay";
	public static final String COMMAND_REMOVE_OVERLAY = "onRemoveOverlay";
	public static final String COMMAND_CLEAR_OVERLAYS = "onClearOverlays";
	public static final String COMMAND_CLICK_OVERLAY = "onClickOverlay";
	public static final String COMMAND_PUT_STYLE = "onPutStyle";
	public static final String COMMAND_GROUP_CELLS = "onGroupCells";
	public static final String COMMAND_UNGROUP = "onUngroup";
	public static final String COMMAND_ZOOM_IN = "onZoomIn";
	public static final String COMMAND_ZOOM_OUT = "onZoomOut";
	public static final String COMMAND_ZOOM_FIT = "onZoomFit";
	public static final String COMMAND_ZOOM_ACTUAL = "onZoomActual";


}
