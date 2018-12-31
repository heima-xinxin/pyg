 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承

    //查询商品分类信息
    $scope.findItemCatList=function () {
        itemCatService.findItemCatList().success(function (response) {
            $scope.itemCatList=response;
        })
    }
});	
