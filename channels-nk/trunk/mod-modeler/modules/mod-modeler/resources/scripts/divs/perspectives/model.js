$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=home');
//    channels.modeler.scope.addTree('home', '1');
//    channels.modeler.scope.addTree('home', '2');
//    channels.modeler.scope.select('home', '1');
//    $.channels.modeler.initScope('home');
    var scope = $.channels.modeler.scope('model');
////    scope.setup('home');
    scope.tree.add('Taxonomy');
    scope.tree.select('Taxonomy');
    var finder = $.channels.modeler.finder('model');
    finder.grid.setup();});