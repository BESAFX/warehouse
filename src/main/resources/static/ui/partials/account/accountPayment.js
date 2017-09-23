app.controller('accountPaymentCtrl', ['AccountService', 'PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'account',
    function (AccountService, PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, account) {

        $scope.payment = {};
        $scope.payment.account = account;

        $scope.submit = function () {
            PaymentService.create($scope.payment).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }]);