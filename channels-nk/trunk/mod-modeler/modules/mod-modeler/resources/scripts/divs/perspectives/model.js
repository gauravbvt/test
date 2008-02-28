$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=model');
    var finder = $.channels.modeler.finder('model');
    finder.grid.setup();});