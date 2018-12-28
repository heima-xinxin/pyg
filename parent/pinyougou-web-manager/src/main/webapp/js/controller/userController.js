//控制层
app.controller('userController' ,function($scope,$controller,userService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        userService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //分页
    // $scope.findPage=function(page,rows){
    //     contentCategoryService.findPage(page,rows).success(
    //         function(response){
    //             $scope.list=response.rows;
    //             $scope.paginationConf.totalItems=response.total;//更新总记录数
    //         }
    //     );
    // }
    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        userService.dele( $scope.selectIds ).success(
            function(response){
                if(response.flag){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        userService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

});
