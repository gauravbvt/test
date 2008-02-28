$(document).ready( function() {
    var scope = $.channels.modeler.scope('home');

    scope.tree.add('Topics', "/modeler/command/refreshScope",
            function(node) {
                return {"root" : node.id};
            });
    scope.tree.add('Index', "/modeler/command/refreshScope",
            function(node) {
                return {"root" : node.id};
            });
    scope.tree.select('Topics');
});