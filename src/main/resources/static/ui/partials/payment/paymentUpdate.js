app.controller('paymentUpdateCtrl', ['PaymentService', 'AccountService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'payment',
    function (PaymentService, AccountService, $rootScope, $scope, $timeout, $log, $uibModalInstance, payment) {

        $scope.payment = payment;
        $scope.buffer = {};
        $scope.buffer.hijriDate = true;

        $timeout(function () {
            AccountService.findOne(payment.account.id).then(function (data) {
                return $scope.payment.account = data;
            });
        }, 600);

        $scope.submit = function () {
            PaymentService.update($scope.payment).then(function (data) {
                $uibModalInstance.close(data);
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 600);

    }]);