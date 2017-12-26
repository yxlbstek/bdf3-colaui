(function () {
    $(function () {
    	if (status == 403) {
    		$("#notExist").attr("style", "display: none !important");
    		$("#SysEx").attr("style", "display: none !important");
    	} else if (status == 404) {
    		$("#notAuth").attr("style", "display: none !important");
    		$("#SysEx").attr("style", "display: none !important");
    	} else {
    		$("#notExist").attr("style", "display: none !important");
    		$("#notAuth").attr("style", "display: none !important");
    	}
    });

}).call(this);
