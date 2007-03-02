// JavaScript extensions

console.info("Top of script");

String.prototype.trim = function() { return this.replace(/^\s+|\s+$/, ''); };

zk.load(
	"ext.json.json",
	function () {
		return Object.prototype.toJSONString != "undefined";
	});

// Must be set prior to loading mxGraph js library
mxBasePath = '/channels/ext/mxgraph/';
// Load mxGraph js library
zk.load(
	"ext.mxgraph.js.mxClient",
	function () {
		return typeof mxGraph != "undefined";
	});

zkMxGraph = {};

zm_menus = {};

zm_overlays = {};

zkMxGraph.init = function (container) {
	console.debug("zkMxGraph.init");
    if (mxClient.isBrowserSupported()) {
	    if (mxClient.IS_IE) { new mxDivResizer(container);}
        //create the graph model
        var model = new mxGraphModel();
        model.createdIds = false; // Created server-side
        // Create graph and set default state
		var graph = new mxGraph(container, model);
		graph.setTooltips(true);
		container._graph = graph;
		// Installs a popupmenu handler using local function (see below).
		zm_menus[container.id] = {};
		graph.panningHandler.factoryMethod = function(menu, cell, evt) {
				return zm_createPopupMenu(container, menu, cell, evt);
			};
		//init the graph
		var initValue = zm_unescape(getZKAttr(container, "init"));
		console.debug("init = " + initValue);
		var init = initValue.parseJSON();
		zm_initProperties(graph, init.properties);
		zm_initLayout(graph, init.layout);
		zm_initStyleSheet(graph, init.styleSheet);
		zm_initModel(container, init.model);
		zm_initPanningHandler(graph, init.panningHandler);
		zm_initSelection(graph, init.selection);
		zm_menus[container.id] = init.menus;
				
		zm_registerEvents(container);
	}
    else {
    	alert("Your browser is not supported. Please use Firefox 1.5 and later or Internet Explorer 5.5 and later.");
    }
};

zkMxGraph.cleanup = function (container) {
	console.debug("zkMxGraph.cleanup");
	zm_menus[container.id] = null;
    if (container._graph != null) {
        zm_deregisterEvents(container._graph);
    }
};

// Command handling

zkMxGraph.setAttr = function (container, command, val) {
	console.debug("setAttr " + command, + " " + val);
    switch (command) {
    	case "z:setProperty":
    		zm_setProperty(container._graph, val);
    		return true;
    	case "z:setPanning":
    		zm_setPanning(container._graph, val);
    		return true;
		case "z:addVertex":
			zm_addVertex(container, val);
			return true;
		case "z:addEdge":
			zm_addEdge(container, val);
			return true;
		case "z:removeCells":
			zm_removeCells(container._graph, val);
			return true;
		case "z:addMenu":
			zm_addMenu(container, val);
			return true;
		case "z:select":
			zm_select(container._graph, val);
			return true;
        case "z:addOverlay":
        	zm_setOverlay(container, val);
        	return true;
		case "z:removeOverlay":
			zm_removeOverlay(container._graph, val);
			return true;
		case "z:clearOverlays":
			zm_clearOverlays(container, val);
			return true;
		default:
			console.error("Invalid command " + command + " " + val);
			return true;
	}
    return false;
};

function zm_unescape(value) {
	var regex = /\+/g;
	return unescape(String(value).replace(regex, " "));
}

/*
zkMxGraph.rmAttr = function (container, name) {
	// name :: targetType_attributeName:targetId
	...
    switch (name) {
    case "aName":
        container._graph.doSomething() ;
        return true; // command done, do not remove attribute.
        
        ...
    }
    return false;
};
*/


// Initialization

function zm_initProperties(graph, properties) {
	for (prop in properties) {
		graph[prop] = properties[prop];
	}
}

function zm_initPanningHandler(graph, ph) {
	for (prop in ph.properties) {
		graph.panningHandler[prop] = ph.properties[prop];
	}
}

function zm_initLayout(graph, layoutState) {
	if (layoutState != null) {
		var layout;
		switch (layoutState.type) {
			case "FlowLayout":
				layout = new mxFlowLayout(graph);
				layout.vertical = layoutState.vertical;
				layout.spacing = (layoutState.spacing == -1 ? null : layoutState.spacing);
				layout.x0 = (layoutState.x0 == -1 ? null : layoutState.x0);
				layout.y0 = (layoutState.y0 == -1 ? null : layoutState.y0);
				break;
			case "CompactTreeLayout":
				layout = new mxCompactTreeLayout(graph, layoutState.horizontal);
				layout.nodeDistance = layoutState.nodeDistance;
				layout.levelDistance = layoutState.levelDistance;
				break;
			case "FastOrganicLayout":
				layout = new mxFastOrganicLayout(graph);
				layout.forceConstant = layoutState.forceConstant;
				break;
			case "CircleLayout":
				layout = new mxCircleLayout(graph, layoutState.radius);
				break;			
		}
		graph.getLayout=function(cell){ return layout; }
	}
}

function zm_initStyleSheet(graph, styleSheetState) {
	console.debug("zm_initStyleSheet");
	console.debug(styleSheetState);
	var style = graph.stylesheet.getDefaultVertexStyle();
	zm_initStyle(style, styleSheetState.defaultVertexStyle);
	style = graph.stylesheet.getDefaultEdgeStyle();
	zm_initStyle(style, styleSheetState.defaultEdgeStyle);
	var cellStyles = styleSheetState.cellStyles;
	for (styleName in cellStyles) {
		style = {};
		zm_initStyle(style, cellStyles[styleName]);
		graph.stylesheet.putCellStyle(styleName, style);
	}
	
	var style = new Array(); 

	style[mxConstants.STYLE_FONTSIZE] = "0"; 
	style[mxConstants.STYLE_OPACITY] = "0";
	graph.stylesheet.putCellStyle('overlay', style);
}

function zm_initStyle(style, styleState) {
	console.debug("zm_initStyle");
	console.debug(styleState);
	for (attr in styleState) {
		if (attr == mxConstants.STYLE_PERIMETER) {
			switch(styleState[attr]) {
				case "RectanglePerimeter":
					style[attr] = mxPerimeter.RectanglePerimeter;
					break;
				case "RightAngleRectanglePerimeter":
					style[attr] = mxPerimeter.RightAngleRectanglePerimeter;
					break;
				case "RhombusPerimeter":
					style[attr] = mxPerimeter.RhombusPerimeter;
					break;
				case "EllipsePerimeter":
					style[attr] = mxPerimeter.EllipsePerimeter;
					break;
			}
		}
		else if (attr == mxConstants.STYLE_EDGE) {
			switch(styleState[attr]) {
				case "SideToSide":
					style[attr] = mxEdgeStyle.SideToSide;
					break;
				case "TopToBottom":
					style[attr] = mxEdgeStyle.TopToBottom;
					break;
			}
		}
		else {
			style[attr] = styleState[attr];			
		}
	}
}

function zm_initModel(container, modelState) {
	console.debug("zm_initModel");
	console.debug(modelState);
	console.debug(modelState.toJSONString());
	var graph = container._graph;
	var model = graph.getModel();
	var parent = graph.getDefaultParent();						
	// Adds cells to the model in a single step
	model.beginUpdate();
	try {
		for (inx=0; inx< modelState.vertices.length; inx++) {
			//var v = modelState.vertices[inx];
			zm_createVertex(container, modelState.vertices[inx]);
			console.debug("added vertex " + modelState.vertices[inx].value);
		}
		for (jnx=0; jnx< modelState.edges.length; jnx++) {
			var e = modelState.edges[jnx];
			zm_createEdge(container, e);
			console.debug("added edge " + e.value);
		}
		graph.getLayout(parent).execute(parent);
	}
	finally
	{
		// Updates the display
		model.endUpdate();
	}
}

function zm_initSelection(graph, ids) {
	var model = graph.getModel();
	for (i=0; i<ids.length; i++) {
		var id = ids[i];
		graph.selection.tryAddCell(model.getCell(id));
	}
}

// Popup menu

function zm_createPopupMenu(container, menu, cell, evt) {
	// looks up popupMenus for the most specific menu applicable
	var menuDao;
	if (cell != null) {
		menuDao = zm_menus[container.id][cell.id];
		if (menuDao == null) {
			if (cell.isVertex()) { 
				menuDao = zm_menus[container.id]["vertex"];
			}
			else { 
				menuDao = zm_menus[container.id]["edge"]; 
			}
			if (menuDao == null) menuDao = zm_menus[container.id]["cell"];
		}
	}
	if (menuDao == null) {
		menuDao = zm_menus[container.id]["all"];
	}
	// then build it
	// ("itemName, imagePath, command", "parameters") or "_separator_"
	if (menuDao != null) {
		for (i=0; i<menuDao.items.length; i++) {
			var itemDao = menuDao.items[i];
			if (itemDao.name == "_separator_") {
				menu.addSeparator();
				}
			else {
				var data = new Array();
				if (cell != null) {data.push(cell.id);} else {data.push("");}
				for (j=0; j<itemDao.parameters.length; j++) {
					data.push(itemDao.parameters[j]);
				}
				// avoid closure...
				var fnString = "function() { zm_sendMenuCommand('" + container.id + "', '" + data.toJSONString() + "', '" + itemDao.command + "'); }";
				var fn = eval(fnString);
				menu.addItem(itemDao.name, itemDao.icon, fn);
			}
		}
	}
}

function zm_sendMenuCommand(containerId, jsonData, command) {
	var data = jsonData.parseJSON();
	zkau.send({uuid: containerId, cmd: command, data: data}, 25);
}

function zm_sendCommand(container, data, command) {
	zkau.send({uuid: container.id, cmd: command, data: data}, 25);
//			  zkau.asapTimeout(container, command));

}

// Event handling

function zm_registerEvents(container) {
	console.debug("zm_registerEvents");
	// for each event type...
	var graph = container._graph;
	graph.addListener("select", 
			function(source, cells) {
				var ids = new Array();
				if (cells != null) {
					for (i=0;i<cells.length;i++) {
						var cellId = cells[i].getId();
						ids.push(cellId);
					}
				}
				zm_sendCommand(container, ids, "onSelectCells");
			});
}

function zm_deregisterEvents(graph) {
	// do nothing
}

			  
// Commands from server

function zm_setProperty(graph, value) {
	var splitString = value.split(':');
	graph[splitString[0]] = splitString[1];
}

function zm_setPanning(graph, value) {
	var splitString = value.split(':');
	graph.panningHandler[splitString[0]] = splitString[1];
}

// menuName in "all, cell, vertex, edge, <a cell id>"
function zm_addMenu(container, value) {
	var menuDao = value.parseJSON();
	zm_menus[container.id][menuDao.name] = menuDao;
}

function zm_createVertex(container, v) {
	var graph = container._graph;
	var vertex = new mxCell(v.value, new mxGeometry(v.geometry.x, v.geometry.y, v.geometry.width, v.geometry.height), v.style);
	vertex.vertex = true; vertex.edge = false;
	vertex.setId(v.id);	
	var parent;
	if (v.parent != null) {
	 	parent = graph.getModel().getCell(v.parentId);
	} 
	graph.addCell(vertex, parent,null,null,null);
	for (i = 0 ; i < v.overlays.length ; i++) {
		zm_createOverlay(container, v.overlays[i].id, vertex, v.overlays[i]);
	}
	
}

// value is json-ed MxVertex
function zm_addVertex(container, value) {
	console.debug("addVertex " + value);
	var obj = value.parseJSON();
	zm_createVertex(container, obj);
}

function zm_createEdge(container, obj) {
	var graph = container._graph;
	var edge = new mxCell(obj.value, new mxGeometry(), obj.style);
	edge.vertex = false; edge.edge = true;
	edge.setId(obj.id);
	var parent;
	if (obj.parent != null) {
		parent = graph.getModel().getCell(obj.parentId);
	} 
	var source = graph.getModel().getCell(obj.source);
	var target = graph.getModel().getCell(obj.target);
	graph.addEdge(edge, parent, source, target, null);
	for (i = 0 ; i < obj.overlays.length ; i++) {
		zm_createOverlay(container, obj.overlays[i].id, edge, obj.overlays[i]);
	}
}

// value is json-ed MxEdge
function zm_addEdge(container, value) {
	console.debug("addEdge " + value);
	var obj = value.parseJSON();
	zm_createEdge(container, obj);
}

// value is an array of ids
function zm_removeCells(graph, value) {
	var ids = value.parseJSON();
	var cells = new Array();
	for (i=0; i<ids.length; i++) {
		var id = ids[i];
		var cell = graph.model.getCell(id);
		cells.push(cell);
	}
	graph.remove(cells);
}

function zm_select(graph, value) {
	var ids = value.parseJSON();
	var cells = new Array();
	for (i=0; i<ids.length; i++) {
		var id = ids[i];
		var cell = graph.getModel().getCell(id);
		if (cell != null) cells.push(cell);
	}
	graph.setSelectionCells(cells);
}

function zm_createOverlay(container, id, cell, v) {
	var graph = container._graph;
	var model = graph.getModel();
	var overlay = zm_overlays[id];
	if ( overlay == null) {
	    overlay = new mxOverlay(v.image, v.tooltip, v.bounds.width, v.bounds.height);
		zm_overlays[id] = overlay;
		overlay.addListener("click", function(sender, evt, cell) {
			var ids = new Array();
			ids.push(cell.parent.id);
			ids.push(id);
			zm_sendCommand(container, ids, "onClickOverlay");
		});
		overlay.getBounds = function(state) {
			return new mxGeometry(state.x, state.y, state.width, state.height);
		}
	}
	var v = model.addVertex(cell, cell.id+id, '', v.bounds.x, v.bounds.y, v.bounds.width, v.bounds.height);
	model.setStyle(v, "overlay");
	container._graph.setOverlay(v, overlay);
}

function zm_addOverlay(container, value) {
	console.debug("zm_addOverlay");
	var obj = value.parseJSON();
	var model = container._graph.getModel();
	var cell = model.getCell(obj.cell);
	zm_createOverlay(container, obj.id, cell, obj);
}

function zm_removeOverlay(graph, value) {
	console.debug("zm_removeOverlay");
	var model = graph.getModel();
	var obj = value.parseJSON();
	var id = obj.cell + obj.overlay;
	var cell = model.getCell(id);
	model.remove(cell);
}