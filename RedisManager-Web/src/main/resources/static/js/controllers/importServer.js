app.controller('ImportServerCtrl', function($scope, $state, $http, $modal, $interval) {
	
	var initData = function(){
    	$http.get("server/list").success(function (response) {
			$scope.servers = response;
		});
    }
    
    $scope.add = function(model){
    	$scope.modalModel = {workhome:'/usr/local/redis-cluster'};
    	if(model){
    		$scope.modalModel = model;
    	}
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/addServer.html',
	        scope : $scope
	    })
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		$http.post('server/add', $scope.modalModel).success(function(response){
	    			if(response.status){
	    				initData();
	    			}
	            });
	    		modalInstance.close();
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    
    initData();
});