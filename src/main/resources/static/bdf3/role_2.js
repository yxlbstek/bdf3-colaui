cola(function(model) {
	model.describe("roles", {
        dataType: {
            name: "Role",
            properties: {
                name: {
                    validators: [
                        "required",
                        new cola.AjaxValidator({
                            method: "GET",
                            name: "nameAjaxValidator",
                            message: "用户名已存在",
                            disabled: true,
                            data: {
                                name: ":data"
                            },
                            url: "./api/role/exist"
                        })
                    ]
                },
                description: {
                    validators: [{
                            $type: "length",
                            min: 0,
                            max: 60
                        }
                    ]
                }
            }
        },
        provider: {
            url: "./api/role/load",
            pageSize: 10,
            parameter: {
            	searchKey: "{{searchKey}}"
            }
        }
    });
	
	model.describe("editItem", "Role");
	
	model.action({
		add: function () {
            model.definition("nameAjaxValidator").set("disabled", false);
			model.set("editItem", {
                editType: "新增"
            });
            $("#msgModal").modal('show');
		},
		
		save: function() {
            var entity = model.get("editItem");
            var result = entity.validate();
            if (result) {
            	var isNew = entity.get("editType") === "新增";
            	var path = isNew ? "./api/role/add" : "./api/role/modify";
                var data = entity.toJSON();
                $.ajax({                
                    data: JSON.stringify(data),
                    type: isNew ? "POST" : "PUT",
                    contentType : "application/json",
                    url: path,
                    success: function() {
                        model.get("roles").insert(data);
                        $("#msgModal").modal('hide');
                        model.flush("roles");
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
	                url: "./api/role/remove",
	                data: {
	                    "id" : item.get("id")
	                },
	                type: "POST",
	                success: function() {
	                    $("#delModal").modal('hide');
	                    item.remove();
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
            model.definition("nameAjaxValidator").set("disabled", true);
            model.set("editItem", item.toJSON());
            $("#msgModal").modal('show');
        },
        
		search: function () {
			var keyCode = window.event.keyCode;
			if (keyCode == 13) {
				model.flush("roles");
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
