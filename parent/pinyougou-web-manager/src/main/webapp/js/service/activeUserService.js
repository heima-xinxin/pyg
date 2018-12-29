//服务层
app.service('activeUserService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../activeUser/findAll.do');
	}

	//冻结
	this.freeze=function(ids){
		return $http.get('../activeUser/freeze.do?ids='+ids);
	}
    this.unfreeze=function(ids){
        return $http.get('../activeUser/unfreeze.do?ids='+ids);
    }
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../activeUser/search.do?page='+page+"&rows="+rows, searchEntity);
	}    	
});
