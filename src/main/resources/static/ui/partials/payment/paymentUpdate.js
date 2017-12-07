app.controller('paymentUpdateCtrl', ['PaymentService', '$rootScope', '$scope', '$timeout', '$log', '$uibModalInstance', 'payment',
    function (PaymentService, $rootScope, $scope, $timeout, $log, $uibModalInstance, payment) {

        $scope.payment = payment;
        $scope.buffer = {};
        $scope.buffer.hijriDate = true;

        $scope.submit = function () {
            PaymentService.update($scope.payment).then(function (data) {
               $scope.payment = data;
            });
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };

        $timeout(function () {
            window.componentHandler.upgradeAllRegistered();
        }, 1500);

    }]);