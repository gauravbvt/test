$(document).ready( function() {
    var scope = $.channels.modeler.scope.initialize('environment');
    var org = scope.tree.add({name:'Organizations'});
    var loc = scope.tree.add({name:'Locations'});
    org.select();
    
    var finder = $.channels.modeler.finder('environment');
    finder.grid.setup();
});