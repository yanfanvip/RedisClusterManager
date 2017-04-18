app.controller('ClusterOptionsCtrl', function($scope, $state, $stateParams, $http, $modal, $interval, $timeout, $websocket, $console) {
    $scope.id = $stateParams.id;
    $scope.name = $stateParams.name;

    var initData = function(){
    	$http.get("info/cluster/info/" + $scope.id).success(function (response) {
            $scope.clusterInfo = response;
        });
    	
        $http.get("info/cluster/tree/" + $scope.id).success(function (response) {
        	$scope.tree = response;
        });
    }
    
    $scope.moveSlot = function(master){
    	$scope.modalModel = {
    		start : master.slots[0].start,
    		end : master.slots[master.slots.length-1].end
    	};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/moveSlot.html',
	        scope : $scope
	    });
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		modalInstance.close();
	    		$console.show('console', '/slot_move', 
	    			{ cluster : $scope.id, node: master.node, start:$scope.modalModel.start, end:$scope.modalModel.end }
	    		).then(function(flag){
	    			$timeout(function(){
						initData();
			        },5000);
	    		});
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.queryData = function(master){
    	console.log(master);
    }
    
    $scope.moveSlave = function(slave){
    	console.log(slave);
    }
    
    $scope.toMaster = function(slave){
    	console.log(slave);
    }
    
    initData();
});