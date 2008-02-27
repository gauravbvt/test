$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=environment');
    //channels.modeler.scope.initialize('environment');
    channels.modeler.finder.initialize('environment');
});