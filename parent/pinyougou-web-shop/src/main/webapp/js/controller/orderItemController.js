 //控制层 
app.controller('orderItemController' ,function($scope,$controller,$location ,orderItemService){
	
	$controller('baseController',{$scope:$scope});//继承
	//显示状态
    $scope.status = ["未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
	//分页
	$scope.search=function(page,rows){
		orderItemService.search(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	

	


});	
