$(document).ready( function() {
    var scope = $.channels.modeler.scope.initialize('home');

    scope.tree.add({
        name: 'Topics',
        url: "/modeler/command/refreshScope",
        query:
            function($tree, id) {
                return {"root" : id};
            },
        nodeAction:
            function ($tree, node) {
                alert($tree.attr('id') + ' ' + node.attr('id'));
            }
    });
    scope.tree.add({
        name: 'Index',
        url: "/modeler/refreshScope",
        query:
            function($tree, id) {
                return {"root" : id};
            },
        nodeAction:
            function ($tree, node) {
                alert($tree.attr('id') + ' ' + node.attr('id'));
            }
    });
    scope.tree.Topics.select();
});