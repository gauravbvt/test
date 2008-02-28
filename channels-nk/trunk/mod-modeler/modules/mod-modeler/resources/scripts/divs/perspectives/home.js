$(document).ready( function() {
    var scope = $.channels.modeler.scope('home');

    scope.tree.add('1', "/modeler/command/refreshScope",
            function(node) {
                return {"root" : node.id};
            });
    scope.tree.add('2', "/modeler/command/refreshScope",
            function(node) {
                return {"root" : node.id};
            });
    scope.tree.select('1');
    
    var finder = $.channels.modeler.finder('home');
    finder.grid.setup();
});