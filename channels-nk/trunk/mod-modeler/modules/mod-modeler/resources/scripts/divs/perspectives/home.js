$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=home');
    channels.modeler.finder.initialize('home');
});