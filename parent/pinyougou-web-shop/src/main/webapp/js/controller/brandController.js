//控制层
app.controller('brandController' ,function($scope,$controller,$location ,brandService){

    $controller('baseController',{$scope:$scope});//继承

    //保存
    $scope.save=function(){

        var object;

            object = brandService.save($scope.entity);
        object.success(
            function(response){
                if(response.flag){
                    //重新查询
                    alert(response.message);
                }else{
                    alert(response.message);
                }
            }
        );
    }





});