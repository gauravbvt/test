$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=process');
    var finder = $.channels.modeler.finder('process');
    finder.grid.setup();});