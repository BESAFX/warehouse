app.controller('accountCreateUpdateCtrl', ['AccountService', 'StudentService', 'BranchService', 'MasterService', 'CourseService', '$scope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'account',
    function (AccountService, StudentService, BranchService, MasterService, CourseService, $scope, $timeout, $log, $uibModalInstance, title, action, account) {

        $scope.title = title;
        $scope.action = action;
        $scope.accounts = [];
        $scope.branches = [];
        $scope.masters = [];
        $scope.students = [];

        $scope.clear = function () {
            $scope.account = {};
            $scope.account.registerDate = new Date();
            $scope.account.student = {};
            $scope.account.student.contact = {};
            $scope.account.coursePrice = 0;
            $scope.account.courseDiscountAmount = 0;
            $scope.account.courseProfitAmount = 0;
            $scope.account.courseCreditAmount = 0;
            $scope.showBox = true;
            if ($scope.form) {
                $scope.form.$setPristine();
            }
        };

        if (account) {
            $scope.account = account;
            $scope.showBox = $scope.account.coursePaymentType == 'نقدي' ? true : false;
        } else {
            $scope.clear();
            $timeout(function () {
                BranchService.fetchTableData().then(function (data) {
                    $scope.branches = data;
                });
                $scope.$watch('account.course.master.branch', function (newValue, oldValue) {
                    if (newValue) {
                        MasterService.findByBranch(newValue).then(function (data) {
                            $scope.masters = data;
                        });
                    }
                }, true);
                $scope.$watch('account.course.master', function (newValue, oldValue) {
                    if (newValue) {
                        if (newValue.id) {
                            CourseService.findByMaster(newValue.id).then(function (data) {
                                $scope.courses = data;
                            });
                        }
                    }
                }, true);
            }, 2000);
        }
        $scope.submit = function () {
            $scope.account.coursePaymentType = ($scope.showBox ? 'نقدي' : 'قسط شهري');
            switch ($scope.action) {
                case 'create' :
                    AccountService.create($scope.account).then(function (data) {
                        $scope.account = data;
                        $scope.clear();
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    AccountService.update($scope.account).then(function (data) {
                        $scope.account = data;
                    });
                    break;
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }]);