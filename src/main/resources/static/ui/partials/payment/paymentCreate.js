app.controller('paymentCreateCtrl', ['AccountService', 'StudentService', 'BranchService', 'MasterService', 'CourseService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title', 'payment',
    function (AccountService, StudentService, BranchService, MasterService, CourseService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title, payment) {

        $scope.payment = payment;

        $scope.title = title;

        $scope.buffer = {};

        $scope.findPaymentsByAccount = function () {
            PaymentService.findByAccount($scope.payment.account.id).then(function (data) {
                $scope.payments = data;
                AccountService.findRequiredPrice($scope.payment.account.id).then(function (data) {
                    $scope.buffer.requiredPrice = data;
                    AccountService.findRemainPrice($scope.payment.account.id).then(function (data) {
                        $scope.buffer.remainPrice = data;
                        AccountService.findPaidPrice($scope.payment.account.id).then(function (data) {
                            $scope.buffer.paidPrice = data;
                        });
                    });
                });
            });
        };

        $scope.$watch('payment.account', function (newValue, oldValue) {
            if (newValue) {
                $scope.findPaymentsByAccount();
            }
        }, true);

        $timeout(function () {
            $scope.accounts = [];
            $scope.payments = [];
            AccountService.fetchTableData().then(function (data) {
                $scope.accounts = data;
            });
        }, 2000);

        $scope.findPaymentByCode = function () {
            if (!$scope.payment.code) {
                return;
            }
            PaymentService.findByCodeAndBranch($scope.payment.code, $scope.payment.account.course.master.branch).then(function (data) {
                if (data) {
                    noty({
                        text: 'هذا الرقم غير متاح حالياَ، فضلاً ادخل رقم آخر للسند',
                        layout: 'topCenter',
                        type: 'danger',
                        timeout: 5000
                    });
                    $scope.payment.code = "";
                    $scope.form.$setPristine();
                    $('#code').focus();
                }
            });
        };

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $scope.payment = {};
                $scope.buffer = {};
                $scope.form.$setPristine();
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);