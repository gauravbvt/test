$(document).ready( function() {
    var scope = $.channels.modeler.scope('process');
    scope.tree.add('Participation');
    scope.tree.add('Journal');
    scope.tree.add('Todo');
    scope.tree.select('Participation');

    var finder = $.channels.modeler.finder('process');
    finder.grid.setup();
});