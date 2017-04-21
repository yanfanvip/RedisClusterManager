app.controller('InstallCtrl', function($scope, $state, $http, $modal, $interval) {

	$scope.importVersion = function(){
    	$state.go('app.importVersion');
    }
    
    $scope.importServer = function(){
    	$state.go('app.importServer');
    }
});