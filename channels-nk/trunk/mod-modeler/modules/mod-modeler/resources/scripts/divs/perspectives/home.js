$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=home');
//    channels.modeler.scope.addTree('home', '1');
//    channels.modeler.scope.addTree('home', '2');
//    channels.modeler.scope.select('home', '1');
//    $.channels.modeler.initScope('home');
    var scope = $.channels.modeler.scope('home');
////    scope.setup('home');
    scope.tree.add('1');
    scope.tree.add('2');
    scope.tree.select('1');
    channels.modeler.finder.initialize('home');
});