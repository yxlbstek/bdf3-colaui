(function () {
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

		model.describe("users", {
	        provider: {
	            url: "./api/role/loadNotAllotUser",
	            pageSize: 10,
	            beforeSend: function (self, arg) {
	                var roleId = model.get("roles").current.get("id");
	                var searchKey = $("#leftSearch").val();
	                // 使用encodeURI() 为了解决GET下传递中文出现的乱码
	                arg.options.data.roleId = encodeURI(roleId);
	                arg.options.data.searchKey = searchKey;
	            }
	        }
	    });

	    model.describe("roleUsers", {
	        provider: {
	            url: "./api/role/loadIsAllotUser",
	            pageSize: 10,
	            beforeSend: function (self, arg) {
	                var roleId = model.get("roles").current.get("id");
	                var searchKey = $("#rightSearch").val();
	                arg.options.data.roleId = encodeURI(roleId);
	                arg.options.data.searchKey = searchKey;
	            }
	        }
	    });
		
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

	    model.widgetConfig({
	        roleTable: {
	            $type: "table",
	            bind: "role in roles",
	            showHeader: true,
	            changeCurrentItem: true,
	            highlightCurrentItem: true,
	            currentPageOnly: true,
	            itemClick: function (self, arg) {
	                model.flush("users");
	                model.flush("roleUsers");
	            }
	        }
	    });
		
		$("[tag='contentContainer']").attr("tag","");
		$(".ui.label.basic").transition({
		    animation : 'jiggle',
		    duration  : 800,
		    interval  : 1000
		 });
	})
}).call(this);

