$(document).ready( function() {
    var scope = $.channels.modeler.scope('scenario');
    scope.tree.add('Scope');
    scope.tree.add('Causality');
    scope.tree.add('Information');
    scope.tree.select('Scope');

    var finder = $.channels.modeler.finder('scenario');
    finder.grid.setup();
});