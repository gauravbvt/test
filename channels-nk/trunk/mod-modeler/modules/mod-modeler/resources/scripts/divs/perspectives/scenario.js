$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=scenario');
    channels.modeler.finder.initialize('scenario');
});