angular.module('app').directive('uiMultipleInputs', function($timeout, $parse) {
    return {
    	restrict: 'EA',
    	scope : {
    		placeholder : '@',
    		onAdd : '=',
    		ngModel : '=',
    		type : '@'
        },
        replace: true,
    	template:'<div class="chosen-container chosen-container-multi chosen-with-drop chosen-container-active">'+
				 '	<ul class="chosen-choices">'+
				 '		<li ng-repeat="item in ngModel" class="choice" ng-click="remove(item,$index)">'+
				 '			<span>{{item}}</span><a class="fa fa-trash-o"></a>'+
				 '		</li>'+
				 '		<li class="choice-input"><input type="{{type}}" ng-model="model" ng-keypress="($event.which === 13)?add(model):0" placeholder="{{placeholder}}" autocomplete="off"></li>'+
				 '	</ul>'+
				 '</div>',
		link: function(scope, element, attr) {
			scope.add = function(model){
				if(!scope.ngModel){
					scope.ngModel = [];
				}
				if(!scope.onAdd || scope.onAdd(model)){
					if(scope.ngModel.indexOf(model) == -1){
						scope.ngModel.push(model);
					}
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