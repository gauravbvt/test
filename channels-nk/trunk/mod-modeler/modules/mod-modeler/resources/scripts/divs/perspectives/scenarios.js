$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=scenarios');
    channels.modeler.finder.initialize('scenarios');
});