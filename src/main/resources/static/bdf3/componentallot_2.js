cola(function(model) {
    model.describe("roles", {
        provider: {
            url: "./api/role/load",
            pageSize: 10,
            parameter: {
                searchKey: "{{searchRoleKey}}"
            },
            complete: function () {
                if (!model.get("roleId")) {
                    model.set("roleId", model.get("roles").current.get("id"));
                }
            }
        }
    });

    setTimeout(function () {
        model.describe("urls", {
            provider: {
                url: "./api/url/loadTreeByRoleId/{{@roleId}}",
                complete: function () {
                    if (!model.get("urlId")) {
                        model.set("urlId", model.get("urls").current.get("id"));
                    };
                }
            }
        });
        model.flush("urls");
    }, 200);
    //model.get("roles", "sync")

    setTimeout(function () {
        model.describe("components", {
            provider: {
                url: "./api/component/loadByRoleId/{{@roleId}}/{{@urlId}}",
            }
        });
    }, 400);

	model.action({
		save: function() {
		    var roles = model.get("roles");
            var urls = model.get("urls");
            var components = model.get("components");
            if (roles.entityCount > 0) {
                if (urls.entityCount > 0) {
                    if (components.entityCount > 0) {
                        var roleId = roles.current.get("id");
                        var modifyComponentIds = [];
                        components.each(function(c) {
                            if (c.state == "MODIFIED") {
                                modifyComponentIds.push(c.get("id"));
                            }
                        });

                        model.set("permission", {
                            roleId: roleId,
                            componentIds: modifyComponentIds
                        });
                        var data = model.get("permission").toJSON();
                        $.ajax("./api/component/save", {
                            type: "POST",
                            data: JSON.stringify(data),
                            contentType: "application/json; charset=utf-8",
                            success: function() {
                                cola.NotifyTipManager.success({
                                    message: "消息提示",
                                    description: "保存成功!",
                                    showDuration: 3000
                                });
                            }
                        });
                    } else {
                        cola.alert("该页面没有添加可分配的组件！", {
                            level: cola.MessageBox.level.WARNING
                        })
                    }
                } else {
                    cola.alert("请先添加菜单及组件！", {
                        level: cola.MessageBox.level.WARNING
                    })
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
		},

        refresh: function() {
            model.flush("components");
        }

	});

    model.set("items", [
        {
            key: "Read",
            value: "只读"
        },
        {
            key: "ReadWrite",
            value: "可操作"
        }
    ]);

    model.widgetConfig({
        roleTable: {
            $type: "table",
            bind: "role in roles",
            showHeader: true,
            changeCurrentItem: true,
            highlightCurrentItem: true,
            currentPageOnly: true,
            itemClick: function (self, arg) {
                if (self.get("currentItem").get("id") != model.get("roleId")) {
                    model.set("roleId", self.get("currentItem").get("id"));
                    model.flush("urls");
                    model.flush("components");
                }
            }
        },
        componentTable: {
            $type: "table",
            bind: "component in components",
            showHeader: true,
            changeCurrentItem: true,
            highlightCurrentItem: true,
            currentPageOnly: true,
        },
        urlTree: {
            $type: "tree",
            lazyRenderChildNodes: false,
            highlightCurrentItem: true,
            bind: {
                expression: "url in urls",
                textProperty: "name",
                checkedProperty: "navigable",
                child: {
                    recursive: true,
                    textProperty: "name",
                    checkedProperty: "navigable",
                    expression: "url in url.children"
                }
            },
            itemClick: function (self, arg) {
                if (arg.item.get("data.id") != model.get("roleId")) {
                    model.set("urlId", arg.item.get("data.id"));
                    model.flush("components");
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
