(function () {
    $(function () {

        $(".ui.form").form({
             fields: {
                 username: {
                     identifier: "username",
                     rules: [
                         {
                             type: "empty",
                             prompt: "请输入用户名"
                         }
                     ]
                 },
                 password: {
                     identifier: 'password',
                     rules: [
                         {
                             type: "empty",
                             prompt: "请输入密码"
                         }
                     ]
                 }
             }
        });
        
        var url = location.search;
    	var loginError;
    	if (url.length > 0) {
    		loginError = "用户名或密码错误"; 
    	} else {
    		loginError = "";
    	};

        if (loginError) {
            $(".ui.form").form("add errors", [loginError]);
        }
    });

}).call(this);
