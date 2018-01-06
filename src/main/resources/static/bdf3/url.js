(function () {
    cola(function (model) {

        model.dataType({
            name: "Url",
            properties: {
                name: {
                    validators: [
                        "required",
                        {
                            $type: "length",
                            min: 0,
                            max: 30
                        },
                        new cola.AjaxValidator({
                            method: "GET",
                            name: "nameAjaxValidator",
                            message: "菜单已存在!",
                            disabled: true,
                            data: {
                                name: ":data"
                            },
                            url: "./api/url/exist"
                        })
                    ]
                },
                path: {
                    validators: [{
                        $type: "length",
                        min: 0,
                        max: 60
                    }]
                },
                icon: {
                    validators: ["required", {
                        $type: "length",
                        min: 0,
                        max: 120
                    }]
                },
                description: {
                    validators: [{
                        $type: "length",
                        min: 0,
                        max: 120
                    }]
                },
                urls: {
                    dataType: "Url",
                    provider: {
                        url: "./api/url/loadSub",
                        parameter: {
                            parentId: "{{@id}}"
                        }
                    }
                }
            }
        });

        model.describe("editItem", "Url");
        model.describe("urls", {
            dataType: "Url",
            provider: {
                url: "./api/url/loadTop"
            }
        });

        model.widgetConfig({
             urlTree: {
                 $type: "tree",
                 lazyRenderChildNodes: false,
                 autoExpand: true,
                 autoCollapse: false,
                 highlightCurrentItem: true,
                 bind: {
                     expression: "url in urls",
                     textProperty: "name",
                     child: {
                         recursive: true,
                         textProperty: "name",
                         expression: "url in url.urls"
                     }
                 },
                 currentNodeChange: function (self, arg) {
                     model.definition("nameAjaxValidator").set("disabled", true);
                     var current = self.get("currentNode");
                     model.set("editItem", current.get("data").toJSON());
                 },
                 height: "100%",
             }

        });

       model.action({
           save: function() {
               var entity = model.get("editItem");
               var result = entity.validate();
               if (result) {
                   var isNew = entity.get("editType") === "新增";
                   var path = isNew ? "./api/url/add" : "./api/url/modify";
                   var data = entity.toJSON();
                   $.ajax({
                       data: JSON.stringify(data),
                       type: isNew ? "POST" : "PUT",
                       contentType : "application/json",
                       url: path,
                       success: function() {
                           //model.get("urls").insert(data);
                           //$("#msgModal").modal('hide');
                           model.flush("urls");
                           cola.NotifyTipManager.success({
                               message: "消息提示",
                               description: "保存成功!",
                               showDuration: 3000
                           });
                       }
                   });
               }
           },

           addTop: function () {
               var nodes, order, entity;
               model.definition("nameAjaxValidator").set("disabled", false);
               nodes = model.get("urls");
               if (!nodes.entityCount > 0) {
                   order = 1;
               } else {
                   order = nodes.last().get("order") + 1;
               }
               entity = nodes.insert({
                   editType: "新增",
                   name : "<新菜单>",
                   order : order,
                   icon : "icon edit",
                   navigable : true
               });
               var tree = cola.widget("urlTree");
               var currentNode = tree.findNode(entity);
               tree.expand(currentNode);
               tree.set("currentItem", entity);
           },

           addChild: function() {
               var childNodes, currentNode, newEntity, order, entity, tree;
               model.definition("nameAjaxValidator").set("disabled", false);
               tree = cola.widget("urlTree");
               currentNode = tree.get("currentNode");
               entity = currentNode.get("data");
               if (entity.state == "new") {
                   cola.NotifyTipManager.error({
                       message: "添加失败！！",
                       description: "您添加的节点未保存，请先保存再添加子节点！!",
                       showDuration: 3000
                   });
               } else {
                   childNodes = entity.get("urls", "sync");
                   order = 1;
                   if (!childNodes) {
                       entity.set("urls", []);
                       childNodes = entity.get("urls");
                   } else {
                       if (childNodes.entityCount > 0)
                           order = childNodes.last().get("order") + 1;
                   }
                   newEntity = childNodes.insert({
                       editType: "新增",
                       name: "<新菜单>",
                       order: order,
                       icon : "icon edit",
                       navigable: true,
                       parentId: entity.get("id")
                   });
                   var newCurrentNode = tree.findNode(entity);
                   tree.expand(newCurrentNode);
                   tree.set("currentItem", newEntity);
                   return false;
               }
           },

           removeShow: function () {
               $("#delModal").modal('show');
           },

           search: function() {
               var key = window.event.keyCode;
               if (key == 13) {
                   model.flush("urls");
               }
           },

           cancelRemove: function () {
               $("#delModal").modal('hide');
           },

           refresh: function() {
               model.flush("urls");
           },

           remove: function () {
               var entity = model.get("editItem");
               var  childs = entity.get("urls", "sync");
               if(childs && childs.entityCount > 0){
                   $("#removeModal").modal('hide');
                   cola.NotifyTipManager.error({
                       message: "删除失败",
                       description: "您删除的节点有子节点，请先删除子节点！",
                       showDuration: 3000,
                   });
               } else {
                   $.ajax({
                       url: "./api/url/remove",
                       data: {
                           "id": entity.get("id")
                       },
                       type: "POST",
                       success: function() {
                           entity.remove();
                           $("#delModal").modal('hide');
                           model.flush("urls");
                       }
                   }).done(function(){
                       var currentNode = cola.widget("urlTree").get("currentNode");
                       if (currentNode) {
                           return currentNode.get("data").remove();
                       }
                   });
                   cola.NotifyTipManager.success({
                       message: "消息提示",
                       description: "删除成功!",
                       showDuration: 3000
                   });
               }
           },

           showIcon: function() {
               cola.widget("dialogIcon").show();
           },

           cancelSelect: function() {
               cola.widget("dialogIcon").hide();
           }

       });

        model.widgetConfig({
            dialogIcon: {
                $type: "dialog",
                beforeShow: function (self, arg) {
                    $.get('static/resources/icons.json', function(data){
                        $.each(data, function(index, obj) {
                            var $h3 = $("<h1 style='font-size: 25px; color: #000;'>" + obj.name + "</h1>");
                            var $divContent = $("<div class='ui doubling five column grid' style='margin-top: 10px;'></div>");
                            var $topDiv = $("<div style='padding: 30px;'></div>");
                            $topDiv.append($h3);
                            $("#iconContent").append($topDiv);

                            var icons = obj.icons;
                            for(var i = 0; i < icons.length; i++){
                                var name = icons[i];
                                var end = name.indexOf(" icon");
                                var showName = name.substring(0, end);
                                var $div = $("<div class='hvr-pulse' style='height: 140px; width: 190px; text-align: center; padding: 15px;'><i style='font-size: 22px; width: 100%; height: 50px;' class='" + name + "'></i><p style='font-size: 16px;'>" + showName + "</p></div>");
                                $divContent.append($div);
                            }
                            $topDiv.append($divContent);
                        });
                        $("i").dblclick(function(){
                            var iconName = $(this).attr("class");
                            cola.widget("urlTree").get("currentNode").get("data").set("icon", iconName);
                            cola.widget("iconInput").set("value", iconName);
                            cola.widget("dialogIcon").hide();
                        });
                    }, 'json');
                },
                hide: function(self, arg) {
                    $("#iconContent").find("div").remove();
                }
            }
        });

        var path = "./url";
        App.resetComponentAuth(path);

        /*解决页面刚渲染时页面结构错乱*/
        $("[tag='contentContainer']").attr("tag","");

    });

}).call(this);
