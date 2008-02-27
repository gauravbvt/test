channels_NS("channels.modeler.constants");

$(document).ready( function () {
    console.info("channels.modeler.initialize");
    channels.modeler.initialize();
})

channels.modeler.constants['home'] = "Modeler";
channels.modeler.constants['environment'] = "Environment";
channels.modeler.constants['model'] = "Model";
channels.modeler.constants['scenario'] = "Scenario";
channels.modeler.constants['process'] = "Process";


channels.modeler.initialize = function() {
   channels.modeler.switchToPerspective('home');
}

// User switches to a perspective
channels.modeler.switchToPerspective = function(perspective_code)  {
    // Bring perspective on top by giving it z-index greater than those of the other perspectives
    perspective_id = '#'+perspective_code;

    $(perspective_id).css('display', '');
    $('.perspective_main').not(perspective_id).css('display', 'none');
    $('#perspectives_title').html(channels.modeler.constants[perspective_code]);

    // Set selected style on swither button
    if ( channels.modeler.button != null )
        channels.modeler.button.className = "fisheyeItem";
    channels.modeler.button = document.getElementById( perspective_code + '_button' );
    channels.modeler.button.className = "fisheyeItem selected";
}