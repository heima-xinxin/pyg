//服务层
app.service('orderStatisticsService',function($http){
	//分页 
	this.search=function(date){
		return $http.get('../orderStatistics/search.do?date = '+date);
	}
});
