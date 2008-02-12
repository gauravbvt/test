channels_NS("channels.modeler.constants");

$(document).ready( function () {
    console.info("channels.modeler.initialize");
    channels.modeler.initialize();
})

channels.modeler.constants['home'] = "Home";
channels.modeler.constants['context'] = "Context";
channels.modeler.constants['model'] = "Model";
channels.modeler.constants['scenarios'] = "Scenarios";
channels.modeler.constants['q_and_a_etc'] = "Questions, notes etc.";

channels.modeler.initialize = function() {
   channels.modeler.switchToPerspective('home');
}

// User switches to a perspective
channels.modeler.switchToPerspective = function(perspective_code)  {
    // Bring perspective on top by giving it z-index greater than those of the other perspectives
    console.info("switch to perspective", perspective_code);
    perspective_id = '#'+perspective_code;
    //$(perspective_id).css('z-index', 100);
    $(perspective_id).css('visibility', '');
    //$('.perspective_main').not(perspective_id).css('z-index', 0);
    $('.perspective_main').not(perspective_id).css('visibility', 'hidden');
    // Change perspective title
    $('#perspectives_title').html(channels.modeler.constants[perspective_code]);
}