cola(function(model) {
    model.describe("roles", {
        provider: {
            url: "./api/role/load",
            pageSize: 10,
            parameter: {
                searchKey: "{{searchRoleKey}}"
            }
        }
    });

    model.get("roles", "sync");

    model.dataType({
        name: "Url",
        properties: {
            urls: {
                dataType: "Url",
                provider: {
                    name: "xxx",
                    url: "./api/url/loadSubByRoleId",
                    parameter: {
                        parentId: "{{@id}}",
                        roleId: model.get("roles").current.get("id")
                    },
                    // beforeSend: function (self, arg) {
                    //     var provider = model.definition("xxx");
                    //     provider.get("parameter");
                    //     arg.options.data.roleId = encodeURI(model.get("roles").current.get("id"));
                    //     arg.options.data.parentId = self.parameter.parentId;
                    // }
                }
            }
        }
    });
    model.describe("urls", {
        dataType: "Url",
        provider: {
            url: "./api/url/loadTopByRoleId",
            parameter: {
                roleId: model.get("roles").current.get("id")
            }
        }
    });


    //model.get("urls", "sync");
	
	model.action({
        refresh: function() {
            model.flush("urls");
        },

        selectedNodes: function (nodes, checkedNode, unCheckNode) {
             $.each(nodes, function () {
                var node = this;
                if (node.get("checked")) {
                    checkedNode.push(node.get("data.id"));
                } else {
                    unCheckNode.push(node.get("data.id"));
                }
                var children = node.get("children");
                if (children) {
                    model.action.selectedNodes(children, checkedNode, unCheckNode);
                }
            });
        },

        save: function () {
            var nodes = cola.widget("urlTree").getItems();
            var checkedNode = []; // 选中的节点
            var noCheckNode = []; // 末选中的节点
            model.action.selectedNodes(nodes, checkedNode, noCheckNode);
            var currentRole = model.get("roles").current;
            if (currentRole) {
                var roleId = currentRole.get("id");
                model.set("permission", {
                    roleId: roleId,
                    urlIds: checkedNode,
                    excludeUrlIds: noCheckNode
                });
                var data = model.get("permission").toJSON();
                if (checkedNode.length > 0) {
                    $.ajax("./api/permission/save", {
                        type: "POST",
                        data: JSON.stringify(data),
                        contentType: "application/json; charset=utf-8",
                        success: function() {

                        }
                    });
                } else {
                    cola.alert("请选择树节点！");
                }
            } else {
                cola.alert("请先添加角色！", {
                    level: cola.MessageBox.level.WARNING
                })
            }

        },

        searchRole: function () {
			var keyCode = window.event.keyCode;
			if (keyCode == 13) {
				model.flush("roles");
			}
		}
	});

    model.widgetConfig({
        roleTable: {
            $type: "table",
            bind: "role in roles",
            showHeader: true,
            changeCurrentItem: true,
            highlightCurrentItem: true,
            currentPageOnly: true,
            itemClick: function (self, arg) {
                var provider = model.definition("xxx");
                provider.set("parameter", {
                    roleId: arg.item.get("id")
                });
                model.flush("urls");
            }
        },
        urlTree: {
            $type: "tree",
            //lazyRenderChildNodes: false,
            //autoExpand: true,
            //autoCollapse: true,
            highlightCurrentItem: true,
            bind: {
                expression: "url in urls",
                textProperty: "name",
                checkedProperty: "navigable",
                child: {
                    recursive: true,
                    textProperty: "name",
                    checkedProperty: "navigable",
                    expression: "url in url.urls"
                }
            },
            height: "100%",
        }
    });
	
	$("[tag='contentContainer']").attr("tag","");
	$(".ui.label.basic").transition({
	    animation : 'jiggle',
	    duration  : 800,
	    interval  : 1000
	 });

})
