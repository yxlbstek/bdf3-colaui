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

        if (loginError) {
            $(".ui.form").form("add errors", [loginError]);
        }

        /*解决页面刚渲染时页面结构错乱*/
        $("[tag='contentContainer']").attr("tag","");
    });

}).call(this);
