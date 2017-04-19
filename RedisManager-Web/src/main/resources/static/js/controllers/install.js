app.controller('InstallCtrl', function($scope, $state, $http, $modal, $interval) {

	$scope.import = function(){
    	$state.go('app.import');
    }
    
});