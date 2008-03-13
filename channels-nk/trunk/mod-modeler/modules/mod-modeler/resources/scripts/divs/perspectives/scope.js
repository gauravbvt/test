/*
 * Parts Derived From:
 *
 * Async Treeview 0.1 - Lazy-loading extension for Treeview
 *
 * http://bassistance.de/jquery-plugins/jquery-plugin-treeview/
 *
 * Copyright (c) 2007 Jšrn Zaefferer
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: $Id$
 *
 */

(function($) {


    var initTree = function (tree, queryUrl, queryFunction, nodeAction) {
        tree.treeview({
            "url" : queryUrl,
            "query" : queryFunction,
            "nodeAction" : nodeAction
        });
        tree.getSelectedId = function() {
            return tree.find('li:has(a.selected)').attr('id');
            
        };
    }


    $.channels.modeler.scope =  {

        initialize : function(perspective) {

            var scope = $('#' + perspective + ' .scope');
            var buttonSet = scope.find('.toggle');
            var treeSet = scope.find('.treeSet');
            scope.id=perspective;
            scope.tree = {};

            scope.tree.add = function(settings) {
                var name = settings.name;
                var queryUrl = settings.url;
                var queryFunction = settings.query;
                var nodeAction = settings.nodeAction;

                buttonSet.append("<li id='" + name + "'>" + name +"</li>");
                treeSet.append("<div class='tree' id='" + name + "'><ul id='" + name + "' class='root'></ul></div>");
                var tree =treeSet.find("div.tree:has(ul.root[id='" + name + "'])");
                initTree(tree,queryUrl, queryFunction, nodeAction);

                buttonSet.find("#" + name).click(function(event) {
                    tree.select(name);
                });
                tree.css("display", "none");
                // treeSet.find("div.tree:has(ul[id='" + name + "'])").css("display", 'none');
                scope.tree[name] = tree;
                tree.select = function() {
                    buttonSet.find("li").removeClass("selected");
                    buttonSet.find("#" + name).addClass("selected");
                    treeSet.find("div.tree:has(ul.root)").css("display", "none");
                    scope.tree.current = treeSet.find("div.tree:has(ul[id='" + name + "'])");
                    scope.tree.current.css("display", '');
                };
                tree.actions = settings.actionMap || {};
                tree.id = name;
                return tree;
            };

            $.channels.modeler.scope[perspective] = scope;
            return scope;
        }
    }

    



    function load(settings, query, child, container) {
        $.getJSON(settings.url, query, function(response) {
            var $container = $(container);
            var treeId = $container.attr('id');
            var scopeId = $container.parents('div.perspective_main').attr('id');
            var scope = $.channels.modeler.scope[scopeId];
            var tree = scope.tree[treeId];

            function createNode(parent) {
                var current = $("<li/>").attr("id", this.id || "").html("<a href='#'><span>" + this.text + "</span></a>").appendTo(parent);
                current.find('a').click(function (event) {
                    $container.find('a').removeClass('selected');
                    $(this).addClass('selected');
                    if (settings.nodeAction) {
                        settings.nodeAction($container, current);
                    }
                });;

                if (this.classes) {
                    current.children("span").addClass(this.classes);
                }
                if (this.expanded) {
                    current.addClass("open");
                }
                if (this.hasChildren || this.children && this.children.length) {
                    var branch = $("<ul/>").appendTo(current);
                    if (this.hasChildren) {
                        current.addClass("hasChildren");
                        createNode.call({
                            text:"placeholder",
                            id:"placeholder",
                            children:[]
                        }, branch);
                    }
                    if (this.children && this.children.length) {
                        $.each(this.children, createNode, [branch])
                    }
                }
                if (this.actions) {
                    buildMenu(current, this.actions);
                }
            }

           function buildMenu(node, actions) {
                var id = node.attr('id');
                var menu = $("<div class='contextMenu'  id='" + id + "'><ul></ul></div>");
                var bindings = {};

                for (var i = 0 ; i < actions.length ; i++) {
                    var action = actions[i];
                    var li = $("<li id='" + action.name + "'></li>");
                    if (action.icon) {
                        li.append("<img src='" + action.icon + "' />");
                    }
                    //bindings[actions[i].name] = eval("$.channels.modeler.scope.action." + actions[i].name);
                    var actionFunc = tree.actions[action.name] || actionMap[action.name];
                    if (actionFunc != undefined) {
                        bindings[action.name] = function(t) {
                            actionFunc(node, tree, scope);
                        }
                    }


                    li.append(action.label);
                    menu.find("ul").append(li);

                }
                menulist = $container.parents('div.scope').find('div.menulist');
                menulist.append(menu);
                node.find('span').contextMenu(id, {
                    "menu" : menu,
                    "bindings" : bindings
                });

            }

            $.each(response, createNode, [child]);
            $container.treeview({add: child});
        });
    }

    var proxied = $.fn.treeview;
    $.fn.treeview = function(settings) {
        if (!settings.url) {
            return proxied.apply(this, arguments);
        }
        var container = this;
        load(settings, settings.query($(container), ''), this, container);
        var userToggle = settings.toggle;
        return proxied.call(this, $.extend({}, settings, {
            collapsed: true,
            toggle: function() {
                var $this = $(this);
                if ($this.hasClass("hasChildren")) {
                    var childList = $this.removeClass("hasChildren").find("ul");
                    childList.empty();
                    load(settings, settings.query($(container), $this.attr('id')),childList,  container);
                }
                if (userToggle) {
                    userToggle.apply(this, arguments);
                }
            }
        }));
    };

    var actionMap = {
        open : function(node, tree, scope) {
            alert('open' + scope.id + '>' + tree.id + '>' + node.attr('id'));
        },
        close : function(node, tree, scope) {
            alert('close' + scope.id + '>' + tree.id + '>' + node.attr('id'));
        }
    }


})(jQuery);