app.controller('ImportVersionCtrl', function($scope, $state, $http, $modal, $Popup, $timeout, $interval, FileUploader, toaster) {

	var initData = function(){
    	$http.get("version/list").success(function (response) {
			$scope.versions = response;
		});
    }
    
    var uploader = $scope.uploader = new FileUploader({
        url: 'upload/redis'
    });

    uploader.filters.push({
        name: 'customFilter',
        fn: function(item, options) {
            return this.queue.length < 2;
        }
    });

    uploader.onCompleteAll = function() {
        toaster.pop("success", "file upload complete");
        $timeout(function(){
			$scope.closeModal();
			uploader.clearQueue();
			initData();
		},2000);
    };
    
    uploader.onCompleteItem = function(fileItem, response, status, headers) {
        if(!response.status){
        	uploader.cancelAll();
        	toaster.pop("error", response.message);
        }else if(response.message){
        	toaster.pop("success", response.message);
        }
    };

    $scope.add = function(){
    	$scope.modalModel = {};
	    var modalInstance = $modal.open({
	        templateUrl: 'tpl/app/modal/uploadRedis.html',
	        scope : $scope
	    })
	    modalInstance.opened.then(function(){
	    	$scope.ok = function () {
	    		uploader.uploadAll();
		    };
		    $scope.closeModal = function(){
		    	modalInstance.close();
		    }
	    });
    }
    
    $scope.del = function(version){
    	$Popup.confirm('Warning','are you sure delete this resource?').then(function(flag){
    		if(flag){
    	    	$http.post('version/del', {name : version.name}).success(function(response){
    				if(response.status){
    					initData();
    				}
    	        });
    		}
    	})
    }
    
    initData();
});