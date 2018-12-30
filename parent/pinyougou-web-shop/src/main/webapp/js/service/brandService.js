//服务层
app.service('brandService',function($http){
//增加
	this.save=function(entity){
		return  $http.post('../brand/save.do',entity );
	}
});
