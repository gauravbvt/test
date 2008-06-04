
function svg_left(svgElement) {
    svg_translate(svgElement, -20, 0)
}

function svg_right(svgElement) {
    svg_translate(svgElement, 20, 0)
}

function svg_up(svgElement) {
    svg_translate(svgElement, 0, -20)
}

function svg_down(svgElement) {
    svg_translate(svgElement, 0, 20)
}

function svg_zoomIn(svgElement) {
    svg_setScale(svgElement, 1.25, 1.25);
}

function svg_zoomOut(svgElement) {
    svg_setScale(svgElement, 0.75, 0.75);
}

function svg_reset(svgElement) {
    getTransformList(svgElement).clear();
}

function svg_translate(svgElement, x, y) {
    var xform = getSVGTransform(svgElement);
    xform.setTranslate(x, y);
    getTransformList(svgElement).appendItem(xform);
}

function svg_setScale(svgElement, x, y) {
    var xform = getSVGTransform(svgElement);
    xform.setScale(x, y);
    getTransformList(svgElement).appendItem(xform);
}

function getTransformList(svgElement) {
    return getSVGElement(svgElement).firstChild.transform.baseVal;
}

function getSVGTransform(svgElement) {
    return getSVGElement(svgElement).createSVGTransform();
}

function getSVGElement(svgElement) {
    return document.getElementById(svgElement);
}

function svg_wicket_call(callbackUrl, refId) {
    // alert("Calling back to " + callbackUrl + " with " + refId)
    var wcall = wicketAjaxGet(callbackUrl + '&refId='+refId, function() { }, function() { });
}
