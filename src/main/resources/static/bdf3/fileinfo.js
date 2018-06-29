(function () {
	cola(function(model) { 
		model.describe("fileInfos", {
	        dataType: {
	            name: "FileInfo"
	        },
	        provider: {
	            url: "./api/fileinfo/load",
	            pageSize: 10,
	            parameter: {
	            	searchKey: "{{searchKey}}"
	            }
	        }
	    });
		
		model.describe("editItem", "FileInfo");
		
		model.action({
			uploadTip: function() {
				cola.widget("dialogUpload").show();
			},
			
			cancelUpload: function() {
				cola.widget("dialogUpload").hide();
			},
			
			upload: function() {
				var formData = new FormData();
				var files = $('#file')[0].files;
				for (var i = 0; i < files.length; i++) {
					formData.append("files", files[i]);
				}
				$.ajax({                
                    data: formData,
                    type: "POST",
                    url: "fileUpload/upload",
                    async: false,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function() {
                    	model.flush("fileInfos");
                    	cola.widget("dialogUpload").hide();
                        cola.NotifyTipManager.success({
                            message: "消息提示",
                            description: "上传成功!",
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
			
			download: function(item) {
		        if (item) {
		        	window.location.href="http://localhost:8080/bdf3-colaui/fileUpload/download?fileId=" + item.get("id");
		        } else {
		        	cola.NotifyTipManager.warning({
                        message: "消息提示",
                        description: "暂无文件下载!",
                        showDuration: 3000
                    });
		        }
			},
			
			
	        removeTip: function(item) {
	        	model.set("editItem", item);
	        	$("#delModal").modal('show');
	        },
	        
	        remove: function() {
		        var item = model.get("editItem");
		        if (item) {
		            $.ajax({
		                url: "./api/fileinfo/remove",
		                data: {
		                    "id" : item.get("id")
		                },
		                type: "POST",
		                success: function() {
		                	item.remove();
		                    $("#delModal").modal('hide');
	                        cola.NotifyTipManager.success({
	                            message: "消息提示",
	                            description: "删除成功!",
	                            showDuration: 3000
	                        });
		                }
		            });
		        }
	        },
	        
	        cancelRemove: function() {
	        	$("#delModal").modal('hide');
	        },
	        
			search: function() {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("fileInfos");
				}			
			}    
		});

		var path = "./fileinfo";
		App.resetComponentAuth(path);

		/*解决页面刚渲染时页面结构错乱*/
		$("[tag='contentContainer']").attr("tag", "");	    

	})
}).call(this);

