app.controller("homeCtrl", ['$scope', '$rootScope', '$log', 'PersonService', 'MasterService', 'OfferService', 'AccountService', 'BranchService', 'CourseService', 'FileService', '$timeout',
    function ($scope, $rootScope, $log, PersonService, MasterService, OfferService, AccountService, BranchService, CourseService, FileService, $timeout) {

        $scope.buffer = {};

        $scope.charts =
            [
                {"id": 1, "name": 'مخطط شريطي رأسي', "class": 'fa fa-bar-chart fa-2x', "type": 'CHART_BAR'},
                {"id": 2, "name": 'مخطط خطي', "class": 'fa fa-line-chart fa-2x', "type": 'CHAR_LINE'},
                {
                    "id": 3,
                    "name": 'مخطط شريطي افقي',
                    "class": 'fa fa-bar-chart fa-rotate-270 fa-2x',
                    "type": 'CHART_HORIZONTAL_BAR'
                },
                {"id": 4, "name": 'مخطط دائري', "class": 'fa fa-pie-chart fa-2x', "type": 'CHART_PIE'}
            ];

        $scope.masterCounter = 0;
        $scope.masterCounterTarget = 0;

        $scope.offerCounter = 0;
        $scope.offerCounterTarget = 0;

        $scope.accountCounter = 0;
        $scope.accountCounterTarget = 0;

        $scope.courseCounter = 0;
        $scope.courseCounterTarget = 0;

        $timeout(function () {
            MasterService.fetchCount().then(function (data) {
                $scope.masterCounterTarget = data;
                OfferService.fetchCount().then(function (data) {
                    $scope.offerCounterTarget = data;
                    AccountService.fetchCount().then(function (data) {
                        $scope.accountCounterTarget = data;
                        CourseService.fetchCount().then(function (data) {
                            $scope.courseCounterTarget = data;
                        });
                    });
                });
            });
        }, 2000);

        $scope.fetchBranchesList = function () {
            BranchService.fetchTableData().then(function (data) {
                $scope.branches = data;
                $scope.buffer.branch = $scope.branches[0];
                $scope.fetchAccountsCountByBranch();
            });
        };

        $scope.fetchMastersList = function () {
            MasterService.fetchTableData().then(function (data) {
                $scope.masters = data;
                $scope.buffer.master = $scope.masters[0];
                $scope.fetchAccountsCountByMaster();
            });
        };

        $scope.fetchCoursesList = function () {
            CourseService.fetchTableData().then(function (data) {
                $scope.courses = data;
                $scope.buffer.course = $scope.courses[0];
                $scope.fetchAccountsCountByCourse();
            });
        };

        $scope.fetchOffersList = function () {
            OfferService.fetchTableData().then(function (data) {
                $scope.offers = data;
            });
        };

        $scope.fetchAccountsList = function () {
            AccountService.fetchTableData().then(function (data) {
                $scope.accounts = data;
            });
        };

        $scope.fetchAccountsCountByBranch = function () {
            AccountService.fetchAccountsCountByBranch($scope.buffer.branch.id).then(function (data) {

                $scope.labels1 = [];

                $scope.data1 = [[]];

                $scope.series1 = ['عدد التسجيلات'];

                angular.forEach(data, function (wrapperUtil) {
                    $scope.labels1.push(wrapperUtil.obj1);
                    $scope.data1[0].push(wrapperUtil.obj2);
                });
            })
        };

        $scope.fetchAccountsCountByMaster = function () {
            AccountService.fetchAccountsCountByMaster($scope.buffer.master.id).then(function (data) {

                $scope.labels2 = [];

                $scope.data2 = [[]];

                $scope.series2 = ['عدد التسجيلات'];

                angular.forEach(data, function (wrapperUtil) {
                    $scope.labels2.push(wrapperUtil.obj1);
                    $scope.data2[0].push(wrapperUtil.obj2);
                });
            })
        };

        $scope.fetchAccountsCountByCourse = function () {
            AccountService.fetchAccountsCountByCourse($scope.buffer.course.id).then(function (data) {

                $scope.labels3 = [];

                $scope.data3 = [[]];

                $scope.series3 = ['عدد التسجيلات'];

                angular.forEach(data, function (wrapperUtil) {
                    $scope.labels3.push(wrapperUtil.obj1);
                    $scope.data3[0].push(wrapperUtil.obj2);
                });
            })
        };
    }]);
