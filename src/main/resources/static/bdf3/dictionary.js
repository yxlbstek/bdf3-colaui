(function () {
	cola(function(model) {
	    model.describe("dictionaries", {
			dataType: {
				name: "Dictionary",
				properties: {
					code: {
						validators: [
							"required",
							new cola.AjaxValidator({
								method: "GET",
								name: "codeAjaxValidator",
								message: "该编码已存在！",
								disabled: true,
								data: {
									code: ":data"
								},
								url: "./api/dictionary/exist"
							})
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
					}
				}
			},
	        provider: {
	            url: "./api/dictionary/load",
	            pageSize: 10,
	            parameter: {
	                searchKey: "{{searchKey}}"
	            },
	            complete: function () {
	                if (!model.get("dictionaryId")) {
	                    model.set("dictionaryId", model.get("dictionaries").current.get("id"));
	                }
	            }
	        }
	    });

	    setTimeout(function () {
	        model.describe("dictionaryItems", {
				dataType: {
					name: "DictionaryItem",
					properties: {
						key: {
							validators: [
								"required",
								new cola.AjaxValidator({
									method: "GET",
									name: "keyAjaxValidator",
									message: "该Key已存在！",
									disabled: true,
									data: {
										key: ":data"
									},
									url: "./api/dictionaryItem/exist"
								})
							]
						},
						value: {
							validators: [
								"required", {
									$type: "length",
									min: 0,
									max: 64
								}
							]
						}
					}
				},
	            provider: {
	                url: "./api/dictionaryItem/load/{{@dictionaryId}}",
					pageSize: 10,
					parameter: {
						searchKey: "{{searchItemKey}}"
					},
	            }
	        });
	        model.flush("dictionaryItems");
	    }, 200);

		model.describe("editItemDictionary", "Dictionary");
		model.describe("editItemDictionaryItem", "DictionaryItem");

		model.action({
			addDictionary: function () {
				model.definition("codeAjaxValidator").set("disabled", false);
				model.set("editItemDictionary", {
					editType: "新增"
				});
				$("#dictionaryModal").modal('show');
			},

			saveDictionary: function() {
				var entity = model.get("editItemDictionary");
				var result = entity.validate();
				if (result) {
					var isNew = entity.get("editType") === "新增";
					var path = isNew ? "./api/dictionary/add" : "./api/dictionary/modify";
					var data = entity.toJSON();
					$.ajax({
						data: JSON.stringify(data),
						type: isNew ? "POST" : "PUT",
						contentType : "application/json",
						url: path,
						success: function() {
							model.get("dictionaries").insert(data);
							$("#dictionaryModal").modal('hide');
							model.flush("dictionaries");
							cola.NotifyTipManager.success({
								message: "消息提示",
								description: "保存成功!",
								showDuration: 3000
							});
						}
					});
				}
			},

			cancelDictionary: function() {
				model.set("editItemDictionary", {});
				$("#dictionaryModal").modal('hide');
			},

			removeDictionaryTip: function (item) {
				model.set("editItemDictionary", item);
				$("#delDictionaryModal").modal('show');
			},

			removeDictionary: function () {
				var item = model.get("editItemDictionary");
				if(item){
					$.ajax({
						url: "./api/dictionary/remove",
						data: {
							"id" : item.get("id")
						},
						type: "POST",
						success: function() {
							$("#delDictionaryModal").modal('hide');
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

			cancelRemoveDictionary: function () {
				$("#delDictionaryModal").modal('hide');
			},

			modifyDictionary: function(item) {
				model.definition("codeAjaxValidator").set("disabled", false);
				model.set("editItemDictionary", item.toJSON());
				$("#dictionaryModal").modal('show');
			},

	        search: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("dictionaries");
				}
			},

			//字典项
			addItem: function () {
				model.definition("keyAjaxValidator").set("disabled", false);
				model.set("editItemDictionaryItem", {
					editType: "新增"
				});
				$("#dictionaryItemModal").modal('show');
			},

			saveItem: function() {
				var entity = model.get("editItemDictionaryItem");
				var result = entity.validate();
				if (result) {
					var isNew = entity.get("editType") === "新增";
					var path = isNew ? "./api/dictionaryItem/add" : "./api/dictionaryItem/modify";
					var data = entity.toJSON();
					$.ajax({
						data: JSON.stringify(data),
						type: isNew ? "POST" : "PUT",
						contentType : "application/json",
						url: path,
						success: function() {
							model.get("dictionaryItems").insert(data);
							$("#dictionaryItemModal").modal('hide');
							model.flush("dictionaryItems");
							cola.NotifyTipManager.success({
								message: "消息提示",
								description: "保存成功!",
								showDuration: 3000
							});
						}
					});
				}
			},

			cancelItem: function() {
				model.set("editItemDictionaryItem", {});
				$("#dictionaryItemModal").modal('hide');
			},

			removeItemTip: function (item) {
				model.set("editItemDictionaryItem", item);
				$("#delDictionaryItemModal").modal('show');
			},

			removeItem: function () {
				var item = model.get("editItemDictionaryItem");
				if(item){
					$.ajax({
						url: "./api/dictionaryItem/remove",
						data: {
							"id" : item.get("id")
						},
						type: "POST",
						success: function() {
							$("#delDictionaryItemModal").modal('hide');
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

			cancelRemoveItem: function () {
				$("#delDictionaryItemModal").modal('hide');
			},

			modifyItem: function(item) {
				model.definition("keyAjaxValidator").set("disabled", true);
				model.set("editItemDictionaryItem", item.toJSON());
				$("#dictionaryItemModal").modal('show');
			},

			searchItem: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("dictionaryItems");
				}
			}
		});

	    model.widgetConfig({
	        dictionaryTable: {
	            $type: "table",
	            bind: "role in dictionaries",
	            showHeader: true,
	            changeCurrentItem: true,
	            highlightCurrentItem: true,
	            currentPageOnly: true,
	            itemClick: function (self, arg) {
	                if (self.get("currentItem").get("id") != model.get("dictionaryId")) {
	                    model.set("dictionaryId", self.get("currentItem").get("id"));
	                    model.flush("dictionaryItems");
	                }
	            }
	        }
	    });

		var path = "./dictionary";
		App.resetComponentAuth(path);
		
		$("[tag='contentContainer']").attr("tag","");
		$(".ui.label.basic").transition({
		    animation : 'jiggle',
		    duration  : 800,
		    interval  : 1000
		 });

	})
}).call(this);

