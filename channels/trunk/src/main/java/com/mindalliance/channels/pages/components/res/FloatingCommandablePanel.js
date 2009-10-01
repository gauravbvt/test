__DEBUG__ = false;

if ( typeof(Floater) == "undefined" ) {
    Floater = { };
    StyleStates = {};
}

Floater.findMaxZIndex = function() {
    if ( __DEBUG__ ) console.log("Finding max z-index");
    var maxZIndex = 0;
    for ( var elementId in StyleStates ) {
        var style = StyleStates[elementId];
        if ( typeof(style.zIndex) != "undefined" )
            maxZIndex = Math.max(maxZIndex, parseInt(style.zIndex));
    }
    if ( __DEBUG__ ) console.log("Max z-index is %i", maxZIndex);
    return maxZIndex;
}

Floater.moveToTop = function( element ) {
    if ( __DEBUG__ ) console.log("Moving %s at %s to top", element.id, element.style.zIndex);
    var maxZIndex = Floater.findMaxZIndex();
    var z = 0;
    if ( typeof(element.style.zIndex) != "undefined" )
        z = parseInt(element.style.zIndex);
    if ( z <= maxZIndex ) {
        element.style.zIndex = maxZIndex + 1;
        Floater.recordStyle(element.id);
        if ( __DEBUG__ ) console.log("z-index of %s changed to %s", element, element.style.zIndex);
    }
}

Floater.recordStyle = function( elementId ) {
    var element = document.getElementById(elementId);
    if ( __DEBUG__ ) console.log("Recording style %s for %s", element.style.toString(), elementId);
    StyleStates[elementId] = {};
    StyleStates[elementId].top = element.style.top;
    StyleStates[elementId].left = element.style.left;
    StyleStates[elementId].bottom = element.style.bottom;
    StyleStates[elementId].width = element.style.width;
    StyleStates[elementId].zIndex = element.style.zIndex;
}

Floater.restoreStyle = function( elementId ) {
    var element = document.getElementById(elementId);
    if ( __DEBUG__ ) console.log("Attempting to restore style for %s", element.id);
    var style = StyleStates[elementId];
    if ( typeof(style) != "undefined" ) {
        if ( __DEBUG__ ) console.log("Restoring prior style");
        if ( typeof(style.top) != "undefined" ) {
            element.style.top = style.top;
            if ( __DEBUG__ ) console.log("top = %s", style.top);
        }
        if ( typeof(style.left) != "undefined" ) {
            element.style.left = style.left;
            if ( __DEBUG__ ) console.log("left = %s", style.left);
        }
        if ( typeof(style.bottom) != "undefined" ) {
            element.style.bottom = style.bottom;
            if ( __DEBUG__ ) console.log("bottom = %s", style.bottom);
        }
        if ( typeof(style.width) != "undefined" ) {
            element.style.width = style.width;
            if ( __DEBUG__ ) console.log("width = %s", style.width);
        }
        if ( typeof(style.zIndex) != "undefined" ) {
            element.style.zIndex = style.zIndex;
            if ( __DEBUG__ ) console.log("z-index = %s", style.zIndex);
        }
    }
    Floater.recordStyle(elementId);
    Floater.moveToTop(element);
    element.style.display = "block";
}

Floater.beginMove = function( elementToMove, event ) {
    Floater.moveToTop(elementToMove);
    var startX = event.clientX;
    var startY = event.clientY;
    var startLeft = parseInt(elementToMove.style.left);
    var startTop = parseInt(elementToMove.style.top);
    var startBottom = parseInt(elementToMove.style.bottom);

    if ( document.addEventListener ) {
        document.addEventListener("mousemove", moveHandler, true);
        document.addEventListener("mouseup", upHandler, true);
    }
    else if ( document.attachEvent ) {
        document.attachEvent("onmousemove", moveHandler);
        document.attachEvent("onmouseup", upHandler);
    }
    else {
        var oldmovehandler = document.onmousemove;
        var olduphandler = document.onmouseup;
        document.onmousemove = moveHandler;
        document.onmouseup = upHandler;
    }
    if ( event.stopPropagation ) event.stopPropagation();
    else event.cancelBubble = true;
    if ( event.preventDefault ) event.preventDefault();
    else event.returnValue = false;

    function moveHandler( e ) {
        if ( !e ) e = window.event;
        var deltaX = e.clientX - startX;
        var deltaY = e.clientY - startY;
        var left = startLeft + deltaX;
        var top = startTop + deltaY;
        var bottom = startBottom - deltaY;
        if ( left > 0 && top > 0 && bottom > 0 ) {
            elementToMove.style.left = left + "px";
            elementToMove.style.top = top + "px";
            elementToMove.style.bottom = bottom + "px";
            Floater.recordStyle(elementToMove.id);
        }
        if ( e.stopPropagation ) e.stopPropagation();
        else e.cancelBubble = true;
    }

    function upHandler( e ) {
        if ( !e ) e = window.event;
        if ( document.removeEventListener ) {
            document.removeEventListener("mouseup", upHandler, true);
            document.removeEventListener("mousemove", moveHandler, true);
        }
        else if ( document.detachEvent ) {
            document.detachEvent("onmouseup", upHandler);
            document.detachEvent("onmousemove", moveHandler);
        }
        else {
            document.onmouseup = olduphandler;
            document.onmousemove = oldmovehandler;
        }
        if ( e.stopPropagation ) e.stopPropagation();
        else e.cancelBubble = true;
    }
}

Floater.beginResize = function( elementToResize, event ) {
    Floater.moveToTop(elementToResize);
    var startX = event.clientX;
    var startY = event.clientY;
    var startWidth = parseInt(elementToResize.style.width);
    var startHeight = $(elementToResize).height();
    var startBottom = parseInt(elementToResize.style.bottom);

    if ( document.addEventListener ) {
        document.addEventListener("mousemove", moveHandler, true);
        document.addEventListener("mouseup", upHandler, true);
    }
    else if ( document.attachEvent ) {
        document.attachEvent("onmousemove", moveHandler);
        document.attachEvent("onmouseup", upHandler);
    }
    else {
        var oldmovehandler = document.onmousemove;
        var olduphandler = document.onmouseup;
        document.onmousemove = moveHandler;
        document.onmouseup = upHandler;
    }
    if ( event.stopPropagation ) event.stopPropagation();
    else event.cancelBubble = true;
    if ( event.preventDefault ) event.preventDefault();
    else event.returnValue = false;

    function moveHandler( e ) {
        if ( !e ) e = window.event;
        var deltaX = e.clientX - startX;
        var deltaY = e.clientY - startY;
        var width = startWidth + deltaX;
        var height = startHeight + deltaY;
        var bottom = startBottom - deltaY;
        if ( height > 300 && width > 300 && bottom > 0 ) {
            elementToResize.style.width = width + "px";
            elementToResize.style.bottom = bottom + "px";
            Floater.recordStyle(elementToResize.id);
        }
        if ( e.stopPropagation ) e.stopPropagation();
        else e.cancelBubble = true;
    }

    function upHandler( e ) {
        if ( !e ) e = window.event;
        if ( document.removeEventListener ) {
            document.removeEventListener("mouseup", upHandler, true);
            document.removeEventListener("mousemove", moveHandler, true);
        }
        else if ( document.detachEvent ) {
            document.detachEvent("onmouseup", upHandler);
            document.detachEvent("onmousemove", moveHandler);
        }
        else {
            document.onmouseup = olduphandler;
            document.onmousemove = oldmovehandler;
        }
        if ( e.stopPropagation ) e.stopPropagation();
        else e.cancelBubble = true;
    }

}



