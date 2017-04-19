app.controller('DashboardCtrl', function($scope, $state, $http, $modal, $interval) {
    $scope.openCluster = function(cluster){
        $state.go('app.cluster', {id : cluster.uuid, name : cluster.name});
    }
    
    var initData = function(){
    	$http.get("info/clusters").success(function (response) {
			$scope.clusters = response;
		});
    }
    
    $scope.addCluster = function(){
    	$scope.modalModel = {};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/addClusterMonitor.html',
	        scope : $scope
	    })
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		$http.post('manager/cluster/add', $scope.modalModel).success(function(response){
	    			if(response.status){
	    				$http.get("info/cluster/info/" + response.data).then(function(){
	    					initData();
	    				});
	    			}
	            });
	    		modalInstance.close();
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.create = function(){
    	$state.go('app.install');
    }
    
    initData();
    
    var timer = $interval(function(){
    	initData();
    },30000)

    $scope.$on("$destroy", function() {
    	$interval.cancel(timer);
    });
});