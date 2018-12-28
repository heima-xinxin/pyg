app.service("uploadService",function($http){
	
	this.uploadFile = function(){
		// 向后台传递数据://定义一个表单
		var formData = new FormData();
		// 向formData中添加数据: //向表单中添加数据
		formData.append("file",file.files[0]);
		//发送异步请求
		return $http({
			method:'post',
			url:'../upload/uploadFile.do',
			data:formData,
			//undefined 即代表form表单中上传文件的 mutipart/form"dta 什么属性
			headers:{'Content-Type':undefined} ,// Content-Type : text/html  text/plain
			transformRequest: angular.identity
		});
	}
	
});