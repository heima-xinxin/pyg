 //控制层 
app.controller('orderStatisticsController' ,function($scope,$controller,$location ,orderStatisticsService){
	
	$controller('baseController',{$scope:$scope});//继承


	//分页
	$scope.search=function(date){
        orderStatisticsService.search(date).success(
			function(response){
				$scope.list=response;

			}			
		);
	}
	

	


});	
