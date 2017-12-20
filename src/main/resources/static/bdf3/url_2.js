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
                url: "./api/url/loadTop",
                parameter: {
                    searchKey: "{{searchKey}}"
                }
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
                               message: "保存成功！！！",
                               showDuration: 1500,
                           });
                       }
                   });
               }
           },

           addTop: function () {
               var nodes, order, entity;
               model.definition("nameAjaxValidator").set("disabled", false);
               nodes = model.get("urls");
               if (!nodes) {
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
                       showDuration: 2000
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
                       message: "删除成功！！！",
                       showDuration: 1500,
                   });
               }
           }
       });
    });

}).call(this);
