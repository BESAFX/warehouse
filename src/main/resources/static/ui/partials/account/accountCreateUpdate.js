app.controller('accountCreateUpdateCtrl', ['AccountService', 'StudentService', 'BranchService', 'MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', 'title', 'action', 'account',
    function (AccountService, StudentService, BranchService, MasterService, CourseService, $scope, $rootScope, $timeout, $log, $uibModalInstance, title, action, account) {

        $scope.title = title;
        $scope.action = action;
        $scope.buffer = {};

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

        $scope.print = function (account) {
            window.open('/report/account/contract/' + account.id);
        };

        if (account) {
            $scope.account = account;
            $scope.showBox = $scope.account.coursePaymentType == 'نقدي' ? true : false;
        } else {
            $scope.clear();
            $timeout(function () {
                BranchService.fetchBranchMasterCourse().then(function (data) {
                    $scope.branches = data;
                });
            }, 2000);
        }
        $scope.submit = function () {
            switch ($scope.action) {
                case 'create' :
                    $scope.account.coursePaymentType = ($scope.showBox ? 'نقدي' : 'قسط شهري');
                    $scope.account.course = $scope.buffer.course;
                    $scope.account.course.master = $scope.buffer.master;
                    $scope.account.course.master.branch = $scope.buffer.branch;
                    AccountService.create($scope.account).then(function (data) {
                        $rootScope.showConfirmNotify("تسجيل الطلاب", "هل تود طباعة العقد ؟", "notification", "fa-info", function () {
                            $scope.print(data);
                        });
                        $scope.account = {};
                        $scope.form.$setPristine();
                    });
                    break;
                case 'update' :
                    $scope.account.coursePaymentType = ($scope.showBox ? 'نقدي' : 'قسط شهري');
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