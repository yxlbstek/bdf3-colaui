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
		add: function() {
            var currentRole = model.get("roles").current;
            if (currentRole) {
               var currentUser = model.get("users").current;
                if (currentUser) {
                    $.ajax({
                        url: "./api/role/addRoleUser",
                        data: {
                            "roleId": currentRole.get("id"),
                            "actorId": currentUser.get("username")
                        },
                        type: "POST",
                        success: function() {
                            //model.flush("users");
                            currentUser.remove();
                            model.get("roleUsers").insert(currentUser.toJSON());
                            cola.NotifyTipManager.success({
                                message: "消息提示",
                                description: "保存成功!",
                                showDuration: 3000
                            });
                        }
                    });
                } else {
                    cola.alert("请先添加用户！", {
                        level: cola.MessageBox.level.WARNING
                    })
                }
            } else {
                cola.alert("请先添加角色！", {
                    level: cola.MessageBox.level.WARNING
                })
            }
        },

        remove: function () {
            var currentRoleUser = model.get("roleUsers").current;
            if (currentRoleUser) {
                var currentRole = model.get("roles").current;
                $.ajax({
                    url: "./api/role/removeRoleUser",
                    data: {
                        "roleId": currentRole.get("id"),
                        "actorId": currentRoleUser.get("username")
                    },
                    type: "POST",
                    success: function() {
                        //model.flush("users");
                        currentRoleUser.remove();
                        model.get("users").insert(currentRoleUser.toJSON());
                        cola.NotifyTipManager.success({
                            message: "消息提示",
                            description: "移除成功!",
                            showDuration: 3000
                        });
                    }
                });
            } else {
                cola.alert("没有需要移除的用户！", {
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

        searchLeftUser: function () {
            var keyCode = window.event.keyCode;
            if (keyCode == 13) {
                model.flush("users");
            }
        },

        searchRightUser: function () {
            var keyCode = window.event.keyCode;
            if (keyCode == 13) {
                model.flush("roleUsers");
            }
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
