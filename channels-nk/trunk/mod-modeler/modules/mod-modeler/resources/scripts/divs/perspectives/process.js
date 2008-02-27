$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=process');
    channels.modeler.finder.initialize('process');
});