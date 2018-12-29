//服务层
app.service('userService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../user/findAll.do');
	}

	//冻结
	this.freeze=function(ids){
		return $http.get('../user/freeze.do?ids='+ids);
	}
    this.unfreeze=function(ids){
        return $http.get('../user/unfreeze.do?ids='+ids);
    }
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../user/search.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
