app.controller('ClusterQueryCtrl', function($scope, $state, $stateParams, $http, $interval, $timeout, $modal, $Popup) {
    $scope.id = $stateParams.id;
    $scope.name = $stateParams.name;

    $scope.query = {key : null, data : null}
    var lastPage = {};
    var queryParam = {
		query : "*",
		cursor : "0",
		client : 0
	}
    
    $scope.search = function(key, flush){
    	if(key == null || key == ""){
    		$scope.query.key = null;
    		$scope.query.data = null;
    		$scope.query.hasMore = null
    		return;
    	}
    	$scope.query.key = key;
    	if(queryParam.query != key || flush){
    		queryParam = {
				query : key,
				cursor : "0",
				client : 0
			}
    	}
    	var loading = $modal.open({
	        templateUrl: 'tpl/app/modal/loading.html'
	    });
		$timeout(function(){
			loading.close();
        },30000);
    	$http.post('query/scan/' + $scope.id, queryParam).success(function(response){
    		loading.close();
    		lastPage = queryParam;
    		queryParam.cursor = response.cursor;
    		queryParam.client = response.client;
    		$scope.query.data = response.keys;
    		$scope.query.hasMore = response.hasMore;
    	});
    }
    
    $scope.next = function(){
    	$scope.search(queryParam.query, false);
    }
    
    $scope.get = function(key){
    	$http.get('query/get/' + $scope.id +"/" + key).success(function(response){
    		$scope.JSON = JSON.stringify(response.data);
    	    var modalInstance = $modal.open({
    	        templateUrl: 'tpl/app/modal/json.html',
    	        scope : $scope
    	    })
    	    modalInstance.opened.then(function(){
    		    $scope.closeModal = function(){ modalInstance.close(); }
    	    });
    	});
    }
    $scope.del = function(key){
    	$Popup.confirm('Warning','are you sure delete [' + key + ']?').then(function(flag){
    		if(flag){
    	    	$http.post('query/delete/' + $scope.id +"/" + key,{}).success(function(response){
    	    		$http.post('query/scan/' + $scope.id, lastPage).success(function(response){
    	        		queryParam.cursor = response.cursor;
    	        		queryParam.client = response.client;
    	        		$scope.query.data = response.keys;
    	        		$scope.query.hasMore = response.hasMore;
    	        	});
    	    	});
    		}
    	});
    }
});