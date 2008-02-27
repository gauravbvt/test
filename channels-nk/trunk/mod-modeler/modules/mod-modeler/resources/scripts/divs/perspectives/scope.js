channels_NS("channels.modeler.scope");

(function($) {
//channels.modeler.scope.initialize = function (path) {
//    var tree = $(path);
//
//    tree.find('span').each(function() {
//        var span = $(this);
//        span.wrap('<a href="#"></a>');
//
//
//        span.parent('a').click(function (event) {
//            //$.get('/modeler/command/refreshFinder?target=' + scope + '&id=' + span.id + '&type=' + span.attr('class'));
//            //alert('/modeler/command/refreshFinder?target=' + scope + '&id=' + span.attr('id') + '&type=' + span.attr('class'));
//            tree.find('a').removeClass('selected');
//            $(this).addClass('selected');
//        });
//    });
//    tree.treeview({
//        //persist : "location"
//
//    });
//}
//
//
//channels.modeler.scope.select = function(scope, name) {
//    var buttonSet = $('#' + scope + ' .toggle');
//    var treeSet = $('#' + scope + ' .treeSet');
//    buttonSet.find(".button").removeClass("selected");
//    buttonSet.find("#" + name).addClass("selected");
//    treeSet.find("div.tree:has(ul.root)").css("display", "none");
//    treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", '');
//
//}
//
//channels.modeler.scope.addTree = function (scope, name) {
//    var buttonSet = $('#' + scope + ' .toggle');
//    var treeSet = $('#' + scope + ' .treeSet');
//
//    buttonSet.append("<div class='button' id='" + name + "'>" + name +"</div>");
//    treeSet.append("<div class='tree'><ul id='" + name + "' class='root'><li><span class='organization'>" + name + "</span></li></ul></div>");
//
//    channels.modeler.scope.initialize('#' + scope + ' .tree #' + name);
//
//    buttonSet.find("#" + name).click(function(event) {
//        channels.modeler.scope.select(scope, name);
//    });
//
//    treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", 'none');
//
//
//
//}

    $.channels = {};
    $.channels.modeler={};


    var initTree = function (tree) {

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

//    $.channels.modeler.scope = function(perspective) {
//        return ;
//    }

    $.channels.modeler.scope = function(perspective) {
        var scope = $('#' + perspective + ' .scope');
        var buttonSet = scope.find('.toggle');
        var treeSet = scope.find('.treeSet');

        scope.tree = {};

        scope.tree.add = function(name) {
                buttonSet.append("<div class='button' id='" + name + "'>" + name +"</div>");
                treeSet.append("<div class='tree'><ul id='" + name + "' class='root'><li><span class='organization'>" + name + "</span></li></ul></div>");

                initTree(treeSet.find('.tree #' + name));

                buttonSet.find("#" + name).click(function(event) {
                    scope.tree.select(name);
                });

                treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", 'none');

            };

        scope.tree.select = function(name) {
                buttonSet.find(".button").removeClass("selected");
                buttonSet.find("#" + name).addClass("selected");
                treeSet.find("div.tree:has(ul.root)").css("display", "none");
                treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", '');
            };

        return scope;
    }

})(jQuery);