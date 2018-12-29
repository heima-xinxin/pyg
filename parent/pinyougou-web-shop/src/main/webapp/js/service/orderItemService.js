//服务层
app.service('orderItemService',function($http){
	//分页 
	this.search=function(page,rows){
		return $http.post('../orderItem/search.do?page='+page+'&rows='+rows);
	}
});
