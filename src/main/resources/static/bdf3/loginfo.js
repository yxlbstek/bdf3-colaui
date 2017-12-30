(function () {
	cola(function(model) {
		model.describe("logInfos", {
	        provider: {
	            url: "./api/logInfo/load",
	            pageSize: 15,
	            parameter: {
	            	searchKey: "{{searchKey}}"
	            }
	        }
	    });
		
		model.action({
			search: function () {
				var keyCode = window.event.keyCode;
				if (keyCode == 13) {
					model.flush("logInfos");
				}			
			}    
		});
		
		model.widgetConfig({
			logInfoTable: {
                $type: "table",
                bind: "logInfo in logInfos",
                highlightCurrentItem: true,
                columns: [
                    {
                        bind: "formatDate(logInfo.operationDate, 'yyyy-MM-dd HH:mm:ss')",
                        caption: "操作时间"
                    }, {
						bind: ".module",
						caption: "所属模块"
					}, {
						bind: ".category",
						caption: "日志类型"
					}, {
						bind: ".operationUser",
						caption: "操作人"
					}, {
						bind: ".operationUserNickname",
						caption: "操作人昵称"
					}, {
						bind: ".operation",
						caption: "操作"
					},{
						bind: ".source",
						caption: "来源"
					}, {
						bind: ".ip",
						caption: "IP地址"
					}
				]
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

