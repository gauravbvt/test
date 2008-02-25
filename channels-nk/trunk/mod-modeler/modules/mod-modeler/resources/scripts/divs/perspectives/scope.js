channels_NS("channels.modeler.scope");

channels.modeler.scope.initialize = function (scope) {
    var tree = $('#' + scope).find('#scope-tree');

    tree.find('span').each(function() {
        var span = $(this);
        span.wrap('<a href="#"></a>');


        span.parent('a').click(function (event) {
            //$.get('/modeler/command/refreshFinder?target=' + scope + '&id=' + span.id + '&type=' + span.attr('class'));
            //alert('/modeler/command/refreshFinder?target=' + scope + '&id=' + span.attr('id') + '&type=' + span.attr('class'));
            tree.find('a').removeClass('selected');
            $(this).addClass('selected');
        });
    });
    tree.treeview({
        //persist : "location"

    });

    


}
