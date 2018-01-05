(function() {
	cola(function(model) {
		var errorCount, login, loginCallback, longPollingTimeOut, showLoginMessage;
		model.describe("menus", {
			provider : {
				url : App.prop("service.menus")
			}
		});
		model.describe("user", {
			provider: {
				url: App.prop("service.user.detail")
			}
		});

		model.describe("users", {
			provider: {
				url: "./api/message/loadUsers"
			}
		});

		model.describe("editItemSent", {});

		model.dataType({
			name : "Password",
			properties : {
				oldPassword: {
					validators: [
						"required",
						{
							$type: "length",
							min: 6,
							max: 120
						},
						new cola.AjaxValidator({
							method: "GET",
							name: "oldAjaxValidator",
							message: "输入的密码不正确!",
							disabled: true,
							data: {
								oldPassword: ":data"
							},
							url: "./api/user/validatePassword"
						})
					]
				},
				newPassword : {
					validators: ["required", {
						$type: "length",
						min: 6,
						max: 120
					}]
				},
				confirmPassword : {
					validators: ["required", {
						$type: "length",
						min: 6,
						max: 120
					}]
				}
			}
		});

		model.describe("pw", "Password");
		model.set("messages", {});
		errorCount = 0;
		longPollingTimeOut = null;

		window.refreshMessage = function() {
			var options;
			options = {};
			if (longPollingTimeOut) {
				clearTimeout(longPollingTimeOut);
			}
			if (App.prop("longPollingTimeout")) {
				options.timeout = App.prop("longPollingTimeout");
			}
			return $.ajax(App.prop("service.messagePull"), options).done(
				function(messages) {
					var i, len, message;
					if (messages) {
						errorCount = 0;
						for (i = 0, len = messages.length; i < len; i++) {
							message = messages[i];
							model.set("messages", message.messageCount);
						}
					}
					if (App.prop("liveMessage")) {
						return longPollingTimeOut = setTimeout(
							refreshMessage, App
								.prop("longPollingInterval"));
					}
				}).error(
				function(xhr, status, ex) {
					if (App.prop("liveMessage")) {
						if (status === "timeout") {
							return longPollingTimeOut = setTimeout(
								refreshMessage, App
									.prop("longPollingInterval"));
						} else {
							errorCount++;
							return longPollingTimeOut = setTimeout(
								refreshMessage, 5000 * Math.pow(2, Math
									.min(6, errorCount - 1)));
						}
					}
				});
		};

		longPollingTimeOut = setTimeout(refreshMessage, 1000);
		refreshMessage();

		model.widgetConfig({
			dialogPassword: {
				$type: "dialog",
				beforeShow: function (self, arg) {
					model.definition("oldAjaxValidator").set("disabled", false);
					model.set("pw", {});
				},
				hide: function(self, arg) {
					model.definition("oldAjaxValidator").set("disabled", true);
				}
			},

			confirmPasswordInput: {
				$type: "input",
				post: function (self, arg) {
					var newPassword = cola.widget("newPasswordInput").get("value");
					var confirmPassword = cola.widget("confirmPasswordInput").get("value");
					if (confirmPassword != newPassword) {
						cola.alert("两次输入的密码不一致！", {
							level: cola.MessageBox.level.WARNING
						})
						cola.widget("savePwBtn").set("display", false);
					} else {
						cola.widget("savePwBtn").set("display", true);
					}
				}
			},

			subMenuTree : {
				$type : "tree",
				autoExpand : true,
				bind : {
					expression : "menu in subMenu",
					child : {
						recursive : true,
						expression : "menu in menu.children"
					}
				},
				itemClick : function(self, arg) {
					var data, menus;
					data = arg.item.get("data").toJSON();
					menus = data.children;
					if (menus && menus.length > 0) {
						return App.open(data.path, data);
					} else {
						App.open(data.path, data);
						cola.widget("subMenuLayer").hide();
					}
				}
			},

			subMenuLayer : {
				beforeShow : function() {
					return $("#viewTab").parent().addClass("lock");
				},
				beforeHide : function() {
					return $("#viewTab").parent().removeClass("lock");
				}
			}
		});

		model.action({
			changePassword: function() {
				cola.widget("dialogPassword").show();
			},

			cancelPassword: function() {
				cola.widget("dialogPassword").hide();
			},

			savePassword: function() {
				var entity = model.get("pw");
				var result = entity.validate();
				if (result) {
					$.ajax({
						data: {
							"newPassword": entity.get("newPassword")
						},
						type: "GET",
						url: "./api/user/changePassword",
						success: function() {
							cola.NotifyTipManager.success({
								message: "消息提示",
								description: "保存成功!",
								showDuration: 3000
							});
							cola.widget("dialogPassword").hide();
						}
					});
				}
			},

			dropdownIconVisible : function(item) {
				var menus, result;
				menus = item.get("children");
				result = false;
				if (menus && menus.entityCount > 0) {
					result = true;
				}
				return result;
			},

			showUserSidebar : function() {
				return cola.widget("userSidebar").show();
			},

			menuItemClickParent : function(item) {
				var data = item.toJSON();
				if (data.path && data.path != '') {
					if (!data.children.children) {
						cola.widget("subMenuLayer").hide();
					}
					return App.open(data.path, data);
				}
			},

			menuItemClickChildren : function(item) {
				 var data, i, len, menu, menus, recursive;
				 data = item.toJSON();
				 menus = data.children;
				 
				 //右侧栏toggle
				 if (cola.widget("subMenuLayer").get("display")) {
					 cola.widget("subMenuLayer").hide();
				 }
				 
				 recursive = function(d) {
				 	var i, len, ref, results;
				 	if (d.menus && d.menus.length > 0) {
				 		ref = d.menus;
				 		results = [];
				 		for (i = 0, len = ref.length; i < len; i++) {
				 			item = ref[i];
				 			results.push(recursive(item));
				 		}
				 		return results;
				 	} else {
				 		d.menus = null;
				 		return d.hasChild = false;
				 	}
				 };
				 if (menus && menus.length > 0) {
				 	 for (i = 0, len = menus.length; i < len; i++) {
				 		 menu = menus[i];
				 		 recursive(data);
				 	 }
				 	 model.set("subMenu", menus);
				 	 model.set("currentMenu", data);
				 	 cola.widget("subMenuLayer").show()
					 if (data.path && data.path != "") {
						 return App.open(data.path, data);
					 }
				 } else {
				 	 model.set("subMenu", []);
				 	 cola.widget("subMenuLayer").hide();
					 if (data.path && data.path != "") {
						 return App.open(data.path, data);
					 }
				 }
				 
			},

			hideSubMenuLayer : function() {
				return cola.widget("subMenuLayer").hide();
			},

			toggleSidebar : function() {
				var $dom, className;
				className = "collapsed";
				$dom = $("#frameworkSidebarBox");
				return $dom.toggleClass(className, !$dom.hasClass(className));
			},

			messageBtnClick : function() {
				var action;
				action = App.prop("message.action");
				if (action && typeof action === "object") {
					App.open(action.path, action);
				}
			},

			taskBtnClick : function() {
				var action;
				action = App.prop("task.action");
				if (action && typeof action === "object") {
					App.open(action.path, action);
				}
			},

			closeTab : function() {
				var keycode = event.keyCode == null ? event.which
						: event.keyCode;
				var viewTab = cola.widget("viewTab");
				if (keycode === 87) { // 快捷键w,关闭当前标签页
					viewTab.removeTab(viewTab.get("currentTab"));
				}
			}
		});

		$("#frameworkSidebar").accordion({
			exclusive : false
		}).delegate(".menu-item", "click", function() {
			$("#frameworkSidebar").find(".menu-item.current-item")
					.removeClass("current-item");
			return $fly(this).addClass("current-item");
		});

		// 菜单提示tip
		tipLabel = function($dom, event) {
			// 当菜单收缩后启用提示功能
			if (!$("#frameworkSidebarBox").hasClass("collapsed"))
				return;
			var _this = $($dom);
			var _parentDom = $($dom).parent();
			var tooltip = $("<div class='just-tooltip'><div class='just-con'>"
					+ _this.text() + "</div>"
					+ "<span class='just-right'></span></div>");
			$("body").append(tooltip);
			var div = $("div.just-tooltip");
			div.css({
				"top" : (_this.offset().top) + "px",
				"left" : (_parentDom.outerWidth() + 10) + "px",
				"opacity" : 0.6
			});
			div.animate({
				left : (_parentDom.outerWidth()) + "px",
				opacity : '0.9'
			}, "normal");
		};

		tipLabelOut = function() {
			$("div.just-tooltip").remove();
		};

		return $("#rightContainer>.layer-dimmer").on("click", function() {
			return cola.widget("subMenuLayer").hide();
		});
	});

	cola.ready(function() {
		var workbench;
		workbench = App.prop("workbench");
		if (workbench) {
			return App.open(workbench.path, workbench);
		}
	});

}).call(this);

