$(document).ready( function() {
    $.get('/modeler/command/refreshScope?target=q_and_a_etc');
    channels.modeler.finder.initialize('q_and_a_etc');
});