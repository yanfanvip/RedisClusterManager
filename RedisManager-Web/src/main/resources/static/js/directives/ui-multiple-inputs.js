angular.module('app').directive('uiMultipleInputs', function($timeout, $parse) {
    return {
    	restrict: 'EA',
    	scope : {
    		placeholder : '@',
    		ngModel : '='
        },
    	template:'<div style="display: flex;">'+
			    	'<div ng-if="ngModel.length > 0" style="padding-top: 7px;">'+
			    		'<ul style="display:inline;padding: 15px;">'+
			    			'<li ng-repeat="item in ngModel" style="padding:3px 5px 3px 5px; margin: 0 5px 0 5px;border-radius:5px;display:inline;background-color:#1b75bb;color:#ffffff;">'+
			    				'<span>{{item}}</span><a style="padding-left:10px;color:#ffffff;" ng-click="remove(item,$index)">x</a>'+
			    			'</li>'+
			    		'</ul>'+
			    	'</div>'+
			    	'<div style="width:100%;">'+
			    		'<input class="form-control" style="width:100%;padding-top: 7px;" type="text" ng-model="model" ng-keypress="($event.which === 13)?add(model):0" placeholder="{{placeholder}}">'+
			    	'</div>'+
			    '</div>',
		link: function(scope, element, attr) {
			scope.add = function(model){
				if(!scope.ngModel){
					scope.ngModel = [];
				}
				if(scope.ngModel.indexOf(model) == -1){
					scope.ngModel.push(model);
				}
				scope.model = "";
			}
			scope.remove = function(item, index){
				if (isNaN(index) || index>= this.length) { return false; }  
				scope.ngModel.splice(index, 1);
			}
		}
    };
  });