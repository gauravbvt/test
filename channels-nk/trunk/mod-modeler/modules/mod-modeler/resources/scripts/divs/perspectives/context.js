$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=context');
    //channels.modeler.scope.initialize('context');
    channels.modeler.finder.initialize('context');
});