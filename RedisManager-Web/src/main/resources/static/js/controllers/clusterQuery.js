app.controller('ClusterQueryCtrl', function($scope, $state, $stateParams, $http, $interval) {
    $scope.id = $stateParams.id;
    $scope.name = $stateParams.name;

    $scope.query = {key : null, data : null}
    
    var queryParam = {
		query : "*",
		cursor : "0",
		client : 0
	}
    
    $scope.search = function(key, flush){
    	if(key == null || key == "" || key.indexOf("*") == -1){
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
    	$http.post('query/scan/' + $scope.id, queryParam).success(function(response){
    		queryParam.cursor = response.cursor;
    		queryParam.client = response.client;
    		$scope.query.data = response.keys;
    		$scope.query.hasMore = response.hasMore;
    	});
    }
    
    $scope.next = function(){
    	$scope.search(queryParam.query, false);
    }
});