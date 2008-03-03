//<![CDATA[

// This is for preventing namespace collision.
// Doing this ensures that only things that are
// exported are available and only on the masMap
// object.
// Code lifted from: http://ajaxcookbook.org/javascript-api-namespaces/
// Once this is made into a jquery plugin we won't need this because
// jquery allows encapsulation in exactly the same way.
var masMap = new Object();

/* 
    function used to make symbols available through
    this namespace.
 */
function _exportSymbol(name, symbol) {
  masMap[name] = symbol;
}

(function(){

var _gmap ;

// infinity is nice but lat lngs don't go beyond +/- 90 so we don't
// need it 
var _maxBounds = {minX:10000, minY:10000, maxX:-10000, maxY:-10000}

/* 
    internal function.
    updates the bounds of objects visible in the map
    i.e. overlays etc.
    if 'full' is true, does a full recomputation
    of bounds. see inline comments.
 */
function _updateMapMinMaxBounds(lat, lng, full) {
    // when an object is deleted, the maxbounds can go up
    // or down and there's no way to figure out the actual
    // bounds without iterating through all existing overlays
    if (full)
        _maxBounds = _computeFullBounds(overlays) ;
    else
    // when an object is added, the maxbounds can only
    // increase, but not decrease so all we need to do is
    // check the incoming dimensions.
    // lat is y, lng is x   
        _maxBounds = _updateMaxBounds(_maxBounds, lng, lat) ;
}

/* 
    internal function.
    given current bounds and new coordinates,
    updates the extent of the bounding box
    so that it encompasses the new coordinates.
 */
function _updateMaxBounds(curBounds, x, y) {
    var maxBounds = {minX : curBounds.minX, 
                     minY : curBounds.minY, 
                     maxX : curBounds.maxX, 
                     maxY : curBounds.maxY} ;
    
    if (x < maxBounds.minX) maxBounds.minX = x ;
    if (y < maxBounds.minY) maxBounds.minY = y ;
    if (x > maxBounds.maxX) maxBounds.maxX = x ;
    if (y > maxBounds.maxY) maxBounds.maxY = y ;
    
    return maxBounds ;
}

/* 
    internal function.
    iterates through an array of overlays and
    computes the bounding box that will encompass them
    all.
 */
function _computeFullBounds(overlayContainerArray) {
    var fullBounds = {minX : 10000, minY : 10000, maxX : -10000, maxY : -10000 } ;
    
    if (overlayContainerArray.length < 1)
        return fullBounds;
    
    for (var i = 1; i < overlayContainerArray.length ; i ++) {
        var o = overlayContainerArray[i].getOverlay() ;
        var latlng = o.getLatLng() ;
        // lat is y, lng is x
        fullBounds = _updateMaxBounds(fullBounds, latlng.lng(), latlng.lat())
    }
    return fullBounds ;
}

/* 
    internal object.
    used to keep track of state of overlays.
    there is actually no need for storing these
    locally if we can get these from server, but
    since they are small its not going to kill the
    client to cache them here.
 */
function OverlayContainer(overlay, type, state) {
    this._overlay = overlay ;
    this._type = type ;
    this._state = state ;
    this._infoTab = {title:'', content:''} ;
    this._menuTab = {title:'', content:''} ;
}
// useless getter functions to remove later
OverlayContainer.prototype.getOverlay = function() {return this._overlay;}
OverlayContainer.prototype.getType = function() {return this._type;}
OverlayContainer.prototype.getState = function(){return this._state;}

// Icon resources
// Hard-coded for now. Should eventually be stored and loaded from NK.
// -- how image icons work --
// - when adding a marker, specify the type of marker using a string parameter
// - the string parameter is used to pull the appropriate icon for the type.
// - note that stupid google maps won't allow just any image. it needs a 
// 24 bit png with true alpha transparency. they did that just cuz we had so
// much free time.
// - this is also why the organization icons aren't showing correctly right
// now. will fix images when we get to the point where we decide what objects
// to show on the map and what their representation should be.
var _imagesDir = "images/" ;
var _iconResource = new Array() ;
_iconResource["person_normal"] = _imagesDir + "pawn_glass_blue.png" ;
_iconResource["person_error"] = _imagesDir + "pawn_glass_blue_border_red.png" ;
_iconResource["organization_normal"] = _imagesDir + "office-building.png" ;
_iconResource["organization_error"] = _imagesDir + "office-building_border_red.png" ;

// stores all overlays in an associative array. don't just iterate over this
// because you won't find anything if you don't have the keys ;)
var overlays = new Array() ;
_exportSymbol('_overlays', overlays) ;

/* 
    internal function.
    given an icon resource key (a string), creates a custom
    marker corresponding to the resource and returns it.
 */
function _createGIconFor(iconResourceKey) {
    // this is where we would place some kinda
    // ajax call to get the desired icon from
    // the server and then put the rest of this
    // function in a function that's triggered
    // when the server response arrives
    var imgURL = _iconResource[iconResourceKey] ;
    
    if (!imgURL)
        return null ;
    var gi = new GIcon() ;
    gi.image = imgURL;
    gi.iconSize = new GSize(32, 32) ;
    //gi.transparent = 'images/transparent16x16.png' ;
    gi.shadowAnchor = new GPoint(0, 32) ;
    gi.iconAnchor = new GPoint(32, 32) ;
    gi.infoWindowAnchor = new GPoint(0, 0) ;
    gi.imageMap = [1,1, 32,1, 32,32, 1,32]; 
    return gi ;
}

/* 
    internal function.
    encapsulates the logic of creating the
    key that pulls the appropriate icon.
 */
function _constructIconResourceKey(type, state) {
    return type + "_" + state ;
}

/* 
    given an html element and width and height,
    creates the google map in it.
 */
function createMap(elementID, width, height) {
    if (GBrowserIsCompatible()) {
        elem = document.getElementById(elementID) ;
        if (width)
            elem.style.width = width ;
        if (height)
            elem.style.height = height ;
        _gmap = new GMap2(elem);
        _gmap.addControl(new GSmallMapControl()) ;
        GEvent.addListener(_gmap,"click", _mapClicked) ;
        _exportSymbol('_gmap', _gmap) ;
      }
}
_exportSymbol('createMap', createMap) ;

/* 
    returns the bounds of all the objects on the map
    i.e. the bounding box. if 'human' is true,
    returns a human-readable string instead of the
    bounds object.
 */
function getBounds(human) {
    if (human)
        return 'minX: ' + _maxBounds.minX + 
               ' minY: ' + _maxBounds.minY +
               ' maxX: ' + _maxBounds.maxX +
               ' maxY: ' + _maxBounds.maxY ;
    else
        return _maxBounds ;
}
_exportSymbol('getBounds', getBounds) ;

/* 
    adjusts the map so that all the objects on
    the map are visible in the viewport, changing
    zoom level if necessary.
    if 'doPan' is true and the amount of panning
    needed is reasonable, it will do a smooth
    pan from wherever the map is centered to the
    center of the bounding box of all objects.
 */
function fitVisibleOverlays(doPan) {
    // standard coordinate system
    // bottom to top, left to right
    var sw = new GLatLng(_maxBounds.minY, _maxBounds.minX) ;
    var ne = new GLatLng(_maxBounds.maxY, _maxBounds.maxX) ;
    var bounds = new GLatLngBounds(sw, ne) ;
    
    var zl = _gmap.getBoundsZoomLevel(bounds) ;
    var boundsCenter = bounds.getCenter() ;
    
    centerOn(boundsCenter.lat(), boundsCenter.lng(), zl, doPan) ;
}
_exportSymbol('fitVisibleOverlays', fitVisibleOverlays) ;

/* 
    centers the map at the specified lat lng,
    changes zoomlevel if specified and does a smooth
    pan if reasonable.
 */
function centerOn(lat, lng, newZoomLevel, doPan) {
    gll = new GLatLng(lat, lng) ;
    if (doPan) {
        var currentZoom = _gmap.getZoom() ;
        // no new zoom level specified
        if (!newZoomLevel) {
            _gmap.panTo(gll) ;
        }
        else {
	        // pan as little as possible to prevent
	        // any extra tiles from being loaded
	        // if we are zooming in
	        // first pan then zoom
	        if (currentZoom > newZoomLevel) {
		        _gmap.panTo(gll) ;
		        _gmap.setZoom(newZoomLevel) ;
	        }
	        // else if we are zooming out
	        // first zoom then pan
	        else {
	            _gmap.setZoom(newZoomLevel) ;
	            _gmap.panTo(gll) ;
	        }
        }
    }
    else {
        if (newZoomLevel)
	        _gmap.setCenter(gll, newZoomLevel) ;
	    else
	        _gmap.setCenter(gll) ;
    }
}
_exportSymbol('centerOn', centerOn) ;

/* 
    internal function.
    given an id returns the marker associated with the id.
 */
function _getMarker(id) {
    var oc = overlays[id] ;
    if (!oc)
        return null ;
    return oc._overlay ;
}

/* 
    internal function.
    each marker can be associated with a tabbed information
    window. as of now, one of those tabs is being used to show the menu
    for the object. this function will recreate tabs for
    display of description and the menu. if we want to
    change how the description or menu is shown, we need to change
    only this function.
 */
function _rebindMarkerInfoTabs(overlayContainer) {
    var oc = overlayContainer ;

    var tabArray = new Array() ;
    if (oc._infoTab.title) {
        var desc = new GInfoWindowTab(oc._infoTab.title, oc._infoTab.content) ;
        tabArray.push(desc) ;
    }
    if (oc._menuTab.title) {
	    var menu = new GInfoWindowTab(oc._menuTab.title, oc._menuTab.content) ;
        tabArray.push(menu) ;
    }
    var marker = oc._overlay ;
    marker.bindInfoWindowTabsHtml(tabArray) ;
}

/* 
    creates a new menu given the marker id and html content.
    replaces the existing one.
 */
function createOrReplaceMarkerMenu(id, menuHTML) {
    var oc = overlays[id] ;
    if (!oc)
        return ;
    oc._menuTab.title = 'menu' ;    
    oc._menuTab.content = menuHTML ;
    _rebindMarkerInfoTabs(oc) ;
}
_exportSymbol('createOrReplaceMarkerMenu', createOrReplaceMarkerMenu) ;

/* 
    adds a new marker to the map with the specified id, lat and lng.
    type is used to determine what icon to use.
    state can be 'normal' or 'error'. title and descHTML are used
    to add an info window.
 */
function addMarker(id, lat, lng, type, state, title, descHTML) {
    var gicon = _createGIconFor(_constructIconResourceKey(type, state)) ;    
    var marker = new GMarker(new GLatLng(lat, lng), {icon:gicon}) ;
    overlays[id] = new OverlayContainer(marker, type, state) ;
    
    // Is this a hack? Who knows? It works :)
    marker['__masIDKey'] = id ;
    
    _gmap.addOverlay(marker) ;
    if (descHTML) {
	    overlays[id]._infoTab.title = title ;
	    overlays[id]._infoTab.content = descHTML ;
	    _rebindMarkerInfoTabs(overlays[id]) ;
    }
   
    // update bounds
    var latlng = marker.getLatLng() ;
    _updateMapMinMaxBounds(latlng.lat(), latlng.lng()) ;
}
_exportSymbol('addMarker', addMarker) ;

/* 
    selection listener stuff.
    add a function with one parameter as the listener.
    when a marker is clicked, the listeners are all notified
    of the selected marker.
    so if we want populate menus dynamically, we can just
    write a function that responds to selection, makes a
    request, builds the menu from the returned XML and uses 'createOrReplaceMarkerMenu'
    function. that's all there is to it.
 */
var _selectedItems = new Array() ;
var _listeners = new Array() ;
function addSelectionListener(listener) {
    if (_listeners.indexOf(listener) == -1)
        _listeners.push(listener) ;
}
_exportSymbol('addSelectionListener', addSelectionListener) ;

/* 
    self explanatory.
 */
function removeSelectionListener(listener) {
    var lidx = _listeners.indexOf(listener) ;
    if (idx != -1)
        _listeners.splice(lidx, 1) ;
}
_exportSymbol('removeSelectionListener', removeSelectionListener) ;

/* 
    thing that detects selection and clicks. since
    there may be many objects on the map, we detect
    clicks on the canvas and check if an object of
    interest was under the mouse instead of registering
    listeners on every object added (which probably
    does the same thing we are doing in the background
    anyway).
 */
function _mapClicked(overlay, point) {
    // was the overlay something we added?
    if (overlay && overlay.__masIDKey) {
	    // get key for the overlay
	    var selectedItemID = overlay.__masIDKey; 
	    for (var i = 0; i < _listeners.length ; i++)
	        _listeners[i](selectedItemID) ;
    }
    else {
	    for (var i = 0; i < _listeners.length ; i++)
	        _listeners[i](null) ;
    }
}

/* 
    right now objects can be in 'normal' or 'error' state,
    the latter being used to indicate an issue or something.
    we can add as many states as we like. changing state
    to 'error' will put a red border around the icons
    on the map. 
 */
function setState(id, state) {
    oc = overlays[id] ;
    overlayType = oc.getType() ;
    irk = _constructIconResourceKey(overlayType, state) ;
    oc.getOverlay().setImage(_iconResource[irk]) ;
    oc._state = state ;
}
_exportSymbol('setState', setState) ;

/* 
    given an id, gets the state of the object. we won't use
    this because we should be getting state from the server.
 */
function getState(id) {
    return overlays[id].getState() ;
}
_exportSymbol('getState', getState) ;

/* 
    given an id, removes the object from the map and the
    cache. how much simpler can it get?
 */
function removeOverlay(id) {
    oc = overlays[id] ;
    _gmap.removeOverlay(marker.getOverlay()) ;
    delete overlays[id] ;
    
    _updateMapMinMaxBounds(null, null, true) ;
}
_exportSymbol('removeOverlay', removeOverlay) ;

})() ;

//]]>