app.controller('ClusterCtrl', function($scope, $state, $stateParams, $q, $http, $modal, $Popup, $interval) {
    $scope.id = $stateParams.id;
    $scope.name = $stateParams.name;

    $scope.clusterOptions = {
        tooltip: {
            show: true,
            formatter: function (data) {
            	var name = data.name;
            	if(data.data.value){
            		name = data.data.value.host + ":" + data.data.value.port;
            	}
                return '<div style="text-center"><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:'+ data.color +'"></span>'+ name +'</div>';
            }
        }
    };

    var series = {
        type: 'graph',
        layout: 'force',
        focusNodeAdjacency : true,
        roam : 'scale',
        label: { emphasis: { show: true, position:'bottom'}},
        force: { repulsion: 300 }
    }
    var status_ok = "#090";
    var status_fail = "#f10";

    var defaultConfig = {
        xAxis : {
            axisLabel :{
                formatter: function (value) {
                    return new Date(value).format('hh:mm');
                }
            }
        },
        toolbox : {
            show : true,
            feature : {
                dataZoom : { title:{zoom:'zoom',back:'back'},show : true },
                restore : {title:'restore'},
                magicType : { title:{line:'line',bar:'bar',stack:'stack',tiled:'tiled'},type: ['line', 'bar', 'stack', 'tiled'] },
                saveAsImage : {title:'saveAsImage'}
            }
        },
        tooltip : {
            trigger: 'item',
            formatter: function (data) {
                return '<div style="text-center"><span>' + data.seriesName + '</span><br><span style="display:inline-block;margin-right:5px;border-radius:10px;'
                        +'width:9px;height:9px;background-color:'+data.color +'"></span>'+ new Date(data.name).format('yyyy-MM-dd hh:mm:ss')+' : '+data.value+'</div>';
            }
        }
    }

    $scope.computerChats = [{ name : "Cpu",  field : "combinedCpu" }, { name : "Memory", field : "combinedMem" },
                            {name : "Desk", field : "combinedDisk"},{name : "Swap", field : "freeSwap"}];
    var selectedComputers = $scope.computerChats[0];
    var computerAllData = null;
    $scope.changeComputer = function(selected){
        selectedComputers = selected;
        $scope.computerConfig = Object.assign({title: selected.name}, defaultConfig);
        if(computerAllData == null){
            return;
        }
        var datas = {};
        computerAllData.forEach(function(d){
            if(datas[d.ip] == null){
                datas[d.ip] = [];
            }
            datas[d.ip].push({ x : d.date, y : d[selected.field]})
        });
        $scope.computerDatas = formatData(datas);
    }

    $scope.redisChats = [{ name : "instantaneous_ops_per_sec", field : "instantaneous_ops_per_sec" }, { name : "commands_processed_ops_by_sec",  field : "commands_processed_ops_by_sec" },
                            {name : "connections_received_ops_by_sec", field : "connections_received_ops_by_sec"},{name : "net_input_bytes_ops_by_sec", field : "net_input_bytes_ops_by_sec"},
                            {name : "net_output_bytes_ops_by_sec", field : "net_output_bytes_ops_by_sec"},{name : "used_memory", field : "used_memory"}];
    var selectedRedis = $scope.redisChats[0];
    var redisAllData = null;
    
    $scope.changeRedis = function(selected){
        selectedRedis = selected;
        $scope.redisConfig = Object.assign({title: selected.name}, defaultConfig, {legend:{show : false}});
        if(redisAllData == null){
            return;
        }
        var datas = {};
        redisAllData.forEach(function(d){
            var key = d.hostname + ":" + d.port;
            if(datas[key] == null){
                datas[key] = [];
            }
            datas[key].push({ x : d.date, y : d[selected.field]})
        });
        $scope.redisDatas = formatData(datas);
    }
    
    var initData = function(){
        $http.get("info/cluster/redisInfo/" + $scope.id).success(function (response) {
            redisAllData = response;
            $scope.changeRedis(selectedRedis);
        });
        
        $http.get("info/cluster/serverInfo/" + $scope.id).success(function (response) {
            computerAllData = response;
            $scope.changeComputer(selectedComputers);
        });
        
    	$q.all([
    		$http.get("info/cluster/info/" + $scope.id), 
    		$http.get("info/cluster/tree/" + $scope.id)]
    	).then(function(datas){
    		 var clusterInfo = datas[0].data;
    		 var response = datas[1].data;
    		 series.data= [];
         	 series.links = [];
             series.data.push({name: 'Cluster', symbolSize:60, itemStyle: { normal:{color: clusterInfo.cluster_state=='ok'?status_ok:status_fail}}} );
             response.masters.forEach(function(data){
                 series.data.push({name: data.master.node, value: data.master, symbolSize:40, itemStyle: { normal:{color: data.master.status==='CONNECT'?status_ok:status_fail}}});
                 series.links.push({source: 'Cluster', target: data.master.node});
                 data.slaves.forEach(function(s){
                     series.data.push({name: s.node, value : s , symbolSize:30, itemStyle: { normal:{color:  s.status==='CONNECT'?status_ok:status_fail}}});
                     series.links.push({source: data.master.node, target: s.node});
                 });
             });
             $scope.clusterDatas = series;
    	});
    }
    
    function formatData(data){
        var rets = [];
        for(var name in data){
            if(Object.prototype.hasOwnProperty.call(data,name)) {
                rets.push({ "name" : name, "datapoints" : data[name] });
            }
        }
        return rets;
    }
    
    $scope.delete_cluster = function(){
    	$Popup.confirm('Warning','are you sure delete this cluster?').then(function(flag){
    		if(flag){
    	    	$http.post('manager/cluster/delete/' + $scope.id).success(function(response){
    				if(response.status){
    					$state.go('app.dashboard');
    				}
    	        });
    		}
    	})
    }
    $scope.options_cluster = function(){
    	$state.go('app.clusterOptions', {id : $scope.id, name : $scope.name});
    }
    $scope.query_cluster = function(){
    	$state.go('app.clusterQuery', {id : $scope.id, name : $scope.name});
    }
    
    initData();
    
    var timer = $interval(function(){
    	initData();
    },30000)

    $scope.$on("$destroy", function() {
    	$interval.cancel(timer);
    });
});