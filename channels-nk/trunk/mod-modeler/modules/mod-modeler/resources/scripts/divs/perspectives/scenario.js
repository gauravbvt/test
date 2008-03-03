$(document).ready( function() {
    var scope = $.channels.modeler.scope('scenario');
    scope.tree.add('Scope');
    scope.tree.add('Causality');
    scope.tree.add('Information');
    scope.tree.select('Scope');

    var finder = $.channels.modeler.finder('scenario');
    finder.grid.setup();

    $( "#scenario .perspective_main_right ul.toggle li" ).click( function(){
        $( "#scenario .perspective_main_right ul.toggle li" ).removeClass( "selected" );
        $( this ).addClass( "selected" );
        var id = $( this ).attr( "id" ).substr( 3 );
        $( "#scenario div.scenario_view" ).css( "display", "none" );
        $( "#sv" + id ).css( "display", "" );
    });
});