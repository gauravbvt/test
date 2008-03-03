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
        });

        tree.getSelected = function() {
            return tree.find('li:has(a.selected)').attr('id');
            
        };
    }


    $.channels.modeler.scope = function(perspective) {
        var scope = $('#' + perspective + ' .scope');
        var buttonSet = scope.find('.toggle');
        var treeSet = scope.find('.treeSet');

        scope.tree = {};

        scope.tree.add = function(name, queryUrl, queryFunction) {
            buttonSet.append("<li id='" + name + "'>" + name +"</li>");
            treeSet.append("<div class='tree'><ul id='" + name + "' class='root'></ul></div>");

            initTree(treeSet.find('.tree #' + name),queryUrl, queryFunction);

            buttonSet.find("#" + name).click(function(event) {
                scope.tree.select(name);
            });

            treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", 'none');

        };

        scope.tree.select = function(name) {
            buttonSet.find("li").removeClass("selected");
            buttonSet.find("#" + name).addClass("selected");
            treeSet.find("div.tree:has(ul.root)").css("display", "none");
            scope.tree.selected = treeSet.find("div.tree:has(ul[id='" + name + "'])");
            scope.tree.selected.css("display", '');
        };

        return scope;
    }

})(jQuery);