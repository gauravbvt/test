$(document).ready( function() {
    var scope = $.channels.modeler.scope('environment');
    scope.tree.add('Organizations');
    scope.tree.add('Locations');
    scope.tree.select('Organizations');

    var finder = $.channels.modeler.finder('environment');
    finder.grid.setup();
});