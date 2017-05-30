app.controller('paymentCreateCtrl', ['AccountService', 'StudentService', 'BranchService', 'MasterService', 'CourseService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'title', 'payment',
    function (AccountService, StudentService, BranchService, MasterService, CourseService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, title, payment) {

        $scope.payment = payment;

        $scope.title = title;

        $scope.buffer = {};

        $timeout(function () {
            AccountService.findRequiredPrice($scope.payment.account.id).then(function (data) {
                $scope.buffer.requiredPrice = data;
                AccountService.findRemainPrice($scope.payment.account.id).then(function (data) {
                    $scope.buffer.remainPrice = data;
                    AccountService.findPaidPrice($scope.payment.account.id).then(function (data) {
                        $scope.buffer.paidPrice = data;
                    });
                });
            });
        }, 1000);

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $scope.payment = {};
                $scope.buffer = {};
                $scope.form.$setPristine();
                $uibModalInstance.dismiss('cancel');
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);