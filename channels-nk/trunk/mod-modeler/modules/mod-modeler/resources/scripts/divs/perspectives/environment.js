$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=environment');
    //channels.modeler.scope.initialize('environment');
    var finder = $.channels.modeler.finder('environment');
    finder.grid.setup();
});