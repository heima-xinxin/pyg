 //控制层 
app.controller('orderItemController' ,function($scope,$controller,$location ,orderItemService){
	
	$controller('baseController',{$scope:$scope});//继承

	//分页
	$scope.search=function(page,rows){
		orderItemService.search(page,rows).success(
			function(response){
				alert(1);
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	

	


});	
