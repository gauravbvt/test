$(document).ready( function() {
    var scope = $.channels.modeler.scope.initialize('scenario');
    var scopetree = scope.tree.add({name: 'Scope'});
    var causality = scope.tree.add({name: 'Causality'});
    var information = scope.tree.add({name: 'Information'});
    scopetree.select();

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