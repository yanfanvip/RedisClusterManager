app.controller('ClusterOptionsCtrl', function($scope, $state, $stateParams, $http, $interval) {
    $scope.id = $stateParams.id;
    $scope.name = $stateParams.name;

    var initData = function(){
    	$http.get("info/cluster/info/" + $scope.id).success(function (response) {
            $scope.clusterInfo = response;
        });
    	
        $http.get("info/cluster/tree/" + $scope.id).success(function (response) {
        	console.log(response);
        	$scope.tree = response;
        });
    }

    initData();
});