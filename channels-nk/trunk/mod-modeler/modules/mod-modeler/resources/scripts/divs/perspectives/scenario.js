$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=scenario');
    var finder = $.channels.modeler.finder('scenario');
    finder.grid.setup();});