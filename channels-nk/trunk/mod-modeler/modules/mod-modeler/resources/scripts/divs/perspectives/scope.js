(function($) {

    var initTree = function (tree, queryUrl, queryFunction) {

        tree.find('span').each(function() {
            var span = $(this);
            span.wrap('<a href="#"></a>');


            span.parent('a').click(function (event) {
                tree.find('a').removeClass('selected');
                $(this).addClass('selected');
            });
        });
        tree.treeview({
            "url" : queryUrl,
            "query" : queryFunction
            //persist : "location"

        });
    }


    $.channels.modeler.scope = function(perspective) {
        var scope = $('#' + perspective + ' .scope');
        var buttonSet = scope.find('.toggle');
        var treeSet = scope.find('.treeSet');

        scope.tree = {};

        scope.tree.add = function(name, queryUrl, queryFunction) {
                buttonSet.append("<li class='button' id='" + name + "'>" + name +"</li>");
                treeSet.append("<div class='tree'><ul id='" + name + "' class='root'></ul></div>");

                initTree(treeSet.find('.tree #' + name),queryUrl, queryFunction);

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