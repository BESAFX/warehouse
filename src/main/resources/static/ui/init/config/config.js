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
        'ui.sortable'
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
            templateUrl: "/ui/partials/profile/profile.html",
            controller: "profileCtrl"
        });
    }
]);


