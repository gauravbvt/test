$(document).ready( function() {
    var scope = $.channels.modeler.scope.initialize('process');
    var participation = scope.tree.add({name: 'Participation'});
    var journal = scope.tree.add({name: 'Journal'});
    var todo = scope.tree.add({name: 'Todo'});
    participation.select();

    var finder = $.channels.modeler.finder('process');
    finder.grid.setup();
});