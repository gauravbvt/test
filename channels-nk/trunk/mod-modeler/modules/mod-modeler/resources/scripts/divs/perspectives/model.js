$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=model');
    channels.modeler.finder.initialize('model');
});