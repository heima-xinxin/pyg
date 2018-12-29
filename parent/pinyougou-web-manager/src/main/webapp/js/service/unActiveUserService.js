//服务层
app.service('unActiveUserService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../unActiveUser/findAll.do');
	}

	//冻结
	this.freeze=function(ids){
		return $http.get('../unActiveUser/freeze.do?ids='+ids);
	}
    this.unfreeze=function(ids){
        return $http.get('../unActiveUser/unfreeze.do?ids='+ids);
    }
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../unActiveUser/search.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
