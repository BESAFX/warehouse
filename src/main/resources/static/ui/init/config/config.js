var app = angular.module('Application',
    [
        'ui.router',
        'ngAnimate',
        'toggle-switch',
        'jcs-autoValidate',
        'fm',
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
                '/ui/css/theme-black.css'
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
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/menu/menu.html",
            controller: "menuCtrl"
        });

        /**************************************************************
         *                                                            *
         * Company State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("company", {
            url: "/company",
            css: [
                '/ui/css/mdl-style-red-deep_orange.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/company/company.html",
            controller: "companyCtrl"
        });

        /**************************************************************
         *                                                            *
         * Branch State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("branch", {
            url: "/branch",
            css : [
                '/ui/css/mdl-style-purple-pink.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/branch/branch.html",
            controller: "branchCtrl"
        });

        /**************************************************************
         *                                                            *
         * Master State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("master", {
            url: "/master",
            css: [
                '/ui/css/mdl-style-green-orange.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/master/master.html",
            controller: "masterCtrl"
        });

        /**************************************************************
         *                                                            *
         * Offer State                                                *
         *                                                            *
         *************************************************************/
        $stateProvider.state("offer", {
            url: "/offer",
            css: [
                '/ui/css/mdl-style-indigo-pink.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/offer/offer.html",
            controller: "offerCtrl"
        });

        /**************************************************************
         *                                                            *
         * Bank State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("bank", {
            url: "/bank",
            css: [
                '/ui/css/mdl-style-lime-orange.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/bank/bank.html",
            controller: "bankCtrl"
        });

        /**************************************************************
         *                                                            *
         * Deposit State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("deposit", {
            url: "/deposit",
            css: [
                '/ui/css/mdl-style-lime-orange.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/bank/deposit.html",
            controller: "depositCtrl"
        });

        /**************************************************************
         *                                                            *
         * Withdraw State                                             *
         *                                                            *
         *************************************************************/
        $stateProvider.state("withdraw", {
            url: "/withdraw",
            css: [
                '/ui/css/mdl-style-lime-orange.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/bank/withdraw.html",
            controller: "withdrawCtrl"
        });

        /**************************************************************
         *                                                            *
         * BillBuy State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("billBuy", {
            url: "/billBuy",
            css: [
                '/ui/css/mdl-style-light_green-lime.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/billBuy/billBuy.html",
            controller: "billBuyCtrl"
        });

        /**************************************************************
         *                                                            *
         * BillBuyType State                                          *
         *                                                            *
         *************************************************************/
        $stateProvider.state("billBuyType", {
            url: "/billBuyType",
            css: [
                '/ui/css/mdl-style-light_green-lime.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/billBuyType/billBuyType.html",
            controller: "billBuyTypeCtrl"
        });

        /**************************************************************
         *                                                            *
         * Course State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("course", {
            url: "/course",
            css: [
                '/ui/css/mdl-style-indigo-pink.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/course/course.html",
            controller: "courseCtrl"
        });

        /**************************************************************
         *                                                            *
         * Student State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("student", {
            url: "/student",
            templateUrl: "/ui/partials/student/student.html",
            controller: "studentCtrl"
        });

        /**************************************************************
         *                                                            *
         * Account State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("account", {
            url: "/account",
            css: [
                '/ui/css/mdl-style-indigo-pink.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/account/account.html",
            controller: "accountCtrl"
        });

        /**************************************************************
         *                                                            *
         * Payment State                                              *
         *                                                            *
         *************************************************************/
        $stateProvider.state("payment", {
            url: "/payment",
            css: [
                '/ui/css/mdl-style-indigo-pink.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/payment/payment.html",
            controller: "paymentCtrl"
        });

        /**************************************************************
         *                                                            *
         * PaymentOut State                                           *
         *                                                            *
         *************************************************************/
        $stateProvider.state("paymentOut", {
            url: "/paymentOut",
            css: [
                '/ui/css/mdl-style-light_green-lime.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/paymentOut/paymentOut.html",
            controller: "paymentOutCtrl"
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
         * Person State                                               *
         *                                                            *
         *************************************************************/
        $stateProvider.state("person", {
            url: "/person",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/person/person.html",
            controller: "personCtrl"
        });

        /**************************************************************
         *                                                            *
         * Team State                                                 *
         *                                                            *
         *************************************************************/
        $stateProvider.state("team", {
            url: "/team",
            css: [
                '/ui/css/mdl-style.css',
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/team/team.html",
            controller: "teamCtrl"
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
                '/ui/css/theme-black.css'
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
                '/ui/css/theme-black.css'
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
                '/ui/css/theme-black.css'
            ],
            templateUrl: "/ui/partials/about/about.html",
            controller: "aboutCtrl"
        });
    }
]);


