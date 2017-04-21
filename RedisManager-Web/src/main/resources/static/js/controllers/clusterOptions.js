app.controller('ClusterOptionsCtrl', function($scope, $state, $stateParams, $http, $modal, $interval, $timeout, $websocket, $Popup, $console) {
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
    	if(master.slots.length > 0){
    		$scope.modalModel = {
	    		start : master.slots[0].start,
	    		end : master.slots[master.slots.length-1].end
	    	};
    	}else{
    		$scope.modalModel = {}
    	}
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
    
    $scope.moveSlave = function(slave){
    	$scope.modalModel = {slave:slave};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/moveSlave.html',
	        scope : $scope
	    });
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		modalInstance.close();
	    		if($scope.modalModel.select != ""){
	    			$http.post('manager/cluster/' + $scope.id + '/slaveof/' + $scope.modalModel.select + '/' + slave.node).success(function(response){
	    				if(response.status){
	    					initData();
	    				}
	    	        });
	    		}
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.toMaster = function(slave){
    	$Popup.confirm('Warning','are you sure change this node to master?').then(function(flag){
    		if(flag){
    	    	$http.post('manager/cluster/' + $scope.id + '/tomaster/' + slave.node).success(function(response){
    				if(response.status){
    					initData();
    				}
    	        });
    		}
    	})
    }
    
    $scope.delNode = function(slave){
    	$Popup.confirm('Warning','are you sure delete this node?').then(function(flag){
    		if(flag){
    	    	$http.post('manager/cluster/' + $scope.id + '/forget/' + slave.node).success(function(response){
    				if(response.status){
    					initData();
    				}
    	        });
    		}
    	})
    }
    
    $scope.addNode = function(){
    	$scope.modalModel = {}
    	var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/addNode.html',
	        scope : $scope
	    });
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		modalInstance.close();
	    		$http.post('manager/cluster/addNode/' + $scope.id,{host:$scope.modalModel.host,port:$scope.modalModel.port}).success(function(response){
    				if(response.status){
    					initData();
    				}
    	        });
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    initData();
});