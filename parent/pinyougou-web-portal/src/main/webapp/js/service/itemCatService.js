//服务层
app.service('itemCatService',function($http){

    //查询商品分类信息
    this.findItemCatList = function () {
        return $http.get("../itemCat/findItemCatList.do");
    }

});
