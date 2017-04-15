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
      }
    ]
  );