var app = angular.module('Application',
    [
        'ui.router',
        'ngAnimate',
        'toggle-switch',
        'jcs-autoValidate',
        'fm',
        'ui.select',
        'ngSanitize',
        'counter',
        'FBAngular',
        'ui.bootstrap',
        'angularCSS',
        'smart-table',
        'lrDragNDrop',
        'localytics.directives',
        'angularFileUpload',
        'ngLoadingSpinner',
        'ngStomp',
        'luegg.directives',
        'chart.js',
        'monospaced.elastic',
        'pageslide-directive',
        'ui.bootstrap.contextMenu',
        'kdate',
        'ui.sortable',
        'timer'
    ]);

app.factory('errorInterceptor', ['$q', '$rootScope', '$location', '$log',
    function ($q, $rootScope, $location, $log) {
        return {
            request: function (config) {
                return config || $q.when(config);
            },
            requestError: function (request) {
                return $q.reject(request);
            },
            response: function (response) {
                return response || $q.when(response);
            },
            responseError: function (response) {
                if (response && response.status === 404) {
                }
                if (response && response.status >= 500) {
                    $rootScope.showTechnicalNotify("الدعم الفني", response.data.message, "error", "fa-ban");
                }
                return $q.reject(response);
            }
        };
    }]);

app.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$cssProvider', 'ChartJsProvider', '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $locationProvider, $cssProvider, ChartJsProvider, $httpProvider) {

        ChartJsProvider.setOptions({colors: ['#803690', '#00ADF9', '#DCDCDC', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360']});

        $urlRouterProvider.otherwise("/menu");

        $locationProvider.html5Mode(true);

        $httpProvider.interceptors.push('errorInterceptor');

        /**************************************************************
         *                                                            *
         * Home State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("home", {
            url: "/home",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css',
                '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/home/home.html",
            controller: "homeCtrl"
        });

        /**************************************************************
         *                                                            *
         * Menu State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("menu", {
            url: "/menu",
            css: [
                '/ui/css/mdl-style-green-orange.css',
                '/ui/css/theme-black.css',
                '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/menu/menu.html",
            controller: "menuCtrl"
        });

        /**************************************************************
         *                                                            *
         * Admin State                                                *
         *                                                            *
         *************************************************************/
        $stateProvider.state("admin", {
            url: "/admin",
            css: [
                '/ui/css/mdl-style-green-orange.css',
                '/ui/css/theme-black.css',
                '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/admin/admin.html",
            controller: "adminCtrl"
        });

        /**************************************************************
         *                                                            *
         * Register State                                             *
         *                                                            *
         *************************************************************/
        $stateProvider.state("register", {
            url: "/register",
            css: [
                '/ui/css/mdl-style-green-orange.css',
                '/ui/css/theme-black.css',
                '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/register/register.html",
            controller: "registerCtrl"
        });

        /**************************************************************
         *                                                            *
         * Calculate State                                            *
         *                                                            *
         *************************************************************/
        $stateProvider.state("calculate", {
            url: "/calculate",
            css: [
                '/ui/css/mdl-style-green-orange.css',
                '/ui/css/theme-black.css',
                '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/calculate/calculate.html",
            controller: "calculateCtrl"
        });

        /**************************************************************
         *                                                            *
         * Report State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("report", {
            url: "/report",
            templateUrl: "/ui/partials/report/report.html",
            controller: "reportCtrl"
        });

        /**************************************************************
         *                                                            *
         * Help State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("help", {
            url: "/help",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css',
                 '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/help/help.html",
            controller: "helpCtrl"
        });

        /**************************************************************
         *                                                            *
         * Profile State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("profile", {
            url: "/profile",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css',
                 '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/profile/profile.html",
            controller: "profileCtrl"
        });

        /**************************************************************
         *                                                            *
         * About State                                                *
         *                                                            *
         *************************************************************/
        $stateProvider.state("about", {
            url: "/about",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css',
                 '/ui/css/style.css'
            ],
            templateUrl: "/ui/partials/about/about.html",
            controller: "aboutCtrl"
        });
    }
]);


