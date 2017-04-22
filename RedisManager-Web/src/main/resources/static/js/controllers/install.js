app.controller('InstallCtrl', function($scope, $state, $q, $http, $modal, $console, $interval) {

	$scope.importVersion = function(){
    	$state.go('app.importVersion');
    }
    
    $scope.importServer = function(){
    	$state.go('app.importServer');
    }
    
    var initData = function(){
		$q.all([
			$http.get("server/list"),
			$http.get("version/list"),
			$http.get("server/installed")
		]).then(function(datas){
			$scope.servers = datas[0].data;
			$scope.resources = datas[1].data;
			$scope.installed = datas[2].data;
		});
    }
    
    $scope.install = function(){
    	$scope.modalModel = {};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/installModal.html',
	        scope : $scope
	    })
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		$console.show('console', '/install', $scope.modalModel).then(function(flag){
	    			initData();
	    		});
	    		modalInstance.close();
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.create = function(){
    	$scope.modalModel = {};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/createCluster.html',
	        scope : $scope
	    })
	    modalInstance.opened.then(function(){
	    	console.log($scope.modalModel);
	    	$scope.ok = function () {
	    		$console.show('console', '/create', $scope.modalModel).then(function(flag){
	    			initData();
	    		});
	    		modalInstance.close();
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.remove = function(install){
    	$http.post('server/delInstall', install).success(function(response){
			if(response.status){
				initData();
			}
        });
    }
    initData();
});