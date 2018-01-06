(function () {
	cola(function(model) {

		model.describe("sentMessages", {
	        provider: {
	            url: "./api/message/loadSent",
	            pageSize: 10,
	            parameter: {
	            	searchKey: "{{searchSentKey}}"
	            }
	        }
	    });

		model.describe("receiverMessages", {
			provider: {
				url: "./api/message/loadReceiver",
				pageSize: 10,
				parameter: {
					searchKey: "{{searchReceiverKey}}"
				}
			}
		});

		model.describe("users", {
			provider: {
				url: "./api/message/loadUsers"
			}
		});

		model.describe("editItemSent", {});
		model.describe("editItemReceiver", {});
		model.describe("message", {});
		model.describe("messageSend", {});

		model.action({
			removeSendTip: function(sent) {
				model.set("editItemSent", sent);
				$("#delSendModal").modal('show');
			},

			removeSend: function () {
				var item = model.get("editItemSent");
				if(item){
					$.ajax({
						url: "./api/message/removeSend",
						data: {
							"id" : item.get("id")
						},
						type: "POST",
						success: function() {
							$("#delSendModal").modal('hide');
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

			cancelRemoveSend: function () {
				$("#delSendModal").modal('hide');
			},

			removeReceiverMessageTip: function(receiver) {
				model.set("editItemReceiver", receiver);
				$("#delReceiverModal").modal('show');
			},

			removeReceiver: function() {
				var item = model.get("editItemReceiver");
				if(item){
					$.ajax({
						url: "./api/message/removeReceiver",
						data: {
							"id" : item.get("id")
						},
						type: "POST",
						success: function() {
							$("#delReceiverModal").modal('hide');
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

			cancelDialog: function() {
				cola.widget("dialogMessage").hide();
			},

			cancelSendDialog: function() {
				cola.widget("dialogSendMessage").hide();
			},

			cancelRemoveReceiver: function() {
				$("#delReceiverModal").modal('hide');
			},

			searchReceiver: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("receiverMessages");
				}
			},

			searchSent: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("sentMessages");
				}
			},

			sendTip: function() {
				model.set("editItemSent", {});
				cola.widget("dialogNewMessage").show();
			},

			sendMessage: function() {
				var entity = model.get("editItemSent");
				if (entity) {
					var receiver = entity.get("receiver");
					if (receiver != null && receiver != "") {
						var data = entity.toJSON();
						$.ajax({
							data: JSON.stringify(data),
							type: "POST",
							contentType : "application/json",
							url: "./api/message/sendMessage",
							success: function() {
								cola.widget("dialogNewMessage").hide();
								model.flush("sentMessages");
								cola.NotifyTipManager.success({
									message: "消息提示",
									description: "发送成功!",
									showDuration: 3000
								});
							}
						});
					} else {
						cola.alert("收件人不能为空！", {
							level: cola.MessageBox.level.WARNING
						})
					}
				}
			},

			cancelSend : function() {
				cola.widget("dialogNewMessage").hide();
			},

			chooseUser: function() {
				cola.widget("dialogChooseUser").show();
			},

			saveChooseUser: function() {
				var receivers = [];
				var receiver = "";
				var users = model.get("users");
				users.each(function(user) {
					if (user.get("choose") == true) {
						receivers.push(user.get("username"));
					}
				});
				for (var i = 0; i < receivers.length; i++) {
					receiver += receivers[i] + ";";
				};
				model.get("editItemSent").set("receiver", receiver);
				cola.widget("dialogChooseUser").hide();
				model.flush("users");
			},

			cancelChooseUser: function() {
				cola.widget("dialogChooseUser").hide();
				model.flush("users");
			}
		});

		model.widgetConfig({
			receiverTable: {
				$type: "table",
				bind: "receiver in receiverMessages",
				highlightCurrentItem: true,
				columns: [
					{
						bind: ".title",
						caption: "消息名称"
					}, {
						bind: ".sender",
						caption: "发送人"
					}, {
						bind: "formatDate(receiver.createdAt, 'yyyy-MM-dd HH:mm:ss')",
						caption: "接收时间"
					}, {
						caption: "操作",
						template: "operation"
					}
				],
				itemDoubleClick: function (self, arg) {
					model.set("message", arg.item);
					cola.widget("dialogMessage").show();
					if (!arg.item.get("read")) {
						$.ajax({
							url: "./api/message/isRead",
							data: {
								"id" : arg.item.get("id")
							},
							type: "POST",
							success: function() {
								$("#delReceiverModal").modal('hide');
								model.flush("receiverMessages");
								App.refreshMessage();
							}
						});
					}
				},
				renderRow: function (self, arg) {
					if (!arg.item.get("read")) {
						arg.dom.style.color = "blue";
						arg.dom.style.fontWeight = "bold";
					}
				},
			},

			sentTable: {
				$type: "table",
				bind: "sent in sentMessages",
				highlightCurrentItem: true,
				columns: [
					{
						bind: ".title",
						caption: "消息名称"
					}, {
						bind: ".receiver",
						caption: "接收人"
					}, {
						bind: "formatDate(sent.createdAt, 'yyyy-MM-dd HH:mm:ss')",
						caption: "发送时间"
					}, {
						caption: "操作",
						template: "operation"
					}
				],
				itemDoubleClick: function (self, arg) {
					model.set("messageSend", arg.item);
					cola.widget("dialogSendMessage").show();
				}
			}
		});

		/*解决页面刚渲染时页面结构错乱*/
		$("[tag='contentContainer']").attr("tag","");

	})
}).call(this);

