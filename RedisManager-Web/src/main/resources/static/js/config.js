var app = angular.module('app') .config( ['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
    function ($controllerProvider,   $compileProvider,   $filterProvider,   $provide) {
        app.controller = $controllerProvider.register;
        app.directive  = $compileProvider.directive;
        app.filter     = $filterProvider.register;
        app.factory    = $provide.factory;
        app.service    = $provide.service;
        app.constant   = $provide.constant;
        app.value      = $provide.value;
    }
]).config(function($httpProvider){
    $httpProvider.defaults.transformRequest = MyQuery.param;
    $httpProvider.defaults.headers.get = {'Cache-Control':'no-cache', "Pragma":"no-cache", "cache": false}
    $httpProvider.defaults.headers.post = {'Content-Type': 'application/x-www-form-urlencoded', "cache": false};
    $httpProvider.interceptors.push("httpInterceptor");
});

app.factory("httpInterceptor", [ "$q", "$rootScope", 'toaster', function($q, $rootScope, toaster) {
	return {
		request: function(config) {
			return config || $q.when(config);
		},
	　　 requestError: function(rejection) {
	　　　　 return $q.reject(rejection)
	　	},
	 	response: function(response) {
	 		if(response.config.method == 'POST'){
		 		if (response.data.status){
		 			if(response.data.message){
		 				toaster.pop("success", "success", response.data.message);
		 			}else{
		 				toaster.pop("success", "success");
		 			}
		 		}else{
		 			if(response.data.message){
		 				toaster.pop("warning", "failed", response.data.message);
		 			}
		 		}
	 		}
		   return response || $q.when(response);
	 	},
	 	responseError : function(rejection) {
	 		if (rejection.status == 500){
	 			toaster.pop("error", rejection.statusText, rejection.data.message);
	 		}
	 		return $q.reject(rejection);
	 	}
	};
}]);

app.factory('$Popup', ['$q','$modal', function ($q, $modal) {
    return {
    	alert: function(title, content) {
	        var deferred = $q.defer();
	        var confirmModal = $modal.open({
	          backdrop: 'static',
	          windowClass: "confirmModal",
	          template :  '<div class="m-c">\n'+
				          '  <div class="modal-header">\n'+
				          '    <h4 class="modal-title">{{title}}</h4>\n'+
				          '  </div>\n'+
				          '  <div class="modal-body" ng-if="content">{{content}}</div>\n'+
				          '  <div class="modal-footer" style="text-align: center;">\n'+
				          '    <button type="button" class="btn btn-primary" ng-click="ok()">OK</button>\n'+
				          '  </div>\n'+
				          '</div>\n',
			  controller: function($scope, $modalInstance){
				  $scope.title = title;
				  $scope.content = content;
				  $scope.ok = function () {
					  $modalInstance.dismiss('cancel');
			          deferred.resolve(true);
		          };
		          $scope.cancel = function () {  
		            $modalInstance.dismiss('cancel');
		            deferred.resolve(false);
		          };
			  }
	        });
	        return deferred.promise;
      },
      showText: function(title, content) {
	        var deferred = $q.defer();
	        var confirmModal = $modal.open({
	          backdrop: 'static',
	          windowClass: "confirmModal",
	          template :  '<div class="m-c">\n'+
				          '  <div class="modal-header">\n'+
				          '    <h4 class="modal-title">{{title}}</h4>\n'+
				          '  </div>\n'+
				          '  <div class="modal-body" ng-if="content"><textarea style="width:100%;height:200px;resize:none;" readonly="readonly">{{content}}</textarea></div>\n'+
				          '  <div class="modal-footer" style="text-align: center;">\n'+
				          '    <button type="button" class="btn btn-primary" ng-click="ok()">OK</button>\n'+
				          '  </div>\n'+
				          '</div>\n',
			  controller: function($scope, $modalInstance){
				  $scope.title = title;
				  $scope.content = content;
				  $scope.ok = function () {
					  $modalInstance.dismiss('cancel');
			          deferred.resolve(true);
		          };
		          $scope.cancel = function () {  
		            $modalInstance.dismiss('cancel');
		            deferred.resolve(false);
		          };
			  }
	        });
	        return deferred.promise;
      },
      confirm: function(title, content) {
        var deferred = $q.defer();
        var confirmModal = $modal.open({
          backdrop: 'static',
          windowClass: "confirmModal",
          template :  '<div class="m-c">\n'+
			          '  <div class="modal-header">\n'+
			          '    <h4 class="modal-title">{{title}}</h4>\n'+
			          '  </div>\n'+
			          '  <div class="modal-body" ng-if="content">{{content}}</div>\n'+
			          '  <div class="modal-footer" style="text-align: center;">\n'+
			          '    <button type="button" class="btn btn-primary" ng-click="ok()">OK</button>\n'+
			          '    <button type="button" class="btn btn-warning" ng-click="cancel()">cancel</button>\n'+
			          '  </div>\n'+
			          '</div>\n',
		  controller: function($scope, $modalInstance){
			  $scope.title = title;
			  $scope.content = content;
			  $scope.ok = function () {
				  $modalInstance.dismiss('cancel');
		          deferred.resolve(true);
	          };
	          $scope.cancel = function () {  
	            $modalInstance.dismiss('cancel');
	            deferred.resolve(false);
	          };
		  }
        });
        return deferred.promise;
      }
    }
}]);

app.factory('$console', ['$q','$compile','$modal', '$websocket', function ($q, $compile, $modal, $websocket) {
    return {
    	show: function(title, websocket, message) {
	        var deferred = $q.defer();
	        var confirmModal = $modal.open({
	          backdrop: 'static',
	          windowClass: "terminalModal",
	          template :  '<div class="m-c">\n'+
				          '  <div class="modal-header">\n'+
				          '    <h4 class="modal-title">{{title}}</h4>\n'+
				          '  </div>\n'+
				          '  <div class="modal-body"><div id="console" class="terminal"></div></div>\n'+
				          '  <div class="modal-footer" style="text-align: center;">\n'+
				          '    <button type="button" class="btn btn-primary" ng-click="ok()">OK</button>\n'+
				          '  </div>\n'+
				          '</div>\n',
			  controller: function($scope, $modalInstance){
				  $scope.title = title;
				  $scope.ok = function () {
					  $modalInstance.dismiss('cancel');
			          deferred.resolve(true);
		          };
		          confirmModal.opened.then(function() {
		          		var ws = $websocket('ws://'+document.location.host + websocket);
						  ws.onOpen(function(){
						  		ws.send(JSON.stringify(message));
						  		var element = angular.element("#console");
						  	  	element.append('<p class="log">command>' + websocket + " "+ JSON.stringify(message) + '</p>');
						  	  	var top = element[0].scrollHeight - element[0].clientHeight + 100;
							  	if(top > 0){
							  		element[0].scrollTop = top;
							  	}
						  });
						  ws.onMessage(function (message) {
						  		var element = angular.element("#console");
						  	  	element.append('<p class="log">log>' + message.data + '</p>');
						  	  	var top = element[0].scrollHeight - element[0].clientHeight + 100;
							  	if(top > 0){
							  		element[0].scrollTop = top;
							  	}
						  });
						  ws.onError(function(){
						  		var element = angular.element("#console");
						  	  	element.append('<p class="log">error>websocket error</p>');
						  	  	var top = element[0].scrollHeight - element[0].clientHeight + 100;
							  	if(top > 0){
							  		element[0].scrollTop = top;
							  	}
						  })
                  });
			  }
	        });
	        return deferred.promise;
      }
    }
}]);

app.filter('to_trusted', ['$sce',
    function ($sce) {
		return function (text) { 
			return $sce.trustAsHtml(text); 
		} 
	}] 
);