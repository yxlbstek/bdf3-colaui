(function () {
	cola(function(model) {
		model.describe("components", {
	        dataType: {
	            name: "Component",
	            properties: {
	                componentId: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 60
	                        }
	                    ]
	                },
	                name: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 60
	                        }
	                    ]
	                },
	                urlId: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 120
	                        }
	                    ]
	                },
	                path: {
	                    validators: [{
	                        $type: "length",
	                        min: 0,
	                        max: 120
	                    }]
	                },
	                urlName: {
	                    provider: {
	                        url: "./api/component/getUrlName",
	                        parameter: {
	                            urlId: "{{@urlId}}"
	                        }
	                    }
	                },
	                description: {
	                    validators: [{
	                        $type: "length",
	                        min: 0,
	                        max: 120
	                    }]
	                }
	            }
	        },
	        provider: {
	            url: "./api/component/load",
	            pageSize: 10,
	            parameter: {
	            	searchKey: "{{searchKey}}"
	            }
	        }
	    });

	    model.describe("urls", {
	        provider : "./api/url/loadAll",
	    });
		
		model.describe("editItem", "Component");
		
		model.action({
			add: function () {
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
	            	var path = isNew ? "./api/component/add" : "./api/component/modify";
	                var data = entity.toJSON();
	                $.ajax({                
	                    data: JSON.stringify(data),
	                    type: isNew ? "POST" : "PUT",
	                    contentType : "application/json",
	                    url: path,
	                    success: function() {
	                        model.get("components").insert(data);
	                        $("#msgModal").modal('hide');
	                        model.flush("components");
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
		                url: "./api/component/remove",
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
	            model.set("editItem", item.toJSON());
	            $("#msgModal").modal('show');
	        },
	        
			search: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("components");
				}			
			}    
		});

	    model.widgetConfig({
	        urlDropDown: {
	            $type: "dropdown",
	            openMode: "drop",
	            items: "{{url in urls}}",
	            valueProperty: "name",
	            bind : "editItem.urlName",
	            post: function (self, arg) {
	                var currentComponent = model.get("editItem");
	                var currentUrl = self.get("currentItem");
	                currentComponent.set("urlId", currentUrl.get("id"));
	                currentComponent.set("path", currentUrl.get("path"));
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

