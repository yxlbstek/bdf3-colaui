cola(function(model) {
	model.describe("users", {
        dataType: {
            name: "User",
            properties: {
                username: {
                    validators: [
                        "required",
                        new cola.AjaxValidator({
                            method: "GET",
                            name: "usernameAjaxValidator",
                            message: "用户名已存在",
                            disabled: true,
                            data: {
                                username: ":data"
                            },
                            url: "./api/user/exist"
                        })
                    ]
                },
                password: {
                    validators: [
                        "required", {
                            $type: "length",
                            min: 6,
                            max: 120
                        }
                    ]
                },
                nickname: {
                    validators: [
                        "required", {
                            $type: "length",
                            max: 10
                        }
                    ]
                }
            }
        },
        provider: {
            url: "./api/user/load",
            pageSize: 10,
            parameter: {
            	searchKey: "{{searchKey}}"
            }
        }
    });
	
	model.describe("editItem", "User");
	
	model.action({
		add: function () {
			cola.widget("username").set("readOnly", false);
            cola.widget("password").set("readOnly", false);
            model.definition("usernameAjaxValidator").set("disabled", false);
			model.set("editItem", {
                editType: "新增",
                accountNonExpired: true,
                accountNonLocked: true,
                credentialsNonExpired: true,
                enabled: true
            });
            $("#msgModal").modal('show');
		},
		
		save: function() {
            var entity = model.get("editItem");
            var result = entity.validate();
            if (result) {
            	var isNew = entity.get("editType") === "新增";
            	var path = isNew ? "./api/user/add" : "./api/user/modify";
                var data = entity.toJSON();
                $.ajax({                
                    data: JSON.stringify(data),
                    type: isNew ? "POST" : "PUT",
                    contentType : "application/json",
                    url: path,
                    success: function() {
                        model.get("users").insert(data);
                        $("#msgModal").modal('hide');
                        model.flush("users");
                        cola.NotifyTipManager.success({
                            message: "保存成功！！！",
                            showDuration: 1500,
                        });
                    }
                });
            }
        },

		cancel: function() {
            model.set("editItem", {});
            $("#msgModal").modal('hide');
        },
		
        removeTip: function (item) {
        	model.set("editItem", item);
        	$("#delModal").modal('show');
        },
        
        remove: function () {
	        var item = model.get("editItem");
	        if(item){
	            $.ajax({
	                url: "./api/user/remove",
	                data: {
	                    "username" : item.get("username")
	                },
	                type: "POST",
	                success: function() {
	                    $("#delModal").modal('hide');
	                    item.remove();
	                    //model.flush("users");
                        cola.NotifyTipManager.success({
                            message: "删除成功！！！",
                            showDuration: 1500,
                        });
	                }
	            });
	        }

        },
        
        cancelRemove: function () {
        	$("#delModal").modal('hide');
        },
        
        modify: function(item) {
            cola.widget("username").set("readOnly", true);
            cola.widget("password").set("readOnly", true);
            model.definition("usernameAjaxValidator").set("disabled", true);
            model.set("editItem", item.toJSON());
            $("#msgModal").modal('show');
        },

        addManager: function() {
            var entity = model.get("users").current;
            if (entity) {
                var isManager = entity.get("administrator");
                if (isManager) {
                    cola.alert("该用户已是管理员！", {
                        level: cola.MessageBox.level.WARNING
                    })
                } else {
                    entity.set("administrator", true);
                    $.ajax({
                        data: JSON.stringify(entity.toJSON()),
                        type: "PUT",
                        contentType : "application/json",
                        url: "./api/user/modify",
                        success: function() {
                            cola.NotifyTipManager.success({
                                message: "保存成功！！！",
                                showDuration: 1500,
                            });
                        }
                    });
                }
            }
        },

        cancelManager: function() {
            var entity = model.get("users").current;
            if (entity) {
                var isManager = entity.get("administrator");
                if (!isManager) {
                    cola.alert("该用户不是管理员，无需取消！", {
                        level: cola.MessageBox.level.WARNING
                    })
                } else {
                    entity.set("administrator", false);
                    $.ajax({
                        data: JSON.stringify(entity.toJSON()),
                        type: "PUT",
                        contentType : "application/json",
                        url: "./api/user/modify",
                        success: function() {
                            cola.NotifyTipManager.success({
                                message: "保存成功！！！",
                                showDuration: 1500,
                            });
                        }
                    });
                }
            }
        },

        resetPassword: function(item) {
            $.ajax({
                data: JSON.stringify(item.toJSON()),
                type: "PUT",
                contentType : "application/json",
                url: "./api/user/resetPassword",
                success: function() {
                    cola.alert("密码已重置，新密码为：123456", {
                        level: cola.MessageBox.level.WARNING
                    })
                }
            });
        },
        
		search: function () {
			var keyCode = window.event.keyCode;
			if (keyCode == 13) {
				model.flush("users");
			}			
		}    
	});
	
	model.set("sexs", [{
        name: "男"
    }, {
        name: "女"
    } ]);
	
	model.widgetConfig({
		birthdayPicker : {
            $type: "datePicker",
            bind: "editItem.birthday"
        },
        sexDropDown: {
            $type: "dropdown",
            "class": "error",
            openMode: "drop",
            items: "{{sex in sexs}}",
            valueProperty: "name",
            bind: "editItem.sex"
        }
	});
})
