__DEBUG__ = false;

if ( typeof(Floater) == "undefined" ) {
    Floater = { };
    StyleStates = {};
    NormalStates = {};
    Minimized = {};
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
    var maxZIndex = this.findMaxZIndex();
    var z = 0;
    if ( typeof(element.style.zIndex) != "undefined" )
        z = parseInt(element.style.zIndex);
    if ( z <= maxZIndex ) {
        element.style.zIndex = maxZIndex + 1;
        this.recordStyle(element);
        if ( __DEBUG__ ) console.log("z-index of %s changed to %s", element, element.style.zIndex);
    }
}

Floater.minimizeNormalize = function( minimizeId, padBottom ) {
    e = document.getElementById( minimizeId ).parentNode.parentNode.parentNode.parentNode;
    if ( Minimized[e] != undefined ) {
        // normalizing
        e.style.backgroundColor = NormalStates[e].backgroundColor;
        e.style.border = NormalStates[e].border;
        var top = parseInt(e.style.top);
        var bottom = parseInt(e.style.bottom);
        var deltaY = Minimized[e] - top;
        e.style.top = Minimized[e] + "px";
        e.style.bottom = (bottom - deltaY) + "px";
        Minimized[e] = undefined;
    } else {
        // minimizing
        NormalStates[e] = {};
        NormalStates[e].backgroundColor = e.style.backgroundColor;
        NormalStates[e].border = e.style.border;
        e.style.backgroundColor = "transparent";
        e.style.border = "0";
        Minimized[e] = parseInt(e.style.top);
    }
}

Floater.recordStyle = function( element ) {
    if ( __DEBUG__ ) console.log("Recording style %s for %s", element.style.toString(), element.id);
    StyleStates[element.id] = {};
    StyleStates[element.id].top = element.style.top;
    StyleStates[element.id].left = element.style.left;
    StyleStates[element.id].bottom = element.style.bottom;
    StyleStates[element.id].width = element.style.width;
    StyleStates[element.id].zIndex = element.style.zIndex;
}

Floater.restoreStyle = function( element ) {
    if ( __DEBUG__ ) console.log("Attempting to restore style for %s", element.id);
    var style = StyleStates[element.id];
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
}

Floater.onOpen = function( elementId ) {
    var element = document.getElementById(elementId);
    this.restoreStyle(element);
    this.recordStyle(element);
    this.moveToTop(element);
    element.style.display = "block";
}

Floater.beginMove = function( elementToMove, event, padTop, padLeft, padBottom, padRight ) {
    this.moveToTop(elementToMove);
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
        var left = Math.max(padLeft, startLeft + deltaX);
        var top = startTop + deltaY;
        var bottom = startBottom - deltaY;
        var rightX = left + $(elementToMove).width();
        if ( rightX + padRight < $(elementToMove.parentNode).width() ) {
            elementToMove.style.left = left + "px";
        }
        var actualBottom;
        if ( Minimized[elementToMove] == undefined ) {
            actualBottom = bottom;
        } else {
            actualBottom = top + 30;
        }
        if ( top > padTop && actualBottom > padBottom ) {
            elementToMove.style.top = top + "px";
            elementToMove.style.bottom = bottom + "px";
        }
        Floater.recordStyle(elementToMove);
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

Floater.beginResize = function( elementToResize, event, minWidth, minHeight, padBottom, padRight ) {
    this.moveToTop(elementToResize);
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
        var bottom = Math.max(padBottom, startBottom - deltaY);
        if ( width > minWidth ) {
            elementToResize.style.width = width + "px";
        }
        if ( height > minHeight ) {
            elementToResize.style.bottom = bottom + "px";
        }
        Floater.recordStyle(elementToResize);
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



