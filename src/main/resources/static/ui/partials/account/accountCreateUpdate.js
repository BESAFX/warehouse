app.controller('accountCreateUpdateCtrl', ['AccountService', 'StudentService', 'BranchService', 'MasterService', 'CourseService', '$scope', '$rootScope', '$timeout', '$log', '$uibModalInstance', '$uibModal', 'title', 'action', 'account',
    function (AccountService, StudentService, BranchService, MasterService, CourseService, $scope, $rootScope, $timeout, $log, $uibModalInstance, $uibModal, title, action, account) {

        $scope.title = title;
        $scope.action = action;
        $scope.buffer = {};

        $scope.clear = function () {
            $scope.account = {};
            $scope.account.student = {};
            $scope.account.student.contact = {};
            $scope.account.coursePrice = 0;
            $scope.account.courseDiscountAmount = 0;
            $scope.account.courseProfitAmount = 0;
            $scope.account.courseCreditAmount = 0;
            if ($scope.form) {
                $scope.form.$setPristine();
            }
        };

        $scope.print = function (account) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: '/ui/partials/account/accountContract.html',
                controller: 'accountContractCtrl',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                    account: function () {
                        return account;
                    }
                }
            });

            modalInstance.result.then(function (buffer) {

            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        if (account) {
            AccountService.findOne(account.id).then(function (data) {
                $scope.account = data;
            });
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
                    AccountService.create($scope.account).then(function (data) {
                        $rootScope.showConfirmNotify("تسجيل الطلاب", "هل تود طباعة العقد ؟", "notification", "fa-info", function () {
                            $scope.print(data);
                        });
                        $scope.account = {};
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

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);