$(document).ready( function() {
    var scope = $.channels.modeler.scope('home');

    scope.tree.add('Topics', "/modeler/command/refreshScope",
            function($tree, id) {
                return {"root" : id};
            });
    scope.tree.add('Index', "/modeler/command/refreshScope",
            function($tree, id) {
                return {"root" : id};
            });
    scope.tree.select('Topics');
});