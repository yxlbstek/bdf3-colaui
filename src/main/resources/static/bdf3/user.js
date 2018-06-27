(function () {
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
	                            message: "消息提示",
	                            description: "保存成功!",
	                            showDuration: 3000
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
	                            message: "消息提示",
	                            description: "删除成功!",
	                            showDuration: 3000
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
	                    cola.NotifyTipManager.warning({
                            message: "消息提示",
                            description: "该用户已是管理员！",
                            showDuration: 3000
                        });
	                } else {
	                    entity.set("administrator", true);
	                    $.ajax({
	                        data: JSON.stringify(entity.toJSON()),
	                        type: "PUT",
	                        contentType : "application/json",
	                        url: "./api/user/modify",
	                        success: function() {
	                            cola.NotifyTipManager.success({
	                                message: "消息提示",
	                                description: "保存成功!",
	                                showDuration: 3000
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
	                    cola.NotifyTipManager.warning({
                            message: "消息提示",
                            description: "该用户不是管理员，无需取消！",
                            showDuration: 3000
                        });
	                } else {
	                    entity.set("administrator", false);
	                    $.ajax({
	                        data: JSON.stringify(entity.toJSON()),
	                        type: "PUT",
	                        contentType : "application/json",
	                        url: "./api/user/modify",
	                        success: function() {
	                            cola.NotifyTipManager.success({
	                                message: "消息提示",
	                                description: "保存成功!",
	                                showDuration: 3000
	                            });
	                        }
	                    });
	                }
	            }
	        },

	        resetPassword: function() {
	        	$("#resetPasswordModal").modal('show');
	        },
	        
	        confirmResetPassword: function() {
	        	var entity = model.get("users").current;
	        	$.ajax({
	                data: JSON.stringify(entity.toJSON()),
	                type: "PUT",
	                contentType : "application/json",
	                url: "./api/user/resetPassword",
	                success: function() {
	                	$("#resetPasswordModal").modal('hide');
	                    cola.NotifyTipManager.success({
                            message: "消息提示",
                            description: "用户 【" + entity.get("nickname") + "】密码已重置，新密码为：123456",
                            showDuration: 5000
                        });
	                }
	            });
	        },
	        
	        cancelresetPassword: function() {
	        	$("#resetPasswordModal").modal('hide');
	        },
	        
			search: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("users");
				}			
			}    
		});

		var path = "./user";
		App.resetComponentAuth(path);

		/*解决页面刚渲染时页面结构错乱*/
		$("[tag='contentContainer']").attr("tag","");

	})
}).call(this);

