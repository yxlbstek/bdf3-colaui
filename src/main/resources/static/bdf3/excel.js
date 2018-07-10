(function () {
	cola(function(model) {
	    model.describe("importerSolutions", {
	    	dataType: {
	            name: "ImporterSolution",
	            properties: {
	                id: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 64
	                        }
	                    ]
	                },
	                name: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 64
	                        }
	                    ]
	                },
	                entityManagerFactoryName: {
	                    validators: [
	                        "required", {
	                            $type: "length",
	                            min: 0,
	                            max: 120
	                        }
	                    ]
	                },
	                entityClassName: {
	                    validators: [{
	                        $type: "length",
	                        min: 0,
	                        max: 120
	                    }]
	                }
	            }
	        },
	        provider: {
	            url: "./api/excel/loadImporterSolutions",
	            parameter: {
	                searchKey: "{{searchKey}}"
	            },
	            complete: function () {
	            	var importerSolution = model.get("importerSolutions").current;
	                if (!model.get("importerSolutionId")) {
						if (importerSolution) {
							model.set("importerSolutionId", importerSolution.get("id"));
							setTimeout(function () {
						    	model.flush("mappingRules");
						    }, 200);
						}
	                }
	            }
	        }
	    });
	    
	    setTimeout(function () {
	    	model.describe("mappingRules", {
	            provider: {
	                url: "./api/excel/loadMappingRules",
	                beforeSend: function (self, arg) {
		                var importerSolutionId = model.get("importerSolutions").current.get("id");
		                arg.options.data.importerSolutionId = encodeURI(importerSolutionId);
		            }
	            }
	        });
	    }, 200);
    	
	    model.describe("entityManagerFactoryNames", {
	        provider : "./api/excel/loadEntityManagerFactoryNames",
	    });
	    model.flush("entityManagerFactoryNames");
	    
    	model.describe("entityClassNames", {
	        provider : "./api/excel/loadEntityClassNames/{{@managerFactoryName}}",
	    });
    	
    	model.describe("editItem", "ImporterSolution");
    	model.describe("editItemMappingRule", "");

		model.action({
			//测试Excel导入方法---------------
			
			importTip: function() {
				cola.widget("dialogImport").show();
			},
			
			cancelImport: function() {
				cola.widget("dialogImport").hide();
			},
			
			Import: function() {
				var formData = new FormData();
				var files = $('#file')[0].files;
				for (var i = 0; i < files.length; i++) {
					formData.append("file", files[i]);
				}
				formData.append("importerSolutionId", "text1");
				$.ajax({
					data: formData,
                    type: "POST",
                    url: "excel/importExcel",
                    async: false,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function(result) {
                    	cola.widget("dialogImport").hide();
                        cola.NotifyTipManager.warning({
                            message: "消息提示",
                            description: result,
                            showDuration: 3000
                        });
                        
                        var form = document.createElement('form');
                    	document.body.appendChild(form);
                    	var pos = file.nextSibling;
                    	form.appendChild(file);
                    	form.reset();
                    	pos.parentNode.insertBefore(file, pos);
                    	document.body.removeChild(form);
                    }
                });
			},
			//测试Excel导入方法---------------
			
			
			addImporterSolution: function () {
				model.set("editItem", {
	                editType: "新增"
	            });
	            $("#msgImporterSolutionModal").modal('show');
			},
			
			cancelImporterSolution: function() {
	            model.set("editItem", {});
	            $("#msgImporterSolutionModal").modal('hide');
	        },
			
			saveImporterSolution: function() {
				var entity = model.get("editItem");
	            var result = entity.validate();
	            if (result) {
	            	var isNew = entity.get("editType") === "新增";
	            	var path = isNew ? "./api/excel/addImporterSolution" : "./api/excel/modifyImporterSolution";
	                var data = entity.toJSON();
	                $.ajax({                
	                    data: JSON.stringify(data),
	                    type: isNew ? "POST" : "PUT",
	                    contentType: "application/json",
	                    url: path,
	                    success: function() {
	                    	model.get("importerSolutions").insert(data);
	                    	model.flush("importerSolutions");
	                        $("#msgImporterSolutionModal").modal('hide');
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "保存成功!",
	                            showDuration: 5000
	                        });
	                    }
	                });
	            }
	        },
	        
	        modifyImporterSolution: function(item) {
	            model.set("editItem", item.toJSON());
	            $("#msgImporterSolutionModal").modal('show');
	        },
	        
	        removeImporterSolutionTip: function (item) {
	        	model.set("editItem", item);
	        	$("#delImporterSolutionModal").modal('show');
	        },
	        
	        removeImporterSolution: function () {
		        var item = model.get("editItem");
		        if (item) {
		            $.ajax({
		                url: "./api/excel/removeImporterSolution",
		                data: {
		                    "id" : item.get("id")
		                },
		                type: "POST",
		                success: function() {
		                    $("#delImporterSolutionModal").modal('hide');
		                    item.remove();
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "删除成功!",
	                            showDuration: 5000
	                        });
		                }
		            });
		        }
	        },
	        
	        cancelRemoveImporterSolution: function () {
	        	$("#delImporterSolutionModal").modal('hide');
	        },
	        
	        search: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("importerSolutions");
				}			
			},
			
			autoCreateMappingRules: function() {
				var item = model.get("importerSolutions").current;
		        if (item) {
		            $.ajax({
		                url: "./api/excel/autoCreateMappingRules",
		                data: {
		                    "importerSolutionId": item.get("id")
		                },
		                type: "POST",
		                success: function() {
		                	model.flush("mappingRules");
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "创建成功!",
	                            showDuration: 5000
	                        });
		                }
		            });
		        }
			},
	        
	        //MappingRule
	        addMappingRule: function () {
	        	var importerSolution = model.get("importerSolutions").current;
	        	if (importerSolution) {
	        		model.set("editItemMappingRule", {
		                editType: "新增",
		                importerSolutionId: importerSolution.get("id")
		            });
		            $("#msgMappingRuleModal").modal('show');
	        	} else {
	        		cola.NotifyTipManager.warning({
                        message: "消息提示",
                        description: "请先添加Excel导入方案！",
                        showDuration: 3000
                    });
	        	}
			},
	        
			cancelMappingRule: function() {
	            model.set("editItemMappingRule", {});
	            $("#msgMappingRuleModal").modal('hide');
	        },
			
			saveMappingRule: function() {
				var entity = model.get("editItemMappingRule");
	            var result = entity.validate();
	            if (result) {
	            	var isNew = entity.get("editType") === "新增";
	            	var path = isNew ? "./api/excel/addMappingRule" : "./api/excel/modifyMappingRule";
	                var data = entity.toJSON();
	                $.ajax({                
	                    data: JSON.stringify(data),
	                    type: isNew ? "POST" : "PUT",
	                    contentType: "application/json",
	                    url: path,
	                    success: function() {
	                    	model.get("mappingRules").insert(data);
	                    	model.flush("mappingRules");
	                        $("#msgMappingRuleModal").modal('hide');
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "保存成功!",
	                            showDuration: 5000
	                        });
	                    }
	                });
	            }
	        },
	        
	        modifyMappingRule: function(item) {
	            model.set("editItemMappingRule", item.toJSON());
	            $("#msgMappingRuleModal").modal('show');
	        },
	        
	        removeMappingRuleTip: function (item) {
	        	model.set("editItemMappingRule", item);
	        	$("#delMappingRuleModal").modal('show');
	        },
	        
	        removeMappingRule: function () {
		        var item = model.get("editItemMappingRule");
		        if (item) {
		            $.ajax({
		                url: "./api/excel/removeMappingRule",
		                data: {
		                    "id" : item.get("id")
		                },
		                type: "POST",
		                success: function() {
		                    $("#delMappingRuleModal").modal('hide');
		                    item.remove();
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "删除成功!",
	                            showDuration: 5000
	                        });
		                }
		            });
		        }
	        },
	        
	        cancelRemoveMappingRule: function () {
	        	$("#delMappingRuleModal").modal('hide');
	        }
	        

		});

	    model.widgetConfig({
	    	importerSolutionTable: {
	            $type: "table",
	            bind: "importerSolution in importerSolutions",
	            showHeader: true,
	            changeCurrentItem: true,
	            highlightCurrentItem: true,
	            currentPageOnly: true,
	            itemClick: function (self, arg) {
	                if (self.get("currentItem").get("id") != model.get("importerSolutionId")) {
	                    model.set("importerSolutionId", self.get("currentItem").get("id"));
	                    model.flush("mappingRules");
	                }
	            }
	        },
	        mappingRuleTable: {
	            $type: "table",
	            bind: "mappingRule in mappingRules",
	            showHeader: true,
	            changeCurrentItem: true,
	            highlightCurrentItem: true,
	            currentPageOnly: true,
	        },
	        managerFactoryNameDropDown: {
	            $type: "dropdown",
	            openMode: "drop",
	            dropdownHeight: "300px",
	            items: "{{mgn in entityManagerFactoryNames}}",
	            bind : "editItem.entityManagerFactoryName",
	            post: function (self, arg) {
	                var importerSolution = model.get("editItem");
	                var currentItem = self.get("currentItem");
	                importerSolution.set("entityManagerFactoryName", currentItem);
	            }
	        },
	        entityClassNameDropDown: {
	            $type: "dropdown",
	            openMode: "drop",
	            items: "{{ecn in entityClassNames}}",
	            dropdownHeight: "300px",
	            bind : "editItem.entityClassName",
	            beforeOpen: function(self, arg) {
	            	var importerSolution = model.get("editItem");
	                if (!model.get("managerFactoryName")) {
						model.set("managerFactoryName", importerSolution.get("entityManagerFactoryName"));
	                }
	            	model.flush("entityClassNames");
	            },
	            post: function (self, arg) {
	                var importerSolution = model.get("editItem");
	                var currentItem = self.get("currentItem");
	                importerSolution.set("entityClassName", currentItem);
	            }
	        }
	    });

		var path = "./excel";
		App.resetComponentAuth(path);

		/*解决页面刚渲染时页面结构错乱*/
		$("[tag='contentContainer']").attr("tag","");
	})
}).call(this);

