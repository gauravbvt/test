$(document).ready( function() {
//    $.get('/modeler/command/refreshScope?target=home');
//    channels.modeler.scope.addTree('home', '1');
//    channels.modeler.scope.addTree('home', '2');
//    channels.modeler.scope.select('home', '1');
//    $.channels.modeler.initScope('home');
    var scope = $.channels.modeler.scope.initialize('model');
////    scope.setup('home');
    var taxonomy = scope.tree.add({name:'Taxonomy'});
    taxonomy.select();
    var finder = $.channels.modeler.finder('model');
    finder.grid.setup();});