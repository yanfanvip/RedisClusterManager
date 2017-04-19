'use strict';

/**
 * Config for the router
 */
angular.module('app').config(['$stateProvider', '$urlRouterProvider', function ($stateProvider,   $urlRouterProvider) {
          $urlRouterProvider.otherwise('/app/dashboard');
          $stateProvider
              .state('app', {
                  abstract: true,
                  url: '/app',
                  templateUrl: 'tpl/app.html'
              })
              
              .state('app.dashboard', {
                  url: '/dashboard',
                  templateUrl: 'tpl/app/dashboard.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['js/controllers/dashboard.js']);
                      }]
                  }
              })
             .state('app.install', {
                  url: '/install',
                  templateUrl: 'tpl/app/install.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['js/controllers/install.js']);
                      }]
                  }
              })
              .state('app.import', {
                  url: '/import',
                  templateUrl: 'tpl/app/import.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function( $ocLazyLoad ){
                        return $ocLazyLoad.load(['js/controllers/import.js']);
                      }]
                  }
              })
              
              .state('app.cluster', {
                  url: '/cluster/:id?name',
                  templateUrl: 'tpl/app/cluster.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function($ocLazyLoad){
                        return $ocLazyLoad.load(['js/controllers/cluster.js']);
                      }]
                  }
              })
              .state('app.clusterOptions', {
                  url: '/clusterOptions/:id?name',
                  templateUrl: 'tpl/app/clusterOptions.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function($ocLazyLoad){
                        return $ocLazyLoad.load(['js/controllers/clusterOptions.js']);
                      }]
                  }
              })
              .state('app.clusterQuery', {
                  url: '/clusterQuery/:id?name',
                  templateUrl: 'tpl/app/clusterQuery.html',
                  resolve: {
                    deps: ['$ocLazyLoad',
                      function($ocLazyLoad){
                        return $ocLazyLoad.load(['js/controllers/clusterQuery.js']);
                      }]
                  }
              })
              
      }
    ]
  );